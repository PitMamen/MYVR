package com.kamino.launcher3d.model;

import android.content.Context;
import android.opengl.GLES20;

import com.kamino.launcher3d.utils.MatrixState;
import com.kamino.launcher3d.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class TextureBall {
	private int indexCount = 0;
	private String mFragmentShader = "precision mediump float;"
			+ "varying vec2 vTextureCoord;uniform sampler2D uTexture;"
			+ "uniform float uAlpha;void main(){"
			+ "    vec4 bcolor = texture2D(uTexture, vTextureCoord);"
			+ "    bcolor.a = uAlpha;" + "    gl_FragColor = bcolor;" + "}";
	private ShortBuffer mIndexBuffer;
	private ByteBuffer mIndexByteBuffer;
	private int mProgram;
	private FloatBuffer mTexCoorBuffer;
	private FloatBuffer mVertexBuffer;
	private String mVertexShader = "uniform mat4 uMVPMatrix;"
			+ "attribute vec3 aPosition;attribute vec2 aTexCoor;"
			+ "varying vec2 vTextureCoord;void main(){"
			+ "   gl_Position = uMVPMatrix * vec4(aPosition,1);"
			+ "   vTextureCoord = aTexCoor;" + "}";
	int maPositionHandle;
	private int maTexCoorHandle;
	private int muAlphaHandle;
	private int muMVPMatrixHandle;

	public TextureBall(Context context) {
		initVertexData(1.0F);
		initShader(context);
	}

	public void drawSelf(int textureId, float alpha) {
		GLES20.glUseProgram(mProgram);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		GLES20.glUniform1f(muAlphaHandle, alpha);
		//每个顶点3个位置坐标，2个纹理坐标
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 12, mVertexBuffer);
		GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT,
				false, 8, mTexCoorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maTexCoorHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indexCount,
				GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
	}

	public short[] generateIndex(int bw, int bh) {
		short[] result = new short[2 * bh * (bw + 1)];
		int c = 0;
		int bIncrease = 1;

		for (int i = 0; i < bh; ++i) {
			if (bIncrease == 1) {
				for (int j = 0; j <= bw; ++j) {
					float s = (float) (j + i * (bw + 1));
					float t = (float) (j + (i + 1) * (bw + 1));
					result[c++] = (short) ((int) s);
					result[c++] = (short) ((int) t);
				}
			} else {
				for (int j = bw; j >= 0; --j) {
					float s = (float) (j + i * (bw + 1));
					float t = (float) (j + (i + 1) * (bw + 1));
					result[c++] = (short) ((int) s);
					result[c++] = (short) ((int) t);
				}
			}

			bIncrease = 1 - bIncrease;
		}

		return result;
	}

	public float[] generateTexCoor(int bw, int bh) {
		float[] result = new float[2 * (bw + 1) * (bh + 1)];
		float sizew = 1.0F / (float) bw;
		float sizeh = 1.0F / (float) bh;
		int c = 0;

		for (int i = 0; i <= bh; i++) {
			for (int j = bw; j >= 0; --j) {
				float s = sizew * (float) j;
				float t = sizeh * (float) i;
				result[c++] = s;
				result[c++] = t;
			}
		}

		return result;
	}

	public float[] generateVertex(float angleSpan, float r) {
		int bw = (int) (360.0F / angleSpan);
		int bh = (int) (180.0F / angleSpan);
		float[] result = new float[3 * (bw + 1) * (bh + 1)];
		int c = 0;

		for (float i = 90.0F; i >= -90.0F; i -= angleSpan) {
			for (float j = 360.0F; j >= 0.0F; j -= angleSpan) {
				double xozLength = (1.0F * r) * Math.cos(Math.toRadians(i));
				result[c++] = (float) (xozLength * Math.cos(Math.toRadians(j)));
				result[c++] = (float) ((1.0F * r) * Math.sin(Math.toRadians(i)));
				result[c++] = (float) (xozLength * Math.sin(Math.toRadians(j)));
			}
		}

		return result;
	}

	public void initShader(Context context) {
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		muAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha");
	}

	public void initVertexData(float r) {
		float[] vertexs = generateVertex(10.0F, r);
		ByteBuffer vertexBytes = ByteBuffer.allocateDirect(4 * vertexs.length);
		vertexBytes.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertexBytes.asFloatBuffer();
		mVertexBuffer.put(vertexs);
		mVertexBuffer.position(0);
		float[] texCoors = generateTexCoor(36, 18);
		ByteBuffer texCoorBytes = ByteBuffer
				.allocateDirect(4 * texCoors.length);
		texCoorBytes.order(ByteOrder.nativeOrder());
		mTexCoorBuffer = texCoorBytes.asFloatBuffer();
		mTexCoorBuffer.put(texCoors);
		mTexCoorBuffer.position(0);
		short[] indexs = generateIndex(36, 18);
		mIndexByteBuffer = ByteBuffer.allocateDirect(2 * indexs.length);
		mIndexByteBuffer.order(ByteOrder.nativeOrder());
		mIndexBuffer = mIndexByteBuffer.asShortBuffer();
		mIndexBuffer.put(indexs);
		mIndexBuffer.position(0);
		indexCount = indexs.length;
	}
}
