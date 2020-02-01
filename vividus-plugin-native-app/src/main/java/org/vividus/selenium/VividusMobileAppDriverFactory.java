package org.vividus.selenium;

public class VividusMobileAppDriverFactory extends AbstractVividusDriverFactory
{
    private final IDriverFactory driverFactory;

    public VividusMobileAppDriverFactory(IDriverFactory driverFactory)
    {
        this.driverFactory = driverFactory;
    }

    @Override
    protected void configureVividusWebDriver(VividusWebDriver vividusWebDriver)
    {
        vividusWebDriver.setWebDriver(driverFactory.getRemoteWebDriver(vividusWebDriver.getDesiredCapabilities()));
        vividusWebDriver.setRemote(true);
    }
}
