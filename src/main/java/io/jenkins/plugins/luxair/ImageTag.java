package io.jenkins.plugins.luxair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;


public class ImageTag {

    private static final Logger logger = Logger.getLogger(ImageTag.class.getName());

    public static List<String> getTags(String image, String registry, String filter, String user, String password) {

        String[] authService = getAuthService(registry);
        String realm = authService[0];
        String service = authService[1];
        String token = getAuthToken(realm, service, image, user, password);
        List<String> tags = getImageTagsFromRegistry(image, registry, token);
        return tags.stream().filter(tag -> tag.matches(filter))
            .map(tag -> image + ":" + tag)
            .collect(Collectors.toList());
    }

    private static String[] getAuthService(String registry) {

        String[] rtn = new String[2];
        rtn[0] = "";
        rtn[1] = "";
        String url = registry + "/v2/";
        String headerValue = Unirest.get(url).asEmpty()
            .getHeaders().getFirst("Www-Authenticate");
        String pattern = "Bearer realm=\"(\\S+)\",service=\"(\\S+)\"";
        Matcher m = Pattern.compile(pattern).matcher(headerValue);
        if (m.find()) {
            logger.log(Level.INFO, "realm: " + m.group(1));
            rtn[0] = m.group(1);
            logger.log(Level.INFO, "service: " + m.group(2));
            rtn[1] = m.group(2);
        }
        return rtn;
    }

    private static String getAuthToken(String realm, String service, String image, String user, String password) {

        String token = "";
        GetRequest request = Unirest.get(realm);
        if (!user.isEmpty() && !password.isEmpty()) {
            request = request.basicAuth(user, password);
        }
        HttpResponse<JsonNode> response = request 
            .queryString("service", service)
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
        String url = registry + "/v2/{image}/tags/list";
        logger.log(Level.INFO, url);
        HttpResponse<JsonNode> response = Unirest.get(url)
            .header("Authorization", "Bearer " + token)
            .routeParam("image", image)
            .asJson();
        logger.log(Level.INFO, response.getStatusText());
        if (response.isSuccess()) {
            response.getBody().getObject().getJSONArray("tags").forEach(item -> {
                tags.add(item.toString());
            });
        }
        Collections.sort(tags, Collections.reverseOrder());

        return tags;
    }
}
