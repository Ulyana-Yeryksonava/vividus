package org.vividus.selenium;

public class VividusMobileAppDriverFactory extends AbstractVividusDriverFactory
{
    @Override
    protected void configureVividusWebDriver(VividusWebDriver vividusWebDriver)
    {
        vividusWebDriver.setWebDriver(getWebDriverFactory()
                .getRemoteWebDriver(vividusWebDriver.getDesiredCapabilities()));
        vividusWebDriver.setRemote(true);
    }
}
