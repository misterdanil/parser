import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import parser.MvideoParser;

public class Test2 {
	/**
	 * @param args
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {
//		String a = "Смартфон Xiaomi Redmi 9C NFC 3GB/64GB Green";
//		String a = "Смартфон realme C35 4/128GB Glowing Black (RMX3511) черный";
//		String a = "Смартфон vivo T1 6/128GB таинственная галактика";
//		String a = "Смартфон Samsung Galaxy A33 5G 6/128GB Black (SM-A336B)";
//		String a = "Смартфон Honor X9a 6/128GB 5109ALXU Silver";
//		String a = "Смартфон Apple iPhone 14 Pro 256GB Space Black";
//		String a = "Смартфон Samsung Galaxy A53 5G 8/256GB Black (SM-A536E)";
//		String a = "Смартфон Apple iPhone 14 128GB Midnight (Dual Sim)";
//		String a = "Смартфон ZTE Blade A51 3G/64G Gr";
//		String a = "Смартфон ZTE Blade V40 Vita 4G/128G Bl";
//		String a = "Смартфон Honor 70 8/128 Emerald Green";
//		String a = "Смартфон Doogee X98 Pro Graphite Gray";
//		String a = "Смартфон Samsung Galaxy S22 (SM-S901B) 8/256Gb Pink";
//		String a = "Смартфон Ulefone Armor X5 red/красный";
//		String a = "Смартфон Xiaomi Redmi 10C NFC 4/64 Graphite";
//		String a = "Смартфон Itel A48 Violet";
//		String a = "Смартфон realme C31 3/32 Light Silver (RMX3501)";
//		String a = "Смартфон HUAWEI nova Y61 4/64Gb (EVE-LX9N) Black";

//		 String a = "Смартфон HUAWEI nova Y70 Midnight Black (MGA-LX9N)";
//		String a = "Смартфон Infinix Hot 20i (X665E) 4/128Gb Black";
//		String a = "Смартфон realme C31 3/32 Dark Green (RMX3501)";
//		String a = "Смартфон Tecno Spark 8c 4/64 Diamond Gray";
//		String a = "Смартфон HUAWEI P50 Pro Cocoa Gold (JAD-LX9)";
//		String a = "Смартфон Samsung Galaxy Z Fold4 512GB Graygreen";
//		String a = "Смартфон Samsung Galaxy Z Fold4 512GB Graygreen (SM-F936)";
//		String a = "Смартфон Tecno Spark Go (2/32) Ice Silver";
//		String a = "Смартфон vivo V23e Dancing Waves (2116)";
//		String a = "Смартфон realme C25Y 4/64 Metal Gray (RMX3269)";
//		String a = "Смартфон HUAWEI Mate 50 8/256Gb (CET-LX9) Black";
		String a = "Смартфон Doogee S88 Plus Orange";
		MvideoParser parser = new MvideoParser();
		
		String[] arg = parser.getUniqueInfo(a);
		parser.finishWebDriver();

//		a = a.replaceAll("\\s/\\s", "/");
//		System.out.println(a);
//
//		Pattern p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) (.+?(?= \\()) (.*$)");
//		Pattern p2 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*Gb)) (.+?(?= \\()) ([^\\s]*) (.*$)");
//		Pattern p11 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\()) ([^\\s]*) (\\w*/\\w*Gb) (.*$)");
//		Pattern p3 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\()) ([^\\s]*) (.*$)");
//		Pattern p4 = Pattern
//				.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*NFC)) ([^\\s]*) (\\w*/?\\w*(GB|Gb|)?) (?<color>.*$)");
//		Pattern p5 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) (.*$)");
//		Pattern p6 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) (\\d+\\w*) (.*$)");
//		Pattern p7 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) ([^\\s]*) (.*$)");
//		Pattern p8 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*G)) ([^\\s]*) (.*$)");
//		Pattern p9 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\d+/?\\d+)) ([^\\s]*) (.*$)");
//		Pattern p12 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\d+/?\\d+)) ([^\\s]*) (.+?(?= \\()) (.*$)");
//		Pattern p13 = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*GB)) ([^\\s]*) (.*$)");
//
//		Matcher m = p2.matcher(a);
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
//		String part7 = m.group("color");

	}
}
