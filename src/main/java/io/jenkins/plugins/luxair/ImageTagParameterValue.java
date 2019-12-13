
package io.jenkins.plugins.luxair;


import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.StringParameterValue;

public class ImageTagParameterValue extends StringParameterValue {

    private static final long serialVersionUID = 19934923473827424L;
    private String value;

    @DataBoundConstructor
    public ImageTagParameterValue(String name, String value, String description) {
        super(name, description);
        this.setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}