package com.kamino.mygallery.util;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import android.opengl.Matrix;

public final class MatrixState {
   static float[][] mStack;
   public static int stackTop;
   static ByteBuffer llbb;
   static float[] cameraLocation;
   static float[] mMVPMatrix;
   private static float[] mProjMatrix = new float[16];
   private static float[] mVMatrix = new float[16];
   private static float[] currMatrix = new float[16];
   private static float[] headTrackerMatrix = new float[16];
   private static float[] mVMatrixForSpecFrame;

   static {
      int[] size = new int[]{10, 16};
      mStack = (float[][])Array.newInstance(Float.TYPE, size);
      stackTop = -1;
      llbb = ByteBuffer.allocateDirect(12);
      cameraLocation = new float[3];
      mVMatrixForSpecFrame = new float[16];
      mMVPMatrix = new float[16];
   }

   public static void setInitStack() {
      Matrix.setIdentityM(headTrackerMatrix, 0);
      Matrix.setRotateM(currMatrix, 0, 0.0F, 1.0F, 0.0F, 0.0F);
   }

   public static void translate(float x, float y, float z) {
      Matrix.translateM(currMatrix, 0, x, y, z);
   }

   public static void rotate(float angle, float x, float y, float z) {
      Matrix.rotateM(currMatrix, 0, angle, x, y, z);
   }

   public static void setProjectFrustum(float[] matrix) {
      mProjMatrix = matrix;
   }

   public static void pushMatrix() {
      ++stackTop;
      
      for(int i = 0; i < 16; ++i) {
         mStack[stackTop][i] = currMatrix[i];
      }
   }

   public static void scale(float x, float y, float z) {
      Matrix.scaleM(currMatrix, 0, x, y, z);
   }

   public static void setHeadTrackerMatrix(float[] matrix) {
      headTrackerMatrix = matrix;
  }
  
 public static void popMatrix() {
      for(int i = 0; i < 16; ++i) {
         currMatrix[i] = mStack[stackTop][i];
      }

      stackTop += -1;
   }

  

	public static void copyMVMatrix(float[] matrix) {
      mVMatrixForSpecFrame = matrix;
   }

   public static void setCamera() {
      Matrix.setLookAtM(mVMatrix, 0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F);
   }

   public static float[] getCamera() {
      return mVMatrix;
   }

   public static float[] getFinalMatrix() {
      Matrix.multiplyMM(mMVPMatrix, 0, mVMatrixForSpecFrame, 0, currMatrix, 0);
      Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
      return mMVPMatrix;
   }

   public static float[] getHeadMatrix() {
      Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
      Matrix.multiplyMM(mMVPMatrix, 0, headTrackerMatrix, 0, mMVPMatrix, 0);
      return mMVPMatrix;
   }

   public static float[] getFinalMatrixOrigin() {
      Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
      Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
      return mMVPMatrix;
   }
}
