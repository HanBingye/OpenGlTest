package com.bing.test2.bean;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

import android.util.FloatMath;
import android.util.Log;

import com.bing.test2.util.Geometry.Point;
import com.bing.test2.util.Geometry.Cylinder;
import com.bing.test2.util.Geometry.Circle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectBuilder {
    private static final int FLOAT_PRE_VERTEX = 3;//每个顶点含有x,y,z三个分量
    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<>();
    private int offset = 0;//记录第几个顶点

    private ObjectBuilder(int pointNum) {
        vertexData = new float[pointNum * FLOAT_PRE_VERTEX];
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }


    //圆柱体顶部既 圆的顶点个数（圆心+重复两次的第一个顶点+pointNum）重复两次使圆闭合
    private static int circlePointNum(int pointNum) {
        return pointNum + 2;
    }

    //圆柱体侧面顶点个数 上顶圆边+下顶圆边 一个圆边的个数为pointNum+1
    private static int cylinderPointNum(int pointNum) {
        return (pointNum + 1) * 2;
    }

    static GeneratedData createPunk(Cylinder cylinder, int pointNum) {
        int totalPointNum = circlePointNum(pointNum) + cylinderPointNum(pointNum);
        ObjectBuilder objectBuilder = new ObjectBuilder(totalPointNum);
        Circle circle = new Circle(cylinder.center.translateY(cylinder.height / 2), cylinder.radius);
        objectBuilder.appendCircle(circle, pointNum);
        objectBuilder.appendCylinder(cylinder, pointNum);
        return objectBuilder.build();
    }

    static GeneratedData createMallet(Point center, float radius, float height, int pointNum) {
        int totalPointNum = circlePointNum(pointNum) * 2 + cylinderPointNum(pointNum) * 2;

        ObjectBuilder builder = new ObjectBuilder(totalPointNum);

        // 木槌的底座
        float baseHeight = height * 0.25f;
        Circle baseCircle = new Circle(center.translateY(-baseHeight), radius);
        Cylinder baseCylinder = new Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);

        builder.appendCircle(baseCircle, pointNum);
        builder.appendCylinder(baseCylinder, pointNum);

        // 木槌的手柄
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;

        Circle handleCircle = new Circle(center.translateY(height * 0.5f), handleRadius);
        Cylinder handleCylinder = new Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);

        builder.appendCircle(handleCircle, pointNum);
        builder.appendCylinder(handleCylinder, pointNum);

        return builder.build();
    }

    private void appendCircle(Circle circle, int pointNum) {
        int startVertex = offset / FLOAT_PRE_VERTEX;
        int vertexNum = circlePointNum(pointNum);
        //加圆心
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;
        //添加每个顶点 在xoz平面上映射
        for (int i = 0; i <= pointNum; i++) {
            float radiansPercent = (float) i / (float) pointNum * (float) Math.PI * 2;
            vertexData[offset++] = (float) (circle.center.x + circle.radius * Math.cos(radiansPercent));
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = (float) (circle.center.z + circle.radius * Math.sin(radiansPercent));
        }
        //GL_TRIANGLE_FAN 三角形扇
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, vertexNum);
            }
        });

    }


    private void appendCylinder(Cylinder cylinder, int pointNum) {
        int startVertex = offset / FLOAT_PRE_VERTEX;
        int vertexNum = cylinderPointNum(pointNum);
        float topY = cylinder.center.y + cylinder.height / 2;
        float bottomY = cylinder.center.y - cylinder.height / 2;
        for (int i = 0; i <= pointNum; i++) {
            float radiansPercent = (float) i / (float) pointNum * (float) Math.PI * 2;
            float xPosition = (float) (cylinder.center.x + cylinder.radius * Math.cos(radiansPercent));
            float zPosition = (float) (cylinder.center.z + cylinder.radius * Math.sin(radiansPercent));
            vertexData[offset++] = xPosition;
            vertexData[offset++] = bottomY;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = topY;
            vertexData[offset++] = zPosition;
        }
        //GL_TRIANGLE_STRIP 三角形带
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, vertexNum);
            }
        });

    }


    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    interface DrawCommand {
        void draw();
    }

}
