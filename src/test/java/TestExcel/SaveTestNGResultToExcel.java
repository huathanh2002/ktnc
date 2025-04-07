package TestExcel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SaveTestNGResultToExcel {
    WebDriver driver;
    public UImap uImap;
    public UImap datafile;
    public String workingDir;

    HSSFWorkbook workbook;
    HSSFSheet sheet;
    Map<String, Object[]> TestNGResults;

    @BeforeClass
    public void SetUp(){
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("TestNG Results");
        TestNGResults = new HashMap<>();

        uImap = new UImap("src/main/resources/locator.propertites");
        datafile = new UImap("src/main/resources/data.propertites");
    }

    @Test(description = "Open test")
    public void launchWebsite(){
        try {
            driver.get("https://practicetestautomation.com/practice-test-login/");
            TestNGResults.put("2",new Object[]{
                    1d,"Navigate to demo website", "Site gets opened successfully", "pass"
            });
        } catch (Exception e) {
            TestNGResults.put("2",new Object[]{
                    1d,"Navigate to demo website", "Site failed to open", "Fail"
            });
            e.printStackTrace();
        }
    }

    @Test(description = "Fill the login Details")
    public void fillLoginDetails() throws Exception{
        try {
            WebElement username = driver.findElement(uImap.getlocator("username"));
            username.sendKeys(datafile.getData("student"));

            WebElement password = driver.findElement(uImap.getlocator("password"));
            username.sendKeys(datafile.getData("Password123"));

            TestNGResults.put("3", new Object[]{
                    2d,"Fill login form data (Username/Password)","Login details are filled successfully","pass"
            });

        } catch (Exception e) {
            TestNGResults.put("3", new Object[]{
                    2d,"Fill login form data (Username/Password)","Login form filling failed","failed"
            });
            e.printStackTrace();
        }
    }

    @Test(description = "perform login")
    public void doLogin() throws Exception{
        try {
            WebElement loginButton = driver.findElement(uImap.getlocator("submit"));
            loginButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(uImap.getlocator("post-title")));
            Assert.assertEquals(username.getText(),"Logged In Successfully");

            TestNGResults.put("4", new Object[]{
                    3d,"Click login and verify ","Login successfully","pass"
            });
        } catch (Exception e) {
            TestNGResults.put("4", new Object[]{
                    3d,"Click login and verify ","Login failed","fail"
            });
            e.printStackTrace();
        }
    }
    @AfterClass
    public void suiteTearDown(){
        Set<String> keyset = TestNGResults.keySet();
        int rownum = 0;
        for (String key : keyset){
            Row row = sheet.createRow(rownum++);
            Object[] objArr = TestNGResults.get(key);
            int cellnum = 0;
            for (Object obj : objArr){
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Date)
                    cell.setCellValue((Date) obj);
                else if (obj instanceof Boolean)
                    cell.setCellValue((Boolean) obj);
                else if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Double)
                    cell.setCellValue((Double) obj);
            }
        }

        try (FileOutputStream out = new FileOutputStream(("SaveTestNGResultToExcel.xls"))) {
            workbook.write(out);
            System.out.println("successfully");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver.quit();
    }
}
