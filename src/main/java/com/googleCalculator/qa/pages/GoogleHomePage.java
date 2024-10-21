package com.googleCalculator.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.googleCalculator.qa.base.TestBase;

public class GoogleHomePage extends TestBase {
	private static final Logger logger = LogManager.getLogger(GoogleHomePage.class);
	public  void getCalculator()
	{
		WebElement searchBar = driver.findElement(By.xpath("//textarea[@aria-label='Search']"));
		searchBar.sendKeys("calculator");
		logger.info("Enter calculator-expression on serach bar");
		searchBar.sendKeys(Keys.ENTER);
		logger.info("Clicking enter");
	}
public boolean SearchCalculatorViaSearchBar() {
	boolean flag = false;
	getCalculator();

	WebElement calendarBox = driver.findElement(By.xpath("//div[@id='center_col']"));
	if (calendarBox.isDisplayed()) {
		logger.info("Calendar box appears on Google");
		flag=true;
	}
    return flag;
}
	public  boolean selectUIOperandExists(String value )  {
		String s= "//div[@role='button' and contains(text(),'" +value + "')]";
		System.out.println(s);
		WebElement elementSelected=driver.findElement(By.xpath(s));
		logger.info("Checking the element with xpath"+s+"+is displayed on Google Calculator");
		return elementSelected.isDisplayed();
    }
	
	
	

}
