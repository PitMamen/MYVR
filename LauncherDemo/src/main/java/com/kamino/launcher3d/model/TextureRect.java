package com.kamino.launcher3d.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;

import com.kamino.launcher3d.utils.MatrixState;
import com.kamino.launcher3d.utils.ShaderUtil;

public class TextureRect {
	public static final float UNIT_SIZE = 2.0F;
	float mWidth;
	float mHeight;
	String mFragmentShader;
	int mProgram;
	FloatBuffer mTexCoorBuffer;
	FloatBuffer mVertexBuffer;
	String mVertexShader;
	int maPositionHandle;
	int maTexCoorHandle;
	private int muAlphaHandle;
	int muMVPMatrixHandle;
	int vCount = 0;
	float x = 0.0F;
	float xAngle = 0.0F;
	float y = 0.0F;
	float yAngle = 0.0F;
	float z = 0.0F;
	float zAngle = 0.0F;

	public TextureRect(Context context, float width, float height) {
		mWidth = width;
		mHeight = height;
		initVertexData();
		initTexCoorData();
		initShader(context);
	}

	public void drawSelf(int textureId) {
		GLES20.glUseProgram(mProgram);
		GLES20.glUniform1f(muAlphaHandle, 1.0F);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 12, mVertexBuffer);
		GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT,
				false, 8, mTexCoorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maTexCoorHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}

	public void drawSelf(int textureId, float alpha) {
		GLES20.glUseProgram(mProgram);
		GLES20.glUniform1f(muAlphaHandle, alpha);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		//每个顶点3个位置坐标，2个纹理坐标
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 12, mVertexBuffer);
		GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT,
				false, 8, mTexCoorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maTexCoorHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}

	public void initShader(Context context) {
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh",
				context.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh",
				context.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		muAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha");
	}

	public void initTexCoorData() {
		float[] texCoors = new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F,
				1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F };
		ByteBuffer texCoorBytes = ByteBuffer
				.allocateDirect(4 * texCoors.length);
		texCoorBytes.order(ByteOrder.nativeOrder());
		mTexCoorBuffer = texCoorBytes.asFloatBuffer();
		mTexCoorBuffer.put(texCoors);
		mTexCoorBuffer.position(0);
	}

	public void initVertexData() {
		vCount = 6;
		float[] vertexs = new float[] { 
				-mWidth / 2.0F, mHeight / 2.0F, 0.0F,
				-mWidth / 2.0F, -mHeight / 2.0F, 0.0F, 
				mWidth / 2.0F,-mHeight / 2.0F, 0.0F, 
				mWidth / 2.0F, -mHeight / 2.0F, 0.0F,
				mWidth / 2.0F, mHeight / 2.0F, 0.0F,
				-mWidth / 2.0F,mHeight / 2.0F, 0.0F };
		ByteBuffer vertexBytes = ByteBuffer.allocateDirect(4 * vertexs.length);
		vertexBytes.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertexBytes.asFloatBuffer();
		mVertexBuffer.put(vertexs);
		mVertexBuffer.position(0);
	}
}
