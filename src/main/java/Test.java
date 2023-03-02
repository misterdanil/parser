import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

import model.FrequencyMeasure;
import model.Resource;
import parser.MvideoParser;
import parser.Parser;

public class Test {
	public static void main(String[] args) throws IOException {
		Parser parser = new MvideoParser();
		Resource res = parser.getResource("https://www.mvideo.ru/bff/product-details?productId=30066554");
		System.out.println();
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
	}
}
