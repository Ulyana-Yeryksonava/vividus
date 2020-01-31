package org.vividus.selenium;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;

import com.browserup.bup.client.ClientUtil;

import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.vividus.bdd.context.IBddRunContext;
import org.vividus.bdd.model.MetaWrapper;
import org.vividus.bdd.model.RunningStory;
import org.vividus.proxy.IProxy;
import org.vividus.selenium.manager.IWebDriverManagerContext;
import org.vividus.selenium.manager.WebDriverManagerParameter;

public abstract class AbstractVividusDriverFactory implements IVividusDriverFactory
{
    @Inject private IWebDriverFactory webDriverFactory;
    @Inject private IBddRunContext bddRunContext;
    @Inject private IWebDriverManagerContext webDriverManagerContext;
    @Inject private IProxy proxy;

    private boolean remote;

    @Override
    public VividusWebDriver create()
    {
        VividusWebDriver vividusWebDriver = createVividusWebDriver(bddRunContext.getRunningStory());

        DesiredCapabilities desiredCapabilities = vividusWebDriver.getDesiredCapabilities();
        if (proxy.isStarted())
        {
            desiredCapabilities.setCapability(CapabilityType.PROXY, createSeleniumProxy());
            desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        }
        configureVividusWebDriver(vividusWebDriver);
        return vividusWebDriver;
    }

    protected abstract void configureVividusWebDriver(VividusWebDriver vividusWebDriver);

    private Proxy createSeleniumProxy()
    {
        try
        {
            return ClientUtil.createSeleniumProxy(proxy.getProxyServer(),
                    remote ? InetAddress.getLocalHost() : InetAddress.getLoopbackAddress());
        }
        catch (UnknownHostException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private VividusWebDriver createVividusWebDriver(RunningStory runningStory)
    {
        VividusWebDriver vividusWebDriver = new VividusWebDriver();
        setBaseDesiredCapabilities(vividusWebDriver, runningStory);

        return vividusWebDriver;
    }

    private void setBaseDesiredCapabilities(VividusWebDriver vividusWebDriver, RunningStory runningStory)
    {
        DesiredCapabilities desiredCapabilities = vividusWebDriver.getDesiredCapabilities();
        desiredCapabilities.merge(webDriverManagerContext.getParameter(
                WebDriverManagerParameter.DESIRED_CAPABILITIES));
        webDriverManagerContext.reset(WebDriverManagerParameter.DESIRED_CAPABILITIES);
        if (runningStory != null)
        {
            Scenario scenario = runningStory.getRunningScenario().getScenario();
            Meta mergedMeta = mergeMeta(runningStory.getStory(), scenario);
            MetaWrapper metaWrapper = new MetaWrapper(mergedMeta);
            ControllingMetaTag.setDesiredCapabilitiesFromMeta(desiredCapabilities, metaWrapper);
            setDesiredCapabilities(desiredCapabilities, runningStory, scenario, metaWrapper);
        }
    }

    protected void setDesiredCapabilities(DesiredCapabilities desiredCapabilities, RunningStory runningStory,
            Scenario scenario, MetaWrapper metaWrapper)
    {
    }

    private static Meta mergeMeta(Story story, Scenario scenario)
    {
        Meta storyMeta = story.getMeta();
        return scenario == null ? storyMeta : scenario.getMeta().inheritFrom(storyMeta);
    }

    protected IWebDriverFactory getWebDriverFactory()
    {
        return webDriverFactory;
    }

    public void setRemote(boolean remote)
    {
        this.remote = remote;
    }
}
