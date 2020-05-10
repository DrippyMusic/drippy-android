package me.vitormac.drippy;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.Locale;

public class Utils {

    public static Drawable getButtonIcon(Context context, boolean playing) {
        if (playing) {
            return context.getDrawable(R.drawable.ic_pause);
        }
        return context.getDrawable(R.drawable.ic_play_arrow);
    }

    public static String format(int seconds) {
        int minutes = seconds / 60, period = seconds - (minutes * 60);
        return String.format(Locale.US, "%02d:%02d", minutes, period);
    }

}
