package io.jenkins.plugins.luxair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;

public class ImageTagParameterDefinition extends SimpleParameterDefinition {

    private static final Logger logger = Logger.getLogger(ImageTagParameterDefinition.class.getName());

    private String image;
    private String registry;
    private String credentialid;

    @DataBoundConstructor
    public ImageTagParameterDefinition(String name, String description, String image, String registry, String credentialid) {
        super(name, description);
        this.image = image;
        this.registry = registry;
        this.credentialid = credentialid;
        logger.log(Level.INFO, "Object ImageTagParameterDefinition created.");
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

    public List<String> getTags() {
        List<String> imageTags;
        String user = null;
        String password = null;
        imageTags = ImageTag.getTags(image, registry, user, password);
        return imageTags;
    }

    private static final long serialVersionUID = 3938123092372L;

    @Override
    public ParameterValue createValue(String value) {
        return new StringParameterValue(getName(), value, getDescription());
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        return req.bindJSON(StringParameterValue.class, jo);
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Image Tag Parameter";
        }        
    }

}