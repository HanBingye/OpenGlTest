package com.bing.test2.bean;

import android.content.Context;
import android.opengl.GLES20;

import com.bing.test2.R;

public class ColorShaderProgram2 extends ShaderProgram {

    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int uColorLocation;

    public ColorShaderProgram2(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

    }



    public void setUniforms(float[] matrix,float r,float g, float b){
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        GLES20.glUniform4f(uColorLocation,r,g,b,1f);

    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

}