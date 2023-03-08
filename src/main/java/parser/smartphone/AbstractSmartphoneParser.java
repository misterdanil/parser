package parser.smartphone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import model.Resource;
import parser.Parser;

public abstract class AbstractSmartphoneParser implements Parser {
	protected String SERIES_ID;
	protected String MODEL_ID;
	protected String OS_ID;
	
	protected String SCREEN_DIAGONAL_ID;
	protected String SCREEN_RESOLUTION_ID;
	protected String SCREEN_TECHNOLOGY_ID;
	protected String SCREEN_FREQUENCY_ID;
	protected String SCREEN_BRIGHTLESS_ID;

	protected String PROCESSOR_FREQUENCY_ID;
	protected String PROCESSOR_CORES_ID;

	protected String RAM_ID;

	protected String ROM_ID;

	protected String CAMERA_MPICS_ID;
	protected String CAMERA_NUMBER_ID;
	protected String CAMERA_RESOLUTION_ID;
	protected String CAMERA_ZOOM_ID;
	protected String CAMERA_FLASH_ID;

	protected String FRONT_CAMERA_NUMBER_ID;
	protected String FRONT_CAMERA_MPICS_ID;

	protected String SD_MAX_ID;
	protected String SD_TYPE_ID;

	protected String SIM_ID;

	protected String MOBILE_WIRELESS_ID;
	protected String MOBILE_5G_ID;
	protected String WIFI_ID;
	protected String NFC_ID;
	protected String MIRACAST_ID;
	protected String BLUETOOTH_ID;
	protected String GPS_ID;
	protected String GLONASS_ID;

	protected String TOUCH_ID;
	protected String FACE_ID;

	protected String INTERFACE_ID;
	protected String HEADPHONES_ID;

	protected String MATERIAL_ID;

	protected String BATTERY_TYPE_ID;
	protected String BATTERY_CAPACITY_ID;
	protected String BATTERY_FAST_CHARGE_ID;

	protected String COLOR_ID;
	protected String WEIGHT_ID;
	protected String DIMENSIONS_ID;

	private ObjectMapper mapper = new ObjectMapper();

	public abstract String getSeries(Object node);

	public abstract String getBrand(Object node);

	public abstract String getOS(Object node);

	public abstract ObjectNode getScreenNode(Object node);

	protected void addToNode(ObjectNode node, String key, String value) {
		if (value != null) {
			node.put(key, value);
		}
	}

	protected void addToNode(ObjectNode node, String key, Boolean value) {
		if (value != null) {
			node.put(key, value);
		}
	}
	protected void addToResource(String name, Resource resource, ObjectNode node, ObjectMapper mapper) {
		try {
			resource.addAttribute(name, mapper.writeValueAsString(node));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format(
					"Exception occurred while transformating json to string. Couldn't transform mvideo '%s' json data to string",
					name), e);
		}
	}

	protected Boolean toBoolean(String value) {
		if (value != null) {
			return value.equals("Да") ? true : false;
		}
		return null;
	}

	protected <T> T transformData(JsonNode node) {
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

	public abstract ObjectNode getProcessorNode(Object node);

	public abstract ObjectNode getRamNode(Object node);

	public abstract ObjectNode getRomNode(Object node);

	public abstract ObjectNode getBackCameraNode(Object node);

	public abstract ObjectNode getFrontCameraNode(Object node);

	public abstract ObjectNode getSDCardNode(Object node);

	public abstract ObjectNode getSimNode(Object node);

	public abstract ObjectNode getWirelessNode(Object node);

	public abstract ObjectNode getSecurityNode(Object node);

	public abstract ObjectNode getInterfacesNode(Object node);

	public abstract ObjectNode getMaterialNode(Object node);

	public abstract ObjectNode getBatteryNode(Object node);

	public abstract ObjectNode getAppearanceNode(Object node);

	public ObjectMapper getMapper() {
		return mapper;
	}
}
