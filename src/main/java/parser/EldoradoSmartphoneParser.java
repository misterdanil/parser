package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import model.MemoryMeasure;
import model.Resource;
import model.Review;
import model.WeightMeasure;
import parser.smartphone.AbstractSmartphoneParser;

public class EldoradoSmartphoneParser extends AbstractSmartphoneParser {
	private final String categoryId = "1461428";
//	private Map<String, WebElement> attributes = new HashMap<>();

	private Map<String, Element> attributes = new HashMap<>();
	private List<JsonNode> jsonAttributes;
	private WebDriver webDriver;
	private int times;
	private boolean test = false;

	private static final Logger logger = LoggerFactory.getLogger(EldoradoSmartphoneParser.class);

	public EldoradoSmartphoneParser() {
		RAM_ID = "15451";
		ROM_ID = "15451";

		COLOR_ID = "0";
	}

	@Override
	public void createWebDriver() {
		ChromeOptions options = new ChromeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NONE);
		options.addArguments("disable-blink-features=AutomationControlled", "--remote-allow-origins=*",
				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False",
				"devtools.jsonview.enabled=false");
		webDriver = new ChromeDriver(options);
		System.setProperty("webdriver.chrome.silentOutput", "true");
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
	}

	private static class Wrap {
		private Result result;
		private List<WebElement> webElements;
		private boolean isExist = true;
	}

	private static enum Result {
		OK, BLOCKED;
	}

	@Override
	public void finishWebDriver() {
		webDriver.quit();
		webDriver = null;
	}

	@Override
	public Resource getResource(Resource resource) {
		if (times == 20) {
			times = 0;
			return null;
		}
		System.out.println("starting getting product " + resource.getLink());
		Wrap wrap = new Wrap();
		if (test) {
			webDriver.navigate().to(resource.getLink() + "/");
		} else {
			webDriver.navigate().to(resource.getLink());
		}
		WebDriverWait wdw = new WebDriverWait(webDriver, Duration.ofSeconds(20));
		wdw.until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					List<WebElement> head = driver.findElements(By.tagName("head"));
					if (head.size() > 0) {
						String text = head.get(0).findElement(By.tagName("title")).getAttribute("textContent");
						if (text.equals("Access Blocked")) {
							wrap.result = Result.BLOCKED;
						}
					}
					List<WebElement> nextData = webDriver.findElements(By.className("specificationTextTable"));
					if (nextData.size() > 0) {
						wrap.result = Result.OK;
					}
					return wrap.result != null;
				} catch (StaleElementReferenceException e) {
					return apply(driver);
				}
			}
		});

		if (wrap.result == Result.BLOCKED) {
			finishWebDriver();
			createWebDriver();
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			times++;
			test = test == false;
			return getResource(resource);
		}

		times = 0;

		Document doc = Jsoup.parse(webDriver.getPageSource());
//		WebElement characteristicsNode = webDriver.findElement(".specificationTextTable"))
//				.findElement(By.tagName("table")).findElement(By.tagName("tbody"));

		Element characteristicsNode = doc.select(".specificationTextTable").get(0).select("table").get(0)
				.select("tbody").get(0);

//		List<WebElement> tempAttributes = characteristicsNode.findElements(By.tagName("tr"));

		Elements tempAttributes = characteristicsNode.select("tr");

		tempAttributes.forEach(tempAttribute -> {
//			List<WebElement> tds = tempAttribute.findElements(By.tagName("td"));

			Elements tds = tempAttribute.select("td");
			if (tds.size() > 1) {
//				attributes.put(tds.get(0).getAttribute("textContent"), tds.get(1));
				attributes.put(tds.get(0).text(), tds.get(1));
			}
		});

		resource.addAttribute("os", getOS());
		addToResource("screen", resource, getScreenNode(), getMapper());
		addToResource("screen", resource, getScreenNode(), getMapper());
		addToResource("processor", resource, getProcessorNode(), getMapper());
		addToResource("ram", resource, getRamNode(Type.HTML), getMapper());
		addToResource("rom", resource, getRomNode(Type.HTML), getMapper());
		addToResource("back_camera", resource, getBackCameraNode(), getMapper());
		addToResource("front_camera", resource, getFrontCameraNode(), getMapper());
		addToResource("sd", resource, getSDCardNode(), getMapper());
		resource.addAttribute("sim", getSimValue());
		addToResource("wireless", resource, getWirelessNode(), getMapper());
		addToResource("security", resource, getSecurityNode(), getMapper());
		addToResource("interface", resource, getInterfacesNode(), getMapper());
		resource.addAttribute("material", getMaterial());
		addToResource("battery", resource, getBatteryNode(), getMapper());
		addToResource("appearance", resource, getAppearanceNode(), getMapper());

		attributes.clear();
		jsonAttributes.clear();

		return resource;
	}

	@Override
	public Resource getResource(String link) {
//		while (webDriver.findElements(By.className("q-fixed-name")).size() == 0) {
//			System.out.println("No model");
//		}
//
//		WebElement body = webDriver.findElement(By.tagName("body"));
//
//		String model = body.findElement(By.className("q-fixed-name")).findElement(By.tagName("p"))
//				.getAttribute("textContent");
//
//		String id = getId(body);
//		String brandName = getBrand(body);
//		String series = getSeries(model);
//
//		WebElement characteristicsNode = body.findElement(By.className("specificationTextTable"))
//				.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
//
//		List<WebElement> tempAttributes = characteristicsNode.findElements(By.tagName("tr"));
//		tempAttributes.forEach(tempAttribute -> {
//			List<WebElement> tds = tempAttribute.findElements(By.tagName("td"));
//			if (tds.size() > 1) {
//				attributes.put(tds.get(0).getAttribute("textContent"), tds.get(1));
//			}
//		});
//
//		Resource resource = new Resource(id, link, model);
//		resource.addAttribute("brand", brandName);
//		resource.addAttribute("series", series);
//
//		resource.addAttribute("os", getOS());
//		addToResource("screen", resource, getScreenNode(), getMapper());
//		addToResource("processor", resource, getProcessorNode(), getMapper());
//		addToResource("ram", resource, getRamNode(Type.HTML), getMapper());
//		addToResource("rom", resource, getRomNode(Type.HTML), getMapper());
//		addToResource("back_camera", resource, getBackCameraNode(), getMapper());
//		addToResource("front_camera", resource, getFrontCameraNode(), getMapper());
//		addToResource("sd", resource, getSDCardNode(), getMapper());
//		resource.addAttribute("sim", getSimValue());
//		addToResource("wireless", resource, getWirelessNode(), getMapper());
//		addToResource("security", resource, getSecurityNode(), getMapper());
//		addToResource("interface", resource, getInterfacesNode(), getMapper());
//		resource.addAttribute("material", getMaterial());
//		addToResource("battery", resource, getBatteryNode(), getMapper());
//		addToResource("appearance", resource, getAppearanceNode(), getMapper());
//
//		return resource;

		return null;
	}

	@Override
	public List<Resource> getResources(String link, int page) {
		System.out.println("Starting getting data from " + link);
		Iterator<Entry<String, JsonNode>> productsIt = getProductsNode(link + "/?page=" + page);
		System.out.println("Got product nodes");

		List<Resource> resources = new ArrayList<>();
		while (productsIt.hasNext()) {
			JsonNode productNode = productsIt.next().getValue();

			if (productNode.has("agent")) {
				continue;
			}

			String category = productNode.get("categoryId").asText();
			if (!category.equals(categoryId)) {
				continue;
			}

			String id = productNode.get("id").asText();
			String productLink = "https://eldorado.ru/cat/detail/" + productNode.get("code").asText();
			String name = productNode.get("name").asText();
			String model = productNode.get("model").asText();

			Resource resource = new Resource(id, productLink, name);
			resource.setPrice(Double.valueOf(productNode.get("price").asText()));

			ArrayNode imageLinks = (ArrayNode) productNode.get("images");
			List<String> images = new ArrayList<>();
			for (int j = 0; j < imageLinks.size(); j++) {
				images.add("https://static.eldorado.ru" + imageLinks.get(j).get("url").asText());
			}

			resource.getImages().addAll(images);

			String brand = getBrandFromJson(productNode);
			resource.addAttribute("brand", brand);

			String series = getSeries(model);
			resource.addAttribute("series", series);

			ArrayNode listingDescriptionNode = (ArrayNode) productNode.get("listingDescription");

			jsonAttributes = listingDescriptionNode.findParents("id");

			ObjectNode ramNode = getRamNode(Type.JSON);
			addToResource("ram", resource, ramNode, getMapper());

			ObjectNode romNode = getRomNode(Type.JSON);
			addToResource("rom", resource, romNode, getMapper());

			ObjectNode appearanceNode = getMainAppearanceNode(Type.JSON);
			addToResource("appearance", resource, appearanceNode, getMapper());
			resource.getColors().put(appearanceNode.get("color").asText(), productLink);

			resources.add(resource);
		}

		return resources;

	}

	protected String getPropertyById(String id, String property) {
		List<JsonNode> nodes = jsonAttributes.stream().filter(item -> item.get("id").asText().equals(id))
				.collect(Collectors.toList());
		if (nodes.size() > 0) {
			return nodes.get(0).get("attributeNameToValueMap").path(property).asText(null);
		}
		return null;
	}

	private Iterator<Entry<String, JsonNode>> getProductsNode(String link) {
		Wrap wrap = new Wrap();
		webDriver.navigate().to(link);

		WebDriverWait wdw = new WebDriverWait(webDriver, Duration.ofSeconds(30));
		wdw.until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				wrap.webElements = driver.findElements(By.tagName("head"));
				try {
					if (wrap.webElements.size() > 0) {
						String text = wrap.webElements.get(0).findElement(By.tagName("title"))
								.getAttribute("textContent");
						if (text.equals("Access Blocked")) {
							wrap.result = Result.BLOCKED;
						}
					}
					wrap.webElements = webDriver.findElements(By.id("__NEXT_DATA__"));
					if (wrap.webElements.size() > 0) {
						wrap.result = Result.OK;
					}
				} catch (StaleElementReferenceException e) {
					return apply(driver);
				}
				return wrap.result != null;
			}
		});

		if (wrap.result == Result.BLOCKED) {
			webDriver.navigate().to("https://eldorado.ru");
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return getProductsNode(link);
		}

		String jsonText = wrap.webElements.get(0).getAttribute("textContent");
		JsonNode jsonNode;
		try {
			jsonNode = getMapper().readTree(jsonText);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Exception occurred while reading json products data", e);
		}
		return jsonNode.get("props").get("initialState").get("products-store-module").get("products").fields();
	}

	public String getId(WebElement webElement) {
		return webElement.findElement(By.className("basket")).getAttribute("data-gtm-product-id");
	}

	public String getSeries(String model) {
		try {
			return extractByRegex(REGEX_NAME1, model, "series");
		} catch (RegexMismatchException e) {
			System.out.println("Couldn't get series from model" + model + " by regex " + REGEX_NAME1);
			return null;
		}
	}

	private static String REGEX_NAME1 = "(?<series>.+?(?= \\d*[/+]?\\d+(GB|TB))) (.*$)";

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
		Matcher m = p.matcher(text);
		if (m.matches()) {
			try {
				return m.group(group);
			} catch (IllegalStateException e) {
				throw new RegexMismatchException("Couldn't extract group " + group, e);
			}
		}

		throw new RegexMismatchException("Regex " + regex + " doesn't match " + text);
	}

	public String getBrand(WebElement element) {
		return element.findElement(By.className("i-flocktory")).getAttribute("data-fl-item-vendor");
	}

	public String getBrandFromJson(JsonNode node) {
		return node.path("brandName").asText();
	}

	public String getOS() {
		return extractByName("Операционная система");
	}

	private String extractByName(String name) {
		if (attributes.containsKey(name)) {
			String text = attributes.get(name).ownText();
			if (text.contains("&nbsp")) {
				text = text.substring(0, text.indexOf("&nbsp"));
			}
			return text;
		}
		return null;
	}

	public ObjectNode getScreenNode() {
		String type = extractByName("Тип экрана");
		String diagonal = extractByName("Диагональ экрана");
		if (diagonal != null) {
			if (diagonal.contains("\"")) {
				diagonal = diagonal.replace("\"", "");
			}
		}
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

	private static enum Type {
		JSON, HTML;
	}

	public ObjectNode getRamNode(Type type) {
		String value;
		if (type.equals(Type.JSON)) {
			value = getPropertyById(RAM_ID, "Объем оперативной памяти");
		} else {
			value = extractByName("Объем оперативной памяти");
		}
		ObjectNode ramNode = getMapper().createObjectNode();
		if (value != null) {
			String[] valueAndMeasure = value.split(" ");
			addToNode(ramNode, "value", valueAndMeasure[0]);
			addToNode(ramNode, "measure", MemoryMeasure.getMemoryMeasureString(valueAndMeasure[1]));
		}

		return ramNode;
	}

	public ObjectNode getRomNode(Type type) {
		String value;
		if (type.equals(Type.JSON)) {
			value = getPropertyById(RAM_ID, "Объем встроенной памяти");
		} else {
			value = extractByName("Объем встроенной памяти");
		}
		ObjectNode romNode = getMapper().createObjectNode();
		if (value != null) {
			String[] valueAndMeasure = value.split(" ");
			addToNode(romNode, "value", valueAndMeasure[0]);
			addToNode(romNode, "measure", MemoryMeasure.getMemoryMeasureString(valueAndMeasure[1]));
		}

		return romNode;
	}

	public ObjectNode getBackCameraNode() {
		String mpics = extractByName("Разрешение камеры");
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
		String mpics = extractByName("Разрешение фронтальной камеры");
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
		String navigation = extractByName("Спутниковая навигация");
		Boolean isSupportGPS = navigation != null ? toGpsSupport(navigation) : null;
		Boolean isSupportGlonass = navigation != null ? toGlonassSupport(navigation) : null;

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
//		String weightMeasure = WeightMeasure.getWeightMeasureString(weight.split(" ")[1]);

		String height = extractByName("Высота");
		String clearHeight = height != null ? height.split(" ")[0] : null;

		String width = extractByName("Ширина");
		String clearWidth = width != null ? width.split(" ")[0] : null;

		String depth = extractByName("Глубина");
		String clearDepth = depth != null ? depth.split(" ")[0] : null;

		String measure = null;
		if (width == null && height == null && depth != null) {
			measure = depth.split(" ")[1];
		} else if (width != null || height != null) {
			measure = height == null ? width.split(" ")[1] : height.split(" ")[1];
		}

		String dimensions = String.join("*", clearHeight, clearWidth, clearDepth);
		dimensions += " ";

		ObjectNode appearanceNode = getMapper().createObjectNode();
		addToNode(appearanceNode, "color", color);
		addToNode(appearanceNode, "weight", weight);
		addToNode(appearanceNode, "measure", measure);
		addToNode(appearanceNode, "dimensions", dimensions);

		return appearanceNode;
	}

	public ObjectNode getMainAppearanceNode(Type type) {
		String color;
		if (type.equals(Type.JSON)) {
			color = getPropertyById(COLOR_ID, "Цвет");
		} else {
			color = extractByName("Цвет");
		}
		ObjectNode appearanceNode = getMapper().createObjectNode();
		addToNode(appearanceNode, "color", color);

		return appearanceNode;
	}

	@Override
	public List<Review> getReviews(Resource resource) {
		if (times == 20) {
			times = 0;
			return null;
		}

		int page = 1;
		String link = resource.getLink();

		Wrap wrap = new Wrap();

		List<Review> reviews = new ArrayList<>();

		while (wrap.isExist) {
			webDriver.navigate().to(link + "/page/" + page + "/?show=response");

			WebDriverWait wdw = new WebDriverWait(webDriver, Duration.ofSeconds(20));
			wdw.until(new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver driver) {
					try {
						List<WebElement> head = driver.findElements(By.tagName("head"));
						if (head.size() > 0) {
							String text = head.get(0).findElement(By.tagName("title")).getAttribute("textContent");
							if (text.equals("Access Blocked")) {
								wrap.result = Result.BLOCKED;
								return true;
							}
						}
						List<WebElement> userReviews = webDriver.findElements(By.className("usersReviewsList"));
						if (userReviews.size() > 0
								&& userReviews.get(0).findElements(By.className("usersReviewsListItem")).size() == 0) {
							wrap.isExist = false;
							wrap.result = Result.OK;
						} else {
							wrap.result = Result.OK;
						}

						return wrap.result.equals(Result.OK);

					} catch (StaleElementReferenceException e) {
						return apply(driver);
					}
				}
			});
			if (wrap.result == Result.BLOCKED) {
				webDriver.manage().deleteAllCookies();
				finishWebDriver();
				createWebDriver();
				times++;
				test = test == false;
				return getReviews(resource);
			}

			if (wrap.isExist) {
				times = 0;

				Document pageNode = Jsoup.parse(webDriver.getPageSource());
				Elements reviewNodes = pageNode.getElementsByClass("usersReviewsListItem");
				reviewNodes.forEach(reviewNode -> {
					Element topNode = reviewNode.getElementsByClass("topBlockItem").get(0);
					String sender = topNode.getElementsByClass("userName").get(0).ownText();

					String date = topNode.getElementsByClass("userReviewDate").get(0).ownText();
					SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy hh:mm:ss");
					Date sendDate;
					try {
						sendDate = sdf.parse(date);
					} catch (ParseException e) {
						throw new RuntimeException(
								String.format("Exception occurred while parsing date. Couldn't parse date '%s'", date),
								e);
					}

					String text = reviewNode.getElementsByClass("middleBlockItem").get(0).ownText();
					Integer rating = reviewNode.getElementsByClass("starFull").size();

					Review review = new Review(sender, text, sendDate);
					review.setRating(rating);

					reviews.add(review);
				});

				page++;
			}
		}
		return reviews;
	}

}
