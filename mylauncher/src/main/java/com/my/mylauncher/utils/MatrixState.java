package com.my.mylauncher.utils;

import android.opengl.Matrix;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MatrixState {
	public static FloatBuffer cameraFB;
	static float[] cameraLocation;
	private static float[] currMatrix;
	public static float[] lightLocation = new float[] { 0.0F, 0.0F, 0.0F };
	public static FloatBuffer lightPositionFB;
	static ByteBuffer llbb;
	static ByteBuffer llbbL;
	static float[] mMVPMatrix;
	private static float[] mProjMatrix = new float[16];
	static float[][] mStack;
	private static float[] mVMatrix = new float[16];
	private static float[] mVMatrixForSpecFrame;
	public static int stackTop;

	static {
		int[] size = new int[] { 10, 16 };
		mStack = (float[][]) Array.newInstance(Float.TYPE, size);
		stackTop = -1;
		llbb = ByteBuffer.allocateDirect(12);
		cameraLocation = new float[3];
		mVMatrixForSpecFrame = new float[16];
		mMVPMatrix = new float[16];
		llbbL = ByteBuffer.allocateDirect(12);
	}

	public static void copyMVMatrix() {
		for (int i = 0; i < 16; ++i) {
			mVMatrixForSpecFrame[i] = mVMatrix[i];
		}

	}

	public static void copyMVMatrix(float[] matrix) {
		mVMatrixForSpecFrame = matrix;
	}

	public static float[] getCamera() {
		return mVMatrix;
	}

	public static float[] getFinalMatrix() {
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrixForSpecFrame, 0, currMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}

	public static float[] getMMatrix() {
		return currMatrix;
	}

	public static void matrix(float[] matrix) {
		float[] mm = new float[16];
		Matrix.multiplyMM(mm, 0, currMatrix, 0, matrix, 0);
		currMatrix = mm;
	}

	public static void popMatrix() {
		for (int i = 0; i < 16; ++i) {
			currMatrix[i] = mStack[stackTop][i];
		}

		stackTop += -1;
	}

	public static void pushMatrix() {
		++stackTop;

		for (int i = 0; i < 16; ++i) {
			mStack[stackTop][i] = currMatrix[i];
		}
	}

	public static void rotate(float angle, float x, float y, float z) {
		Matrix.rotateM(currMatrix, 0, angle, x, y, z);
	}

	public static void scale(float x, float y, float z) {
		Matrix.scaleM(currMatrix, 0, x, y, z);
	}

	public static void setCamera(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ) {
		Matrix.setLookAtM(mVMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY,
				centerZ, upX, upY, upZ);
		cameraLocation[0] = eyeX;
		cameraLocation[1] = eyeY;
		cameraLocation[2] = eyeZ;
		llbb.clear();
		llbb.order(ByteOrder.nativeOrder());
		cameraFB = llbb.asFloatBuffer();
		cameraFB.put(cameraLocation);
		cameraFB.position(0);
	}

	public static void setInitStack() {
		currMatrix = new float[16];
		Matrix.setRotateM(currMatrix, 0, 0.0F, 1.0F, 0.0F, 0.0F);
	}

	public static void setLightLocation(float x, float y, float z) {
		llbbL.clear();
		lightLocation[0] = x;
		lightLocation[1] = y;
		lightLocation[2] = z;
		llbbL.order(ByteOrder.nativeOrder());
		lightPositionFB = llbbL.asFloatBuffer();
		lightPositionFB.put(lightLocation);
		lightPositionFB.position(0);
	}

	public static void setProjectFrustum(float left, float right, float bottom,
			float top, float near, float far) {
		Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}

	public static void setProjectFrustum(float[] matrix) {
		mProjMatrix = matrix;
	}

	public static void setProjectOrtho(float left, float right, float bottom,
			float top, float near, float far) {
		Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}

	public static void translate(float x, float y, float z) {
		Matrix.translateM(currMatrix, 0, x, y, z);
	}
}
