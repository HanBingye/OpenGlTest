package com.bing.test2.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int res) {
        int[] textureId = new int[1];
        //创建纹理对象
        GLES20.glGenTextures(1, textureId, 0);
        if (textureId[0] == 0) {
            Log.e(TAG, "glGenTextures failed");
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), res, options);
        if (bitmap == null) {
            Log.e(TAG, "resource" + res + "could not be decode");
            GLES20.glDeleteTextures(1, textureId, 0);
            return 0;
        }
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        /**
         * 设置过滤器
         * GL_TEXTURE_MIN_FILTER 缩小
         * GL_TEXTURE_MAG_FILTER 放大
         * GL_LINEAR_MIPMAP_LINEAR 三线性过滤
         * GL_LINEAR 双线性过滤
         */
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //加载位图数据到opengl
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        //生成MIP贴图
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        //释放数据
        bitmap.recycle();
        //传递 0 解除绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureId[0];
    }
}
