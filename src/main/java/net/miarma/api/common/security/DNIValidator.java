package net.miarma.api.common.security;

/**
 * Validador de DNI/NIE español.
 * <p>
 * Este validador comprueba si un DNI o NIE es válido según las reglas establecidas por la legislación española.
 * Un DNI debe tener 8 dígitos seguidos de una letra, y un NIE debe comenzar con X, Y o Z seguido de 7 dígitos y una letra.
 *
 * @author José Manuel Amador Gallardo
 */
public class DNIValidator {

    /**
     * Valida un DNI o NIE español.
     *
     * @param id El DNI o NIE a validar.
     * @return true si el DNI/NIE es válido, false en caso contrario.
     */
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

    /**
     * Calcula la letra correspondiente a un número de DNI.
     *
     * @param number El número del DNI (8 dígitos).
     * @return La letra correspondiente.
     */
    private static char calculateLetter(int number) {
        String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        return letters.charAt(number % 23);
    }
}
