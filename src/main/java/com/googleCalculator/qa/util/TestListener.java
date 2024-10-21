package com.googleCalculator.qa.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.googleCalculator.qa.base.TestBase;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestListener extends TestBase implements ITestListener {

    private static ExtentReports extentReports;
    private ExtentTest test;
    private ExtentSparkReporter extentSparkReporter;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public TestListener() {
        // Initialize the ExtentReports and ExtentSparkReporter
        extentSparkReporter = new ExtentSparkReporter("C:\\Users\\tarun\\IdeaProjects\\GoogleCalculatorTestFramework\\reports\\ExtentReport.html");
        extentReports = new ExtentReports();
        extentReports.attachReporter(extentSparkReporter);
    }

    @Override
    public void onTestStart(ITestResult result) {
        test = extentReports.createTest(result.getMethod().getMethodName()); // Create a test in the report
        extentTest.set(test);
        System.out.println("Test " + result.getName() + " started.");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.pass("Test passed successfully."); // Log the success in the report
        System.out.println("Test " + result.getName() + " passed.");
    }

    @Override
        public void onTestFailure(ITestResult result) {
            test.fail("Test failed: " + result.getThrowable().getMessage());
            // Log the failure in the report
            String screenshotsDir = "C:\\Users\\tarun\\IdeaProjects\\GoogleCalculatorTestFramework\\failed_tests\\";
            String failedTest = result.getName();

            try {
                TakesScreenshot screenshot = (TakesScreenshot) driver;
                File file = screenshot.getScreenshotAs(OutputType.FILE);
                Path destinationPath = Paths.get(screenshotsDir + failedTest + ".png");

                // Check if the file already exists
                if (Files.exists(destinationPath)) {
                    // Optionally delete the existing file
                    // Files.delete(destinationPath);

                    // Append a timestamp to the filename
                    String newFilename = failedTest + "_" + System.currentTimeMillis() + ".png";
                    destinationPath = Paths.get(screenshotsDir + newFilename);
                }

                // Save the screenshot
                Files.copy(file.toPath(), destinationPath);
                test.addScreenCaptureFromPath(destinationPath.toString()); // Attach screenshot to the report
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Test '" + failedTest + "' has failed and a screenshot was taken.");
        }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.skip("Test skipped."); // Log the skip in the report
        System.out.println("Test " + result.getName() + " skipped.");
    }

    @Override
    public void onFinish(ITestContext context) {
        extentReports.flush(); // Flush the report at the end
        System.out.println("All tests finished.");
    }

    public static ExtentTest getCurrentTest() {
        return extentTest.get(); // Getter for the current ExtentTest
    }
}