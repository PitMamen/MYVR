package com.my.mylauncher.entity;

import com.my.mylauncher.model.TextureRect;

public class ImageEntity {
	private float Xtrans;
	private float Ytrans;
	private float Ztrans;
	private float rotate;
	private float Xsize;
	private float Ysize;
	private TextureRect RectTexture;
	private int RectTextureId;
	public ImageEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ImageEntity(float xtrans, float ytrans, float ztrans, float rotate, float xsize, float ysize,
			TextureRect rectTexture, int rectTextureId) {
		super();
		Xtrans = xtrans;
		Ytrans = ytrans;
		Ztrans = ztrans;
		this.rotate = rotate;
		Xsize = xsize;
		Ysize = ysize;
		RectTexture = rectTexture;
		RectTextureId = rectTextureId;
	}
	public float getXtrans() {
		return Xtrans;
	}
	public void setXtrans(float xtrans) {
		Xtrans = xtrans;
	}
	public float getYtrans() {
		return Ytrans;
	}
	public void setYtrans(float ytrans) {
		Ytrans = ytrans;
	}
	public float getZtrans() {
		return Ztrans;
	}
	public void setZtrans(float ztrans) {
		Ztrans = ztrans;
	}
	public float getRotate() {
		return rotate;
	}
	public void setRotate(float rotate) {
		this.rotate = rotate;
	}
	public float getXsize() {
		return Xsize;
	}
	public void setXsize(float xsize) {
		Xsize = xsize;
	}
	public float getYsize() {
		return Ysize;
	}
	public void setYsize(float ysize) {
		Ysize = ysize;
	}
	public TextureRect getRectTexture() {
		return RectTexture;
	}
	public void setRectTexture(TextureRect rectTexture) {
		RectTexture = rectTexture;
	}
	public int getRectTextureId() {
		return RectTextureId;
	}
	public void setRectTextureId(int rectTextureId) {
		RectTextureId = rectTextureId;
	}
	
	
	
	
	
}
