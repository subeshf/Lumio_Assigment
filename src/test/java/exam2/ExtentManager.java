package exam2;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getExtent() {

        if (extent == null) {

            ExtentSparkReporter reporter =
                    new ExtentSparkReporter("reports/extent-report.html");

            reporter.config().setReportName("TLDR Automation Report");
            reporter.config().setDocumentTitle("Lomio Test Execution");

            extent = new ExtentReports();
            extent.attachReporter(reporter);

            extent.setSystemInfo("Project", "TLDR");
            extent.setSystemInfo("Browser", "Chrome");
            extent.setSystemInfo("Framework", "Selenium + TestNG");
        }
        return extent;
    }
}
