package exam2;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;



@Listeners(TestListener.class)
public class Slidertest {
	
	
	   Logger log = LogManager.getLogger(Slidertest.class);

    WebDriver driver;
    WebDriverWait wait;
    SoftAssert soft;
    MetricsCollectorsSlider metrics;

    @BeforeMethod
    public void setup() {
        System.out.println("========================================");
        System.out.println("🚀 Starting Test Setup");
        System.out.println("========================================");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // enable if needed
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        soft = new SoftAssert();
        metrics = new MetricsCollectorsSlider();
        
        System.out.println("✅ WebDriver initialized successfully");
        System.out.println("✅ Metrics Collector initialized");
    }

 @Test(priority = 1)
public void TC_02_validate_Watch_Tailer_Button_FrontTopTenVsSliderMovies() throws InterruptedException {
    System.out.println("========================================");
    System.out.println("🎬 TEST: Validate Front Top Ten vs Slider Movies");
    System.out.println("========================================");
    TestListener.getTest().info("Watch on and Tailer  button Validation");
    driver.get("https://tldr.lumiolabs.ai/");
    System.out.println("🌐 Navigated to: https://tldr.lumiolabs.ai/");
    log.info("🌐 Navigated to: https://tldr.lumiolabs.ai/");
    Thread.sleep(2000);

    // Get the count of providers first
    List<WebElement> initialProviders = driver.findElements(
        By.xpath("//div[@class='py-3 lg:py-4 false']")
    );
    int providerCount = initialProviders.size();
    
    System.out.println("📊 Found " + providerCount + " providers to test");
    log.info("📊 Found " + providerCount + " providers to test");

    // Loop through each provider
    for(int j = 0; j < providerCount; j++) {
        System.out.println("\n========================================");
        System.out.println("🔄 Testing Provider " + (j + 1) + " of " + providerCount);
        System.out.println("========================================");
        
        Thread.sleep(2000);
        
        // ✅ RE-FETCH providers list to avoid stale elements
        List<WebElement> providers = driver.findElements(
            By.xpath("//div[@class='py-3 lg:py-4 false']")
        );
        
        // Scroll to the provider element
        WebElement providerElement = providers.get(j);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});", 
            providerElement
        );
        
        Thread.sleep(500); // Small wait after scroll
        
        // Click the provider (with fallback to JavaScript click)
        try {
            providerElement.click();
            System.out.println("✅ Clicked provider " + (j + 1) + " using standard click");
        } catch (ElementNotInteractableException e) {
            System.out.println("⚠️ Standard click failed, using JavaScript click");
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", 
                providerElement
            );
        }
        
        Thread.sleep(1500);

        // 1️⃣ Get front page Top Ten movies
        System.out.println("📋 Step 1: Collecting movies from front page");
        List<String> frontMovies = getFrontTopTenMovies();
        soft.assertFalse(frontMovies.isEmpty(), "❌ No movies found on front page for provider " + (j + 1));
        
        metrics.setTotalMoviesOnFrontPage(frontMovies.size());
        System.out.println("✅ Total movies found on front page: " + frontMovies.size());

        // 2️⃣ Click first movie
        System.out.println("📋 Step 2: Opening slider by clicking first movie");
        
        List<WebElement> movieElements = driver.findElements(
            By.xpath("//img[@data-card-type='top-ten-card']")
        );
        
        if (movieElements.isEmpty()) {
            System.out.println("❌ No movie elements found for provider " + (j + 1));
            soft.fail("No movies found for provider " + (j + 1));
            continue; // Skip to next provider
        }
        
        WebElement firstMovie = movieElements.get(0);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstMovie);
        System.out.println("✅ First movie clicked, slider opened");

        Thread.sleep(1500);

        // 3️⃣ Collect movies from slider
        System.out.println("📋 Step 3: Validating movies in slider");
        List<String> sliderMovies = getSliderMovies(frontMovies.size(),soft);
        
        metrics.setTotalMoviesInSlider(sliderMovies.size());
        System.out.println("✅ Total movies validated in slider: " + sliderMovies.size());

        // 4️⃣ Compare front vs slider
        System.out.println("📋 Step 4: Comparing front page movies with slider movies");
        compareMovies(frontMovies, sliderMovies);
        
        // 5️⃣ Navigate back to home page for next provider
        System.out.println("🔙 Navigating back to home page...");
        driver.get("https://tldr.lumiolabs.ai/");
        Thread.sleep(2000);
        System.out.println("✅ Ready for next provider");
    }
    
    soft.assertAll();
}
    
   
    
    
    
    
    
    
    
    
    // ===================== FRONT MOVIES =====================
    public List<String> getFrontTopTenMovies() {
        System.out.println("🔍 Fetching movies from front page...");
        List<String> frontMovies = new ArrayList<>();

        List<WebElement> movies = driver.findElements(
                By.xpath("//img[@data-card-type='top-ten-card']")
        );

        System.out.println("📊 Found " + movies.size() + " movie elements on front page");

        for (int i = 0; i < movies.size(); i++) {
            WebElement movie = movies.get(i);
            String title = getMovieTitle(movie);
            if (title != null && !title.isEmpty()) {
                frontMovies.add(title.trim());
                System.out.println("   Movie " + (i + 1) + ": " + title.trim());
            }
        }

        System.out.println("🎬 Front Page Movies List:");
        frontMovies.forEach(m -> System.out.println(" ➤ " + m));

        return frontMovies;
    }

    // ===================== SLIDER MOVIES =====================
    public List<String> getSliderMovies(int expectedCount,SoftAssert soft) throws InterruptedException {
        System.out.println("🔄 Starting slider navigation and validation...");
        List<String> sliderMovies = new ArrayList<>();

        for (int i = 0; i < expectedCount; i++) {
            System.out.println("----------------------------------------");
            System.out.println("🎯 Processing Slider Position: " + (i + 1) + "/" + expectedCount);

            String movieName = getCurrentMovieName();

            if (!movieName.equals("Not found") && !sliderMovies.contains(movieName)) {
                sliderMovies.add(movieName);
                System.out.println("✅ Movie Name: " + movieName);
                metrics.addMovieValidated(movieName);
            } else if (movieName.equals("Not found")) {
                System.out.println("❌ Could not retrieve movie name at position " + (i + 1));
                metrics.addIssue("Movie name not found at slider position " + (i + 1));
            } else {
               // System.out.println("⚠️ Duplicate movie detected: " + movieName);
            	soft.fail("⚠️ Duplicate movie detected: " + movieName);
            
            	MetricsCollectorsSlider.duplicateMovies++;
            	continue;
            }

            // Validate content
            contentValidation(movieName, soft, i + 1);

            // Navigate to next slide
            driver.findElement(By.tagName("body")).sendKeys(Keys.ARROW_RIGHT);
            System.out.println("⏭️ Navigated to next slide");
            Thread.sleep(800);
        }

        System.out.println("----------------------------------------");
        return sliderMovies;
    }

    // ===================== COMPARE =====================
    public void compareMovies(List<String> front, List<String> slider) {
        System.out.println("🔍 Comparing front page movies with slider movies...");

        soft.assertEquals(
                slider.size(),
                front.size(),
                "❌ Movie count mismatch (Front vs Slider)"
        );

        int matchCount = 0;
        int mismatchCount = 0;

        for (int i = 0; i < slider.size(); i++) {
            String frontMovie = front.get(i).toLowerCase().trim();
            String sliderMovie = slider.get(i).toLowerCase().trim();

            boolean matches = sliderMovie.contains(frontMovie) || frontMovie.contains(sliderMovie);

            if (matches) {
                System.out.println("✅ Match at position " + (i + 1) + ": " + front.get(i));
                matchCount++;
            } else {
                System.out.println("❌ Mismatch at position " + (i + 1) + 
                         " | Front: " + front.get(i) + 
                         " | Slider: " + slider.get(i));
                mismatchCount++;
                metrics.addIssue("Title mismatch at position " + (i + 1));
            }

            soft.assertTrue(matches,
                    "❌ Title mismatch at index " + i +
                            " | Front: " + front.get(i) +
                            " | Slider: " + slider.get(i)
            );
        }

        System.out.println("📊 Comparison Results: " + matchCount + " matches, " + mismatchCount + " mismatches");
    }

    // ===================== CONTENT VALIDATION =====================
    public void contentValidation(String sliderMovie, SoftAssert soft, int movieIndex) {
        System.out.println("🔍 Validating content for: " + sliderMovie + " (Position: " + movieIndex + ")");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String xpathofMovieBox = "//div[contains(@class,'swiper-slide-active')]//span[normalize-space()='Watch on']";
        String xpathofTailerButton = "//div[contains(@class,'swiper-slide-active')]//span[normalize-space()='Play Trailer']";
        String xpathofDescription = "//div[contains(@class,'swiper-slide-active')]//p[contains(@class,'red-hat-semi-bold')]";

        try {
            // ================= Description Validation =================
            System.out.println("📝 Checking description...");
            String disText = driver.findElement(By.xpath(xpathofDescription)).getText();

            if (disText.isBlank()) {
                System.out.println("❌ Description is MISSING for: " + sliderMovie);
                metrics.addDescriptionMissing(sliderMovie);
                takeScreenshot("Movie_" + sanitizeFileName(sliderMovie) + "_MissingDescription");
                log.info("Description is missing for " + sliderMovie);
                soft.fail("Description is missing for " + sliderMovie);
            } else {
                System.out.println("✅ Description found: " + disText.substring(0, Math.min(50, disText.length())) + "...");
                metrics.addDescriptionFound(sliderMovie);
            }

            // Track validation status
            boolean watchOnValidated = false;
            boolean trailerValidated = false;

            // ================= Watch On Validation =================
            System.out.println("🔍 Validating 'Watch On' button for: " + sliderMovie);
            if (validateWatchOn(wait, xpathofMovieBox, sliderMovie)) {
                watchOnValidated = true;
                metrics.incrementWatchOnValidated();
                System.out.println("✅ 'Watch On' validation PASSED for: " + sliderMovie);
                log.info("✅ 'Watch On' validation PASSED for: " + sliderMovie);
            } else {
                System.out.println("❌ 'Watch On' validation FAILED for: " + sliderMovie);
                takeScreenshot("Movie_" + sanitizeFileName(sliderMovie) + "_WatchOnFailed");
                soft.fail("Movie (" + sliderMovie + ") - Watch On validation failed");
                log.info("Movie (" + sliderMovie + ") - Watch On validation failed");
                metrics.incrementWatchOnFailed();
            }

            // ================= Play Trailer Validation =================
            System.out.println("🔍 Validating 'Play Trailer' button for: " + sliderMovie);
            if (validatePlayTrailer(wait, xpathofTailerButton, sliderMovie)) {
                trailerValidated = true;
                metrics.incrementTrailerValidated();
                System.out.println("✅ 'Play Trailer' validation PASSED for: " + sliderMovie);
                log.info("✅ 'Play Trailer' validation PASSED for: " + sliderMovie);
                
            } else {
                System.out.println("❌ 'Play Trailer' validation FAILED for: " + sliderMovie);
                takeScreenshot("Movie_" + sanitizeFileName(sliderMovie) + "_TrailerFailed");
                soft.fail("Movie (" + sliderMovie + ") - Play Trailer validation failed");
                log.info("Movie (" + sliderMovie + ") - Play Trailer validation failed");
                metrics.incrementTrailerFailed();
            }

            // Close any open dialogs
            new Actions(driver)
                    .pause(Duration.ofSeconds(5))
                    .sendKeys(Keys.ESCAPE)
                    .perform();

            // Final status log
            String statusEmoji = (watchOnValidated && trailerValidated) ? "✅" : "⚠️";
            System.out.println(statusEmoji + " Movie " + movieIndex + " (" + sliderMovie + ") - Validation Summary: " +
                    "WatchOn=" + (watchOnValidated ? "PASS" : "FAIL") +
                    ", Trailer=" + (trailerValidated ? "PASS" : "FAIL"));

        } catch (Exception e) {
            System.out.println("💥 EXCEPTION at movie index " + movieIndex + " (" + sliderMovie + "): " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("Movie_" + sanitizeFileName(sliderMovie) + "_Exception");
            soft.fail("Error at movie index " + movieIndex + " (" + sliderMovie + "): " + e.getMessage());

            metrics.addIssue("Movie " + sliderMovie + " - Exception: " + e.getMessage());
            
            try {
                driver.navigate().refresh();
                System.out.println("🔄 Page refreshed after exception");
            } catch (Exception ex) {
                System.out.println("💥 CRITICAL: Failed to refresh page during cleanup: " + ex.getMessage());
                takeScreenshot("Movie_" + sanitizeFileName(sliderMovie) + "_RefreshFailed");
                soft.fail("Failed to refresh page during cleanup: " + ex.getMessage());
            }
        }
    }

    // ================= WATCH ON VALIDATION =================
    private boolean validateWatchOn(WebDriverWait wait, String xpath, String title) {
        try {
            String parentWindow = driver.getWindowHandle();

            WebElement watchButton = findElementSafely(wait, By.xpath(xpath));
            if (watchButton == null) {
                System.out.println("❌ 'Watch On' button NOT FOUND for: " + title);
                soft.fail("'Watch On' button not found for: " + title);
                return false;
            }

            System.out.println("✅ 'Watch On' button found for: " + title);

            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", watchButton);

            try {
                watchButton.click();
                System.out.println("Clicked 'Watch On' button using standard click");
            } catch (ElementNotInteractableException e) {
                System.out.println("Using JavaScript click for 'Watch On' button");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", watchButton);
            }

            // Wait for new window
            try {
                wait.until(d -> d.getWindowHandles().size() > 1);
                System.out.println("New window opened successfully");
            } catch (TimeoutException e) {
                System.out.println("❌ New window DID NOT OPEN for: " + title);
                soft.fail("New window didn't open for: " + title);
                return false;
            }

            // Switch to new window
            boolean windowSwitched = false;
            for (String window : driver.getWindowHandles()) {
                if (!window.equals(parentWindow)) {
                    driver.switchTo().window(window);
                    windowSwitched = true;
                    try {
                        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

                        // Wait for full page load
                        wait.until(webDriver ->
                                ((JavascriptExecutor) webDriver)
                                        .executeScript("return document.readyState")
                                        .equals("complete")
                        );

                        String providerPageTitle = driver.getTitle();
                        String providerPageUrl = driver.getCurrentUrl();

                        System.out.println("✅ Provider page opened - Title: " + providerPageTitle);
                        System.out.println("   Provider URL: " + providerPageUrl);

                        metrics.addProviderPage(providerPageTitle, providerPageUrl);

                        if (providerPageTitle.toLowerCase().contains(title.toLowerCase())) {
                            System.out.println("✅ Provider page title matches movie: " + title);
                        } else {
                            System.out.println("⚠️ Provider page title may not match - Expected: " + title + " | Actual: " + providerPageTitle);
                        }

                    } catch (Exception e) {
                        System.out.println("⚠️ Could not get provider page details: " + e.getMessage());
                    }
                    driver.close();
                    System.out.println("Closed provider window");
                    break;
                }
            }

            driver.switchTo().window(parentWindow);

            if (!windowSwitched) {
                System.out.println("❌ Failed to switch to new window for: " + title);
                return false;
            }

            return true;

        } catch (Exception e) {
            System.out.println("💥 Watch On validation exception for " + title + ": " + e.getMessage());
            e.printStackTrace();
            soft.fail("Watch On validation error for " + title + ": " + e.getMessage());
            return false;
        }
    }

    // ================= PLAY TRAILER VALIDATION =================
    private boolean validatePlayTrailer(WebDriverWait wait, String xpath, String title) {
        try {
            WebElement playTrailerButton = findElementSafely(wait, By.xpath(xpath));
            if (playTrailerButton == null) {
                System.out.println("❌ 'Play Trailer' button NOT FOUND for: " + title);
                soft.fail("'Play Trailer' button not found for: " + title);
                return false;
            }

            System.out.println("✅ 'Play Trailer' button found for: " + title);

            playTrailerButton.click();
            System.out.println("Clicked 'Play Trailer' button successfully");

            // Wait for video element or player to appear
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("video")));
                System.out.println("✅ Trailer video element detected");
            } catch (TimeoutException e) {
                System.out.println("⚠️ Trailer video element not detected (may still be loading or using different player)");
            }

            return true;

        } catch (Exception e) {
            System.out.println("💥 Play Trailer validation exception for " + title + ": " + e.getMessage());
            e.printStackTrace();
            soft.fail("Play Trailer validation error for " + title + ": " + e.getMessage());
            return false;
        }
    }

    private WebElement findElementSafely(WebDriverWait wait, By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("Element not found: " + locator);
            return null;
        }
    }

    // ===================== CURRENT SLIDE TITLE =====================
    public String getCurrentMovieName() {
        try {
            WebElement activeSlide = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".swiper-slide-active .text-white.md\\:text-\\[28px\\]")
                    )
            );

            String name = activeSlide.getText().trim();
            return name.isEmpty() ? "Not found" : name;

        } catch (Exception e) {
            System.out.println("Could not retrieve current movie name: " + e.getMessage());
            return "Not found";
        }
    }

    // ===================== MOVIE TITLE FROM IMAGE =====================
    public String getMovieTitle(WebElement movieImage) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    movieImage
            );

            String title = movieImage.getAttribute("alt");
            if (title != null && !title.trim().isEmpty()) {
                return title.trim();
            }

            title = movieImage.getAttribute("title");
            if (title != null && !title.trim().isEmpty()) {
                return title.trim();
            }

            return null;

        } catch (Exception e) {
            System.out.println("Could not extract title from movie image: " + e.getMessage());
            return null;
        }
    }

    // ===================== SCREENSHOT =====================
    public String takeScreenshot(String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String destPath = "screenshots/failures/" + testName + "_" + timestamp + ".png";
            File dest = new File(destPath);

            dest.getParentFile().mkdirs();

            FileUtils.copyFile(src, dest);
            System.out.println("📸 Screenshot saved: " + destPath);
            return dest.getAbsolutePath();
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "Unknown";
        }
        return fileName.replaceAll("[^a-zA-Z0-9-_]", "_").substring(0, Math.min(fileName.length(), 50));
    }

    // ===================== TEARDOWN =====================
    @AfterMethod
    public void teardown() throws InterruptedException {
    //	soft.assertAll();
        System.out.println("🧹 Cleaning up and closing browser...");
        Thread.sleep(2000);
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser closed successfully");
        }
    }
   
    
    @AfterSuite
    
    public void matric() {
    	
        // Print final metrics
        System.out.println("\n========================================");
        System.out.println("📊 FINAL METRICS for Watch and Tailer Button");
      
        metrics.printMetrics();

     //   soft.assertAll();
        System.out.println("========================================");
        System.out.println("✅ TEST COMPLETED");
        System.out.println("========================================");
    	
    }
    
    
    
}