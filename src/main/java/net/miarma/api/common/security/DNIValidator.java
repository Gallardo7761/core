package net.miarma.api.common.security;

public class DNIValidator {
	public static boolean isValid(String dni) {
		if (dni == null || dni.length() != 9) {
			return false;
		}

		String numberPart = dni.substring(0, 8);
		char letterPart = dni.charAt(8);

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
