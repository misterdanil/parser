import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Test4 {
	private static WebDriver webDriver;
	static {
		ChromeOptions options = new ChromeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NONE);
		options.addArguments("disable-blink-features=AutomationControlled", "headless", "--remote-allow-origins=*",
				"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
				"start-maximized", "excludeSwitches=enable-automation", "useAutomationExtension=False");
		long a = System.currentTimeMillis();
		Wrap wrap = new Wrap();

		webDriver = new ChromeDriver(options);
		webDriver.navigate().to("https://www.eldorado.ru/c/smartfony");
	}

	public static void main(String[] args) {
		Wrap wrap = new Wrap();

		webDriver.navigate().to("https://www.eldorado.ru/c/smartfony?page=1");
		WebDriverWait wdw = new WebDriverWait(webDriver, Duration.ofSeconds(10));
		wdw.until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				List<WebElement> head = driver.findElements(By.tagName("head"));
				if (head.size() > 0) {
					String text = head.get(0).findElement(By.tagName("title")).getAttribute("textContent");
					if (text.equals("Access Blocked")) {
						wrap.result = Result.BLOCKED;
					}
				}
				List<WebElement> nextData = webDriver.findElements(By.id("__NEXT_DATA__"));
				if (nextData.size() > 0) {
					wrap.result = Result.OK;
				}
				return wrap.result != null;
			}
		});

		if (wrap.result == Result.BLOCKED) {
			main(args);
		} else if (wrap.result == Result.OK) {
			System.out.println("yes");
		}
	}

	private static class Wrap {
		private Result result;
	}

	private static enum Result {
		OK, BLOCKED;
	}
}
