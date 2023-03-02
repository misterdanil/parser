package model;

public enum WeightMeasure {
	MG, G, KG, T;

	public static String getWeightMeasureString(String value) {
		if (value == null) {
			return null;
		}
		value = value.toLowerCase();

		if (value.equals("мг")) {
			return MG.toString();
		} else if (value.equals("г")) {
			return MG.toString();
		} else if (value.equals("кг")) {
			return MG.toString();
		} else if (value.equals("т")) {
			return MG.toString();
		} else {
			return null;
		}
	}
}
