/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.bing.test2.bean;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;
import static com.bing.test2.Constants.BYTES_PER_SHORT;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

//索引缓冲区对象
public class IndexBuffer {
    private final int bufferId;
    
    public IndexBuffer(short[] indexData) {
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new index buffer object.");
        }
        
        bufferId = buffers[0];
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        
        ShortBuffer indexArray = ByteBuffer
            .allocateDirect(indexData.length * BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indexData);
        indexArray.position(0);
                               
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexArray.capacity() * BYTES_PER_SHORT,
            indexArray, GL_STATIC_DRAW);                             
         
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    }
    
    public int getBufferId() {
        return bufferId;
    }
}
