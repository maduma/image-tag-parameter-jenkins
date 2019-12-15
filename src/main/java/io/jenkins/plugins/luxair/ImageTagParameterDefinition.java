package io.jenkins.plugins.luxair;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;



public class ImageTagParameterDefinition extends SimpleParameterDefinition {

    private static final Logger logger = Logger.getLogger(ImageTagParameterDefinition.class.getName());

    private String image;
    private String registry;
    private String filter;
    private String credentialId;

    @DataBoundConstructor
    public ImageTagParameterDefinition(String name, String description, String image, String registry, String filter, String credentialId) {
        super(name, description);
        this.image = image;
        this.registry = registry;
        if (filter.isEmpty()) {
            this.filter = getDefaultFilter();
        } else {
            this.filter = filter;
        }
        this.credentialId = credentialId;
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

    public String getCredentialId() {
        return credentialId;
    }

    public List<String> getTags() {
        List<String> imageTags;
        String user = "";
        String password = "";

        StandardUsernamePasswordCredentials c = CredentialsMatchers.firstOrNull(
            CredentialsProvider.lookupCredentials(
                StandardUsernamePasswordCredentials.class,
                Jenkins.getInstanceOrNull().getItemGroup(),
                ACL.SYSTEM,
                new DomainRequirement()
            ),
            CredentialsMatchers.withId(credentialId)
        );
        if (c != null) {
            user = c.getUsername();
            password = c.getPassword().getPlainText();
        } else {
            logger.log(Level.INFO, "Cannot find credential for : " + credentialId);
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

        public ListBoxModel doFillCredentialIdItems(@AncestorInPath Item context,
                                                    @QueryParameter String credentialId,
                                                    @QueryParameter String remote) {
            logger.log(Level.INFO, "context:" + context + ":");
            logger.log(Level.INFO, "credentialId:" + credentialId + ":");
            logger.log(Level.INFO, "remote:" + remote + ":");
            logger.log(Level.INFO, "Auth:" + Jenkins.getAuthentication() + ":");
            if (context == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER) ||
                context != null && !context.hasPermission(Item.EXTENDED_READ)) {
                logger.log(Level.INFO, "no permission to list credential");
                return new StandardListBoxModel().includeCurrentValue(credentialId);
            }
            return new StandardListBoxModel()
                    .includeEmptyValue()
                    .includeAs(ACL.SYSTEM, context, StandardUsernameCredentials.class)
                    .includeCurrentValue(credentialId);
        }

    }

}