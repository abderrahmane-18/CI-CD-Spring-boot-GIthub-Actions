package com.example.employeemanager.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeLifecycleIT {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(15);

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = System.getProperty("selenium.base-url", "http://localhost:8080");

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1440,1000"
        );

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void employeeLifecycle_shouldAddDisplayAndDeleteEmployee() throws Throwable {
        String suffix = UUID.randomUUID().toString();
        String firstName = "Selenium";
        String lastName = "Employee-" + suffix.substring(0, 8);
        String email = "selenium-" + suffix + "@example.test";
        String position = "QA Engineer";
        By employeeRow = By.cssSelector(
                "[data-testid='employee-row'][data-employee-email='" + email + "']"
        );

        try {
            driver.get(baseUrl);

            wait.until(ExpectedConditions.elementToBeClickable(By.id("add-employee-button"))).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("employee-form")));

            driver.findElement(By.id("employee-first-name")).sendKeys(firstName);
            driver.findElement(By.id("employee-last-name")).sendKeys(lastName);
            driver.findElement(By.id("employee-email")).sendKeys(email);
            driver.findElement(By.id("employee-position")).sendKeys(position);
            driver.findElement(By.id("save-employee-button")).click();

            WebElement createdEmployee = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(employeeRow)
            );
            assertThat(createdEmployee.getText())
                    .contains(firstName, lastName, email, position);

            createdEmployee.findElement(By.cssSelector("[data-testid='delete-employee']")).click();

            wait.until(ExpectedConditions.invisibilityOfElementLocated(employeeRow));
            assertThat(driver.findElements(employeeRow)).isEmpty();
        } catch (Throwable failure) {
            captureFailureScreenshot();
            throw failure;
        }
    }

    private void captureFailureScreenshot() {
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return;
        }

        try {
            Path screenshotDirectory = Path.of("target", "selenium-screenshots");
            Files.createDirectories(screenshotDirectory);
            Path source = screenshotDriver.getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(
                    source,
                    screenshotDirectory.resolve("employee-lifecycle-failure.png"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException ignored) {
            // Preserve the original Selenium failure when screenshot creation is unavailable.
        }
    }
}
