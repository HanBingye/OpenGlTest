package com.bing.test2.bean;

import static android.opengl.GLES20.GL_FLOAT;
import static com.bing.test2.Constants.BYTES_PER_FLOAT;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexArray {

    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertex) {
        //将数据写入缓冲区
        floatBuffer = ByteBuffer
                .allocateDirect(vertex.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex);
    }
    public void setVertexAttribPointer(int position,int location,int count,int stride){
        floatBuffer.position(position);
        //将着色器中的属性与floatBuffer数据相关联
        GLES20.glVertexAttribPointer(location,count,GL_FLOAT,false,stride,floatBuffer);
        //告诉opengl,可以在floatBuffer缓冲区中找到location对应的数据
        GLES20.glEnableVertexAttribArray(location);
        floatBuffer.position(0);
    }

    public void updateBuffer(float[] vertexData, int start, int count) {
        floatBuffer.position(start);
        floatBuffer.put(vertexData, start, count);
        floatBuffer.position(0);
    }
}
