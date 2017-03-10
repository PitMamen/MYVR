package com.kamino.mygallery;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.kamino.mygallery.device.DeviceManager;
import com.kamino.mygallery.device.DiskInfo;
import com.kamino.mygallery.device.FileUtils;
import com.kamino.mygallery.file.FileCategoryHelper;
import com.kamino.mygallery.file.FileComparatorHelper;
import com.kamino.mygallery.file.FileInfo;
import com.kamino.mygallery.file.FileType;
import com.kamino.mygallery.file.SortType;
import com.kamino.mygallery.file.Util;
import com.kamino.mygallery.model.TextureBall;
import com.kamino.mygallery.model.TextureRect;
import com.kamino.mygallery.util.LocalImageLoader;
import com.kamino.mygallery.util.MatrixState;
import com.kamino.mygallery.util.Utils;

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

import static com.kamino.mygallery.util.LocalImageLoader.decodeThumbBitmapForFile;

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
    private int mCurFileBmpId = -1;
    private float scale3;
    private int mThemeTextureId;
    private int outAnimPosition = -1;
    private int inAnimPosition = -1;
    private long startTime1;
    private long startTime2;
    private float scale1;
    private float scale2;
    private int mIconBackId;
    private int mIconBackOnId;
    private int mIconDeleteId;
    private int mIconDeleteOnId;
    private int mIconDeleteDownId;
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
    private boolean[] mToolBtnFocus = new boolean[4];
    //�ļ������� ��һ��int���͵�ֵmFocusIndex��¼
    private int mFocusIndex = -1;
    private DeviceManager mDeviceManager;
    FileCategoryHelper mCategoryHelper;
    FileComparatorHelper mComparatorHelper;
    FileType[] mFileTypes;
    private Thread mUpdateListThread;
    boolean mUpdateFlag;
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
    public float iconSpace = 0.05F;
    public float iconWidth = 1.4F;
    public float iconHeight = 1.4F;
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
    private boolean isDeleteClick = false;
    private boolean isShowAnimation = false;
    private boolean isHasImageFocused = false;
    private boolean hasDeleteAppFocused = false;
    private long startAnimationTime;
    private float animationOffset;
    private float animatedOffset;
    private float animationEndOffset;
    private float animTime = 350.0F;
    private int currentLookPosition = -1;
    private int deleteIconFocusedTextureId;
    private float deleteIconSize = 0.25F;
    private int deleteIconTextureId;
    private int NoImageTextureId;
    private int currentDeleteFocused = -1;
    private int dotTextureId;



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


    private int getPageIndex(int index) {
        return 1 + index / 8;
    }

    private int getTotalPageNum() {
        if (mCurFileList != null) {
            int size = mCurFileList.size();
            if (size != 0) {
                if (size < 8) {
                    return 1;
                }

                if (size % 8 != 0) {
                    return 1 + size / 8;
                }

                return size / 8;
            }
        }

        return 0;
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

        NoImageTextureId = Utils.initTexture(res.getString(R.string.no_pictures),Color.WHITE,30.0F,200);

        deleteIconTextureId = Utils.initTexture(res, R.drawable.delete);
        deleteIconFocusedTextureId = Utils.initTexture(res, R.drawable.delete_focused);
        dotTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_page_indicator);

        mIconDeleteId = Utils.initTexture(res, R.drawable.ic_delete);

        mIconDeleteOnId = Utils.initTexture(res, R.drawable.ic_delete_blu);

        mIconDeleteDownId = Utils.initTexture(res, R.drawable.ic_delete_red);

        mIconBackId = Utils.initTexture(res, R.drawable.ic_back_wait);
        //����ƶ������ؼ�
        mIconBackOnId = Utils.initTexture(res, R.drawable.ic_back_blu);
        //��ɫ���ϼ�
        mIconUpId = Utils.initTexture(res, R.drawable.ic_left);
        //����ƶ������ϼ�
        mIconUpOnId = Utils.initTexture(res, R.drawable.ic_left_blu);
        //��ɫ���ϼ�
        mIconUpNoneId = Utils.initTexture(res, R.drawable.ic_left_hui);
        //��ɫ���¼�
        mIconDownId = Utils.initTexture(res, R.drawable.ic_right);
        //����ƶ������¼�
        mIconDownOnId = Utils.initTexture(res, R.drawable.ic_right_blu);
        //��ɫ���¼�
        mIconDownNoneId = Utils.initTexture(res, R.drawable.ic_right_hui);

        //�ذ��
        mIconResetId = Utils.initTexture(res, R.drawable.ic_reset);
        //����ƶ����ذ��
        mIconResetOnId = Utils.initTexture(res, R.drawable.ic_reset_on);
        //��Ƶ�м�Ĳ��ż�


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
                    goPreviousPage();
                }
            } else if (mCurSortFileList.size() % 8 == 0) {
                goPreviousPage();
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

    public void goNextPage() {
        if(mMenuLevel==0){
            if ( !isDeleteClick && !isShowAnimation
                    && mPageNo+1 < getTotalPageNum()) {
                startAnimationTime = System.currentTimeMillis();
                isShowAnimation = true;
                animationOffset = 4.0F * -(iconWidth+iconSpace);
                animationEndOffset = animatedOffset + animationOffset;
            }
        }else if(mMenuLevel==1){
            if(mCurIndex+1==mCurFileList.size()){
                ((MainActivity)mContext).showToast(mContext.getString(R.string.no_more_last));
            }else{
                scale3=0.0F;
                mCurFileBmpId = -1;
                mCurFile = mCurFileList.get(++mCurIndex);
                mPageNo = getPageIndex(mCurIndex)-1;
                animatedOffset = -mPageNo*4*(iconWidth+iconSpace);

            }

        }

    }

    public void goPreviousPage() {
        if(mMenuLevel==0){
            if ( !isDeleteClick && !isShowAnimation && mPageNo+1 > 1) {
                startAnimationTime = System.currentTimeMillis();
                isShowAnimation = true;
                animationOffset = 4.0F * (iconWidth+iconSpace);
                animationEndOffset = animatedOffset + animationOffset;
            }
        }else if(mMenuLevel==1){
            if(mCurIndex==0){
                ((MainActivity)mContext).showToast(mContext.getString(R.string.no_more_first));
            }else{
                scale3=0.0F;
                mCurFileBmpId=-1;
                mCurFile = mCurFileList.get(--mCurIndex);
                mPageNo = getPageIndex(mCurIndex)-1;
                animatedOffset = -mPageNo*4*(iconWidth+iconSpace);
            }
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
                        if (mToolBtnFocus[0]) {
                            if (!isDeleteClick) {
                                ((MainActivity) mContext).finish();
                            } else {
                                isDeleteClick = !isDeleteClick;
                            }
                        }
                        if (mToolBtnFocus[3]) {
                            isDeleteClick = !isDeleteClick;
                            return;
                        }
                        if (mToolBtnFocus[1]) {
                            goPreviousPage();
                            return;
                        }
                        if (mToolBtnFocus[2]) {
                            goNextPage();
                            return;
                        }
                        if (isDeleteClick) {
                            if (currentLookPosition == 2) {
                                if (1 + mPageNo+1 <= getTotalPageNum()) {
                                    isShowAnimation = true;
                                    startAnimationTime = System.currentTimeMillis();
                                    animationOffset = 4.0F * -(iconWidth+iconSpace);
                                    animationEndOffset = animatedOffset + animationOffset;
                                    return;
                                }
                            } else if (currentLookPosition == 0) {
                                if (mPageNo+1 != 1) {
                                    startAnimationTime = System.currentTimeMillis();
                                    isShowAnimation = true;
                                    animationOffset = 4.0F * (iconWidth+iconSpace);
                                    animationEndOffset = animatedOffset + animationOffset;
                                    return;
                                }
                            }
                            if (currentDeleteFocused != -1 && mCurFileList.size() > 0) {
                                //ɾ��ͼƬ�Ĳ���
                                mCurIndex = currentDeleteFocused;
                                mCurFile = (FileInfo) mCurFileList.get(currentDeleteFocused);
                                deleteCurFile(true);
                                return;
                            }
                        } else if (!isDeleteClick && !isShowAnimation) {
                           if (currentLookPosition == 2) {
                                if (1 + mPageNo+1 <= getTotalPageNum()) {
                                    isShowAnimation = true;
                                    startAnimationTime = System.currentTimeMillis();
                                    animationOffset = 4.0F * -(iconWidth+iconSpace);
                                    animationEndOffset = animatedOffset + animationOffset;
                                    return;
                                }
                            } else if (currentLookPosition == 0) {
                                if (mPageNo+1 != 1) {
                                    startAnimationTime = System.currentTimeMillis();
                                    isShowAnimation = true;
                                    animationOffset = 4.0F * (iconWidth+iconSpace);
                                    animationEndOffset = animatedOffset + animationOffset;
                                    return;
                                }
                            }else if (mFocusIndex != -1 && mCurFileList.size() > 0
                                    && getPageIndex(mFocusIndex) == mPageNo+1) {
                                mCurFile = mCurFileList.get(mFocusIndex);
                                mMenuLevel =1;
                                mCurIndex = mFocusIndex;
                                return;
                            }
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
                    if (!isDeleteClick) {
                        //�˳�Ӧ��
                        ((MainActivity) mContext).finish();
                        return;
                    } else {
                        isDeleteClick = !isDeleteClick;
                        return;
                    }



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
                    mCurFileBmpId=-1;
                    mIsFileList = false;
                    mMenuLevel = 0;
                    scale3 = 0.0F;
                    mCurIndex = 0;
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

    public final void goUp() {

    }

    public final void goDown() {


    }

    public final void onPause() {
        super.onPause();
        mUpdateFlag = false;
        mUpdateListThread = null;
        if (mTFReceiver != null) {
            mContext.unregisterReceiver(mTFReceiver);
            mTFReceiver = null;
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
        private void drawThemeBg() {
            MatrixState.pushMatrix();
            MatrixState.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            MatrixState.scale(mThemeBgScale, mThemeBgScale, mThemeBgScale);
            //��������
            mTextureBall.drawSelf(mThemeTextureId);
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






        //������ʱ����ֵļ���ȦȦ
        private void drawLoadingIcon() {
            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.0F, 0.2F + -iconDistance);
            MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
            MatrixState.scale(1.0F, 1.0F, 1.0F);
            //�����ص�ʱ����ֵ�ȦȦ�ı�������ɫ��
            mTextureRect.drawSelfOrigin(mLoadingBaseId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.0F, 0.0F, 0.21F + -iconDistance);
            MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
            MatrixState.scale(1.0F, 1.0F, 1.0F);
            //���ص�ʱ�򿴵���ȦȦ��һ���֣���ɫ��
            mTextureRect.drawSelfOrigin(mLoadingOnId);
            MatrixState.popMatrix();

            mLoadingAngle += 3.0F;
        }



        //��Ӧ�����������ļ��б� �Լ����ܲ˵���
        private void drawMediaFileList() {
            if (mIsMediaUpdated && mCurFileList != null && mCurFileList.size() != 0) {
                isHasImageFocused = false;
                hasDeleteAppFocused = false;
                mPageCount = getTotalPageNum();
                for (int i = 0; i < mCurFileList.size(); ++i) {
                    if (getPageIndex(i) >= -1 + mPageNo+1
                            && getPageIndex(i) <= 1 + mPageNo+1) {

                        FileInfo info = (FileInfo) mCurFileList.get(i);
                        if (mCurFileList.get(i).getImageTextureId() == -1){
                            if (!info.imageDecode) {
                                //��ͼƬ·�� ͼƬ����ͼ���� ִ��������mIsLoadingDelayed=true
                                mResExecutor
                                        .execute(new DecodeFileRunnable(
                                                info));
                                //ͼƬ������ϲ���չʾ��Ƶ��
                            }else{
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
                                    info.setImageTextureId( Utils
                                            .initTexture(bmp));
                                } else {
                                    info.setImageTextureId(0);
                                }
                            }
                        }
                        if (isShowAnimation) {
                            long passTime = System.currentTimeMillis()
                                    - startAnimationTime;
                            startAnimationTime = System.currentTimeMillis();
                            if (animationOffset > 0.0F) {
                                if (animatedOffset < animationEndOffset) {
                                    animatedOffset += (float) passTime / animTime
                                            * animationOffset;
                                } else {
                                    animatedOffset = animationEndOffset;
                                    isShowAnimation = false;
                                    mPageNo += -1;
                                }
                            }
                            if (animationOffset < 0.0F) {
                                if (animatedOffset > animationEndOffset) {
                                    animatedOffset += (float) passTime / animTime
                                            * animationOffset;
                                } else {
                                    animatedOffset = animationEndOffset;
                                    isShowAnimation = false;
                                    ++mPageNo;
                                }
                            }
                        }
                        float x = (iconWidth+iconSpace) * ((float) (i % 4) - 1.5F)
                                + (float) (i / 8) * 4.0F * (iconWidth+iconSpace)
                                + animatedOffset;
                        float y = -(iconHeight + iconSpace)
                                * (float) (i % 8 / 4)
                                + (iconWidth + iconSpace) / 2.0F
                                - 0.55F;
                        float alpha = 1.0F;
                        float yrotate = 0.0F;
                        if (getPageIndex(i) != mPageNo+1) {
                            alpha = 0.3F;
                            if(getPageIndex(i) == mPageNo){
                                yrotate = 30.0F;
                            }else{
                                yrotate = -30.0F;
                            }
                        }

                        MatrixState.pushMatrix();
                        MatrixState.translate(x, y+0.5F, 0.02F + -iconDistance);
                        MatrixState.rotate(yrotate,0.0F,1.0F,0.0F);
                        if (isLookingAtObject(iconWidth-0.2F, iconHeight-0.2F, iconDistance-0.02F)) {
                            if (outAnimPosition != i) {
                                outAnimPosition = i;
                                scale1 = 0.0F;
                                startTime1 = System.currentTimeMillis();
                            } else {
                                if (scale1 < 0.1F) {
                                    scale1 = 0.1F * ((float) (System
                                            .currentTimeMillis() - startTime1) / scaleAnimTime);

                                } else {
                                    scale1 = 0.1F;
                                }

                                if (getPageIndex(i) == mPageNo+1) {
                                    MatrixState.translate(0.0F, 0.0F, scale1);
                                    MatrixState.scale(1.0F + scale1, 1.0F + scale1,
                                            1.0F);
                                }
                            }
                            isHasImageFocused = true;
                            mFocusIndex = i;
                        } else if (outAnimPosition == i) {

                            outAnimPosition = -1;
                            inAnimPosition = i;
                            scale2 = 0.0F;
                            startTime2 = System.currentTimeMillis();
                            if (getPageIndex(i) == mPageNo+1) {
                                MatrixState.translate(0.0F, 0.0F, scale1);
                                MatrixState.scale(1.0F + scale1, 1.0F + scale1,
                                        1.0F);
                            }
                        } else if (inAnimPosition == i) {
                            if (scale2 < 0.1F) {
                                scale2 = 0.1F * ((float) (System
                                        .currentTimeMillis() - startTime2) / scaleAnimTime);

                            } else {
                                scale2 = 0.1F;
                            }

                            if (getPageIndex(i) == mPageNo+1) {
                                MatrixState.translate(0.0F, 0.0F, 0.1F - scale2);
                                MatrixState.scale(1.1F - scale2, 1.1F - scale2,
                                        1.0F);

                            }
                        }
                        // draw app icon
                        MatrixState.pushMatrix();
                        MatrixState.translate(0.0F, 0.0F, 0.0F);
                        MatrixState.scale(iconWidth,iconHeight, 1.0F);
                        mTextureRect.drawSelf(mCurFileList.get(i).getImageTextureId(),
                                alpha);
                        MatrixState.popMatrix();

                        // draw del icon
                        if (isDeleteClick && getPageIndex(i) == mPageNo+1
                                ) {
                            MatrixState.pushMatrix();
                            MatrixState.translate(0.7F-deleteIconSize/2, 0.7F-deleteIconSize/2, 0.003F);
                            MatrixState.scale(deleteIconSize,deleteIconSize,1.0F);
                            if (isLookingAtObject(deleteIconSize, deleteIconSize,
                                    -0.003F + iconDistance)) {
                                hasDeleteAppFocused = true;
                                currentDeleteFocused = i;
                                mTextureRect.drawSelf(deleteIconFocusedTextureId,
                                        1.0F);
                            } else {
                                mTextureRect.drawSelf(deleteIconTextureId, 1.0F);
                            }

                            MatrixState.popMatrix();
                        }

                        MatrixState.popMatrix();
                    }
                }

                currentLookPosition = -1;

                for (int i = 0; i < 3; ++i) {
                    MatrixState.pushMatrix();
                    MatrixState.translate((4*iconWidth+4*iconSpace) * (float) (i - 1), 0.0F,
                            0.02F-iconDistance);
                    if (isLookingAtObject(3.6F, 2.9F, iconDistance-0.02F)) {
                        currentLookPosition = i;
                       // Log.e("cscscscs","��������һҳ����Ӱ"+i);
                    }

                    MatrixState.popMatrix();
                }

                if (!isHasImageFocused) {
                    mFocusIndex = -1;
                }

                if (!hasDeleteAppFocused) {
                    currentDeleteFocused = -1;
                }

                /*int totalPage = getTotalPageNum();

                // draw page dot
                for (int i = 0; i < totalPage; ++i) {
                    MatrixState.pushMatrix();
                    float x = dotIconWidthWithMargin * (float) (i - totalPage / 2);
                    if (totalPage % 2 == 0) {
                        x = dotIconWidthWithMargin
                                * ((float) i - (float) (totalPage - 1) / 2.0F);
                    }

                    MatrixState.translate(x, -1.8F, -iconDistance+0.02F);
                    MatrixState.scale(dotIconSize, dotIconSize, 1.0F);
                    if (i + 1 == mPageNo+1) {
                        MatrixState.scale(2.0F, 2.0F, 1.0F);
                    }

                    mTextureRect.drawSelf(dotTextureId, 1.0F);
                    MatrixState.popMatrix();
                }*/



                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -2.0F, 1.0F + -iconDistance);
                MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                //�����ؼ������� ��͸����ɫ
                MatrixState.scale(2.5F, 0.8F, 1.0F);
                mTextureRect.drawSelfOrigin(mTransRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(-0.75F, -1.8F, 1.0F + -iconDistance);
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
                MatrixState.translate(-0.25F, -1.8F, 1.0F + -iconDistance);
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
                MatrixState.translate(0.25F, -1.8F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                //�������һҳ&&û�е����Ի���
                if (mPageNo != -1 + mPageCount) {
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

                MatrixState.pushMatrix();
                MatrixState.translate(0.75F, -1.8F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                if (!isDeleteClick) {
                    if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                        mToolBtnFocus[3] = true;
                        mTextureRect.drawSelfOrigin(mIconDeleteOnId);
                    } else {
                        mToolBtnFocus[3] = false;
                        mTextureRect.drawSelfOrigin(mIconDeleteId);
                    }
                } else {
                    if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                        mToolBtnFocus[3] = true;
                    } else {
                        mToolBtnFocus[3] = false;
                    }
                    mTextureRect.drawSelfOrigin(mIconDeleteDownId);
                }
                MatrixState.popMatrix();
            } else if (mIsMediaUpdated) {
                /*MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.1F + -iconDistance);
                MatrixState.scale(2.0F, 0.3125F, 1.0F);
                //������û���κ��ļ��е�����³���һ�������Ŀ��
                mTextureRect.drawSelfOrigin(mIconBgId);
                MatrixState.popMatrix();*/

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, 0.0F, 0.1F + -iconDistance);
                MatrixState.scale(2.0F, 0.4F, 1.0F);
                //������û���κ��ļ��е�����³���һ�������Ŀ��
                mTextureRect.drawSelfOrigin(NoImageTextureId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -2.0F, 1.0F + -iconDistance);
                MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                //�����ؼ������� ��͸����ɫ
                MatrixState.scale(1.0F, 0.8F, 1.0F);
                mTextureRect.drawSelfOrigin(mTransRectId);
                MatrixState.popMatrix();

                MatrixState.pushMatrix();
                MatrixState.translate(0.0F, -1.8F, 1.0F + -iconDistance);
                MatrixState.scale(0.3F, 0.3F, 1.0F);
                if (isLookingAtObject(0.3F, 0.3F, iconDistance - 1.0F)) {
                    mToolBtnFocus[0] = true;
                    mTextureRect.drawSelfOrigin(mIconBackOnId);
                } else {
                    mToolBtnFocus[0] = false;
                    mTextureRect.drawSelfOrigin(mIconBackId);
                }
                MatrixState.popMatrix();
            } else {
                //�����Ǽ��ص��ĸ�ȦȦ
                drawLoadingIcon();
            }

           //TODO ....................................

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

        //���������ֵĲ鿴ͼƬ�Ľ���
        private void drawOneImage(FileInfo info) {

            if (mCurFileBmpId < 0) {
                Bitmap bmp;
                if (Util.getFileExtName(info.name)
                        //  ���� String ����һ�� String �Ƚϣ������Ǵ�Сд
                        .equalsIgnoreCase("gif")) {
                    bmp = Util.getBitmap(mContext,
                            mContext.getContentResolver(),
                            info.path);
                } else {
                    LocalImageLoader.getInstance();
                    bmp = decodeThumbBitmapForFile(
                            (String) info.path, 2048, 1024);
                }
                //��ʼ��ͼƬ��bitmap
                mCurFileBmpId = Utils.initTexture(bmp);
                info.imageRatio = (float) bmp.getWidth()
                        / (float) bmp.getHeight();
            } else {
                MatrixState.pushMatrix();
                //  MatrixState.translate(0.0F, 0.0F, -1.0F);
                MatrixState.translate(0.0F, 0.0F, 2.2F + -iconDistance);
                MatrixState.scale(1.2F * info.imageRatio * scale3,
                        1.2F * scale3, 1.0F);
                mTextureRect.drawSelfOrigin(mCurFileBmpId);
                MatrixState.popMatrix();

                if (scale3 < 1.0F) {
                    scale3 = 0.01F + scale3;
                    return;
                }

                scale3 = 1.0F;
                return;
            }

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

            switch (mMenuLevel) {
                //��0��
                case 0:
                    //��������
                    drawThemeBg();
                    //������������ ������������� �Լ�������
                    drawTimeEle();
                    //drawTabBgAndTitle();
                    if (mTabNo != 0) {
                        //���豸�洢����ҳ�Լ����ؼ�
                        //drawDiskList();
                    } else {
                        //����ҳ �Լ����ؼ�
                        //drawCategoryList();
                    }
                    drawMediaFileList();
                    mIsFileList = true;
                    //���ذ���Ǹ���ͷ
                    drawResetIcon();
                    //�����СԲ��
                    drawAnchor();
                    break;
                case 1:
                    drawOneImage(mCurFile);
                    mIsFileList = true;
                    break;

                case 2:

            }

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
