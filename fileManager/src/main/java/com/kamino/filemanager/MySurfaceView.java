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
    //��¼�ذ��ͼ�걻������״̬
    public boolean mResetHeadFocus;
    //���뵽����ʾ�ļ��Ľ���
    public boolean mIsMediaUpdated = false;
    //Ĭ��û�е����Ի���
    public boolean mIsOptMenuShow = false;
    //��¼���ǵ����Ի�������͵�type
    public int mOptMenuType = 0;
    public int mCurIndex;
    public FileInfo mCurFile;
    Runnable mUpdateListRun;
    Context mContext;
    private SceneRenderer mRenderer;
    int mMenuLevel = 0;
    //mTabNo = �� ��ʾ�ڵڼ�����Ŀ����
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
    //��¼�Ի�������ݱ�ѡ�е�״̬ ���μ�¼����
    //ȡ��  ɾ��  ��   ��װ   ɾ�����ڶ����ɾ����
    private boolean[] mFileOptFlag = new boolean[8];
    //���ؼ� ���ϼ�  ���¼�  �Ľ����¼ true��ʾ������
    private boolean[] mToolBtnFocus = new boolean[3];
    //�ļ������� ��һ��int���͵�ֵmFocusIndex��¼
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
    //��ʾ�ļ����������� �ĳ�ʼ��ID
    private int[] mMediaFileCountId = new int[4];
    private int[] mContentArrayId = new int[3];
    private int[] mStorageIconsId = new int[3];
    private long[] mMediaFileCount = new long[4];
    //��¼���ĸ�APP���� 0��1,2 �ֱ����  ��Ƶ��ͼƬ��APK
    int mCurMediaType = 0;
    List<FileInfo> mCurFileList = new ArrayList<FileInfo>();
    private List<FileInfo> mCurSortFileList = new ArrayList<FileInfo>();
    String mCurListPath;
    private Stack<String> mPathStack = new Stack<String>();
    private Stack<Integer> mPageStack = new Stack<Integer>();
    private int mPageCount;
    //Ϊ���ʱ���ʾ�����Ѿ���ȡ�����������
    private boolean mFileListInited = false;
    private boolean mMountedDeviceListInited = false;
    private int mMountedDiskLines = 1;
    //��ʾ����Ƿ��ƶ�������ߵı�������
    private boolean[] mLeftTabFocus = new boolean[3];
    //��ʾ����Ƿ��ƶ���������ı�������
    private boolean[] mTopTabFocus = new boolean[3];
    //��¼�м�� ��Ƶ��ͼƬ��APK��������״̬
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
    //mCurListTitleId��¼�ļ������ֱ���ʼ��û��
    protected int mCurListTitleId;
    //����ϵͳ�������һҳ ��һҳ Ҫ���¿�ʼ��ͼ�� true��ʾҪ�ػ�
    public boolean mIsLoadingDelayed = false;
    public long mCurrentTime = 0L;
    public float mLoadingAngle;
    //��ȡ���ִ洢�ռ������
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
        //ö�����͵�����
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

    //��װAPK
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
                //���öԻ��������
                mOptMenuType = -1;
                //�Ի�������
                mIsOptMenuShow = false;
                return;
            }

            if (free >= size * 2L) {
                if (Util.isKaminoRom()) {
                    Intent intent = new Intent();
                    //����һ��intent-filterΪcom.kamino.action.INSTALL_NODIPLAY�Ĺ㲥
                    intent.setAction("com.kamino.action.INSTALL_NODIPLAY");
                    intent.putExtra("packagename", apkFile.getAbsolutePath());
                    context.sendBroadcast(intent);
                    return;
                }
                //��װ�ļ�����ͼintent
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
        //���öԻ��������
        mOptMenuType = -1;
        //�����Ի���
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

    //�۾��Ƿ񿴵�������
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

    //��ʼ��������
    void initTheme() {
        try {
            mResContext = mContext.createPackageContext("com.kamino.settings",
                    Context.CONTEXT_IGNORE_SECURITY);

            if (mResContext != null) {
                //��������
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

    //���±����򱳾�
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

    //��ʼ�����ؼ�
    void initTexture() {
        mTextureBall = new TextureBall(mContext);
        mTextureRect = new TextureRect(mContext);
        textRect = new TextureRect(mContext);
        iconStateTextureRect = new TextureRect(mContext);

        Resources res = getResources();
        setTimeTextureId();
        //�����ؼ�


        mIconBackId = Utils.initTexture(res, R.drawable.ic_back);
        //����ƶ������ؼ�
        mIconBackOnId = Utils.initTexture(res, R.drawable.ic_back_on);
        //��ɫ���ϼ�
        mIconUpId = Utils.initTexture(res, R.drawable.ic_up);
        //����ƶ������ϼ�
        mIconUpOnId = Utils.initTexture(res, R.drawable.ic_up_on);
        //��ɫ���ϼ�
        mIconUpNoneId = Utils.initTexture(res, R.drawable.ic_up_none);
        //��ɫ���¼�
        mIconDownId = Utils.initTexture(res, R.drawable.ic_down);
        //����ƶ������¼�
        mIconDownOnId = Utils.initTexture(res, R.drawable.ic_down_on);
        //��ɫ���¼�
        mIconDownNoneId = Utils.initTexture(res, R.drawable.ic_down_none);
        /*
         * mIconXId = Utils.initTexture(res, R.drawable.ic_x); mIconXOnId =
		 * Utils.initTexture(res, R.drawable.ic_x_on); mIconDeleteId =
		 * Utils.initTexture(res, R.drawable.ic_delete); mIconDeleteOnId =
		 * Utils.initTexture(res, R.drawable.ic_delete_on); mIconDeleteRedId =
		 * Utils.initTexture(res, R.drawable.ic_delete_red);
		 */
        //�ذ��
        mIconResetId = Utils.initTexture(res, R.drawable.ic_reset);
        //����ƶ����ذ��
        mIconResetOnId = Utils.initTexture(res, R.drawable.ic_reset_on);
        //��Ƶ�м�Ĳ��ż�
        mIconPlayId = Utils.initTexture(res, R.drawable.ic_play);
        //��װ��Ĭ�ϵ�ͼ��
        mIconLauncherId = Utils.initTexture(res, R.drawable.ic_launcher);
        //�ļ��е�ͼ��
        mIconFilesId = Utils.initTexture(res, R.drawable.ic_files);
        //�����ļ���ͼ��
        mIconFileId = Utils.initTexture(res, R.drawable.ic_file);
        //ѡ��֮��ı߿򱳾�
        mIconBgId = Utils.initTexture(res, R.drawable.ic_bg);
        //�ļ��洢��ͼƬ��ѡ�еı߿�
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

        mTxtOpenId = Utils.initTexture(Utils.generateWLT(//��
                res.getString(R.string.txt_open), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtOpenOnId = Utils.initTexture(Utils.generateWLT(//��ѡ�еĴ�
                res.getString(R.string.txt_open), 32.0F,
                res.getColor(R.color.blue), 256, 40, Alignment.ALIGN_CENTER));
        mTxtDeleteId = Utils.initTexture(Utils.generateWLT(//ɾ��
                res.getString(R.string.txt_delete), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtDeleteOnId = Utils.initTexture(Utils.generateWLT(//��ѡ�е�ɾ��
                res.getString(R.string.txt_delete), 32.0F,
                res.getColor(R.color.blue), 256, 40, Alignment.ALIGN_CENTER));
        mTxtCancelId = Utils.initTexture(Utils.generateWLT(//ȡ��
                res.getString(R.string.txt_cancel), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtCancelOnId = Utils.initTexture(Utils.generateWLT(//��ѡ�е�ȡ��
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
        mTxtInstallId = Utils.initTexture(Utils.generateWLT(//��װ
                res.getString(R.string.txt_install), 32.0F,
                res.getColor(R.color.text_color), 256, 40, Alignment.ALIGN_CENTER));
        mTxtInstallOnId = Utils.initTexture(Utils.generateWLT(//��ѡ�еİ�װ
                res.getString(R.string.txt_install), 32.0F,
                res.getColor(R.color.blue), 256, 40, Alignment.ALIGN_CENTER));
        mTipDeleteId = Utils.initTexture(Utils.generateWLT(//ɾ����ѡ�ļ�
                res.getString(R.string.txt_tip_delete), 20.0F,
                res.getColor(R.color.text_color), 256, 128, Alignment.ALIGN_CENTER));
        mTipInstallId = Utils.initTexture(Utils.generateWLT(//��̨���ڰ�װ
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
        //������ɫ
        mBlueRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
                .getColor(R.color.blue)));
        //����ɫ
        mGrayRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
                .getColor(R.color.text_color_grey)));
        //��������ɫ ��͸����ɫ ���ؼ��ı���
        mTransRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
                .getColor(R.color.bg_trans)));
        // mWhiteRectId = Utils.initTexture(Utils.getRectBitmap(getResources()
        // .getColor(R.color.white)));
        //����ɫ
        mWhiteCircleId = Utils.initTexture(Utils.getCircleBitmap(getResources()
                .getColor(R.color.white)));
        //���ص�ʱ����ֵ�ȦȦ�ı�������ɫ��
        mLoadingBaseId = Utils.initTexture(res, R.drawable.loading_base);
        //���ص�ʱ�򿴵���ȦȦ��һ���֣���ɫ��
        mLoadingOnId = Utils.initTexture(res, R.drawable.loading_on);
        //����� ����������豸�洢 ����
        String[] titleArray = res.getStringArray(R.array.titles);
        String[] showstoragesArray = res.getStringArray(R.array.showstorages);
        String[] showsizeArray = res.getStringArray(R.array.showsize);
        SurplusSize = res.getString(R.string.SurplusSize);
        TotalSize = res.getString(R.string.TotalSize);
        //�м�ı���  ��Ƶ  ͼƬ ��װ�� ����
        String[] kindsArray = res.getStringArray(R.array.kinds);
        //����������洢�豸
        String[] storagesArray = res.getStringArray(R.array.storages);
        //�豸�洢����ı��� �ڲ��洢������SD������ ����
        String[] contentArray = res.getStringArray(R.array.contents);
        //�м�ı���  ��Ƶ  ͼƬ ��װ����ͼ��
        int[] menuIcons = new int[]{R.drawable.ic_video, R.drawable.ic_pic,
                R.drawable.ic_apk, R.drawable.ic_music};
        //�м�ı���  ��Ƶ  ͼƬ ��װ����ѡ��ʱ���ͼ��
        int[] menuIconsOn = new int[]{R.drawable.ic_video_on,
                R.drawable.ic_pic_on, R.drawable.ic_apk_on,
                R.drawable.ic_music_on};
        //�豸�洢����� �ڲ��洢 SD����ͼ��
        int[] storageIcons = new int[]{R.drawable.ic_content_rom,
                R.drawable.ic_content_sd, R.drawable.ic_content_sd};
        //����� ����������豸�洢
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
        //����� ����������豸�洢
        for (int i = 0; i < storagesArray.length; ++i) {
            mstorageArrayId[i] = Utils.initTexture(storagesArray[i], getResources()
                    .getColor(R.color.text_color), 20.0F, 80);
            mstorageArrayBlueId[i] = Utils.initTexture(storagesArray[i], getResources()
                    .getColor(R.color.blue), 20.0F, 80);
        }
        //�м�ı���  ��Ƶ  ͼƬ ��װ��
        for (int i = 0; i < kindsArray.length; ++i) {
            //û������ֻ����ɫ��
            mKindsArrayId[i] = Utils.initTexture(kindsArray[i], getResources()
                    .getColor(R.color.text_color), 20.0F, 80);
            //�����˾ͻ���ɫ��
            mKindsArrayBlueId[i] = Utils.initTexture(kindsArray[i],
                    getResources().getColor(R.color.blue), 20.0F, 80);
            //Ĭ�� �м�ı���  ��Ƶ  ͼƬ ��װ����ͼ��
            mMenuIconsId[i] = Utils.initTexture(res, menuIcons[i]);
            //������ �м�ı���  ��Ƶ  ͼƬ ��װ����ͼ��
            mMenuIconsOnId[i] = Utils.initTexture(res, menuIconsOn[i]);
        }
        //�豸�洢����ı��� �ڲ��洢������SD������
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

    //Ϊ���ɾ���ļ�
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
        //�����豸�洢�豸���б���Ϣ
    void initMountedDeviceList() {
        final ArrayList<DiskInfo> infoList = new ArrayList<DiskInfo>();

        for (int n = 0; n < mDiskInfoList.size(); n++) {
            try {
                final DiskInfo info = (DiskInfo) mDiskInfoList.get(n);
                //��ʾ�洢�豸�Ƿ����
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
        //��ʾ���ڵĴ洢�豸�м�ҳ ��4��Ϊһҳ
        mMountedDiskLines = 1 + (-1 + mMountedDiskList.size()) / 4;
        //��ʾ�洢�ռ������Ѿ���ʼ�����
        mMountedDeviceListInited = true;
        return;
    }

    //��ȡ���ִ洢�ռ�����ݲ�������mDiskInfoList��������
    private void initDeviceList() {
        mDiskInfoList.clear();
        try {
            if (mDeviceManager.getInternalDevicesList() != null
                    && mDeviceManager.getInternalDevicesList().size() != 0) {
                //ֻ��һ��
                for (int i = 0; i < mDeviceManager.getInternalDevicesList()
                        .size(); ++i) {
                    final DiskInfo info = new DiskInfo((String) mDeviceManager
                            .getInternalDevicesList().get(i));
                    info.name = "Internal Storage: ";// �ڲ��洢������
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
                    info.name = "SD card: ";// "SD��������";
                    info.type = 1;
                    info.total = FileUtils.getStorageTotal(info.path);
                    info.available = FileUtils.getStorageAvailable(info.path);
                    info.free = FileUtils.getStorageFree(info.path);
                    info.freeRatio = FileUtils.getFreeSize(info.path)
                            / FileUtils.getTotalSize(info.path);
                    mDiskInfoList.add(info);
                    mDiskSDInfoList.add(info);
                    for (DiskInfo diskInfo : mDiskSDInfoList) {
                        Log.e("��������·��mDiskSDInfoList",""+diskInfo.path);
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
                        info.name = "U Disk: ";// "U�̵�����";
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
                    Log.e("��������·��mDiskUInfoList",""+diskInfo.path);
                }
            }
        } catch (Exception e) {
        }
        mShowDiskList = mDiskInfoList ;
        for (DiskInfo diskInfo : mDiskInfoList) {
            Log.e("��������·��mDiskInfoList",""+diskInfo.path);
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

    //��ʼ���ļ������� ��������FileCategoryHelper��FileTypeStat
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
        //Log.e("����click", "mIsFileList��ֵ��" + mIsFileList);
        if (mIsFileList) {
            //�ذ�ļ�ͷ��������
            if (mResetHeadFocus) {
                //�������������ʹ�ӽǱ��������
                resetHeadTracker();
            } else {
                //�ڵڼ���
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

					/*//����ڵ�һ����Ŀ Ҳ������Ƶ
					if (mMediaListFocus[0]) {
						mTabNo = 0;
						return;
					}
					//����ڵڶ�����Ŀ Ҳ����ͼƬ
					if (mMediaListFocus[1]) {
						mTabNo = 1;
						mPageNo = 0;
						return;
					}
					//����ڵ�������Ŀ Ҳ����APK
					if (mMediaListFocus[2]) {
						mTabNo = 1;
						mPageNo = 0;
						return;
					}*/
					/*//������ؼ�
					if (mToolBtnFocus[0]) {
						mToolBtnFocus[0] = false;
						goReturn();
						return;
					}*/
                        //��¼�洢�豸��ҳ������1
                        if (mMountedDiskLines > 1) {
                            //���ϼ�
                            if (mToolBtnFocus[1]) {
                                goUp();
                                return;
                            }
                            //���¼�
                            if (mToolBtnFocus[2]) {
                                goDown();
                                return;
                            }
                        }
                       // Log.e("����click", "mTabNo��ֵ��" + mTabNo);
                        //�ڷ��������Ŀ����
                        if (mTabNo == 0) {
                            for (int i = 0; i < 3; ++i) {
                                if (!mMediaListFocus[mTabNuLeft] && mMediaListFocus[i]) {
                                   // Log.e("����click", "mMediaListFocus[i]��ֵ��" + mMediaListFocus[i]);
                                    mTabNuLeft = i;
                                    mCurMediaType = i;
                                    mIsMediaUpdated = false;
                                    //���������mIsMediaUpdated = true
                                    //���뵽����ʾ�ļ��Ľ���
                                    //�߳�ִ������ �ҳ���ǰҪչʾ�����͵������ļ��������ڼ�����
                                    mFileExecutor.execute(new ScanListRunnable(i));
                                    //��һҳ
                                    mPageNo = 0;
                                    //����ϵͳ�������һҳ ��һҳ Ҫ���¿�ʼ��ͼ�� true��ʾҪ�ػ�
                                    mIsLoadingDelayed = true;
                                    //��һ��
                                    //mMenuLevel = 1;
                                    return;
                                }
                            }
                            //���豸�洢��Ŀ����
                        } else {
                            //�ļ�������
                            if (mFocusIndex >= 0) {
                                mCurListPath = ((DiskInfo) mMountedDiskList
                                        .get(mFocusIndex)).path;
                                mPathStack.push(mCurListPath);
                                mPageStack.push(Integer.valueOf(mPageNo));
                                mIsMediaUpdated = false;
                                //���������mIsMediaUpdated = true
                                //���뵽����ʾ�ļ��Ľ���
                                mFileExecutor.execute(new SortListRunnable());
                                mIsTitleLoaded = false;
                                mPageNo = 0;//��һҳ
                                mMenuLevel = 2;//�ڶ���
                                return;
                            }

                        }
                        //Log.e("����click", "mFocusIndex��ֵ��" + mFocusIndex);
                        //������ؼ�
                        if (mToolBtnFocus[0]) {
                            mToolBtnFocus[0] = false;
                            goReturn();
                            //������ϼ�
                        } else if (mToolBtnFocus[1]) {
                            goUp();
                            //������¼�
                        } else if (mToolBtnFocus[2]) {
                            goDown();
                        }
                        //�����˶Ի���
                        if (mIsOptMenuShow) {
                            //��������ɾ����ѡ�ļ��ĶԻ���
                            if (mOptMenuType == 3) {
                                //���ɾ��
                                if (mFileOptFlag[4]) {
                                    deleteCurFile(true);
                                }
                                //ɾ���ļ�֮������״̬
                                mFileOptFlag[4] = false;
                                //���öԻ��������
                                mOptMenuType = -1;
                                //����Ի���
                                mIsOptMenuShow = false;
                                return;
                            }
                            //���ڲ鿴ͼƬ��״̬
                            if (mOptMenuType == 5) {
                                //���öԻ��������
                                mOptMenuType = -1;
                                //����Ի���
                                mIsOptMenuShow = false;
                                return;
                            }
                            //������Ǵ�
                            if (mFileOptFlag[2]) {
                                //��ͼƬ
                                if (mCurFile.type == FileType.PICTURE) {
                                    //�������ͼƬ�͵���type=5ͼƬ�Ŵ󶯻�
                                    mOptMenuType = 5;
                                    //��1.2*0.5����ʼ�Ŵ�1.2*1.0��
                                    scale3 = 0.5F;
                                    //Ĭ����-1 ��ʾͼƬ��û�л�����
                                    mCurFileBmpId = -1;
                                    mFileOptFlag[2] = false;
                                    return;
                                }
                                //����Ƶ
                                if (mCurFile.type == FileType.VIDEO) {
                                    //������Ƶ
                                    Util.playFile(mContext, mCurFile);
                                    mFileOptFlag[2] = false;
                                }
                            } else {
                                //������ǰ�װ
                                if (mFileOptFlag[3]) {
                                    //�����������ڰ�װ�ĶԻ���
                                    mOptMenuType = 4;
                                    //��װ�ļ�
                                    installApk(mContext, new File(mCurFile.path));
                                    mFileOptFlag[3] = false;
                                    return;
                                }
                                //�������ɾ��
                                if (mFileOptFlag[1]) {
                                    mFileOptFlag[1] = false;
                                    //����ɾ����ѡ�ļ��ĶԻ���
                                    mOptMenuType = 3;
                                    return;
                                }
                            }
                            //���öԻ�������ͣ�����ǿհ״���
                            mOptMenuType = -1;
                            //����Ի���
                            mIsOptMenuShow = false;
                            return;
                        }
                       // Log.e("����click", "mFocusIndex��ֵ��" + mFocusIndex);
                        //�ļ���ѡ�е�ʱ����ȷ�� ��mFocusIndex��ֵ��ֵ��mCurIndex
                        if (mFocusIndex >= 0) {
                            //�ļ�������
                            mCurIndex = mFocusIndex;
                            mCurFile = (FileInfo) mCurFileList.get(mFocusIndex);
                            //�������Ĳ�����Ƶ��ͼƬ����Ƶ
                            if (mCurFile.type != FileType.VIDEO
                                    && mCurFile.type != FileType.PICTURE
                                    && mCurFile.type != FileType.AUDIO) {
                                //��APK
                                if (mCurFile.type == FileType.APK) {
                                    //������װ ɾ�� ȡ�� �ĶԻ���
                                    mOptMenuType = 1;
                                    //��ѹ����
                                } else if (mCurFile.type == FileType.ZIP
                                        && ((MainActivity) mContext).isGetPath()) {
                                    //��ѹ������·��������MainActivity����û��������
                                    returnPathResult();
                                } else {
                                    //������Щ����һ�ɵ���  ɾ�� ȡ��
                                    mOptMenuType = 2;
                                }
                            } else {
                                //������������Ƶ��ͼƬ����Ƶ�͵��� �򿪡�ɾ����ȡ���ĶԻ���
                                mOptMenuType = 0;
                            }
                            //�����Ի���
                            mIsOptMenuShow = true;
                            return;
                        }

                       // Log.e("����click", "mFocusIndex��ֵ��" + mFocusIndex);
                        // ��һҳ
                        break;
                    case 1:
                        break;
                    //�ڵڶ��� �����������豸�洢���ļ��洢������ ������ֻ��0���1��
                    case 2:
                        //���ؼ�
                        if (mToolBtnFocus[0]) {
                            mToolBtnFocus[0] = false;
                            goReturn();
                            //���ϼ�
                        } else if (mToolBtnFocus[1]) {
                            goUp();
                            //���¼�
                        } else if (mToolBtnFocus[2]) {
                            goDown();
                        }
                        //�����˶Ի���
                        if (mIsOptMenuShow) {
                            //ɾ����ѡ�ļ�
                            if (mOptMenuType == 3) {
                                //���ɾ��
                                if (mFileOptFlag[4]) {
                                    deleteCurFile(false);
                                }

                                mFileOptFlag[4] = false;
                                //���öԻ���״̬
                                mOptMenuType = -1;
                                //����Ի���
                                mIsOptMenuShow = false;
                                return;
                            }
                            //�鿴ͼƬ��״̬
                            if (mOptMenuType == 5) {
                                mOptMenuType = -1;
                                //����Ի���
                                mIsOptMenuShow = false;
                                return;
                            }
                            //�����
                            if (mFileOptFlag[2]) {
                                //��ͼƬ
                                if (mCurFile.type == FileType.PICTURE) {
                                    mOptMenuType = 5;
                                    scale3 = 0.5F;
                                    mCurFileBmpId = -1;
                                    mFileOptFlag[2] = false;
                                    return;
                                }
                                //TODO  ����Ƶ
                                if (mCurFile.type == FileType.VIDEO) {
                                    //������Ƶ
                                    Util.playFile(mContext, mCurFile);
                                    mFileOptFlag[2] = false;
                                }
                            } else {
                                //�����װ
                                if (mFileOptFlag[3]) {
                                    //������̨���ڰ�װ�Ի���
                                    mOptMenuType = 4;
                                    installApk(mContext, new File(mCurFile.path));
                                    mFileOptFlag[3] = false;
                                    return;
                                }
                                //���ɾ��
                                if (mFileOptFlag[1]) {
                                    mFileOptFlag[1] = false;
                                    //����ɾ����ѡ�ļ��Ի���
                                    mOptMenuType = 3;
                                    return;
                                }
                            }
                            //���öԻ���״̬
                            mOptMenuType = -1;
                            //����Ի���
                            mIsOptMenuShow = false;
                            return;
                        }
                        //�ļ���ѡ��
                        if (mFocusIndex >= 0) {
                            if (((FileInfo) mCurSortFileList.get(mFocusIndex)).isDirectory) {
                                mCurListPath = ((FileInfo) mCurSortFileList
                                        .get(mFocusIndex)).path;
                                mPathStack.push(mCurListPath);
                                mPageStack.push(Integer.valueOf(mPageNo));
                                mIsMediaUpdated = false;
                                //���������mIsMediaUpdated = true
                                //���뵽����ʾ�ļ��Ľ���
                                mFileExecutor.execute(new SortListRunnable());
                                mIsTitleLoaded = false;
                                //��һҳ
                                mPageNo = 0;
                                return;
                            }

                            mCurIndex = mFocusIndex;
                            mCurFile = (FileInfo) mCurSortFileList.get(mFocusIndex);
                            //�������Ĳ�����Ƶ��ͼƬ����Ƶ
                            if (mCurFile.type != FileType.VIDEO
                                    && mCurFile.type != FileType.PICTURE
                                    && mCurFile.type != FileType.AUDIO) {
                                //APK
                                if (mCurFile.type == FileType.APK) {
                                    mOptMenuType = 1;
                                    //zip
                                } else if (mCurFile.type == FileType.ZIP
                                        && ((MainActivity) mContext).isGetPath()) {
                                    //��zip��·������MainActivity����û����������
                                    returnPathResult();
                                } else {
                                    //��������ɾ�� ȡ���ĶԻ���
                                    mOptMenuType = 2;
                                }
                            } else {
                                //�������Ǵ� ɾ�� ȡ���ĶԻ���
                                mOptMenuType = 0;
                            }
                            //�����Ի���
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
            //�ڵڼ���
            switch (mMenuLevel) {
                case 0:
                    //�����Ի���ʱ
                    if (mIsOptMenuShow) {
                        //���öԻ��������
                        mOptMenuType = -1;
                        //����Ի���
                        mIsOptMenuShow = false;
                        return;
                    }
                    //���ڷ����������Ŀ��
                    if (mTabNo != 0) {
                        mTabNo = 0;
                        return;
                    }
                    //�˳�Ӧ��
                    ((MainActivity) mContext).finish();
                    return;
                case 1:
                    //�����˶Ի���
                    if (mIsOptMenuShow) {
                        //���öԻ��������
                        mOptMenuType = -1;
                        //����Ի���
                        mIsOptMenuShow = false;
                        return;
                    }
                    //�ص���0��
                    mIsFileList = false;
                    mMenuLevel = 0;
                    return;
                case 2:
                    //�����˶Ի���
                    if (mIsOptMenuShow) {
                        //���öԻ��������
                        mOptMenuType = -1;
                        //����Ի���
                        mIsOptMenuShow = false;
                        return;
                    }

                    if (mPathStack.size() > 0) {
                        mPathStack.pop();//���
                        mPageNo = ((Integer) mPageStack.pop()).intValue();
                        if (mPathStack.size() > 0) {
                            mCurListPath = (String) mPathStack.peek();//�������һ�ɾ��
                            mIsMediaUpdated = false;
                            //���������mIsMediaUpdated = true
                            //���뵽����ʾ�ļ��Ľ���
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

    //�ڷ������ �豸�洢֮���л�
    public final void goLeft() {
        if (mMenuLevel == 0 && mTabNo > 0) {
            mTabNo += -1;
        }
    }

    //�ڷ������ �豸�洢֮���л�
    public final void goRight() {
        if (mMenuLevel == 0 && mTabNo <= 0) {
            ++mTabNo;
            mPageNo = 0;
        }
    }

    public final void goUp() {
        if (mMenuLevel == 0 || mTabNo != 0) {
            if (mMenuLevel == 0 && mTabNo == 1) {
                //���豸�洢����ҳ
                if (mPageNo > 0) {
                    mPageNo += -1;
                }
            } else if (mPageNo > 0) {
                //������ҳ
                mPageNo += -1;
                //����ϵͳ�������һҳ ��һҳ Ҫ���¿�ʼ��ͼ�� true��ʾҪ�ػ�
                mIsLoadingDelayed = true;
                mCurrentTime = System.currentTimeMillis();
                return;
            }
        }
    }

    public final void goDown() {
        if (mMenuLevel == 0 || mTabNo != 0) {
            if (mMenuLevel == 0 && mTabNo == 1) {
                //���豸�洢����ҳ
                if (mPageNo < -1 + mMountedDiskLines) {
                    ++mPageNo;
                }
            } else if (mPageNo < -1 + mPageCount) {
                //������ҳ
                ++mPageNo;
                //����ϵͳ�������һҳ ��һҳ Ҫ���¿�ʼ��ͼ�� true��ʾҪ�ػ�
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

        //��̬ע��㲥 �����洢�豸��״̬
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
        //��̬ע��㲥 ������װ�����º�������װ��
        if (installOkReceiver == null) {
            installOkReceiver = new InstallPackageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PACKAGE_REPLACED");
            filter.addAction("android.intent.action.PACKAGE_ADDED");
            filter.addDataScheme("package");
            mContext.registerReceiver(installOkReceiver, filter);
        }
        //��̬ע��㲥 ����Ӧ�ó���װʧ�ܵĹ㲥
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

    //����㲥������  ���մ洢�豸���͹�����״̬
    class TFBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != "android.intent.action.MEDIA_BAD_REMOVAL"
                    && intent.getAction() != "android.intent.action.MEDIA_REMOVED") {
                if (intent.getAction() == "android.intent.action.MEDIA_MOUNTED") {
                    Log.d("MySurfaceView", "ACTION_MEDIA_MOUNTED");
                    initMountedDeviceList();
                    //���¼����ļ������� ������
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

    //����㲥������  ����Ӧ�ó���İ�װ���� ���� �Ͱ�װʧ��
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
                    //���öԻ��������
                    mOptMenuType = -1;
                    //ȡ���Ի���
                    mIsOptMenuShow = false;
                    //��װʧ��
                    Toast.makeText(
                            context,
                            context.getResources().getString(
                                    R.string.txt_tip_install_fail), Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                //���öԻ��������
                mOptMenuType = -1;
                //ȡ���Ի���
                mIsOptMenuShow = false;
                //��װ���
                Toast.makeText(
                        context,
                        context.getResources().getString(
                                R.string.txt_tip_install_finish), Toast.LENGTH_SHORT).show();
            }

        }
    }

    //�����߳�ִ�к�ʱ����   �õ������ļ�������
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
            mIsMediaUpdated = true;//���뵽����ʾ�ļ��Ľ���
        }
    }

    //�����߳�ִ�к�ʱ����  �ж��ļ��Ƿ���һ���ļ���
    final class SortListRunnable implements Runnable {
        @Override
        public final void run() {
            sortFileList(mCurListPath, mComparatorHelper);
            mIsMediaUpdated = true;//���뵽����ʾ�ļ��Ľ���
        }
    }

    final class UpdateListRunnable implements Runnable {
        @Override
        public final void run() {
            //�����б� ��������Ϳ�ʼ �뿪����ͽ���
            while (mUpdateFlag) {
                if (mMenuLevel == 0) {
                    //�����豸�洢�豸���б���Ϣ
                    initMountedDeviceList();
                    //��ʼ���ļ������� ��������FileCategoryHelper��FileTypeStat
                    initFileList();
                }
                //1��ִ��һ��
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //�����ļ����ͺ�������������ͬ��ͼƬ   �����Լ���ͼƬ ��ϵͳ�Լ�����
    final class DecodeFileRunnable implements Runnable {
        private final FileInfo mFileInfo;

        DecodeFileRunnable(FileInfo info) {
            mFileInfo = info;
        }

        @Override
        public final void run() {
            //����Ƶ�����ͼƬ
            if (mCurMediaType == 0) {
               // LocalImageLoader.getInstance().generateVideoThumbnail(
               //         mFileInfo.path);
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            } else {
                //������Ƶ�����ͼƬ
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            }
            mFileInfo.imageDecode = true;
        }
    }

    //��ʼ����ͼƬ��ͼ��
    final class DecodeTypeRunnable implements Runnable {
        private final FileInfo mFileInfo;

        DecodeTypeRunnable(FileInfo info) {
            mFileInfo = info;
        }

        @Override
        public final void run() {
            //����ƵӦ������
            if (mFileInfo.type == FileType.VIDEO) {
              // LocalImageLoader.getInstance().generateVideoThumbnail(
              //          mFileInfo.path);
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            } else {
                //������ƵӦ������
                LocalImageLoader.getInstance().getLocalBitmap(mFileInfo.path);
            }
            //���Լ���ͼƬ
            mFileInfo.imageDecode = true;
        }
    }

    //�õ���װ��APP��ͼ��
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

    //�����̼߳�ⱳ�����Ƿ����ù�
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

        //��������
        private void drawFileOptMenu(int type) {
            //�������Ǵ� �� ɾ���� ȡ���ĶԻ���
            if (type == 0) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                MatrixState.pushMatrix();
                MatrixState.scale(1.6F, 1.0F, 1.0F);
                //������ 0.8͸����
                mTextureRect.drawSelf(0, 0.8F);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.3F, 0.1F);
                MatrixState.scale(1.0F, 0.125F, 1.0F);
                if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                    //��ѡ�еĴ�
                    mFileOptFlag[2] = true;
                    mTextureRect.drawSelfOrigin(mTxtOpenOnId);
                } else {
                    //��û�б�ѡ��
                    mFileOptFlag[2] = false;
                    mTextureRect.drawSelfOrigin(mTxtOpenId);
                }

                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.15F, 0.1F);
                MatrixState.scale(1.0F, 0.01F, 1.0F);
                //����ɫ����
                mTextureRect.drawSelfOrigin(mGrayRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.1F);
                MatrixState.scale(1.0F, 0.125F, 1.0F);
                if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                    //��ѡ�е�ɾ�� ��¼����
                    mFileOptFlag[1] = true;
                    mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                } else {
                    //Ĭ�ϵ�ɾ��
                    mFileOptFlag[1] = false;
                    mTextureRect.drawSelfOrigin(mTxtDeleteId);
                }

                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.15F, 0.1F);
                MatrixState.scale(1.0F, 0.01F, 1.0F);
                //����ɫ����
                mTextureRect.drawSelfOrigin(mGrayRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.3F, 0.1F);
                MatrixState.scale(1.0F, 0.125F, 1.0F);
                if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                    //��ѡ�е�ȡ�� ��¼�ڵ�0��λ��
                    mFileOptFlag[0] = true;
                    mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                } else {
                    mFileOptFlag[0] = false;
                    //ȡ��
                    mTextureRect.drawSelfOrigin(mTxtCancelId);
                }
                MatrixState.popMatrix();

                MatrixState.popMatrix();
            } else {
                //�������ǰ�װ��ɾ����ȡ���ĶԻ���
                if (type == 1) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.6F, 1.0F, 1.0F);
                    //������ 0.8͸����
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.3F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //��ѡ�еİ�װ  ��¼�ڵ�����λ��
                        mFileOptFlag[3] = true;
                        mTextureRect.drawSelfOrigin(mTxtInstallOnId);
                    } else {
                        mFileOptFlag[3] = false;
                        //��װ
                        mTextureRect.drawSelfOrigin(mTxtInstallId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.01F, 1.0F);
                    //������
                    mTextureRect.drawSelfOrigin(mGrayRectId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        mFileOptFlag[1] = true;
                        //��ѡ�е�ɾ��
                        mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                    } else {
                        mFileOptFlag[1] = false;
                        //Ĭ�ϵ�ɾ��
                        mTextureRect.drawSelfOrigin(mTxtDeleteId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.01F, 1.0F);
                    //������
                    mTextureRect.drawSelfOrigin(mGrayRectId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.3F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //��ѡ�е�ȡ��
                        mFileOptFlag[0] = true;
                        mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                    } else {
                        mFileOptFlag[0] = false;
                        //ȡ��
                        mTextureRect.drawSelfOrigin(mTxtCancelId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //��������ɾ��  ȡ���ĶԻ���
                if (type == 2) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.4F, 0.7F, 1.0F);
                    //������ 0.8͸����
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //��ѡ�е�ɾ��
                        mFileOptFlag[1] = true;
                        mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                    } else {
                        //Ĭ�ϵ�ɾ��
                        mFileOptFlag[1] = false;
                        mTextureRect.drawSelfOrigin(mTxtDeleteId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.1F);
                    MatrixState.scale(0.8F, 0.01F, 1.0F);
                    //����ɫ����
                    mTextureRect.drawSelfOrigin(mGrayRectId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //��ѡ�е�ȡ��
                        mFileOptFlag[0] = true;
                        mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                    } else {
                        //ȡ��
                        mFileOptFlag[0] = false;
                        mTextureRect.drawSelfOrigin(mTxtCancelId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //���ɾ���ļ�֮�󵯳����ĶԻ���
                if (type == 3) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.6F, 0.7F, 1.0F);
                    //������ 0.8͸����
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.15F, 0.05F);
                    MatrixState.scale(1.4F, 0.7F, 1.0F);
                    //��ɾ����ѡ�ļ��ĸ���
                    mTextureRect.drawSelfOrigin(mTipDeleteId);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(-0.3F, -0.15F, 0.1F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //��ѡ�е�ȡ��
                        mFileOptFlag[0] = true;
                        mTextureRect.drawSelfOrigin(mTxtCancelOnId);
                    } else {
                        //ȡ��
                        mFileOptFlag[0] = false;
                        mTextureRect.drawSelfOrigin(mTxtCancelId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.3F, -0.15F, 0.12F);
                    MatrixState.scale(1.0F, 0.125F, 1.0F);
                    if (isLookingAtObject(0.6F, 0.25F, iconDistance - 2.0F)) {
                        //��ѡ�еĵڶ��� ��ɾ�� ��¼�ڵ��ĸ�λ��
                        mFileOptFlag[4] = true;
                        mTextureRect.drawSelfOrigin(mTxtDeleteOnId);
                    } else {
                        mFileOptFlag[4] = false;
                        //Ĭ�ϵ�ɾ��
                        mTextureRect.drawSelfOrigin(mTxtDeleteId);
                    }
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //�����װ֮����ֵĶԻ���
                if (type == 4) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 2.0F + -iconDistance);

                    MatrixState.pushMatrix();
                    MatrixState.scale(1.6F, 0.7F, 1.0F);
                    //������ 0.8͸����
                    mTextureRect.drawSelf(0, 0.8F);
                    MatrixState.popMatrix();

                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.05F);
                    MatrixState.scale(1.4F, 0.7F, 1.0F);
                    //�� ��̨���ڰ�װ ������
                    mTextureRect.drawSelfOrigin(mTipInstallId);
                    MatrixState.popMatrix();

                    MatrixState.popMatrix();
                    return;
                }
                //�����ͼƬ֮������ķŴ��Ч��
                if (type == 5) {
                    //Ĭ����-1 ��ʾͼƬ��û�л�����
                    if (mCurFileBmpId < 0) {
                        Bitmap bmp;
                        if (Util.getFileExtName(mCurFile.name)
                                //  ���� String ����һ�� String �Ƚϣ������Ǵ�Сд
                                .equalsIgnoreCase("gif")) {
                            bmp = Util.getBitmap(mContext,
                                    mContext.getContentResolver(),
                                    mCurFile.path);
                        } else {
                            LocalImageLoader.getInstance();
                            bmp = LocalImageLoader.decodeThumbBitmapForFile(
                                    (String) mCurFile.path, 2048, 1024);
                        }
                        //��ʼ��ͼƬ��bitmap
                        mCurFileBmpId = Utils.initTexture(bmp);
                        mCurFile.imageRatio = (float) bmp.getWidth()
                                / (float) bmp.getHeight();
                    } else {
                        MatrixState.pushMatrix();
                        MatrixState.translate(0.0F, 0.0F, 2.2F + -iconDistance);
                        MatrixState.scale(1.2F * mCurFile.imageRatio * scale3,
                                1.2F * scale3, 1.0F);
                        //���ͼƬ��ķŴ�Ч��
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

        //��������
        private void drawThemeBg() {
            MatrixState.pushMatrix();
            MatrixState.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            MatrixState.scale(mThemeBgScale, mThemeBgScale, mThemeBgScale);
            //��������
            mTextureBall.drawSelf(mThemeTextureId);
            MatrixState.popMatrix();
        }

        //������������ ������������� �Լ�������
        private void drawTabBgAndTitle() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.0F, -iconDistance - 0.2F);
            MatrixState.scale(mRectBgXScale, mRectBgYScale, 1.0F);
            //������Ĵ󱳾��� ��͸����ɫ
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            for (int i = 0; i < 3; ++i) {
                MatrixState.pushMatrix();
                MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F,
                        mRectBgYScale / 2.0F - 0.4F - 0.55F * i, -iconDistance);
                if (isLookingAtObject(1.0F, 0.5F, iconDistance)) {
                    //��ʾ����ƶ����������Ŀ��
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

                //��ʾ�������ĸ���Ŀ����
                if (mTabNuLeft == i) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(-0.55F, 0.05F, 0.0F);
                    MatrixState.scale(0.04F, 0.55F, 1.0F);
                    //����ɫ����
                    mTextureRect.drawSelfOrigin(mBlueRectId);
                    MatrixState.popMatrix();
                }

                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 1.3F, 0.0F,
                    -iconDistance);
            MatrixState.scale(0.02F, 2.86F, 1.0F);
            //�������Ļ�ɫ����
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();

            for (int i = 0; i < 3; ++i) {
                MatrixState.pushMatrix();
                MatrixState.translate(-1.0F + 1.55F * i,
                        mRectBgYScale / 2.0F - 0.25F, -iconDistance);
                if (isLookingAtObject(1.0F, 0.5F, iconDistance)) {
                    //��ʾ����ƶ����������Ŀ��
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

                //��ʾ�������ĸ���Ŀ����
                if (mTabNuTop == i) {
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, -0.30F, 0.01F);
                    MatrixState.scale(0.8F, 0.04F, 1.0F);
                    //����ɫ����
                    mTextureRect.drawSelfOrigin(mBlueRectId);
                    MatrixState.popMatrix();
                }

                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.6F, mRectBgYScale / 2.0F - 0.55F,
                    -iconDistance);
            MatrixState.scale(mRectBgXScale - 1.424F, 0.02F, 1.0F);
            //�������Ļ�ɫ����
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F, -0.4F,
                    -iconDistance - 0.01F);
            MatrixState.scale(1.2F, 0.3F, 1.0F);
            //���ش洢
            mTextureRect.drawSelfOrigin(mshowstorageArrayId[mTabNuTop]);
            MatrixState.popMatrix();


            //DiskInfo info = (DiskInfo) mMountedDiskList.get(mTabNuTop);
            DiskInfo info = (DiskInfo)mShowDiskList.get(0);
            //Log.e("���ԡ�������","mMountedDiskList��С"+mMountedDiskList.size());


            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F, -1.0F, -iconDistance - 0.01F);
            MatrixState.scale(1.2F, 0.35F, 1.0F);
            //�洢�豸�Ŀ��Կռ���ܿռ�û�г�ʼ��
            if (info.sizeTextureId <= 0) {
                //��ʼ���洢�豸�Ŀ��Կռ���ܿռ�
                info.sizeTextureId = Utils.initTexture(Utils
                        .generateWLT(SurplusSize + info.available + "\n" + TotalSize + info.total,
                                14.0F, mContext.getResources()
                                        .getColor(R.color.white),
                                120, 35, Alignment.ALIGN_CENTER));
            } else {
                //���洢�豸�Ŀ��Կռ���ܿռ�
                mTextureRect.drawSelfOrigin(info.sizeTextureId);
            }
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.7F, -0.65F, -iconDistance - 0.005F);
            MatrixState.scale(1.10F, 0.08F, 1.0F);
            //���洢�豸���泤���Ļ�ɫ����
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-mRectBgXScale / 2.0F + 0.15F + 1.18F * info.freeRatio * 0.5F, -0.647F, -iconDistance);
            MatrixState.scale(1.18F * info.freeRatio, 0.08F, 1.0F);
            //���ݿ��ðٷֱȻ����ÿռ����ɫ����
            mTextureRect.drawSelfOrigin(mBlueRectId);
            MatrixState.popMatrix();


        }

        //TODO ����ҳ
        private void drawCategoryList() {
            //Ϊ���ʱ���ʾ�����Ѿ���ȡ�����������
            if (mFileListInited) {
                for (int i = 0; i < 3; ++i) {
                    if (mMediaFileCountId[i] > 0) {
                        int[] textures = new int[]{mMediaFileCountId[i]};
                        GLES20.glDeleteTextures(1, textures, 0);
                        //�ļ��ĸ�����*��
                        //��ʼ���ļ��ĸ�����n��
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
                //������֮�������Ϊfalse Ҫ�õ�ʱ�������µ���initFileList����������ȡ
                mFileListInited = false;
            }

            for (int i = 0; i < 3; ++i) {
                MatrixState.pushMatrix();
                MatrixState.translate(1.5F * (float) i - 1.5F, 0.0F,
                        -iconDistance);
                if (isLookingAtObject(1.0F, 1.0F, iconDistance)) {
                    //��¼�м�� ��Ƶ��ͼƬ��APK��������״̬
                    mMediaListFocus[i] = true;
                    MatrixState.pushMatrix();
                    MatrixState.translate(0.0F, 0.0F, 0.005F);
                    MatrixState.scale(1.6F, 1.6F, 1.0F);
                    //�������ͻ��߿򱳾�
                    mTextureRect.drawSelfOrigin(mIconBgId);
                    MatrixState.popMatrix();
                } else {
                    mMediaListFocus[i] = false;
                }

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.2F, 0.02F);
                MatrixState.scale(0.5F, 0.5F, 1.0F);
                //�м�� ��Ƶ��ͼƬ��APK������
                if (mMediaListFocus[i]) {
                    //�м�ı���  ��Ƶ  ͼƬ ��װ�� ��������ͼƬ
                    mTextureRect.drawSelfOrigin(mMenuIconsOnId[i]);
                } else {
                    //�м�ı���  ��Ƶ  ͼƬ ��װ��
                    mTextureRect.drawSelfOrigin(mMenuIconsId[i]);
                }
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.2F, 0.01F);
                MatrixState.scale(1.6F, 0.25F, 1.0F);
                //�м�� ��Ƶ��ͼƬ��APK������
                if (mMediaListFocus[i]) {
                    //�н��㻭��ɫ��
                    mTextureRect.drawSelfOrigin(mKindsArrayBlueId[i]);
                } else {
                    //û�н��㻭��
                    mTextureRect.drawSelfOrigin(mKindsArrayId[i]);
                }
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -0.4F, 0.02F);
                MatrixState.scale(0.6F, 0.3F, 1.0F);
                //��ʾ�ļ����������ֻ�û�г�ʼ��
                if (mMediaFileCountId[i] <= 0) {
                    //��ʼ����ʾ�ļ�����������
                    mMediaFileCountId[i] = Utils.initTexture(Utils.generateWLT(
                            "(" + mMediaFileCount[i] + ")", 64.0F,
                            mContext.getResources()
                                    .getColor(R.color.text_color), 256, 128,
                            Alignment.ALIGN_CENTER));
                } else {
                    //����ʾ�ļ�����������
                    mTextureRect.drawSelfOrigin(mMediaFileCountId[i]);
                }
                MatrixState.popMatrix();
                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.8F, 1.0F + -iconDistance);
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            MatrixState.scale(1.6F, 0.8F, 1.0F);
            //�����ؼ������� ��͸����ɫ
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
            MatrixState.scale(0.3F, 0.3F, 1.0F);
            if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                mToolBtnFocus[0] = true;
                //���ؼ���ȡ����
                mTextureRect.drawSelfOrigin(mIconBackOnId);
            } else {
                mToolBtnFocus[0] = false;
                //Ĭ�Ϸ��ؼ�
                mTextureRect.drawSelfOrigin(mIconBackId);
            }

            MatrixState.popMatrix();
        }

        //���豸�洢����ҳ
        private void drawDiskList() {
            //�洢�ռ�������
            if (mDiskInfoList.size() > 0) {
                //��ʾ�洢�ռ������Ѿ���ʼ�����
                if (mMountedDeviceListInited) {
                    //����ÿһ���洢�ռ�Ĵ�С
                    for (int i = 0; i < mDiskInfoList.size(); ++i) {
                        DiskInfo info = (DiskInfo) mDiskInfoList.get(i);
                        if (info.mounted && info.sizeTextureId > 0) {
                            int[] textures = new int[]{info.sizeTextureId};
                            GLES20.glDeleteTextures(1, textures, 0);
                            //��ʼ���豸�洢�Ŀռ��С ���ÿռ�/�ܿռ�
                            info.sizeTextureId = Utils.initTexture(Utils
                                    .generateWLT(info.free + "/" + info.total,
                                            32.0F, mContext.getResources()
                                                    .getColor(R.color.white),
                                            256, 128, Alignment.ALIGN_CENTER));
                        }
                    }
                    //�����˾Ͱ�״̬����Ϊ0 �����ݸ���ʱ��������
                    mMountedDeviceListInited = false;
                }

                int offset = 4 * mPageNo;

                while (true) {
                    int total;
                    //�����һҳ
                    if (mPageNo == -1 + mMountedDiskLines) {
                        total = mMountedDiskList.size();
                    } else {
                        //�������һҳ��ʾ�洢�豸������
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
                            //���洢�豸��ѡ��ʱ�򻭵ı߿�
                            mTextureRect.drawSelfOrigin(mIconSelectionId);
                            MatrixState.popMatrix();
                        }

                        MatrixState.pushMatrix();
                        MatrixState.translate(-1.85F, 0.07F, 0.0F);
                        MatrixState.scale(0.3F, 0.3F, 1.0F);
                        //���������жϴ洢�ļ�������
                        if (info.type == 0) {
                            //���ڲ��洢��ͼ��
                            mTextureRect.drawSelfOrigin(mStorageIconsId[0]);
                        } else {
                            //��SD����ͼ��
                            mTextureRect.drawSelfOrigin(mStorageIconsId[1]);
                        }
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(-0.8F, 0.07F, 0.0F);
                        MatrixState.scale(1.6F, 0.25F, 1.0F);
                        //�洢�豸������û�г�ʼ��
                        if (info.nameTextureId <= 0) {
                            //��ʼ���洢�豸������
                            info.nameTextureId = Utils.initTexture(Utils
                                    .generateWLT(
                                            info.name,
                                            32.0F,
                                            mContext.getResources().getColor(
                                                    R.color.white), 256, 32,
                                            Alignment.ALIGN_NORMAL));
                        } else {
                            //����ʼ���洢�豸������
                            mTextureRect.drawSelfOrigin(info.nameTextureId);
                        }
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(1.2F, 0.07F, 0.0F);
                        MatrixState.scale(1.6F, 0.8F, 1.0F);
                        //�洢�豸�Ŀ��Կռ���ܿռ�û�г�ʼ��
                        if (info.sizeTextureId <= 0) {
                            //��ʼ���洢�豸�Ŀ��Կռ���ܿռ�
                            info.sizeTextureId = Utils.initTexture(Utils
                                    .generateWLT(info.free + "/" + info.total,
                                            32.0F, mContext.getResources()
                                                    .getColor(R.color.white),
                                            256, 128, Alignment.ALIGN_CENTER));
                        } else {
                            //���洢�豸�Ŀ��Կռ���ܿռ�
                            mTextureRect.drawSelfOrigin(info.sizeTextureId);
                        }
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(0.0F, -0.16F, 0.01F);
                        MatrixState.scale(4.0F, 0.04F, 1.0F);
                        //���洢�豸���泤���Ļ�ɫ����
                        mTextureRect.drawSelfOrigin(mGrayRectId);
                        MatrixState.popMatrix();

                        MatrixState.pushMatrix();
                        MatrixState.translate(-2.0F + 2.0F * info.freeRatio,
                                -0.16F, 0.02F);
                        MatrixState.scale(4.0F * info.freeRatio, 0.04F, 1.0F);
                        //���ݿ��ðٷֱȻ����ÿռ����ɫ����
                        mTextureRect.drawSelfOrigin(mBlueRectId);

                        MatrixState.popMatrix();

                        MatrixState.popMatrix();
                    }
                    //����һ���洢�豸�ٻ���һ��
                    ++offset;
                }
            }
            //������豸��ҳ������һҳ
            if (mMountedDiskLines > 1) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.8F, 1.0F + -iconDistance);
                MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                MatrixState.scale(2.0F, 0.8F, 1.0F);
                //�����ؼ������� ��͸����ɫ
                mTextureRect.drawSelfOrigin(mTransRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(-0.5F, -1.6F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    //����ƶ����˷��ؼ����� ����¼״̬
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
                //�ڵ�һҳ
                if (mPageNo == 0) {
                    mToolBtnFocus[1] = false;
                    //�����ܱ�ѡ�еĺ�ɫ���ϼ�
                    mTextureRect.drawSelfOrigin(mIconUpNoneId);
                } else {
                    //���ڵ�һҳ�����Ա�ѡ�еİ�ɫ����ɫ���ϼ�
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
                //�����һҳ �����ܱ�ѡ�еĺ�ɫ���¼�
                if (mPageNo == -1 + mMountedDiskLines) {
                    mToolBtnFocus[2] = false;
                    mTextureRect.drawSelfOrigin(mIconDownNoneId);
                } else {
                    //�������һҳ�����Ա�ѡ�еİ�ɫ����ɫ�����¼�
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
                //ֻ��һҳ��ֻ��һ�����ؼ�
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.8F, 1.0F + -iconDistance);
                MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                MatrixState.scale(1.6F, 0.8F, 1.0F);
                //�����ؼ������� ��͸����ɫ
                mTextureRect.drawSelfOrigin(mTransRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                //�����ؼ�
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

        //���ļ��ı��� ���ļ������� �ͻ�ɫ����
        private void drawDiskBgAndTitle() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.2F, -iconDistance - 0.1F);
            //������Ĵ󱳾��� ��͸����ɫ
            MatrixState.scale(mRectBgXScale - 1.0F, mRectBgYScale - 0.4F, 1.0F);
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            //�����ļ����ݵı���
            if (!mIsTitleLoaded) {
                //mIsTitleLoadedĬ����false����ʾ�ļ������ֻ�û�г�ʼ��
                if (mCurListTitleId <= 0) {
                    //��ʼ���ļ���
                    mCurListTitleId = Utils.initTexture(Utils.generateWLT(Util
                                    .getLastFileName(mCurListPath), 20.0F, mContext
                                    .getResources().getColor(R.color.text_color), 256, 128,
                            Alignment.ALIGN_CENTER));
                } else {
                    int[] textures = new int[]{mCurListTitleId};
                    GLES20.glDeleteTextures(1, textures, 0);
                    //��ʼ���ļ���
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
                //�������ļ����ݵı�����
                mTextureRect.drawSelfOrigin(mCurListTitleId);
                MatrixState.popMatrix();
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.51F,
                    0.01F + -iconDistance);
            MatrixState.scale(3.2F, 0.02F, 1.0F);
            //���ļ��������泤���Ļ�ɫ����
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();
        }

        //���ļ���ͼ��
        private void drawDiskFileList() {
            //���뵽����ʾ�ļ��Ľ��� �����ļ�������>0���������ļ��洢����
            if (mIsMediaUpdated && mCurSortFileList.size() > 0) {
                //�ļ���ҳ��
                mPageCount = 1 + (-1 + mCurSortFileList.size()) / 8;
                //����ϵͳ�������һҳ ��һҳ Ҫ���¿�ʼ��ͼ�� true��ʾҪ�ػ�
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
                    //��ʾ�ļ�û�л���
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
                            //�ļ�������  ��mFocusIndex��¼
                            mFocusIndex = offset;
                            //û�е����Ի���
                            if (!mIsOptMenuShow) {
                                MatrixState.scale(1.1F, 1.1F, 1.0F);
                            }
                        }
                        //�����˶Ի��� && �ļ��������
                        if (mIsOptMenuShow && mCurIndex == offset) {
                            MatrixState.scale(1.1F, 1.1F, 1.0F);
                        }

                        //���ļ���ͼ��
                        //��һ���ļ���
                        if (info.isDirectory) {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.01F);
                            MatrixState.scale(iconWidth - 0.32F,
                                    iconHeight - 0.32F, 1.0F);
                            //�Ƿ񵯳��Ի���
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //���ļ��е�ͼ��
                            mTextureRect.drawSelf(mIconFilesId, alpha);
                            MatrixState.popMatrix();
                            //�ļ�������Ƶ ����ͼƬ
                        } else if (info.type != FileType.VIDEO
                                && info.type != FileType.PICTURE) {
                            //��Ƶ�ļ�
                            if (info.type == FileType.AUDIO) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.3F,
                                        iconHeight - 0.35F, 1.0F);
                                //�Ƿ񵯳��Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //���м�Ĵ�����ͼ��  ��Ƶ ͼƬ ��װ�� ���� ����ָ���ֵ�ͼ��
                                mTextureRect.drawSelf(mMenuIconsId[3], alpha);
                                MatrixState.popMatrix();
                                //APK�ļ�
                            } else if (info.type == FileType.APK) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.25F,
                                        iconHeight - 0.3F, 1.0F);
                                //imageTextureIdĬ����-1 ��ʾ��û�л�
                                if (info.imageTextureId <= 0) {
                                    //�Ƿ񵯳��Ի���
                                    float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                    //��װ��Ĭ�ϵ�ͼ��
                                    mTextureRect.drawSelf(mIconLauncherId,
                                            alpha);
                                    if (!mIsLoadingDelayed) {
                                        if (!info.imageDecode) {
                                            mResExecutor
                                                    .execute(new LoadAppIconRunnable(
                                                            info));
                                        } else {
                                            //��ʼ����װ��APP��ͼ��
                                            Bitmap bmp = LocalImageLoader
                                                    .getInstance()
                                                    .getAppIcon(mContext,
                                                            info.path);
                                            if (bmp == null) {
                                                //Ϊ�վͻ�Ĭ�ϵ�APPͼ�� ����һ��launcherͼ��
                                                info.imageTextureId = mIconLauncherId;
                                            } else {
                                                //��ʼ����װ��APP��ͼ��
                                                info.imageTextureId = Utils
                                                        .initTexture(bmp);
                                            }
                                        }
                                    }
                                } else {
                                    //�Ƿ񵯳��Ի���
                                    float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                    //��װ���Լ���ͼ��
                                    mTextureRect.drawSelf(info.imageTextureId,
                                            alpha);
                                }

                                MatrixState.popMatrix();
                            } else {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.32F,
                                        iconHeight - 0.32F, 1.0F);
                                //�Ƿ񵯳��Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //�����ļ���ͼ��
                                mTextureRect.drawSelf(mIconFileId, alpha);
                                MatrixState.popMatrix();
                            }
                        } else {
                            //�ļ���������Ƶ����ͼƬ
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.0F);
                            MatrixState.scale(iconWidth - 0.1F,
                                    iconHeight - 0.3F, 1.0F);
                            //imageTextureIdĬ����-1 ��ʾͼƬ��û�л�
                            if (info.imageTextureId < 0) {
                                //�Ƿ񵯳��Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //������Ĵ󱳾�
                                mTextureRect.drawSelf(0, alpha);
                                MatrixState.popMatrix();

                                if (!mIsLoadingDelayed) {
                                    if (!info.imageDecode) {
                                        //��ʼ����ͼƬ���ļ�
                                        mResExecutor
                                                .execute(new DecodeTypeRunnable(
                                                        info));
                                        //��ʼ����Ƶ�ļ�
                                    } else if (info.type == FileType.VIDEO) {
                                        Bitmap bmp = /*LocalImageLoader
                                                .getInstance()
                                                .generateVideoThumbnail(
                                                        info.path);*/
                                                LocalImageLoader
                                                        .getInstance()
                                                        .getLocalBitmap(info.path);
                                        if (bmp != null) {
                                            //��ʼ����Ƶ�ļ�
                                            info.imageTextureId = Utils
                                                    .initTexture(bmp);
                                        } else {
                                            info.imageTextureId = 0;
                                        }
                                    } else {
                                        //�ļ�������ͼƬ����
                                        Bitmap bmp;
                                        //�õ�gif��ͼƬbmp
                                        if (Util.getFileExtName(info.name)
                                                .equalsIgnoreCase("gif")) {
                                            bmp = Util
                                                    .getBitmap(
                                                            mContext,
                                                            mContext.getContentResolver(),
                                                            info.path);
                                        } else {
                                            //�õ���ͨ�ļ���ͼƬ��bmp
                                            bmp = LocalImageLoader
                                                    .getInstance()
                                                    .getLocalBitmap(info.path);
                                        }

                                        if (bmp != null) {
                                            //��ʼ��ͼƬ�ļ�bmp
                                            info.imageTextureId = Utils
                                                    .initTexture(bmp);
                                        } else {
                                            info.imageTextureId = 0;
                                        }
                                    }
                                }
                            } else {
                                //�Ƿ񵯳��Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //��ͼƬ����Ƶ�Լ���ͼ��
                                mTextureRect.drawSelf(info.imageTextureId,
                                        alpha);
                                MatrixState.popMatrix();
                            }
                            //�ļ���������Ƶ�Ļ���Ҫ���м�Ĳ���ͼ��
                            if (info.type == FileType.VIDEO) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.6F,
                                        iconHeight - 0.6F, 1.0F);
                                //�Ƿ񵯳��˶Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //����Ƶ�м�Ĳ��ż�
                                mTextureRect.drawSelf(mIconPlayId, alpha);
                                MatrixState.popMatrix();
                            }
                        }
                        //Ĭ����nameTextureId=-1 ��ʾ��û�л��ļ�������
                        if (info.nameTextureId <= 0) {
                            //��ʼ��Ӧ�õ����� ������info��
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
                            //�Ƿ񵯳��Ի���
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //���ļ�������
                            mTextureRect.drawSelf(info.nameTextureId, alpha);
                            MatrixState.popMatrix();
                        }
                        //�ļ�������&&û�е����Ի���  || �����˶Ի��� && �ļ��������
                        if (mFocusIndex == offset && !mIsOptMenuShow
                                || mIsOptMenuShow && mCurIndex == offset) {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.0F, 0.011F);
                            MatrixState.scale(0.3F + iconWidth,
                                    0.34F + iconHeight, 1.0F);
                            //�Ƿ񵯳��Ի���
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //�������ͻ��߿򱳾�
                            mTextureRect.drawSelf(mIconBgId, alpha);
                            MatrixState.popMatrix();
                        }

                        MatrixState.popMatrix();
                    }
                    //����֮���������һ���ļ�
                    ++offset;
                }
                //���뵽����ʾ�ļ��Ľ���
            } else if (mIsMediaUpdated) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.1F + -iconDistance);
                MatrixState.scale(2.0F, 0.3125F, 1.0F);
                //������û���κ��ļ��е�����³���һ�������Ŀ��
                mTextureRect.drawSelfOrigin(mIconBgId);
                MatrixState.popMatrix();
            } else {
                //������ʱ�������ȦȦ
                drawLoadingIcon();
            }
            //�����˶Ի���
            if (mIsOptMenuShow) {
                drawFileOptMenu(mOptMenuType);
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            MatrixState.scale(2.0F, 0.8F, 1.0F);
            //�����ؼ������� ��͸����ɫ
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
            //���ڵ�һҳ && û�е����Ի���
            if (mPageNo != 0 && !mIsOptMenuShow) {
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[1] = true;
                    //����ƶ��������ϵİ�ť��
                    mTextureRect.drawSelfOrigin(mIconUpOnId);
                } else {
                    mToolBtnFocus[1] = false;
                    //���ϵİ�ť
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
            //�������һҳ&&û�е����Ի���
            if (mPageNo != -1 + mPageCount && !mIsOptMenuShow) {
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[2] = true;
                    //����ƶ������µİ�ť��
                    mTextureRect.drawSelfOrigin(mIconDownOnId);
                } else {
                    mToolBtnFocus[2] = false;
                    //���µİ�ť
                    mTextureRect.drawSelfOrigin(mIconDownId);
                }
            } else {
                mToolBtnFocus[2] = false;
                mTextureRect.drawSelfOrigin(mIconDownNoneId);
            }
            MatrixState.popMatrix();
        }

        //������ʱ����ֵļ���ȦȦ
        private void drawLoadingIcon() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.58F, 0.0F, 0.2F + -iconDistance);
            MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
            MatrixState.scale(1.0F, 1.0F, 1.0F);
            //�����ص�ʱ����ֵ�ȦȦ�ı�������ɫ��
            mTextureRect.drawSelfOrigin(mLoadingBaseId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.58F, 0.0F, 0.21F + -iconDistance);
            MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
            MatrixState.scale(1.0F, 1.0F, 1.0F);
            //���ص�ʱ�򿴵���ȦȦ��һ���֣���ɫ��
            mTextureRect.drawSelfOrigin(mLoadingOnId);
            MatrixState.popMatrix();

            mLoadingAngle += 3.0F;
        }

        //����ӦӦ�õı��⡢���� �ͻ�ɫ����
        private void drawMediaBgAndTitle() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.2F, -iconDistance - 0.1F);
            //������Ĵ󱳾��� ��͸����ɫ
            MatrixState.scale(mRectBgXScale - 1.0F, mRectBgYScale - 0.4F, 1.0F);
            mTextureRect.drawSelfOrigin(mTransRectId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.3F,
                    -iconDistance);
            MatrixState.scale(1.6F, 0.25F, 1.0F);
            //��Ӧ������ı���
            mTextureRect.drawSelfOrigin(mKindsArrayId[mCurMediaType]);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.51F,
                    0.01F + -iconDistance);
            MatrixState.scale(3.2F, 0.02F, 1.0F);
            //�������Ļ�ɫ����
            mTextureRect.drawSelfOrigin(mGrayRectId);
            MatrixState.popMatrix();
        }

        //��Ӧ�����������ļ��б� �Լ����ܲ˵���
        private void drawMediaFileList() {
            //���뵽����ʾ�ļ��Ľ��� && �ļ�������0
            if (mIsMediaUpdated && mCurFileList.size() > 0) {
                //��ҳ��
                mPageCount = 1 + (-1 + mCurFileList.size()) / 8;
                //����ϵͳ�������һҳ ��һҳ Ҫ���¿�ʼ��ͼ�� true��ʾҪ�ػ�
                if (mIsLoadingDelayed
                        && System.currentTimeMillis() - mCurrentTime > 500L) {
                    mIsLoadingDelayed = false;
                }
                //��¼ǰ���м�ҳ�ļ�*8
                int offset = 8 * mPageNo;

                while (true) {
                    int total;
                    //�����һҳ
                    if (mPageNo == -1 + mPageCount) {
                        total = mCurFileList.size();
                    } else {
                        //��ǰҳ��֮ǰҳ���е�ͼ��
                        total = 8 * (1 + mPageNo);
                    }

                    if (offset >= total) {
                        break;
                    }
                    //���ǣ����һ��+1���ļ�
                    if (offset < mCurFileList.size()
                            && mCurFileList.get(offset) != null) {
                        FileInfo info = (FileInfo) mCurFileList.get(offset);
                        //�ֳ�����  һ��4��
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


                        //�����˶Ի��� && �ļ��������
                        if (mIsOptMenuShow && mCurIndex == offset) {
                            MatrixState.scale(1.1F, 1.1F, 1.0F);
                        }

                        if (mCurMediaType < 2) {
                            //����Ƶ ͼƬ����
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.0F);
                            MatrixState.scale(iconWidth - 0.1F,
                                    iconHeight - 0.3F, 1.0F);
                            //ͼƬû�г�ʼ�� ��ȥ��ʼ��
                            if (info.imageTextureId < 0) {
                                //�Ƿ񵯳��˶Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                mTextureRect.drawSelf(0, alpha);
                                MatrixState.popMatrix();
                                //û�е������һҳ����һҳ���߸ճ�ʼ��
                                if (!mIsLoadingDelayed) {
                                    //ͼƬ��û�м��غ�
                                    if (!info.imageDecode) {
                                        //��ͼƬ·�� ͼƬ����ͼ���� ִ��������mIsLoadingDelayed=true
                                        mResExecutor
                                                .execute(new DecodeFileRunnable(
                                                        info));
                                        //ͼƬ������ϲ���չʾ��Ƶ��
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
                                            Log.v("������Ƶ��", "��Ƶ������ͼΪnull" + offset);
                                            info.imageTextureId = 0;
                                        }
                                        //ͼƬ������ϲ�����Ƭչʾ��
                                    } else {
                                        Bitmap bmp;
                                        //�����Ƭ��gif
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
                                //�Ƿ񵯳��˶Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //��ͼƬ
                                mTextureRect.drawSelf(info.imageTextureId,
                                        alpha);
                                /*mTextureRect.drawSelf(TestRectId,
                                        alpha);*/
                                MatrixState.popMatrix();
                            }
                            //����Ƶ��
                            if (mCurMediaType == 0) {
                                MatrixState.pushMatrix();
                                MatrixState.translate(0.0F, 0.12F, 0.01F);
                                MatrixState.scale(iconWidth - 0.8F,
                                        iconHeight - 0.8F, 1.0F);
                                //�Ƿ񵯳��˶Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //����Ƶ�м�Ĳ��ż�
                                mTextureRect.drawSelf(mIconPlayId, alpha);
                                MatrixState.popMatrix();
                            }
                        }//��APKӦ������
                        else {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.12F, 0.01F);
                            MatrixState.scale(iconWidth - 0.25F,
                                    iconHeight - 0.3F, 1.0F);
                            //APKͼ�껹û�г�ʼ�� ��ȥ��ʼ��
                            if (info.imageTextureId <= 0) {
                                //�Ƿ񵯳��˶Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //����װ��Ĭ�ϵ�ͼ��
                                mTextureRect.drawSelf(mIconLauncherId, alpha);
                                //û�е������һҳ����һҳ���߸ճ�ʼ��
                                if (!mIsLoadingDelayed) {
                                    //ͼƬû�м��غ�
                                    if (!info.imageDecode) {
                                        //��ʼ����װ��Ĭ�ϵ�ͼ�� ��ʹimageDecode=true
                                        mResExecutor
                                                .execute(new LoadAppIconRunnable(
                                                        info));
                                        //ͼƬ���غ���
                                    } else {
                                        //��ʼ����װ�Լ���ͼ��
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
                                //APKͼ���ʼ����� ���Ի���
                            } else {
                                //�Ƿ񵯳��˶Ի���
                                float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                                //��APKͼ��
                                mTextureRect.drawSelf(info.imageTextureId,
                                        alpha);
                            }
                            MatrixState.popMatrix();
                        }

                        if (info.nameTextureId <= 0) {
                            //��ʼ��Ӧ�ø��Ե�����
                            info.nameTextureId = Utils.initTexture(Utils
                                    .generateWLT(Utils.subStringCN(info.name, 14),
                                            32.0F, mContext.getResources()
                                                    .getColor(R.color.white),
                                            256, 256, Alignment.ALIGN_CENTER));
                        } else {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, -0.38F, -0.01F);
                            MatrixState.scale(iconWidth, iconHeight, 1.0F);
                            //�Ƿ񵯳��˶Ի���
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //��Ӧ�ø��Ե�����
                            mTextureRect.drawSelf(info.nameTextureId, alpha);
                            MatrixState.popMatrix();
                        }
                        //�ļ�������&&û�е����Ի���  || �����˶Ի��� && �ļ��������
                        if (mFocusIndex == offset && !mIsOptMenuShow
                                || mIsOptMenuShow && mCurIndex == offset) {
                            /*MatrixState.pushMatrix();
                            MatrixState.translate(0.0F, 0.05F, 0.011F);
                            MatrixState.scale(0.15F + iconWidth,
                                    0.25F + iconHeight, 1.0F);
                            float alpha = (mIsOptMenuShow ? 0.5F : 1.0F);
                            //�������ͻ��߿򱳾�
                            mTextureRect.drawSelf(mIconBgId, alpha);
                            MatrixState.popMatrix();*/
                        }

                        MatrixState.popMatrix();
                    }
                    //����һ���ļ��ٻ���һ��
                    ++offset;
                }
                //�ļ����غ��˵����ļ���Ϊ0
            } else if (mIsMediaUpdated) {
                MatrixState.pushMatrix();
                MatrixState.translate(0.58F, 0.0F, 0.1F + -iconDistance);
                MatrixState.scale(2.0F, 0.3125F, 1.0F);
                //������û���κ��ļ��е�����³���һ�������Ŀ��
                mTextureRect.drawSelfOrigin(mIconBgId);
                MatrixState.popMatrix();
                //�ļ���û�м��غ�
            } else {
                //�����Ǽ��ص��ĸ�ȦȦ
                drawLoadingIcon();
            }
            //�����˶Ի���
            if (mIsOptMenuShow) {
                drawFileOptMenu(mOptMenuType);
            }

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, -1.6F, 1.0F + -iconDistance);
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            //�����ؼ������� ��͸����ɫ
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
            //���ڵ�һҳ && û�е����˶Ի���
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
            //�������һҳ&&û�е����Ի���
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

        //���ذ���Ǹ���ͷ
        private void drawResetIcon() {
            //drawTimeEle();
            MatrixState.pushMatrix();
            MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            MatrixState.translate(0.0F, 0.0F, -iconDistance);
            MatrixState.scale(0.5F, 0.5F, 1.0F);
            if (isLookingAtObject(0.5F, 0.5F, iconDistance)) {
                //��¼�ذ��ͷ��������״̬
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

        //�����СԲ��
        private void drawAnchor() {
            MatrixState.pushMatrix();
          //  MatrixState.translate(0.0F, 0.0F, -1.0F);
            MatrixState.translate(0.0F, 0.0F, -iconDistance+1.5F);
            MatrixState.scale(0.045F, 0.045F, 1.0F);
            mTextureRect.drawSelf(mWhiteCircleId);
            MatrixState.popMatrix();
        }

        private void drawTimeEle() {
            // draw time text//��ʱ��͵���
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
            //��������
            drawThemeBg();
            switch (mMenuLevel) {
                //��0��
                case 0:
                    //������������ ������������� �Լ�������
                    drawTimeEle();
                    drawTabBgAndTitle();
                    if (mTabNo != 0) {
                        //���豸�洢����ҳ�Լ����ؼ�
                        //drawDiskList();
                    } else {
                        //����ҳ �Լ����ؼ�
                        //drawCategoryList();
                    }
                    drawMediaFileList();
                    mIsFileList = true;
                    break;
                case 1:
                    //drawTimeEle();
                    //drawTabBgAndTitle();
                    //����ӦӦ�õı��⡢���� �ͻ�ɫ����
                    //drawMediaBgAndTitle();
                    //��Ӧ�����������ļ��б� �Լ����ܲ˵���
                    //drawMediaFileList();
                    //mIsFileList = true;
                    //break;
                case 2:
                    //���ļ��ı��� ���ļ������� �ͻ�ɫ����
                    //drawDiskBgAndTitle();
                    //���ļ���ͼ�� �ļ��� ���ܼ� �Լ����ض���
                    //drawDiskFileList();
                    //mIsFileList = true;
            }
            //���ذ���Ǹ���ͷ
            drawResetIcon();
            //�����СԲ��
            drawAnchor();
        }

        public void onSurfaceCreated(EGLConfig config) {
            GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            MatrixState.setInitStack();
            MatrixState.setCamera();
            //��ʼ�����ؼ�
            initTexture();
            //��ʼ��������
            initTheme();
            firstload();

        }

        public void firstload() {
            mTabNuLeft = 1;
            mCurMediaType = 1;
            mIsMediaUpdated = false;
            //���������mIsMediaUpdated = true
            //���뵽����ʾ�ļ��Ľ���
            //�߳�ִ������ �ҳ���ǰҪչʾ�����͵������ļ��������ڼ�����
            mFileExecutor.execute(new ScanListRunnable(1));
            //��һҳ
            mPageNo = 0;
            //����ϵͳ�������һҳ ��һҳ Ҫ���¿�ʼ��ͼ�� true��ʾҪ�ػ�
            mIsLoadingDelayed = true;
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            return;
        }
    }
}
