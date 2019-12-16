package io.jenkins.plugins.luxair;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
//import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.Extension;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.security.ACL;
import hudson.util.ListBoxModel;

import java.util.ArrayList;
import java.util.Collections;
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

        if (!credentialId.isEmpty()) {
            final List<StandardUsernamePasswordCredentials> allcreds = new ArrayList<>();
            Jenkins.get().getItemGroup().getAllItems(Folder.class).forEach(it -> {
                List<StandardUsernamePasswordCredentials> creds;
                creds = CredentialsProvider.lookupCredentials(
                    StandardUsernamePasswordCredentials.class, (Item) it, ACL.SYSTEM,
                    Collections.emptyList());
                allcreds.addAll(creds);
                creds.forEach(cred -> { 
                    logger.log(Level.INFO, "- :" + cred.getId() + "");
                });
            });

            StandardUsernamePasswordCredentials c = CredentialsMatchers.firstOrNull(
                allcreds, CredentialsMatchers.withId(credentialId));
            if (c != null) {
                user = c.getUsername();
                password = c.getPassword().getPlainText();
            } else {
                logger.log(Level.INFO, "Cannot find credential for :" + credentialId + ":");
            }

        } else {
            logger.log(Level.INFO, "CredentialId is empty");
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
                                                    @QueryParameter String registry) {
            logger.log(Level.INFO, "context :" + context + ":");
            logger.log(Level.INFO, "credentialId :" + credentialId + ":");
            logger.log(Level.INFO, "registry :" + registry + ":");
            logger.log(Level.INFO, "Authentication :" + Jenkins.getAuthentication() + ":");
            if (context == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER) ||
                context != null && !context.hasPermission(Item.EXTENDED_READ)) {
                logger.log(Level.INFO, "No permission to list credential");
                return new StandardListBoxModel().includeCurrentValue(credentialId);
            }
            return new StandardListBoxModel()
                    .includeEmptyValue()
                    .includeAs(ACL.SYSTEM, context, StandardUsernameCredentials.class)
                    .includeCurrentValue(credentialId);
        }

    }

}