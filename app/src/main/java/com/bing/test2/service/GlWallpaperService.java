package com.bing.test2.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.bing.test2.render.ParticlesRender;

public class GlWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine{

        private GlWallpaperSurface surface;
        private ParticlesRender render;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            surface = new GlWallpaperSurface(GlWallpaperService.this);
            surface.setEGLContextClientVersion(2);
            render = new ParticlesRender(GlWallpaperService.this);
            surface.setRenderer(render);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                //保留EGL上下文 不必重新加载所有的OpenGL数据
                surface.setPreserveEGLContextOnPause(true);
            }
        }

        //无论动态壁纸可见还是隐藏都会被调用
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if(visible){
                surface.onResume();
            }else{
                surface.onPause();
            }
        }

        //滚动屏幕时会被调用
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            surface.queueEvent(()->{
                render.handleOffsetsChanged(xOffset,yOffset);
            });
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            surface.onWallpaperDestroy();
        }

        public class GlWallpaperSurface extends GLSurfaceView{

            public GlWallpaperSurface(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                //返回动态壁纸的渲染表面
                return getSurfaceHolder();
            }

            public void onWallpaperDestroy(){
                super.onDetachedFromWindow();
            }
        }
    }
}
