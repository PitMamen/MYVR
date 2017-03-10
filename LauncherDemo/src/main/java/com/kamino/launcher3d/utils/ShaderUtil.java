package com.kamino.launcher3d.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class ShaderUtil {
	public static void checkGlError(String msg) {
		int error = GLES20.glGetError();
		if (error != 0) {
			Log.e("ES20_ERROR", msg + ": glError " + error);
			throw new RuntimeException(msg + ": glError " + error);
		}
	}

	public static int createProgram(String vertexShader, String fragmentShader) {
		int vertex = loadShader('\u8b31', vertexShader);
		int program;
		if (vertex == 0) {
			program = 0;
		} else {
			int fragment = loadShader('\u8b30', fragmentShader);
			if (fragment == 0) {
				return 0;
			}

			program = GLES20.glCreateProgram();
			if (program != 0) {
				GLES20.glAttachShader(program, vertex);
				checkGlError("glAttachShader");
				GLES20.glAttachShader(program, fragment);
				checkGlError("glAttachShader");
				GLES20.glLinkProgram(program);
				int[] params = new int[1];
				GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, params, 0);
				if (params[0] != 1) {
					Log.e("ES20_ERROR", "Could not link program: ");
					Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
					GLES20.glDeleteProgram(program);
					return 0;
				}
			}
		}

		return program;
	}

	public static String loadFromAssetsFile(String path, Resources res) {
		String str = null;

		try {
			ByteArrayOutputStream as = new ByteArrayOutputStream();
			InputStream in = res.getAssets().open(path);
			int ch = 0;
			while ((ch = in.read()) != -1)
				as.write(ch);
			byte[] arrayOfByte = as.toByteArray();
			in.close();
			as.close();
			str = new String(arrayOfByte, "UTF-8");
			str = str.replaceAll("\\r\\n", "\n");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return str;
	}

	public static int loadShader(int type, String path) {
		int shader = GLES20.glCreateShader(type);
		if (shader != 0) {
			GLES20.glShaderSource(shader, path);
			GLES20.glCompileShader(shader);
			int[] params = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params, 0);
			if (params[0] == 0) {
				Log.e("ES20_ERROR", "Could not compile shader " + type + ":");
				Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}

		return shader;
	}
}
