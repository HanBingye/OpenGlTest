package com.bing.test2.util;

public class MatrixHelper {
    public static void perspectiveM(float[] m, float yFov, float aspect, float n, float f) {
        //把角度转化为弧度
        float radians = (float) (yFov * Math.PI / 180.0);
        //计算焦距
        float a = (float) (1.0 / (Math.tan(radians / 2.0)));
        //一次写入矩阵的一列
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }

}
