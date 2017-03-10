package com.kamino.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.kamino.store.Data.DataUtils;
import com.kamino.store.Data.FileUtils;
import com.kamino.store.entity.APKEntity;
import com.kamino.store.entity.ImageEntity;
import com.kamino.store.model.Anchor;
import com.kamino.store.model.Background;
import com.kamino.store.model.TextureBall;
import com.kamino.store.model.TextureRect;
import com.kamino.store.utils.MatrixState;
import com.kamino.store.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

public class MainSurfaceView extends CardboardView {
	private static final float CAMERA_Z = 0.01F;
	public static final int TYPE_IMAGE = 4;
	public static final int TYPE_LOCAL = 0;
	public static final int TYPE_OL = 2;
	public static final int TYPE_STORE = 1;
	public static final int TYPE_TOOL = 5;
	public static final int TYPE_VR_LIVE = 3;
	private Anchor anchor;
	private float animTime;
	private float animatedOffset;
	private float animationEndOffset;
	private float animationOffset;
	private float appIconBgHeight;
	private float appIconMargin;
	private float appIconSize;
	private float appIconVerticalMargin;
	private float appIconWidthWithMargin;
	private TextureRect appTextureRect;
	private Background backBtnBg;
	private int backDefaultTextureId;
	private int backFocusedTextureId;
	private float backIconSize = 0.3F;
	private int backTextureId;
	private int left1gameTextureId;
	private int left1appTextureId;
	private int middle1bigTextureId;
	private int middle1smallTextureId;
	private int middle2smallTextureId;
	private int middle2bigTextureId;
	private int right1accountTextureId;
	private int right1manageTextureId;
	private float ballRadius;
	private float[] camera;
	private float[] cameraView;
	private int currentLookPosition = -1;
	private int currentPage;
	private int currentPosition = -1;
	int currentPower;
	private int deleteIconFocusedTextureId;
	private float deleteIconSize;
	private int deleteIconTextureId;
	private TextureRect deleteRect;
	private int deleteRedTextureId;
	private float dotIconMargin;
	private float dotIconSize;
	private float dotIconWidthWithMargin;
	private int dotTextureId;
	private TextureRect dotTextureRect;
	private boolean flag_index;
	private boolean flag_game;
	private boolean flag_app;
	private boolean flag_down;
	private boolean flag_account;
	private boolean flag_manage;
	private boolean flag_downloading;
	private boolean flag_downloaded;
	private boolean flag_leisure;
	private boolean flag_shoot;
	private boolean[] focusBtnFlags;
	private float[] headView;
	private TextureRect iconBackTextureRect;
	private TextureRect left1gameTextureRect;
	private TextureRect left1appTextureRect;
	private TextureRect middle1bigTextureRect;
	private TextureRect middle1smallTextureRect;
	private TextureRect middle2smallTextureRect;
	private TextureRect middle2bigTextureRect;
	private TextureRect right1accountTextureRect;
	private TextureRect right1manageTextureRect;
	private TextureRect mTextureRect;
	private float iconDistance;
	private int inAnimPosition;
	private boolean isBackFocused;
	private boolean isDeleteClick;
	private boolean isHasAppFocused;
	boolean isPower;
	private boolean isShowAnimation;
	private int UnConnectedinfoTextureId;
	private int NoDownloadingtaskTextureId;
	private int NoDownloadedTextureId;
	private Background fourBtnBg;
	private Background threeBtnBg;
	private TextureRect downlAPKBg;
	private TextureRect downlAPKBt;
	private int[] gameBtnDefaultIcons;
	private int[] gameBtnFocusedIcons;
	private int[] manageBtnDefaultIcons;
	private int[] manageBtnFocusedIcons;
	private float localBtnMargin;
	private float localBtnSize;
	private TextureRect UnConnectedinfoRect;
	static Context mContext;
	private String mCurrentLanguage;
	private String mCurrentTheme;
	private boolean mNeedUpdate;
	private int mTabNo = 0;
	MainSurfaceView.MyStereoRenderer mRenderer;
	private float[] modelView;
	private int outAnimPosition;
	Bitmap power;
	private float scale1;
	private float scale2;
	private float scaleAnimTime;
	private int senceLight;
	private long startAnimationTime;
	private long startTime1;
	private long startTime2;
	private TextureBall textureBall;
	private float[] mXRotationMatrix;
	private float[] mYRotationMatrix;
	private boolean mSensorMode;
	private ArrayList<ImageEntity> imagelists;
	private static Toast mtoast;
	private int[] mTitleArrayId;
	// 表示光标是否移动到了大标题上面
	private boolean[] mTabFocus;
	private int mBlueRectId;
	private int mGrayRectId;
	public float mRectBgXScale;
	public float mRectBgYScale;
	private int downlAPKID;
	// private Bitmap APKBGbitmap =Utils.getAPKBGBitmap();
	private int downBtDefaultTextureId;
	private int downBtFocusedTextureId;
	private int downOKBtDefaultTextureId;
	private int downOKBtFocusedTextureId;
	private int downingBtDefaultTextureId;
	private int downingBtFocusedTextureId;
	private int down;
	private boolean APKDEL;
	private ArrayList<APKEntity> appList;
	private ArrayList<APKEntity> GAMEList;
	private ArrayList<APKEntity> APPList;
	private ArrayList<APKEntity> LEISUREList;
	private ArrayList<APKEntity> SHOOTList;
	private int mLoadingOnId;
	private int[] downfrom;
	private APKEntity showapk;
	private File GAMEFILE;
	//定义线程池数量为3个
	private final static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
	//下载APK界面的APK的icon
	private static Bitmap IconBitmap;
	//下载APK界面的APK的场景缩略图1
	private static Bitmap Thumbnail1Bitmap;
	//下载APK界面的APK的场景缩略图2
	private static Bitmap Thumbnail2Bitmap;
	//下载APK界面的APK的场景缩略图3
	private static Bitmap Thumbnail3Bitmap;
	//下载APK界面的APK的场景缩略图4
	private static Bitmap Thumbnail4Bitmap;
	private boolean initdownimg;
	private static int finalprogress = 0;
	//下载的apk的总的字节数
	private static int APKlength = 0;
	private static int changeprogress = 0;
	private static int changeprogressinit = 0;
	private static String DOWNAPKURL;
	private static String SAVEAPKURL;
	private InstallPackageReceiver installOkReceiver;
	private static int downloadstate = 0;
	private static int parsexml = 0;
	private int downprogress;
	public float mLoadingAngle;
	private String state;
	private static boolean againdownimg = false;
	//loadingimg=0 表示下载完成 loadingimg=1表示正在下载 这时显示加载的圈圈 这时一个开关 根据值的不同显示不同的界面
	private static int loadingimg = 0;
	//表示下载4张缩略图的存活的线程数量
	private static int ThreadCount = 4;
	//在网上下载apk、图片等用handler处理子线程发送过来的消息
	public static Handler handler = new Handler() {
		// 接受子线程发送给主线程的信息的
		public void handleMessage(android.os.Message msg) {
			int type = msg.what;// 子线程的标识
			//更新下载APK界面的APK的icon
			if (type == 0) {
				System.out.println("IconBitmap收到了，，更新IconBitmap。。。" + (Bitmap) msg.obj);
				IconBitmap = (Bitmap) msg.obj;
			//更新下载APK界面的APK的场景缩略图1
			} else if (type == 1) {
				//下载完成减少1
				ThreadCount--;
				//当ThreadCount=0时 表示四张图下载完成
				if (ThreadCount == 0) {
					//显示下载APK的界面给用户看 不为0则显示加载的界面给用户看
					loadingimg = 0;
				}
				Thumbnail1Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail1Bitmap收到了，，更新Thumbnail1Bitmap。。。" + (Bitmap) msg.obj);
			//更新下载APK界面的APK的场景缩略图2
			} else if (type == 2) {
				ThreadCount--;
				if (ThreadCount == 0) {
					loadingimg = 0;
				}
				Thumbnail2Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail2Bitmap收到了，，更新Thumbnail2Bitmap。。。" + (Bitmap) msg.obj);
			//更新下载APK界面的APK的场景缩略图3
			} else if (type == 3) {
				ThreadCount--;
				if (ThreadCount == 0) {
					loadingimg = 0;
				}
				Thumbnail3Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail3Bitmap收到了，，更新Thumbnail3Bitmap。。。" + (Bitmap) msg.obj);
			//更新下载APK界面的APK的场景缩略图4
			} else if (type == 4) {
				ThreadCount--;
				if (ThreadCount == 0) {
					loadingimg = 0;
				}
				Thumbnail4Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail4Bitmap收到了，，更新Thumbnail4Bitmap。。。" + (Bitmap) msg.obj);
			//拿到下载apk的宽度 这里开三个线程一起下载 每个线程负责不一样的范围 三个线程执行完毕 下载完成
			} else if (type == 5) {
				String url = msg.getData().getString("url");
				//savepath 表示线程下载的数据保存的位置
				String savepath = msg.getData().getString("savepath");
				System.out.println("程序下载的宽度是。。。。。。。。。。。。。。" + msg.arg1 + "-" + msg.arg2 + "..." + url);
				// DataUtils.downloadapk("http://qnfile.orangelive.tv/TXMV%28com.txmv%29.apk",msg.arg1,msg.arg2
				// ,handler);
				MyAsyncTask task = new MyAsyncTask(handler, msg.arg1, msg.arg2, savepath);
				//异步执行任务 可以同时进行下载
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
			//将下载apk的进度重置为0
			} else if (type == 6) {
				finalprogress = 0;
			//更新下载的进度 将进度负值给changeprogress
			} else if (type == 7) {
				int progress = msg.getData().getInt("progress");
				String savepath = msg.getData().getString("savepath");
				changeprogress = (int) (((float) (progress) / APKlength) * 100);
				//进度为100表示下载完成  或者进度为99但是文件的字节数=下载文件的字节数也表示下载完成  因为下载的时候temp有可能发生少加的情况
				if ((changeprogress == 100) || (changeprogress == 99 && new File(savepath).length() == APKlength)) {
					//表示文件已经下载完成 这里自动去安装这个下载的apk
					downloadstate = 1;
					//根据文件的路径安装apk
					FileUtils.installAPK(mContext, savepath);
				}
				System.out.println("程序的进度是。。。。。。。。。。。。。。" + changeprogress);
			//显示正在下载的界面
			} else if (type == 8) {
				loadingimg = (int) msg.obj;
			}
		};
	};




	public MainSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//关联StereoRenderer
		init(context);
		//初始化数据
		initData();
	}

	public MainSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//关联StereoRenderer
		init(context);
		//初始化数据
		initData();
	}

	// 初始化基本属性值
	private void initData() {
		// APKBGbitmap =Utils.getAPKBGBitmap();
		// sappManager = StoreAPPManager.getInstance(mContext);
		System.out.println("initData方法被执行。。。。。。。。。");
		downlAPKID = 0;
		appIconSize = 0.9F;
		appIconMargin = 0.2F;
		appIconWidthWithMargin = appIconSize + appIconMargin;
		deleteIconSize = 0.25F;
		dotIconSize = 0.03F;
		dotIconMargin = 0.2F;
		dotIconWidthWithMargin = dotIconSize + dotIconMargin;
		animTime = 350.0F;
		flag_index = true;
		isHasAppFocused = false;
		currentPage = 1;
		outAnimPosition = -1;
		inAnimPosition = -1;
		scaleAnimTime = 150.0F;
		focusBtnFlags = new boolean[4];
		downfrom = new int[6];
		isPower = false;
		currentPower = 0;
		mCurrentLanguage = "zh";
		mCurrentTheme = "sence_light1";
		mSensorMode = true;
		GAMEFILE = new File(DataUtils.GAMEAPKPATH);
		initdownimg = false;
		GAMEList = new ArrayList<APKEntity>();
		appList = new ArrayList<APKEntity>();
		APPList = new ArrayList<APKEntity>();
		LEISUREList = new ArrayList<APKEntity>();
		SHOOTList = new ArrayList<APKEntity>();
		mTitleArrayId = new int[2];
		mTabFocus = new boolean[2];
		downprogress = 0;
		down = 1;
		APKDEL = false;
		mRectBgXScale = 6.0F;
		mRectBgYScale = 3.5F;
		localBtnMargin = 0.3F;
		localBtnSize = 0.3F;
		iconDistance = -5.0F;
		appIconVerticalMargin = 0.25F;
		appIconBgHeight = 1.05F;
		ballRadius = 50.0F;

	}

	private void initWorld() {
		System.out.println("initWorld方法被执行。。。。。。。。。");
		GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
		//初始化变换矩阵
		MatrixState.setInitStack();
		anchor = new Anchor(mContext);
		//小圆点的大小
		anchor.setUnitSize(0.012f);
		mTextureRect = new TextureRect(mContext, 1.0F, 1.0F);
		textureBall = new TextureBall(mContext);
		// 初始化的是返回、上一页、下一页、删除图标的大小 以及只有一个返回键的大小
		iconBackTextureRect = new TextureRect(mContext, backIconSize, backIconSize);
		// 初始化的是标示页数小点的圆形大小
		dotTextureRect = new TextureRect(mContext, dotIconSize, dotIconSize);
		// 初始化的是左边的游戏图片
		left1gameTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// 初始化的是本地APP里面个应用图标的大小
		appTextureRect = new TextureRect(mContext, appIconSize, appIconBgHeight);
		// 初始化的是左边的应用图片
		left1appTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// 初始化的是中间左上角的大图
		middle1bigTextureRect = new TextureRect(mContext, 2.1F, 1.5F);
		// 初始化的是中间左下角的小图
		middle1smallTextureRect = new TextureRect(mContext, 1.01F, 0.85F);
		// 初始化的是中间左下角第二张的小图
		middle2smallTextureRect = new TextureRect(mContext, 1.01F, 0.85F);
		// 初始化的是中间右边的大图
		middle2bigTextureRect = new TextureRect(mContext, 1.5F, 2.4F);
		// 初始化的是右边的我的信息的图片
		right1accountTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// 初始化的是右边的下载管理的图片
		right1manageTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// 初始化的是只有一个返回键的地板的矩形大小
		backBtnBg = new Background(mContext, 1.0f, 0.8f);
		// 初始化的是本地APP里面的本地引用四个字的大小
		UnConnectedinfoRect = new TextureRect(mContext, 2.0f, 0.5f);
		// 初始化的是卸载操作的时候图标右上角的小叉叉的大小
		deleteRect = new TextureRect(mContext, deleteIconSize, deleteIconSize);
		// 初始化的是有四个控制按钮的矩形
		fourBtnBg = new Background(mContext, 3.0f, 0.8f);
		// 初始化的是有三个控制按钮的矩形
		threeBtnBg = new Background(mContext, 2.4f, 0.8f);
		// 初始化的是下载APK界面的黑色大背景
		downlAPKBg = new TextureRect(mContext, 5.8f, 3.8f);
		// 初始化的是下载APK界面的下载按钮的大小
		downlAPKBt = new TextureRect(mContext, 2.0f, 0.5f);
		// 画的是只有一个返回键在上面的小矩形
		// backBgTextureId = Utils
		// .initTexture((Bitmap) Utils.getBitmapRect(2, 2,
		// getResources().getColor(R.color.bg_back)));
		// Bitmap downBtBitmap = Utils.getdownBtBitmap(readBitMap(mContext,
		// R.drawable.image_button_long_focused),"下载");
		downBtDefaultTextureId = Utils.initTexture(mContext, "下载", 100, false);
		downBtFocusedTextureId = Utils.initTexture(mContext, "下载", 100, true);
		downOKBtDefaultTextureId = Utils.initTexture(mContext, "打开", 100, false);
		downOKBtFocusedTextureId = Utils.initTexture(mContext, "打开", 100, true);
		downingBtDefaultTextureId = Utils.initTexture(mContext, 0 + "%", 0, false);
		downingBtFocusedTextureId = Utils.initTexture(mContext, 0 + "%", 0, true);
		// 初始化返回键ID
		backDefaultTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_back_default);
		backFocusedTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_back_focused);
		backTextureId = backDefaultTextureId;
		// 初始化加载的时候看到的圈圈的一部分（白色）ID
		mLoadingOnId = Utils.initTexture(mContext.getResources(), R.drawable.loading_img);
		// 初始化红色的垃圾箱ID
		deleteRedTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_delete_red);
		// 初始化卸载的小叉叉ID
		deleteIconTextureId = Utils.initTexture(mContext.getResources(), R.drawable.delete);
		deleteIconFocusedTextureId = Utils.initTexture(mContext.getResources(), R.drawable.delete_focused);
		// 初始化页面页数的小标记ID
		dotTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_page_indicator);
		// 初始化左边的游戏图片ID
		left1gameTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_game,
				mContext.getResources().getString(R.string.indexgame));
		// left1gameTextureId = Utils.initTexture(mContext.getResources(),
		// R.drawable.ic_home_game);
		// 初始化中间的那五张图片ID
		left1appTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_app,
				mContext.getResources().getString(R.string.indexapp));
		middle1bigTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad1);
		middle1smallTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad2);
		middle2smallTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad3);
		middle2bigTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad4);
		//初始化右边的账号ID
		right1accountTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_user,
				mContext.getResources().getString(R.string.indexaccount));
		//初始化右边的管理ID
		right1manageTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_manage,
				mContext.getResources().getString(R.string.indexmanage));

		// 画球的背景图片
		updateTheme(true);
		// 画下方返回、上一页、下一页、删除键
		try {
			TypedArray localBtnDefs = mContext.getResources().obtainTypedArray(R.array.manage_btn_default);
			manageBtnDefaultIcons = new int[localBtnDefs.length()];
			for (int i = 0; i < localBtnDefs.length(); i++) {
				manageBtnDefaultIcons[i] = Utils.initTexture(mContext.getResources(), localBtnDefs.getResourceId(i, 0));
			}
			localBtnDefs.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		// 画下方返回、上一页、下一页、删除键被选中的状态
		try {
			TypedArray locatBtnFocus = mContext.getResources().obtainTypedArray(R.array.manage_btn_focused);
			manageBtnFocusedIcons = new int[locatBtnFocus.length()];
			for (int i = 0; i < locatBtnFocus.length(); i++) {
				manageBtnFocusedIcons[i] = Utils.initTexture(mContext.getResources(),
						locatBtnFocus.getResourceId(i, 0));
			}
			locatBtnFocus.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		// 画下方返回、上一页、下一页
		try {
			TypedArray localBtnDefs = mContext.getResources().obtainTypedArray(R.array.game_btn_default);
			gameBtnDefaultIcons = new int[localBtnDefs.length()];
			for (int i = 0; i < localBtnDefs.length(); i++) {
				gameBtnDefaultIcons[i] = Utils.initTexture(mContext.getResources(), localBtnDefs.getResourceId(i, 0));
			}
			localBtnDefs.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		// 画下方返回、上一页、下一页被选中的状态
		try {
			TypedArray locatBtnFocus = mContext.getResources().obtainTypedArray(R.array.game_btn_focused);
			gameBtnFocusedIcons = new int[locatBtnFocus.length()];
			for (int i = 0; i < locatBtnFocus.length(); i++) {
				gameBtnFocusedIcons[i] = Utils.initTexture(mContext.getResources(), locatBtnFocus.getResourceId(i, 0));
			}
			locatBtnFocus.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		//将图片的x.y.z平移 大小等保存在ImageEntity里面
		imagelists = new ArrayList<ImageEntity>();
		imagelists.add(new ImageEntity(-2.6F, 0.4F, iconDistance + 0.15F, 27F, 1.3F, 1.2F, left1gameTextureRect,
				left1gameTextureId));
		imagelists.add(new ImageEntity(-2.6F, -0.9F, iconDistance + 0.15F, 27F, 1.3F, 1.2F, left1appTextureRect,
				left1appTextureId));
		imagelists.add(
				new ImageEntity(-0.75F, 0.2F, iconDistance, 0, 2.1F, 1.5F, middle1bigTextureRect, middle1bigTextureId));
		imagelists.add(new ImageEntity(-1.3F, -1.0F, iconDistance, 0, 1.01F, 0.8F, middle1smallTextureRect,
				middle1smallTextureId));
		imagelists.add(new ImageEntity(-0.2F, -1.0F, iconDistance, 0, 1.01F, 0.8F, middle2smallTextureRect,
				middle2smallTextureId));
		imagelists.add(new ImageEntity(1.15F, -0.22F, iconDistance, 0, 1.5F, 2.4F, middle2bigTextureRect,
				middle2bigTextureId));
		imagelists.add(new ImageEntity(2.65F, 0.4F, iconDistance + 0.15F, -27F, 1.3F, 1.2F, right1accountTextureRect,
				right1accountTextureId));
		imagelists.add(new ImageEntity(2.65F, -0.9F, iconDistance + 0.15F, -27F, 1.3F, 1.2F, right1manageTextureRect,
				right1manageTextureId));

		initTextTexture();
	}

	// TODO 初始化文字
	private void initTextTexture() {

		Resources res = mContext.getResources();
		// TODO Auto-generated method stub
		// 画的是游戏、应用里面的抱歉，未连接
		UnConnectedinfoTextureId = Utils.initTexture(mContext.getString(R.string.unconnected), 28.0f, 300, 60);
		// 画的是下载管理里面的无下载任务
		NoDownloadingtaskTextureId = Utils.initTexture(mContext.getString(R.string.Nodownloadtask), 28.0f, 300, 60);
		// 画的是下载管理里面的无下载完成
		NoDownloadedTextureId = Utils.initTexture(mContext.getString(R.string.Nodownloaddown), 28.0f, 300, 60);
		// 大标题 正在下载、下载完成 文字
		String[] titleArray = res.getStringArray(R.array.managetitle);
		// 大标题 正在下载、下载完成
		for (int i = 0; i < titleArray.length; ++i) {
			mTitleArrayId[i] = Utils.initTexture(titleArray[i], getResources().getColor(R.color.text_color));
		}
		// 画淡蓝色
		mBlueRectId = Utils.initTexture(Utils.getRectBitmap(getResources().getColor(R.color.blue)));
		// 画灰色
		mGrayRectId = Utils.initTexture(Utils.getRectBitmap(getResources().getColor(R.color.text_color_grey)));

	}

	// TODO
	// 重置状态（换页面）
	private void resetStatus() {
		//首页的开关
		flag_index = false;
		//游戏的开关
		flag_game = false;
		//应用的开关
		flag_app = false;
		//下载界面的开关
		flag_down = false;
		//账号的开关
		flag_account = false;
		//管理的开关
		flag_manage = false;
		//管理里面正在下载界面的开关
		flag_downloading = false;
		//管理里面下载完成界面的开关
		flag_downloaded = false;
		//休闲娱乐的开关
		flag_leisure = false;
		//射击的开关
		flag_shoot = false;
		//加载APK界面时只加载一次所有的图片
		initdownimg = false;
		//下载APK界面的ID
		downlAPKID = 0;
		//表示是否画下载界面的垃圾箱
		APKDEL = false;
		//有垃圾箱的时候表示是否看到的是垃圾箱  没有垃圾箱表示是否看到了下载或者打开
		down = 1;
		for (int i = 0; i < downfrom.length; i++) {
			downfrom[i] = 0;
		}
		// 上一页下一页动画平移的距离
		animatedOffset = 0.0F;
		// 缩小
		inAnimPosition = -1;
		// 放大
		outAnimPosition = -1;
		//放大
		scale1 = 0.0F;
		//缩小
		scale2 = 0.0F;
	}

	// TODO
	private void draw(Eye eye) {
		// System.out.println("OnDraw方法被执行。。。。。。。。。");
		float eyeX;
		if (eye.getType() == Eye.Type.LEFT) {
			eyeX = 2.0E-4F;
		} else {
			eyeX = -2.0E-4F;
		}
		// set anchor camera
		Matrix.setLookAtM(camera, 0, eyeX, 0.0F, CAMERA_Z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
		MatrixState.copyMVMatrix(camera);
		// draw anchor
		float xoff;
		if (eye.getType() == Eye.Type.LEFT) {
			xoff = 0.02F;
		} else {
			xoff = -0.02F;
		}
		//画小圆点
		MatrixState.pushMatrix();
		MatrixState.translate(xoff, 0.0F, -1.0F);
		anchor.drawSelf();
		MatrixState.popMatrix();
		//在下面这几行代码之前画的东西都是随着眼睛而动的
		if (isSensorMode()) {
			Matrix.multiplyMM(cameraView, 0, mXRotationMatrix, 0, eye.getEyeView(), 0);
			Matrix.multiplyMM(cameraView, 0, mYRotationMatrix, 0, cameraView, 0);
		} else {
			Matrix.multiplyMM(cameraView, 0, mYRotationMatrix, 0, mXRotationMatrix, 0);
		}
		Matrix.multiplyMM(cameraView, 0, cameraView, 0, camera, 0);
		MatrixState.copyMVMatrix(cameraView);
		//在上面这几行代码之后画的东西都是固定的
		// draw scene 画背景球
		MatrixState.pushMatrix();
		MatrixState.scale(ballRadius, ballRadius, ballRadius);
		MatrixState.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		textureBall.drawSelf(senceLight, 1.0F);
		MatrixState.popMatrix();
		//判断wifi是否连接
		if (isContectWIFI()) {
			//连接并且GAMEList没有值
			if (GAMEList.size() == 0) {
				//并且网上的XML文件已经下载到本地
				if (GAMEFILE.exists()) {
					//因为有左右眼 这里为了防止加载两次 就定义一个parsexml 就是让他只解析一次
					parsexml++;
					if (parsexml == 1) {
						System.out.println("GAMEFILE存在。。。。。。解析XML");
						fixedThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								GAMEList = DataUtils.parseXML(DataUtils.GAMEAPKPATH);
								for (int i = 0; i < GAMEList.size(); i++) {
									System.out.println(GAMEList.get(i).getName() + "....." + i
											+ GAMEList.get(i).getPackagename());
								}
							}
						});
					}
			    //网上的XML文件没有下载到本地
				} else {
					//因为有左右眼 这里为了防止加载两次 就定义一个parsexml 就是让他只解析一次
					parsexml++;
					if (parsexml == 1) {
						fixedThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								System.out.println("没有XML文件。。。。。。。。。。。。。");
								//没有在本地就先下载到本地 成功之后再去解析XML
								if (DataUtils.saveGAMEXML(DataUtils.GAMEAPKPATH)) {
									System.out.println(DataUtils.GAMEAPKPATH + "..............");
									System.out.println("GAMEFILE不存在。。。。。。网上下载后解析XML");
									GAMEList = DataUtils.parseXML(DataUtils.GAMEAPKPATH);
									for (int i = 0; i < GAMEList.size(); i++) {
										System.out.println(GAMEList.get(i).getName() + "....." + i);
									}
								}
							}
						});
					}

				}
			}
			//在首页
			if (flag_index) {
				// 画首页
				drawindex();
				// 画返回键
				drawBack();
			//在画很多apk的界面
			} else if (flag_game || flag_leisure || flag_shoot || flag_app) {
				drawGameApp();
				// 画游戏控制按钮
				drawgameBtn(-2.6F);
			} else if (flag_manage) {
				// 画下载管理界面
				drawmanage();
				if (flag_downloading) {
					drawgameBtn(-2.1F);
				} else if (flag_downloaded) {
					// 画下载管理控制按钮
					drawmanageBtn();
				}
			} else if (flag_account) {
				drawLoadingIcon();
				// 画返回键
				// drawdownloadAPK(new APKEntity());
				drawBack();
			} else if (flag_down && loadingimg == 0) {
				drawdownloadAPK(showapk);
				drawBack();
			} else if (flag_down && loadingimg == 1) {
				drawLoadingIcon();
				drawBack();
			}
			//没有连接wifi
		} else {
			if (flag_index) {
				// 画首页
				drawindex();
				// 画返回键
				drawBack();
			} else if (flag_game) {
				// 画抱歉未连接这几个字
				drawText(UnConnectedinfoTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
				// 画游戏控制按钮
				drawgameBtn(-2.6F);
			} else if (flag_manage) {
				// 画下载管理界面
				drawmanage();
				if (flag_downloading) {
					drawText(NoDownloadingtaskTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
					drawgameBtn(-2.1F);
				} else if (flag_downloaded) {
					// 画下载管理控制按钮
					drawText(NoDownloadedTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
					drawmanageBtn();
				}

			} else if (flag_account) {
				drawLoadingIcon();
				// 画返回键
				// drawdownloadAPK(new APKEntity());
				drawBack();
			} else if (flag_app) {
				// 画抱歉未连接这几个字
				drawText(UnConnectedinfoTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
				// 画应用控制按钮
				drawgameBtn(-2.6F);
			}
		}

	}

	// TODO
	// 画游戏控制按钮
	private void drawgameBtn(float y) {
		// 画下方的地板
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, y, iconDistance);
		MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		threeBtnBg.drawSelf(0.4F);
		MatrixState.popMatrix();
		// draw control buttons
		// 画返回、上一页、下一页 的这些图标
		for (int i = 0; i < 3; ++i) {
			MatrixState.pushMatrix();
			MatrixState.translate((localBtnSize + localBtnMargin) * ((float) i - 1.0F), y + 0.3F, iconDistance);
			// 是否移动到了所有返回、上一页、下一页 的这些图标
			if (isLookingAtObject(localBtnSize, localBtnSize, iconDistance)) {
				// 表示被选中了
				focusBtnFlags[i] = true;
				if (i == 0) {
					// 表示返回键被选中
					isBackFocused = true;
				}
				iconBackTextureRect.drawSelf(gameBtnFocusedIcons[i]);
			} else {
				// 没有选中就把状态改过来
				focusBtnFlags[i] = false;
				if (i == 0) {
					// 表示返回键没有被选中
					isBackFocused = false;
				}
				iconBackTextureRect.drawSelf(gameBtnDefaultIcons[i]);
			}
			MatrixState.popMatrix();
		}
	}

	// 画位于图片中间的文字
	private void drawText(int id, float xtran, float ytran, float ztran) {
		MatrixState.pushMatrix();
		MatrixState.translate(xtran, ytran, ztran);
		// MatrixState.translate(0.0F, 0.0F, iconDistance + 0.002F);
		UnConnectedinfoRect.drawSelf(id);
		MatrixState.popMatrix();
	}

	// TODO
	// 画下载管理控制按钮
	private void drawmanageBtn() {
		// 画下方的地板
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -2.1F, iconDistance);
		MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		fourBtnBg.drawSelf(0.4F);
		MatrixState.popMatrix();
		// draw control buttons
		// 画返回、上一页、下一页、删除 的这些图标
		for (int i = 0; i < 4; ++i) {
			MatrixState.pushMatrix();
			MatrixState.translate((localBtnSize + localBtnMargin) * ((float) i - 1.5F), -1.8F, iconDistance);
			// 是否移动到了所有返回、上一页、下一页、删除 的这些图标
			if (isLookingAtObject(localBtnSize, localBtnSize, iconDistance)) {
				// 表示被选中了
				focusBtnFlags[i] = true;
				if (i == 0) {
					// 表示返回键被选中
					isBackFocused = true;
				}
				if (i == 3) {
					// 删除按键被点击了
					if (isDeleteClick) {
						// 画红色的垃圾箱
						iconBackTextureRect.drawSelf(deleteRedTextureId);
					} else {
						// 没有被电击就画系统默认被选中状态focused的垃圾箱
						iconBackTextureRect.drawSelf(manageBtnFocusedIcons[i]);
					}
				} else {
					iconBackTextureRect.drawSelf(manageBtnFocusedIcons[i]);
				}
			} else {
				// 没有选中就把状态改过来
				focusBtnFlags[i] = false;
				if (i == 0) {
					// 表示返回键没有被选中
					isBackFocused = false;
				}

				if (i == 3) {
					// 删除键已经被点了
					if (isDeleteClick) {
						// 被点了就画红色的图标
						iconBackTextureRect.drawSelf(deleteRedTextureId);
					} else {
						// 没有被点就画默认的defalut图标
						iconBackTextureRect.drawSelf(manageBtnDefaultIcons[i]);
					}
				} else {
					iconBackTextureRect.drawSelf(manageBtnDefaultIcons[i]);
				}
			}

			MatrixState.popMatrix();
		}
	}

	// 画返回键
	private void drawBack() {
		// draw background
		// 画地板
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -1.9F, iconDistance);
		MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		backBtnBg.drawSelf(0.4F);
		MatrixState.popMatrix();
		// draw back button
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -1.8F, iconDistance);
		// 是否移动到了返回键图标上面
		if (isLookingAtObject(backIconSize, backIconSize, iconDistance)) {
			backTextureId = backFocusedTextureId;
			isBackFocused = true;
		} else {
			backTextureId = backDefaultTextureId;
			isBackFocused = false;
		}
		iconBackTextureRect.drawSelf(backTextureId);
		MatrixState.popMatrix();
	}

	// 画首页
	private void drawindex() {
		for (int i = 0; i < imagelists.size(); i++) {
			float scale = 0.1F;
			// 画左上角的大图片
			MatrixState.pushMatrix();
			MatrixState.translate(imagelists.get(i).getXtrans(), imagelists.get(i).getYtrans(),
					imagelists.get(i).getZtrans());
			MatrixState.rotate(imagelists.get(i).getRotate(), 0.0F, 1.0F, 0.0F);
			// 是否移动到了左上角的大图片上
			if (isLookingAtObject(imagelists.get(i).getXsize(), imagelists.get(i).getYsize(),
					imagelists.get(i).getZtrans())) {
				if (outAnimPosition != i) {
					outAnimPosition = i;
					scale1 = 0.0F;
					startTime1 = System.currentTimeMillis();
				} else {
					if (scale1 < scale) {
						scale1 = scale * ((System.currentTimeMillis() - startTime1) / scaleAnimTime);
					} else {
						scale1 = scale;
					}
					// 放大的动画
					MatrixState.translate(0.0F, 0.0F, scale1);
				}
				// 表示被看到的状态
				currentPosition = i;
			} else if (outAnimPosition == i) {
				outAnimPosition = -1;
				inAnimPosition = i;
				scale2 = 0.0F;
				startTime2 = System.currentTimeMillis();
				MatrixState.translate(0.0F, 0.0F, scale1);
			} else if (inAnimPosition == i) {
				if (scale2 < scale) {
					scale2 = scale * ((System.currentTimeMillis() - startTime2) / scaleAnimTime);
				} else {
					scale2 = scale;
				}
				// 表示缩小的动画
				MatrixState.translate(0.0F, 0.0F, scale - scale2);
			}
			(imagelists.get(i).getRectTexture()).drawSelf(imagelists.get(i).getRectTextureId());
			MatrixState.popMatrix();
		}
	};

	// 画下载管理界面
	private void drawmanage() {
		drawTabBgAndTitle();
	}

	// 画大标题的文字 和文字下面的线 以及背景板
	private void drawTabBgAndTitle() {
		// 画正在下载 下载完成 蓝线 和 灰色的线
		for (int i = 0; i < 2; ++i) {
			MatrixState.pushMatrix();
			MatrixState.translate(2.2F * ((float) i - 0.5F), mRectBgYScale / 2.0F - 0.6F, iconDistance);
			if (isLookingAtObject(1.4F, 0.5F, iconDistance)) {
				// 表示光标移动到了这个栏目上
				mTabFocus[i] = true;
			} else {
				mTabFocus[i] = false;
			}

			MatrixState.pushMatrix();
			MatrixState.translate(0.0F, 0.0F, 0.05F);
			MatrixState.scale(2.0F, 0.3125F, 1.0F);
			// 画大标题 分类浏览 设备存储 文字
			mTextureRect.drawSelf(mTitleArrayId[i]);
			MatrixState.popMatrix();

			// 表示我们在哪个栏目里面
			if (mTabNo == i) {
				MatrixState.pushMatrix();
				MatrixState.translate(0.0F, -0.2F, 0.011F);
				MatrixState.scale(2.2F, 0.04F, 1.0F);
				// 画蓝色的线
				mTextureRect.drawSelf(mBlueRectId);
				MatrixState.popMatrix();
			}

			MatrixState.popMatrix();
		}

		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.81F, 0.01F + iconDistance);
		MatrixState.scale(4.4F, 0.02F, 1.0F);
		// 画长长的灰色的线
		mTextureRect.drawSelf(mGrayRectId);
		MatrixState.popMatrix();
	}

	// TODO
	// 画显示APK下载的界面
	private void drawdownloadAPK(APKEntity apkinfo) {
		DOWNAPKURL = apkinfo.getDownloadurl();
		SAVEAPKURL = DataUtils.GAMEIMG_FOLDERPATH + File.separator + apkinfo.getBelong() + apkinfo.getId() + ".apk";
		if (downlAPKID == 0) {
			if (!initdownimg) {
				initdownimg = true;
				//开启线程下载之前利用handler将loadingimg=1 显示正在下载的界面
				handler.sendMessage(handler.obtainMessage(8, 1));
				ThreadCount = 4;
				System.out.println("开始加载图片。。。。。。。。。。。。。");
				readBitMapformurl(apkinfo.getIconurl(), 0, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail1url(), 1, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail2url(), 2, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail3url(), 3, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail4url(), 4, apkinfo.getId(), apkinfo.getBelong());
				System.out.println("加载完成图片。。。。。。。。。。。。。");
			}
			Bitmap downlAPKBitmap = Utils.getdownlAPKBitmap((int) 580, (int) 380, apkinfo.getName(), apkinfo.getSize(),
					apkinfo.getBesupported(), apkinfo.getIntroduction(),
					// 黑色背景
					Utils.getAPKBGBitmap(),
					// 五个图标
					// readBitMap(mContext, R.drawable.headset),
					// readBitMap(mContext,R.drawable.ic_default),
					// readBitMap(mContext, R.drawable.ic_default),
					// readBitMap(mContext, R.drawable.ic_default),
					// readBitMap(mContext, R.drawable.ic_default), true);
					IconBitmap, Thumbnail1Bitmap, Thumbnail2Bitmap, Thumbnail3Bitmap, Thumbnail4Bitmap, true);
			System.out.println("..." + IconBitmap + Thumbnail1Bitmap + "..." + Thumbnail2Bitmap + "..."
					+ Thumbnail3Bitmap + "..." + Thumbnail4Bitmap);
			if (downlAPKBitmap != null) {
				downlAPKID = Utils.initTexture(downlAPKBitmap);
			}
		}
		// System.out.println("downlAPKID=........"+downlAPKID);
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, iconDistance - 1.5F);
		downlAPKBg.drawSelf(downlAPKID);
		MatrixState.popMatrix();
		if (APKDEL) {
			MatrixState.pushMatrix();
			MatrixState.translate(-0.05F, 0.6F, iconDistance - 1.499F);
			if (changeprogressinit > 200) {
				state = changeprogress + "%";
				if (changeprogress == 100 && downloadstate == 1) {
					state = "正在安装";
				}
				downingBtFocusedTextureId = Utils.initTexture(mContext, state, changeprogress, true);
				downingBtDefaultTextureId = Utils.initTexture(mContext, state, changeprogress, false);
				changeprogressinit = 0;
			} else {
				changeprogressinit++;
			}
			if (isLookingAtObject(2.0F, 0.5F, iconDistance - 1.499F)) {
				downlAPKBt.drawSelf(downingBtFocusedTextureId);
			} else {
				downlAPKBt.drawSelf(downingBtDefaultTextureId);
			}
			MatrixState.popMatrix();

			MatrixState.pushMatrix();
			MatrixState.translate(1.3F, 0.6F, iconDistance - 1.499F);
			if (isLookingAtObject(localBtnSize, localBtnSize, iconDistance - 1.499F)) {
				down = 0;
				iconBackTextureRect.drawSelf(manageBtnFocusedIcons[3]);
			} else {
				down = 1;
				iconBackTextureRect.drawSelf(manageBtnDefaultIcons[3]);
			}
			MatrixState.popMatrix();
		} else {
			MatrixState.pushMatrix();
			MatrixState.translate(-0.05F, 0.6F, iconDistance - 1.499F);
			if (isLookingAtObject(2.0F, 0.5F, iconDistance - 1.499F)) {
				down = 0;
				if (DataUtils.checkAPKExit(mContext, showapk.getPackagename())) {
					downprogress = 1;
					downlAPKBt.drawSelf(downOKBtFocusedTextureId);
				} else {
					downprogress = 0;
					downlAPKBt.drawSelf(downBtFocusedTextureId);
				}
			} else {
				down = 1;
				downprogress = 0;
				if (DataUtils.checkAPKExit(mContext, showapk.getPackagename())) {
					downlAPKBt.drawSelf(downOKBtDefaultTextureId);
				} else {
					downlAPKBt.drawSelf(downBtDefaultTextureId);
				}
			}
			MatrixState.popMatrix();
		}

	}

	// 画很多APK应用里面的所有程序
	// TODO
	private void drawGameApp() {
		// 得到所有的程序
		// appList = appManager.getStoreAPKs();
		if (appList != null && appList.size() != 0) {
			// 表示应用程序是否被看到 看到true 没有看到false
			isHasAppFocused = false;

			for (int i = 0; i < appList.size(); ++i) {
				// System.out.println(appList.size()+"......"+appList.get(i).getId()+"...."+appList.get(i).getName());
				// 当前在那一页 默认在第一页
				// System.out.println(getPageIndex(i)+"..."+i+"当前页。。。。。。");
				if (getPageIndex(i) >= -1 + currentPage && getPageIndex(i) <= 1 + currentPage) {
					// APP的IconId默认是-1
					// System.out.println("初始化之前的TID。。。"+"..."+i+appList.get(i).getTextureId());
					if (appList.get(i).getTextureId() == -1) {
						// Bitmap bmp = appManager.getAppBitmap(appList.get(i)
						// .getPackagename());
						// Bitmap bmp = Utils.getIconBitamp(mContext,
						// appList.get(i).getIcon());
						readBitMapformurl(appList.get(i).getIconurl(), 0, appList.get(i).getId(),
								appList.get(i).getBelong());
						Bitmap bmp = Utils.changeBitampsize(IconBitmap);
						// Bitmap bmp = IconBitmap;
						if (bmp != null) {
							// 初始化图片、背景、文字
							int id = Utils.initAppTexture(bmp, appList.get(i).getName());
							appList.get(i).setTextureId(id);
							// System.out.println("初始化之后的TID。。。"+"..."+i+appList.get(i).getTextureId());
						}
					}
					// 点击上一页、下一页的时候开启动画 true表示开启 false表示不开始
					if (isShowAnimation) {
						long passTime = System.currentTimeMillis() - startAnimationTime;
						startAnimationTime = System.currentTimeMillis();
						// animationOffset>0表示往X轴正方向移动 即去上一页
						if (animationOffset > 0.0F) {
							if (animatedOffset < animationEndOffset) {
								animatedOffset += (float) passTime / animTime * animationOffset;
							} else {
								// 表示动画完了
								animatedOffset = animationEndOffset;
								isShowAnimation = false;
								currentPage += -1;
							}
						}
						// animationOffset<0表示往X轴负方向移动 即去上一页
						if (animationOffset < 0.0F) {
							if (animatedOffset > animationEndOffset) {
								animatedOffset += (float) passTime / animTime * animationOffset;
							} else {
								// 表示动画完了
								animatedOffset = animationEndOffset;
								isShowAnimation = false;
								++currentPage;
							}
						}
					}
					// 5-8就再往左多平移一点
					float x = appIconWidthWithMargin * ((float) (i % 4) - 1.5F)
							+ (float) (i / 8) * 4.0F * appIconWidthWithMargin + animatedOffset;
					// 5-8就再往下多平移一点
					float y = -(appIconBgHeight + appIconVerticalMargin) * (float) (i % 8 / 4)
							+ (appIconBgHeight + appIconVerticalMargin) / 2.0F - 0.55F;
					float alpha = 1.0F;
					// 画上下页的虚影 透明度0.3实现
					if (getPageIndex(i) != currentPage) {
						alpha = 0.3F;
					}

					MatrixState.pushMatrix();
					MatrixState.translate(x, y, iconDistance - 0.5F);
					// 是否移动到了本地APP里面的图标上面
					if (isLookingAtObject(0.9F, 1.05F, iconDistance - 0.501F)) {
						if (outAnimPosition != i) {
							outAnimPosition = i;
							scale1 = 0.0F;
							startTime1 = System.currentTimeMillis();
						} else {
							if (scale1 < 0.1F) {
								scale1 = 0.1F * ((float) (System.currentTimeMillis() - startTime1) / scaleAnimTime);
							} else {
								scale1 = 0.1F;
							}

							if (getPageIndex(i) == currentPage) {
								// 往Z轴正方向移动间接放大
								MatrixState.translate(0.0F, 0.0F, scale1);
								// 放大
								MatrixState.scale(1.0F + scale1, 1.0F + scale1, 1.0F);
							}
						}
						// 看着APP的状态
						isHasAppFocused = true;
						currentPosition = i;
					} else if (outAnimPosition == i) {
						outAnimPosition = -1;
						inAnimPosition = i;
						scale2 = 0.0F;
						startTime2 = System.currentTimeMillis();
						if (getPageIndex(i) == currentPage) {
							MatrixState.translate(0.0F, 0.0F, scale1);
							MatrixState.scale(1.0F + scale1, 1.0F + scale1, 1.0F);
						}
						// 这表示缩小的动画
					} else if (inAnimPosition == i) {
						if (scale2 < 0.1F) {
							scale2 = 0.1F * ((float) (System.currentTimeMillis() - startTime2) / scaleAnimTime);
						} else {
							scale2 = 0.1F;
						}

						if (getPageIndex(i) == currentPage) {
							MatrixState.translate(0.0F, 0.0F, 0.1F - scale2);
							MatrixState.scale(1.1F - scale2, 1.1F - scale2, 1.0F);
						}
					}
					// 画所有的APP图标以及其背景
					// draw app icon
					MatrixState.pushMatrix();
					MatrixState.translate(0.0F, 0.0F, 0.0F);
					appTextureRect.drawSelf(appList.get(i).getTextureId(), alpha);
					MatrixState.popMatrix();
					MatrixState.popMatrix();
				}
			}
			currentLookPosition = -1;
			// 算出光标在三页中的哪一页
			for (int i = 0; i < 3; ++i) {
				MatrixState.pushMatrix();
				MatrixState.translate(5.0F * (float) (i - 1), -0.5F, iconDistance - 0.5F);
				// 是否移动到了页面的左右两边的虚影上面 （点击虚影可以实现上下页之间的跳转）
				if (isLookingAtObject(3.0F, 2.2F, iconDistance - 0.5F)) {
					currentLookPosition = i;
				}
				MatrixState.popMatrix();
			}

			if (!isHasAppFocused) {
				currentPosition = -1;
			}
			int totalPage = getTotalPageNum();

			// draw page dot
			// 画表示页数的小点
			for (int i = 0; i < totalPage; ++i) {
				MatrixState.pushMatrix();
				// 以中间的小圆点为坐标0
				float x = dotIconWidthWithMargin * (float) (i - totalPage / 2);
				if (totalPage % 2 == 0) {
					x = dotIconWidthWithMargin * ((float) i - (float) (totalPage - 1) / 2.0F);
				}
				MatrixState.translate(x, -2.1F, iconDistance - 0.5F);
				// 如果是当前页 那么就放大2*2倍
				if (i + 1 == currentPage) {
					MatrixState.scale(2.0F, 2.0F, 1.0F);
				}
				dotTextureRect.drawSelf(dotTextureId, 1.0F);
				MatrixState.popMatrix();
			}

		}
	}

	// 画加载时候出现的加载圈圈
	private void drawLoadingIcon() {
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, 0.0F, 0.21F + iconDistance);
		MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
		// 加载的时候看到的圈圈的一部分（白色）
		mTextureRect.drawSelf(mLoadingOnId);
		MatrixState.popMatrix();
		mLoadingAngle += 3.0F;
	}

	// 算出总的页数
	private int getTotalPageNum() {
		// appList = appManager.getStoreAPKs();
		if (appList != null) {
			int size = appList.size();
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

	// 0-7返回1 8-15返回2 16-23返回3 24-31返回4 表示第几页
	private int getPageIndex(int index) {
		return 1 + index / 8;
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static void readBitMapformurl(final String imgurl, final int sendint, final int id, final String belong) {
		File imgfolder = new File(DataUtils.GAMEIMG_FOLDERPATH);
		if (!imgfolder.exists()) {
			imgfolder.mkdirs();
		}
		final File imgfile = new File(DataUtils.GAMEIMG_FOLDERPATH + File.separator + belong + id + sendint + ".png");
		if (!(imgfile.exists() && !againdownimg)) {
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					BitmapFactory.Options opt = new BitmapFactory.Options();
					//opt.inPreferredConfig = Bitmap.Config.RGB_565;
					opt.inPurgeable = true;
					opt.inInputShareable = true;
					InputStream is = null;
					URL url = null;
					try {
						if(!imgfile.exists()){
							imgfile.createNewFile();
						}
						url = new URL(imgurl);
						HttpURLConnection huc = (HttpURLConnection) url.openConnection();
						huc.setRequestMethod("GET");
						huc.setConnectTimeout(10000);
						int responseCode = huc.getResponseCode();
						System.out.println("下载图片的" + "线程的请求码是。。。。。。。。" + responseCode);
						if (responseCode == 200) {
							is = huc.getInputStream();
							RandomAccessFile raf = new RandomAccessFile(imgfile, "rws");
							byte[] buffer = new byte[1024];
							int temp = 0;
							while ((temp = is.read(buffer)) != -1) {
								raf.write(buffer, 0, temp);
								System.out.println("下载进度。。。。。。" + temp);
							}
							raf.close();
							is.close();
							Bitmap imgbitmap = BitmapFactory.decodeFile(
									DataUtils.GAMEIMG_FOLDERPATH + File.separator + belong + id + sendint + ".png",
									opt);
							if (imgbitmap == null) {
								System.out.println("解析图片出错。。。。。");
								//解析出错再次下载
								againdownimg = true;
							} else {
								againdownimg = false;
							}
							Message message = handler.obtainMessage();
							message.what = sendint;
							message.obj = imgbitmap;
							handler.sendMessage(message);
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} else {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			Bitmap imgbitmap = BitmapFactory
					.decodeFile(DataUtils.GAMEIMG_FOLDERPATH + File.separator + belong + id + sendint + ".png", opt);
			// System.out.println("解析的图片路径是。。。。"+DataUtils.GAMEIMG_FOLDERPATH +
			// File.separator + belong + id + sendint + ".png");
			// System.out.println("解析到的图片是。。。。"+imgbitmap);
			if (imgbitmap == null) {
				System.out.println("解析图片出错。。。。。");
				againdownimg = true;
			} else {
				againdownimg = false;
			}
			Message message = handler.obtainMessage();
			message.what = sendint;
			message.obj = imgbitmap;
			handler.sendMessage(message);
		}

	}

	// 画眼睛小球
	// radius：弧度
	private boolean isLookingAtObject(float width, float height, float radius) {
		float[] initVec = new float[] { 0.0F, 0.0F, 0.0F, 1.0F };
		float[] objPosVec = new float[4];
		double yaw1 = Math.atan2((width / 2.0F), Math.abs(radius));
		double pitch1 = Math.atan2((height / 2.0F), Math.abs(radius));

		Matrix.multiplyMM(modelView, 0, headView, 0, MatrixState.getMMatrix(), 0);
		Matrix.multiplyMV(objPosVec, 0, modelView, 0, initVec, 0);
		float yaw = (float) Math.atan2(objPosVec[0], (-objPosVec[2]));
		float pitch = (float) Math.atan2(objPosVec[1], (-objPosVec[2]));
		return Math.abs(yaw) < Math.abs(yaw1) && Math.abs(pitch) < Math.abs(pitch1);
	}

	// TODO
	public void onConfirm() {
		// 判断光标是否在返回键上面
		if (isBackFocused) {
			// 删除按钮没有被点击了
			if (!isDeleteClick) {
				if (flag_game || flag_app || flag_account || flag_manage || flag_leisure || flag_shoot) {
					// 重置状态
					resetStatus();
					// 回到首页
					flag_index = true;
					// 返回按键不被选中
					isBackFocused = false;
					// 设置当前页
					currentPage = 1;
					return;
				} else if (flag_down) {
					//根据downfrom的值判断是通过哪个界面进入的下载界面 再返回到不同的地方
					for (int i = 0; i < downfrom.length; i++) {
						if (downfrom[i] != 0) {
							switch (i) {
							case 0:
								resetStatus();
								flag_game = true;
								isBackFocused = false;
								currentPage = 1;
								break;
							case 1:
								resetStatus();
								flag_index = true;
								isBackFocused = false;
								currentPage = 1;
								break;
							case 2:

								break;
							case 3:

								break;
							case 4:

								break;
							case 5:

								break;

							default:
								break;
							}
						}
					}
					//在首页直接退出
				} else if (flag_index) {
					((MainActivity) mContext).finish();
				}
			} else {
				// 删除按键选中的话，将其改成不被选中状态
				isDeleteClick = !isDeleteClick;
			}
		} else {
			// 在首页
			if (flag_index) {
				// 在首页里面currentPosition表示8个应用的当前位置
				switch (currentPosition) {
				case 0:
					resetStatus();
					//表示要展示那些APK集合
					appList = GAMEList;
					flag_game = true;
					
					return;
				case 1:
					resetStatus();
					//这里没有应用的APK 先用游戏的APK代替
					appList = GAMEList;
					flag_app = true;
					
					return;
				case 2:
					//没有联网
					if (!isContectWIFI()) {
						showText(mContext.getResources().getString(R.string.unconnected));
					} else {
						//联网了 这里也是先自己定义一个 后面根据需求换
						showapk = new APKEntity("com.orange.juhelive", "GAME", 11, "特种部队", "31.1M", "支持外设:VR眼镜",
								"简介:《Western VR》是安卓平台的一款虚拟现实FPS游戏,它让你在虚拟现实世界里变身一名西部牛仔,"
										+ "举起你的双枪和前来挑战的坏人们一决高下!牛仔,上吧!此游戏可使用小G操控!",
								// "http://qnfile.orangelive.tv/JuheLive%20.apk",
								"http://qnfile.orangelive.tv/TXMV%28com.txmv%29.apk",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png");
						resetStatus();
						flag_down = true;
						//表示是从首页点进来的
						downfrom[1] = 1;
					}
					return;
				case 3:
					if (!isContectWIFI()) {
						showText(mContext.getResources().getString(R.string.unconnected));
					} else {
						resetStatus();
						//这里没有休闲娱乐的APK 先用游戏的APK代替
						appList = GAMEList;
						flag_leisure = true;
					}
					return;
				case 4:
					if (!isContectWIFI()) {
						showText(mContext.getResources().getString(R.string.unconnected));
					} else {
						resetStatus();
						//这里没有射击的APK 先用游戏的APK代替
						appList = GAMEList;
						flag_shoot = true;
						/*
						 * appList.clear(); APKEntity storeapk4 = new
						 * APKEntity("特种部队", R.drawable.headset,"31.1M",
						 * "支持外设:VR眼镜",
						 * "简介:《Western VR》是安卓平台的一款虚拟现实FPS游戏,它让你在虚拟现实世界里变身一名西部牛仔,"
						 * + "举起你的双枪和前来挑战的坏人们一决高下!牛仔,上吧!此游戏可使用小G操控!",
						 * "http://baidu.com", R.drawable.ic_default,
						 * R.drawable.ic_default, R.drawable.ic_default,
						 * R.drawable.ic_default, "com.kamino.store"); if
						 * (appList.size() == 0) { for (int i = 0; i < 2; i++) {
						 * appList.add(storeapk4); } }
						 */

					}
					return;
				case 5:
					if (!isContectWIFI()) {
						showText(mContext.getResources().getString(R.string.unconnected));
					} else {
						showapk = new APKEntity("com.orange.juhelive", "GAME", 12, "特种部队", "31.1M", "支持外设:VR眼镜",
								"简介:《Western VR》是安卓平台的一款虚拟现实FPS游戏,它让你在虚拟现实世界里变身一名西部牛仔,"
										+ "举起你的双枪和前来挑战的坏人们一决高下!牛仔,上吧!此游戏可使用小G操控!",
								"http://qnfile.orangelive.tv/JuheLive%20.apk",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png");
						resetStatus();
						flag_down = true;
						downfrom[1] = 1;
					}
					return;
				case 6:
					resetStatus();
					flag_account = true;
					return;
				case 7:
					resetStatus();
					flag_manage = true;
					//默认进去画正在下载的界面
					flag_downloading = true;
					return;
				default:
					return;
				}
				//管理界面
			} else if (flag_manage) {
				// 光标在第三+1个图标也就是垃圾箱上面
				if (focusBtnFlags[3]) {
					isDeleteClick = !isDeleteClick;
					return;
				}
				// 光标在第一+1个图标也就是上一页的图标上面
				if (focusBtnFlags[1]) {
					// goPreviousPage();
					return;
				}
				// 光标在第二+1个图标也就是下一页的图标上面
				if (focusBtnFlags[2]) {
					// goNextPage();
					return;
				}
				//正在下载被选中
				if (mTabFocus[0]) {
					resetStatus();
					//表示在正在下载下面画蓝色的线
					mTabNo = 0;
					flag_manage = true;
					flag_downloading = true;
				//下载完成被选中
				} else if (mTabFocus[1]) {
					resetStatus();
					//表示在下载完成下面画蓝色的线
					mTabNo = 1;
					flag_manage = true;
					flag_downloaded = true;
				}
			} else if (flag_game || flag_leisure || flag_shoot || flag_app) {
				// 光标在第一+1个图标也就是上一页的图标上面
				if (focusBtnFlags[1]) {
					goPreviousPage();
					return;
				}
				// 光标在第二+1个图标也就是下一页的图标上面
				if (focusBtnFlags[2]) {
					goNextPage();
					return;
				}

				if (currentLookPosition == 2) {
					if (1 + currentPage <= getTotalPageNum()) {
						isShowAnimation = true;
						startAnimationTime = System.currentTimeMillis();
						animationOffset = 4.0F * -appIconWidthWithMargin;
						animationEndOffset = animatedOffset + animationOffset;
						return;
					}
					// 光标在上一页虚影
				} else if (currentLookPosition == 0) {
					if (currentPage != 1) {
						startAnimationTime = System.currentTimeMillis();
						isShowAnimation = true;
						animationOffset = 4.0F * appIconWidthWithMargin;
						animationEndOffset = animatedOffset + animationOffset;
						return;
					}
					// 光标在本页中 APP被选中了 去下载界面
				} else if (currentPosition != -1 && appList.size() > 0 && getPageIndex(currentPosition) == currentPage) {
					resetStatus();
					downfrom[0] = currentPage;
					showapk = appList.get(currentPosition);
					// System.out.println(showapk.getName()+"..............");
					// System.out.println(currentPosition+"....................");
					flag_down = true;
					return;
				}
				//在下载界面
			} else if (flag_down) {
				// 看到的是下载
				if (!APKDEL && down == 0 && downprogress == 0) {
					APKDEL = true;
					downAPK(DOWNAPKURL, SAVEAPKURL);
					// 看到了垃圾箱
				} else if (APKDEL && down == 0) {
					APKDEL = false;

					// 看到的是打开
				} else if (!APKDEL && down == 0 && downprogress == 1) {
					System.out.println("打开" + showapk.getPackagename() + "app.........");
					//开启指定的程序
					DataUtils.openAPK(mContext, showapk.getPackagename());
				}
			}
		}

	}
	//放回是否在画很多APK的界面
	public boolean isDrawGame() {
		return flag_game || flag_leisure || flag_shoot || flag_app;
	}

	// TODO
	public void onBackPressed() {
		//在首页返回键直接退出
		if (flag_index) {
			((MainActivity) mContext).finish();
		//在游戏 应用 账号 休闲娱乐 射击等等时返回到首页			
		} else if (flag_game || flag_app || flag_account || flag_leisure || flag_shoot) {
			// 重置状态
			resetStatus();
			// 切换到主页
			flag_index = true;
			// 让返回键不被选中
			isBackFocused = false;
			currentPage = 1;
			return;
			//在管理界面
		} else if (flag_manage) {
			//没有点击垃圾箱 回到主页
			if (!isDeleteClick) {
				// 重置状态
				resetStatus();
				// 切换到主页
				flag_index = true;
				// 让返回键不被选中
				isBackFocused = false;
				currentPage = 1;
				return;
				//点击垃圾箱 改成没有点击的状态
			} else {
				// 已经点击了就把他改成没有点击的状态
				isDeleteClick = !isDeleteClick;
			}
			//在下载页面
		} else if (flag_down) {
			//根据downfrom的值判断是通过哪个界面进入的下载界面 再返回到不同的地方
			for (int i = 0; i < downfrom.length; i++) {
				if (downfrom[i] != 0) {
					switch (i) {
					case 0:
						currentPage = 1;
						resetStatus();
						flag_game = true;
						isBackFocused = false;
						break;
					case 1:
						resetStatus();
						flag_index = true;
						isBackFocused = false;
						currentPage = 1;
						break;
					case 2:

						break;
					case 3:

						break;
					case 4:

						break;
					case 5:

						break;

					default:
						break;
					}
				}
			}
		}
	}
	//去下一页    开启动画
	public void goNextPage() {
		if ((flag_game || flag_leisure || flag_shoot || flag_app) && !isShowAnimation
				&& currentPage < getTotalPageNum()) {
			startAnimationTime = System.currentTimeMillis();
			isShowAnimation = true;
			// 往X轴负方向平移
			animationOffset = 4.0F * -appIconWidthWithMargin;
			animationEndOffset = animatedOffset + animationOffset;
		}
	}

	//去上一页    开启动画
	public void goPreviousPage() {
		if ((flag_game || flag_leisure || flag_shoot || flag_app) && !isShowAnimation && currentPage > 1) {
			startAnimationTime = System.currentTimeMillis();
			isShowAnimation = true;
			// 往X轴正方向平移
			animationOffset = 4.0F * appIconWidthWithMargin;
			animationEndOffset = animatedOffset + animationOffset;
		}
	}

	public void downAPK(final String apkurl, final String savepath) {
		//线程池执行耗时操作  这里是要得到下载APK的总字节数
		fixedThreadPool.execute(new Runnable() {
			public void run() {
				InputStream is = null;
				URL url;
				try {
					url = new URL(apkurl);
					HttpURLConnection httpurlc = (HttpURLConnection) url.openConnection();
					httpurlc.setConnectTimeout(10000);
					httpurlc.setRequestMethod("GET");
					int responseCode = httpurlc.getResponseCode();
					System.out.println("TEST" + "线程的请求码是。。。。。。。。" + responseCode);
					if (responseCode == 200) {
						is = httpurlc.getInputStream();
						//将要下载的apk的总的字节数赋值给APKlength
						APKlength = httpurlc.getContentLength();
						System.out.println("TEST" + "下载文件的总的字节数是。。。。。。。。" + APKlength);
						int threadblock = APKlength / 3;
						//将下载apk的进度重置为0
						handler.sendEmptyMessage(6);
						//开三个线程下载APK
						for (int i = 0; i < 3; i++) {
							int startindex = i * threadblock;
							int endindex = (i + 1) * threadblock - 1;
							if (i == 2) {
								endindex = APKlength;
							}
							System.out.println("test+" + startindex + "......" + endindex);
							Message message = handler.obtainMessage(5, startindex, endindex, url);
							Bundle bundle = new Bundle();
							bundle.putString("url", apkurl);
							bundle.putString("savepath", savepath);
							message.setData(bundle);
							handler.sendMessage(message);
						}

					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	//利用AsyncTask和RandomAccessFile实现开多个线程下载的任务
	public static class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
		private Handler handler;
		private int start;
		private int end;
		private String savepath;

		public MyAsyncTask(Handler handler, int start, int end, String savepath) {
			this.handler = handler;
			this.start = start;
			this.end = end;
			this.savepath = savepath;
		}

		@Override//子线程执行下载任务
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			InputStream is = null;
			try {
				URL url = new URL(params[0]);
				HttpURLConnection httpurlc = (HttpURLConnection) url.openConnection();
				httpurlc.setConnectTimeout(10000);
				httpurlc.setRequestMethod("GET");
				//定义下载的范围
				httpurlc.setRequestProperty("Range", "bytes=" + start + "-" + end);
				// System.out.println("TEST" + "下载文件的总的字节数是。。。。。。。。" + length);
				int responseCode = httpurlc.getResponseCode();
				System.out.println("TEST" + "线程的请求码是。。。。。。。。" + responseCode);
				if (responseCode == 206) {
					is = httpurlc.getInputStream();
					System.out.println("TEST线程ID" + Thread.currentThread().getId() + "......" + start + "-" + end);
					//rws 可读可写可执行
					RandomAccessFile raf = new RandomAccessFile(savepath, "rws");
					//跳过start字节再开始执行保存文件的任务
					raf.seek(start);
					byte[] buffer = new byte[1024];
					int temp = 0;
					int progress = start;
					while ((temp = is.read(buffer)) != -1) {
						raf.write(buffer, 0, temp);
						progress += temp;
						//加锁 保证+temp一次只执行一次 避免finalprogress值变小
						synchronized (this) {
							finalprogress += temp;
							//调用onProgressUpdate方法更新进度
							publishProgress(finalprogress);
						}

					}
					raf.close();
					is.close();
					// System.out.println("TEST" + params[0] + "...." +
					// params[1] + "...." + params[2]);
					return true;
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			return null;
		}

		@Override//下载更新进度
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			//发送handler消息更新进度
			Message message = handler.obtainMessage();
			message.what = 7;
			Bundle bundle = new Bundle();
			bundle.putString("savepath", savepath);
			bundle.putInt("progress", values[0]);
			message.setData(bundle);
			handler.sendMessage(message);
		}

		@Override//下载出错
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!result) {
				showText("下载出错");
				changeprogress = 0;
				changeprogressinit = 0;
				finalprogress = 0;
			}
			System.out.println("TEST" + result + "...........");
		}
	}

	class InstallPackageReceiver extends BroadcastReceiver {

		public final void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!TextUtils.equals(action, "android.intent.action.PACKAGE_ADDED")
					&& !TextUtils.equals(action, "android.intent.action.PACKAGE_REPLACED")) {
				if (TextUtils.equals(action, "com.kamino.action.INSTALL_FAILED")) {
					System.out.println("安装失败。。。。。。。。。。。。收到广播");
				}
			} else {
				// downloadstate = 2;
				APKDEL = false;
				System.out.println("安装成功。。。。。。。。。。。。收到广播");
			}

		}
	}

	// TODO
	public class MyStereoRenderer implements StereoRenderer {
		private static final float Z_FAR = 100.0F;
		private static final float Z_NEAR = 1.0F;
		long currentTime2 = 0L;
		long lastDrawtime = 0L;
		long lastPrintTime = 0L;

		public void onRendererShutdown() {
		}

		public void onSurfaceChanged(int width, int height) {
			float ratio = (float) width / (float) height;
			//定义project矩阵
			MatrixState.setProjectFrustum(-ratio, ratio, -1.0F, 1.0F, Z_NEAR, Z_FAR);
		}

		public void onSurfaceCreated(EGLConfig gl) {
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			// 定义各个图标的大小、资源
			initWorld();
		}

		@Override
		public void onDrawEye(Eye eye) {
			//执行画图逻辑
			draw(eye);
		}

		@Override
		public void onFinishFrame(Viewport port) {
		}

		@Override
		public void onNewFrame(HeadTransform head) {
			if (lastDrawtime == 0L) {
				lastDrawtime = System.currentTimeMillis();
			} else {
				currentTime2 = System.currentTimeMillis();
				if (System.currentTimeMillis() - lastPrintTime > 1000L) {
					// Log.d("CurrentFrame", 1000L / (System.currentTimeMillis()
					// - lastDrawtime) + " FPS");
					lastPrintTime = currentTime2;
				}
				lastDrawtime = currentTime2;
			}

			GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			head.getHeadView(headView, 0);
			if (mNeedUpdate) {
				updateLanguage();
				updateTheme(false);
				mNeedUpdate = false;
			}

		}

	}

	//程序加载的时候注册广播
	public void onResume() {
		super.onResume();
		mNeedUpdate = true;
		mSensorMode = true;

		// 动态注册广播 监听安装包更新和新增安装包
		if (installOkReceiver == null) {
			installOkReceiver = new InstallPackageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.PACKAGE_REPLACED");
			filter.addAction("android.intent.action.PACKAGE_ADDED");
			filter.addDataScheme("package");
			mContext.registerReceiver(installOkReceiver, filter);
		}
	}

	//程序退出的时候还原数据、注销广播
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		parsexml = 0;
		changeprogress = 0;
		changeprogressinit = 0;
		finalprogress = 0;
		loadingimg = 0;
		IconBitmap = null;
		Thumbnail1Bitmap = null;
		Thumbnail2Bitmap = null;
		Thumbnail3Bitmap = null;
		Thumbnail4Bitmap = null;
		againdownimg = false;
		APKDEL = false;
		System.out.println("退出的时候调用了onpause。。。。。。。。。。。。");
		if (installOkReceiver != null) {
			mContext.unregisterReceiver(installOkReceiver);
			installOkReceiver = null;
		}

	}
	//关联StereoRenderer
	public void init(Context context) {
		System.out.println("init方法被执行。。。。。。。。。");
		mContext = context;
		camera = new float[16];
		headView = new float[16];
		modelView = new float[16];
		cameraView = new float[16];
		mXRotationMatrix = new float[16];
		mYRotationMatrix = new float[16];
		Matrix.setRotateM(mXRotationMatrix, 0, 0, 1.0f, 0, 0);
		Matrix.setRotateM(mYRotationMatrix, 0, 0, 0, 1.0f, 0);
		setEGLContextClientVersion(2);
		setEGLConfigChooser(new EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 gl, EGLDisplay display) {
				int[] params = new int[] { EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, EGL10.EGL_RENDERABLE_TYPE, 4,
						EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8, EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_DEPTH_SIZE, 8,
						EGL10.EGL_SAMPLE_BUFFERS, 1, EGL10.EGL_SAMPLES, 4, EGL10.EGL_STENCIL_SIZE, 0, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				gl.eglChooseConfig(display, params, configs, 1, new int[1]);
				return configs[0];
			}
		});

		mRenderer = new MyStereoRenderer();
		setRenderer(mRenderer);
		setRenderMode(RENDERMODE_CONTINUOUSLY);
	}
	//更换语言
	private void updateLanguage() {
		String locale = mContext.getResources().getConfiguration().locale.getLanguage();
		String lang;
		if (locale.startsWith("zh")) {
			lang = "zh";
		} else if (locale.startsWith("ko")) {
			lang = "ko";
		} else {
			lang = "en";
		}
		if (!TextUtils.equals(lang, mCurrentLanguage)) {
			mCurrentLanguage = lang;
			initTextTexture();
		}
	}
	//更换背景主题
	private void updateTheme(boolean reload) {
		try {
			Context context = mContext.createPackageContext("com.kamino.settings", 2);
			if (context != null) {
				String theme = context.getSharedPreferences("Theme", 5).getString("Theme_id", "sence_light1");
				if (!TextUtils.equals(mCurrentTheme, theme) || reload) {
					mCurrentTheme = theme;
					if (senceLight > 0) {
						GLES20.glDeleteTextures(1, new int[] { senceLight }, 0);
					}
					// 第一个参数为ID名，第二个为资源属性是ID或者是Drawable，第三个为包名。
					// 如果找到了，返回资源Id，如果找不到，返回0 。
					int themeId = context.getResources().getIdentifier(theme, "drawable", context.getPackageName());
					senceLight = Utils.initTexture(context, themeId);
				}
			}
		} catch (Exception ex) {
			Log.i("MainSurfaceView", "背景图片com.kamino.settings包名找不到");
			// senceLight = Utils.initTexture(getBackground(mContext,
			// R.drawable.sence_light1));
			senceLight = Utils.initTexture(mContext, R.drawable.sence_light1);
		}
	}

	// enable or disable sensor mode
	// default: enable
	public void enableSensorMode(boolean enable) {
		mSensorMode = enable;
	}

	public boolean isSensorMode() {
		return mSensorMode;
	}
	//判断wifi是否连接
	public boolean isContectWIFI() {
		ConnectivityManager connectService = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = connectService.getActiveNetworkInfo();
		if (netinfo != null && netinfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	//避免Toast点击多次之后出现的显示多次问题
	public static void showText(String text) {
		if (mtoast == null) {
			mtoast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		} else {
			mtoast.setText(text);
		}
		mtoast.show();
	}

}
