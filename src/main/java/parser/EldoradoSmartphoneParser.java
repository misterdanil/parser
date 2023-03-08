package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import model.MemoryMeasure;
import model.Resource;
import model.Review;
import model.WeightMeasure;
import parser.smartphone.AbstractSmartphoneParser;

public class EldoradoSmartphoneParser extends AbstractSmartphoneParser {
	private final String categoryId = "1461428";
	private List<WebElement> attributes;
	private List<JsonNode> jsonAttributes;

	private static final Logger logger = LoggerFactory.getLogger(EldoradoSmartphoneParser.class);

	@Override
	public Resource getResource(String link) {
		ChromeOptions options = new ChromeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NONE);
		options.addArguments("disable-blink-features=AutomationControlled", "headless",
				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False");
		long a = System.currentTimeMillis();

		WebDriver webDriver = new ChromeDriver(options);
		webDriver.get(link);

		WebElement body = webDriver.findElement(By.tagName("body"));

		String brandName = getBrand(body);

		WebElement characteristicsNode = body
				.findElement(By.className("specificationTextTable q-item-full-specs-table"))
				.findElement(By.tagName("table")).findElement(By.tagName("tbody"));

		attributes = characteristicsNode.findElements(By.tagName("tr"));

		webDriver.close();
		return jsonNode;
	}

	@Override
	public List<Resource> getResources(String link, int page) {
		JsonNode jsonNode = getProductsNode();

		List<Resource> resources = new ArrayList<>();
		for (int i = 0; i < jsonNode.size(); i++) {
			JsonNode productNode = jsonNode.get(i);

			String id = productNode.get("id").asText();
			String productLink = "https://eldorado/cat/detail/" + productNode.get("code").asText();
			String name = productNode.get("name").asText();

			Resource resource = new Resource(id, productLink, name);

			ArrayNode imageLinks = (ArrayNode) productNode.get("images");
			List<String> images = new ArrayList<>();
			for (int j = 0; j < imageLinks.size(); i++) {
				images.add(imageLinks.get("url").asText());
			}
			resource.addAttribute("images", String.join(", ", images.toArray(new String[images.size()])));

			String brand = getBrand(productNode);
			resource.addAttribute("brand", brand);

			String series = getSeries(productNode);
			resource.addAttribute("series", series);

			ArrayNode listingDescriptionNode = (ArrayNode) productNode.get("listingDescription");

			jsonAttributes = listingDescriptionNode.findParents("id");

			ObjectNode ramNode = getRamNode();
			addToResource("ram", resource, ramNode, getMapper());

			ObjectNode romNode = getRomNode();
			addToResource("rom", resource, romNode, getMapper());

			ObjectNode appearanceNode = getMainAppearanceNode();
			addToResource("appearance", resource, appearanceNode, getMapper());

			resources.add(resource);
		}

		return resources;

	}

	protected String getPropertyById(String id, String property) {
		List<JsonNode> nodes = jsonAttributes.stream().filter(item -> item.get("id").asText().equals(id))
				.collect(Collectors.toList());
		if (nodes.size() > 0) {
			return nodes.get(0).get("attributeNameToValueMap").get(property).asText();
		}
		return null;
	}

	private JsonNode getProductsNode() {
		ChromeOptions options = new ChromeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NONE);
		options.addArguments("disable-blink-features=AutomationControlled", "headless",
				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False");
		long a = System.currentTimeMillis();

		WebDriver webDriver = new ChromeDriver(options);
		webDriver.get("https://www.eldorado.ru/c/smartfony/");

		List<WebElement> webElements = new ArrayList<>();
		while (webElements.size() == 0) {
			webElements = webDriver.findElements(By.id("__NEXT_DATA__"));
			System.out.println("check");
			synchronized (webDriver) {
				try {
					webDriver.wait(500);
				} catch (InterruptedException e) {
					throw new RuntimeException("Exception occurred while waiting for searching products element", e);
				}
			}
		}
		String jsonText = webElements.get(0).getAttribute("textContent");
		JsonNode jsonNode;
		try {
			jsonNode = getMapper().readTree(jsonText);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Exception occurred while reading json products data", e);
		}
		jsonNode = jsonNode.get("props").get("initialState").get("products-store-module").get("products");
		webDriver.close();
		return jsonNode;
	}

	@Override
	public List<Review> getReviews(String productId) {
		return null;
	}

	public String getSeries(WebElement webElement) {
		String model = webElement.findElement(By.className("q-fixed-name no-mobile")).getText();
		try {
			return extractByRegex(REGEX_NAME1, model, "series");
		} catch (RegexMismatchException e) {
			logger.info("Couldn't get series from model" + model + " by regex " + REGEX_NAME1);
			return null;
		}
	}

	private static String REGEX_NAME1 = "(?<series>.+?(?= \\d+[/+]\\d+GB)) ([^\\s]*) (?<color>.+?(?= \\()) (.*$)";

	private class RegexMismatchException extends Exception {

		public RegexMismatchException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public RegexMismatchException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

	}

	private String extractByRegex(String regex, String text, String group) throws RegexMismatchException {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(group);
		if (m.matches()) {
			try {
				return m.group(text);
			} catch (IllegalStateException e) {
				throw new RegexMismatchException("Couldn't extract group " + group, e);
			}
		}

		throw new RegexMismatchException("Regex " + regex + " doesn't match " + text);
	}

	public String getBrand(WebElement element) {
		return element.findElement(By.className("i-flocktory")).getAttribute("data-fl-item-vendor");
	}

	public String getOS() {
		return extractByName("Операционная система");
	}

	private String extractByName(String name) {
		for (int i = 0; i < attributes.size(); i++) {
			List<WebElement> tds = attributes.get(i).findElements(By.tagName("td"));
			if (tds.get(0).getText().equals(name)) {
				return tds.get(1).getText();
			}
		}
		return null;
	}

	public ObjectNode getScreenNode(WebElement webElement) {
		String type = extractByName("Тип экрана");
		String diagonal = extractByName("Диагональ экрана");
		String resolution = extractByName("Разрешение экрана");
		String frequency = extractByName("Максимальная частота обновления");

		ObjectNode screenNode = getMapper().createObjectNode();

		addToNode(screenNode, "diagonal", diagonal);
		addToNode(screenNode, "resolution", resolution);
		addToNode(screenNode, "type", type);
		addToNode(screenNode, "frequency", frequency);

		return screenNode;
	}

	public ObjectNode getProcessorNode() {
		String manufacturer = extractByName("Производитель процессора");
		String model = extractByName("Модель процессора");
		String frequency = extractByName("Частота процессора");
		String cores = extractByName("Количество ядер");

		ObjectNode processorNode = getMapper().createObjectNode();
		addToNode(processorNode, "value", manufacturer + " " + model + " " + frequency);
		addToNode(processorNode, "cores", cores);

		return processorNode;
	}

	private boolean toGpsSupport(String text) {
		return text.contains("GPS");
	}

	private boolean toGlonassSupport(String text) {
		return text.contains("ГЛОНАСС");
	}

	public ObjectNode getRamNode() {
		String value = extractByName("Объем оперативной памяти");

		ObjectNode ramNode = getMapper().createObjectNode();
		if (value != null) {
			String[] valueAndMeasure = value.split(" ");
			addToNode(ramNode, "value", valueAndMeasure[0]);
			addToNode(ramNode, "measure", MemoryMeasure.getMemoryMeasureString(valueAndMeasure[1]));
		}

		return ramNode;
	}

	public ObjectNode getRomNode() {
		String value = extractByName("Объем встроенной памяти");

		ObjectNode romNode = getMapper().createObjectNode();
		if (value != null) {
			String[] valueAndMeasure = value.split(" ");
			addToNode(romNode, "value", valueAndMeasure[0]);
			addToNode(romNode, "measure", MemoryMeasure.getMemoryMeasureString(valueAndMeasure[1]));
		}

		return romNode;
	}

	public ObjectNode getBackCameraNode() {
		String mpicsStr = extractByName("Разрешение камеры");
		String mpics = mpicsStr != null ? String.join(", ", mpicsStr.split("+")) : null;
		String count = extractByName("Количество основных камер");
		String zoom = extractByName("Zoom цифровой");
		String flash = extractByName("Встроенная вспышка");

		ObjectNode backCameraNode = getMapper().createObjectNode();
		addToNode(backCameraNode, "mpics", mpics);
		addToNode(backCameraNode, "count", count);
		addToNode(backCameraNode, "zoom", zoom);
		addToNode(backCameraNode, "flash", toBoolean(flash));

		return backCameraNode;
	}

	public ObjectNode getFrontCameraNode() {
		String mpicsStr = extractByName("Разрешение фронтальной камеры");
		String mpics = mpicsStr != null ? String.join(", ", mpicsStr.split("/")) : null;
		String count = extractByName("Количество фронтальных камер");

		ObjectNode frontCameraNode = getMapper().createObjectNode();
		addToNode(frontCameraNode, "mpics", mpics);
		addToNode(frontCameraNode, "count", count);

		return frontCameraNode;
	}

	public ObjectNode getSDCardNode() {
		String sdMax = extractByName("Максимальный объем карт памяти");
		String measure = MemoryMeasure.getMemoryMeasureString(sdMax != null ? sdMax.split(" ")[1] : null);
		String type = extractByName("Карты памяти");

		ObjectNode sdNode = getMapper().createObjectNode();
		addToNode(sdNode, "max", sdMax);
		addToNode(sdNode, "measure", measure);
		addToNode(sdNode, "type", type);

		return sdNode;
	}

	public String getSimValue() {
		String sim = extractByName("Тип SIM-карты");

		return sim;
	}

	public ObjectNode getWirelessNode() {
		Boolean isSupport4G = toBoolean(extractByName("Работа в 4G(LTE)-сетях"));
		Boolean isSupport5G = toBoolean(extractByName("Работа в 5G(LTE)-сетях"));
		String wifi = extractByName("Стандарт Wi-Fi");
		String bluetooth = extractByName("Версия Bluetooth");
		Boolean isSupportGPS = toGpsSupport(extractByName("Спутниковая навигация"));
		Boolean isSupportGlonass = toGlonassSupport(extractByName("Спутниковая навигация"));

		ObjectNode wirelessNode = getMapper().createObjectNode();
		addToNode(wirelessNode, "5g_support", isSupport5G);
		addToNode(wirelessNode, "wifi", wifi);
		addToNode(wirelessNode, "bluetooth", bluetooth);
		addToNode(wirelessNode, "gps_support", isSupportGPS);
		addToNode(wirelessNode, "glonass_support", isSupportGlonass);

		return wirelessNode;
	}

	public ObjectNode getSecurityNode() {
		Boolean isSupportTouchId = toBoolean(extractByName("Сканер отпечатков пальцев"));
		Boolean isSupportFaceId = toBoolean(extractByName("Сенсор распознавания лица	"));

		ObjectNode securityNode = getMapper().createObjectNode();
		addToNode(securityNode, "touch_id", isSupportTouchId);
		addToNode(securityNode, "face_id", isSupportFaceId);

		return securityNode;
	}

	public ObjectNode getInterfacesNode() {
		String connectionInterface = extractByName("Интерфейсный разъем	");
		String headphones = extractByName("Разъем для наушников");

		ObjectNode interfaceNode = getMapper().createObjectNode();
		addToNode(interfaceNode, "connection", connectionInterface);
		addToNode(interfaceNode, "headphones", headphones);

		return interfaceNode;
	}

	public String getMaterial() {
		String material = extractByName("Материал корпуса");

		return material;
	}

	protected Boolean toBoolean(String value) {
		if (value != null) {
			return value.equals("Есть") ? true : false;
		}
		return null;
	}

	public ObjectNode getBatteryNode() {
		String type = extractByName("Тип аккумулятора");
		String capacity = extractByName("Емкость аккумулятора");
		Boolean isSupportFastCharge = toBoolean(extractByName("Поддержка быстрой зарядки"));

		ObjectNode batteryNode = getMapper().createObjectNode();
		addToNode(batteryNode, "type", type);
		addToNode(batteryNode, "capacity", capacity);
		addToNode(batteryNode, "fast_charge_support", isSupportFastCharge);

		return batteryNode;
	}

	public ObjectNode getAppearanceNode() {
		String color = extractByName("Цвет");
		String weight = extractByName("Вес");
		if (weight != null) {
			String measure = WeightMeasure.getWeightMeasureString(weight.split(" ")[1]);
		}
		String height = extractByName("Высота");
		height = height == null ? height.split(" ")[0] : null;

		String width = extractByName("Ширина");
		width = width == null ? width.split(" ")[0] : null;

		String depth = extractByName("Глубина");
		depth = depth == null ? depth.split(" ")[0] : null;

		String measure = height == null ? width.split(" ")[1] : height.split(" ")[1];
		measure = measure == null ? depth.split(" ")[1] : null;

		String dimensions = String.join("*", height, width, depth);
		dimensions += " ";

		ObjectNode appearanceNode = getMapper().createObjectNode();
		addToNode(appearanceNode, "color", color);
		addToNode(appearanceNode, "weight", weight);
		addToNode(appearanceNode, "measure", measure);
		addToNode(appearanceNode, "dimensions", dimensions);

		return appearanceNode;
	}

	public ObjectNode getMainAppearanceNode() {
		String color = getPropertyById(COLOR_ID, "Цвет");

		ObjectNode appearanceNode = getMapper().createObjectNode();
		addToNode(appearanceNode, "color", color);

		return appearanceNode;
	}

}
