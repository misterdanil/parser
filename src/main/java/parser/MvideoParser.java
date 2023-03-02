package parser;

import java.io.IOException;


import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.devtools.v109.network.model.RequestId;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import model.FrequencyMeasure;
import model.MemoryMeasure;
import model.Resource;
import model.Review;
import model.WeightMeasure;

public class MvideoParser implements Parser {

	private static final String HOME_LINK = "https://mvideo.ru";

	private static final String SERIES_ID = "46";
	private static final String MODEL_ID = "752";
	private static final String OS_ID = "30851850";

	private static final String SCREEN_RESOLUTION_ID = "30848201";
	private static final String SCREEN_TECHNOLOGY_ID = "30852907";
	private static final String SCREEN_FREQUENCY_ID = "2119";
	private static final String SCREEN_BRIGHTLESS_ID = "2055";

	private static final String PROCESSOR_FREQUENCY_ID = "619180";
	private static final String PROCESSOR_CORES_ID = "30852972";

	private static final String RAM_ID = "30852805";

	private static final String ROM_ID = "613463";

	private static final String CAMERA_MPICS_ID = "30848204";
	private static final String CAMERA_NUMBER_ID = "30848501";
	private static final String CAMERA_RESOLUTION_ID = "30852911";
	private static final String CAMERA_ZOOM_ID = "30852957";
	private static final String CAMERA_FLASH_ID = "30852865";

	private static final String FRONT_CAMERA_NUMBER_ID = "30852914";
	private static final String FRONT_CAMERA_MPICS_ID = "30852958";

	private static final String SD_MAX_ID = "601934";
	private static final String SD_TYPE_ID = "332";

	private static final String SIM_ID = "30852858";

	private static final String MOBILE_WIRELESS_ID = "30852925";
	private static final String MOBILE_5G_ID = "30853313";
	private static final String WIFI_ID = "210";
	private static final String NFC_ID = "153";
	private static final String MIRACAST_ID = "677";
	private static final String BLUETOOTH_ID = "135";
	private static final String GPS_ID = "4629";
	private static final String GLONASS_ID = "2380";

	private static final String TOUCH_ID = "30852774";
	private static final String FACE_ID = "8537";

	private static final String INTERFACE_ID = "263";
	private static final String HEADPHONES_ID = "2335";

	private static final String MATERIAL_ID = "83";

	private static final String BATTERY_TYPE_ID = "267";
	private static final String BATTERY_CAPACITY_ID = "11908";
	private static final String BATTERY_FAST_CHARGE_ID = "30852685";

	private static final String COLOR_ID = "30852685";
	private static final String WEIGHT_ID = "11929";
	private static final String DIMENSIONS_ID = "30852910";

	private final ObjectMapper mapper = new ObjectMapper();

	private List<JsonNode> attributes;

	Long cur = null;

	@Override
	public Resource getResource(String link) {
		long a = System.currentTimeMillis();
		WebClient webClient = getWebClient();

		webClient.addRequestHeader("accept", "application/json");
		String content;
		try {
			Page page = webClient.getPage(link);
			content = page.getWebResponse().getContentAsString();
		} catch (FailingHttpStatusCodeException | IOException e) {
			throw new RuntimeException(String.format(
					"Exception occurred while getting mvideo json resources. Couldn't get data from '%s'", link), e);
		}
		long b = System.currentTimeMillis();
		System.out.println(b - a);

		JsonNode body;
		try {
			body = mapper.readTree(content).get("body");
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format(
					"Exception occured while transformating string json to jackson json node. Couldn't transform product data:\n%s",
					link), e);
		}

		String id = body.path("productId").asText(null);
		String name = body.path("name").asText(null);
		Resource resource = new Resource(id, link, name);

		String images = body.path("images").toPrettyString();
		if (images != null) {
			try {
				String[] imageLinks = mapper.readValue(images, String[].class);
				resource.addAttribute("images", String.join(", ", imageLinks));
			} catch (IOException e) {
				throw new RuntimeException(String.format(
						"Exception occurred while parsing json images. Couldn't get images from json:\n%s", body), e);
			}
		}

		JsonNode all = body.get("properties").get("all");

		resource.addAttribute("brand", body.path("brandName").asText(null));
		resource.addAttribute("model", body.path("modelName").asText(null));

		attributes = body.get("properties").get("all").findParents("id");

		parseCharacteristics(all, resource);

		return resource;
	}

	private WebClient getWebClient() {
		long a = System.currentTimeMillis();
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
//		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		String mvideoUrl = "https://www.mvideo.ru";

		// MVideo requires some cookies. This getting page download all necessary
		// cookies
		try {
			webClient.getPage(mvideoUrl);
		} catch (FailingHttpStatusCodeException | IOException e) {
			throw new RuntimeException(String.format(
					"Exception occured while getting page. Couldn't handle correct page with url '%s'", mvideoUrl), e);
		}

		return webClient;
	}

	private void parseCharacteristics(JsonNode all, Resource resource) {
		addInitialData(resource, all);
		addScreenNode(resource, all, mapper);
		addProcessorNode(resource, all, mapper);
		addRamNode(resource, all, mapper);
		addRomNode(resource, all, mapper);
		addBackCameraNode(resource, all, mapper);
		addFrontCameraNode(resource, all, mapper);
		addSDCardNode(resource, all, mapper);
		addSimNode(resource, all, mapper);
		addWirelessNode(resource, all, mapper);
		addSecurityNode(resource, all, mapper);
		addInterfacesNode(resource, all, mapper);
		addMaterialNode(resource, all, mapper);
		addBatteryNode(resource, all, mapper);
		addAppearanceNode(resource, all, mapper);
	}

	private void addInitialData(Resource resource, JsonNode node) {
		String series = getPropertyById(SERIES_ID, "value");
		resource.addAttribute("series", series);
		String model = getPropertyById(MODEL_ID, "value");
		resource.addAttribute("model", model);
		String os = getPropertyById(OS_ID, "value");
		resource.addAttribute("os", os);
	}

	private String getPropertyById(String id, String property) {
		List<JsonNode> nodes = attributes.stream().filter(item -> item.get("id").asText().equals(id))
				.collect(Collectors.toList());
		if (nodes.size() > 0) {
			return nodes.get(0).get(property).asText();
		}
		return null;
	}

	private void addScreenNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String screen = getPropertyById(SCREEN_RESOLUTION_ID, "value");
		String type = getPropertyById(SCREEN_TECHNOLOGY_ID, "value");
		String frequency = getPropertyById(SCREEN_FREQUENCY_ID, "value");
		String frequencyMeasure = FrequencyMeasure
				.getFrequencyMeasureString(getPropertyById(SCREEN_FREQUENCY_ID, "measure"));
		String brightless = getPropertyById(SCREEN_BRIGHTLESS_ID, "value");

		ObjectNode screenNode = mapper.createObjectNode();

		addToNode(screenNode, "diagonal", screen.substring(0, screen.indexOf("\"")));
		addToNode(screenNode, "resolution", screen.substring(screen.indexOf("/") + 1, screen.length()));
		addToNode(screenNode, "type", type);
		addToNode(screenNode, "frequency", frequency);
		addToNode(screenNode, "frequence_measure", frequencyMeasure);
		addToNode(screenNode, "brightless", brightless);

		addToResource("screen", resource, screenNode, mapper);
	}

	private void addToNode(ObjectNode node, String key, String value) {
		if (value != null) {
			node.put(key, value);
		}
	}

	private void addToNode(ObjectNode node, String key, Boolean value) {
		if (value != null) {
			node.put(key, value);
		}
	}

	private Boolean toBoolean(String value) {
		if (value != null) {
			return value.equals("Да") ? true : false;
		}
		return null;
	}

	private void addProcessorNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String value = getPropertyById(PROCESSOR_FREQUENCY_ID, "value");
		String cores = getPropertyById(PROCESSOR_CORES_ID, "value");

		ObjectNode processorNode = mapper.createObjectNode();
		addToNode(processorNode, "value", value);
		addToNode(processorNode, "cores", cores);

		addToResource("processor", resource, processorNode, mapper);
	}

	private void addRamNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String value = getPropertyById(RAM_ID, "value");
		String measure = getPropertyById(RAM_ID, "measure");

		ObjectNode ramNode = mapper.createObjectNode();
		addToNode(ramNode, "value", value);
		addToNode(ramNode, "measure", MemoryMeasure.getMemoryMeasureString(measure));

		addToResource("ram", resource, ramNode, mapper);
	}

	private void addRomNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String value = getPropertyById(ROM_ID, "value");
		String measure = getPropertyById(ROM_ID, "measure");

		ObjectNode romNode = mapper.createObjectNode();
		addToNode(romNode, "value", value);
		addToNode(romNode, "measure", MemoryMeasure.getMemoryMeasureString(measure));

		addToResource("rom", resource, romNode, mapper);
	}

	private void addBackCameraNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String mpicsStr = getPropertyById(CAMERA_MPICS_ID, "value");
		String mpics = mpicsStr != null ? String.join(", ", mpicsStr.split("/")) : null;
		String count = getPropertyById(CAMERA_NUMBER_ID, "value");
		String resolution = getPropertyById(CAMERA_RESOLUTION_ID, "value");
		String zoom = getPropertyById(CAMERA_ZOOM_ID, "value");
		String flash = getPropertyById(CAMERA_FLASH_ID, "value");

		ObjectNode backCameraNode = mapper.createObjectNode();
		addToNode(backCameraNode, "mpics", mpics);
		addToNode(backCameraNode, "count", count);
		addToNode(backCameraNode, "resolution",
				resolution != null ? resolution.substring(0, resolution.indexOf(" ")) : null);
		addToNode(backCameraNode, "zoom", zoom);
		addToNode(backCameraNode, "flash", toBoolean(flash));

		addToResource("back_camera", resource, backCameraNode, mapper);
	}

	private void addFrontCameraNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String mpicsStr = getPropertyById(FRONT_CAMERA_MPICS_ID, "value");
		String mpics = mpicsStr != null ? String.join(", ", mpicsStr.split("/")) : null;
		String count = getPropertyById(FRONT_CAMERA_NUMBER_ID, "value");

		ObjectNode frontCameraNode = mapper.createObjectNode();
		addToNode(frontCameraNode, "mpics", mpics);
		addToNode(frontCameraNode, "count", count);

		addToResource("front_camera", resource, frontCameraNode, mapper);
	}

	private void addSDCardNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String sdMax = getPropertyById(SD_MAX_ID, "value");
		String measure = MemoryMeasure.getMemoryMeasureString(getPropertyById(SD_MAX_ID, "measure"));
		String type = getPropertyById(SD_TYPE_ID, "value");

		ObjectNode sdNode = mapper.createObjectNode();
		addToNode(sdNode, "max", sdMax);
		addToNode(sdNode, "measure", measure);
		addToNode(sdNode, "type", type);

		addToResource("sd", resource, sdNode, mapper);
	}

	private void addSimNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String sim = getPropertyById(SIM_ID, "value");

		resource.addAttribute("sim", sim);
	}

	private void addWirelessNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String mobileWireless = getPropertyById(MOBILE_WIRELESS_ID, "value");
		Boolean isSupport5G = toBoolean(getPropertyById(MOBILE_5G_ID, "value"));
		String wifi = getPropertyById(WIFI_ID, "value");
		Boolean isSupportNFC = toBoolean(getPropertyById(NFC_ID, "value"));
		Boolean isSupportMiracast = toBoolean(getPropertyById(MIRACAST_ID, "value"));
		String bluetooth = getPropertyById(BLUETOOTH_ID, "value");
		Boolean isSupportGPS = toBoolean(getPropertyById(GPS_ID, "value"));
		Boolean isSupportGlonass = toBoolean(getPropertyById(GLONASS_ID, "value"));

		ObjectNode wirelessNode = mapper.createObjectNode();
		addToNode(wirelessNode, "mobile_wireless", mobileWireless);
		addToNode(wirelessNode, "5g_support", isSupport5G);
		addToNode(wirelessNode, "wifi", wifi);
		addToNode(wirelessNode, "nfc_support", isSupportNFC);
		addToNode(wirelessNode, "miracast_support", isSupportMiracast);
		addToNode(wirelessNode, "bluetooth", bluetooth);
		addToNode(wirelessNode, "gps_support", isSupportGPS);
		addToNode(wirelessNode, "glonass_support", isSupportGlonass);

		addToResource("wireless", resource, wirelessNode, mapper);
	}

	private void addSecurityNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		Boolean isSupportTouchId = toBoolean(getPropertyById(TOUCH_ID, "value"));
		Boolean isSupportFaceId = toBoolean(getPropertyById(FACE_ID, "value"));

		ObjectNode securityNode = mapper.createObjectNode();
		addToNode(securityNode, "touch_id", isSupportTouchId);
		addToNode(securityNode, "face_id", isSupportFaceId);

		addToResource("security", resource, securityNode, mapper);
	}

	private void addInterfacesNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String connectionInterface = getPropertyById(INTERFACE_ID, "value");
		String headphones = getPropertyById(HEADPHONES_ID, "value");

		ObjectNode interfaceNode = mapper.createObjectNode();
		addToNode(interfaceNode, "connection", connectionInterface);
		addToNode(interfaceNode, "headphones", headphones);

		addToResource("interface", resource, interfaceNode, mapper);
	}

	private void addMaterialNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String material = getPropertyById(MATERIAL_ID, "value");

		resource.addAttribute("material", material);
	}

	private void addBatteryNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String type = getPropertyById(BATTERY_TYPE_ID, "value");
		String capacity = getPropertyById(BATTERY_CAPACITY_ID, "value");
		Boolean isSupportFastCharge = toBoolean(getPropertyById(BATTERY_FAST_CHARGE_ID, "value"));

		ObjectNode batteryNode = mapper.createObjectNode();
		addToNode(batteryNode, "type", type);
		addToNode(batteryNode, "capacity", capacity);
		addToNode(batteryNode, "fast_charge_support", isSupportFastCharge);

		addToResource("battery", resource, batteryNode, mapper);
	}

	private void addAppearanceNode(Resource resource, JsonNode node, ObjectMapper mapper) {
		String color = getPropertyById(COLOR_ID, "value");

		String weight = getPropertyById(WEIGHT_ID, "value");
		String measure = WeightMeasure.getWeightMeasureString(getPropertyById(WEIGHT_ID, "measure"));
		String dimensions = getPropertyById(DIMENSIONS_ID, "value");

		ObjectNode appearanceNode = mapper.createObjectNode();
		addToNode(appearanceNode, "color", color);
		addToNode(appearanceNode, "weight", weight);
		addToNode(appearanceNode, "measure", measure);
		addToNode(appearanceNode, "dimensions", dimensions);

		addToResource("appearance", resource, appearanceNode, mapper);
	}

	private void addToResource(String name, Resource resource, ObjectNode node, ObjectMapper mapper) {
		try {
			resource.addAttribute(name, mapper.writeValueAsString(node));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format(
					"Exception occurred while transformating json to string. Couldn't transform mvideo '%s' json data to string",
					name), e);
		}
	}

	public class Tt extends Thread {
		private DevTools d;
		private RequestId[] rid;

		public Tt(DevTools db, RequestId[] i) {
			d = db;
			rid = i;
		}

		@Override
		public void run() {
			d.addListener(Network.responseReceived(), responseReceived -> {
				rid[0] = responseReceived.getRequestId();
				String url = responseReceived.getResponse().getUrl();
				int status = responseReceived.getResponse().getStatus();
				String type = responseReceived.getType().toJson();
				String headers = responseReceived.getResponse().getHeaders().toString();
				System.out.println(url);
				if (url.equals("https://www.mvideo.ru/bff/product-details/list")) {
					String responseBody = d.send(Network.getResponseBody(rid[0])).getBody();
//					System.out.println("yes");
					System.out.println(responseBody);
//					System.out.println(System.currentTimeMillis() - a);
//					webDriver.quit();
				}
			});
		}

	}

	private class IsFoundWrap {
		private boolean isFound;
	}

	@Override
	public List<Resource> getResources(String link, int page) {
		link += "?page=" + page;

		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-blink-features=AutomationControlled", "--headless",
				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False",
				"devtools.jsonview.enabled=false");
		long a = System.currentTimeMillis();
		ChromeDriver webDriver = new ChromeDriver(options);
		DevTools devTools = webDriver.getDevTools();
		devTools.createSession();
		devTools.send(Network.clearBrowserCache());
		devTools.send(Network.setCacheDisabled(true));

		IsFoundWrap wrap = new IsFoundWrap();
		StringBuilder builder = new StringBuilder();

		final RequestId[] requestIds = new RequestId[1];
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.of(100000000)));
		devTools.addListener(Network.responseReceived(), responseReceived -> {
			requestIds[0] = responseReceived.getRequestId();
			String url = responseReceived.getResponse().getUrl();
			if (url.equals("https://www.mvideo.ru/bff/product-details/list")) {
				builder.append(devTools.send(Network.getResponseBody(requestIds[0])).getBody());
				wrap.isFound = true;
				webDriver.quit();
			}
		});
		webDriver.get(link);

		WebDriverWait wdw = new WebDriverWait(webDriver, Duration.ofSeconds(15));
		wdw.until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return wrap.isFound;
			}
		});

		String d = builder.toString();
		JsonNode productsNode;
		try {
			productsNode = mapper.readTree(builder.toString());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format(
					"Exception occurred while transforming json string products to json node. Couldn't get data from:\n'%s'",
					builder.toString()), e);
		}
		ArrayNode productNodes = (ArrayNode) productsNode.findValue("products");
		List<Resource> resources = new ArrayList<>();

		productNodes.forEach(productNode -> {
			attributes = productNode.findParents("id");

			String nameTransit = productNode.get("nameTranslit").asText();
			String id = productNode.get("productId").asText();
			String productLink = HOME_LINK + "/products/" + nameTransit + "-" + id;
			String name = productNode.get("name").asText();

			Resource resource = new Resource(id, productLink, name);

			parseCharacteristics(productNode, resource);

			resources.add(resource);
		});
		
		System.out.println(System.currentTimeMillis() - a);

		return resources;

//		cur = System.currentTimeMillis();
//		WebClient webClient = getWebClient();
//		int offset = (page - 1) * 24;
//
//		link += "?categoryId=" + 205 + "&offset=" + offset + "&limit=24";
//
//		webClient.addRequestHeader("accept", "application/json");
//		System.out.println(System.currentTimeMillis() - cur);
//		String content;
//		try {
//			content = webClient.getPage(link).getWebResponse().getContentAsString();
//		} catch (IOException e) {
//			throw new RuntimeException(String.format(
//					"Exception occurred while getting resources by link '%s'. Couldn't get response body", link), e);
//		}
//
//		List<String> productIds = getProductIds(content);
//		List<JsonNode> productNodes = getProducts(productIds, webClient);
//
//		List<Resource> resources = new ArrayList<>();
//		productNodes.forEach(productNode -> {
//			String nameTransit = productNode.get("nameTransit").asText();
//			String id = productNode.get("id").asText();
//			String productLink = HOME_LINK + "/products/" + nameTransit + "-" + id;
//
//			resources.add(parseContent(productNode, productLink));
//		});
//		return resources;

	}

	private List<String> getProductIds(String content) {
		JsonNode node;
		try {
			node = mapper.readTree(content);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format(
					"Exception occurred while parsing product ids response body. Couldn't get data of:\n'%s'", content),
					e);
		}
		List<String> productIds = transformData(node.findValue("products"));

		return productIds;
	}

	private List<JsonNode> getProducts(List<String> ids, WebClient webClient) {
		ObjectNode node = mapper.createObjectNode();
		node.put("brand", true);
		node.put("category", true);
		node.putArray("mediaTypes").add("images");
		node.put("multioffer", false);

		ArrayNode productIdsNode = node.putArray("productIds");
		ids.forEach(id -> productIdsNode.add(id));

		node.putObject("propertiesConfig").put("propertiesPortionSize", 5);
		node.putArray("propertyTypes").add("KEY");
		node.put("status", true);

		String link = "https://www.mvideo.ru/bff/product-details/list";
		ChromeOptions options = new ChromeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NONE);
		options.addArguments("disable-blink-features=AutomationControlled", "headless",
				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False");
		long a = System.currentTimeMillis();

		WebDriver webDriver = new ChromeDriver(options);
		webDriver.get("https://www.mvideo.ru/bff/product-details/list");

		WebRequest request;
		try {
			request = new WebRequest(new URL(link), HttpMethod.POST);
			request.setAdditionalHeader("authority", "www.mvideo.ru");
			request.setAdditionalHeader("method", "POST");
			request.setAdditionalHeader("path", "/bff/product-details/list");
			request.setAdditionalHeader("scheme", "https");
			request.setAdditionalHeader("accept", "application/json");
			request.setAdditionalHeader("baggage",
					"sentry-transaction=/,sentry-public_key=1e9efdeb57cf4127af3f903ec9db1466,sentry-trace_id=c0dbc393acf649378829d8e49be0a8fd,sentry-sample_rate=0.5");
			request.setAdditionalHeader("sentry-trace", "c0dbc393acf649378829d8e49be0a8fd-bb90b3ad9a59a350-1");
			request.setAdditionalHeader("content-type", "application/json");
			request.setAdditionalHeader("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
		} catch (MalformedURLException e) {
			throw new RuntimeException("Exception occurred while getting products from ");
		}
		try {
			String body = mapper.writeValueAsString(node);
			request.setRequestBody(body);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Exception occurred while transforming json node to string", e);
		}

		String content;
		try {
			content = getWebClient().getPage(request).getWebResponse().getContentAsString();
		} catch (IOException e) {
			throw new RuntimeException(String.format("Exception occurred while getting page by url '%s'", link), e);
		}

		JsonNode productsNode;
		try {
			productsNode = mapper.readTree(content);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format(
					"Exception occurred while transforming json string products to json node. Couldn't get data from:\n'%s'",
					content), e);
		}
		return productsNode.findValues("products");
	}

//	@Override
//	public Resource getResource(String link) {
//		ChromeOptions options = new ChromeOptions();
//		options.setPageLoadStrategy(PageLoadStrategy.NONE);
//		options.addArguments("disable-blink-features=AutomationControlled", "headless",
//				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
//				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False");
//		
//		long a = System.currentTimeMillis();
//		WebDriver webDriver = new ChromeDriver(options);
//		webDriver.get("https://www.mvideo.ru/");
//		/*
//		 * try { synchronized (webDriver) {
//		 * 
//		 * webDriver.wait(1000); } } catch (InterruptedException e) { // TODO
//		 * Auto-generated catch block e.printStackTrace(); }
//		 */
//		webDriver.navigate().to(link);
//		/*
//		 * try { synchronized (webDriver) {
//		 * 
//		 * webDriver.wait(1000); } } catch (InterruptedException e) { // TODO
//		 * Auto-generated catch block e.printStackTrace(); }
//		 */
//		long b =System.currentTimeMillis();
//		System.out.println(b - a);
//		String c = webDriver.getPageSource();
//		System.out.println(c);
//		return null;
//	}

	@Override
	public List<Review> getReviews(String productId) {
		String link = "https://www.mvideo.ru/bff/reviews/product?productId=" + productId;
		WebClient webClient = getWebClient();

		String content;
		try {
			content = webClient.getPage(link).getWebResponse().getContentAsString();
		} catch (IOException e) {
			throw new RuntimeException(String
					.format("Exception occurred while getting json reviews. Couldn't get data by url: '%s'", link), e);
		}
		JsonNode node;
		try {
			node = mapper.readTree(content);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format(
					"Exception occurred while transforming string json to json object. Couldn't transform:\n%s",
					content), e);
		}
		ArrayNode reviewNodes = (ArrayNode) node.findValue("reviews");

		List<Review> reviews = new ArrayList<>();
		reviewNodes.forEach(reviewNode -> {
			String advantages = reviewNode.get("benefits").asText();
			String disadvantages = reviewNode.get("drawbacks").asText();
			String sender = reviewNode.get("name").asText();
			String text = reviewNode.get("text").asText();
			String dateStr = reviewNode.get("date").asText();
			List<String> imageLinks = transformData(reviewNode.get("photos"));
			Integer likesCount = reviewNode.get("like").asInt();
			Integer dislikesCount = reviewNode.get("dislike").asInt();

			String format = "yyyy-mm-dd";
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date;
			try {
				date = sdf.parse(reviewNode.get("date").asText());
			} catch (ParseException e) {
				throw new RuntimeException(
						String.format("Exception occurred while parsing date. Couldn't parse date '%s' by format '%s'",
								dateStr, format),
						e);
			}

			Review review = new Review(sender, text, date);
			review.setAdvantages(advantages);
			review.setDisadvantages(disadvantages);
			review.addImageLinks(imageLinks);
			review.setLikesCount(likesCount);
			review.setDislikesCount(dislikesCount);

			reviews.add(review);
		});

		return reviews;
	}

	private <T> T transformData(JsonNode node) {
		try {
			T t = mapper.readValue(node.toPrettyString(), new TypeReference<T>() {
			});
			return t;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(
					String.format("Exception occurred while reading data from json. Couldn't transform:\n'%s'",
							node.toPrettyString()),
					e);
		}
	}
}
