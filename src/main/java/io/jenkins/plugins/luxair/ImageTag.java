package io.jenkins.plugins.luxair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonNull;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class ImageTag {

    private static final Logger logger = Logger.getLogger(ImageTag.class.getName());

    public static List<String> getTags(String image, String registry, String user, String password) {

        String token = getAuthToken(registry, image, user, password);
        List<String> tags = getImageTagsFromRegistry(image, registry, token);
        return tags;
    }

    private static String getAuthToken(String registry, String image, String user, String password) {

        String token = null;
        String url = "https://" + registry + "/v2/token";
        HttpResponse<JsonNode> response = Unirest.get(url)
            .queryString("service", registry)
            .queryString("scope", "repository:" + image + ":pull")
            .asJson();
        if (response.isSuccess()) {
            token = response.getBody().getObject().getString("token");
        }

        logger.log(Level.INFO, token);
        return token;
    }

    private static List<String> getImageTagsFromRegistry(String image, String registry, String token) {
        List<String> tags = new ArrayList<>();
        String url = "https://" + registry + "/v2/{image}/tags/list";
        logger.log(Level.INFO, url);
        HttpResponse<JsonNode> response = Unirest.get(url)
            .header("Authorization", "Bearer " + token)
            .routeParam("image", image)
            .asJson();
        logger.log(Level.INFO, response.getStatusText());
        if (response.isSuccess()) {
            response.getBody().getObject().getJSONArray("tags").forEach(item -> {
                logger.log(Level.INFO, "- " + item.toString());
                tags.add(image + ":" + item.toString());
            });
        }

        return tags;
    }
}