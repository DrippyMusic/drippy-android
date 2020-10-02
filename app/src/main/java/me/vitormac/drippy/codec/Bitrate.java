package me.vitormac.drippy.codec;

public enum Bitrate {

    B64K("64K"),
    B96K("96K"),
    B128K("128K");

    private final String name;

    Bitrate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
