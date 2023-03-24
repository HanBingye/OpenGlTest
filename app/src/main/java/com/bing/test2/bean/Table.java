package com.bing.test2.bean;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.bing.test2.Constants.BYTES_PRE_FLOAT;

import com.bing.test2.program.TextureShaderProgram;

public class Table {
    private static final int POSITION_COUNT = 2;
    private static final int TEXTURE_COUNT = 2;
    private static final int STRIDE = (POSITION_COUNT+TEXTURE_COUNT)*BYTES_PRE_FLOAT;
    private static final float[] vertexData = {
            //x,y,S,T  S和T是纹理坐标
            0f,0f,       0.5f,0.5f,
            -0.5f,-0.8f, 0f,0.9f ,
            0.5f,-0.8f,  1f,0.9f,
            0.5f,0.8f,   1f,0.1f,
            -0.5f,0.8f,  0f,0.1f,
            -0.5f,-0.8f, 0f,0.9f
    };
    private final VertexArray vertexArray;

    public Table(){
        vertexArray = new VertexArray(vertexData);
    }
    public void bindData(TextureShaderProgram textureShaderProgram){
        vertexArray.setVertexAttribPointer(0,textureShaderProgram.getPositionAttributeLocation(),POSITION_COUNT,STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COUNT,textureShaderProgram.getTextureAttributeLocation(),TEXTURE_COUNT,STRIDE);
    }
    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
    }
}
