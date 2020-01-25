package org.vividus.selenium.model;

import org.jbehave.core.annotations.AsParameters;

@AsParameters
public class DesiredCapability
{
    private String capabilityName;
    private String value;

    public String getCapabilityName()
    {
        return capabilityName;
    }

    public void setCapabilityName(String capabilityName)
    {
        this.capabilityName = capabilityName;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
