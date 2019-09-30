import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;

public class HDStreamsMovieCapture {
    public void captureMovieLink(){

        ArrayList<String> links = new ArrayList<String>(Arrays.asList(
                "https://hd-streams.org/movies/alita-battle-angel-2019"));


        System.setProperty("webdriver.gecko.driver", "./geckodriver");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "logs.txt");

        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");

        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(firefoxBinary);

        WebDriver drv = new FirefoxDriver(options);

        ArrayList<String> oloadlinks = new ArrayList<>();
        for (String link : links){
            drv.get(link);
            System.out.println("opened webpage");
            String mainwindowhandle = drv.getWindowHandle();
            System.out.println(mainwindowhandle);

            WebDriverWait wait = new WebDriverWait(drv, 50);

            wait.until(webDriver -> webDriver.findElement(By.id("app")));
            System.out.println("awaited load of new page");
            wait.until(webDriver -> ((JavascriptExecutor) drv).executeScript("return document.readyState").equals("complete"));
            System.out.println("finished loading page");

            drv.findElement(By.cssSelector(".movie-cover")).click();

            WebDriverWait waiter = new WebDriverWait(drv, 120);
            try{
                waiter.until(webDriver -> webDriver.findElement(By.id("embededVideo")));
            }catch (WebDriverException e){
                System.out.println("there sems to be a captcha on site");
            }

            WebElement videoframe = drv.findElement(By.id("embededVideo"));
            oloadlinks.add(videoframe.getAttribute("src"));
            System.out.println(oloadlinks.get(oloadlinks.size()-1));
        }


        drv.close();

        for (String link : oloadlinks) {
            System.out.println(link);
        }
    }

}
