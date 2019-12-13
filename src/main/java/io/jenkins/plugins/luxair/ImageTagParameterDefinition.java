package io.jenkins.plugins.luxair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;

public class ImageTagParameterDefinition extends SimpleParameterDefinition {

    private static final Logger LOGGER = Logger.getLogger(ImageTagParameterDefinition.class.getName());

    private String image;
    private String registry;
    private String credentialid;

    @DataBoundConstructor
    public ImageTagParameterDefinition(String name, String description, String image, String registry, String credentialid) {
        super(name, description);
        this.image = image;
        this.registry = registry;
        this.credentialid = credentialid;
    }

    public String getImage() {
        return image;
    }

    public String getRegistry() {
        return registry;
    }

    public String getCredentialid() {
        return credentialid;
    }

    public List<String> getDisplayableTags() {
        List<String> imageTags;
        imageTags = new ArrayList<String>();
        imageTags.add(registry + "/" + image + ":" + "v0.1");
        imageTags.add(registry + "/" + image + ":" + "v0.2");
        return imageTags;
    }
    /**
     *
     */
    private static final long serialVersionUID = 3938123092372L;

    @Override
    public ParameterValue createValue(String value) {
        return new ImageTagParameterValue(getName(), getDescription(), value);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue paramValue = req.bindJSON(StringParameterValue.class, jo);
        return new ImageTagParameterValue(paramValue.getName(), paramValue.getDescription(), (String)paramValue.getValue());
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Image Tag Parameter";
        }        
    }

}