package cnrst.ma;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class Scenario2 {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
	System.setProperty("webdriver.gecko.driver","C:\\geckodriver\\geckodriver.exe");
    driver = new FirefoxDriver();
    baseUrl = "https://www.katalon.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testScenario2() throws Exception {
    driver.get("http://localhost:8989/connecter");
    driver.findElement(By.id("inputMail")).click();
    driver.findElement(By.id("inputMail")).clear();
    driver.findElement(By.id("inputMail")).sendKeys("invalidone@gmail.com");
    driver.findElement(By.id("inputPassword")).click();
    driver.findElement(By.id("inputPassword")).clear();
    driver.findElement(By.id("inputPassword")).sendKeys("123456");
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Accueil'])[1]/following::button[1]")).click();
    driver.findElement(By.linkText("Modifier la banque")).click();
    driver.findElement(By.id("inputRIB")).click();
    driver.findElement(By.id("inputRIB")).clear();
    driver.findElement(By.id("inputRIB")).sendKeys("546566565855");
    driver.findElement(By.id("inputBanque")).click();
    driver.findElement(By.id("inputAgence")).click();
    driver.findElement(By.id("inputSwiftCode")).click();
    driver.findElement(By.id("inputVille")).click();
    new Select(driver.findElement(By.id("inputVille"))).selectByVisibleText("AGADIR");
    driver.findElement(By.id("inputVille")).click();
    driver.findElement(By.id("contratSubmitBtn")).click();
    driver.findElement(By.id("genererContrat")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='CNRST'])[1]/following::button[1]")).click();
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
