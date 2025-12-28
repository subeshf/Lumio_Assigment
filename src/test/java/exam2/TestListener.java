package exam2;

import org.testng.*;
import com.aventstack.extentreports.*;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getExtent();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest =
                extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);

        test.get().info("Test started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {

        test.get().fail(result.getThrowable());

        try {
        	
        	
            LumioTest testInstance =
                    (LumioTest) result.getInstance();
            
            Slidertest testInstance1 =
                    (Slidertest) result.getInstance();

            String screenshotPath =
                    testInstance.takeScreenshot(
                            result.getMethod().getMethodName());
            
            

            if (screenshotPath != null) {
                test.get().addScreenCaptureFromPath(screenshotPath);
            } else {
                test.get().warning("Screenshot path was null");
            }
            
            
            String screenshotPath1 =
                    testInstance1.takeScreenshot(
                            result.getMethod().getMethodName());

            if (screenshotPath1 != null) {
                test.get().addScreenCaptureFromPath(screenshotPath1);
            } else {
                test.get().warning("Screenshot path was null");
            }

        } catch (Exception e) {
            test.get().fail("Screenshot capture failed: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip("Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    // 🔑 THIS enables logging from test classes
    public static ExtentTest getTest() {
        return test.get();
    }
}
