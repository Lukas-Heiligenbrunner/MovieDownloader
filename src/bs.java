import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class bs {
    public void capturelinks(String hdstreamslink) {
        System.setProperty("webdriver.gecko.driver", "./geckodriver");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "logs.txt");

        FirefoxBinary firefoxBinary = new FirefoxBinary();
        //firefoxBinary.addCommandLineOptions("--headless");

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.cache.disk.enable", false);
        profile.setPreference("browser.cache.memory.enable", false);
        profile.setPreference("browser.cache.offline.enable", false);
        profile.setPreference("network.http.use-cache", false);
//        profile.setPreference("network.cookie.cookieBehavior", 2);

        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(profile);
        options.setBinary(firefoxBinary);

        WebDriver drv = new FirefoxDriver(options);
        drv.manage().deleteAllCookies();
        drv.get(hdstreamslink);
        System.out.println("opened webpage");
        String mainwindowhandle = drv.getWindowHandle();
        System.out.println(mainwindowhandle);

        WebDriverWait wait = new WebDriverWait(drv, 50);

        wait.until(webDriver -> ((JavascriptExecutor) drv).executeScript("return document.readyState").equals("complete"));
        System.out.println("finished loading page");

        ArrayList<String> links = new ArrayList<>();

        WebElement el = drv.findElement(By.className("episodes"));
        List<WebElement> els = el.findElements(By.cssSelector("a"));
        for (WebElement ell : els){
            String link = ell.getAttribute("href");
            if (link.contains("OpenLoadHD")){
                links.add(link);
                System.out.println(link);
            }

        }

        drv.get(links.get(0));
        drv.findElement(By.className("play")).click();

        WebDriverWait waitme = new WebDriverWait(drv, 50);
        waitme.until(webDriver -> webDriver.findElement(By.id("embededVideo")));
        WebElement videoframe = drv.findElement(By.id("embededVideo"));
        System.out.println(videoframe.getAttribute("src"));

        drv.quit();

    }
}
