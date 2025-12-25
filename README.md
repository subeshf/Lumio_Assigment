Lumio_Assigment

Java Selenium automation framework using Selenium WebDriver, TestNG, Maven, and Extent Reports.

📥 Clone Repository
git clone https://github.com/subeshf/Lumio_Assigment.git
cd Lumio_Assigment

▶️ Run Tests
Prerequisites

Java JDK 11+

Maven 3.6+

Chrome Browser


Run with Maven
mvn clean test

Run with TestNG

Run testng.xml from IDE or Maven configuration

🛠 Tech Stack

Java

Selenium WebDriver

TestNG

Maven

Extent Reports

Log4j2

📊 Reports & Logs

Extent Report: reports/extent-report.html

Screenshots: screenshots/ (on failures)

Logs: logs/

📄 Key Classes

LumioTestcases.java – UI test execution

MetricsCollector.java – Execution metrics

ExtentManager.java – Report setup

TestListener.java – Listener & screenshots

🧪 Flow

Test → Validation → Metrics → Screenshot on failure → Report
