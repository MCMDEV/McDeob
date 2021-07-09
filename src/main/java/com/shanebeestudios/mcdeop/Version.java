package com.shanebeestudios.mcdeop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class Version {

    private final String name;
    private final String clientJar;
    private final String clientMappings;
    private final String serverJar;
    private final String serverMappings;

    public Version(String name, String clientJar, String clientMappings, String serverJar, String serverMappings) {
        this.name = name;
        this.clientJar = clientJar;
        this.clientMappings = clientMappings;
        this.serverJar = serverJar;
        this.serverMappings = serverMappings;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public String getClientJar() {
        return clientJar;
    }

    public String getClientMappings() {
        return clientMappings;
    }

    public String getServerJar() {
        return serverJar;
    }

    public String getServerMappings() {
        return serverMappings;
    }

    private static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    public static CompletableFuture<List<Version>> loadVersions()   {
        return CompletableFuture.supplyAsync(() -> {
            List<Version> versionList = new ArrayList<>();
            try (InputStream is = new URL(VERSION_MANIFEST).openStream(); InputStreamReader isr = new InputStreamReader(is)) {
                JsonElement jsonElement = JsonParser.parseReader(isr);
                JsonArray versions = jsonElement.getAsJsonObject().get("versions").getAsJsonArray();
                for (JsonElement version : versions) {
                    String id = version.getAsJsonObject().get("id").getAsString();
                    String url = version.getAsJsonObject().get("url").getAsString();
                    try (InputStream is2 = new URL(url).openStream(); InputStreamReader isr2 = new InputStreamReader(is2)) {
                        JsonElement jsonElement2 = JsonParser.parseReader(isr2);
                        JsonObject downloads = jsonElement2.getAsJsonObject().get("downloads").getAsJsonObject();

                        if (!downloads.has("client_mappings")) continue;
                        if (!downloads.has("server_mappings")) continue;

                        String clientJar = downloads.get("client").getAsJsonObject().get("url").getAsString();
                        String clientMappings = downloads.get("client_mappings").getAsJsonObject().get("url").getAsString();

                        String serverJar = downloads.get("server").getAsJsonObject().get("url").getAsString();
                        String serverMappings = downloads.get("server_mappings").getAsJsonObject().get("url").getAsString();

                        versionList.add(new Version(id, clientJar, clientMappings, serverJar, serverMappings));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return versionList;
        }, Executors.newSingleThreadExecutor());
    }

    public static Version getByName(List<Version> versionList, String name)    {
        return versionList.stream().filter(version -> version.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
