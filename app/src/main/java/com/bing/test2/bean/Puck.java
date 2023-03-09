package com.bing.test2.bean;

import com.bing.test2.util.Geometry.Cylinder;
import com.bing.test2.util.Geometry.Point;

import java.util.List;

public class Puck {
    private static final int POSITION_COUNT = 3;
    public final float radius;
    public final float height;
    private final List<ObjectBuilder.DrawCommand> drawList;
    private final VertexArray vertexArray;

    public Puck(float radius, float height, int pointNum) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPunk(new Cylinder(new Point(0f, 0f, 0f), radius, height), pointNum);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;

    }
    public void bindData(ColorShaderProgram2 colorShaderProgram){
        vertexArray.setVertexAttribPointer(0,colorShaderProgram.getPositionAttributeLocation(),POSITION_COUNT,0);
    }
    public void draw(){
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
