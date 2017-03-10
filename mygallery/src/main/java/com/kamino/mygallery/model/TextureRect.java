package com.kamino.mygallery.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;

import com.kamino.mygallery.util.MatrixState;
import com.kamino.mygallery.util.ShaderUtil;

public final class TextureRect {
	int mProgram;
	int muMVPMatrixHandle;
	int maPositionHandle;
	int maTexCoorHandle;
	String mVertexShader;
	String mFragmentShader;
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	int vCount = 0;
	private int muAlphaHandle;

	public TextureRect(Context context) {
		vCount = 6;
		float[] vertexs = new float[] { -0.5F, 0.5F, 0.0F, -0.5F, -0.5F, 0.0F,
				0.5F, -0.5F, 0.0F, 0.5F, -0.5F, 0.0F, 0.5F, 0.5F, 0.0F, -0.5F,
				0.5F, 0.0F };
		ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(4 * vertexs.length);
		vertexBuffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertexBuffer.asFloatBuffer();
		mVertexBuffer.put(vertexs);
		mVertexBuffer.position(0);
		float[] texCoors = new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F,
				1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F };
		ByteBuffer texCoorsBuffer = ByteBuffer
				.allocateDirect(4 * texCoors.length);
		texCoorsBuffer.order(ByteOrder.nativeOrder());
		mTexCoorBuffer = texCoorsBuffer.asFloatBuffer();
		mTexCoorBuffer.put(texCoors);
		mTexCoorBuffer.position(0);
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

	public final void drawSelf(int textureId) {
		GLES20.glUseProgram(mProgram);
		GLES20.glUniform1f(muAlphaHandle, 0.5F);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrixOrigin(), 0);
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

	public final void drawSelf(int textureId, float alpha) {
		GLES20.glUseProgram(mProgram);
		GLES20.glUniform1f(muAlphaHandle, alpha);
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

	public final void drawSelfOrigin(int textureId) {
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
}
