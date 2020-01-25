package org.vividus.bdd.steps;

import java.util.List;

import org.jbehave.core.annotations.Given;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.vividus.selenium.IWebDriverProvider;
import org.vividus.selenium.SauceLabsCapabilityType;
import org.vividus.selenium.manager.IWebDriverManagerContext;
import org.vividus.selenium.manager.WebDriverManagerParameter;
import org.vividus.selenium.model.DesiredCapability;

import io.appium.java_client.InteractsWithApps;

public class ApplicationSteps
{
    private IWebDriverProvider webDriverProvider;
    private IWebDriverManagerContext webDriverManagerContext;

    public ApplicationSteps(IWebDriverProvider webDriverProvider, IWebDriverManagerContext webDriverManagerContext)
    {
        this.webDriverProvider = webDriverProvider;
        this.webDriverManagerContext = webDriverManagerContext;
    }

    @Given("I run the application located at `$location` and capabilities: $capabilities")
    public void runApplicationAtLocation(String location, List<DesiredCapability> capabilities)
    {
        DesiredCapabilities desiredCapabilities = webDriverManagerContext
                .getParameter(WebDriverManagerParameter.DESIRED_CAPABILITIES);
        desiredCapabilities.setCapability(SauceLabsCapabilityType.APP, location);
        capabilities.forEach(c -> desiredCapabilities.setCapability(c.getCapabilityName(), c.getValue()));
        webDriverProvider.getUnwrapped(InteractsWithApps.class);
    }
}
