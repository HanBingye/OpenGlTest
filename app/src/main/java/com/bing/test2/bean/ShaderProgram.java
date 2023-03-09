package com.bing.test2.bean;

import android.content.Context;
import android.opengl.GLES20;

import com.bing.test2.util.ResourceUtils;
import com.bing.test2.util.ShaderHelper;

abstract class ShaderProgram {
    protected static final String U_COLOR = "u_Color";
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected final int program;

    public ShaderProgram(Context context,int vertexResourceID,int fragmentResourceID){
        program = ShaderHelper.buildProgram(ResourceUtils.readTextFileFromResource(context, vertexResourceID),
                ResourceUtils.readTextFileFromResource(context, fragmentResourceID));
    }
    public void useProgram(){
        GLES20.glUseProgram(program);
    }
}
