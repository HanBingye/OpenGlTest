package com.bing.test2.render;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.bing.test2.R;
import com.bing.test2.util.MatrixHelper;
import com.bing.test2.util.ResourceUtils;
import com.bing.test2.util.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HockeyRender1 implements GLSurfaceView.Renderer {
    private static final String A_POSITION = "a_Position";
    private static final String U_COLOR = "u_Color";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";
    private static final int BYTES_PRE_FLOAT = 4;
    private static final int POSITION_COUNT = 2;
    private static final int COLOR_COUNT = 3;
    private static final int STRIDE = (POSITION_COUNT+COLOR_COUNT)*BYTES_PRE_FLOAT;
    private final float[] projectionMatrix = new float[16];//投影矩阵
    private final float[] modelMatrix = new float[16];//模型矩阵
    private final FloatBuffer vertexData;
    private final Context context;
    private int program;
    private int colorLocation;
    private int positionLocation;
    private int matrixLocation;

    public HockeyRender1(Context context) {
        this.context = context;

        float[] tableVertices = {
                //triangle fan  x,y,r,g,b
                0f, 0f , 1f , 1f , 1f ,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                //Line 1
                -0.5f, 0f,1f,0f,0f,
                0.5f, 0f,1f,0f,0f,
                //木槌
                0f, -0.4f,0f,1f,0f,
                0f, 0.4f,0f,0f,1f
        };
        vertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTES_PRE_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //设置清空屏幕用的颜色
        glClearColor(0f, 0f, 0f, 0f);
        String vertexResource = ResourceUtils.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentResource = ResourceUtils.readTextFileFromResource(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexResource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentResource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        ShaderHelper.validateProgram(program);
        //使用程序
        glUseProgram(program);
        colorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        //获取属性位置
        positionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        matrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        //把位置设置在数据的开头处
        vertexData.position(0);
        //告诉opengl,可以在vertexData缓冲区中找到 A_POSITION 对应的数据
        GLES20.glVertexAttribPointer(positionLocation,POSITION_COUNT,GL_FLOAT,false,STRIDE,vertexData);
        GLES20.glEnableVertexAttribArray(positionLocation);

        vertexData.position(POSITION_COUNT);
        GLES20.glVertexAttribPointer(colorLocation,COLOR_COUNT,GL_FLOAT,false,STRIDE,vertexData);
        GLES20.glEnableVertexAttribArray(colorLocation);

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
        //清空屏幕，这会擦除屏幕上的所有颜色，并用之前glClearColor()调用定义的颜色填充整个屏幕
        glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUniformMatrix4fv(matrixLocation,1,false, projectionMatrix,0);
        //绘制桌子   glUniform4f更新着色器代码中 u_Color 的值
//        glUniform4f(colorLocation,1f,1f,1f,1f);
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
        //绘制分割线
//        glUniform4f(colorLocation,1f,0f,0f,1f);
        glDrawArrays(GL_LINES,6,2);
        //绘制两个木槌
//        glUniform4f(colorLocation,0f,1f,0f,1f);
        glDrawArrays(GL_POINTS,8,1);
//        glUniform4f(colorLocation,0f,0f,1f,1f);
        glDrawArrays(GL_POINTS,9,1);


    }
}
