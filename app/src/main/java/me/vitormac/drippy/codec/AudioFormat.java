package me.vitormac.drippy.codec;

public enum AudioFormat {

    AAC("aac"),
    MP3("mp3");

    private final String name;

    AudioFormat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
