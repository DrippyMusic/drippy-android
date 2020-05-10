package me.vitormac.drippy;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Utils {

    public static Drawable getButtonIcon(Context context, boolean playing) {
        if (playing) {
            return context.getDrawable(R.drawable.ic_pause);
        }
        return context.getDrawable(R.drawable.ic_play_arrow);
    }


}
