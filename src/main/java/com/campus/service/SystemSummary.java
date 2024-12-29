package com.campus.service;

/** Aggregate counts shown on the admin dashboard/reports page. */
public record SystemSummary(int totalStudents, int totalFaculty, int totalCourses, int totalActiveEnrollments) {
}
