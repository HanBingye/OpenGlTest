package com.bing.test2.util;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        //创建一个新的着色器对象
        int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId == 0) {
            Log.e(TAG, "could not create shader");
            return 0;
        }
        //上传着色器源代码
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        //编译着色器源代码
        GLES20.glCompileShader(shaderObjectId);
        int[] compileStatus = new int[1];
        //检查编译状态
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId);
            Log.e(TAG, "compileShader failed:\n"+shaderCode);
            return 0;
        }
        return shaderObjectId;
    }

    public static int linkProgram(int vertexShader, int fragmentShader) {
        //新建程序对象
        int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            Log.e(TAG, "could not create program");
            return 0;
        }
        //附上着色器
        GLES20.glAttachShader(programObjectId, vertexShader);
        GLES20.glAttachShader(programObjectId, fragmentShader);
        //链接程序
        GLES20.glLinkProgram(programObjectId);
        int[] linkStatus = new int[1];
        //检查链接状态
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programObjectId);
            Log.e(TAG, "linkProgram  failed");
            return 0;
        }
        return programObjectId;
    }

    public static boolean validateProgram(int program) {
        //验证程序是否有效
        GLES20.glValidateProgram(program);
        int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        return validateStatus[0] != 0;
    }
    public static int buildProgram(String vertexResource,String fragmentResource ){
        int vertexShader = ShaderHelper.compileVertexShader(vertexResource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentResource);
        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        ShaderHelper.validateProgram(program);
        return program;
    }
}
