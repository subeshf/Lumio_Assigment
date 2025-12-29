package exam2;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@Listeners(TestListener.class)
public class LumioTest {

    /* =========================
       LOGGER
       ========================= */
    Logger log = LogManager.getLogger(LumioTest.class);

    /* =========================
       DRIVER OBJECTS
       ========================= */
    public WebDriver driver;
    WebDriverWait wait;
    Actions act;
    SoftAssert soft;

    // TC_01 – Launch Browser
    
    
    @Test(priority = 1)
    public void TC_01_OpenBrowser() {

        log.info("Launching Chrome browser");

        ChromeOptions option = new ChromeOptions();
        option.addArguments("--headless=new"); 
        option.addArguments("--window-size=1920,1080");
        option.addArguments("--start-maximized");
      
        driver = new ChromeDriver(option);

       // driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://tldr.lumiolabs.ai/");

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        act = new Actions(driver);
        soft = new SoftAssert();

        log.info("Navigated to TLDR application");
        Assert.assertTrue(driver.getCurrentUrl().contains("tldr.lumiolabs.ai"));
    }

   
    //  TC_02 – Verify Page Title
       
    @Test(priority = 2,enabled=true)
    public void TC_02_VerifyTitle() {

        log.info("Verifying page title");

        String expected =
                "TLDR - Trending, New & Upcoming Movies & Shows on OTT";

        Assert.assertEquals(driver.getTitle(), expected);
    }
    
    
    

    // TC_03 – Calendar Navigation

    @Test(priority = 3,enabled=true)
    public void TC_03_OpenCalendarAndGoToOctober() {

        log.info("Opening calendar");

        WebElement calendarButton = driver.findElement(By.xpath("//div[contains(@class,'border-l border-[#444444] flex items-center justify-center')]"));
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].scrollIntoView(true);", calendarButton);
		
		act = new Actions(driver);
		act.moveToElement(calendarButton).perform();

        WebElement prevBtn =driver.findElement(By.xpath("//button[contains(@aria-label,'Go to the Previous Month')]"));
              

        while (true) {
            String month = driver.findElement(By.xpath("//span[contains(@role,'status')]")).getText();
            if (month.equals("October 2025")) {
                log.info("Reached October 2025");
                break;
            }
            prevBtn.click();
        }

        driver.findElement(By.xpath("//button[normalize-space()='4']")).click();
     
    }
    
    
    
    //  TC_04 – Weekly Content Validation from October to Till of December

    @Test(priority = 4,enabled=true)
    
    public void TC_04_ValidateWeeklyContentTillEnd() {
        
    	
    	log.info("Weekly Content Validation from October to Till of December");
    	TestListener.getTest().info("Weekly Content Validation from October to Till of December");
    	
    	changeWeek("TC_004");
    	
    	//Looping for 2 month
    	
    	for(int i=0;i<2;i++)
		{
			changeMonth("TC_004");	
		}	
    }
    
    
    // TC_05 – Provider Validation

    
    @Test(priority = 5,enabled=true)
    public void TC_05_ValidateProviders() {

        log.info("Validating content providers");

        List<WebElement> providers =
                driver.findElements(By.xpath("//div[@class='py-3 lg:py-4 false']"));

        for (WebElement provider : providers) {

            String providerName =
                    provider.findElement(By.tagName("img")).getAttribute("alt");

            log.info("Clicking provider: " + providerName);
            provider.click();

            MetricsCollector.providersValidated.add(providerName);

            String decodedUrl =
                    URLDecoder.decode(driver.getCurrentUrl(), StandardCharsets.UTF_8);

            soft.assertTrue(
                    decodedUrl.toLowerCase().contains(providerName.toLowerCase()),
                    "Provider URL mismatch for " + providerName
            );
        }
      
        MetricsCollector.totalProvidersValidated=providers.size();
        soft.assertAll();
    }
    
    /* =========================
    TC_06 – Past 3 Week Provider's Content Validation
    ========================= */
    
    @Test(priority =6,enabled=true)
    
    public void TC_006_ValidatePast3WeekContentProvider() {
    	
    	log.info("Checking last 3 week Providers Contents");
	    int weekno = 1;
	    int maxweek = 3;
	    act = new Actions(driver);
	    String currentMonth = getCurrentMonthName();
	    if (currentMonth.equalsIgnoreCase("November")) {
	        maxweek = 6;
	    }
	
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	
	    for (int weekRow = 1; weekRow <= maxweek; weekRow++) {
	
	        // Open calendar (hover safety)
	        WebElement calendarButton = wait.until(ExpectedConditions
	                .visibilityOfElementLocated(By.xpath(
	                        "//div[contains(@class,'border-l border-[#444444]')]")));
	
	        js.executeScript("arguments[0].scrollIntoView({block:'center'});", calendarButton);
	        new Actions(driver).moveToElement(calendarButton).perform();
	
	        // 🔑 IMPORTANT: click BUTTON inside TD
	        List<WebElement> weekButtons = driver.findElements(
	                By.xpath("//tbody/tr[" + weekRow + "]/td[1]//button"));
	
	        if (weekButtons.isEmpty()) {
	            continue;
	        }
	
	        WebElement weekButton = weekButtons.get(0);
	
	        // Skip disabled days
	        if (!weekButton.isEnabled()) {
	            continue;
	        }
	
	        wait.until(ExpectedConditions.elementToBeClickable(weekButton));
	        js.executeScript("arguments[0].click();", weekButton);
	
	        System.out.println("Month name  "+currentMonth+ "  WeekNumber " + weekno );
	        
	        checkImageMoviesTitle("TC_006");
	        weekno++;
	        
	        providerChangeValidation();
	        
	        MetricsCollector.totalWeekCheckforProviderContent++;
	    }
    	
    }
    
   




    @Test(priority = 8)
    public void TC_08_PageRefreshStability() {

        log.info("Refreshing page to verify stability");

        driver.navigate().refresh();

        Assert.assertTrue(
                driver.findElements(
                        By.xpath("//img[@data-card-type='top-ten-card']")).size() > 0);
    }

    /* =========================
       FINAL METRICS REPORT
       ========================= */
    @AfterSuite
public void printExecutionSummary() {

    log.info("===== FINAL EXECUTION SUMMARY =====");

    System.out.println("\n========== FINAL EXECUTION SUMMARY ==========");
    
    System.out.println("\n========== Weekly (October to December) Calendar Validation ==========");
    System.out.println("Weeks Tested: " + MetricsCollector.totalWeekValidated);
    System.out.println("Months Tested: " + MetricsCollector.totalMonthValidated);
    System.out.println("Total Movies Verified (TC_004): " + MetricsCollector.totalTitlesVerifiedTC04);
    
    System.out.println("\n========== Provider Validation (Last 3 Weeks) ==========");
    System.out.println("Providers Validated: " + MetricsCollector.providersValidated.size());
    System.out.println("Provider Names: " + MetricsCollector.providersValidated);
    System.out.println("Total Week Checks for Provider Content: " + MetricsCollector.totalWeekCheckforProviderContent);
    System.out.println("Total Movies in Provider Check: " + MetricsCollector.totalMoviesProvider);
    
    
    System.out.println("\n========== Issues Summary ==========");
    System.out.println("Total Issues Found: " + MetricsCollector.issuesFound);

    if (MetricsCollector.issues.length() > 0) {
        System.out.println("\nDetailed Issues:");
        System.out.println(MetricsCollector.issues);
    } else {
        System.out.println("No Issues Found ✅");
    }
    
    printExecutionSummaryinExtentreport();
}

    
    @BeforeSuite
    public void setupMetrics() {
        MetricsCollector.reset();
        log.info("MetricsCollector reset - starting fresh test run");
    }

    /* =========================
       CLEANUP
       ========================= */
    @AfterClass
    public void tearDown() {
        log.info("Closing browser");
        driver.quit();
    }
    
    

public void  printExecutionSummaryinExtentreport() {

    ExtentReports extent = ExtentManager.getExtent();
    ExtentTest summaryTest = extent.createTest("📊 Final Execution Summary");

    summaryTest.info("===== FINAL EXECUTION SUMMARY =====");

    summaryTest.info("📅 Weekly Calendar Validation (Oct → Dec)");
    summaryTest.info("Weeks Tested: " + MetricsCollector.totalWeekValidated);
    summaryTest.info("Months Tested: " + MetricsCollector.totalMonthValidated);
    summaryTest.info("Total Movies Verified (TC_004): " + MetricsCollector.totalTitlesVerifiedTC04);

    summaryTest.info("🎬 Provider Validation (Last 3 Weeks)");
    summaryTest.info("Providers Validated: " + MetricsCollector.providersValidated.size());
    summaryTest.info("Provider Names: " + MetricsCollector.providersValidated);
    summaryTest.info("Total Week Checks: " + MetricsCollector.totalWeekCheckforProviderContent);
    summaryTest.info("Total Movies in Provider Check: " + MetricsCollector.totalMoviesProvider);


    
    summaryTest.info("⚠️ Issues Summary");
    summaryTest.info("Total Issues Found: " + MetricsCollector.issuesFound);
    if ((MetricsCollectorsSlider.issuesFound.size() > 0))
    summaryTest.fail("Total issues found in TC_02_validate_Watch_Tailer_Button_FrontTopTenVsSlider" + MetricsCollectorsSlider.issuesFound.size() + "</pre>");
    
    if (MetricsCollector.issues.length() > 0) {
        summaryTest.fail("❌ Issues Found:");
        
        
     
        summaryTest.fail("<pre>" + MetricsCollector.issues + "</pre>");
    } else {
        summaryTest.pass("✅ No Issues Found");
    }

    extent.flush();
}
    

	
    
    public String takeScreenshot(String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);

            // Add timestamp for uniqueness
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String destPath = "screenshots/failures/" + testName + "_" + timestamp + ".png";
            File dest = new File(destPath);
            
            // Create directory if it doesn't exist
            dest.getParentFile().mkdirs();

            FileUtils.copyFile(src, dest);
            log.info("Screenshot saved: " + destPath);
            return dest.getAbsolutePath();
        } catch (Exception e) {
            log.error("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
    
  
	
	public void providerChangeValidation() {
		   WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		    List<WebElement> providers =
		            driver.findElements(By.xpath("//div[@class='py-3 lg:py-4 false']"));

		    for (int i = 0; i < providers.size(); i++) {

		        By providerDivLocator =
		                By.xpath("(//div[@class='py-3 lg:py-4 false'])[" + (i + 1) + "]");

		        WebElement providerDiv =
		                wait.until(ExpectedConditions.elementToBeClickable(providerDivLocator));

		        String providerName =
		                providerDiv.findElement(By.tagName("img")).getAttribute("alt");

		        System.out.println("Clicking provider: " + providerName);

		        providerDiv.click();
		        
		        String rawUrl = driver.getCurrentUrl();
		        String decodedUrl = URLDecoder.decode(rawUrl, StandardCharsets.UTF_8);

		        System.out.println("Decoded URL: " + decodedUrl);

		        boolean isProviderPresent =
		                decodedUrl.toLowerCase().contains(providerName.toLowerCase());

		        soft.assertTrue(
		                isProviderPresent,
		                "Provider mismatch. Expected: " + providerName +
		                " | URL: " + decodedUrl
		        );

		        // Wait for page/content refresh after click
		        wait.until(ExpectedConditions.presenceOfElementLocated(
		                By.xpath("//img[@data-card-type='top-ten-card']")));
		        
		        
		        checkImageMoviesCount();
		        
		        
		        
		      
		        
		    }
		    
		    soft.assertAll();
	}
	
	
	
public void changeMonth(String tc) {
		WebElement calendarButton = driver.findElement(By.xpath("//div[contains(@class,'border-l border-[#444444] flex items-center justify-center')]"));
		WebElement Next_Month_Button = driver.findElement(By.xpath("//button[contains(@aria-label,'Go to the Next Month')]"));
		act.moveToElement(calendarButton).perform();
		
		Next_Month_Button.click();
		
		// Add a small wait for calendar to update
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 MetricsCollector.totalMonthValidated++;
		//checkImageMoviesTitle();
		changeWeek(tc);
	}
	

	
	public void changeWeek(String tc) {
	
		 log.info("Validating weekly content");
	    int weekno = 1;
	    int maxweek = 5;
	    act = new Actions(driver);
	    String currentMonth = getCurrentMonthName();
	    if (currentMonth.equalsIgnoreCase("November")) {
	        maxweek = 6;
	    }
	
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	
	    for (int weekRow = 1; weekRow <= maxweek; weekRow++) {
	
	        // Open calendar (hover safety)
	        WebElement calendarButton = wait.until(ExpectedConditions
	                .visibilityOfElementLocated(By.xpath(
	                        "//div[contains(@class,'border-l border-[#444444]')]")));
	
	        js.executeScript("arguments[0].scrollIntoView({block:'center'});", calendarButton);
	        new Actions(driver).moveToElement(calendarButton).perform();
	
	        // 🔑 IMPORTANT: click BUTTON inside TD
	        List<WebElement> weekButtons = driver.findElements(
	                By.xpath("//tbody/tr[" + weekRow + "]/td[1]//button"));
	
	        if (weekButtons.isEmpty()) {
	            continue;
	        }
	
	        WebElement weekButton = weekButtons.get(0);
	
	        // Skip disabled days
	        if (!weekButton.isEnabled()) {
	            continue;
	        }
	
	        wait.until(ExpectedConditions.elementToBeClickable(weekButton));
	        js.executeScript("arguments[0].click();", weekButton);
	
	        System.out.println("Month name  "+currentMonth+ "  WeekNumber " + weekno );
	        log.info("Month name  "+currentMonth+ "  WeekNumber " + weekno );
	       
	        //for weekly tested log
	        
	        MetricsCollector.totalWeekValidated++;
	        MetricsCollector.weeksTested.add(currentMonth+weekno);
	        
	        checkImageMoviesTitle(tc);
	        weekno++;
	    }
	}

	


	

	// Helper method to get current month name from calendar
	public String getCurrentMonthName() {
		try {
			String calendarYearMonth = driver.findElement(By.xpath("//span[contains(@role,'status')]")).getText();
			// Extract month name (e.g., "October 2025" -> "October")
			return calendarYearMonth.split(" ")[0];
		} catch (Exception e) {
			System.out.println("Error getting month name: " + e.getMessage());
			return "October"; // Fallback
		}
	}
	
	public void checkImageMoviesTitle(String tc) {

    List<WebElement> listofmoviesimages =
            driver.findElements(By.xpath("//img[@data-card-type='top-ten-card']"));

    log.info("Total no of movies " + listofmoviesimages.size());
    System.out.println("Total no of movies " + listofmoviesimages.size());

    for (int i = 0; i < listofmoviesimages.size(); i++) {

        try {
            // Re-fetch element to avoid stale element issues
            WebElement singlemovieimage = driver.findElements(
                    By.xpath("//img[@data-card-type='top-ten-card']"))
                    .get(i);

            // Ensure image is visible (important for headless)
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    singlemovieimage
            );

            String movieTitle = null;

            // ===============================
            // 1️⃣ PRIMARY (Headless-safe): alt attribute
            // ===============================
            movieTitle = singlemovieimage.getAttribute("alt");

            // ===============================
            // 2️⃣ FALLBACK: title attribute
            // ===============================
            if (movieTitle == null || movieTitle.trim().isEmpty()) {
                movieTitle = singlemovieimage.getAttribute("title");
            }

            // ===============================
            // 3️⃣ FINAL FALLBACK: DOM text (no hover wait)
            // ===============================
            if (movieTitle == null || movieTitle.trim().isEmpty()) {
                try {
                    WebElement titleElement = singlemovieimage.findElement(
                            By.xpath("./ancestor::div[contains(@class,'group')]//span[contains(@class,'red-hat-semi-bold')]")
                    );
                    movieTitle = titleElement.getText().trim();
                } catch (Exception ignored) {
                    // Intentionally ignored
                }
            }

            // ===============================
            // RESULT LOGGING
            // ===============================
            if (movieTitle == null || movieTitle.isEmpty()) {
                log.warn("⚠️ Movie index " + i + " - Title not available (headless-safe)");
                System.out.println("⚠️ Movie index " + i + " - Title not available");
            } else {
                log.info("✅ Movie index " + i + " | Title: " + movieTitle);
                System.out.println("Movie index " + i + " | Title: " + movieTitle);

                if ("TC_004".equals(tc)) {
                    MetricsCollector.totalTitlesVerifiedTC04++;
                } else {
                    MetricsCollector.totalTitlesVerified++;
                }
            }

        } catch (Exception e) {
            log.error("❌ Error processing movie index " + i + " : " + e.getMessage(), e);
            System.out.println("Error processing movie index " + i + " : " + e.getMessage());
        }
    }
}

	
	
	public void checkImageMoviesCount() {

    List<WebElement> listofmoviesimages =
            driver.findElements(By.xpath("//img[@data-card-type='top-ten-card']"));

    int totalMovies = listofmoviesimages.size();
    System.out.println("Total no of movies: " + totalMovies);

    // ✅ STRICT RULE: must be EXACTLY 10
    soft.assertEquals(
            totalMovies,
            10,
            "Movie count is not exactly 10. Found: " + totalMovies + " ProviderUrl has incorrect movies count"+ driver.getCurrentUrl()
    );

    int imageNumber = 1;
    for (WebElement singlemovieimage : listofmoviesimages) {
        soft.assertTrue(
                singlemovieimage.isDisplayed(),
                imageNumber + " Position movie image not displayed"
        );
        imageNumber++;
        
        MetricsCollector.totalMoviesProvider++;
    }
    }

	public String getMovieTitle(WebElement singleMovieImage) {

	    try {
	        // Ensure visibility (headless-safe)
	        ((JavascriptExecutor) driver).executeScript(
	                "arguments[0].scrollIntoView({block:'center'});",
	                singleMovieImage
	        );

	        // 1️⃣ BEST: alt attribute
	        String title = singleMovieImage.getAttribute("alt");
	        if (title != null && !title.trim().isEmpty()) {
	            return title.trim();
	        }

	        // 2️⃣ fallback: title attribute
	        title = singleMovieImage.getAttribute("title");
	        if (title != null && !title.trim().isEmpty()) {
	            return title.trim();
	        }

	        // 3️⃣ last fallback: DOM text (NO hover, NO wait)
	        try {
	            WebElement titleElement = singleMovieImage.findElement(
	                    By.xpath("./ancestor::div[contains(@class,'group')]//span[contains(@class,'red-hat-semi-bold')]")
	            );
	            title = titleElement.getText().trim();
	            if (!title.isEmpty()) {
	                return title;
	            }
	        } catch (Exception ignored) {}

	        // Nothing found
	        return null;

	    } catch (Exception e) {
	        log.error("Error getting movie title", e);
	        return null;
	    }
	}

	
}