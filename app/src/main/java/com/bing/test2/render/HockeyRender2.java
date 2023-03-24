package com.bing.test2.render;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glViewport;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.bing.test2.R;
import com.bing.test2.program.ColorShaderProgram;
import com.bing.test2.bean.Mallet;
import com.bing.test2.bean.Table;
import com.bing.test2.program.TextureShaderProgram;
import com.bing.test2.util.MatrixHelper;
import com.bing.test2.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HockeyRender2 implements GLSurfaceView.Renderer {
    private final float[] projectionMatrix = new float[16];//投影矩阵
    private final float[] modelMatrix = new float[16];//模型矩阵
    private final Context context;
    private Table table;
    private Mallet mallet;
    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;
    private int texture;


    public HockeyRender2(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f,0.0f,0.0f,0.0f);
        table = new Table();
        mallet = new Mallet();
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        //设置视口尺寸
        glViewport(0, 0, width, height);
        float aspect = width > height?
                (float) width/(float) height:
                (float) height/(float) width;
        /*//适应宽高比
        if(width > height){
            //生成正交投影
            Matrix.orthoM(matrix,0,-aspect,aspect,-1,1,-1,1);
        }else{
            Matrix.orthoM(matrix,0,-1,1,-aspect,aspect,-1,1);
        }*/
        //使用投影矩阵,视椎体从-1位置开始，在-10位置结束
        MatrixHelper.perspectiveM(projectionMatrix,45,(float) width/height,1f,10f);
        //将模型矩阵设置为单位矩阵
        Matrix.setIdentityM(modelMatrix,0);
        //沿着z轴平移-3
        Matrix.translateM(modelMatrix,0,0f,0f,-3f);
        Matrix.rotateM(modelMatrix,0,-60f,1f,0f,0f);
        //创建临时变量
        float[] temp = new float[16];
        //矩阵相乘，注意顺序，先projectionMatrix，后modelMatrix
        Matrix.multiplyMM(temp,0,projectionMatrix,0,modelMatrix,0);
        //将结果存回projectionMatrix
        System.arraycopy(temp,0,projectionMatrix,0,temp.length);


    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GL_COLOR_BUFFER_BIT);
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectionMatrix,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

    }
}
