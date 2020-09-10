package me.vitormac.drippy.providers.model;

import javax.crypto.spec.SecretKeySpec;

public class DeezerData extends DataModel {

    private SecretKeySpec key;

    private int size;

    public SecretKeySpec getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = new SecretKeySpec(key.getBytes(), "Blowfish");
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
