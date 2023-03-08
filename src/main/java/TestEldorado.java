import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestEldorado {
	public static void main(String[] args) {
		Pattern p1 = Pattern.compile("^(Смартфон) (?<series>.+?(?= \\d+[/+]\\d+GB)) ([^\\s]*) (?<color>.+?(?= \\()) (.*$)");
		
		String a = "Смартфон C31 3+32GB Dark Green (RMX3501)";
		
		Matcher m = p1.matcher(a);
		System.out.println(m.matches());
		System.out.println(m.group("series"));
		System.out.println(m.group("color"));
	}
}
