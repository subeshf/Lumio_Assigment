Lumio_Assigment

🚀 Java Selenium Automation Framework using TestNG & Extent Reports

This project is a Java-based test automation framework built using Selenium WebDriver, TestNG, Maven, and Extent Reports.
It is designed to automate UI validation, collect execution metrics, generate rich HTML reports, and capture screenshots for failures.

📂 Project Structure
Lumio_Assigment
│
├── src
│   ├── test
│   │   ├── java
│   │   │   └── exam2
│   │   │       ├── ExtentManager.java
│   │   │       ├── LumioTestcases.java
│   │   │       ├── MetricsCollector.java
│   │   │       └── TestListener.java
│   │   │
│   │   └── resources
│   │       └── log4j2.xml
│   │
│   └── main
│
├── logs
│   └── (execution logs)
│
├── reports
│   └── extent-report.html
│
├── screenshots
│   └── (failure screenshots)
│
├── test-output
│   └── (TestNG default reports)
│
├── target
│
├── pom.xml
└── testng.xml

🛠️ Tech Stack
Tool / Technology	Purpose
Java	Programming language
Selenium WebDriver	Browser automation
TestNG	Test execution & assertions
Maven	Build & dependency management
Extent Reports	HTML reporting
Log4j2	Logging framework
✨ Key Features

✅ Automated UI test execution

📊 Execution metrics tracking

Weeks tested

Providers validated

Total titles verified

Watch / Trailer validation

Issues found

📸 Automatic screenshots on failure

🧾 Extent HTML Report generation

🪵 Centralized logging using Log4j2

🧪 Soft assertions support

🔁 Stable execution with waits & retry-safe logic

📄 Important Classes Explained
LumioTestcases.java

Main test class

Contains UI automation logic

Executes validations and updates metrics

MetricsCollector.java

Central place to track:

Total tests executed

Providers validated

Issues found

Titles verified

ExtentManager.java

Creates and manages the Extent Report instance

Ensures single report generation per run

TestListener.java

Implements ITestListener

Captures:

Test pass / fail status

Screenshots on failure

Logs into Extent Report

▶️ How to Run the Tests
1️⃣ Prerequisites

Java JDK 11 or higher

Apache Maven 3.6+

Chrome Browser

ChromeDriver (matching browser version)

2️⃣ Clone the Repository
git clone https://github.com/subeshf/Lumio_Assigment.git
cd Lumio_Assigment

3️⃣ Run Using Maven
mvn clean test

4️⃣ Run Using TestNG XML

You can also run directly using:

testng.xml


(from IDE or Maven configuration)

📊 Reports & Logs
📈 Extent Report

Location:

reports/extent-report.html


Open in browser after execution

📸 Screenshots

Location:

screenshots/


Captured only on failures

🪵 Logs

Location:

logs/


Controlled via log4j2.xml

🧪 Test Execution Flow

Test starts

Browser launches

UI actions & validations performed

Metrics collected

Screenshots captured on failure

Results logged

Extent Report generated

🧩 Configuration Files
pom.xml

Dependency management

Plugins for TestNG & reporting

testng.xml

Controls:

Test execution order

Parallel execution (if enabled)

Listener binding
