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
        Object instance = result.getInstance();
        String screenshotPath = null;

        if (instance instanceof LumioTest) {

            LumioTest testInstance = (LumioTest) instance;
            screenshotPath = testInstance.takeScreenshot(
                    result.getMethod().getMethodName());

        } else if (instance instanceof SliderTest) {

            SliderTest testInstance1 = (SliderTest) instance;
            screenshotPath = testInstance1.takeScreenshot(
                    result.getMethod().getMethodName());

        } else {
            test.get().warning("Unknown test class: "
                    + instance.getClass().getName());
        }

        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath);
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
