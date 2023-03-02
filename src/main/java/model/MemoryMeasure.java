package model;

public enum MemoryMeasure {
	B, KB, MB, GB, TB, PB;

	public static String getMemoryMeasureString(String value) {
		if (value == null) {
			return null;
		}
		value = value.toLowerCase();

		if (value.equals("б")) {
			return B.toString();
		} else if (value.equals("кб")) {
			return KB.toString();
		} else if (value.equals("мб")) {
			return MB.toString();
		} else if (value.equals("гб")) {
			return GB.toString();
		} else if (value.equals("пб")) {
			return PB.toString();
		} else {
			return null;
		}
	}
}
