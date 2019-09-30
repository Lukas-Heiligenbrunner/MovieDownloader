import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ILoad {

    public void capturelinks(String hdstreamslink, int seasonoffset) {
        System.setProperty("webdriver.gecko.driver", "./geckodriver");
        System.setProperty("webdriver.chrome.driver", "./chromedriver");

        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "logs.txt");

        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");

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
        String mainwindowhandle = drv.getWindowHandle();
        System.out.println(mainwindowhandle);



        ArrayList<String> seasonlinks = new ArrayList<>();

        WebDriverWait wait = new WebDriverWait(drv, 50);


        // getting seasons first
        drv.get(hdstreamslink);
        wait.until(webDriver -> ((JavascriptExecutor) drv).executeScript("return document.readyState").equals("complete"));
        System.out.println("finished loading page");

        List<WebElement> myseasons = drv.findElements(By.className("list-name"));

        System.out.println("found " + myseasons.size() + " seasons");

        for (WebElement elem: myseasons) {
            String link = elem.findElement(By.tagName("a")).getAttribute("href");
            seasonlinks.add(link);
            System.out.println(link);
        }

        //finished grabbing seasons
        int currseason = 0;

        for (String seasonlink:seasonlinks) {
            currseason++;
            if (currseason < seasonoffset){
                continue;
            }
            drv.get(seasonlink);
            System.out.println("getting season" + currseason);

            wait.until(webDriver -> ((JavascriptExecutor) drv).executeScript("return document.readyState").equals("complete"));
            System.out.println("finished loading page");

            List<WebElement> myepisodes = drv.findElements(By.className("list-name"));
            ArrayList<String> episodelinks = new ArrayList<>();

            System.out.println("found "+ myepisodes.size() + " episodes in season");

            for (WebElement elem:myepisodes) {
                String episodelink = elem.findElement(By.tagName("a")).getAttribute("href");

                episodelinks.add(episodelink);
            }

            ArrayList<String> links = new ArrayList<>();

            for (String episodelink:episodelinks) {

                drv.get(episodelink);
                wait.until(webDriver -> ((JavascriptExecutor) drv).executeScript("return document.readyState").equals("complete"));
                System.out.println("finished loading page");

                drv.findElement(By.className("ddl-mirror-box-stream")).click();

                for (String hndl : drv.getWindowHandles()) {
                    drv.switchTo().window(hndl);
                    wait.until(webDriver -> ((JavascriptExecutor) drv).executeScript("return document.readyState").equals("complete"));
                    String url = drv.getCurrentUrl();
                    if (url.contains("openload.co")) {
                        links.add(url);
                    }
                }

                Set<String> set = drv.getWindowHandles();

                set.remove(mainwindowhandle);
                assert set.size() == 1;
                for (String hndl : set) {
                    drv.switchTo().window(hndl);
                    drv.close();
                }


                drv.switchTo().window(mainwindowhandle);

            }

            System.out.println("links of season: " + currseason);
            for (String currlink:links) {
                System.out.println(currlink);
            }

        }
    }
}
