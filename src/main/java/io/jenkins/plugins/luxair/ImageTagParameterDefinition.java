package io.jenkins.plugins.luxair;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import java.util.List;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;



public class ImageTagParameterDefinition extends SimpleParameterDefinition {

    private static final Logger logger = Logger.getLogger(ImageTagParameterDefinition.class.getName());

    private String image;
    private String registry;
    private String filter;
    private String credentialid;

    @DataBoundConstructor
    public ImageTagParameterDefinition(String name, String description, String image, String registry, String filter, String credentialid) {
        super(name, description);
        this.image = image;
        this.registry = registry;
        if (filter.isEmpty()) {
            this.filter = getDefaultFilter();
        } else {
            this.filter = filter;
        }
        this.credentialid = credentialid;
    }

    public String getImage() {
        return image;
    }

    public String getRegistry() {
        return registry;
    }

    public String getFilter() {
        return filter;
    }

    public String getDefaultFilter() {
        return ".*";
    }

    public String getCredentialid() {
        return credentialid;
    }

    public List<String> getTags() {
        List<String> imageTags;
        String user = "";
        String password = "";
        if(credentialid.split(":").length == 2) {
            user = credentialid.split(":")[0];
            password = credentialid.split(":")[1];
        }
        imageTags = ImageTag.getTags(image, registry, filter, user, password);
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