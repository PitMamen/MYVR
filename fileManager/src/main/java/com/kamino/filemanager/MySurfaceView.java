package com.kamino.filemanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.text.Layout.Alignment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.kamino.filemanager.device.DeviceManager;
import com.kamino.filemanager.device.DiskInfo;
import com.kamino.filemanager.device.FileUtils;
import com.kamino.filemanager.file.FileCategoryHelper;
import com.kamino.filemanager.file.FileComparatorHelper;
import com.kamino.filemanager.file.FileInfo;
import com.kamino.filemanager.file.FileType;
import com.kamino.filemanager.file.SortType;
import com.kamino.filemanager.file.Util;
import com.kamino.filemanager.model.TextureBall;
import com.kamino.filemanager.model.TextureRect;
import com.kamino.filemanager.util.LocalImageLoader;
import com.kamino.filemanager.util.MatrixState;
import com.kamino.filemanager.util.Utils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

@SuppressLint("ClickableViewAccessibility")
public final class MySurfaceView extends CardboardView implements StatMonitor.OnStatChangeListener {
    public int mPageNo = 0;
    ThreadPoolExecutor mResExecutor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(3);
    ThreadPoolExecutor mFileExecutor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(3);
    //记录地板的图标被看到的状态
    public boolean mResetHeadFocus;
    //进入到了显示文件的界面
    public boolean mIsMediaUpdated = false;
    //默认没有弹出对话框
    public boolean mIsOptMenuShow = false;
    //记录的是弹出对话框的类型的type
    public int mOptMenuType = 0;
    public int mCurIndex;
    public FileInfo mCurFile;
    Runnable mUpdateListRun;
    Context mContext;
    private SceneRenderer mRenderer;
    int mMenuLevel = 0;
    //mTabNo = 几 表示在第几个栏目里面
    private int mTabNo = 0;
    private int mTabNuLeft = 0;
    private int mTabNuTop = 0;
    float[] headView = new float[16];
    private float[] cameraView = new float[16];
    float[] headAngle = new float[4];
    private TextureBall mTextureBall;
    private TextureRect mTextureRect;
    private float mThemeBgScale = 50.0F;
    private float iconDistance = 5.0F;
    private int mIconResetOnId;
    private int mLoadingOnId;
    private int mLoadingBaseId;
    private int mIconLauncherId;
    private int mIconPlayId;
    private int mIconFilesId;
    private int mIconFileId;
    private int mIconBgId;
    private int mIconSelectionId;
    private int mCurFileBmpId = -1;
    private float scale3;
    // private int mTxtEmptyId;
    // private int mTxtConfirmId;
    // private int mTxtConfirmOnId;
    private int mTxtCancelId;
    private int mTxtCancelOnId;
    private int mTxtOpenId;
    private int mTxtOpenOnId;
    private int mTxtDeleteId;
    private int mTxtDeleteOnId;
    private int mTxtInstallId;
    private int mTxtInstallOnId;
    private int mTipDeleteId;
    private int mTipInstallId;
    // private int mTipInstallFinishId;
    // private int mTipInstallFailId;
    private int mThemeTextureId;
    private int outAnimPosition = 0;
    private int inAnimPosition = 0;
    private long startTime1;
    private long startTime2;
    private float scale1;
    private float scale2;
    private int mIconBackId;
    private int mIconBackOnId;
    // private int mIconDeleteId;
    // private int mIconDeleteOnId;
    // private int mIconDeleteRedId;
    // private int mIconXId;
    // private int mIconXOnId;
    private int mIconUpId;
    private int mIconUpOnId;
    private int mIconUpNoneId;
    private int mIconDownId;
    private int mIconDownOnId;
    private int mIconDownNoneId;
    private int mBlueRectId;
    private int mTransRectId;
    private int mGrayRectId;
    // private int mWhiteRectId;
    private int mWhiteCircleId;
    private int TestRectId;
    private int mIconResetId;
    //记录对话框的内容被选中的状态 依次记录的是
    //取消  删除  打开   安装   删除（第二层的删除）
    private boolean[] mFileOptFlag = new boolean[8];
    //返回键 向上键  向下键  的焦点记录 true表示被看到
    private boolean[] mToolBtnFocus = new boolean[3];
    //文件被看到 用一个int类型的值mFocusIndex记录
    private int mFocusIndex = -1;
    private DeviceManager mDeviceManager;
    FileCategoryHelper mCategoryHelper;
    FileComparatorHelper mComparatorHelper;
    FileType[] mFileTypes;
    private Thread mUpdateListThread;
    boolean mUpdateFlag;
    private InstallPackageReceiver installOkReceiver;
    private InstallPackageReceiver installFailReceiver;
    private TFBroadcastReceiver mTFReceiver;
    private String mTheme;
    Context mResContext;
    int mThemeResId;
    private int[] mTitleArrayId = new int[3];
    private int[] mTitleArrayBlueId = new int[3];
    private int[] mstorageArrayId = new int[3];
    private int[] mstorageArrayBlueId = new int[3];
    private int[] mshowstorageArrayId = new int[3];
    private int[] mshowsizeArrayId = new int[2];
    private int[] mKindsArrayId = new int[4];
    private int[] mMenuIconsId = new int[4];
    private int[] mKindsArrayBlueId = new int[4];
    private int[] mMenuIconsOnId = new int[4];
    //表示文件总数的文字 的初始化ID
    private int[] mMediaFileCountId = new int[4];
    private int[] mContentArrayId = new int[3];
    private int[] mStorageIconsId = new int[3];
    private long[] mMediaFileCount = new long[4];
    //记录在哪个APP里面 0，1,2 分别代表  视频、图片、APK
    int mCurMediaType = 0;
    List<FileInfo> mCurFileList = new ArrayList<FileInfo>();
    private List<FileInfo> mCurSortFileList = new ArrayList<FileInfo>();
    String mCurListPath;
    private Stack<String> mPathStack = new Stack<String>();
    private Stack<Integer> mPageStack = new Stack<Integer>();
    private int mPageCount;
    //为真的时候表示数据已经获取到并保存好了
    private boolean mFileListInited = false;
    private boolean mMountedDeviceListInited = false;
    private int mMountedDiskLines = 1;
    //表示光标是否移动到了左边的标题上面
    private boolean[] mLeftTabFocus = new boolean[3];
    //表示光标是否移动到了上面的标题上面
    private boolean[] mTopTabFocus = new boolean[3];
    //记录中间的 视频、图片、APK被看到的状态
    private boolean[] mMediaListFocus = new boolean[8];
    public boolean mIsFileList = false;
    public float mRectBgXScale = 6.0F;
    public float mRectBgYScale = 3.0F;
    public float iconSpace = 0.0F;
    public float iconWidth = 1.14F;
    public float iconHeight = 1.14F;
    public float scaleAnimTime = 250.0F;
    protected boolean mThemeLoaded = false;

    protected boolean mIsTitleLoaded = false;
    //mCurListTitleId记录文件的名字被初始化没有
    protected int mCurListTitleId;
    //告诉系统点击了上一页 下一页 要重新开始画图了 true表示要重画
    public boolean mIsLoadingDelayed = false;
    public long mCurrentTime = 0L;
    public float mLoadingAngle;
    //获取各种存储空间的数据
    public ArrayList<DiskInfo> mDiskInfoList = new ArrayList<DiskInfo>();
    public ArrayList<DiskInfo> mDiskSDInfoList = new ArrayList<DiskInfo>();
    public ArrayList<DiskInfo> mDiskUInfoList = new ArrayList<DiskInfo>();
    public ArrayList<DiskInfo> mMountedDiskList = new ArrayList<DiskInfo>();
    public ArrayList<DiskInfo> mShowDiskList = new ArrayList<DiskInfo>();

    private long mTimeStamp;
    private SimpleDateFormat mFormat;
    private int timeTextureId;
    private boolean lastIsPower;
    private int lastPower;
    private String lastTime;
    int currentPower;
    boolean isPower;
    Bitmap power;
    private boolean isNeedHeadset;
    private boolean isNeedSound;
    private TextureRect textRect;

    private int[] statusIconIds = new int[3];
    private float statusIconMargin = 0.1F;
    private float statusIconSize = 0.2F;
    private int statusIdHeadset;
    private int statusIdSound;
    private String SurplusSize;
    private String TotalSize;
    private TextureRect iconStateTextureRect;
    private int bluetoothConnectedTextureId;
    private int bluetoothOffTextureId;
    private int bluetoothOnTextureId;
    private int wifi1TextureId;
    private int wifi2TextureId;
    private int wifi3TextureId;
    private int wifi4TextureId;
    private int wifiOffTextureId;
    private int sdcardInTextureId;
    private int sdcardOutTextureId;
    private float SDalpha = 1.0F;
    private float Ualpha = 1.0F;


    public MySurfaceView(Context context) {
        super(context);
        //枚举类型的数组
        mFileTypes = new FileType[]{FileType.VIDEO, FileType.PICTURE,
                FileType.APK};
        mUpdateFlag = true;
        mUpdateListRun = new UpdateListRunnable();
        mTheme = "sence_light1";
        lastTime = "";
        isPower = false;
        currentPower = 0;
        lastPower = -1;
        lastIsPower = false;
        mResContext = null;
        mContext = context;
        power = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.power_run);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(new EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 gl, EGLDisplay display) {
                int[] params = new int[]{EGL10.EGL_SURFACE_TYPE,
                        EGL10.EGL_WINDOW_BIT, EGL10.EGL_RENDERABLE_TYPE, 4,
                        EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_DEPTH_SIZE, 8,
                        EGL10.EGL_SAMPLE_BUFFERS, 1, EGL10.EGL_SAMPLES, 4,
                        EGL10.EGL_STENCIL_SIZE, 0, EGL10.EGL_NONE};
                EGLConfig[] config = new EGLConfig[1];
                gl.eglChooseConfig(display, params, config, 1, new int[1]);
                return config[0];
            }
        });
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        LocalImageLoader.getInstance().loadThumbCache(context);
        mDeviceManager = new DeviceManager(context);
        initDeviceList();
        initMountedDeviceList();
        mCategoryHelper = new FileCategoryHelper(context);
        mComparatorHelper = new FileComparatorHelper();
        initFileList();
    }

    //安装APK
    @SuppressLint("ShowToast")
    private void installApk(Context context, File apkFile) {
        if (mDeviceManager.getInternalDevicesList() != null
                && mDeviceManager.getInternalDevicesList().size() != 0) {
            long free = FileUtils.getAvailableSize((String) mDeviceManager
                    .getInternalDevicesList().get(0));
            long size = FileUtils.getFileSize(apkFile);
            if (size <= 0L) {
                Toast.makeText(context,
                        context.getString(R.string.txt_tip_apkerror), Toast.LENGTH_SHORT).show();
                //重置对话框的类型
                mOptMenuType = -1;
                //对话框消除
                mIsOptMenuShow = false;
                return;
            }

            if (free >= size * 2L) {
                if (Util.isKaminoRom()) {
                    Intent intent = new Intent();
                    //发送一个intent-filter为com.kamino.action.INSTALL_NODIPLAY的广播
                    intent.setAction("com.kamino.action.INSTALL_NODIPLAY");
                    intent.putExtra("packagename", apkFile.getAbsolutePath());
                    context.sendBroadcast(intent);
                    return;
                }
                //安装文件的意图intent
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setDataAndType(Uri.fromFile(apkFile),
                        "application/vnd.android.package-archive");
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                context.startActivity(intent);
                return;
            }
        }

        Toast.makeText(context, context.getString(R.string.txt_tip_nospace), Toast.LENGTH_SHORT)
                .show();
        //重置对话框的类型
        mOptMenuType = -1;
        //消除对话框
        mIsOptMenuShow = false;
    }

    private int getPageIndex(int index) {
        return 1 + index / 8;
    }

    public void setTimeTextureId() {
        long curTime = System.currentTimeMillis();
        if (mTimeStamp <= 0L) {
            mTimeStamp = curTime;
        } else if (curTime - mTimeStamp <= 2000L) {
            return;
        }

        if (mFormat == null) {
            mFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        }

        String timeText = mFormat.format(new Date(curTime));
        if (timeTextureId != -1
                && (!timeText.equals(lastTime) || lastPower != currentPower || lastIsPower != isPower)) {
            int[] textures = new int[]{timeTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            lastTime = timeText;
            lastPower = currentPower;
            lastIsPower = isPower;
            timeTextureId = Utils.initTexture(Utils.generateWLT(currentPower,
                    170, 52, power, isPower, timeText));
            //timeTextureId = Utils.initTexture(Utils.getRectBitmap(Color.RED));
        }
    }

    //眼睛是否看到了物体
    static boolean isLookingAtObject(float width, float height, float radius) {
        float[] initVec = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] objPosVec = new float[4];
        double yaw1 = Math.atan2(width / 2.0f, radius);
        double pitch1 = Math.atan2(height / 2.0f, radius);

        Matrix.multiplyMV(objPosVec, 0, MatrixState.getHeadMatrix(), 0,
                initVec, 0);

        float yaw = (float) Math.atan2(objPosVec[0], -objPosVec[2]);
        float pitch = (float) Math.atan2(objPosVec[1], -objPosVec[2]);

        return Math.abs(yaw) < Math.abs(yaw1)
                && Math.abs(pitch) < Math.abs(pitch1);
    }

    private static int calculateInSampleSize(BitmapFactory.Options opt, int width, int height) {
        int result = 1;

        if (opt.outHeight > height || opt.outWidth > width) {
            int hr = Math.round((float) opt.outHeight / (float) height);
            int wr = Math.round((float) opt.outWidth / (float) width);
            if (hr >= wr) {
                System.out.println("calculateInSampleSize" + hr);
                return hr;
            }

            result = wr;
        }
        System.out.println("calculateInSampleSize" + result);
        return result;
    }

    public static Bitmap getBackground(Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        System.out.println(is.hashCode());
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        opt.inSampleSize = calculateInSampleSize(opt, 2000, 1000);
        opt.inJustDecodeBounds = false;
        System.out.println("OK");
        return BitmapFactory.decodeStream(is, null, opt);
    }

    //初始化背景球
    void initTheme() {
        try {
            mResContext = mContext.createPackageContext("com.kamino.settings",
                    Context.CONTEXT_IGNORE_SECURITY);

            if (mResContext != null) {
                //画背景球
                mTheme = mResContext.getSharedPreferences("Theme", 5)
                        .getString("Theme_id", "sence_light1");
                mThemeResId = mResContext.getResources().getIdentifier(mTheme,
                        "drawable", mResContext.getPackageName());
                if (mThemeTextureId > 0) {
                    GLES20.glDeleteTextures(1, new int[]{mThemeTextureId}, 0);
                }
            }
        } catch (NameNotFoundException ex) {
            mThemeTextureId = Utils.initTexture(getBackground(mContext,
                    R.drawable.sence_light1));
            //Log.e("hehe", "name no found");
        }
    }

    //更新背景球背景
    void updateTheme() {
        if (mThemeResId > 0 && mThemeTextureId <= 0) {
            if (mThemeLoaded) {
                mThemeTextureId = Utils
                        .initTexture(LocalImageLoader.getInstance()
                                .getLocalBitmap(mResContext, mThemeResId));
                return;
            }

            mResExecutor.execute(new LoadThemeResRunnable());
        }
    }

    //初始化各控件
    void initTexture() {
        mTextureBall = new TextureBall(mContext);
        mTextureRect = new TextureRect(mContext);
        textRect = new TextureRect(mContext);
        iconStateTextureRect = new TextureRect(mContext);

        Resources res = getResources();
        setTimeTextureId();
        //画返回键


        mIconBackId = Utils.initTexture(res, R.drawable.ic_back);
        //光标移动到返回键
        mIconBackOnId = Utils.initTexture(res, R.drawable.ic_back_on);
        //白色向上键
        mIconUpId = Utils.initTexture(res, R.drawable.ic_up);
        //光标移动到向上键
        mIconUpOnId = Utils.initTexture(res, R.drawable.ic_up_on);
        //黑色向上键
        mIconUpNoneId = Utils.initTexture(res, R.drawable.ic_up_none);
        //白色向下键
        mIconDownId = Utils.initTexture(res, R.drawable.ic_down);
        //光标移动到向下键
        mIconDownOnId = Utils.initTexture(res, R.drawable.ic_down_on);
        //黑色向下键
        mIconDownNoneId = Utils.initTexture(res, R.drawable.ic_down_none);
        /*
         * mIconXId = Utils.initTexture(res, R.drawable.ic_x); mIconXOnId =
		 * Utils.initTexture(res, R.drawable.ic_x_on); mIconDeleteId =
		 * Utils.initTexture(res, R.drawable.ic_delete); mIconDeleteOnId =
		 * Utils.initTexture(res, R.drawable.ic_delete_on); mIconDeleteRedId =
		 * Utils.initTexture(res, R.drawable.ic_delete_red);
		 */
        //地板键
        mIconResetId = Utils.initTexture(res, R.drawable.ic_reset);
        //光标移动到地板键
        mIconResetOnId = Utils.initTexture(res, R.drawable.ic_reset_on);
        //视频中间的播放键
        mIconPlayId = Utils.initTexture(res, R.drawable.ic_play);
        //安装包默认的图标
        mIconLauncherId = Utils.initTexture(res, R.drawable.ic_launcher);
        //文件夹的图标
        mIconFilesId = Utils.initTexture(res, R.drawable.ic_files);
        //单个文件的图标
        mIconFileId = Utils.initTexture(res, R.drawable.ic_file);
        //选中之后的边框背景
        mIconBgId = Utils.initTexture(res, R.drawable.ic_bg);
        //文件存储的图片被选中的边框
        mIconSelectionId = Utils.initTexture(res, R.drawable.ic_selection);
        /*
		 * mTxtEmptyId = Utils.initTexture(Utils.generateWLT(
		 * res.getString(R.string.txt_empty), 32.0F,
		 * res.getColor(R.color.text_color), 40, Alignment.ALIGN_CENTER));
		 */

        wifiOffTextureId = Utils.initTexture(res,
                R.drawable.ic_wifi_off);
        wifi1TextureId = Utils.initTexture(res,
                R.drawable.ic_wifi_1);
        wifi2TextureId = Utils.initTexture(res,
                R.drawable.ic_wifi_2);
        wifi3TextureId = Utils.initTexture(res,
                R.drawable.ic_wifi_3);
        wifi4TextureId = Utils.initTexture(res,
                R.drawable.ic_wifi_4);
        bluetoothOffTextureId = Utils.initTexture(res,
                R.drawable.ic_bluetooth_off);
        bluetoothOnTextureId = Utils.initTexture(res,
                R.drawable.ic_bluetooth_on);
        bluetoothConnectedTextureId = Utils.initTexture(
                res, R.drawable.ic_bluetooth_connected);
        sdcardOutTextureId = Utils.initTexture(res,
                R.drawable.ic_sdcard_out);
        sdcardInTextureId = Utils.initTexture(res,
                R.drawable.ic_sdcard_in);
        statusIdHeadset = Utils.initTexture(res,
                R.drawable.headset);
        statusIdSound = Utils.initTexture(res,
                R.drawable.sound);

        TestRectId = Utils.initTexture(Utils.getRectBitmap(Color.RED));

        mTxtOpenId = Utils.initTexture(Utils.generateWLT(//打开
                res.getString(R.string.txt_open), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtOpenOnId = Utils.initTexture(Utils.generateWLT(//被选中的打开
                res.getString(R.string.txt_open), 32.0F,
                res.getColor(R.color.blue), 256, 40, Alignment.ALIGN_CENTER));
        mTxtDeleteId = Utils.initTexture(Utils.generateWLT(//删除
                res.getString(R.string.txt_delete), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtDeleteOnId = Utils.initTexture(Utils.generateWLT(//被选中的删除
                res.getString(R.string.txt_delete), 32.0F,
                res.getColor(R.color.blue), 256, 40, Alignment.ALIGN_CENTER));
        mTxtCancelId = Utils.initTexture(Utils.generateWLT(//取消
                res.getString(R.string.txt_cancel), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtCancelOnId = Utils.initTexture(Utils.generateWLT(//被选中的取消
                res.getString(R.string.txt_cancel), 32.0F,
                res.getColor(R.color.blue), 256, 40, Alignment.ALIGN_CENTER));
		/*
		 * mTxtConfirmId = Utils.initTexture(Utils.generateWLT(
		 * res.getString(R.string.txt_confirm), 32.0F,
		 * res.getColor(R.color.text_color), 40, Alignment.ALIGN_CENTER));
		 * mTxtConfirmOnId = Utils.initTexture(Utils.generateWLT(
		 * res.getString(R.string.txt_confirm), 32.0F,
		 * res.getColor(R.color.blue), 40, Alignment.ALIGN_CENTER));
		 */
        mTxtInstallId = Utils.initTexture(Utils.generateWLT(//安装
                res.getString(R.string.txt_install), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtInstallOnId = Utils.initTexture(Utils.generateWLT(//被选中的安装
                res.getString(R.string.txt_install), 32.0F,
                res.getColor(R.color.blue), 256, 40, Alignment.ALIGN_CENTER));
        mTipDeleteId = Utils.initTexture(Utils.generateWLT(//删除所选文件
                res.getString(R.string.txt_tip_delete), 20.0F,
                res.getColor(R.color.text_color), 256, 128, Alignment.ALIGN_CENTER));
        mTipInstallId = Utils.initTexture(Utils.generateWLT(//后台正在安装
                res.getString(R.string.txt_tip_install), 20.0F,
                res.getColor(R.color.text_color), 256, 128, Alignment.ALIGN_CENTER));
		/*
		 * mTipInstallFinishId = Utils.initTexture(Utils.generateWLT(
		 * res.getString(R.string.txt_tip_install_finish), 20.0F,
		 * res.getColor(R.color.text_color), 128, Alignment.ALIGN_CENTER));
		 * mTipInstallFailId = Utils.initTexture(Utils.generateWLT(
		 * res.getString(R.string.txt_tip_install_fail), 20.0F,
		 * res.getColor(R.color.text_color), 128, Alignment.ALIGN_CENTER));
		 */
        //画淡蓝色
        mBlueRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
                .getColor(R.color.blue)));
        //画灰色
        mGrayRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
                .getColor(R.color.text_color_grey)));
        //画背景颜色 半透明黑色 返回键的背景
        mTransRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
                .getColor(R.color.bg_trans)));
        // mWhiteRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
        // .getColor(R.color.white)));
        //画白色
        mWhiteCircleId = Utils.initTexture(Utils.getCircleBitmap(getResources()
                .getColor(R.color.white)));
        //加载的时候出现的圈圈的背景（黑色）
        mLoadingBaseId = Utils.initTexture(res, R.drawable.loading_base);
        //加载的时候看到的圈圈的一部分（白色）
        mLoadingOnId = Utils.initTexture(res, R.drawable.loading_on);
        //大标题 分类浏览、设备存储 文字
        String[] titleArray = res.getStringArray(R.array.titles);
        String[] showstoragesArray = res.getStringArray(R.array.showstorages);
        String[] showsizeArray = res.getStringArray(R.array.showsize);
        SurplusSize = res.getString(R.string.SurplusSize);
        TotalSize = res.getString(R.string.TotalSize);
        //中间的标题  视频  图片 安装包 文字
        String[] kindsArray = res.getStringArray(R.array.kinds);
        //上面的三个存储设备
        String[] storagesArray = res.getStringArray(R.array.storages);
        //设备存储里面的标题 内部存储容量、SD卡容量 文字
        String[] contentArray = res.getStringArray(R.array.contents);
        //中间的标题  视频  图片 安装包的图标
        int[] menuIcons = new int[]{R.drawable.ic_video, R.drawable.ic_pic,
                R.drawable.ic_apk, R.drawable.ic_music};
        //中间的标题  视频  图片 安装包被选中时候的图标
        int[] menuIconsOn = new int[]{R.drawable.ic_video_on,
                R.drawable.ic_pic_on, R.drawable.ic_apk_on,
                R.drawable.ic_music_on};
        //设备存储里面的 内部存储 SD卡的图标
        int[] storageIcons = new int[]{R.drawable.ic_content_rom,
                R.drawable.ic_content_sd, R.drawable.ic_content_sd};
        //大标题 分类浏览、设备存储
        for (int i = 0; i < titleArray.length; ++i) {
            mTitleArrayId[i] = Utils.initTexture(titleArray[i], getResources()
                    .getColor(R.color.text_color), 20.0F, 80);
            mTitleArrayBlueId[i] = Utils.initTexture(titleArray[i], getResources()
                    .getColor(R.color.blue), 20.0F, 80);
        }
        for (int i = 0; i < showstoragesArray.length; ++i) {
            mshowstorageArrayId[i] = Utils.initTexture(Utils.generateWLT(
                    showstoragesArray[i], 30.0F,
                    res.getColor(R.color.text_color), 160, 40, Alignment.ALIGN_CENTER));

        }
        for (int i = 0; i < showsizeArray.length; ++i) {
            mshowsizeArrayId[i] = Utils.initTexture(Utils.generateWLT(
                    showsizeArray[i], 15.0F,
                    res.getColor(R.color.text_color), 160, 40, Alignment.ALIGN_CENTER));

        }
        //大标题 分类浏览、设备存储
        for (int i = 0; i < storagesArray.length; ++i) {
            mstorageArrayId[i] = Utils.initTexture(storagesArray[i], getResources()
                    .getColor(R.color.text_color), 20.0F, 80);
            mstorageArrayBlueId[i] = Utils.initTexture(storagesArray[i], getResources()
                    .getColor(R.color.blue), 20.0F, 80);
        }
        //中间的标题  视频  图片 安装包
        for (int i = 0; i < kindsArray.length; ++i) {
            //没看到就只画白色字
            mKindsArrayId[i] = Utils.initTexture(kindsArray[i], getResources()
                    .getColor(R.color.text_color), 20.0F, 80);
            //看到了就画蓝色字
            mKindsArrayBlueId[i] = Utils.initTexture(kindsArray[i],
                    getResources().getColor(R.color.blue), 20.0F, 80);
            //默认 中间的标题  视频  图片 安装包的图标
            mMenuIconsId[i] = Utils.initTexture(res, menuIcons[i]);
            //被看到 中间的标题  视频  图片 安装包的图标
            mMenuIconsOnId[i] = Utils.initTexture(res, menuIconsOn[i]);
        }
        //设备存储里面的标题 内部存储容量、SD卡容量
        for (int i = 0; i < contentArray.length; ++i) {
            mContentArrayId[i] = Utils.initTexture(Utils.generateWLT(
                    contentArray[i], 32.0F,
                    getResources().getColor(R.color.text_color_grey), 256, 40,
                    Alignment.ALIGN_NORMAL));
            mStorageIconsId[i] = Utils.initTexture(res, storageIcons[i]);
        }
        statusIconIds[0] = bluetoothOffTextureId;
        statusIconIds[1] = sdcardOutTextureId;
        statusIconIds[2] = wifiOffTextureId;
    }

    //为真就删除文件
    private void deleteCurFile(boolean isMediaFile) {
        if (!mCurFile.isDirectory) {
            if (mCurFile.type != FileType.VIDEO
                    && mCurFile.type != FileType.PICTURE
                    && mCurFile.type != FileType.AUDIO
                    && mCurFile.type != FileType.APK) {
                Util.deleteFile(mCurFile);
                if (isMediaFile) {
                    mCurFileList.remove(mCurIndex);
                } else {
                    mCurSortFileList.remove(mCurIndex);
                }
            } else {
                Util.deleteFileInDatabase(mContext.getContentResolver(),
                        mCurFile);
                Util.deleteFile(mCurFile);
                if (isMediaFile) {
                    mCurFileList.remove(mCurIndex);
                } else {
                    mCurSortFileList.remove(mCurIndex);
                }
            }

            if (isMediaFile) {
                if (mCurFileList.size() % 8 == 0) {
                    goUp();
                }
            } else if (mCurSortFileList.size() % 8 == 0) {
                goUp();
                return;
            }
        }

    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
        //更新设备存储设备的列表信息
    void initMountedDeviceList() {
        final ArrayList<DiskInfo> infoList = new ArrayList<DiskInfo>();

        for (int n = 0; n < mDiskInfoList.size(); n++) {
            try {
                final DiskInfo info = (DiskInfo) mDiskInfoList.get(n);
                //表示存储设备是否存在
                if (Environment.getStorageState(new File(info.path)).equals(
                        "mounted")) {
                    info.mounted = true;
                    info.total = FileUtils.getStorageTotal(info.path);
                    info.available = FileUtils.getStorageAvailable(info.path);
                    info.free = FileUtils.getStorageFree(info.path);
                    info.freeRatio = (float) FileUtils.getFreeSize(info.path)
                            / (float) FileUtils.getTotalSize(info.path);
					/*Log.i("BLUEBLUEBLUEBLUEBLUE", info.total + "total");
					Log.i("BLUEBLUEBLUEBLUEBLUE", info.free + "free");
					Log.i("BLUEBLUEBLUEBLUEBLUE", FileUtils.getFreeSize(info.path) + "freeRatio");
					Log.i("BLUEBLUEBLUEBLUEBLUE", FileUtils.getTotalSize(info.path)+"freeRatio");*/
                    infoList.add(info);
                } else {
                    info.mounted = false;
                }
            } catch (Exception e) {
                continue;
            }
        }

        mMountedDiskList = infoList;
        //表示存在的存储设备有几页 以4个为一页
        mMountedDiskLines = 1 + (-1 + mMountedDiskList.size()) / 4;
        //表示存储空间数据已经初始化完成
        mMountedDeviceListInited = true;
        return;
    }

    //获取各种存储空间的数据并保存在mDiskInfoList集合里面
    private void initDeviceList() {
        mDiskInfoList.clear();
        try {
            if (mDeviceManager.getInternalDevicesList() != null
                    && mDeviceManager.getInternalDevicesList().size() != 0) {
                //只有一个
                for (int i = 0; i < mDeviceManager.getInternalDevicesList()
                        .size(); ++i) {
                    final DiskInfo info = new DiskInfo((String) mDeviceManager
                            .getInternalDevicesList().get(i));
                    info.name = "Internal Storage: ";// 内部存储的名字
                    info.type = 0;
                    info.total = FileUtils.getStorageTotal(info.path);
                    info.available = FileUtils.getStorageAvailable(info.path);
                    info.free = FileUtils.getStorageFree(info.path);
                    info.freeRatio = FileUtils.getFreeSize(info.path)
                            / FileUtils.getTotalSize(info.path);
                    mDiskInfoList.add(info);

                }
            }
            if (mDeviceManager.getSdDevicesList() != null
                    && mDeviceManager.getSdDevicesList().size() != 0) {
                for (int j = 0; j < mDeviceManager.getSdDevicesList().size(); ++j) {
                    final DiskInfo info = new DiskInfo((String) mDeviceManager
                            .getSdDevicesList().get(j));
                    info.name = "SD card: ";// "SD卡的名字";
                    info.type = 1;
                    info.total = FileUtils.getStorageTotal(info.path);
                    info.available = FileUtils.getStorageAvailable(info.path);
                    info.free = FileUtils.getStorageFree(info.path);
                    info.freeRatio = FileUtils.getFreeSize(info.path)
                            / FileUtils.getTotalSize(info.path);
                    mDiskInfoList.add(info);
                    mDiskSDInfoList.add(info);
                    for (DiskInfo diskInfo : mDiskSDInfoList) {
                        Log.e("测试外设路径mDiskSDInfoList",""+diskInfo.path);
                    }
                }
            }
            if (mDeviceManager.getUsbDevicesList() != null) {
                final int size = mDeviceManager.getUsbDevicesList().size();
                int count = 0;
                if (size != 0) {
                    while (count < mDeviceManager.getUsbDevicesList().size()) {
                        final DiskInfo info = new DiskInfo(
                                (String) mDeviceManager.getUsbDevicesList()
                                        .get(count));
                        info.name = "U Disk: ";// "U盘的名字";
                        info.type = 2;
                        info.total = FileUtils.getStorageTotal(info.path);
                        info.available = FileUtils.getStorageAvailable(info.path);
                        info.free = FileUtils.getStorageFree(info.path);
                        info.freeRatio = FileUtils.getFreeSize(info.path)
                                / FileUtils.getTotalSize(info.path);
                        mDiskInfoList.add(info);
                        mDiskUInfoList.add(info);
                        ++count;
                    }
                }
                for (DiskInfo diskInfo : mDiskUInfoList) {
                    Log.e("测试外设路径mDiskUInfoList",""+diskInfo.path);
                }
            }
        } catch (Exception e) {
        }
        mShowDiskList = mDiskInfoList ;
        for (DiskInfo diskInfo : mDiskInfoList) {
            Log.e("测试外设路径mDiskInfoList",""+diskInfo.path);
        }
    }

    private void returnPathResult() {
        Intent intent = new Intent();
        intent.putExtra("Path", (new File(mCurFile.path)).getAbsolutePath());
        ((MainActivity) mContext).setResult(100, intent);
        ((MainActivity) mContext).finish();
    }

    public final boolean sortFileList(String path, FileComparatorHelper helper) {
        boolean success = true;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            mCurSortFileList.clear();
            File[] list = file.listFiles(mCategoryHelper.getFileFilter());
            if (list != null) {
                for (int i = 0; i < list.length; ++i) {
                    String absPath = list[i].getAbsolutePath();
                    if (Util.isNotAsecFile(absPath)
                            && Util.isNormalFile(absPath)) {
                        FileInfo info = Util.getFileInfo(list[i],
                                mCategoryHelper.getFileFilter(), false);
                        if (info != null) {
                            mCurSortFileList.add(info);
                        }
                    }
                }

                if (mCurSortFileList.size() > 0) {
                    System.setProperty("java.util.Arrays.useLegacyMergeSort",
                            "true");
                    Collections.sort(mCurSortFileList, helper.getComparator());
                    return success;
                }
            }
        } else {
            success = false;
        }

        return success;
    }

    //初始化文件的数量 并保存在FileCategoryHelper的FileTypeStat
    public final void initFileList() {
        mCategoryHelper.initFileTypeStat();
        mMediaFileCount[0] = mCategoryHelper.getFileTypeStat(FileType.VIDEO).count;
        mMediaFileCount[1] = mCategoryHelper.getFileTypeStat(FileType.PICTURE).count;
        mMediaFileCount[2] = mCategoryHelper.getFileTypeStat(FileType.APK).count;
        mFileListInited = true;
    }

    public void onBatteryChanged(int percent) {
        currentPower = percent;
    }

    public void onBatteryPower(boolean charge) {
        isPower = charge;
    }

    @Override
    public void onBluetoothStateChanged(int state) {
        if (state == 2) {
            statusIconIds[0] = bluetoothConnectedTextureId;
        } else if (state != 12 && state != 1 && state != 0 && state != 3) {
            statusIconIds[0] = bluetoothOffTextureId;
        } else {
            statusIconIds[0] = bluetoothOnTextureId;
        }
    }

    @Override
    public void onHeadsetStateChanged(boolean plugin) {
        isNeedHeadset = plugin;
    }

    @Override
    public void onNetworkStateChanged(int type, int state, int rssi) {
        if (type == 1 && state == 0) {
            if (rssi == 0) {
                statusIconIds[2] = wifi1TextureId;
            } else if (rssi == 1) {
                statusIconIds[2] = wifi2TextureId;
            } else if (rssi == 2) {
                statusIconIds[2] = wifi3TextureId;
            } else if (rssi == 3) {
                statusIconIds[2] = wifi4TextureId;
            }
        } else {
            statusIconIds[2] = wifiOffTextureId;
        }
    }

    @Override
    public void onSoundStateChanged(boolean mute) {
        isNeedSound = mute;
    }

    @Override
    public void onTFStateChanged(boolean plugin) {
        if (plugin) {
            statusIconIds[1] = sdcardInTextureId;
        } else {
            statusIconIds[1] = sdcardOutTextureId;
        }
    }

    public final void click() {
        //Log.e("测试click", "mIsFileList的值是" + mIsFileList);
        if (mIsFileList) {
            //地板的箭头被看到了
            if (mResetHeadFocus) {
                //调用这个方法会使视角变回正中央
                resetHeadTracker();
            } else {
                //在第几层
                switch (mMenuLevel) {
                    case 0:

                        if (mTopTabFocus[0]) {
                            mTabNuTop = 0;
                            mShowDiskList = mDiskInfoList;
                        }
                        if (mTopTabFocus[1]) {
                            if(SDalpha==1.0F){
                                mTabNuTop = 1;
                                mShowDiskList = mDiskSDInfoList;
                            }

                        }
                        if (mTopTabFocus[2]) {
                            if(Ualpha==1.0F){
                                mTabNuTop = 2;
                                mShowDiskList = mDiskUInfoList ;
                            }

                        }

					/*//光标在第一个栏目 也就是视频
					if (mMediaListFocus[0]) {
						mTabNo = 0;
						return;
					}
					//光标在第二个栏目 也就是图片
					if (mMediaListFocus[1]) {
						mTabNo = 1;
						mPageNo = 0;
						return;
					}
					//光标在第三个栏目 也就是APK
					if (mMediaListFocus[2]) {
						mTabNo = 1;
						mPageNo = 0;
						return;
					}*/
					/*//点击返回键
					if (mToolBtnFocus[0]) {
						mToolBtnFocus[0] = false;
						goReturn();
						return;
					}*/
                        //记录存储设备的页数大于1
                        if (mMountedDiskLines > 1) {
                            //向上键
                            if (mToolBtnFocus[1]) {
                                goUp();
                                return;
                            }
                            //向下键
                            if (mToolBtnFocus[2]) {
                                goDown();
                                return;
                            }
                        }
                       // Log.e("测试click", "mTabNo的值是" + mTabNo);
                        //在分类浏览栏目里面
                        if (mTabNo == 0) {
                            for (int i = 0; i < 3; ++i) {
                                if (!mMediaListFocus[mTabNuLeft] && mMediaListFocus[i]) {
                                   // Log.e("测试click", "mMediaListFocus[i]的值是" + mMediaListFocus[i]);
                                    mTabNuLeft = i;
                                    mCurMediaType = i;
                                    mIsMediaUpdated = false;
                                    //这个方法令mIsMediaUpdated = true
                                    //进入到了显示文件的界面
                                    //线程执行任务 找出当前要展示的类型的所有文件并保存在集合中
                                    mFileExecutor.execute(new ScanListRunnable(i));
                                    //第一页
                                    mPageNo = 0;
                                    //告诉系统点击了上一页 下一页 要重新开始画图了 true表示要重画
                                    mIsLoadingDelayed = true;
                                    //第一层
                                    //mMenuLevel = 1;
                                    return;
                                }
                            }
                            //在设备存储栏目里面
                        } else {
                            //文件被看到
                            if (mFocusIndex >= 0) {
                                mCurListPath = ((DiskInfo) mMountedDiskList
                                        .get(mFocusIndex)).path;
                                mPathStack.push(mCurListPath);
                                mPageStack.push(Integer.valueOf(mPageNo));
                                mIsMediaUpdated = false;
                                //这个方法令mIsMediaUpdated = true
                                //进入到了显示文件的界面
                                mFileExecutor.execute(new SortListRunnable());
                                mIsTitleLoaded = false;
                                mPageNo = 0;//第一页
                                mMenuLevel = 2;//第二层
                                return;
                            }

                        }
                        //Log.e("测试click", "mFocusIndex的值是" + mFocusIndex);
                        //点击返回键
                        if (mToolBtnFocus[0]) {
                            mToolBtnFocus[0] = false;
                            goReturn();
                            //点击向上键
                        } else if (mToolBtnFocus[1]) {
                            goUp();
                            //点击向下键
                        } else if (mToolBtnFocus[2]) {
                            goDown();
                        }
                        //弹出了对话框
                        if (mIsOptMenuShow) {
                            //弹出的是删除所选文件的对话框
                            if (mOptMenuType == 3) {
                                //点击删除
                                if (mFileOptFlag[4]) {
                                    deleteCurFile(true);
                                }
                                //删除文件之后重置状态
                                mFileOptFlag[4] = false;
                                //重置对话框的类型
                                mOptMenuType = -1;
                                //清除对话框
                                mIsOptMenuShow = false;
                                return;
                            }
                            //正在查看图片的状态
                            if (mOptMenuType == 5) {
                                //重置对话框的类型
                                mOptMenuType = -1;
                                //清除对话框
                                mIsOptMenuShow = false;
                                return;
                            }
                            //点击的是打开
                            if (mFileOptFlag[2]) {
                                //打开图片
                                if (mCurFile.type == FileType.PICTURE) {
                                    //点击的是图片就弹出type=5图片放大动画
                                    mOptMenuType = 5;
                                    //从1.2*0.5倍开始放大到1.2*1.0倍
                                    scale3 = 0.5F;
                                    //默认是-1 表示图片还没有画出来
                                    mCurFileBmpId = -1;
                                    mFileOptFlag[2] = false;
                                    return;
                                }
                                //打开视频
                                if (mCurFile.type == FileType.VIDEO) {
                                    //播放视频
                                    Util.playFile(mContext, mCurFile);
                                    mFileOptFlag[2] = false;
                                }
                            } else {
                                //点击的是安装
                                if (mFileOptFlag[3]) {
                                    //弹出后天正在安装的对话框
                                    mOptMenuType = 4;
                                    //安装文件
                                    installApk(mContext, new File(mCurFile.path));
                                    mFileOptFlag[3] = false;
                                    return;
                                }
                                //点击的是删除
                                if (mFileOptFlag[1]) {
                                    mFileOptFlag[1] = false;
                                    //弹出删除所选文件的对话框
                                    mOptMenuType = 3;
                                    return;
                                }
                            }
                            //重置对话框的类型（点的是空白处）
                            mOptMenuType = -1;
                            //清除对话框
                            mIsOptMenuShow = false;
                            return;
                        }
                       // Log.e("测试click", "mFocusIndex的值是" + mFocusIndex);
                        //文件被选中的时候点击确定 把mFocusIndex的值赋值给mCurIndex
                        if (mFocusIndex >= 0) {
                            //文件被看到
                            mCurIndex = mFocusIndex;
                            mCurFile = (FileInfo) mCurFileList.get(mFocusIndex);
                            //被看到的不是视频、图片、音频
                            if (mCurFile.type != FileType.VIDEO
                                    && mCurFile.type != FileType.PICTURE
                                    && mCurFile.type != FileType.AUDIO) {
                                //是APK
                                if (mCurFile.type == FileType.APK) {
                                    //弹出安装 删除 取消 的对话框
                                    mOptMenuType = 1;
                                    //是压缩包
                                } else if (mCurFile.type == FileType.ZIP
                                        && ((MainActivity) mContext).isGetPath()) {
                                    //把压缩包的路径传给了MainActivity但是没有做处理
                                    returnPathResult();
                                } else {
                                    //不是这些类型一律弹出  删除 取消
                                    mOptMenuType = 2;
                                }
                            } else {
                                //被看到的是视频、图片、音频就弹出 打开、删除、取消的对话框
                                mOptMenuType = 0;
                            }
                            //弹出对话框
                            mIsOptMenuShow = true;
                            return;
                        }

                       // Log.e("测试click", "mFocusIndex的值是" + mFocusIndex);
                        // 第一页
                        break;
                    case 1:
                        break;
                    //在第二层 基本就是在设备存储的文件存储里面了 其他的只有0层和1层
                    case 2:
                        //返回键
                        if (mToolBtnFocus[0]) {
                            mToolBtnFocus[0] = false;
                            goReturn();
                            //向上键
                        } else if (mToolBtnFocus[1]) {
                            goUp();
                            //向下键
                        } else if (mToolBtnFocus[2]) {
                            goDown();
                        }
                        //弹出了对话框
                        if (mIsOptMenuShow) {
                            //删除所选文件
                            if (mOptMenuType == 3) {
                                //点击删除
                                if (mFileOptFlag[4]) {
                                    deleteCurFile(false);
                                }

                                mFileOptFlag[4] = false;
                                //重置对话框状态
                                mOptMenuType = -1;
                                //清除对话框
                                mIsOptMenuShow = false;
                                return;
                            }
                            //查看图片的状态
                            if (mOptMenuType == 5) {
                                mOptMenuType = -1;
                                //清除对话框
                                mIsOptMenuShow = false;
                                return;
                            }
                            //点击打开
                            if (mFileOptFlag[2]) {
                                //打开图片
                                if (mCurFile.type == FileType.PICTURE) {
                                    mOptMenuType = 5;
                                    scale3 = 0.5F;
                                    mCurFileBmpId = -1;
                                    mFileOptFlag[2] = false;
                                    return;
                                }
                                //TODO  打开视频
                                if (mCurFile.type == FileType.VIDEO) {
                                    //播放视频
                                    Util.playFile(mContext, mCurFile);
                                    mFileOptFlag[2] = false;
                                }
                            } else {
                                //点击安装
                                if (mFileOptFlag[3]) {
                                    //弹出后台正在安装对话框
                                    mOptMenuType = 4;
                                    installApk(mContext, new File(mCurFile.path));
                                    mFileOptFlag[3] = false;
                                    return;
                                }
                                //点击删除
                                if (mFileOptFlag[1]) {
                                    mFileOptFlag[1] = false;
                                    //弹出删除所选文件对话框
                                    mOptMenuType = 3;
                                    return;
                                }
                            }
                            //重置对话框状态
                            mOptMenuType = -1;
                            //清除对话框
                            mIsOptMenuShow = false;
                            return;
                        }
                        //文件被选中
                        if (mFocusIndex >= 0) {
                            if (((FileInfo) mCurSortFileList.get(mFocusIndex)).isDirectory) {
                                mCurListPath = ((FileInfo) mCurSortFileList
                                        .get(mFocusIndex)).path;
                                mPathStack.push(mCurListPath);
                                mPageStack.push(Integer.valueOf(mPageNo));
                                mIsMediaUpdated = false;
                                //这个方法令mIsMediaUpdated = true
                                //进入到了显示文件的界面
                                mFileExecutor.execute(new SortListRunnable());
                                mIsTitleLoaded = false;
                                //第一页
                                mPageNo = 0;
                                return;
                            }

                            mCurIndex = mFocusIndex;
                            mCurFile = (FileInfo) mCurSortFileList.get(mFocusIndex);
                            //被看到的不是视频、图片、音频
                            if (mCurFile.type != FileType.VIDEO
                                    && mCurFile.type != FileType.PICTURE
                                    && mCurFile.type != FileType.AUDIO) {
                                //APK
                                if (mCurFile.type == FileType.APK) {
                                    mOptMenuType = 1;
                                    //zip
                                } else if (mCurFile.type == FileType.ZIP
                                        && ((MainActivity) mContext).isGetPath()) {
                                    //将zip的路径传给MainActivity但是没有做出处理
                                    returnPathResult();
                                } else {
                                    //弹出的是删除 取消的对话框
                                    mOptMenuType = 2;
                                }
                            } else {
                                //弹出的是打开 删除 取消的对话框
                                mOptMenuType = 0;
                            }
                            //弹出对话框
                            mIsOptMenuShow = true;
                            return;
                        }
                        break;
                    default:
                        return;
                }
            }
        }

    }

    public final void goReturn() {
        if (mIsFileList) {
            //在第几层
            switch (mMenuLevel) {
                case 0:
                    //弹出对话框时
                    if (mIsOptMenuShow) {
                        //重置对话框的类型
                        mOptMenuType = -1;
                        //清除对话框
                        mIsOptMenuShow = false;
                        return;
                    }
                    //不在分类浏览的栏目里
                    if (mTabNo != 0) {
                        mTabNo = 0;
                        return;
                    }
                    //退出应用
                    ((MainActivity) mContext).finish();
                    return;
                case 1:
                    //弹出了对话框
                    if (mIsOptMenuShow) {
                        //重置对话框的类型
                        mOptMenuType = -1;
                        //清除对话框
                        mIsOptMenuShow = false;
                        return;
                    }
                    //回到第0层
                    mIsFileList = false;
                    mMenuLevel = 0;
                    return;
                case 2:
                    //弹出了对话框
                    if (mIsOptMenuShow) {
                        //重置对话框的类型
                        mOptMenuType = -1;
                        //清除对话框
                        mIsOptMenuShow = false;
                        return;
                    }

                    if (mPathStack.size() > 0) {
                        mPathStack.pop();//清空
                        mPageNo = ((Integer) mPageStack.pop()).intValue();
                        if (mPathStack.size() > 0) {
                            mCurListPath = (String) mPathStack.peek();//弹出最后一项不删除
                            mIsMediaUpdated = false;
                            //这个方法令mIsMediaUpdated = true
                            //进入到了显示文件的界面
                            mFileExecutor.execute(new SortListRunnable());
                            mIsTitleLoaded = false;
                            return;
                        }

                        mMenuLevel = 0;
                        mPageNo = 0;
                        return;
                    }
            }
        }

    }

    //在分类浏览 设备存储之间切换
    public final void goLeft() {
        if (mMenuLevel == 0 && mTabNo > 0) {
            mTabNo += -1;
        }
    }

    //在分类浏览 设备存储之间切换
    public final void goRight() {
        if (mMenuLevel == 0 && mTabNo <= 0) {
            ++mTabNo;
            mPageNo = 0;
        }
    }

    public final void goUp() {
        if (mMenuLevel == 0 || mTabNo != 0) {
            if (mMenuLevel == 0 && mTabNo == 1) {
                //在设备存储的首页
                if (mPageNo > 0) {
                    mPageNo += -1;
                }
            } else if (mPageNo > 0) {
                //不在首页
                mPageNo += -1;
                //告诉系统点击了上一页 下一页 要重新开始画图了 true表示要重画
                mIsLoadingDelayed = true;
                mCurrentTime = System.currentTimeMillis();
                return;
            }
        }
    }

    public final void goDown() {
        if (mMenuLevel == 0 || mTabNo != 0) {
            if (mMenuLevel == 0 && mTabNo == 1) {
                //在设备存储的首页
                if (mPageNo < -1 + mMountedDiskLines) {
                    ++mPageNo;
                }
            } else if (mPageNo < -1 + mPageCount) {
                //不在首页
                ++mPageNo;
                //告诉系统点击了上一页 下一页 要重新开始画图了 true表示要重画
                mIsLoadingDelayed = true;
                mCurrentTime = System.currentTimeMillis();
                return;
            }
        }

    }

    public final void onPause() {
        super.onPause();
        mUpdateFlag = false;
        mUpdateListThread = null;
        if (mTFReceiver != null) {
            mContext.unregisterReceiver(mTFReceiver);
            mTFReceiver = null;
        }

        if (installOkReceiver != null) {
            mContext.unregisterReceiver(installOkReceiver);
            installOkReceiver = null;
        }

        if (installFailReceiver != null) {
            mContext.unregisterReceiver(installFailReceiver);
            installFailReceiver = null;
        }

    }

    public final void onResume() {
        super.onResume();
        mUpdateFlag = true;
        if (mUpdateListThread == null) {
            mUpdateListThread = new Thread(mUpdateListRun);
            // For H8
            // mUpdateListThread.start();
        }

        //动态注册广播 监听存储设备的状态
        if (mTFReceiver == null) {
            mTFReceiver = new TFBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_MOUNTED");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            filter.addAction("android.intent.action.MEDIA_REMOVED");
            filter.addDataScheme("file");
            mContext.registerReceiver(mTFReceiver, filter);
        }
        //动态注册广播 监听安装包更新和新增安装包
        if (installOkReceiver == null) {
            installOkReceiver = new InstallPackageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PACKAGE_REPLACED");
            filter.addAction("android.intent.action.PACKAGE_ADDED");
            filter.addDataScheme("package");
            mContext.registerReceiver(installOkReceiver, filter);
        }
        //动态注册广播 监听应用程序安装失败的广播
        if (installFailReceiver == null) {
            installFailReceiver = new InstallPackageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.kamino.action.INSTALL_FAILED");
            mContext.registerReceiver(installFailReceiver, filter);
        }

    }

    public final boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                click();
                Log.i("MySurfaceView", "down:--" + mFocusIndex);
            default:
                return true;
        }
    }

    //定义广播接收者  接收存储设备发送过来的状态
    class TFBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != "android.intent.action.MEDIA_BAD_REMOVAL"
                    && intent.getAction() != "android.intent.action.MEDIA_REMOVED") {
                if (intent.getAction() == "android.intent.action.MEDIA_MOUNTED") {
                    Log.d("MySurfaceView", "ACTION_MEDIA_MOUNTED");
                    initMountedDeviceList();
                    //重新计算文件的数量 并保存
                    initFileList();
                    return;
                }
            } else {
                Log.d("MySurfaceView", "ACTION_MEDIA_REMOVED");
                initMountedDeviceList();
                initFileList();
            }

        }
    }

    //定义广播接收者  接收应用程序的安装更新 新增 和安装失败
    @SuppressLint("ShowToast")
    class InstallPackageReceiver extends BroadcastReceiver {

        public final void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils
                    .equals(action, "android.intent.action.PACKAGE_ADDED")
                    && !TextUtils.equals(action,
                    "android.intent.action.PACKAGE_REPLACED")) {
                if (TextUtils
                        .equals(action, "com.kamino.action.INSTALL_FAILED")) {
                    //重置对话框的类型
                    mOptMenuType = -1;
                    //取消对话框
                    mIsOptMenuShow = false;
                    //安装失败
                    Toast.makeText(
                            context,
                            context.getResources().getString(
                                    R.string.txt_tip_install_fail), Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                //重置对话框的类型
                mOptMenuType = -1;
                //取消对话框
                mIsOptMenuShow = false;
                //安装完成
                Toast.makeText(
                        context,
                        context.getResources().getString(
                                R.string.txt_tip_install_finish), Toast.LENGTH_SHORT).show();
            }

        }
    }

    //开启线程执行耗时操作   得到所有文件的类型
    final class ScanListRunnable implements Runnable {
        private final int mType;

        ScanListRunnable(int type) {
            mType = type;
        }

        @Override
        public final void run() {
            mCurFileList = mCategoryHelper.getFileList(mFileTypes[mType],
                    SortType.TITLE);
            Log.d("scanlist", "size:" + mCurFileList.size());
            mIsMediaUpdated = true;//进入到了显示文件的界面
        }
    }

    //开启线程执行耗时操作  判断文件是否是一个文件夹
    final class SortListRunnable implements Runnable {
        @Override
        public final void run() {
            sortFileList(mCurListPath, mComparatorHelper);
            mIsMediaUpdated = true;//进入到了显示文件的界面
        }
    }

    final class UpdateListRunnable implements Runnable {
        @Override
        public final void run() {
            //更新列表 进来界面就开始 离开界面就结束
            while (mUpdateFlag) {
                if (mMenuLevel == 0) {
                    //更新设备存储设备的列表信息
                    initMountedDeviceList();
                    //初始化文件的数量 并保存在FileCategoryHelper的FileTypeStat
                    initFileList();
                }
                //1秒执行一次
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据文件类型和在哪里来画不同的图片   不用自己的图片 用系统自己给的
    final class DecodeFileRunnable implements Runnable {
        private final FileInfo mFileInfo;

        DecodeFileRunnable(FileInfo info) {
            mFileInfo = info;
        }

        @Override
        public final void run() {
            //在视频里面的图片
            if (mCurMediaType == 0) {
               // LocalImageLoader.getInstance().generateVideoThumbnail(
               //         mFileInfo.path);
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            } else {
                //不在视频里面的图片
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            }
            mFileInfo.imageDecode = true;
        }
    }

    //初始化有图片的图标
    final class DecodeTypeRunnable implements Runnable {
        private final FileInfo mFileInfo;

        DecodeTypeRunnable(FileInfo info) {
            mFileInfo = info;
        }

        @Override
        public final void run() {
            //在视频应用里面
            if (mFileInfo.type == FileType.VIDEO) {
              // LocalImageLoader.getInstance().generateVideoThumbnail(
              //          mFileInfo.path);
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            } else {
                //不在视频应用里面
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            }
            //用自己的图片
            mFileInfo.imageDecode = true;
        }
    }

    //得到安装包APP的图标
    final class LoadAppIconRunnable implements Runnable {
        private final FileInfo mFileInfo;

        LoadAppIconRunnable(FileInfo info) {
            mFileInfo = info;
        }

        @Override
        public final void run() {
            LocalImageLoader.getInstance().getAppIcon(mContext, mFileInfo.path);
            mFileInfo.imageDecode = true;
        }
    }

    //开启线程检测背景球是否设置过
    final class LoadThemeResRunnable implements Runnable {
        @Override
        public final void run() {
            if (LocalImageLoader.getInstance().getLocalBitmap(mResContext,
                    mThemeResId) != null) {
                mThemeLoaded = true;
            }
        }
    }

    class SceneRenderer implements StereoRenderer {
        long lastDrawtime;
        long currentTime2;
        long lastPrintTime;

        private SceneRenderer() {
            lastDrawtime = 0L;
            currentTime2 = 0L;
            lastPrintTime = 0L;
        }

        //画弹出框
        private void drawFileOptMenu(int type) {
            //弹出的是打开 、 删除、 取消的对话框
            if (type == 0) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                MatrixState.pushMatrix();
                MatrixState.scale(1.6F, 1.0F, 1.0F);
                //画背景 0.8透明度
                mTextureRect.drawSelf(0, 0.8F);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.3F, 0.1F);
                MatrixState.scale(1.0F, 0.125F, 1.0F);
                if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                    //被选中的打开
                    mFileOptFlag[2] = true;
                    mTextureRect.drawSelfOrigin(mTxtOpenOnId);
                } else {
                    //打开没有被选中
                    mFileOptFlag[2] = false;
                    mTextureRect.drawSelfOrigin(mTxtOpenId);
                }

                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.15F, 0.1F);
                MatrixState.scale(1.0F, 0.01F, 1.0F);
                //画灰色的线
                mTextureRect.drawSelfOrigin(mGrayRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.1F);
                MatrixState.scale(1.0F, 0.125F, 1.0F);
                if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                    //被选中的删除 记录下来
                    mFileOptFlag[1] = true;
                    mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                } else {
                    //默认的删除
                    mFileOptFlag[1] = false;
                    mTextureRect.drawSelfOrigin(mTxtDeleteId);
                }

                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.15F, 0.1F);
                MatrixState.scale(1.0F, 0.01F, 1.0F);
                //画灰色的线
                mTextureRect.drawSelfOrigin(mGrayRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.3F, 0.1F);
                MatrixState.scale(1.0F, 0.125F, 1.0F);
                if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                    //被选中的取消 记录在第0个位置
                    mFileOptFlag[0] = true;
                    mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                } else {
                    mFileOptFlag[0] = false;
                    //取消
                    mTextureRect.drawSelfOrigin(mTxtCancelId);
                }
                MatrixState.popMatrix();

                MatrixState.popMatrix();
            } else {
                //弹出的是安装、删除、取消的对话框
                if (type == 1) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.6F, 1.0F, 1.0F);
                    //画背景 0.8透明度
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.3F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //被选中的安装  记录在第三个位置
                        mFileOptFlag[3] = true;
                        mTextureRect.drawSelfOrigin(mTxtInstallOnId);
                    } else {
                        mFileOptFlag[3] = false;
                        //安装
                        mTextureRect.drawSelfOrigin(mTxtInstallId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.01F, 1.0F);
                    //画灰线
                    mTextureRect.drawSelfOrigin(mGrayRectId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        mFileOptFlag[1] = true;
                        //被选中的删除
                        mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                    } else {
                        mFileOptFlag[1] = false;
                        //默认的删除
                        mTextureRect.drawSelfOrigin(mTxtDeleteId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.01F, 1.0F);
                    //画灰线
                    mTextureRect.drawSelfOrigin(mGrayRectId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.3F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //被选中的取消
                        mFileOptFlag[0] = true;
                        mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                    } else {
                        mFileOptFlag[0] = false;
                        //取消
                        mTextureRect.drawSelfOrigin(mTxtCancelId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //弹出的是删除  取消的对话框
                if (type == 2) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.4F, 0.7F, 1.0F);
                    //画背景 0.8透明度
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //被选中的删除
                        mFileOptFlag[1] = true;
                        mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                    } else {
                        //默认的删除
                        mFileOptFlag[1] = false;
                        mTextureRect.drawSelfOrigin(mTxtDeleteId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.1F);
                    MatrixState.scale(0.8F, 0.01F, 1.0F);
                    //画灰色的线
                    mTextureRect.drawSelfOrigin(mGrayRectId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //被选中的取消
                        mFileOptFlag[0] = true;
                        mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                    } else {
                        //取消
                        mFileOptFlag[0] = false;
                        mTextureRect.drawSelfOrigin(mTxtCancelId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //点击删除文件之后弹出来的对话框
                if (type == 3) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.6F, 0.7F, 1.0F);
                    //画背景 0.8透明度
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.15F, 0.05F);
                    MatrixState.scale(1.4F, 0.7F, 1.0F);
                    //画删除所选文件四个字
                    mTextureRect.drawSelfOrigin(mTipDeleteId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(-0.3F, -0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //被选中的取消
                        mFileOptFlag[0] = true;
                        mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                    } else {
                        //取消
                        mFileOptFlag[0] = false;
                        mTextureRect.drawSelfOrigin(mTxtCancelId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.3F, -0.15F, 0.12F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //被选中的第二层 的删除 记录在第四个位置
                        mFileOptFlag[4] = true;
                        mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                    } else {
                        mFileOptFlag[4] = false;
                        //默认的删除
                        mTextureRect.drawSelfOrigin(mTxtDeleteId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //点击安装之后出现的对话框
                if (type == 4) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.6F, 0.7F, 1.0F);
                    //画背景 0.8透明度
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.05F);
                    MatrixState.scale(1.4F, 0.7F, 1.0F);
                    //画 后台正在安装 几个字
                    mTextureRect.drawSelfOrigin(mTipInstallId);
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //点击打开图片之后出来的放大的效果
                if (type == 5) {
                    //默认是-1 表示图片还没有画出来
                    if (mCurFileBmpId < 0) {
                        Bitmap bmp;
                        if (Util.getFileExtName(mCurFile.name)
                                //  将此 String 与另一个 String 比较，不考虑大小写
                                .equalsIgnoreCase("gif")) {
                            bmp = Util.getBitmap(mContext,
                                    mContext.getContentResolver(),
                                    mCurFile.path);
                        } else {
                            LocalImageLoader.getInstance();
                            bmp = LocalImageLoader.decodeThumbBitmapForFile(
                                    (String) mCurFile.path, 2048, 1024);
                        }
                        //初始化图片的bitmap
                        mCurFileBmpId = Utils.initTexture(bmp);
                        mCurFile.imageRatio = (float) bmp.getWidth()
                                / (float) bmp.getHeight();
                    } else {
                        MatrixState.pushMatrix();
                        MatrixState.translate(0.0F, 0.0F, 2.2F + -iconDistance);
                        MatrixState.scale(1.2F * mCurFile.imageRatio * scale3,
                                1.2F * scale3, 1.0F);
                        //点击图片后的放大效果
                        mTextureRect.drawSelfOrigin(mCurFileBmpId);
                        MatrixState.popMatrix();
                    }

                    if (scale3 < 1.0F) {
                        scale3 = 0.01F + scale3;
                        return;
                    }

                    scale3 = 1.0F;
                    return;
                }
            }

        }

        //画背景球
        private void drawThemeBg() {
            MatrixState.pushMatrix();
            MatrixState.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            MatrixState.scale(mThemeBgScale, mThemeBgScale, mThemeBgScale);
            //画背景球
            mTextureBall.drawSelf(mThemeTextureId);
            MatrixState.popMatrix();
        }

        //画大标题的文字 和文字下面的线 以及背景板
        private void drawTabBgAndTitle() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.0F, -iconDistance - 0.2F);
            MatrixState.scale(mRectBgXScale, mRectBgYScale, 1.0F);
            //画背后的大背景板 半透明黑色
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            for (int i = 0; i < 3; ++i) {
                MatrixState.pushMatrix();
                MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F,
                        mRectBgYScale / 2.0F - 0.4F - 0.55F * i, -iconDistance);
                if (isLookingAtObject(1.0F, 0.5F, iconDistance)) {
                    //表示光标移动到了这个栏目上
                    mMediaListFocus[i] = true;
                } else {
                    mMediaListFocus[i] = false;
                }

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.01F);
                MatrixState.scale(1.0F, 0.5F, 1.0F);
                if (mMediaListFocus[i]) {
                    mTextureRect.drawSelfOrigin(mTitleArrayBlueId[i]);
                } else {
                    mTextureRect.drawSelfOrigin(mTitleArrayId[i]);
                }

                MatrixState.popMatrix();

                //表示我们在哪个栏目里面
                if (mTabNuLeft == i) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(-0.55F, 0.05F, 0.0F);
                    MatrixState.scale(0.04F, 0.55F, 1.0F);
                    //画蓝色的线
                    mTextureRect.drawSelfOrigin(mBlueRectId);
                    MatrixState.popMatrix();
                }

                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 1.3F, 0.0F,
                    -iconDistance);
            MatrixState.scale(0.02F, 2.86F, 1.0F);
            //画长长的灰色的线
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();

            for (int i = 0; i < 3; ++i) {
                MatrixState.pushMatrix();
                MatrixState.translate(-1.0F + 1.55F * i,
                        mRectBgYScale / 2.0F - 0.25F, -iconDistance);
                if (isLookingAtObject(1.0F, 0.5F, iconDistance)) {
                    //表示光标移动到了这个栏目上
                    mTopTabFocus[i] = true;
                    if(SDalpha!=1.0F){
                        mTopTabFocus[1] = false;
                    }
                    if(Ualpha!=1.0F){
                        mTopTabFocus[2] = false;
                    }
                } else {
                    mTopTabFocus[i] = false;
                }

                if(mDiskSDInfoList.size()>0 && mDiskUInfoList.size()>0){
                    SDalpha = 1.0F;
                    Ualpha = 1.0F;
                }else if(mDiskSDInfoList.size()>0 && mDiskUInfoList.size()==0){
                    SDalpha = 1.0F;
                    Ualpha = 0.5F;
                }else if(mDiskSDInfoList.size()==0 && mDiskUInfoList.size()>0){
                    SDalpha = 0.5F;
                    Ualpha = 1.0F;
                }else{
                    SDalpha = 0.5F;
                    Ualpha = 0.5F;
                }

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.01F);
                MatrixState.scale(0.8F, 0.4F, 1.0F);
                if (mTopTabFocus[i]) {
                    mTextureRect.drawSelfOrigin(mstorageArrayBlueId[i]);
                } else {
                    if(i==1){
                        mTextureRect.drawSelf(mstorageArrayId[i],SDalpha);
                    }else if(i==2){
                        mTextureRect.drawSelf(mstorageArrayId[i],Ualpha);
                    }else{
                        mTextureRect.drawSelf(mstorageArrayId[i],1.0F);
                    }

                }

                MatrixState.popMatrix();

                //表示我们在哪个栏目里面
                if (mTabNuTop == i) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.30F, 0.01F);
                    MatrixState.scale(0.8F, 0.04F, 1.0F);
                    //画蓝色的线
                    mTextureRect.drawSelfOrigin(mBlueRectId);
                    MatrixState.popMatrix();
                }

                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.6F, mRectBgYScale / 2.0F - 0.55F,
                    -iconDistance);
            MatrixState.scale(mRectBgXScale - 1.424F, 0.02F, 1.0F);
            //画长长的灰色的线
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F, -0.4F,
                    -iconDistance - 0.01F);
            MatrixState.scale(1.2F, 0.3F, 1.0F);
            //本地存储
            mTextureRect.drawSelfOrigin(mshowstorageArrayId[mTabNuTop]);
            MatrixState.popMatrix();


            //DiskInfo info = (DiskInfo) mMountedDiskList.get(mTabNuTop);
            DiskInfo info = (DiskInfo)mShowDiskList.get(0);
            //Log.e("测试。。。。","mMountedDiskList大小"+mMountedDiskList.size());


            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F, -1.0F, -iconDistance - 0.01F);
            MatrixState.scale(1.2F, 0.35F, 1.0F);
            //存储设备的可以空间和总空间没有初始化
            if (info.sizeTextureId <= 0) {
                //初始化存储设备的可以空间和总空间
                info.sizeTextureId = Utils.initTexture(Utils
                        .generateWLT(SurplusSize + info.available + "\n" + TotalSize + info.total,
                                14.0F, mContext.getResources()
                                        .getColor(R.color.white),
                                120, 35, Alignment.ALIGN_CENTER));
            } else {
                //画存储设备的可以空间和总空间
                mTextureRect.drawSelfOrigin(info.sizeTextureId);
            }
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F, -0.65F, -iconDistance - 0.005F);
            MatrixState.scale(1.10F, 0.08F, 1.0F);
            //画存储设备下面长长的灰色的线
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.15F + 1.18F * info.freeRatio * 0.5F, -0.647F, -iconDistance);
            MatrixState.scale(1.18F * info.freeRatio, 0.08F, 1.0F);
            //根据可用百分比画可用空间的蓝色的线
            mTextureRect.drawSelfOrigin(mBlueRectId);
            MatrixState.popMatrix();


        }

        //TODO 画首页
        private void drawCategoryList() {
            //为真的时候表示数据已经获取到并保存好了
            if (mFileListInited) {
                for (int i = 0; i < 3; ++i) {
                    if (mMediaFileCountId[i] > 0) {
                        int[] textures = new int[]{mMediaFileCountId[i]};
                        GLES20.glDeleteTextures(1, textures, 0);
                        //文件的个数（*）
                        //初始化文件的个数（n）
                        mMediaFileCountId[i] = Utils.initTexture(Utils
                                .generateWLT(
                                        "(" + mMediaFileCount[i]
                                                + ")",
                                        64.0F,
                                        mContext.getResources().getColor(
                                                R.color.text_color), 256, 128,
                                        Alignment.ALIGN_CENTER));
                    }
                }
                //画完了之后就重置为false 要用的时候再重新调用initFileList（）方法获取
                mFileListInited = false;
            }

            for (int i = 0; i < 3; ++i) {
                MatrixState.pushMatrix();
                MatrixState.translate(1.5F * (float) i - 1.5F, 0.0F,
                        -iconDistance);
                if (isLookingAtObject(1.0F, 1.0F, iconDistance)) {
                    //记录中间的 视频、图片、APK被看到的状态
                    mMediaListFocus[i] = true;
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.005F);
                    MatrixState.scale(1.6F, 1.6F, 1.0F);
                    //被看到就画边框背景
                    mTextureRect.drawSelfOrigin(mIconBgId);
                    MatrixState.popMatrix();
                } else {
                    mMediaListFocus[i] = false;
                }

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.2F, 0.02F);
                MatrixState.scale(0.5F, 0.5F, 1.0F);
                //中间的 视频、图片、APK被看到
                if (mMediaListFocus[i]) {
                    //中间的标题  视频  图片 安装包 被看到的图片
                    mTextureRect.drawSelfOrigin(mMenuIconsOnId[i]);
                } else {
                    //中间的标题  视频  图片 安装包
                    mTextureRect.drawSelfOrigin(mMenuIconsId[i]);
                }
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.2F, 0.01F);
                MatrixState.scale(1.6F, 0.25F, 1.0F);
                //中间的 视频、图片、APK被看到
                if (mMediaListFocus[i]) {
                    //有焦点画蓝色字
                    mTextureRect.drawSelfOrigin(mKindsArrayBlueId[i]);
                } else {
                    //没有焦点画字
                    mTextureRect.drawSelfOrigin(mKindsArrayId[i]);
                }
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.4F, 0.02F);
                MatrixState.scale(0.6F, 0.3F, 1.0F);
                //表示文件总数的文字还没有初始化
                if (mMediaFileCountId[i] <= 0) {
                    //初始化表示文件总数的文字
                    mMediaFileCountId[i] = Utils.initTexture(Utils.generateWLT(
                            "(" + mMediaFileCount[i] + ")", 64.0F,
                            mContext.getResources()
                                    .getColor(R.color.text_color), 256, 128,
                            Alignment.ALIGN_CENTER));
                } else {
                    //画表示文件总数的文字
                    mTextureRect.drawSelfOrigin(mMediaFileCountId[i]);
                }
                MatrixState.popMatrix();
                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.8F, 1.0F + -iconDistance);
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            MatrixState.scale(1.6F, 0.8F, 1.0F);
            //画返回键背景板 半透明黑色
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                mToolBtnFocus[0] = true;
                //返回键获取焦点
                mTextureRect.drawSelfOrigin(mIconBackOnId);
            } else {
                mToolBtnFocus[0] = false;
                //默认返回键
                mTextureRect.drawSelfOrigin(mIconBackId);
            }

            MatrixState.popMatrix();
        }

        //画设备存储的首页
        private void drawDiskList() {
            //存储空间有数据
            if (mDiskInfoList.size() > 0) {
                //表示存储空间数据已经初始化完成
                if (mMountedDeviceListInited) {
                    //画出每一个存储空间的大小
                    for (int i = 0; i < mDiskInfoList.size(); ++i) {
                        DiskInfo info = (DiskInfo) mDiskInfoList.get(i);
                        if (info.mounted && info.sizeTextureId > 0) {
                            int[] textures = new int[]{info.sizeTextureId};
                            GLES20.glDeleteTextures(1, textures, 0);
                            //初始化设备存储的空间大小 可用空间/总空间
                            info.sizeTextureId = Utils.initTexture(Utils
                                    .generateWLT(info.free + "/" + info.total,
                                            32.0F, mContext.getResources()
                                                    .getColor(R.color.white),
                                            256, 128, Alignment.ALIGN_CENTER));
                        }
                    }
                    //画完了就把状态重置为0 当数据更改时再来调用
                    mMountedDeviceListInited = false;
                }

                int offset = 4 * mPageNo;

                while (true) {
                    int total;
                    //在最后一页
                    if (mPageNo == -1 + mMountedDiskLines) {
                        total = mMountedDiskList.size();
                    } else {
                        //不在最后一页表示存储设备的总数
                        total = 4 * (1 + mPageNo);
                    }

                    if (offset >= total) {
                        break;
                    }

                    if (offset < mMountedDiskList.size()
                            && mMountedDiskList.get(offset) != null) {
                        DiskInfo info = (DiskInfo) mMountedDiskList.get(offset);
                        MatrixState.pushMatrix();
                        MatrixState.translate(0.0F, 0.8F + -0.7F
                                * (float) (offset % 4), -iconDistance);
                        if (isLookingAtObject(4.0F, 0.7F, iconDistance)) {
                            mFocusIndex = offset;
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.0F, -0.05F);
                            MatrixState.scale(4.6F, 0.7F, 1.0F);
                            //画存储设备被选中时候画的边框
                            mTextureRect.drawSelfOrigin(mIconSelectionId);
                            MatrixState.popMatrix();
                        }

                        MatrixState.pushMatrix();
                        MatrixState.translate(-1.85F, 0.07F, 0.0F);
                        MatrixState.scale(0.3F, 0.3F, 1.0F);
                        //根据类型判断存储文件的类型
                        if (info.type == 0) {
                            //画内部存储的图标
                            mTextureRect.drawSelfOrigin(mStorageIconsId[0]);
                        } else {
                            //画SD卡的图标
                            mTextureRect.drawSelfOrigin(mStorageIconsId[1]);
                        }
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(-0.8F, 0.07F, 0.0F);
                        MatrixState.scale(1.6F, 0.25F, 1.0F);
                        //存储设备的名字没有初始化
                        if (info.nameTextureId <= 0) {
                            //初始化存储设备的名字
                            info.nameTextureId = Utils.initTexture(Utils
                                    .generateWLT(
                                            info.name,
                                            32.0F,
                                            mContext.getResources().getColor(
                                                    R.color.white), 256, 32,
                                            Alignment.ALIGN_NORMAL));
                        } else {
                            //画初始化存储设备的名字
                            mTextureRect.drawSelfOrigin(info.nameTextureId);
                        }
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(1.2F, 0.07F, 0.0F);
                        MatrixState.scale(1.6F, 0.8F, 1.0F);
                        //存储设备的可以空间和总空间没有初始化
                        if (info.sizeTextureId <= 0) {
                            //初始化存储设备的可以空间和总空间
                            info.sizeTextureId = Utils.initTexture(Utils
                                    .generateWLT(info.free + "/" + info.total,
                                            32.0F, mContext.getResources()
                                                    .getColor(R.color.white),
                                            256, 128, Alignment.ALIGN_CENTER));
                        } else {
                            //画存储设备的可以空间和总空间
                            mTextureRect.drawSelfOrigin(info.sizeTextureId);
                        }
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(0.0F, -0.16F, 0.01F);
                        MatrixState.scale(4.0F, 0.04F, 1.0F);
                        //画存储设备下面长长的灰色的线
                        mTextureRect.drawSelfOrigin(mGrayRectId);
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(-2.0F + 2.0F * info.freeRatio,
                                -0.16F, 0.02F);
                        MatrixState.scale(4.0F * info.freeRatio, 0.04F, 1.0F);
                        //根据可用百分比画可用空间的蓝色的线
                        mTextureRect.drawSelfOrigin(mBlueRectId);

                        MatrixState.popMatrix();

                        MatrixState.popMatrix();
                    }
                    //画完一个存储设备再画下一个
                    ++offset;
                }
            }
            //如果画设备的页数多于一页
            if (mMountedDiskLines > 1) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.8F, 1.0F + -iconDistance);
                MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                MatrixState.scale(2.0F, 0.8F, 1.0F);
                //画返回键背景板 半透明黑色
                mTextureRect.drawSelfOrigin(mTransRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(-0.5F, -1.6F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    //光标移动到了返回键上面 并记录状态
                    mToolBtnFocus[0] = true;
                    mTextureRect.drawSelfOrigin(mIconBackOnId);
                } else {
                    mToolBtnFocus[0] = false;
                    mTextureRect.drawSelfOrigin(mIconBackId);
                }
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                //在第一页
                if (mPageNo == 0) {
                    mToolBtnFocus[1] = false;
                    //画不能被选中的黑色向上键
                    mTextureRect.drawSelfOrigin(mIconUpNoneId);
                } else {
                    //不在第一页画可以被选中的白色和蓝色向上键
                    if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                        mToolBtnFocus[1] = true;
                        mTextureRect.drawSelfOrigin(mIconUpOnId);
                    } else {
                        mToolBtnFocus[1] = false;
                        mTextureRect.drawSelfOrigin(mIconUpId);
                    }
                }
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.5F, -1.6F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                //在最后一页 画不能被选中的黑色向下键
                if (mPageNo == -1 + mMountedDiskLines) {
                    mToolBtnFocus[2] = false;
                    mTextureRect.drawSelfOrigin(mIconDownNoneId);
                } else {
                    //不在最后一页画可以被选中的白色和蓝色的向下键
                    if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                        mToolBtnFocus[2] = true;
                        mTextureRect.drawSelfOrigin(mIconDownOnId);
                    } else {
                        mToolBtnFocus[2] = false;
                        mTextureRect.drawSelfOrigin(mIconDownId);
                    }
                }
                MatrixState.popMatrix();
            } else {
                //只有一页就只画一个返回键
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.8F, 1.0F + -iconDistance);
                MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                MatrixState.scale(1.6F, 0.8F, 1.0F);
                //画返回键背景板 半透明黑色
                mTextureRect.drawSelfOrigin(mTransRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                //画返回键
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[0] = true;
                    mTextureRect.drawSelfOrigin(mIconBackOnId);
                } else {
                    mToolBtnFocus[0] = false;
                    mTextureRect.drawSelfOrigin(mIconBackId);
                }
                MatrixState.popMatrix();
            }
        }

        //画文件的背景 和文件标题名 和灰色的线
        private void drawDiskBgAndTitle() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.2F, -iconDistance - 0.1F);
            //画背后的大背景板 半透明黑色
            MatrixState.scale(mRectBgXScale - 1.0F, mRectBgYScale - 0.4F, 1.0F);
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            //整个文件内容的标题
            if (!mIsTitleLoaded) {
                //mIsTitleLoaded默认是false，表示文件的名字还没有初始化
                if (mCurListTitleId <= 0) {
                    //初始化文件名
                    mCurListTitleId = Utils.initTexture(Utils.generateWLT(Util
                                    .getLastFileName(mCurListPath), 20.0F, mContext
                                    .getResources().getColor(R.color.text_color), 256, 128,
                            Alignment.ALIGN_CENTER));
                } else {
                    int[] textures = new int[]{mCurListTitleId};
                    GLES20.glDeleteTextures(1, textures, 0);
                    //初始化文件名
                    mCurListTitleId = Utils.initTexture(Utils.generateWLT(Util
                                    .getLastFileName(mCurListPath), 20.0F, mContext
                                    .getResources().getColor(R.color.text_color), 256, 128,
                            Alignment.ALIGN_CENTER));
                }

                mIsTitleLoaded = true;
            } else {
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.3F,
                        -iconDistance - 0.09F);
                MatrixState.scale(2.0F, 1.0F, 1.0F);
                //画整个文件内容的标题名
                mTextureRect.drawSelfOrigin(mCurListTitleId);
                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.51F,
                    0.01F + -iconDistance);
            MatrixState.scale(3.2F, 0.02F, 1.0F);
            //画文件标题下面长长的灰色的线
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();
        }

        //画文件的图标
        private void drawDiskFileList() {
            //进入到了显示文件的界面 并且文件数大于>0及进来了文件存储里面
            if (mIsMediaUpdated && mCurSortFileList.size() > 0) {
                //文件的页数
                mPageCount = 1 + (-1 + mCurSortFileList.size()) / 8;
                //告诉系统点击了上一页 下一页 要重新开始画图了 true表示要重画
                if (mIsLoadingDelayed
                        && System.currentTimeMillis() - mCurrentTime > 500L) {
                    mIsLoadingDelayed = false;
                }

                int offset = 8 * mPageNo;

                while (true) {
                    int total;
                    if (mPageNo == -1 + mPageCount) {
                        total = mCurSortFileList.size();
                    } else {
                        total = 8 * (1 + mPageNo);
                    }

                    if (offset >= total) {
                        break;
                    }
                    //表示文件没有画完
                    if (offset < mCurSortFileList.size()
                            && mCurSortFileList.get(offset) != null) {
                        FileInfo info = (FileInfo) mCurSortFileList.get(offset);
                        float dx = ((float) (offset % 8 % 4) - 1.5F)
                                * (iconWidth + iconSpace);
                        float dy = (0.5F - (float) (offset % 8 / 4))
                                * (iconHeight + iconSpace);

                        MatrixState.pushMatrix();
                        MatrixState.translate(dx, dy, 0.1F + -iconDistance);
                        if (isLookingAtObject(iconWidth, iconHeight,
                                iconDistance - 0.1F)) {
                            //文件被看到  用mFocusIndex记录
                            mFocusIndex = offset;
                            //没有弹出对话框
                            if (!mIsOptMenuShow) {
                                MatrixState.scale(1.1F, 1.1F, 1.0F);
                            }
                        }
                        //弹出了对话框 && 文件被点击了
                        if (mIsOptMenuShow && mCurIndex == offset) {
                            MatrixState.scale(1.1F, 1.1F, 1.0F);
                        }

                        //画文件的图标
                        //是一个文件夹
                        if (info.isDirectory) {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.01F);
                            MatrixState.scale(iconWidth - 0.32F,
                                    iconHeight - 0.32F, 1.0F);
                            //是否弹出对话框
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //画文件夹的图标
                            mTextureRect.drawSelf(mIconFilesId, alpha);
                            MatrixState.popMatrix();
                            //文件不是视频 不是图片
                        } else if (info.type != FileType.VIDEO
                                && info.type != FileType.PICTURE) {
                            //音频文件
                            if (info.type == FileType.AUDIO) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.3F,
                                        iconHeight - 0.35F, 1.0F);
                                //是否弹出对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画中间的大标题的图标  视频 图片 安装包 音乐 这里指音乐的图标
                                mTextureRect.drawSelf(mMenuIconsId[3], alpha);
                                MatrixState.popMatrix();
                                //APK文件
                            } else if (info.type == FileType.APK) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.25F,
                                        iconHeight - 0.3F, 1.0F);
                                //imageTextureId默认是-1 表示还没有画
                                if (info.imageTextureId <= 0) {
                                    //是否弹出对话框
                                    float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                    //安装包默认的图标
                                    mTextureRect.drawSelf(mIconLauncherId,
                                            alpha);
                                    if (!mIsLoadingDelayed) {
                                        if (!info.imageDecode) {
                                            mResExecutor
                                                    .execute(new LoadAppIconRunnable(
                                                            info));
                                        } else {
                                            //初始化安装包APP的图标
                                            Bitmap bmp = LocalImageLoader
                                                    .getInstance()
                                                    .getAppIcon(mContext,
                                                            info.path);
                                            if (bmp == null) {
                                                //为空就画默认的APP图标 就是一个launcher图标
                                                info.imageTextureId = mIconLauncherId;
                                            } else {
                                                //初始化安装包APP的图标
                                                info.imageTextureId = Utils
                                                        .initTexture(bmp);
                                            }
                                        }
                                    }
                                } else {
                                    //是否弹出对话框
                                    float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                    //安装包自己的图标
                                    mTextureRect.drawSelf(info.imageTextureId,
                                            alpha);
                                }

                                MatrixState.popMatrix();
                            } else {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.32F,
                                        iconHeight - 0.32F, 1.0F);
                                //是否弹出对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //单个文件的图标
                                mTextureRect.drawSelf(mIconFileId, alpha);
                                MatrixState.popMatrix();
                            }
                        } else {
                            //文件类型是视频或者图片
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.0F);
                            MatrixState.scale(iconWidth - 0.1F,
                                    iconHeight - 0.3F, 1.0F);
                            //imageTextureId默认是-1 表示图片还没有画
                            if (info.imageTextureId < 0) {
                                //是否弹出对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画后面的大背景
                                mTextureRect.drawSelf(0, alpha);
                                MatrixState.popMatrix();

                                if (!mIsLoadingDelayed) {
                                    if (!info.imageDecode) {
                                        //初始化有图片的文件
                                        mResExecutor
                                                .execute(new DecodeTypeRunnable(
                                                        info));
                                        //初始化视频文件
                                    } else if (info.type == FileType.VIDEO) {
                                        Bitmap bmp = /*LocalImageLoader
                                                .getInstance()
                                                .generateVideoThumbnail(
                                                        info.path);*/
                                                LocalImageLoader
                                                        .getInstance()
                                                        .getLocalBitmap(info.path);
                                        if (bmp != null) {
                                            //初始化视频文件
                                            info.imageTextureId = Utils
                                                    .initTexture(bmp);
                                        } else {
                                            info.imageTextureId = 0;
                                        }
                                    } else {
                                        //文件类型是图片类型
                                        Bitmap bmp;
                                        //得到gif的图片bmp
                                        if (Util.getFileExtName(info.name)
                                                .equalsIgnoreCase("gif")) {
                                            bmp = Util
                                                    .getBitmap(
                                                            mContext,
                                                            mContext.getContentResolver(),
                                                            info.path);
                                        } else {
                                            //得到普通文件的图片的bmp
                                            bmp = LocalImageLoader
                                                    .getInstance()
                                                    .getLocalBitmap(info.path);
                                        }

                                        if (bmp != null) {
                                            //初始化图片文件bmp
                                            info.imageTextureId = Utils
                                                    .initTexture(bmp);
                                        } else {
                                            info.imageTextureId = 0;
                                        }
                                    }
                                }
                            } else {
                                //是否弹出对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画图片、视频自己的图标
                                mTextureRect.drawSelf(info.imageTextureId,
                                        alpha);
                                MatrixState.popMatrix();
                            }
                            //文件类型是视频的话还要画中间的播放图标
                            if (info.type == FileType.VIDEO) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.6F,
                                        iconHeight - 0.6F, 1.0F);
                                //是否弹出了对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画视频中间的播放键
                                mTextureRect.drawSelf(mIconPlayId, alpha);
                                MatrixState.popMatrix();
                            }
                        }
                        //默认是nameTextureId=-1 表示还没有画文件的名字
                        if (info.nameTextureId <= 0) {
                            //初始化应用的名字 并存在info中
                            info.nameTextureId = Utils.initTexture(Utils
                                    .generateWLT(Utils.subStringCN(info.name, 14),
                                            32.0F, mContext.getResources()
                                                    .getColor(R.color.white),
                                            256, 128, Alignment.ALIGN_CENTER));
                        } else {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, -0.28F, -0.01F);
                            MatrixState
                                    .scale(iconWidth, iconWidth / 2.0F, 1.0F);
                            //是否弹出对话框
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //画文件的名字
                            mTextureRect.drawSelf(info.nameTextureId, alpha);
                            MatrixState.popMatrix();
                        }
                        //文件被看到&&没有弹出对话框  || 弹出了对话框 && 文件被点击了
                        if (mFocusIndex == offset && !mIsOptMenuShow
                                || mIsOptMenuShow && mCurIndex == offset) {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.0F, 0.011F);
                            MatrixState.scale(0.3F + iconWidth,
                                    0.34F + iconHeight, 1.0F);
                            //是否弹出对话框
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //被看到就画边框背景
                            mTextureRect.drawSelf(mIconBgId, alpha);
                            MatrixState.popMatrix();
                        }

                        MatrixState.popMatrix();
                    }
                    //画完之后继续画下一个文件
                    ++offset;
                }
                //进入到了显示文件的界面
            } else if (mIsMediaUpdated) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.1F + -iconDistance);
                MatrixState.scale(2.0F, 0.3125F, 1.0F);
                //画的是没有任何文件夹的情况下出现一个这样的框框
                mTextureRect.drawSelfOrigin(mIconBgId);
                MatrixState.popMatrix();
            } else {
                //画加载时候出来的圈圈
                drawLoadingIcon();
            }
            //弹出了对话框
            if (mIsOptMenuShow) {
                drawFileOptMenu(mOptMenuType);
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            MatrixState.scale(2.0F, 0.8F, 1.0F);
            //画返回键背景板 半透明黑色
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-0.5F, -1.4F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                mToolBtnFocus[0] = true;
                mTextureRect.drawSelfOrigin(mIconBackOnId);
            } else {
                mToolBtnFocus[0] = false;
                mTextureRect.drawSelfOrigin(mIconBackId);
            }
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.4F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            //不在第一页 && 没有弹出对话框
            if (mPageNo != 0 && !mIsOptMenuShow) {
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[1] = true;
                    //光标移动到了向上的按钮上
                    mTextureRect.drawSelfOrigin(mIconUpOnId);
                } else {
                    mToolBtnFocus[1] = false;
                    //向上的按钮
                    mTextureRect.drawSelfOrigin(mIconUpId);
                }
            } else {
                mToolBtnFocus[1] = false;
                mTextureRect.drawSelfOrigin(mIconUpNoneId);
            }
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.5F, -1.4F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            //不在最后一页&&没有弹出对话框
            if (mPageNo != -1 + mPageCount && !mIsOptMenuShow) {
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[2] = true;
                    //光标移动在向下的按钮上
                    mTextureRect.drawSelfOrigin(mIconDownOnId);
                } else {
                    mToolBtnFocus[2] = false;
                    //向下的按钮
                    mTextureRect.drawSelfOrigin(mIconDownId);
                }
            } else {
                mToolBtnFocus[2] = false;
                mTextureRect.drawSelfOrigin(mIconDownNoneId);
            }
            MatrixState.popMatrix();
        }

        //画加载时候出现的加载圈圈
        private void drawLoadingIcon() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.58F, 0.0F, 0.2F + -iconDistance);
            MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
            MatrixState.scale(1.0F, 1.0F, 1.0F);
            //画加载的时候出现的圈圈的背景（黑色）
            mTextureRect.drawSelfOrigin(mLoadingBaseId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.58F, 0.0F, 0.21F + -iconDistance);
            MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
            MatrixState.scale(1.0F, 1.0F, 1.0F);
            //加载的时候看到的圈圈的一部分（白色）
            mTextureRect.drawSelfOrigin(mLoadingOnId);
            MatrixState.popMatrix();

            mLoadingAngle += 3.0F;
        }

        //画相应应用的标题、背景 和灰色的线
        private void drawMediaBgAndTitle() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.2F, -iconDistance - 0.1F);
            //画背后的大背景板 半透明黑色
            MatrixState.scale(mRectBgXScale - 1.0F, mRectBgYScale - 0.4F, 1.0F);
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.3F,
                    -iconDistance);
            MatrixState.scale(1.6F, 0.25F, 1.0F);
            //画应用里面的标题
            mTextureRect.drawSelfOrigin(mKindsArrayId[mCurMediaType]);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.51F,
                    0.01F + -iconDistance);
            MatrixState.scale(3.2F, 0.02F, 1.0F);
            //画长长的灰色的线
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();
        }

        //画应用里面具体的文件列表 以及功能菜单键
        private void drawMediaFileList() {
            //进入到了显示文件的界面 && 文件数大于0
            if (mIsMediaUpdated && mCurFileList.size() > 0) {
                //总页数
                mPageCount = 1 + (-1 + mCurFileList.size()) / 8;
                //告诉系统点击了上一页 下一页 要重新开始画图了 true表示要重画
                if (mIsLoadingDelayed
                        && System.currentTimeMillis() - mCurrentTime > 500L) {
                    mIsLoadingDelayed = false;
                }
                //记录前面有几页文件*8
                int offset = 8 * mPageNo;

                while (true) {
                    int total;
                    //在最后一页
                    if (mPageNo == -1 + mPageCount) {
                        total = mCurFileList.size();
                    } else {
                        //当前页和之前页所有的图标
                        total = 8 * (1 + mPageNo);
                    }

                    if (offset >= total) {
                        break;
                    }
                    //不是（最后一个+1）文件
                    if (offset < mCurFileList.size()
                            && mCurFileList.get(offset) != null) {
                        FileInfo info = (FileInfo) mCurFileList.get(offset);
                        //分成两排  一排4个
                        float dx = ((float) (offset % 8 % 4) - 0.97F)
                                * (iconWidth + iconSpace);
                        float dy = (0.28F - (float) (offset % 8 / 4))
                                * (iconHeight + iconSpace);
                        MatrixState.pushMatrix();
                        MatrixState.translate(dx, dy, 0.02F + -iconDistance);
                        if (isLookingAtObject(iconWidth-0.1F, iconHeight-0.1F,
                                iconDistance - 0.02F) && !mIsOptMenuShow) {
                            if (outAnimPosition != offset){
                                outAnimPosition = offset;
                                scale1 = 0.0F;
                                startTime1 = System.currentTimeMillis();
                            } else {
                                if (scale1 < 0.1F) {
                                    scale1 = 0.1F * ((float) (System
                                            .currentTimeMillis() - startTime1) / scaleAnimTime);
                                } else {
                                    scale1 = 0.1F;
                                }

                                if (getPageIndex(offset) == mPageNo+1) {
                                   // MatrixState.translate(0.0F, 0.0F, scale1);
                                    MatrixState.scale(1.0F + scale1, 1.0F + scale1,
                                            1.0F);
                                }
                            }
                            mFocusIndex = offset;
                        } else if (outAnimPosition == offset) {
                            outAnimPosition = -1;
                            inAnimPosition = offset;
                            scale2 = 0.0F;
                            startTime2 = System.currentTimeMillis();
                            if (getPageIndex(offset) == mPageNo+1) {
                                //MatrixState.translate(0.0F, 0.0F, scale1);
                                MatrixState.scale(1.0F + scale1, 1.0F + scale1,
                                        1.0F);
                            }
                        } else if (inAnimPosition == offset) {
                            if (scale2 < 0.1F) {
                                scale2 = 0.1F * ((float) (System
                                        .currentTimeMillis() - startTime2) / scaleAnimTime);
                            } else {
                                scale2 = 0.1F;
                            }

                            if (getPageIndex(offset) == mPageNo+1) {
                               // MatrixState.translate(0.0F, 0.0F, 0.1F - scale2);
                                MatrixState.scale(1.1F - scale2, 1.1F - scale2,
                                        1.0F);
                            }
                        }


                        //弹出了对话框 && 文件被点击了
                        if (mIsOptMenuShow && mCurIndex == offset) {
                            MatrixState.scale(1.1F, 1.1F, 1.0F);
                        }

                        if (mCurMediaType < 2) {
                            //在视频 图片里面
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.0F);
                            MatrixState.scale(iconWidth - 0.1F,
                                    iconHeight - 0.3F, 1.0F);
                            //图片没有初始化 进去初始化
                            if (info.imageTextureId < 0) {
                                //是否弹出了对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                mTextureRect.drawSelf(0, alpha);
                                MatrixState.popMatrix();
                                //没有点击了上一页、下一页或者刚初始化
                                if (!mIsLoadingDelayed) {
                                    //图片还没有加载好
                                    if (!info.imageDecode) {
                                        //将图片路径 图片缩略图保存 执行完了令mIsLoadingDelayed=true
                                        mResExecutor
                                                .execute(new DecodeFileRunnable(
                                                        info));
                                        //图片加载完毕并在展示视频中
                                    } else if (mCurMediaType == 0) {
                                        Bitmap bmp = /*LocalImageLoader
                                                .getInstance()
                                                .generateVideoThumbnail(
                                                        info.path);*/
                                                LocalImageLoader
                                                        .getInstance()
                                                        .getLocalBitmap(info.path);
                                        if (bmp != null) {
                                            info.imageTextureId = Utils
                                                    .initTexture(bmp);
                                        } else {
                                            Log.v("测试视频。", "视频的缩略图为null" + offset);
                                            info.imageTextureId = 0;
                                        }
                                        //图片加载完毕并在照片展示中
                                    } else {
                                        Bitmap bmp;
                                        //如果照片是gif
                                        if (Util.getFileExtName(info.name)
                                                .equalsIgnoreCase("gif")) {
                                            bmp = Util
                                                    .getBitmap(
                                                            mContext,
                                                            mContext.getContentResolver(),
                                                            info.path);
                                        } else {
                                            bmp = LocalImageLoader
                                                    .getInstance()
                                                    .getLocalBitmap(info.path);
                                        }

                                        if (bmp != null) {
                                            info.imageTextureId = Utils
                                                    .initTexture(bmp);
                                        } else {
                                            info.imageTextureId = 0;
                                        }
                                    }
                                }
                            } else {
                                //是否弹出了对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画图片
                                mTextureRect.drawSelf(info.imageTextureId,
                                        alpha);
                                /*mTextureRect.drawSelf(TestRectId,
                                        alpha);*/
                                MatrixState.popMatrix();
                            }
                            //在视频里
                            if (mCurMediaType == 0) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.8F,
                                        iconHeight - 0.8F, 1.0F);
                                //是否弹出了对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画视频中间的播放键
                                mTextureRect.drawSelf(mIconPlayId, alpha);
                                MatrixState.popMatrix();
                            }
                        }//在APK应用里面
                        else {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.01F);
                            MatrixState.scale(iconWidth - 0.25F,
                                    iconHeight - 0.3F, 1.0F);
                            //APK图标还没有初始化 进去初始化
                            if (info.imageTextureId <= 0) {
                                //是否弹出了对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画安装包默认的图标
                                mTextureRect.drawSelf(mIconLauncherId, alpha);
                                //没有点击了上一页、下一页或者刚初始化
                                if (!mIsLoadingDelayed) {
                                    //图片没有加载好
                                    if (!info.imageDecode) {
                                        //初始化安装包默认的图标 并使imageDecode=true
                                        mResExecutor
                                                .execute(new LoadAppIconRunnable(
                                                        info));
                                        //图片加载好了
                                    } else {
                                        //初始化安装自己的图标
                                        Bitmap bmp = LocalImageLoader
                                                .getInstance().getAppIcon(
                                                        mContext, info.path);
                                        if (bmp == null) {
                                            info.imageTextureId = mIconLauncherId;
                                        } else {
                                            info.imageTextureId = Utils
                                                    .initTexture(bmp);
                                        }
                                    }
                                }
                                //APK图标初始化完成 可以画了
                            } else {
                                //是否弹出了对话框
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //画APK图标
                                mTextureRect.drawSelf(info.imageTextureId,
                                        alpha);
                            }
                            MatrixState.popMatrix();
                        }

                        if (info.nameTextureId <= 0) {
                            //初始化应用各自的名字
                            info.nameTextureId = Utils.initTexture(Utils
                                    .generateWLT(Utils.subStringCN(info.name, 14),
                                            32.0F, mContext.getResources()
                                                    .getColor(R.color.white),
                                            256, 256, Alignment.ALIGN_CENTER));
                        } else {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, -0.38F, -0.01F);
                            MatrixState.scale(iconWidth, iconHeight, 1.0F);
                            //是否弹出了对话框
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //画应用各自的名字
                            mTextureRect.drawSelf(info.nameTextureId, alpha);
                            MatrixState.popMatrix();
                        }
                        //文件被看到&&没有弹出对话框  || 弹出了对话框 && 文件被点击了
                        if (mFocusIndex == offset && !mIsOptMenuShow
                                || mIsOptMenuShow && mCurIndex == offset) {
                            /*MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.05F, 0.011F);
                            MatrixState.scale(0.15F + iconWidth,
                                    0.25F + iconHeight, 1.0F);
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //被看到就画边框背景
                            mTextureRect.drawSelf(mIconBgId, alpha);
                            MatrixState.popMatrix();*/
                        }

                        MatrixState.popMatrix();
                    }
                    //画完一个文件再画另一个
                    ++offset;
                }
                //文件加载好了但是文件数为0
            } else if (mIsMediaUpdated) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.58F, 0.0F, 0.1F + -iconDistance);
                MatrixState.scale(2.0F, 0.3125F, 1.0F);
                //画的是没有任何文件夹的情况下出现一个这样的框框
                mTextureRect.drawSelfOrigin(mIconBgId);
                MatrixState.popMatrix();
                //文件还没有加载好
            } else {
                //画的是加载的哪个圈圈
                drawLoadingIcon();
            }
            //弹出了对话框
            if (mIsOptMenuShow) {
                drawFileOptMenu(mOptMenuType);
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            //画返回键背景板 半透明黑色
            MatrixState.scale(2.0F, 0.8F, 1.0F);
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-0.5F, -1.4F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                mToolBtnFocus[0] = true;
                mTextureRect.drawSelfOrigin(mIconBackOnId);
            } else {
                mToolBtnFocus[0] = false;
                mTextureRect.drawSelfOrigin(mIconBackId);
            }
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.4F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            //不在第一页 && 没有弹出了对话框
            if (mPageNo != 0 && !mIsOptMenuShow) {
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[1] = true;
                    mTextureRect.drawSelfOrigin(mIconUpOnId);
                } else {
                    mToolBtnFocus[1] = false;
                    mTextureRect.drawSelfOrigin(mIconUpId);
                }
            } else {
                mToolBtnFocus[1] = false;
                mTextureRect.drawSelfOrigin(mIconUpNoneId);
            }
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.5F, -1.4F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            //不在最后一页&&没有弹出对话框
            if (mPageNo != -1 + mPageCount && !mIsOptMenuShow && mPageCount !=0) {
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[2] = true;
                    mTextureRect.drawSelfOrigin(mIconDownOnId);
                } else {
                    mToolBtnFocus[2] = false;
                    mTextureRect.drawSelfOrigin(mIconDownId);
                }
            } else {
                mToolBtnFocus[2] = false;
                mTextureRect.drawSelfOrigin(mIconDownNoneId);
            }
            MatrixState.popMatrix();
        }

        //画地板的那个箭头
        private void drawResetIcon() {
            //drawTimeEle();
            MatrixState.pushMatrix();
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            MatrixState.translate(0.0F, 0.0F, -iconDistance);
            MatrixState.scale(0.5F, 0.5F, 1.0F);
            if (isLookingAtObject(0.5F, 0.5F, iconDistance)) {
                //记录地板箭头被看到的状态
                mResetHeadFocus = true;
                if (outAnimPosition != -2) {
                    outAnimPosition = -2;
                    scale1 = 0.0F;
                    startTime1 = System.currentTimeMillis();
                } else {
                    if (scale1 < 0.1F) {
                        scale1 = 0.1F * ((float) (System.currentTimeMillis() - startTime1) / scaleAnimTime);
                    } else {
                        scale1 = 0.1F;
                    }

                    MatrixState.translate(0.0F, 0.0F, scale1);
                    MatrixState.scale(1.0F + scale1, 1.0F + scale1, 1.0F);
                }

                mTextureRect.drawSelfOrigin(mIconResetOnId);
            } else {
                mResetHeadFocus = false;
                if (outAnimPosition == -2) {
                    outAnimPosition = -1;
                    inAnimPosition = -2;
                    scale2 = 0.0F;
                    startTime2 = System.currentTimeMillis();
                    MatrixState.translate(0.0F, 0.0F, scale1);
                    MatrixState.scale(1.0F + scale1, 1.0F + scale1, 1.0F);
                } else if (inAnimPosition == -2) {
                    if (scale2 < 0.1F) {
                        scale2 = 0.1F * ((float) (System.currentTimeMillis() - startTime2) / scaleAnimTime);
                    } else {
                        scale2 = 0.1F;
                    }

                    MatrixState.translate(0.0F, 0.0F, 0.1F - scale2);
                    MatrixState.scale(1.1F - scale2, 1.1F - scale2, 1.0F);
                }

                mTextureRect.drawSelfOrigin(mIconResetId);
            }
            MatrixState.popMatrix();
        }

        //画光标小圆点
        private void drawAnchor() {
            MatrixState.pushMatrix();
          //  MatrixState.translate(0.0F, 0.0F, -1.0F);
            MatrixState.translate(0.0F, 0.0F, -iconDistance+1.5F);
            MatrixState.scale(0.045F, 0.045F, 1.0F);
            mTextureRect.drawSelf(mWhiteCircleId);
            MatrixState.popMatrix();
        }

        private void drawTimeEle() {
            // draw time text//画时间和电量
            MatrixState.pushMatrix();
            MatrixState.translate(-0.03F, 1.8F, -iconDistance);
            //			MatrixState.translate(0.16F, 0.9F, iconDistance);
            MatrixState.scale(1.31F, 0.4F, 1.0F);
            setTimeTextureId();
            textRect.drawSelfOrigin(timeTextureId);
            //textRect.drawSelf(Utils.initTexture(Utils.getRectBitmap(Color.RED)));
            MatrixState.popMatrix();

            int count = 3;
            if (isNeedHeadset) {
                //++count;
            }
            if (isNeedSound) {
                //++count;
            }

            // draw status icons
            for (int i = 0; i < count; ++i) {
                MatrixState.pushMatrix();
                float x = (statusIconSize + statusIconMargin)
                        * (float) (i - count / 2);
                if (count % 2 == 0) {
                    x = (statusIconSize + statusIconMargin)
                            * ((float) i - (float) (count - 1) / 2.0F);
                }

                MatrixState.translate(x + 1.05F, 1.8F, -iconDistance);
                MatrixState.scale(0.015F + statusIconSize, statusIconSize, 1.0F);
				/*if (i == 3) {
					if (isNeedSound) {
						iconStateTextureRect.drawSelf(statusIdSound);
					} else if (isNeedHeadset) {
						iconStateTextureRect.drawSelf(statusIdHeadset);
					}
				} else if (i == 4) {
					if (isNeedHeadset) {
						iconStateTextureRect.drawSelf(statusIdHeadset);
						}*/

                if (i == 4) {
                    Log.i("MainSurfaceView", "count==5");
                    iconStateTextureRect.drawSelfOrigin(statusIdSound);
                } else if (i == 3) {
                    if (isNeedSound && isNeedHeadset) {
                        Log.i("MainSurfaceView", "????????");
                        iconStateTextureRect.drawSelfOrigin(statusIdHeadset);
                        //iconStateTextureRect.drawSelf(statusIdSound);
                    } else if (isNeedSound) {
                        //iconStateTextureRect.drawSelf(statusIdSound);
                    } else if (isNeedHeadset) {
                        //Log.i("MainSurfaceView", "??????");
                        iconStateTextureRect.drawSelfOrigin(statusIdHeadset);
                    }
                } else {
                    iconStateTextureRect.drawSelfOrigin(statusIconIds[i]);
                }

                MatrixState.popMatrix();
            }

        }
        public final void onFinishFrame(Viewport port) {
            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }

        public void onRendererShutdown() {
        }

        public void onNewFrame(HeadTransform head) {
            if (lastDrawtime == 0L) {
                lastDrawtime = System.currentTimeMillis();
            } else {
                currentTime2 = System.currentTimeMillis();
                if (System.currentTimeMillis() - lastPrintTime > 1000L) {
                    Log.d("CurrentFrame", 1000L
                            / (System.currentTimeMillis() - lastDrawtime)
                            + " FPS");
                    lastPrintTime = currentTime2;
                }

                lastDrawtime = currentTime2;
            }

            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
                    | GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
                    GLES20.GL_ONE_MINUS_SRC_ALPHA);

            head.getHeadView(headView, 0);
            head.getEulerAngles(headAngle, 0);

            MatrixState.setHeadTrackerMatrix(headView);

            mFocusIndex = -1;

            updateTheme();

            setTimeTextureId();
        }

        public void onDrawEye(Eye eye) {
            Matrix.multiplyMM(cameraView, 0, eye.getEyeView(), 0,
                    MatrixState.getCamera(), 0);
            MatrixState.copyMVMatrix(cameraView);
            MatrixState.setProjectFrustum(eye.getPerspective(0.1f, 100f));
            //画背景球
            drawThemeBg();
            switch (mMenuLevel) {
                //第0层
                case 0:
                    //画大标题的文字 和文字下面的线 以及背景板
                    drawTimeEle();
                    drawTabBgAndTitle();
                    if (mTabNo != 0) {
                        //画设备存储的首页以及返回键
                        //drawDiskList();
                    } else {
                        //画首页 以及返回键
                        //drawCategoryList();
                    }
                    drawMediaFileList();
                    mIsFileList = true;
                    break;
                case 1:
                    //drawTimeEle();
                    //drawTabBgAndTitle();
                    //画相应应用的标题、背景 和灰色的线
                    //drawMediaBgAndTitle();
                    //画应用里面具体的文件列表 以及功能菜单键
                    //drawMediaFileList();
                    //mIsFileList = true;
                    //break;
                case 2:
                    //画文件的背景 和文件标题名 和灰色的线
                    //drawDiskBgAndTitle();
                    //画文件的图标 文件名 功能键 以及加载动画
                    //drawDiskFileList();
                    //mIsFileList = true;
            }
            //画地板的那个箭头
            drawResetIcon();
            //画光标小圆点
            drawAnchor();
        }

        public void onSurfaceCreated(EGLConfig config) {
            GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            MatrixState.setInitStack();
            MatrixState.setCamera();
            //初始化各控件
            initTexture();
            //初始化背景球
            initTheme();
            firstload();

        }

        public void firstload() {
            mTabNuLeft = 1;
            mCurMediaType = 1;
            mIsMediaUpdated = false;
            //这个方法令mIsMediaUpdated = true
            //进入到了显示文件的界面
            //线程执行任务 找出当前要展示的类型的所有文件并保存在集合中
            mFileExecutor.execute(new ScanListRunnable(1));
            //第一页
            mPageNo = 0;
            //告诉系统点击了上一页 下一页 要重新开始画图了 true表示要重画
            mIsLoadingDelayed = true;
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            return;
        }
    }
}
