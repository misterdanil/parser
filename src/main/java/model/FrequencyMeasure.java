package model;

public enum FrequencyMeasure {
	Hz, kHz, MHz, GHz;

	public static String getFrequencyMeasureString(String value) {
		if (value == null) {
			return null;
		}
		value = value.toLowerCase();
		if (value.equals("гц")) {
			return Hz.toString();
		} else if (value.equals("кгц")) {
			return kHz.toString();
		} else if (value.equals("мгц")) {
			return MHz.toString();
		} else if (value.equals("ггц")) {
			return GHz.toString();
		} else {
			return null;
		}
	}
}
