package net.miarma.api.common.security;

public class DNIValidator {

    public static boolean isValid(String id) {
        if (id == null || id.length() != 9) {
            return false;
        }

        id = id.toUpperCase(); // Pa evitar problemas con minúsculas
        String numberPart;
        char letterPart = id.charAt(8);

        if (id.startsWith("X") || id.startsWith("Y") || id.startsWith("Z")) {
            // NIE
            char prefix = id.charAt(0);
            String numericPrefix = switch (prefix) {
                case 'X' -> "0";
                case 'Y' -> "1";
                case 'Z' -> "2";
                default -> null;
            };

            if (numericPrefix == null) return false;

            numberPart = numericPrefix + id.substring(1, 8);
        } else {
            // DNI
            numberPart = id.substring(0, 8);
        }

        if (!numberPart.matches("\\d{8}")) {
            return false;
        }

        int number = Integer.parseInt(numberPart);
        char expectedLetter = calculateLetter(number);

        return letterPart == expectedLetter;
    }

    private static char calculateLetter(int number) {
        String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        return letters.charAt(number % 23);
    }
}
