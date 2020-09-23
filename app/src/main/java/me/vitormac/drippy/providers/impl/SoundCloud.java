package me.vitormac.drippy.providers.impl;

import com.google.gson.JsonObject;

import me.vitormac.drippy.providers.ProviderBase;
import me.vitormac.drippy.providers.model.DataModel;

public class SoundCloud extends ProviderBase<DataModel> {

    public SoundCloud(JsonObject data, String id) {
        super(data, id);
    }

    @Override
    protected String getMimeType() {
        return "audio/mpeg";
    }

    @Override
    protected DataModel map(JsonObject object) {
        DataModel data = new DataModel();
        data.setUri(object.get("uri").getAsString());
        return data;
    }

}
