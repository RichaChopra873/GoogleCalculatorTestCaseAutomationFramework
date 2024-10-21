package com.googleCalculator.qa.pages;

import com.googleCalculator.qa.base.TestBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CalculatorPage extends TestBase {
	private static final Logger logger = LogManager.getLogger(CalculatorPage.class);

	public boolean checkACFromCalculatorInBackground(String value)
	{
		logger.info("Checking AC button is in background or not");
		String s= "//div[contains(@style,\"none\") and  contains(text(),'"+value+"')]";
		//AC is in background returns true else false
			return driver.findElement(By.xpath(s)).isDisplayed();
	}

	public void selectSomethingFromCalculator(String value){
			String s="//div[@role='button' and contains(text(),'"+value+"')]";
		logger.info("Clicking the"+value+"button on calculator");
	driver.findElement(By.xpath(s)).click();
		}

	public String getResult()
	{
		logger.info("Getting the result from the calculator answer box");
        return driver.findElement(By.xpath("//span[@id='cwos']")).getText().replaceAll("\\s", "");
    }


	public void click(String locator) {

		WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
		logger.info("Clicking calculator element with locator: "+locator);
		element.click();
	}

public void putExpression(String expression) {
	// Clean the expression: only keep valid characters
	expression = expression.replaceAll("[^0-9+÷()x.-]", ""); // Allow only valid characters
	for (char ch : expression.toCharArray()) {
	//	System.out.println("Current character: '" + ch + "' (ASCII: " + (int) ch + ")=======");
		logger.info("Entering the expression one character at time");
		switch (ch) {
			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
			case '+': case '÷': case '(': case ')':
				String s1 = String.format("//div[text()='%c']", ch);
				logger.info("Clicking calculator element with locator: "+s1);
				click(s1);
				break;

			case 'x':
				String s2 = "//div[@aria-label='multiply']";
				logger.info("Clicking calculator element with locator: "+s2);
				click(s2);
				break;

			case '-':
				String s3 = "//div[@aria-label='minus']";
				logger.info("Clicking calculator element with locator: "+s3);
				click(s3);
				break;

			case '.':
				String s4 = "//div[@aria-label='point']";
				logger.info("Clicking calculator element with locator: "+s4);
				click(s4);
				break;

			default:
				logger.info("Unsupported character enter, Cannot click");
				System.err.println("Ignoring unsupported character: " + ch);
				break;
		}
	}
}

	public String enterExpressionGetResult(String expression) {
		logger.info("Entering the expression: "+expression);
		putExpression(expression);
		// Click the equals button
		click("//div[text()='=']");
		logger.info("Clicking calculator element  = ");
        return getResult();
    }

	public String clearUsingCE(String expression){
		logger.info("Entering the expression: "+expression);
		putExpression(expression);
		click("//div[text()='CE']");
		logger.info("Clicking calculator element CE ");
		return getResult();

	}

	public void enter307TimesANumber(String expression) {
		logger.info("Entering "+expression);
		for(int i=0;i<=307;i++) {
			putExpression(expression);
			System.out.println(i+"this times number is entered");
		}
	}
}
