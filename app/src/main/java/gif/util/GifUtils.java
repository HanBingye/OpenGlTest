package gif.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

public class GifUtils {
    public static Bitmap getBitmapArrayByGif(Context context, String assertPath, int index) {
        try {
            ArrayList<Bitmap> list = new ArrayList<>();
            GifDrawable gifFromAssets = new GifDrawable(context.getAssets(), assertPath);//代表android中assert的gif文件名
            int totalCount = gifFromAssets.getNumberOfFrames();
            Log.d("hanbing", "totalCount="+totalCount);
            if (totalCount < index) {
                index = totalCount - 1;
            }
            return gifFromAssets.seekToFrameAndGet(index);
        } catch (Exception e) {
            return null;
        }
    }
}
