package com.example.openglestutorial;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLESActivity extends Activity implements IOpenGLDemo {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(new OpenGLRenderer(this));
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    protected GLSurfaceView mGLSurfaceView;

    @Override
    public void initLight(GL10 gl) {

    }

    @Override
    public void DrawScene(GL10 gl) {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 设置白色灯光
     *
     * @param gl
     * @param cap
     * @param posX
     * @param posY
     * @param posZ
     */
    public void initWhiteLight(GL10 gl, int cap, float posX, float posY, float posZ) {
        gl.glEnable(cap);// 打开cap号灯

        // 环境光设置
        float[] ambientParams = { 1.0f, 1.0f, 1.0f, 1.0f };// 光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientParams, 0);

        // 散射光设置
        float[] diffuseParams = { 1.0f, 1.0f, 1.0f, 1.0f };// 光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseParams, 0);

        // 反射光设置
        float[] specularParams = { 1f, 1f, 1f, 1.0f };// 光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularParams, 0);

        float[] positionParams = { posX, posY, posZ, 1 };
        gl.glLightfv(cap, GL10.GL_POSITION, positionParams, 0);
    }

    /**
     * 由一个bitmap 创建一个纹理
     *
     * bitmap的大小限制：
     *
     * @param gl
     * @param resourceId
     * @return
     */
    public int initTexture(GL10 gl, int resourceId) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int currTextureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        InputStream is = this.getResources().openRawResource(resourceId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return currTextureId;
    }

    /**
     * 初始化白色材质
     *
     * 材质为白色时什么颜色的光照在上面就将体现出什么颜色
     *
     * @param gl
     */
    private void initMaterial(GL10 gl) {

        // 环境光为白色材质
        float ambientMaterial[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial, 0);

        // 散射光为白色材质
        float diffuseMaterial[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial, 0);

        // 高光材质为白色
        float specularMaterial[] = { 1f, 1f, 1f, 1.0f };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial, 0);
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 100.0f);
    }

    @Override
    public void initObject(GL10 gl) {
        // TODO Auto-generated method stub

    }

}
