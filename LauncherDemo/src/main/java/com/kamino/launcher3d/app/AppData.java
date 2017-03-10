package com.kamino.launcher3d.app;

public class AppData {
	private boolean mIsSystemApp;
	private String mLabel;
	private int mLabelTextureId = -1;
	private String mPackageName;
	private int mTextureId = -1;

	public AppData() {
	}

	public AppData(String pkgName, String label, boolean isSystemApp,
			int textureId, int labelTextureId) {
		mPackageName = pkgName;
		mLabel = label;
		mIsSystemApp = isSystemApp;
		mTextureId = textureId;
		mLabelTextureId = labelTextureId;
	}

	public boolean equals(Object obj) {
		if (this != obj) {
			if (obj == null) {
				return false;
			}

			if (getClass() != obj.getClass()) {
				return false;
			}

			AppData app = (AppData) obj;
			if (mPackageName == null) {
				if (app.mPackageName != null) {
					return false;
				}
			} else if (!mPackageName.equals(app.mPackageName)) {
				return false;
			}
		}

		return true;
	}

	public String getLabel() {
		return mLabel;
	}

	public int getLabelTextureId() {
		return mLabelTextureId;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public int getTextureId() {
		return mTextureId;
	}

	public boolean isSystemApp() {
		return mIsSystemApp;
	}

	public void setLabel(String label) {
		mLabel = label;
	}

	public void setLabelTextureId(int id) {
		mLabelTextureId = id;
	}

	public void setPackageName(String name) {
		mPackageName = name;
	}

	public void setSystemApp(boolean isSystemApp) {
		mIsSystemApp = isSystemApp;
	}

	public void setTextureId(int id) {
		mTextureId = id;
	}

	public String toString() {
		return "AppData [packageName=" + mPackageName + ", label=" + mLabel
				+ ", isSystemApp=" + mIsSystemApp + ", textureId=" + mTextureId
				+ ", labelTextureId=" + mLabelTextureId + "]";
	}
}
