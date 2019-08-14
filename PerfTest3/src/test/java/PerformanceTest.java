import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

public class PerformanceTest {
    private WebDriver driver;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    private String cashIp = "http://172.29.16.238:8081/#/";
    
    private static final String KEY_PATTERN = "//button//span[text()='%s']/../..";
    private static final String NUMBER_KEY_PATTERN = "//div[contains(@class,'numpad')]//button//span[text()='%s']/../..";
    private String [] items = {"кефир апельсиновый", "йогурт апельсиновый", "кефир яблочный", "напиток вишневый", "лимонад вишневый",
                               "пряник виноградный", "пряник банановый", "пряник грушевый", "кефир вишневый", "кефир банановый",
                               "напиток апельсиновый", "напиток виноградный", "напиток малиновый", "напиток томатный", "напиток яблочный",
                               "йогурт виноградный", "йогурт клубничный", "йогурт лимонный", "йогурт малиновый", "йогурт томатный"};


    @Before
    public void setUp() throws Exception {

        System.setProperty( "webdriver.chrome.driver", "C:/Users/t.gospodarova/Documents/ChromeDriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("enable-automation");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        /*options.addArguments("--dns-prefetch-disable");
        //options.addArguments("--disable-features=VizDisplayCompositor");
*/
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

    }

    @Test
    public void performanceTest() throws Exception {

            //логин
            driver.get(cashIp);
            login();

            //выбор товара
            for(int i = 0; i<1; ++i){
                addItemToCheck(items[i]);
            }

            //расчитать и оплатить
            calculateAndPay("11111");
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    public void enterWordDefaultLanguageRu(String word) {
        for (int i = 0; i <3; i++) {
            String letter = String.valueOf(word.charAt(i));
            if(letter.equals(" "))  driver.findElement(By.xpath("//div[@class='space key']/div/button")).click();
            else{
                String xpathLetter = prepareKeyLocator(letter);
                driver.findElement(By.xpath(xpathLetter)).click();
            }
        }
    }

    public String prepareKeyLocator(String letter) {
        return String.format(KEY_PATTERN, letter);
    }

    public String prepareNumberLocator(String number) {
        return String.format(NUMBER_KEY_PATTERN, number);
    }

    public void addItemToCheck(String itemName){
        driver.findElement(By.id("search")).click();
        enterWordDefaultLanguageRu(itemName);
        driver.findElement(By.xpath("//body/div[@id='content']/div[@class='root___1PrEc']/div[@class='searchPage___3kuq8']/div[@class='searchResults___1fidv']/div[@class='searchResults___33hC1']/div[@class='body___3HIb6']/div[@class='ScrollMain___31xwq']/div[@class='ScrollWrapper___2Ozza']/div[@class='viewport___2iQNp']/div[1]/div[1]/button[1]/div[1]/div[1]")).click();
    }

    public void login(){
        driver.findElement(By.xpath("//button/div/div")).click();
        driver.findElement(By.xpath("//div[2]/div/button/div/div")).click();
        driver.findElement(By.xpath("//div[3]/div/button/div/div")).click();
        driver.findElement(By.xpath("//div[2]/div/div/button/div/div")).click();
        driver.findElement(By.xpath("//div[2]/div[3]/div/button/div/div")).click();
    }

    public void enterNumber(String number) {
        for (int i = 0; i < number.length(); i++) {
            String num = String.valueOf(number.charAt(i));
            String xpathNum = prepareNumberLocator(num);
            driver.findElement(By.xpath(xpathNum)).click();
        }
    }

    private void  calculateAndPay(String sum){
        //рассчитать
        driver.findElement(By.xpath("//div[@class='box___3CtS5']")).click();
        driver.findElement(By.xpath("//html[1]/body[1]/div[4]/div[1]/div[1]/div[3]/div[2]/div[1]/div[2]/div[2]/button[1]/div[1]/div[1]/div[1]")).click();

        driver.findElement(By.xpath("//div[@class='cardImage___SXPTP cardImage_small___v94fB']//div[@class='image___1srKI']")).click();
        driver.findElement(By.xpath("/html/body/div[14]/div[2]/div/div/div/button[1]/span[1]")).click();

        //вводим сумму
        String parentWindowHandle = driver.getWindowHandle(); // save the current window handle.
        WebDriver popup = null;
        Iterator<String> windowIterator = driver.getWindowHandles().iterator();
        while(windowIterator.hasNext()) {
            String windowHandle = windowIterator.next();
            driver = driver.switchTo().window(windowHandle);
        }
        enterNumber(sum);
        driver.findElement(By.xpath("//div[@class='raisedButton___lcUPf mode_VGA___1wP0J theme_default___2AmzO']//button//div//div//div[@class='box___3CtS5']")).click();
    }
}