package com.bing.test2.bean;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.bing.test2.Constants.BYTES_PRE_FLOAT;

public class Mallet {
    private static final int POSITION_COUNT = 2;
    private static final int COLOR_COUNT = 3;
    private static final int STRIDE = (POSITION_COUNT+COLOR_COUNT)*BYTES_PRE_FLOAT;
    private static final float[] vertexData = {
            //x,y,r,g,b
            0f, -0.4f,0f,1f,0f,
            0f, 0.4f,0f,0f,1f
    };
    private final VertexArray vertexArray;

    public Mallet(){
        vertexArray = new VertexArray(vertexData);
    }
    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(0,colorShaderProgram.getPositionAttributeLocation(),POSITION_COUNT,STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COUNT,colorShaderProgram.getColorAttributeLocation(),COLOR_COUNT,STRIDE);
    }
    public void draw(){
        glDrawArrays(GL_POINTS,0,2);
    }
}
