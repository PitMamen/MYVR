package com.my.mylauncher.app;

public interface OnAppStateChangeListener {
   void onAppStateChanged(String pkgName, boolean isInstall);
}
