import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Resource;
import parser.MvideoParser;
import parser.Parser;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		MvideoParser parser = new MvideoParser();
		long a = System.currentTimeMillis();
		List<Resource> resources = new ArrayList<>();
		for (int i = 10; i < 30; i++) {
			List<Resource> res = parser.getResources("https://www.mvideo.ru/smartfony-i-svyaz-10/smartfony-205/f/tolko-v-nalichii=da?reff=menu_main", i + 1);
			for(int j = 0; j < res.size(); j++) {
				System.out.print(res.get(j) + ", ");
			}
			System.out.println();
			resources.addAll(res);
			Thread.sleep(3000);
		}
		// Resource resource = parser
//				.getResource("https://www.mvideo.ru/bff/product-details?productId=" + res.get(0).getId());
		long b = System.currentTimeMillis();
		System.out.println(b - a);
		Thread.sleep(3000);
		parser.finishWebDriver();
//		a = System.currentTimeMillis();
//		res = parser.getResources("https://www.mvideo.ru/smartfony-i-svyaz-10/smartfony-205", 3);
//		b = System.currentTimeMillis();
//		System.out.println(b - a);

//		String a = "Смартфон Xiaomi Redmi 9C NFC 3GB/64GB Green";
//		String a = "Смартфон realme C35 4/128GB Glowing Black (RMX3511) черный";
//		String a = "Смартфон vivo T1 6/128GB таинственная галактика";
//		String a = "Смартфон Samsung Galaxy A33 5G 6/128GB Black (SM-A336B)";
//		String a = "Смартфон Honor X9a 6/128GB 5109ALXU Silver";
//		String a = "Смартфон Apple iPhone 14 Pro 256GB Space Black";
//		String a = "Смартфон Samsung Galaxy A53 5G 8/256GB Black (SM-A536E)";
		//
//		a = a.replaceAll("\\s/\\s", "/");
//		System.out.println(a);
//		
//		Pattern p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) (.+?(?= \\()) (.*$)");
//		Pattern p2 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*Gb)) ([^\\s]*) (.+?(?= \\()) (.*$)");
//		Pattern p3 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\()) ([^\\s]*) (.*$)");
//		Pattern p4 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*NFC)) ([^\\s]*) (\\w*GB/?\\w*GB) (.*$)");
//		Pattern p5 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) (.*$)");
//		Pattern p6 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) (\\d*\\w*) (.*$)");
////		
//		System.out.println("(SM-A536E)".matches("^\\([\\w-]*\\)$"));
//		Matcher m = p5.matcher(a);
//		System.out.println(m.matches());
//
//		String part1 = null;
//		String part2 = null;
//		String part3 = null;
//		String part4 = null;
//		String part5 = null;
//		String part6 = null;
//
//		part1 = m.group(1);
//		part2 = m.group(2);
//		part3 = m.group(3);
//		part4 = m.group(4);
//		part5 = m.group(5);
//		part6 = m.group(6);
//

//		String[] imageLinks = null;
//		ObjectMapper mapper = new ObjectMapper();
//		ObjectNode node = mapper.createObjectNode();
//		node.put("id", 5);
//		JsonNode a = node.path("name");
//		ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
//		});
//		try {
//			imageLinks = mapper.readValue(node.path("dad").asText(null), String[].class);
//			System.out.println();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String text = node.path("lol").asText(null);
//		Map<String, String> attr = new HashMap<>();
//		System.out.println(attr.keySet());
//		printSimilarity("BQ 5060L Basic 8 ГБ red".toLowerCase(), "BQ 5060L Basic Maroon Red".toLowerCase());
	}

	public static void printSimilarity(String s, String t) {
		System.out.println(String.format("%.3f is the similarity between \"%s\" and \"%s\"", similarity(s, t), s, t));
	}

	public static double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater length
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0;
			/* both strings are zero length */ }
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	}

	public static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
}
