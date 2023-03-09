package com.bing.test2;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bing.test2.render.HockeyRender3;
import com.bing.test2.render.HockeyRender4;

public class OpenGlActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean renderSet = false;
    private HockeyRender4 hockeyRender;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        hockeyRender = new HockeyRender4(this);
        glSurfaceView.setRenderer(hockeyRender);
        renderSet = true;
        glSurfaceView.setOnTouchListener((v, event) -> {
            if(event != null){
                final float normalizedX =
                        (event.getX() / (float) v.getWidth()) * 2 - 1;
                final float normalizedY =
                        -((event.getY() / (float) v.getHeight()) * 2 - 1);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    glSurfaceView.queueEvent(() -> {
                        hockeyRender.handlePress(normalizedX,normalizedY);

                    });
                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    glSurfaceView.queueEvent(() -> {
                        hockeyRender.handleMove(normalizedX,normalizedY);
                    });
                }
                return true;
            }else{
                return false;
            }
        });
        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(renderSet){
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(renderSet){
            glSurfaceView.onPause();
        }
    }
}
