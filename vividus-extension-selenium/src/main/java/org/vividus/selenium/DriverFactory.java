/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
