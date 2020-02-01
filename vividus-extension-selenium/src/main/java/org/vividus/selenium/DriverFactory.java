package org.vividus.selenium;

import java.net.URL;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.google.common.base.Suppliers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.vividus.selenium.driver.TextFormattingWebDriver;
import org.vividus.util.property.IPropertyParser;

public class DriverFactory implements IDriverFactory
{
    private static final String SELENIUM_GRID_PROPERTY_FAMILY = "selenium.grid.capabilities";

    @Inject private IRemoteWebDriverFactory remoteWebDriverFactory;
    @Inject private IPropertyParser propertyParser;
    private URL remoteDriverUrl;

    private final Supplier<DesiredCapabilities> seleniumGridDesiredCapabilities = Suppliers.memoize(
            () -> new DesiredCapabilities(propertyParser.getPropertyValuesByFamily(SELENIUM_GRID_PROPERTY_FAMILY)));

    @Override
    public WebDriver getRemoteWebDriver(DesiredCapabilities desiredCapabilities)
    {
        DesiredCapabilities capabilities = updateDesiredCapabilities(
                new DesiredCapabilities(getSeleniumGridDesiredCapabilities()).merge(desiredCapabilities));

        RemoteWebDriver remoteWebDriver = remoteWebDriverFactory.getRemoteWebDriver(remoteDriverUrl, capabilities);
        return createWebDriver(remoteWebDriver);
    }

    protected WebDriver createWebDriver(WebDriver webDriver)
    {
        WebDriver driver = new TextFormattingWebDriver(webDriver);
        configureWebDriver(driver);
        return driver;
    }

    protected DesiredCapabilities updateDesiredCapabilities(DesiredCapabilities desiredCapabilities)
    {
        return desiredCapabilities;
    }

    protected void configureWebDriver(WebDriver webDriver)
    {
    }

    @Override
    public DesiredCapabilities getSeleniumGridDesiredCapabilities()
    {
        return seleniumGridDesiredCapabilities.get();
    }

    public void setRemoteDriverUrl(URL remoteDriverUrl)
    {
        this.remoteDriverUrl = remoteDriverUrl;
    }
}
