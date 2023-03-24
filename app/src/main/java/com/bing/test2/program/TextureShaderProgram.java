package com.bing.test2.program;

import android.content.Context;
import android.opengl.GLES20;

import com.bing.test2.R;

public class TextureShaderProgram extends ShaderProgram {


    private final int uMatrixLocation;
    private final int uTextureLocation;
    private final int aPositionLocation;
    private final int aTextureLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix,int textureID){
        //传递矩阵给uMatrixLocation
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        //把活动的纹理单元设置为纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID);
        //把被选定的纹理单元传递给uTextureLocation
        GLES20.glUniform1i(uTextureLocation,0);

    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureAttributeLocation() {
        return aTextureLocation;
    }
}
