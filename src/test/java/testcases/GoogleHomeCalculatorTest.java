package testcases;

import com.googleCalculator.qa.pages.CalculatorPage;
import com.googleCalculator.qa.pages.GoogleHomePage;
import com.googleCalculator.qa.util.TestListener;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.googleCalculator.qa.base.TestBase;
import com.googleCalculator.qa.util.TestUtil;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Arrays;


@Listeners(TestListener.class)
public class GoogleHomeCalculatorTest extends TestBase {

	TestUtil testUtil;
	GoogleHomePage googleHomePage;
	CalculatorPage calculatorPage;

	@BeforeMethod
	public void setUp() throws InterruptedException {
		initialization();
		testUtil = new TestUtil();
		googleHomePage = new GoogleHomePage();
		calculatorPage = new CalculatorPage();
	}

	@Test(priority = 1)
	public void testCalculatorAppIsVisible() throws IOException {
		// Step 1: Check if the calculator is displayed on Google search
		boolean actualResult = googleHomePage.SearchCalculatorViaSearchBar();

		// Step 2: Log the visibility status of the calculator app
		TestListener.getCurrentTest().info("Calculator app visibility: " + actualResult);

		// Step 3: Assert that the calculator app is visible
		Assert.assertTrue(actualResult, "Calculator app is not visible on the Google search.");
	}

	@Test(priority = 2)
	public void testUIElementsAreSelectable() throws IOException {
		// Step 1: Open the calculator
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed");

		// Step 2: Retrieve UI component test data
		TestListener.getCurrentTest().info("Checking for UI components fetched from test data sheet 'UiComponent'");

		for (Object[] uiComponents : TestUtil.getTestData("UiComponent")) {
			// Step 3: Convert the UI component to a string
			String uiElement = Arrays.toString(uiComponents)
					.replace(".0", "")
					.replace("[", "")
					.replace("]", "");

			// Step 4: Log the UI element being checked
			TestListener.getCurrentTest().info("Checking for UI element: " + uiElement);

			// Step 5: Assert that the UI element exists
			Assert.assertTrue(googleHomePage.selectUIOperandExists(uiElement),
					"UI element '" + uiElement + "' is not selectable.");
		}
	}

	@Test(priority = 3)
	public void validateMathematicalExpressionSolving() throws IOException {
		// Step 1: Verify the calculator is displayed
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Step 2: Loop through expressions to evaluate
		for (Object[] expressionData : TestUtil.getTestData("ExpressionsToEvaluate")) {
			String expression = Arrays.toString(expressionData);
			TestListener.getCurrentTest().info("Evaluating expression: " + expression);

			// Step 3: Input the expression into the calculator
			calculatorPage.putExpression(expression);

			// Step 4: Format the expression for evaluation
			String formattedExpression = calculatorPage.getResult()
					.replaceAll("÷", "/")
					.replaceAll("×", "*");

			// Step 5: Calculate the expected result
			calculatorPage.selectSomethingFromCalculator("=");
			String actualValueStr = calculatorPage.getResult();

			Expression exp = new ExpressionBuilder(formattedExpression).build();
			double expectedValue = exp.evaluate();

			// Step 6: Compare results with a tolerance level
			double actualValue = Double.parseDouble(actualValueStr);
			double delta = 0.0001; // Tolerance level
			TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
			TestListener.getCurrentTest().info("Actual Result: " + actualValue);

			Assert.assertTrue(
					Math.abs(expectedValue - actualValue) < delta,
					"Expected: " + expectedValue + ", but got: " + actualValue
			);
		}
	}

	@Test(priority = 4)
	public void testClearEntryFunctionality() throws IOException {
		// Step 1: Open the calculator
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed");

		// Step 2: Prepare the input expression
		String expression = "(6+7)÷8";
		TestListener.getCurrentTest().info("Input expression: " + expression);

		// Step 3: Use CE to clear the last digit
		String actualValue = calculatorPage.clearUsingCE(expression);
		String expectedValue = expression.substring(0, expression.length() - 1); // Expected value after CE

		// Step 4: Log the expected and actual results
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Step 5: Assert that the actual value matches the expected value
		Assert.assertEquals(actualValue, expectedValue, "CE did not clear the last digit as expected.");
	}

	@Test(priority = 5)
	public void testACFunctionality() throws IOException {
		// Validate that the AC button clears the output
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		String expression = "4+4"; // Check AC appears only after pressing '='
		TestListener.getCurrentTest().info("Input Expression: " + expression);
		calculatorPage.putExpression(expression);

		// Check if the AC button is visible in the background
		if (!calculatorPage.checkACFromCalculatorInBackground("AC")) {
			TestListener.getCurrentTest().info("AC not found in background, proceeding with evaluation.");

			// Select '=' to evaluate the expression
			calculatorPage.selectSomethingFromCalculator("=");
			TestListener.getCurrentTest().info("Selected '=' to evaluate the expression.");

			// Select the AC button to clear the display
			calculatorPage.selectSomethingFromCalculator("AC");
			TestListener.getCurrentTest().info("Selected AC button on the front screen.");

			// Expected and actual result comparison
			String expectedValue = "0";
			String actualValue = calculatorPage.getResult();
			TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
			TestListener.getCurrentTest().info("Actual Result: " + actualValue);

			// Assert that the actual value matches the expected value
			Assert.assertEquals(actualValue, expectedValue, "Expected and actual values do not match after pressing AC.");
		} else {
			// Fail the test if AC appears unexpectedly
			Assert.fail("AC appears in the background without pressing '='. Test failed.");
		}
	}


	@Test(priority = 6)
	public void validateInvalidExpressionsNotEntered() throws IOException {
		// Validate that invalid expressions are not accepted by the calculator
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		String invalidExpression = "4.5))( + 3)..";
		TestListener.getCurrentTest().info("Input String: " + invalidExpression);

		// Enter the invalid expression into the calculator
		TestListener.getCurrentTest().info("Putting invalid expression: ");
		calculatorPage.putExpression(invalidExpression);

		// Retrieve the result from the calculator
		TestListener.getCurrentTest().info("getting result from result box: ");
		String actualValue = calculatorPage.getResult();

		TestListener.getCurrentTest().info("Expected Result: Invalid input should not equal the expression.");
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual value does not equal the invalid expression
		Assert.assertFalse(actualValue.equals(invalidExpression),
				String.format("Invalid expression '%s' was incorrectly accepted. Actual output: '%s'",
						invalidExpression, actualValue));
	}


	@Test(priority = 7)
	public void divisionByZero() {
		// Validate that dividing by zero correctly returns "Infinity"
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		String expression = "(3.9+7.9x8)÷0";
		TestListener.getCurrentTest().info("Input String: " + expression);

		// Enter the expression into the calculator
		calculatorPage.putExpression(expression);
		TestListener.getCurrentTest().info("Putting expression in calculator: ");

		// Select the equals operator to evaluate the expression
		calculatorPage.selectSomethingFromCalculator("=");
		String actualValue = calculatorPage.getResult();

		// Expected output when dividing by zero
		String expectedValue = "Infinity";
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual output matches the expected result
		Assert.assertTrue(actualValue.equals(expectedValue),
				String.format("Expected output '%s' for division by zero, but got '%s'.",
						expectedValue, actualValue));
	}


	@Test(priority = 8)
	public void multiplicationByZero() {
		// Validate that multiplying by zero correctly returns "0"
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		String expression = "(3 + 68 + 7.8) x 0";
		TestListener.getCurrentTest().info("Input String: " + expression);

		// Enter the expression into the calculator
		calculatorPage.putExpression(expression);

		// Select the equals operator to evaluate the expression
		calculatorPage.selectSomethingFromCalculator("=");
		String actualValue = calculatorPage.getResult();

		// Expected output when multiplying by zero
		String expectedValue = "0";
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual output matches the expected result
		Assert.assertTrue(actualValue.equals(expectedValue),
				String.format("Expected output '%s' for multiplication by zero, but got '%s'.",
						expectedValue, actualValue));
	}



	@Test(priority = 9)
	public void checkForEmptyInputHowCEAndEqualsBehave() {
		// Validate how the calculator behaves with an empty input for CE and equals
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Define an empty input expression
		String expression = "";
		TestListener.getCurrentTest().info("Input String: '" + expression + "'");

		// Enter the empty expression into the calculator
		calculatorPage.putExpression(expression);

		// Select the equals operator to evaluate the expression
		calculatorPage.selectSomethingFromCalculator("=");
		TestListener.getCurrentTest().info("Selected '=' operator.");

		// Capture the actual result after pressing equals
		String actualValue = calculatorPage.getResult();
		String expectedValue = "0"; // Expected behavior for empty input
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual output matches the expected result
		Assert.assertTrue(actualValue.equals(expectedValue),
				String.format("Expected output '%s' for empty input, but got '%s'.",
						expectedValue, actualValue));

		// Now test the behavior of the Clear Entry (CE) button
		calculatorPage.selectSomethingFromCalculator("CE");
		TestListener.getCurrentTest().info("Selected 'CE' button.");

		// After CE, we expect the result to still be "0"
		String clearedValue = calculatorPage.getResult();
		TestListener.getCurrentTest().info("Expected Result after CE: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result after CE: " + clearedValue);

		// Assert that the result remains "0" after pressing CE
		Assert.assertTrue(clearedValue.equals(expectedValue),
				String.format("Expected output '%s' after CE, but got '%s'.",
						expectedValue, clearedValue));
	}


	@Test(priority = 10)
	public void checkForHowMultipleOperandTogetherBehaves() {
		// Validate how the calculator handles multiple operands in succession
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Define the input expression with multiple operators
		String expression = "7.9x-))x-6";
		TestListener.getCurrentTest().info("Input String: '" + expression + "'");

		// Enter the expression into the calculator
		TestListener.getCurrentTest().info("Putting  expression in calculator ");
		calculatorPage.putExpression(expression);
		TestListener.getCurrentTest().info("Input expression entered into the calculator.");

		// Capture the result from the calculator
		String actualValue = calculatorPage.getResult();
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Expected behavior is that the expression is altered due to multiple operators
		TestListener.getCurrentTest().info("Expected Result: " + expression);

		// Assert that the actual output does not equal the original expression
		Assert.assertFalse(actualValue.equals(expression),
				String.format("The expression was altered. Expected: '%s', but got: '%s'",
						expression, actualValue));
	}


	@Test(priority = 11)
	public void checkForZeroDividedByZero() {
		// Validate that dividing zero by zero yields an error
		String expectedValue = "Error";  // Expected result for this operation
		String expression = "0÷0";       // Expression to be evaluated

		// Log the input expression
		TestListener.getCurrentTest().info("Input String: " + expression);

		// Access the calculator
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Enter the expression and capture the result
		TestListener.getCurrentTest().info("Getting the result from Calculator result box ");
		String actualValue = calculatorPage.enterExpressionGetResult(expression);

		// Log the expected and actual results
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual result matches the expected error message
		Assert.assertTrue(actualValue.equals(expectedValue),
				String.format("Expected an error message for division by zero, but got: '%s'", actualValue));
	}


	@Test(priority = 12)
	public void checkBoundaryValueToGetInfinity() {
		// Validate that multiplying by a large number results in infinity
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Initialize the base number
		String baseNumber = "8";
		TestListener.getCurrentTest().info("Base number for multiplication: " + baseNumber);

		// Perform the multiplication operation that should result in infinity
		calculatorPage.enter307TimesANumber(baseNumber);
		TestListener.getCurrentTest().info("Entered a large multiplier.");

		// Input the expression to evaluate
		String expression = "x10=";
		calculatorPage.putExpression(expression);
		TestListener.getCurrentTest().info("Expression entered: " + expression);

		// Capture the result of the expression
		String actualValue = calculatorPage.enterExpressionGetResult(baseNumber);
		String expectedValue = "Infinity"; // Expected result

		// Log expected and actual results
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual result matches the expected value of infinity
		Assert.assertTrue(actualValue.equals(expectedValue),
				String.format("Expected result to be '%s' for boundary value calculation, but got: '%s'", expectedValue, actualValue));
	}

	@Test(priority = 13)
	public void checkPostDecimalHowManyValuesAreAcceptable() {
		// Validate behavior after entering a decimal point post-calculation
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Initial input value
		String exp = "8";
		TestListener.getCurrentTest().info("Base number entered: " + exp);

		// Perform multiplication to set up the scenario for testing
		TestListener.getCurrentTest().info("Putting "+exp+"--------307 times");
		calculatorPage.enter307TimesANumber(exp);
		calculatorPage.putExpression("x10=");
		TestListener.getCurrentTest().info("Expression for multiplication entered: x10=");

		// Get the result
		TestListener.getCurrentTest().info("Getting result");
		String actualValue = calculatorPage.enterExpressionGetResult(exp);
		String expectedValue = "Infinity"; // Expected result after multiplication
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual result matches the expected value of infinity
		Assert.assertTrue(actualValue.equals(expectedValue),
				String.format("Expected result to be '%s', but got: '%s'", expectedValue, actualValue));
	}

	@Test(priority = 14)
	public void checkWhatHappensWhenPressingEqualRepeatedly() {
		// Validate the behavior of repeated '=' button presses
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Input expression
		String exp = "8+2";
		TestListener.getCurrentTest().info("Input Value: " + exp);

		// Calculate the result of the expression
		calculatorPage.enterExpressionGetResult(exp);
		TestListener.getCurrentTest().info("Initial calculation performed.");

		// Press '=' button twice to check repeated behavior
		for (int i = 0; i < 2; i++) {
			calculatorPage.selectSomethingFromCalculator("=");
			TestListener.getCurrentTest().info("Pressed '=' button.");
		}

		// Capture the actual result
		String actualValue = calculatorPage.getResult();
		double expectedValue = new ExpressionBuilder(exp).build().evaluate(); // Evaluate expected result

		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual result matches the expected value
		Assert.assertTrue(actualValue.equals(String.valueOf((int) expectedValue)),
				String.format("Expected result: '%s', but got: '%s'", expectedValue, actualValue));
	}

	@Test(priority = 15)
	public void checkWhatHappensIfDecimalInsertedAfterAnswer() {
		// Validate the behavior of inserting a decimal after an answer is displayed
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Input expression
		String exp = "8+2";
		TestListener.getCurrentTest().info("Input Value: " + exp);

		// Calculate the result
		TestListener.getCurrentTest().info("getting the result");
		String ActualValueOld = calculatorPage.enterExpressionGetResult(exp);
		TestListener.getCurrentTest().info("Calculation performed. Result displayed."+ActualValueOld);

		// Insert a decimal point
		TestListener.getCurrentTest().info("Inserting decimal point.");
		calculatorPage.selectSomethingFromCalculator(".");

		// Capture the actual result
		String actualValue = calculatorPage.getResult();

		String expectedValue=".";
		TestListener.getCurrentTest().info("Expected Result: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual result matches the expected value
		Assert.assertTrue(actualValue.equals(expectedValue),
				String.format("Expected result to be '%s', but got: '%s'", expectedValue, actualValue));
	}

	@Test(priority = 16)
	public void checkOutputContainsSpecialCharacterE() {
		// Validate the output for specific expressions that may result in the 'e' character
		googleHomePage.getCalculator();
		TestListener.getCurrentTest().info("Calculator displayed.");

		// Input expression that may result in scientific notation
		String exp = "0.4599666666666÷999999999999";
		TestListener.getCurrentTest().info("Input Value: " + exp);

		// Calculate the result
		TestListener.getCurrentTest().info("Getting result from result box");
		calculatorPage.enterExpressionGetResult(exp);
		String actualValue = calculatorPage.getResult();
		String expectedValue = "e"; // Expected to contain 'e' for scientific notation
		TestListener.getCurrentTest().info("Expected Result should contain: " + expectedValue);
		TestListener.getCurrentTest().info("Actual Result: " + actualValue);

		// Assert that the actual result contains the expected character
		Assert.assertTrue(actualValue.contains(expectedValue),
				String.format("Expected result to contain '%s', but got: '%s'", expectedValue, actualValue));
	}



	@AfterMethod
	public void tearDown() {
        driver.quit();
    }
	
	
}
