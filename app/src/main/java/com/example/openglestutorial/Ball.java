package com.example.openglestutorial;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Ball {

    public float zoom = -3f;

    public final float maxZoom = -2f;
    public final float minZoom = -4f;

    private IntBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private IntBuffer mNormalBuffer;// 顶点法向量数据缓冲
    private FloatBuffer mTextureBuffer;// 顶点纹理数据缓冲
    public float mAngleX = 0;// 沿x轴旋转角度
    public float mAngleY = 0;// 沿y轴旋转角度
    public float mAngleZ = 0;// 沿z轴旋转角度
    int vCount = 0;// 顶点数量
    int textureId;// 纹理ID

    public Ball(int scale, int textureId) {
        this.textureId = textureId;
        final int R = 10000 * scale;

        // 实际顶点坐标数据的初始化================begin============================

        ArrayList<Integer> alVertix = new ArrayList<Integer>();// 存放顶点坐标的ArrayList
        final int angleSpan = 9;// 将球进行单位切分的角度

        for (int rowAngle = -90; rowAngle <= 90; rowAngle += angleSpan) {
            for (int colAngleAngle = 0; colAngleAngle < 360; colAngleAngle += angleSpan) {
                double xozLength = R * Math.cos(Math.toRadians(rowAngle));
                int x = (int) (xozLength * Math.cos(Math.toRadians(colAngleAngle)));
                int z = (int) (xozLength * Math.sin(Math.toRadians(colAngleAngle)));
                int y = (int) (R * Math.sin(Math.toRadians(rowAngle)));
                alVertix.add(x);
                alVertix.add(y);
                alVertix.add(z);
            }
        }
        vCount = alVertix.size() / 3;// 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        // 将alVertix中的坐标值转存到一个int数组中
        int vertices[] = new int[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }
        alVertix.clear();
        ArrayList<Float> alTexture = new ArrayList<Float>();// 纹理

        int row = (180 / angleSpan) + 1;// 球面切分的行数
        int col = 360 / angleSpan;// 球面切分的列数

        float splitRow = row;
        float splitCol = col;

        for (int i = 0; i < row; i++)// 对每一行循环
        {
            if (i > 0 && i < row - 1) {// 中间行
                for (int j = 0; j < col; j++) {// 中间行的两个相邻点与下一行的对应点构成三角形
                    int k = i * col + j;
                    // 第1个三角形顶点
                    alVertix.add(vertices[(k + col) * 3]);
                    alVertix.add(vertices[(k + col) * 3 + 1]);
                    alVertix.add(vertices[(k + col) * 3 + 2]);

                    // 纹理坐标
                    alTexture.add(j / splitCol);
                    alTexture.add((i + 1) / splitRow);

                    // 第2个三角形顶点
                    int tmp = k + 1;
                    if (j == col - 1) {
                        tmp = (i) * col;
                    }
                    alVertix.add(vertices[(tmp) * 3]);
                    alVertix.add(vertices[(tmp) * 3 + 1]);
                    alVertix.add(vertices[(tmp) * 3 + 2]);

                    // 纹理坐标
                    alTexture.add((j + 1) / splitCol);
                    alTexture.add(i / splitRow);

                    // 第3个三角形顶点
                    alVertix.add(vertices[k * 3]);
                    alVertix.add(vertices[k * 3 + 1]);
                    alVertix.add(vertices[k * 3 + 2]);

                    // 纹理坐标
                    alTexture.add(j / splitCol);
                    alTexture.add(i / splitRow);
                }
                for (int j = 0; j < col; j++) {// 中间行的两个相邻点与上一行的对应点构成三角形
                    int k = i * col + j;

                    // 第1个三角形顶点
                    alVertix.add(vertices[(k - col) * 3]);
                    alVertix.add(vertices[(k - col) * 3 + 1]);
                    alVertix.add(vertices[(k - col) * 3 + 2]);
                    alTexture.add(j / 40f);
                    alTexture.add((i - 1) / splitRow);

                    int tmp = k - 1;
                    if (j == 0) {
                        tmp = i * col + col - 1;
                    }
                    // 第2个三角形顶点
                    alVertix.add(vertices[(tmp) * 3]);
                    alVertix.add(vertices[(tmp) * 3 + 1]);
                    alVertix.add(vertices[(tmp) * 3 + 2]);
                    alTexture.add((j - 1) / splitCol);
                    alTexture.add(i / splitRow);

                    // 第3个三角形顶点
                    alVertix.add(vertices[k * 3]);
                    alVertix.add(vertices[k * 3 + 1]);
                    alVertix.add(vertices[k * 3 + 2]);
                    alTexture.add(j / splitCol);
                    alTexture.add(i / splitRow);
                }
            }
        }

        vCount = alVertix.size() / 3;// 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        // 将alVertix中的坐标值转存到一个int数组中
        vertices = new int[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }

        // 创建绘制顶点数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mVertexBuffer = vbb.asIntBuffer();// 转换为int型缓冲
        mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);// 设置缓冲区起始位置

        // 创建顶点法向量数据缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length * 4);
        nbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mNormalBuffer = vbb.asIntBuffer();// 转换为int型缓冲
        mNormalBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
        mNormalBuffer.position(0);// 设置缓冲区起始位置

        // 创建纹理坐标缓冲
        float textureCoors[] = new float[alTexture.size()];// 顶点纹理值数组
        for (int i = 0; i < alTexture.size(); i++) {
            textureCoors[i] = alTexture.get(i);
        }

        ByteBuffer cbb = ByteBuffer.allocateDirect(textureCoors.length * 4);
        cbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mTextureBuffer = cbb.asFloatBuffer();// 转换为int型缓冲
        mTextureBuffer.put(textureCoors);// 向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);// 设置缓冲区起始位置

        // 三角形构造顶点、纹理、法向量数据初始化==========end==============================
    }

    public void drawSelf(GL10 gl) {

        gl.glLoadIdentity();
        gl.glFrontFace(GL10.GL_CCW);

        gl.glTranslatef(0f, 0f, zoom);
        gl.glEnable(GL10.GL_CULL_FACE);

        gl.glCullFace(GL10.GL_BACK);

//        gl.glPopMatrix();
        float[] modelview = new float[16];
        ((GL11) gl).glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview, 0); // 获取当前矩阵
        float[] x_axis = { 1, 0, 0, 0 };
        float[] y_axis = { 0, 1, 0, 0 };
        Matrix.invertM(modelview, 0, modelview, 0); // 求逆矩阵
        Matrix.multiplyMV(x_axis, 0, modelview, 0, x_axis, 0); // 获取世界x轴在模型坐标系里的指向（w轴）
        Matrix.multiplyMV(y_axis, 0, modelview, 0, y_axis, 0);

        gl.glRotatef(mAngleX, y_axis[0], y_axis[1], y_axis[2]);
//        gl.glRotatef(mAngleY, x_axis[0], x_axis[1], x_axis[2]);
        gl.glRotatef(mAngleZ, 0, 0, 1);// 沿Z轴旋转
//        gl.glPushMatrix();
        // 允许使用顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 为画笔指定顶点坐标数据
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);

        // 允许使用法向量数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        // 为画笔指定顶点法向量数据
        gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);

        // 开启纹理
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // 允许使用纹理ST坐标缓冲
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        // 为画笔指定纹理ST坐标缓冲
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        // 绑定当前纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

        // 绘制图形
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vCount);

    }

    public void cutSpeed() {

        float speed = 5f;
        mAngleX -= speed;
        mAngleY -= speed;
        mAngleZ -= speed;
        if (mAngleX < 0)
            mAngleX = 0;
        if (mAngleY < 0)
            mAngleY = 0;
        if (mAngleZ < 0)
            mAngleZ = 0;
    }
}
