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

package org.vividus.selenium.manager;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.remote.BrowserType;
import org.vividus.selenium.BrowserWindowSize;
import org.vividus.selenium.IWebDriverProvider;

@ExtendWith(MockitoExtension.class)
class WebDriverManagerTests
{
    @Mock
    private IWebDriverProvider webDriverProvider;

    @InjectMocks
    private WebDriverManager webDriverManager;

    private WebDriver mockWebDriver(Class<? extends WebDriver> webDriverClass, MockSettings mockSettings)
    {
        WebDriver webDriver = mock(webDriverClass, mockSettings);
        when(webDriverProvider.get()).thenReturn(webDriver);
        return webDriver;
    }

    private Options mockOptions(WebDriver webDriver)
    {
        Options options = mock(Options.class);
        when(webDriver.manage()).thenReturn(options);
        return options;
    }

    @Test
    void testResizeWebDriverWithDesiredBrowserSize()
    {
        WebDriver webDriver = mockWebDriver(WebDriver.class, withSettings().extraInterfaces(HasCapabilities.class));
        when(webDriverProvider.get()).thenReturn(webDriver);
        BrowserWindowSize browserWindowSize = mock(BrowserWindowSize.class);
        when(((HasCapabilities) webDriver).getCapabilities()).thenReturn(mock(Capabilities.class));
        Window window = mock(Window.class);
        when(mockOptions(webDriver).window()).thenReturn(window);
        Dimension dimension = mock(Dimension.class);
        when(browserWindowSize.toDimension()).thenReturn(dimension);
        webDriverManager.resize(browserWindowSize);
        verify(window).setSize(dimension);
    }

    @Test
    void testResizeWebDriverWithNullBrowserSize()
    {
        WebDriver webDriver = mockWebDriver(WebDriver.class, withSettings().extraInterfaces(HasCapabilities.class));
        when(webDriverProvider.get()).thenReturn(webDriver);
        Window window = mock(Window.class);
        when(mockOptions(webDriver).window()).thenReturn(window);
        webDriverManager.resize(null);
        verify(window).maximize();
    }

    @Test
    void testResizeWebDriverWithNullBrowserSizeChrome()
    {
        WebDriverManager spy = Mockito.spy(webDriverManager);
        WebDriver webDriver = mockWebDriver(WebDriver.class, withSettings().extraInterfaces(HasCapabilities.class));
        when(webDriverProvider.get()).thenReturn(webDriver);
        Window window = mock(Window.class);
        when(mockOptions(webDriver).window()).thenReturn(window);
        Dimension maxSize = new Dimension(1920, 1200);
        when(window.getSize()).thenReturn(maxSize);
        doReturn(true).when(spy).isBrowserAnyOf(BrowserType.CHROME);
        spy.resize(null);
        verify(window).maximize();
        verify(window).setSize(maxSize);
    }
}
