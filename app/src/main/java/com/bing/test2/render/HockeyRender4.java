package com.bing.test2.render;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.bing.test2.R;
import com.bing.test2.bean.ColorShaderProgram2;
import com.bing.test2.bean.Mallet2;
import com.bing.test2.bean.Puck;
import com.bing.test2.bean.Table;
import com.bing.test2.bean.TextureShaderProgram;
import com.bing.test2.util.Geometry;
import com.bing.test2.util.Geometry.Point;
import com.bing.test2.util.Geometry.Ray;
import com.bing.test2.util.Geometry.Sphere;
import com.bing.test2.util.Geometry.Plane;
import com.bing.test2.util.Geometry.Vector;
import com.bing.test2.util.MatrixHelper;
import com.bing.test2.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HockeyRender4 implements GLSurfaceView.Renderer {
    private final float[] projectionMatrix = new float[16];//投影矩阵
    private final float[] modelMatrix = new float[16];//模型矩阵
    private final float[] viewMatrix = new float[16];//视图矩阵
    private final float[] viewProjectionMatrix = new float[16];//projectionMatrix * viewMatrix
    private final float[] modelViewProjectionMatrix = new float[16];//viewProjectionMatrix * modelMatrix
    private final float[] invertedViewProjectionMatrix = new float[16];//反转矩阵
    private final Context context;
    private Table table;
    private Mallet2 mallet;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram2 colorProgram;
    private int texture;
    private Puck puck;
    private boolean malletPressed = false;//跟踪木槌是否被按到
    private Point malletPosition;//存储木槌的位置


    public HockeyRender4(Context context) {
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
        malletPosition = new Point(0f,mallet.height/2f,0.4f);

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
        //取消掉视图矩阵和投影矩阵的效果
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

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

        positionObjectInScene(malletPosition.x, malletPosition.y, malletPosition.z);
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

    public void handlePress(float normalizedX, float normalizedY) {
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Sphere sphere = new Sphere(new Point(malletPosition.x, malletPosition.y, malletPosition.z), mallet.height / 2);
        malletPressed = Geometry.intersects(sphere,ray);
    }

    public void handleMove(float normalizedX, float normalizedY) {
        if(malletPressed){
            Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
            Point point = Geometry.intersectionPoint(ray, plane);
            malletPosition = new Point(point.x,mallet.height/2,point.z);
        }
    }

    /**
     *  把二维触碰的点转换为两个三维坐标
     * @param normalizedX
     * @param normalizedY
     * @return
     */
    private Ray convertNormalized2DPointToRay(
            float normalizedX, float normalizedY) {

        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc =  {normalizedX, normalizedY,  1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        //得到世界空间中的坐标
        multiplyMV(
                nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        //撤销透视除法的影响
        divideByW(nearPointWorld);
        divideByW(farPointWorld);


        Point nearPointRay =
                new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);

        Point farPointRay =
                new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Ray(nearPointRay,
                Geometry.vectorBetween(nearPointRay, farPointRay));
    }
    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }
}
