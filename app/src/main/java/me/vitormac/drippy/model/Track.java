package me.vitormac.drippy.model;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Track {

    private final String id;
    private final String title;
    private final String album;
    private final int duration;
    private final List<String> artists = new ArrayList<>();
    private final List<String> artworks = new ArrayList<>();

    public Track(JsonObject object) {
        this.id = object.get("id").getAsString();
        this.title = object.get("name").getAsString();
        this.album = object.get("album").getAsJsonObject().get("name").getAsString();
        this.duration = object.get("duration_ms").getAsInt() / 1000;

        for (JsonElement element : object.get("artists").getAsJsonArray()) {
            this.artists.add(element.getAsJsonObject().get("name").getAsString());
        }

        for (JsonElement element : object.get("album").getAsJsonObject().get("images").getAsJsonArray()) {
            this.artworks.add(element.getAsJsonObject().get("url").getAsString());
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    public String getArtists() {
        return Joiner.on(", ").join(this.artists);
    }

    public String getArtwork(int index) {
        return this.artworks.get(index);
    }

}
