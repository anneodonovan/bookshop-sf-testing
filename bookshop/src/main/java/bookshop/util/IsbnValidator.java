package bookshop.util;

/**
 * Validates ISBN-10 and ISBN-13 numbers.
 * Does NOT contact an external repository — validates format and check digit only.
 */
public class IsbnValidator {

    /**
     * Returns true if the ISBN is a valid ISBN-10 or ISBN-13.
     */
    public static boolean isValid(String isbn) {
        if (isbn == null) return false;
        String clean = isbn.replaceAll("[\\s-]", ""); // remove spaces and hyphens
        if (clean.length() == 10) return isValidIsbn10(clean);
        if (clean.length() == 13) return isValidIsbn13(clean);
        return false;
    }

    /** ISBN-10 check digit validation. */
    static boolean isValidIsbn10(String isbn) {
        if (!isbn.substring(0, 9).matches("\\d{9}")) return false;
        char lastChar = isbn.charAt(9);
        if (!Character.isDigit(lastChar) && lastChar != 'X' && lastChar != 'x') return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }
        int checkDigit = (lastChar == 'X' || lastChar == 'x') ? 10 : (lastChar - '0');
        sum += checkDigit;
        return sum % 11 == 0;
    }

    /** ISBN-13 check digit validation. */
    static boolean isValidIsbn13(String isbn) {
        if (!isbn.matches("\\d{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == (isbn.charAt(12) - '0');
    }
}