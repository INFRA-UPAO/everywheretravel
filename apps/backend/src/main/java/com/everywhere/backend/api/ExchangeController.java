package com.everywhere.backend.api; 

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    private static final Logger LOGGER = Logger.getLogger(ExchangeController.class.getName());

    // @GetMapping("/tipo-de-cambio")
    public ResponseEntity<Object> getTipoDeCambio() { 
        System.setProperty("webdriver.chrome.silentOutput", "true");  
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = null;
        
        try {
            driver = new ChromeDriver(options);
            LOGGER.info("Robot iniciando, navegando a la página...");

            driver.get("https://www.cambiomundial.com/");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            LOGGER.info("El robot está en la página, esperando que los valores aparezcan...");

            WebElement buyElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lblvalorcompra")));
            WebElement sellElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lblvalorventa")));

            wait.until(d -> !d.findElement(By.id("lblvalorcompra")).getText().isEmpty());

            String buyPrice = buyElement.getText();
            String sellPrice = sellElement.getText();
            
            LOGGER.info("Valores encontrados: Compra=" + buyPrice + ", Venta=" + sellPrice);

            Map<String, String> rates = new HashMap<>();
            rates.put("buy", buyPrice);
            rates.put("sell", sellPrice);
            
            return new ResponseEntity<>(rates, HttpStatus.OK);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El robot falló: " + e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "El robot no pudo leer los datos de la página.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            
        } finally {
            if (driver != null) {
                driver.quit();
                LOGGER.info("El robot ha terminado y el navegador se ha cerrado.");
            }
        }
    }
}