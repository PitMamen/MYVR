package com.kamino.launcher3d.model;

import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;

import com.kamino.launcher3d.utils.MatrixState;
import com.kamino.launcher3d.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class TextureArc {
	public static final float UNIT_SIZE = 2.0F;
	private HashMap<String, float[]> mDatas;
	String mFragmentShader;
	private String mLastDataTag;
	int mProgram;
	FloatBuffer mTexCoorBuffer;
	FloatBuffer mVertexBuffer;
	String mVertexShader;
	int maPositionHandle;
	int maTexCoorHandle;
	private int muAlphaHandle;
	int muMVPMatrixHandle;
	private int n = 36;
	float unitSize = 1.0F;
	int vCount = 0;

	public TextureArc(Context context) {
		initShader(context);
	}

	private void doInitDatas(float r, float h, float degree) {
		float step = degree / (float) n;
		double rad = 3.141592653589793D * (degree / 180.0F);
		vCount = 4 * 3 * n;
		float[] vertices = new float[3 * vCount];
		float[] textures = new float[2 * vCount];
		int count = 0;
		int stCount = 0;

		for (float i = 0.0F; Math.ceil(i) < degree; i += step) {
			double curRad = Math.toRadians(i);
			double nextRad = Math.toRadians(i + step);
			vertices[count++] = (float) ((-r) * Math.sin(curRad));
			vertices[count++] = -h / UNIT_SIZE;
			vertices[count++] = (float) ((-r) * Math.cos(curRad));
			textures[stCount++] = (float) (curRad / rad);
			textures[stCount++] = 1.0F;
			vertices[count++] = (float) ((-r) * Math.sin(nextRad));
			vertices[count++] = h / UNIT_SIZE;
			vertices[count++] = (float) ((-r) * Math.cos(nextRad));
			textures[stCount++] = (float) (nextRad / rad);
			textures[stCount++] = 0.0F;
			vertices[count++] = (float) ((-r) * Math.sin(curRad));
			vertices[count++] = h / UNIT_SIZE;
			vertices[count++] = (float) ((-r) * Math.cos(curRad));
			textures[stCount++] = (float) (curRad / rad);
			textures[stCount++] = 0.0F;
			vertices[count++] = (float) ((-r) * Math.sin(curRad));
			vertices[count++] = -h / UNIT_SIZE;
			vertices[count++] = (float) ((-r) * Math.cos(curRad));
			textures[stCount++] = (float) (curRad / rad);
			textures[stCount++] = 1.0F;
			vertices[count++] = (float) ((-r) * Math.sin(nextRad));
			vertices[count++] = -h / UNIT_SIZE;
			vertices[count++] = (float) ((-r) * Math.cos(nextRad));
			textures[stCount++] = (float) (nextRad / rad);
			textures[stCount++] = 1.0F;
			vertices[count++] = (float) ((-r) * Math.sin(nextRad));
			vertices[count++] = h / UNIT_SIZE;
			vertices[count++] = (float) ((-r) * Math.cos(nextRad));
			textures[stCount++] = (float) (nextRad / rad);
			textures[stCount++] = 0.0F;
		}

		mDatas.put(r + "_" + h + "_" + degree + "_" + "vertex", vertices);
		mDatas.put(r + "_" + h + "_" + degree + "_" + "texture", textures);
	}

	public void drawSelf(int textureId, float r, float h, float degree) {
		initData(r, h, degree);
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

	public void drawSelf(int textureId, float r, float h, float degree,
			float alpha) {
		initData(r, h, degree);
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

	public void initData(float r, float h, float degree) {
		if (mDatas == null) {
			mDatas = new HashMap<String, float[]>();
		}

		String tag = r + "_" + h + "_" + degree + "_";
		if (!TextUtils.equals(tag, mLastDataTag)) {
			
			float[] vertexs = mDatas.get(tag + "vertex");
			float[] texCoors = mDatas.get(tag + "texture");
			if (vertexs == null) {
				doInitDatas(r, h, degree);
				vertexs = mDatas.get(tag + "vertex");
				texCoors = mDatas.get(tag + "texture");
			}

			mLastDataTag = tag;
			if (mVertexBuffer != null) {
				mVertexBuffer.clear();
			} else {
				ByteBuffer vertexBytes = ByteBuffer
						.allocateDirect(4 * vertexs.length);
				vertexBytes.order(ByteOrder.nativeOrder());
				mVertexBuffer = vertexBytes.asFloatBuffer();
			}

			mVertexBuffer.put(vertexs);
			mVertexBuffer.position(0);
			if (mTexCoorBuffer != null) {
				mTexCoorBuffer.clear();
			} else {
				ByteBuffer texCoorBytes = ByteBuffer
						.allocateDirect(4 * texCoors.length);
				texCoorBytes.order(ByteOrder.nativeOrder());
				mTexCoorBuffer = texCoorBytes.asFloatBuffer();
			}

			mTexCoorBuffer.put(texCoors);
			mTexCoorBuffer.position(0);
		}
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
}
