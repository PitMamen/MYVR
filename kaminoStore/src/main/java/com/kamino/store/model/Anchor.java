package com.kamino.store.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.kamino.store.utils.MatrixState;
import com.kamino.store.utils.ShaderUtil;

import android.content.Context;
import android.opengl.GLES20;


public class Anchor {
	public static final float UNIT_SIZE = 0.4F;
	FloatBuffer mColorBuffer;
	String mFragmentShader;
	int mProgram;
	FloatBuffer mVertexBuffer;
	String mVertexShader;
	int maColorHandle;
	int maPositionHandle;
	int muMVPMatrixHandle;
	private float unitSize = -1.0F;
	int vCount = 0;

	public Anchor(Context context) {
		initShader(context);
	}

	public void drawSelf() {
		if (mVertexBuffer == null) {
			initVertexData();
		}

		GLES20.glUseProgram(mProgram);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 12, mVertexBuffer);
		GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
				16, mColorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maColorHandle);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vCount);
	}

	public void initShader(Context context) {
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_anchor.sh",
				context.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_anchor.sh",
				context.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	public void initVertexData() {
		float size = 0.4F;
		if (unitSize > 0.0F) {
			size = unitSize;
		}

		vCount = 82;
		float step = 360.0F / (float) 80;
		float[] vBuf = new float[3 * vCount];
		vBuf[0] = 0.0F;
		vBuf[1] = 0.0F;
		vBuf[2] = 0.0F;
		float angle = 0.0F;

		for (int i = 3; Math.ceil((double) angle) <= 360.0D;) {
			double angleRad = Math.toRadians((double) angle);
			vBuf[i++] = (float) ((double) (-size) * Math.sin(angleRad));
			vBuf[i++] = (float) ((double) size * Math.cos(angleRad));
			vBuf[i++] = 0.0F;
			angle += step;
		}

		ByteBuffer vertexBytes = ByteBuffer.allocateDirect(4 * vBuf.length);
		vertexBytes.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertexBytes.asFloatBuffer();
		mVertexBuffer.put(vBuf);
		mVertexBuffer.position(0);

		int idx = 0;
		float[] cBuf = new float[4 * vCount];

		for (int i = 0; i < cBuf.length; i += 4) {
			cBuf[idx++] = 1.0F;
			cBuf[idx++] = 1.0F;
			cBuf[idx++] = 1.0F;
			cBuf[idx++] = 0.5F;
		}

		ByteBuffer colorBytes = ByteBuffer.allocateDirect(4 * cBuf.length);
		colorBytes.order(ByteOrder.nativeOrder());
		mColorBuffer = colorBytes.asFloatBuffer();
		mColorBuffer.put(cBuf);
		mColorBuffer.position(0);
	}

	public void setUnitSize(float size) {
		unitSize = size;
	}
}
