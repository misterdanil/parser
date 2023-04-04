import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.devtools.v109.network.model.RequestId;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class Test3 {
	public static void main(String[] args) throws InterruptedException, IOException {

		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-blink-features=AutomationControlled", "--remote-allow-origins=*",
				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False");
		long a = System.currentTimeMillis();
		ChromeDriver webDriver = new ChromeDriver(options);

		webDriver.get("https://www.eldorado.ru/cat/detail/smartfon-samsung-galaxy-a13-4-128gb-black-sm-a137f-dsn/");
//		String j = (String) webDriver.executeScript(
//				"const response = await fetch('https://www.mvideo.ru/bff/product-details/list', { method: 'POST',headers: {'Accept': 'application/json', 'Content-Type': 'application/json'},body:JSON.stringify({\"productIds\":[\"30064210\",\"30064911\",\"30067224\",\"30064946\",\"30067205\",\"30064934\",\"30065329\",\"30063274\",\"30065558\",\"30064918\",\"30063236\",\"30064941\",\"30065546\",\"30066493\",\"30065562\",\"30066554\",\"30065350\",\"30064939\",\"30066440\",\"30064909\",\"30065456\",\"30067223\",\"30063312\",\"30064407\"],\"mediaTypes\":[\"images\"],\"category\":true,\"status\":true,\"brand\":true,\"propertyTypes\":[\"ALL\"],\"propertiesConfig\":{\"propertiesPortionSize\":70},\"multioffer\":false}) }); const json = await response.json(); console.log(JSON.stringify(json)); return JSON.stringify(json);");
//		System.out.println(j);
		Thread.sleep(5000);
	}
}
