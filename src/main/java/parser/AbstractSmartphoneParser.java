package parser;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractSmartphoneParser implements Parser {
	private final String SERIES_ID = "46";
	private final String MODEL_ID = "752";
	private final String OS_ID = "30851850";

	private final String SCREEN_RESOLUTION_ID = "30848201";
	private final String SCREEN_TECHNOLOGY_ID = "30852907";
	private final String SCREEN_FREQUENCY_ID = "2119";
	private final String SCREEN_BRIGHTLESS_ID = "2055";

	private final String PROCESSOR_FREQUENCY_ID = "619180";
	private final String PROCESSOR_CORES_ID = "30852972";

	private final String RAM_ID = "30852805";

	private final String ROM_ID = "613463";

	private final String CAMERA_MPICS_ID = "30848204";
	private final String CAMERA_NUMBER_ID = "30848501";
	private final String CAMERA_RESOLUTION_ID = "30852911";
	private final String CAMERA_ZOOM_ID = "30852957";
	private final String CAMERA_FLASH_ID = "30852865";

	private final String FRONT_CAMERA_NUMBER_ID = "30852914";
	private final String FRONT_CAMERA_MPICS_ID = "30852958";

	private final String SD_MAX_ID = "601934";
	private final String SD_TYPE_ID = "332";

	private final String SIM_ID = "30852858";

	private final String MOBILE_WIRELESS_ID = "30852925";
	private final String MOBILE_5G_ID = "30853313";
	private final String WIFI_ID = "210";
	private final String NFC_ID = "153";
	private final String MIRACAST_ID = "677";
	private final String BLUETOOTH_ID = "135";
	private final String GPS_ID = "4629";
	private final String GLONASS_ID = "2380";

	private final String TOUCH_ID = "30852774";
	private final String FACE_ID = "8537";

	private final String INTERFACE_ID = "263";
	private final String HEADPHONES_ID = "2335";

	private final String MATERIAL_ID = "83";

	private final String BATTERY_TYPE_ID = "267";
	private final String BATTERY_CAPACITY_ID = "11908";
	private final String BATTERY_FAST_CHARGE_ID = "30852685";

	private final String COLOR_ID = "30852685";
	private final String WEIGHT_ID = "11929";
	private final String DIMENSIONS_ID = "30852910";

	private final ObjectMapper mapper = new ObjectMapper();

	private List<JsonNode> attributes;

	public AbstractSmartphoneParser() {
		
	}
	
	
}
