package io.jenkins.plugins.luxair;

import java.util.ArrayList;
import java.util.List;

public class ImageTag {
    
    public static List<String> getTags(String image, String registry, String user, String password) {
        List<String> tags = new ArrayList<String>();
        tags.add(registry + "/" + image + ":" + "v0.1");
        tags.add(registry + "/" + image + ":" + "v0.2");
        return tags;
    }
}