package bookshop;

import bookshop.util.IsbnValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class IsbnValidatorTest {

    //13 Lenght IsBns
    @ParameterizedTest
    @ValueSource(strings = {"9780261102217","9780307887436", "978-0-26-110221-7"})
    public void IsbnTestLenght(String testValue) {
            assertEquals(testValue.length(), 13);
    }

    public void IsbnTestVal(String testValue) {
        assertTrue(IsbnValidator.isValid(testValue));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"NOTCORRECT","9780455 4935"})
        public void IsbnTestLenghtF(String testValue) {
            assertNotEquals(testValue.length(), 13);
    }

    public void IsbnTestValF(String testValue) {
        assertFalse(IsbnValidator.isValid(testValue));
    }

    //10 Lenght IsBns
    @ParameterizedTest
    @ValueSource(strings = {"0330258648","0399208534", "0-399-20853-4"})
    public void IsbnTestLenghtTen(String testValue) {
            assertEquals(testValue.length(), 10);
    }

    public void IsbnTestValTen(String testValue) {
        assertTrue(IsbnValidator.isValid(testValue));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"NOT CORRECT","97804554935"})
        public void IsbnTestLenghtFTen(String testValue) {
            assertNotEquals(testValue.length(), 10);
    }

    public void IsbnTestValFTen(String testValue) {
        assertFalse(IsbnValidator.isValid(testValue));
    }

    @Test
    public void IsBnTestNull() {
        assertFalse(IsbnValidator.isValid(null));
    }

    @Test
    public void IsBnTestEmpty() {
        assertFalse(IsbnValidator.isValid(""));
    }
}

