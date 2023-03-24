package com.bing.test2.render;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.bing.test2.R;
import com.bing.test2.program.ColorShaderProgram2;
import com.bing.test2.bean.Mallet2;
import com.bing.test2.bean.Puck;
import com.bing.test2.bean.Table;
import com.bing.test2.program.TextureShaderProgram;
import com.bing.test2.util.MatrixHelper;
import com.bing.test2.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HockeyRender3 implements GLSurfaceView.Renderer {
    private final float[] projectionMatrix = new float[16];//投影矩阵
    private final float[] modelMatrix = new float[16];//模型矩阵
    private final float[] viewMatrix = new float[16];//视图矩阵
    private final float[] viewProjectionMatrix = new float[16];//projectionMatrix * viewMatrix
    private final float[] modelViewProjectionMatrix = new float[16];//viewProjectionMatrix * modelMatrix
    private final Context context;
    private Table table;
    private Mallet2 mallet;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram2 colorProgram;
    private int texture;
    private Puck puck;


    public HockeyRender3(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f,0.0f,0.0f,0.0f);
        table = new Table();
        mallet = new Mallet2(0.08f,0.15f,32);
        puck = new Puck(0.06f, 0.02f, 32);
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram2(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        //设置视口尺寸
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix,45,(float) width/height,1f,10f);
        //初始化视图矩阵
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);

        mallet.draw();

        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 1f, 0f);
        puck.bindData(colorProgram);
        puck.draw();

    }
    private void positionTableInScene() {
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }


}
