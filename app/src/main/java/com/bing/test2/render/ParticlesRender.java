package com.bing.test2.render;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.bing.test2.R;
import com.bing.test2.bean.Heightmap;
import com.bing.test2.bean.SkyBox;
import com.bing.test2.program.HeightmapShaderProgram;
import com.bing.test2.program.ParticleShaderProgram;
import com.bing.test2.bean.ParticleShooter;
import com.bing.test2.bean.ParticlesSystem;
import com.bing.test2.program.SkyboxShaderProgram;
import com.bing.test2.util.Geometry.Point;
import com.bing.test2.util.Geometry.Vector;
import com.bing.test2.util.MatrixHelper;
import com.bing.test2.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ParticlesRender implements GLSurfaceView.Renderer {
    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];//投影矩阵
    private final float[] viewMatrix = new float[16];//视图矩阵
    private final float[] viewMatrixForSkybox = new float[16];
    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final Context context;
    private ParticleShaderProgram particleProgram;
    private ParticlesSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;
    private int particlesTexture;
    private SkyboxShaderProgram skyboxProgram;
    private SkyBox skybox;
    private int skyBoxTexture;
    private float xRotation, yRotation;
    private HeightmapShaderProgram heightmapProgram;
    private Heightmap heightmap;

    public ParticlesRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        //使能剔除技术消除隐藏面
        glEnable(GL_CULL_FACE);
        //打开深度缓冲区功能
        glEnable(GL_DEPTH_TEST);
        //使能混合技术
        glEnable(GL_BLEND);
        //设置为累加混合
        glBlendFunc(GL_ONE, GL_ONE);
        heightmapProgram = new HeightmapShaderProgram(context);
        heightmap = new Heightmap(((BitmapDrawable) context.getResources().getDrawable(R.drawable.heightmap)).getBitmap());
        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new SkyBox();
        skyBoxTexture = TextureHelper.loadCubeMap(context,
                new int[]{R.drawable.left, R.drawable.right,
                        R.drawable.bottom, R.drawable.top,
                        R.drawable.front, R.drawable.back});
        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticlesSystem(10000);
        globalStartTime = System.nanoTime();
        Vector vector = new Vector(0f, 0.5f, 0f);
        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1f;
        redParticleShooter = new ParticleShooter(new Point(-0.9f, 0f, 0f),
                vector, Color.rgb(255, 50, 5), angleVarianceInDegrees, speedVariance);
        greenParticleShooter = new ParticleShooter(new Point(0f, 0f, 0f),
                vector, Color.rgb(25, 255, 25), angleVarianceInDegrees, speedVariance);
        blueParticleShooter = new ParticleShooter(new Point(0.9f, 0f, 0f),
                vector, Color.rgb(5, 50, 255), angleVarianceInDegrees, speedVariance);
        particlesTexture = TextureHelper.loadTexture(context, R.drawable.particles);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / height, 1f, 100f);
        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        drawHeightmap();
        drawSkyBox();
        drawParticles();

    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);

        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapProgram.useProgram();
        heightmapProgram.setUniforms(modelViewProjectionMatrix);
        heightmap.bindData(heightmapProgram);
        heightmap.draw();
    }

    private void drawSkyBox() {
        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForSkybox();

        //GL_LEQUAL :如果新片段与已经存在那里的片段相比较近或者二者在同等的距离处，就让它通过测试
        GLES20.glDepthFunc(GL_LEQUAL);
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(modelViewProjectionMatrix, skyBoxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
        //GL_LESS :如果新片段比任何已经存在那里的片段近或者比远平面近，就让它通过测试
        GLES20.glDepthFunc(GL_LESS);
    }
    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 1);
        greenParticleShooter.addParticles(particleSystem, currentTime, 1);
        blueParticleShooter.addParticles(particleSystem, currentTime, 1);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();

        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleProgram.useProgram();
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, particlesTexture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
        updateViewMatrices();
    }
    private void updateViewMatrices() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.length);

        translateM(viewMatrix, 0, 0, -1.5f, -5f);
    }
    private void updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }
    private void updateMvpMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }
}
