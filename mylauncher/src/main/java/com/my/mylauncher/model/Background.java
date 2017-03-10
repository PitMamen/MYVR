package com.my.mylauncher.model;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

import com.my.mylauncher.utils.MatrixState;
import com.my.mylauncher.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Background {
	final float UNIT_SIZE = 15.0F;
	float mHeight;
	FloatBuffer mColorBuffer;
	String mFragmentShader;
	int mProgram;
	FloatBuffer mVertexBuffer;
	String mVertexShader;
	int maColorHandle;
	int maPositionHandle;
	int muAlphaHandle;
	int muMVPMatrixHandle;
	int vCount = 0;
	float mWidth;

	//�ذ�ľ�����
	public Background(Context context, float width, float height) {
		mWidth = width;
		mHeight = height;
		initVertexData();
		initShader(context);
	}

	public void drawSelf(float alpha) {
		GLES20.glUseProgram(mProgram);
		GLES20.glUniform1f(muAlphaHandle, alpha);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 12, mVertexBuffer);
		GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
				16, mColorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maColorHandle);
		GLES20.glLineWidth(10.0F);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vCount);
	}

	public void initShader(Context context) {
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_bg.sh",
				context.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_bg.sh",
				context.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		muAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha");
	}

	public void initVertexData() {
		vCount = 4;
		float[] vertexs = new float[] { 
				-mWidth / 2.0F, mHeight / 2.0F, 0.0F,
				-mWidth / 2.0F, -mHeight / 2.0F, 0.0F, 
				mWidth / 2.0F,mHeight / 2.0F, 0.0F, 
				mWidth / 2.0F, -mHeight / 2.0F, 0.0F };
		ByteBuffer vertextBytes = ByteBuffer.allocateDirect(4 * vertexs.length);
		vertextBytes.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertextBytes.asFloatBuffer();
		mVertexBuffer.put(vertexs);
		mVertexBuffer.position(0);
		int color = Color.parseColor("#272727");
		//int color = Color.parseColor("#ff0000");
		float red = (float) Color.red(color) / 255.0F;
		float green = (float) Color.green(color) / 255.0F;
		float blue = (float) Color.blue(color) / 255.0F;
		float[] colors = new float[] { red, green, blue, 1.0F, red, green,
				blue, 1.0F, red, green, blue, 1.0F, red, green, blue, 1.0F };
		ByteBuffer colorBytes = ByteBuffer.allocateDirect(4 * colors.length);
		colorBytes.order(ByteOrder.nativeOrder());
		mColorBuffer = colorBytes.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}
}
