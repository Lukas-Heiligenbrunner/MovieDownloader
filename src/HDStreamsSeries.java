import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class HDStreamsSeries {
    public void capturelinks(String hdstreamslink) {
        System.setProperty("webdriver.gecko.driver", "./geckodriver");
        System.setProperty("webdriver.chrome.driver","./chromedriver");

        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "logs.txt");

        FirefoxBinary firefoxBinary = new FirefoxBinary();
       //firefoxBinary.addCommandLineOptions("--headless");

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.cache.disk.enable", false);
        profile.setPreference("browser.cache.memory.enable", false);
        profile.setPreference("browser.cache.offline.enable", false);
        profile.setPreference("network.http.use-cache", false);
        profile.addExtension(new File("{e58d3966-3d76-4cd9-8552-1582fbc800c1}.xpi"));
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

        wait.until(webDriver -> webDriver.findElement(By.id("app")));
        System.out.println("awaited load of new page");
        wait.until(webDriver -> ((JavascriptExecutor) drv).executeScript("return document.readyState").equals("complete"));
        System.out.println("finished loading page");

//        Set<Cookie> allCookies = drv.manage().getCookies();
//        for (Cookie cookie : allCookies) {
//            System.out.println(cookie.getName());
//        }
        ((JavascriptExecutor)drv).executeScript("window.localStorage.clear();");

        drv.findElement(By.className("episode-play")).click();
        System.out.println("clicked element");


        ArrayList<String> links = new ArrayList<>();
        try{
            links.add(captureLink(drv));
            System.out.println(links.get(0));
        }catch (WebDriverException e){
            System.out.println("there sems to be a captcha on site --> please solve the captcha");
            drv.switchTo().frame(drv.findElements(By.tagName("iframe")).get(1));
            drv.findElement(By.id("solver-button")).click();
            System.out.println("clicked solving button");
            WebDriverWait waiter = new WebDriverWait(drv,120);
            waiter.until(webDriver -> webDriver.findElement(By.id("embededVideo")));
        }


        try {

            while (true) {
                clickNextButton(drv);
                closePopUpWindows(drv, mainwindowhandle);
                String link = captureLink(drv);
                links.add(link);
                System.out.println(link);
            }
        } catch (UnhandledAlertException e) {
            System.out.println("season finished...");
        }

        System.out.println("finished captureing! all links above:");

        for (String link : links) {
            System.out.println(link);
        }

        drv.quit();

    }

    private void closePopUpWindows(WebDriver drv, String mainwindowhandle) {
        if (drv.getWindowHandles().size() > 1) {
            System.out.println("popup closed");
            for (String s : drv.getWindowHandles()) {
                if (!s.equals(mainwindowhandle)) {
                    drv.switchTo().window(s);
                    drv.close();
                }
            }
            drv.switchTo().window(mainwindowhandle);
            clickNextButton(drv);
        }
    }

    private void clickNextButton(WebDriver drv) {
        WebElement nextbutton = drv.findElement(By.className("next"));
        Actions actions = new Actions(drv);
        actions.moveToElement(nextbutton).click().build().perform();
    }

    private String captureLink(WebDriver drv) {
        WebDriverWait wait = new WebDriverWait(drv, 20);
        wait.until(webDriver -> webDriver.findElement(By.id("embededVideo")));
        WebElement videoframe = drv.findElement(By.id("embededVideo"));
        return videoframe.getAttribute("src");
    }
}
