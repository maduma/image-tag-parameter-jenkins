package io.jenkins.plugins.luxair;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;

public class ImageTagParameterDefinition extends SimpleParameterDefinition {

    private List<String> imageTags;

    @DataBoundConstructor
    public ImageTagParameterDefinition(String name, String description, String image) {
        super(name, description);
        this.imageTags = new ArrayList<String>();
        this.imageTags.add("v0.1");
        this.imageTags.add("v0.2");
    }

    public List<String> getDisplayableTags() {
        return this.imageTags;
    }
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public ParameterValue createValue(String value) {
        return new ImageTagParameterValue(getName(), getDescription(), value);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue paramValue = req.bindJSON(StringParameterValue.class, jo);
        return new ImageTagParameterValue(paramValue.getName(), getDescription(), (String)paramValue.getValue());
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Image Tag Parameter";
        }        
    }

}