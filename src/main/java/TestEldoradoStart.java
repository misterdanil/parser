import java.io.IOException;
import java.util.List;

import model.Resource;
import parser.EldoradoSmartphoneParser;

public class TestEldoradoStart {
	public static void main(String[] args) throws IOException, InterruptedException {
		EldoradoSmartphoneParser eldoradoSmartphoneParser = new EldoradoSmartphoneParser();

//		for (int i = 3; i <= 10; i++) {
		Resource resource = eldoradoSmartphoneParser
				.getResource("https://www.eldorado.ru/cat/detail/smartfon-huawei-mate-50-8-256gb-black-cet-lx9/");
//			resources.forEach(resource -> {
//				System.out.println(resource);
//			});
		Thread.sleep(3000);
//		}
//		ObjectMapper mapper = new ObjectMapper();

//		JsonNode node = mapper.readTree(Files.readAllBytes(Paths.get("C:/Users/jonti/Documents/test_eldorado.txt")));
//		Iterator<Entry<String, JsonNode>> it = node.get("props").get("initialState").get("products-store-module")
//				.get("products").fields();
//		while(it.hasNext()) {
//			it.next().get
//		}
	}
}
