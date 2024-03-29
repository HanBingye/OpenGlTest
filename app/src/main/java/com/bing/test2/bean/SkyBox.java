package com.bing.test2.bean;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDrawElements;

import com.bing.test2.program.SkyboxShaderProgram;

import java.nio.ByteBuffer;

public class SkyBox {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final VertexArray vertexArray;
    private final ByteBuffer byteBuffer;

    public SkyBox() {
        //立方体的八个顶点
        vertexArray = new VertexArray(new float[] {
                -1,  1,  1,     // (0) Top-left near
                1,  1,  1,     // (1) Top-right near
                -1, -1,  1,     // (2) Bottom-left near
                1, -1,  1,     // (3) Bottom-right near
                -1,  1, -1,     // (4) Top-left far
                1,  1, -1,     // (5) Top-right far
                -1, -1, -1,     // (6) Bottom-left far
                1, -1, -1      // (7) Bottom-right far
        });

        // 每个面上的两个三角形的索引数组
        byteBuffer =  ByteBuffer.allocateDirect(6 * 6)
                .put(new byte[] {
                        // Front
                        1, 3, 0,
                        0, 3, 2,

                        // Back
                        4, 6, 5,
                        5, 6, 7,

                        // Left
                        0, 2, 4,
                        4, 2, 6,

                        // Right
                        5, 7, 1,
                        1, 7, 3,

                        // Top
                        5, 1, 4,
                        4, 1, 0,

                        // Bottom
                        6, 2, 7,
                        7, 2, 3
                });
        byteBuffer.position(0);
    }

    public void bindData(SkyboxShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0,
                skyboxProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, byteBuffer);
    }
}
