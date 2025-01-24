# Architecture

Campus Management System is a classic layered Java EE web application: no
framework sits between the servlet container and the application code beyond
the Servlet/JSP/JSTL/JDBC APIs themselves.

## Application layers

```text
Browser
   |
Servlet Filter      (CsrfFilter, AuthorizationFilter)
   |
Servlet / Controller (com.campus.controller)
   |
Service              (com.campus.service)
   |
DAO                  (com.campus.dao)
   |
JDBC
   |
MySQL
```

- **Filters** (`com.campus.filter`) run before any servlet and handle
  cross-cutting concerns that would otherwise be duplicated in every
  controller: CSRF validation and role-based authorization.
- **Controllers** (`com.campus.controller`) are thin. They parse and do basic
  validation of request parameters, call exactly one service method, set
  request/session attributes, and forward to a JSP or redirect. They contain
  no SQL and no business rules.
- **Services** (`com.campus.service`) hold the business rules: enrollment
  capacity and duplicate checks, grade value validation, attendance date
  validation, course-ownership checks, account-creation validation. Services
  depend on DAO interfaces, never on `JdbcXxxDao` implementations directly,
  and throw one of three unchecked exceptions - `NotFoundException`,
  `ValidationException`, `UnauthorizedActionException` - that controllers
  catch and turn into a flash message instead of a 500 page.
- **DAOs** (`com.campus.dao`) are the only layer that touches JDBC. Every
  query uses `PreparedStatement` with bound parameters; nothing is ever built
  by concatenating a caller-supplied value into SQL. Each DAO has an
  interface (e.g. `EnrollmentDao`) and a `Jdbc*` implementation, so services
  can be unit-tested against a mock of the interface without a database.
- **Models** (`com.campus.model`) are plain data-holder classes that mirror
  the schema, with a handful of read-only fields (e.g. `Course.facultyName`,
  `Enrollment.courseCode`) populated by DAO joins purely for display.

## Request lifecycle

A typical state-changing request - for example, a student enrolling in a
course - flows like this:

1. Browser submits `POST /student/courses` with `courseId` and `csrfToken`.
2. `CsrfFilter` checks `csrfToken` against the token stored in the caller's
   session; a mismatch (or no session) short-circuits with `403` before any
   application code runs.
3. `AuthorizationFilter` (only mapped to `/student/*`, `/faculty/*`,
   `/admin/*`) checks that a user is logged in and that their role matches
   the path prefix; otherwise it redirects to `/login` or returns `403`.
4. `StudentCourseServlet.doPost` reads `courseId`, calls
   `EnrollmentService.enroll(studentId, courseId)`.
5. `EnrollmentService` loads the course and any existing enrollment through
   `CourseDao`/`EnrollmentDao`, applies the capacity/duplicate rules, and
   calls `EnrollmentDao.insert` (or `updateStatus` to reactivate a dropped
   enrollment).
6. `JdbcEnrollmentDao` runs a parameterized `INSERT` over a `Connection`
   obtained from the JNDI `DataSource`.
7. The servlet sets a one-shot flash message on the session and issues a
   redirect back to `/student/courses` (POST-redirect-GET), which the
   `CsrfFilter` does not intercept since the follow-up request is a GET.

## Authentication

`AuthService.authenticate(username, password)` (`com.campus.service`) looks
the user up by username, verifies the password with `PasswordUtil` (a BCrypt
wrapper over jBCrypt, cost factor 12), and rejects a deactivated account. An
unknown username and a wrong password deliberately produce the same "Invalid
username or password" message, so a failed login doesn't reveal which
usernames exist; a deactivated account gets its own message since that's a
statement about account state, not a credential guess.

`LoginServlet` calls `AuthService`, and on success:

- Calls `HttpServletRequest.changeSessionId()` to issue a new session ID on
  privilege change (session-fixation protection) while carrying over the
  session's CSRF token.
- Stores the authenticated `User` on the session (`Attributes.SESSION_USER`).
- Sets the session's inactivity timeout to 30 minutes
  (`session.setMaxInactiveInterval(30 * 60)`; also declared declaratively as
  `<session-timeout>30</session-timeout>` in `web.xml`).
- Redirects to `/student/dashboard`, `/faculty/dashboard`, or
  `/admin/dashboard` based on `User.getRole()`.

## Authorization

Role-based access to `/student/*`, `/faculty/*`, and `/admin/*` is enforced
in exactly one place: `AuthorizationFilter`. It reads the `User` off the
session, checks the URL prefix against `User.getRole()`, and either lets the
request through, redirects an anonymous request to `/login`, or returns
`403` for a wrong-role request. No servlet under those paths repeats this
check.

A second, narrower kind of authorization - data ownership - lives in the
service layer instead, because it can only be evaluated once the specific
record is loaded: `EnrollmentService.drop` confirms the enrollment belongs
to the requesting student; `GradeService.recordGrade` and
`AttendanceService.recordAttendance` confirm the course is assigned to the
requesting faculty member. Both throw `UnauthorizedActionException`.

## Session management

Sessions are the standard container-managed `HttpSession`, configured for a
30-minute timeout and an `HttpOnly` cookie (`web.xml` `<cookie-config>`), so
client-side script can't read the session cookie. `Attributes` centralizes
the three attribute keys the app uses (`SESSION_USER`, `CSRF_TOKEN`, plus the
`ServiceFactory`'s own application-scoped attribute), so a typo in a literal
string can't silently create a second, disconnected copy of session state.

## CSRF protection

`CsrfTokenUtil` issues one random 256-bit token per session
(`SecureRandom` + URL-safe Base64) the first time it's needed, and every JSP
form embeds it as a hidden `csrfToken` field. `CsrfFilter`, mapped to `/*`,
rejects any `POST`/`PUT`/`DELETE` whose `csrfToken` parameter doesn't match
the session's token, before the request reaches a servlet. This covers every
state-changing form in the app, including the login form itself, since login
CSRF (tricking a browser into authenticating as an attacker-controlled
account) is a real, if less obvious, risk.

## DAO architecture and database access

Every DAO has an interface and one `Jdbc*` implementation, constructed with
a `javax.sql.DataSource`. Queries use `PreparedStatement` exclusively;
result sets are mapped to model objects by a private `mapRow` method per
DAO. Multi-row list queries return `List<T>`; single-row lookups return
`Optional<T>`. `DataAccessException` (unchecked) wraps `SQLException` so DAO
interfaces stay free of checked exceptions.

Creating an account writes to two tables - `users` and either `students` or
`faculty` - and those two statements need to commit or roll back together.
`UserDao`, `StudentDao`, and `FacultyDao` each expose a `Connection`-accepting
overload of `insert` alongside the normal `DataSource`-based one;
`UserManagementService.createStudent`/`createFaculty` open one connection,
disable auto-commit, run both inserts against it, and commit (or roll back on
any exception). Every other write in the app is a single statement, so it
uses the simpler `DataSource`-based DAO methods directly.

The `DataSource` itself is a **Tomcat-managed JNDI resource**
(`jdbc/campusdb`), declared in `META-INF/context.xml` (packaged in the WAR)
and referenced from `web.xml` via `<resource-ref>`. `AppContextListener`
resolves it once at startup via `InitialContext` and publishes it on the
`ServletContext`; `ServiceFactory.get(ServletContext)` then builds every
DAO/service exactly once per application lifetime and hands them to
controllers, so nothing outside `AppContextListener` and `ServiceFactory`
does a JNDI lookup. JNDI was chosen over a bundled connection pool
(HikariCP) because it's the idiomatic fit for a "classic Tomcat" deployment:
connection pooling, credentials, and pool tuning live in the container's
configuration rather than the application's classpath, and an ops team can
repoint the app at a different database without rebuilding the WAR.

## Error handling

Services signal expected failures with three unchecked exceptions
(`NotFoundException`, `ValidationException`, `UnauthorizedActionException`);
controllers catch these narrowly and re-render the relevant page with a
flash message. Anything unexpected (a `DataAccessException`, a `NullPointerException`
from a bug) is left to propagate to the container, which routes it to
`/WEB-INF/jsp/common/error500.jsp` via the `<error-page>` entries in
`web.xml`; a `404` from an unmapped URL goes to `error404.jsp`. Neither error
page leaks a stack trace to the browser.

## Deployment architecture

```text
                 ┌─────────────────────────┐
  Browser  ───▶  │   Apache Tomcat 10.1     │
                 │  campus-management-      │
                 │  system.war              │
                 │                          │
                 │  jdbc/campusdb (JNDI) ───┼───▶  MySQL 8.0
                 └─────────────────────────┘
```

The WAR is self-contained (all library dependencies except the servlet API
itself are packaged under `WEB-INF/lib`) and deployable to any Tomcat 10.1.x
instance by dropping it into `webapps/`. The only external dependency is a
reachable MySQL server matching the `jdbc/campusdb` `Resource` in
`META-INF/context.xml` (or an override supplied by the Tomcat instance - see
the README's "Tomcat deployment" section). No application server clustering,
load balancer, or reverse proxy is assumed; this is a single-instance,
single-database deployment appropriate to the project's scope.
