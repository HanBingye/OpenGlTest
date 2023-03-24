package com.bing.test2.bean;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

import static com.bing.test2.Constants.BYTES_PRE_FLOAT;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//顶点缓冲区对象
public class VertexBuffer {
    private final int bufferId;

    public VertexBuffer(float[] vertexData) {
        final int buffers[] = new int[1];
        //创建顶点缓冲区对象
        glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }
        bufferId = buffers[0];
        //传递GL_ARRAY_BUFFER 告诉OpenGL这是一个顶点缓冲区
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);

        FloatBuffer vertexArray = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PRE_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexArray.position(0);
        //GL_STATIC_DRAW 这个对象将被修改一次，但是会经常使用 （这些只是提示，而不是限制，所以OpenGL可以根据需要做任何优化。大多数情况下，我们都使用GL_ STATIC DRAW）
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PRE_FLOAT,
                vertexArray, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);

        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
                false, stride, dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
