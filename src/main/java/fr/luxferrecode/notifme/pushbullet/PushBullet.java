package fr.luxferrecode.notifme.pushbullet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PushBullet {

    private static final String BASEURI = "https://api.pushbullet.com/v2/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String APIKEY;
    private boolean isValid = false;

    public PushBullet(String apikey) throws InvalidApiKeyException {
        this.APIKEY = apikey;
        if(this.APIKEY == "TEST") {
            this.isValid = true;
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASEURI + "users/me"))
                .GET()
                .setHeader("Access-Token", this.APIKEY)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) isValid = true;
            else throw new InvalidApiKeyException("Invalid API Key");
        } catch(InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public boolean push(String title, String text) {
        if(!this.isValid) return false;
        JSONObject json = new JSONObject();
        try {
            json.add("type", "note");
            json.add("title", title);
            json.add("body", text);
        } catch(Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASEURI + "pushes"))
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .setHeader("Access-Token", this.APIKEY)
                .setHeader("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch(InterruptedException | IOException e) {
            System.out.println("Error: " + e);
            return false;
        }
    }

    public String getApiKey() {
        return APIKEY;
    }
}
