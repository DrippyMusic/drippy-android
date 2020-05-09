package me.vitormac.drippy.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class LoaderTask extends AsyncTask<String, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private final ImageView view;

    public LoaderTask(ImageView view) {
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try (BufferedInputStream stream = new BufferedInputStream(new URL(strings[0]).openStream())) {
            return BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        this.view.setImageBitmap(bitmap);
    }

}