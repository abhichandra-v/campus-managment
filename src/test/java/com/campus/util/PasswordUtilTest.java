package com.campus.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void hashProducesA60CharacterBCryptHash() {
        String hash = PasswordUtil.hash("CorrectHorse123!");
        assertEquals(60, hash.length());
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
    }

    @Test
    void hashingTheSamePasswordTwiceProducesDifferentHashes() {
        String hash1 = PasswordUtil.hash("CorrectHorse123!");
        String hash2 = PasswordUtil.hash("CorrectHorse123!");
        assertNotEquals(hash1, hash2, "BCrypt should salt each hash independently");
    }

    @Test
    void verifyReturnsTrueForTheCorrectPassword() {
        String hash = PasswordUtil.hash("CorrectHorse123!");
        assertTrue(PasswordUtil.verify("CorrectHorse123!", hash));
    }

    @Test
    void verifyReturnsFalseForTheWrongPassword() {
        String hash = PasswordUtil.hash("CorrectHorse123!");
        assertFalse(PasswordUtil.verify("WrongPassword", hash));
    }

    @Test
    void verifyReturnsFalseForNullInputsInsteadOfThrowing() {
        String hash = PasswordUtil.hash("CorrectHorse123!");
        assertFalse(PasswordUtil.verify(null, hash));
        assertFalse(PasswordUtil.verify("CorrectHorse123!", null));
    }

    @Test
    void hashRejectsAnEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(""));
    }
}
