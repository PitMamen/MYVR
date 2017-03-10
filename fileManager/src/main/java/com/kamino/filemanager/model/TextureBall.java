package com.kamino.filemanager.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;

import com.kamino.filemanager.util.MatrixState;
import com.kamino.filemanager.util.ShaderUtil;

public final class TextureBall {
	int mProgram;
	int muMVPMatrixHandle;
	int maPositionHandle;
	String mVertexShader;
	String mFragmentShader;
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	public float hh = 0.0F;
	public float i = 0.0F;
	public float j = 0.0F;
	private int maTexCoorHandle;
	private float[] mVertexs = generateVertex();
	private ByteBuffer mIndexByteBuffer;
	private ShortBuffer mIndexBuffer;
	private int mSize;

	// private int muAlphaHandle;

	public TextureBall(Context context) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * mVertexs.length);
		buffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = buffer.asFloatBuffer();
		mVertexBuffer.put(mVertexs);
		mVertexBuffer.position(0);
		float[] texCoors = generateTexCoor();
		ByteBuffer texCoorBytes = ByteBuffer
				.allocateDirect(4 * texCoors.length);
		texCoorBytes.order(ByteOrder.nativeOrder());
		mTexCoorBuffer = texCoorBytes.asFloatBuffer();
		mTexCoorBuffer.put(texCoors);
		mTexCoorBuffer.position(0);
		short[] indexs = generateIndex();
		mIndexByteBuffer = ByteBuffer.allocateDirect(2 * indexs.length);
		mIndexByteBuffer.order(ByteOrder.nativeOrder());
		mIndexBuffer = mIndexByteBuffer.asShortBuffer();
		mIndexBuffer.put(indexs);
		mIndexBuffer.position(0);
		mSize = indexs.length;
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_ball.sh",
				context.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_ball.sh",
				context.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		// muAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha");
	}

	private static float[] generateVertex() {
		float[] buffer = new float[2109];
		int idx = 0;

		for (float i = 90.0F; i >= -90.0F; i -= 10.0F) {
			for (float j = 360.0F; j >= 0.0F; j -= 10.0F) {
				double cosRad = 1.0D * Math.cos(Math.toRadians((double) i));
				buffer[idx++] = (float) (cosRad * Math.cos(Math
						.toRadians((double) j)));
				buffer[idx++] = (float) (1.0D * Math.sin(Math
						.toRadians((double) i)));
				buffer[idx++] = (float) (cosRad * Math.sin(Math
						.toRadians((double) j)));
			}
		}

		return buffer;
	}

	private static float[] generateTexCoor() {
		float[] buffer = new float[1406];
		int idx = 0;

		for (int i = 0; i <= 18; ++i) {
			for (int j = 36; j >= 0; --j) {
				buffer[idx++] = 0.027777778F * (float) j;
				buffer[idx++] = 0.055555556F * (float) i;
			}
		}

		return buffer;
	}

	private static short[] generateIndex() {
		short[] buffer = new short[1332];
		int idx = 0;
		int flag = 1;

		for (int i = 0; i < 18; ++i) {

			if (flag == 1) {
				for (int j = 0; j <= 36; ++j) {
					buffer[idx++] = (short) (j + i * 37);
					buffer[idx++] = (short) (j + 37 * (i + 1));
				}
			} else {
				for (int k = 36; k >= 0; --k) {
					buffer[idx++] = (short) (k + i * 37);
					buffer[idx++] = (short) (k + 37 * (i + 1));
				}
			}

			flag = 1 - flag;
		}

		return buffer;
	}

	public final void drawSelf(int textureId) {
		GLES20.glUseProgram(mProgram);
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
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, mSize,
				GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
	}
}
