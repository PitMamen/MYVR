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
	// ��ʾ����Ƿ��ƶ����˴��������
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
	//�����̳߳�����Ϊ3��
	private final static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
	//����APK�����APK��icon
	private static Bitmap IconBitmap;
	//����APK�����APK�ĳ�������ͼ1
	private static Bitmap Thumbnail1Bitmap;
	//����APK�����APK�ĳ�������ͼ2
	private static Bitmap Thumbnail2Bitmap;
	//����APK�����APK�ĳ�������ͼ3
	private static Bitmap Thumbnail3Bitmap;
	//����APK�����APK�ĳ�������ͼ4
	private static Bitmap Thumbnail4Bitmap;
	private boolean initdownimg;
	private static int finalprogress = 0;
	//���ص�apk���ܵ��ֽ���
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
	//loadingimg=0 ��ʾ������� loadingimg=1��ʾ�������� ��ʱ��ʾ���ص�ȦȦ ��ʱһ������ ����ֵ�Ĳ�ͬ��ʾ��ͬ�Ľ���
	private static int loadingimg = 0;
	//��ʾ����4������ͼ�Ĵ����߳�����
	private static int ThreadCount = 4;
	//����������apk��ͼƬ����handler�������̷߳��͹�������Ϣ
	public static Handler handler = new Handler() {
		// �������̷߳��͸����̵߳���Ϣ��
		public void handleMessage(android.os.Message msg) {
			int type = msg.what;// ���̵߳ı�ʶ
			//��������APK�����APK��icon
			if (type == 0) {
				System.out.println("IconBitmap�յ��ˣ�������IconBitmap������" + (Bitmap) msg.obj);
				IconBitmap = (Bitmap) msg.obj;
			//��������APK�����APK�ĳ�������ͼ1
			} else if (type == 1) {
				//������ɼ���1
				ThreadCount--;
				//��ThreadCount=0ʱ ��ʾ����ͼ�������
				if (ThreadCount == 0) {
					//��ʾ����APK�Ľ�����û��� ��Ϊ0����ʾ���صĽ�����û���
					loadingimg = 0;
				}
				Thumbnail1Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail1Bitmap�յ��ˣ�������Thumbnail1Bitmap������" + (Bitmap) msg.obj);
			//��������APK�����APK�ĳ�������ͼ2
			} else if (type == 2) {
				ThreadCount--;
				if (ThreadCount == 0) {
					loadingimg = 0;
				}
				Thumbnail2Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail2Bitmap�յ��ˣ�������Thumbnail2Bitmap������" + (Bitmap) msg.obj);
			//��������APK�����APK�ĳ�������ͼ3
			} else if (type == 3) {
				ThreadCount--;
				if (ThreadCount == 0) {
					loadingimg = 0;
				}
				Thumbnail3Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail3Bitmap�յ��ˣ�������Thumbnail3Bitmap������" + (Bitmap) msg.obj);
			//��������APK�����APK�ĳ�������ͼ4
			} else if (type == 4) {
				ThreadCount--;
				if (ThreadCount == 0) {
					loadingimg = 0;
				}
				Thumbnail4Bitmap = (Bitmap) msg.obj;
				System.out.println("Thumbnail4Bitmap�յ��ˣ�������Thumbnail4Bitmap������" + (Bitmap) msg.obj);
			//�õ�����apk�Ŀ�� ���￪�����߳�һ������ ÿ���̸߳���һ���ķ�Χ �����߳�ִ����� �������
			} else if (type == 5) {
				String url = msg.getData().getString("url");
				//savepath ��ʾ�߳����ص����ݱ����λ��
				String savepath = msg.getData().getString("savepath");
				System.out.println("�������صĿ���ǡ���������������������������" + msg.arg1 + "-" + msg.arg2 + "..." + url);
				// DataUtils.downloadapk("http://qnfile.orangelive.tv/TXMV%28com.txmv%29.apk",msg.arg1,msg.arg2
				// ,handler);
				MyAsyncTask task = new MyAsyncTask(handler, msg.arg1, msg.arg2, savepath);
				//�첽ִ������ ����ͬʱ��������
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
			//������apk�Ľ�������Ϊ0
			} else if (type == 6) {
				finalprogress = 0;
			//�������صĽ��� �����ȸ�ֵ��changeprogress
			} else if (type == 7) {
				int progress = msg.getData().getInt("progress");
				String savepath = msg.getData().getString("savepath");
				changeprogress = (int) (((float) (progress) / APKlength) * 100);
				//����Ϊ100��ʾ�������  ���߽���Ϊ99�����ļ����ֽ���=�����ļ����ֽ���Ҳ��ʾ�������  ��Ϊ���ص�ʱ��temp�п��ܷ����ټӵ����
				if ((changeprogress == 100) || (changeprogress == 99 && new File(savepath).length() == APKlength)) {
					//��ʾ�ļ��Ѿ�������� �����Զ�ȥ��װ������ص�apk
					downloadstate = 1;
					//�����ļ���·����װapk
					FileUtils.installAPK(mContext, savepath);
				}
				System.out.println("����Ľ����ǡ���������������������������" + changeprogress);
			//��ʾ�������صĽ���
			} else if (type == 8) {
				loadingimg = (int) msg.obj;
			}
		};
	};




	public MainSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//����StereoRenderer
		init(context);
		//��ʼ������
		initData();
	}

	public MainSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//����StereoRenderer
		init(context);
		//��ʼ������
		initData();
	}

	// ��ʼ����������ֵ
	private void initData() {
		// APKBGbitmap =Utils.getAPKBGBitmap();
		// sappManager = StoreAPPManager.getInstance(mContext);
		System.out.println("initData������ִ�С�����������������");
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
		System.out.println("initWorld������ִ�С�����������������");
		GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
		//��ʼ���任����
		MatrixState.setInitStack();
		anchor = new Anchor(mContext);
		//СԲ��Ĵ�С
		anchor.setUnitSize(0.012f);
		mTextureRect = new TextureRect(mContext, 1.0F, 1.0F);
		textureBall = new TextureBall(mContext);
		// ��ʼ�����Ƿ��ء���һҳ����һҳ��ɾ��ͼ��Ĵ�С �Լ�ֻ��һ�����ؼ��Ĵ�С
		iconBackTextureRect = new TextureRect(mContext, backIconSize, backIconSize);
		// ��ʼ�����Ǳ�ʾҳ��С���Բ�δ�С
		dotTextureRect = new TextureRect(mContext, dotIconSize, dotIconSize);
		// ��ʼ��������ߵ���ϷͼƬ
		left1gameTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// ��ʼ�����Ǳ���APP�����Ӧ��ͼ��Ĵ�С
		appTextureRect = new TextureRect(mContext, appIconSize, appIconBgHeight);
		// ��ʼ��������ߵ�Ӧ��ͼƬ
		left1appTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// ��ʼ�������м����ϽǵĴ�ͼ
		middle1bigTextureRect = new TextureRect(mContext, 2.1F, 1.5F);
		// ��ʼ�������м����½ǵ�Сͼ
		middle1smallTextureRect = new TextureRect(mContext, 1.01F, 0.85F);
		// ��ʼ�������м����½ǵڶ��ŵ�Сͼ
		middle2smallTextureRect = new TextureRect(mContext, 1.01F, 0.85F);
		// ��ʼ�������м��ұߵĴ�ͼ
		middle2bigTextureRect = new TextureRect(mContext, 1.5F, 2.4F);
		// ��ʼ�������ұߵ��ҵ���Ϣ��ͼƬ
		right1accountTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// ��ʼ�������ұߵ����ع����ͼƬ
		right1manageTextureRect = new TextureRect(mContext, 1.3F, 1.2F);
		// ��ʼ������ֻ��һ�����ؼ��ĵذ�ľ��δ�С
		backBtnBg = new Background(mContext, 1.0f, 0.8f);
		// ��ʼ�����Ǳ���APP����ı��������ĸ��ֵĴ�С
		UnConnectedinfoRect = new TextureRect(mContext, 2.0f, 0.5f);
		// ��ʼ������ж�ز�����ʱ��ͼ�����Ͻǵ�С���Ĵ�С
		deleteRect = new TextureRect(mContext, deleteIconSize, deleteIconSize);
		// ��ʼ���������ĸ����ư�ť�ľ���
		fourBtnBg = new Background(mContext, 3.0f, 0.8f);
		// ��ʼ���������������ư�ť�ľ���
		threeBtnBg = new Background(mContext, 2.4f, 0.8f);
		// ��ʼ����������APK����ĺ�ɫ�󱳾�
		downlAPKBg = new TextureRect(mContext, 5.8f, 3.8f);
		// ��ʼ����������APK��������ذ�ť�Ĵ�С
		downlAPKBt = new TextureRect(mContext, 2.0f, 0.5f);
		// ������ֻ��һ�����ؼ��������С����
		// backBgTextureId = Utils
		// .initTexture((Bitmap) Utils.getBitmapRect(2, 2,
		// getResources().getColor(R.color.bg_back)));
		// Bitmap downBtBitmap = Utils.getdownBtBitmap(readBitMap(mContext,
		// R.drawable.image_button_long_focused),"����");
		downBtDefaultTextureId = Utils.initTexture(mContext, "����", 100, false);
		downBtFocusedTextureId = Utils.initTexture(mContext, "����", 100, true);
		downOKBtDefaultTextureId = Utils.initTexture(mContext, "��", 100, false);
		downOKBtFocusedTextureId = Utils.initTexture(mContext, "��", 100, true);
		downingBtDefaultTextureId = Utils.initTexture(mContext, 0 + "%", 0, false);
		downingBtFocusedTextureId = Utils.initTexture(mContext, 0 + "%", 0, true);
		// ��ʼ�����ؼ�ID
		backDefaultTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_back_default);
		backFocusedTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_back_focused);
		backTextureId = backDefaultTextureId;
		// ��ʼ�����ص�ʱ�򿴵���ȦȦ��һ���֣���ɫ��ID
		mLoadingOnId = Utils.initTexture(mContext.getResources(), R.drawable.loading_img);
		// ��ʼ����ɫ��������ID
		deleteRedTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_delete_red);
		// ��ʼ��ж�ص�С���ID
		deleteIconTextureId = Utils.initTexture(mContext.getResources(), R.drawable.delete);
		deleteIconFocusedTextureId = Utils.initTexture(mContext.getResources(), R.drawable.delete_focused);
		// ��ʼ��ҳ��ҳ����С���ID
		dotTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_page_indicator);
		// ��ʼ����ߵ���ϷͼƬID
		left1gameTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_game,
				mContext.getResources().getString(R.string.indexgame));
		// left1gameTextureId = Utils.initTexture(mContext.getResources(),
		// R.drawable.ic_home_game);
		// ��ʼ���м��������ͼƬID
		left1appTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_app,
				mContext.getResources().getString(R.string.indexapp));
		middle1bigTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad1);
		middle1smallTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad2);
		middle2smallTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad3);
		middle2bigTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_ad4);
		//��ʼ���ұߵ��˺�ID
		right1accountTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_user,
				mContext.getResources().getString(R.string.indexaccount));
		//��ʼ���ұߵĹ���ID
		right1manageTextureId = Utils.initTexture(mContext.getResources(), R.drawable.ic_home_manage,
				mContext.getResources().getString(R.string.indexmanage));

		// ����ı���ͼƬ
		updateTheme(true);
		// ���·����ء���һҳ����һҳ��ɾ����
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

		// ���·����ء���һҳ����һҳ��ɾ������ѡ�е�״̬
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

		// ���·����ء���һҳ����һҳ
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

		// ���·����ء���һҳ����һҳ��ѡ�е�״̬
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
		//��ͼƬ��x.y.zƽ�� ��С�ȱ�����ImageEntity����
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

	// TODO ��ʼ������
	private void initTextTexture() {

		Resources res = mContext.getResources();
		// TODO Auto-generated method stub
		// ��������Ϸ��Ӧ������ı�Ǹ��δ����
		UnConnectedinfoTextureId = Utils.initTexture(mContext.getString(R.string.unconnected), 28.0f, 300, 60);
		// ���������ع������������������
		NoDownloadingtaskTextureId = Utils.initTexture(mContext.getString(R.string.Nodownloadtask), 28.0f, 300, 60);
		// ���������ع�����������������
		NoDownloadedTextureId = Utils.initTexture(mContext.getString(R.string.Nodownloaddown), 28.0f, 300, 60);
		// ����� �������ء�������� ����
		String[] titleArray = res.getStringArray(R.array.managetitle);
		// ����� �������ء��������
		for (int i = 0; i < titleArray.length; ++i) {
			mTitleArrayId[i] = Utils.initTexture(titleArray[i], getResources().getColor(R.color.text_color));
		}
		// ������ɫ
		mBlueRectId = Utils.initTexture(Utils.getRectBitmap(getResources().getColor(R.color.blue)));
		// ����ɫ
		mGrayRectId = Utils.initTexture(Utils.getRectBitmap(getResources().getColor(R.color.text_color_grey)));

	}

	// TODO
	// ����״̬����ҳ�棩
	private void resetStatus() {
		//��ҳ�Ŀ���
		flag_index = false;
		//��Ϸ�Ŀ���
		flag_game = false;
		//Ӧ�õĿ���
		flag_app = false;
		//���ؽ���Ŀ���
		flag_down = false;
		//�˺ŵĿ���
		flag_account = false;
		//����Ŀ���
		flag_manage = false;
		//���������������ؽ���Ŀ���
		flag_downloading = false;
		//��������������ɽ���Ŀ���
		flag_downloaded = false;
		//�������ֵĿ���
		flag_leisure = false;
		//����Ŀ���
		flag_shoot = false;
		//����APK����ʱֻ����һ�����е�ͼƬ
		initdownimg = false;
		//����APK�����ID
		downlAPKID = 0;
		//��ʾ�Ƿ����ؽ����������
		APKDEL = false;
		//���������ʱ���ʾ�Ƿ񿴵�����������  û���������ʾ�Ƿ񿴵������ػ��ߴ�
		down = 1;
		for (int i = 0; i < downfrom.length; i++) {
			downfrom[i] = 0;
		}
		// ��һҳ��һҳ����ƽ�Ƶľ���
		animatedOffset = 0.0F;
		// ��С
		inAnimPosition = -1;
		// �Ŵ�
		outAnimPosition = -1;
		//�Ŵ�
		scale1 = 0.0F;
		//��С
		scale2 = 0.0F;
	}

	// TODO
	private void draw(Eye eye) {
		// System.out.println("OnDraw������ִ�С�����������������");
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
		//��СԲ��
		MatrixState.pushMatrix();
		MatrixState.translate(xoff, 0.0F, -1.0F);
		anchor.drawSelf();
		MatrixState.popMatrix();
		//�������⼸�д���֮ǰ���Ķ������������۾�������
		if (isSensorMode()) {
			Matrix.multiplyMM(cameraView, 0, mXRotationMatrix, 0, eye.getEyeView(), 0);
			Matrix.multiplyMM(cameraView, 0, mYRotationMatrix, 0, cameraView, 0);
		} else {
			Matrix.multiplyMM(cameraView, 0, mYRotationMatrix, 0, mXRotationMatrix, 0);
		}
		Matrix.multiplyMM(cameraView, 0, cameraView, 0, camera, 0);
		MatrixState.copyMVMatrix(cameraView);
		//�������⼸�д���֮�󻭵Ķ������ǹ̶���
		// draw scene ��������
		MatrixState.pushMatrix();
		MatrixState.scale(ballRadius, ballRadius, ballRadius);
		MatrixState.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		textureBall.drawSelf(senceLight, 1.0F);
		MatrixState.popMatrix();
		//�ж�wifi�Ƿ�����
		if (isContectWIFI()) {
			//���Ӳ���GAMEListû��ֵ
			if (GAMEList.size() == 0) {
				//�������ϵ�XML�ļ��Ѿ����ص�����
				if (GAMEFILE.exists()) {
					//��Ϊ�������� ����Ϊ�˷�ֹ�������� �Ͷ���һ��parsexml ��������ֻ����һ��
					parsexml++;
					if (parsexml == 1) {
						System.out.println("GAMEFILE���ڡ���������������XML");
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
			    //���ϵ�XML�ļ�û�����ص�����
				} else {
					//��Ϊ�������� ����Ϊ�˷�ֹ�������� �Ͷ���һ��parsexml ��������ֻ����һ��
					parsexml++;
					if (parsexml == 1) {
						fixedThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								System.out.println("û��XML�ļ���������������������������");
								//û���ڱ��ؾ������ص����� �ɹ�֮����ȥ����XML
								if (DataUtils.saveGAMEXML(DataUtils.GAMEAPKPATH)) {
									System.out.println(DataUtils.GAMEAPKPATH + "..............");
									System.out.println("GAMEFILE�����ڡ������������������غ����XML");
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
			//����ҳ
			if (flag_index) {
				// ����ҳ
				drawindex();
				// �����ؼ�
				drawBack();
			//�ڻ��ܶ�apk�Ľ���
			} else if (flag_game || flag_leisure || flag_shoot || flag_app) {
				drawGameApp();
				// ����Ϸ���ư�ť
				drawgameBtn(-2.6F);
			} else if (flag_manage) {
				// �����ع������
				drawmanage();
				if (flag_downloading) {
					drawgameBtn(-2.1F);
				} else if (flag_downloaded) {
					// �����ع�����ư�ť
					drawmanageBtn();
				}
			} else if (flag_account) {
				drawLoadingIcon();
				// �����ؼ�
				// drawdownloadAPK(new APKEntity());
				drawBack();
			} else if (flag_down && loadingimg == 0) {
				drawdownloadAPK(showapk);
				drawBack();
			} else if (flag_down && loadingimg == 1) {
				drawLoadingIcon();
				drawBack();
			}
			//û������wifi
		} else {
			if (flag_index) {
				// ����ҳ
				drawindex();
				// �����ؼ�
				drawBack();
			} else if (flag_game) {
				// ����Ǹδ�����⼸����
				drawText(UnConnectedinfoTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
				// ����Ϸ���ư�ť
				drawgameBtn(-2.6F);
			} else if (flag_manage) {
				// �����ع������
				drawmanage();
				if (flag_downloading) {
					drawText(NoDownloadingtaskTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
					drawgameBtn(-2.1F);
				} else if (flag_downloaded) {
					// �����ع�����ư�ť
					drawText(NoDownloadedTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
					drawmanageBtn();
				}

			} else if (flag_account) {
				drawLoadingIcon();
				// �����ؼ�
				// drawdownloadAPK(new APKEntity());
				drawBack();
			} else if (flag_app) {
				// ����Ǹδ�����⼸����
				drawText(UnConnectedinfoTextureId, 0.0F, 0.0F, iconDistance + 0.002F);
				// ��Ӧ�ÿ��ư�ť
				drawgameBtn(-2.6F);
			}
		}

	}

	// TODO
	// ����Ϸ���ư�ť
	private void drawgameBtn(float y) {
		// ���·��ĵذ�
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, y, iconDistance);
		MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		threeBtnBg.drawSelf(0.4F);
		MatrixState.popMatrix();
		// draw control buttons
		// �����ء���һҳ����һҳ ����Щͼ��
		for (int i = 0; i < 3; ++i) {
			MatrixState.pushMatrix();
			MatrixState.translate((localBtnSize + localBtnMargin) * ((float) i - 1.0F), y + 0.3F, iconDistance);
			// �Ƿ��ƶ��������з��ء���һҳ����һҳ ����Щͼ��
			if (isLookingAtObject(localBtnSize, localBtnSize, iconDistance)) {
				// ��ʾ��ѡ����
				focusBtnFlags[i] = true;
				if (i == 0) {
					// ��ʾ���ؼ���ѡ��
					isBackFocused = true;
				}
				iconBackTextureRect.drawSelf(gameBtnFocusedIcons[i]);
			} else {
				// û��ѡ�оͰ�״̬�Ĺ���
				focusBtnFlags[i] = false;
				if (i == 0) {
					// ��ʾ���ؼ�û�б�ѡ��
					isBackFocused = false;
				}
				iconBackTextureRect.drawSelf(gameBtnDefaultIcons[i]);
			}
			MatrixState.popMatrix();
		}
	}

	// ��λ��ͼƬ�м������
	private void drawText(int id, float xtran, float ytran, float ztran) {
		MatrixState.pushMatrix();
		MatrixState.translate(xtran, ytran, ztran);
		// MatrixState.translate(0.0F, 0.0F, iconDistance + 0.002F);
		UnConnectedinfoRect.drawSelf(id);
		MatrixState.popMatrix();
	}

	// TODO
	// �����ع�����ư�ť
	private void drawmanageBtn() {
		// ���·��ĵذ�
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -2.1F, iconDistance);
		MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		fourBtnBg.drawSelf(0.4F);
		MatrixState.popMatrix();
		// draw control buttons
		// �����ء���һҳ����һҳ��ɾ�� ����Щͼ��
		for (int i = 0; i < 4; ++i) {
			MatrixState.pushMatrix();
			MatrixState.translate((localBtnSize + localBtnMargin) * ((float) i - 1.5F), -1.8F, iconDistance);
			// �Ƿ��ƶ��������з��ء���һҳ����һҳ��ɾ�� ����Щͼ��
			if (isLookingAtObject(localBtnSize, localBtnSize, iconDistance)) {
				// ��ʾ��ѡ����
				focusBtnFlags[i] = true;
				if (i == 0) {
					// ��ʾ���ؼ���ѡ��
					isBackFocused = true;
				}
				if (i == 3) {
					// ɾ�������������
					if (isDeleteClick) {
						// ����ɫ��������
						iconBackTextureRect.drawSelf(deleteRedTextureId);
					} else {
						// û�б�����ͻ�ϵͳĬ�ϱ�ѡ��״̬focused��������
						iconBackTextureRect.drawSelf(manageBtnFocusedIcons[i]);
					}
				} else {
					iconBackTextureRect.drawSelf(manageBtnFocusedIcons[i]);
				}
			} else {
				// û��ѡ�оͰ�״̬�Ĺ���
				focusBtnFlags[i] = false;
				if (i == 0) {
					// ��ʾ���ؼ�û�б�ѡ��
					isBackFocused = false;
				}

				if (i == 3) {
					// ɾ�����Ѿ�������
					if (isDeleteClick) {
						// �����˾ͻ���ɫ��ͼ��
						iconBackTextureRect.drawSelf(deleteRedTextureId);
					} else {
						// û�б���ͻ�Ĭ�ϵ�defalutͼ��
						iconBackTextureRect.drawSelf(manageBtnDefaultIcons[i]);
					}
				} else {
					iconBackTextureRect.drawSelf(manageBtnDefaultIcons[i]);
				}
			}

			MatrixState.popMatrix();
		}
	}

	// �����ؼ�
	private void drawBack() {
		// draw background
		// ���ذ�
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -1.9F, iconDistance);
		MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		backBtnBg.drawSelf(0.4F);
		MatrixState.popMatrix();
		// draw back button
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -1.8F, iconDistance);
		// �Ƿ��ƶ����˷��ؼ�ͼ������
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

	// ����ҳ
	private void drawindex() {
		for (int i = 0; i < imagelists.size(); i++) {
			float scale = 0.1F;
			// �����ϽǵĴ�ͼƬ
			MatrixState.pushMatrix();
			MatrixState.translate(imagelists.get(i).getXtrans(), imagelists.get(i).getYtrans(),
					imagelists.get(i).getZtrans());
			MatrixState.rotate(imagelists.get(i).getRotate(), 0.0F, 1.0F, 0.0F);
			// �Ƿ��ƶ��������ϽǵĴ�ͼƬ��
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
					// �Ŵ�Ķ���
					MatrixState.translate(0.0F, 0.0F, scale1);
				}
				// ��ʾ��������״̬
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
				// ��ʾ��С�Ķ���
				MatrixState.translate(0.0F, 0.0F, scale - scale2);
			}
			(imagelists.get(i).getRectTexture()).drawSelf(imagelists.get(i).getRectTextureId());
			MatrixState.popMatrix();
		}
	};

	// �����ع������
	private void drawmanage() {
		drawTabBgAndTitle();
	}

	// ������������ ������������� �Լ�������
	private void drawTabBgAndTitle() {
		// ���������� ������� ���� �� ��ɫ����
		for (int i = 0; i < 2; ++i) {
			MatrixState.pushMatrix();
			MatrixState.translate(2.2F * ((float) i - 0.5F), mRectBgYScale / 2.0F - 0.6F, iconDistance);
			if (isLookingAtObject(1.4F, 0.5F, iconDistance)) {
				// ��ʾ����ƶ����������Ŀ��
				mTabFocus[i] = true;
			} else {
				mTabFocus[i] = false;
			}

			MatrixState.pushMatrix();
			MatrixState.translate(0.0F, 0.0F, 0.05F);
			MatrixState.scale(2.0F, 0.3125F, 1.0F);
			// ������� ������� �豸�洢 ����
			mTextureRect.drawSelf(mTitleArrayId[i]);
			MatrixState.popMatrix();

			// ��ʾ�������ĸ���Ŀ����
			if (mTabNo == i) {
				MatrixState.pushMatrix();
				MatrixState.translate(0.0F, -0.2F, 0.011F);
				MatrixState.scale(2.2F, 0.04F, 1.0F);
				// ����ɫ����
				mTextureRect.drawSelf(mBlueRectId);
				MatrixState.popMatrix();
			}

			MatrixState.popMatrix();
		}

		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, mRectBgYScale / 2.0F - 0.81F, 0.01F + iconDistance);
		MatrixState.scale(4.4F, 0.02F, 1.0F);
		// �������Ļ�ɫ����
		mTextureRect.drawSelf(mGrayRectId);
		MatrixState.popMatrix();
	}

	// TODO
	// ����ʾAPK���صĽ���
	private void drawdownloadAPK(APKEntity apkinfo) {
		DOWNAPKURL = apkinfo.getDownloadurl();
		SAVEAPKURL = DataUtils.GAMEIMG_FOLDERPATH + File.separator + apkinfo.getBelong() + apkinfo.getId() + ".apk";
		if (downlAPKID == 0) {
			if (!initdownimg) {
				initdownimg = true;
				//�����߳�����֮ǰ����handler��loadingimg=1 ��ʾ�������صĽ���
				handler.sendMessage(handler.obtainMessage(8, 1));
				ThreadCount = 4;
				System.out.println("��ʼ����ͼƬ��������������������������");
				readBitMapformurl(apkinfo.getIconurl(), 0, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail1url(), 1, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail2url(), 2, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail3url(), 3, apkinfo.getId(), apkinfo.getBelong());
				readBitMapformurl(apkinfo.getThumbnail4url(), 4, apkinfo.getId(), apkinfo.getBelong());
				System.out.println("�������ͼƬ��������������������������");
			}
			Bitmap downlAPKBitmap = Utils.getdownlAPKBitmap((int) 580, (int) 380, apkinfo.getName(), apkinfo.getSize(),
					apkinfo.getBesupported(), apkinfo.getIntroduction(),
					// ��ɫ����
					Utils.getAPKBGBitmap(),
					// ���ͼ��
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
					state = "���ڰ�װ";
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

	// ���ܶ�APKӦ����������г���
	// TODO
	private void drawGameApp() {
		// �õ����еĳ���
		// appList = appManager.getStoreAPKs();
		if (appList != null && appList.size() != 0) {
			// ��ʾӦ�ó����Ƿ񱻿��� ����true û�п���false
			isHasAppFocused = false;

			for (int i = 0; i < appList.size(); ++i) {
				// System.out.println(appList.size()+"......"+appList.get(i).getId()+"...."+appList.get(i).getName());
				// ��ǰ����һҳ Ĭ���ڵ�һҳ
				// System.out.println(getPageIndex(i)+"..."+i+"��ǰҳ������������");
				if (getPageIndex(i) >= -1 + currentPage && getPageIndex(i) <= 1 + currentPage) {
					// APP��IconIdĬ����-1
					// System.out.println("��ʼ��֮ǰ��TID������"+"..."+i+appList.get(i).getTextureId());
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
							// ��ʼ��ͼƬ������������
							int id = Utils.initAppTexture(bmp, appList.get(i).getName());
							appList.get(i).setTextureId(id);
							// System.out.println("��ʼ��֮���TID������"+"..."+i+appList.get(i).getTextureId());
						}
					}
					// �����һҳ����һҳ��ʱ�������� true��ʾ���� false��ʾ����ʼ
					if (isShowAnimation) {
						long passTime = System.currentTimeMillis() - startAnimationTime;
						startAnimationTime = System.currentTimeMillis();
						// animationOffset>0��ʾ��X���������ƶ� ��ȥ��һҳ
						if (animationOffset > 0.0F) {
							if (animatedOffset < animationEndOffset) {
								animatedOffset += (float) passTime / animTime * animationOffset;
							} else {
								// ��ʾ��������
								animatedOffset = animationEndOffset;
								isShowAnimation = false;
								currentPage += -1;
							}
						}
						// animationOffset<0��ʾ��X�Ḻ�����ƶ� ��ȥ��һҳ
						if (animationOffset < 0.0F) {
							if (animatedOffset > animationEndOffset) {
								animatedOffset += (float) passTime / animTime * animationOffset;
							} else {
								// ��ʾ��������
								animatedOffset = animationEndOffset;
								isShowAnimation = false;
								++currentPage;
							}
						}
					}
					// 5-8���������ƽ��һ��
					float x = appIconWidthWithMargin * ((float) (i % 4) - 1.5F)
							+ (float) (i / 8) * 4.0F * appIconWidthWithMargin + animatedOffset;
					// 5-8�������¶�ƽ��һ��
					float y = -(appIconBgHeight + appIconVerticalMargin) * (float) (i % 8 / 4)
							+ (appIconBgHeight + appIconVerticalMargin) / 2.0F - 0.55F;
					float alpha = 1.0F;
					// ������ҳ����Ӱ ͸����0.3ʵ��
					if (getPageIndex(i) != currentPage) {
						alpha = 0.3F;
					}

					MatrixState.pushMatrix();
					MatrixState.translate(x, y, iconDistance - 0.5F);
					// �Ƿ��ƶ����˱���APP�����ͼ������
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
								// ��Z���������ƶ���ӷŴ�
								MatrixState.translate(0.0F, 0.0F, scale1);
								// �Ŵ�
								MatrixState.scale(1.0F + scale1, 1.0F + scale1, 1.0F);
							}
						}
						// ����APP��״̬
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
						// ���ʾ��С�Ķ���
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
					// �����е�APPͼ���Լ��䱳��
					// draw app icon
					MatrixState.pushMatrix();
					MatrixState.translate(0.0F, 0.0F, 0.0F);
					appTextureRect.drawSelf(appList.get(i).getTextureId(), alpha);
					MatrixState.popMatrix();
					MatrixState.popMatrix();
				}
			}
			currentLookPosition = -1;
			// ����������ҳ�е���һҳ
			for (int i = 0; i < 3; ++i) {
				MatrixState.pushMatrix();
				MatrixState.translate(5.0F * (float) (i - 1), -0.5F, iconDistance - 0.5F);
				// �Ƿ��ƶ�����ҳ����������ߵ���Ӱ���� �������Ӱ����ʵ������ҳ֮�����ת��
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
			// ����ʾҳ����С��
			for (int i = 0; i < totalPage; ++i) {
				MatrixState.pushMatrix();
				// ���м��СԲ��Ϊ����0
				float x = dotIconWidthWithMargin * (float) (i - totalPage / 2);
				if (totalPage % 2 == 0) {
					x = dotIconWidthWithMargin * ((float) i - (float) (totalPage - 1) / 2.0F);
				}
				MatrixState.translate(x, -2.1F, iconDistance - 0.5F);
				// ����ǵ�ǰҳ ��ô�ͷŴ�2*2��
				if (i + 1 == currentPage) {
					MatrixState.scale(2.0F, 2.0F, 1.0F);
				}
				dotTextureRect.drawSelf(dotTextureId, 1.0F);
				MatrixState.popMatrix();
			}

		}
	}

	// ������ʱ����ֵļ���ȦȦ
	private void drawLoadingIcon() {
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, 0.0F, 0.21F + iconDistance);
		MatrixState.rotate(mLoadingAngle, 0.0F, 0.0F, -1.0F);
		// ���ص�ʱ�򿴵���ȦȦ��һ���֣���ɫ��
		mTextureRect.drawSelf(mLoadingOnId);
		MatrixState.popMatrix();
		mLoadingAngle += 3.0F;
	}

	// ����ܵ�ҳ��
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

	// 0-7����1 8-15����2 16-23����3 24-31����4 ��ʾ�ڼ�ҳ
	private int getPageIndex(int index) {
		return 1 + index / 8;
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// ��ȡ��ԴͼƬ
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
						System.out.println("����ͼƬ��" + "�̵߳��������ǡ���������������" + responseCode);
						if (responseCode == 200) {
							is = huc.getInputStream();
							RandomAccessFile raf = new RandomAccessFile(imgfile, "rws");
							byte[] buffer = new byte[1024];
							int temp = 0;
							while ((temp = is.read(buffer)) != -1) {
								raf.write(buffer, 0, temp);
								System.out.println("���ؽ��ȡ�����������" + temp);
							}
							raf.close();
							is.close();
							Bitmap imgbitmap = BitmapFactory.decodeFile(
									DataUtils.GAMEIMG_FOLDERPATH + File.separator + belong + id + sendint + ".png",
									opt);
							if (imgbitmap == null) {
								System.out.println("����ͼƬ������������");
								//���������ٴ�����
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
			// System.out.println("������ͼƬ·���ǡ�������"+DataUtils.GAMEIMG_FOLDERPATH +
			// File.separator + belong + id + sendint + ".png");
			// System.out.println("��������ͼƬ�ǡ�������"+imgbitmap);
			if (imgbitmap == null) {
				System.out.println("����ͼƬ������������");
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

	// ���۾�С��
	// radius������
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
		// �жϹ���Ƿ��ڷ��ؼ�����
		if (isBackFocused) {
			// ɾ����ťû�б������
			if (!isDeleteClick) {
				if (flag_game || flag_app || flag_account || flag_manage || flag_leisure || flag_shoot) {
					// ����״̬
					resetStatus();
					// �ص���ҳ
					flag_index = true;
					// ���ذ�������ѡ��
					isBackFocused = false;
					// ���õ�ǰҳ
					currentPage = 1;
					return;
				} else if (flag_down) {
					//����downfrom��ֵ�ж���ͨ���ĸ������������ؽ��� �ٷ��ص���ͬ�ĵط�
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
					//����ҳֱ���˳�
				} else if (flag_index) {
					((MainActivity) mContext).finish();
				}
			} else {
				// ɾ������ѡ�еĻ�������ĳɲ���ѡ��״̬
				isDeleteClick = !isDeleteClick;
			}
		} else {
			// ����ҳ
			if (flag_index) {
				// ����ҳ����currentPosition��ʾ8��Ӧ�õĵ�ǰλ��
				switch (currentPosition) {
				case 0:
					resetStatus();
					//��ʾҪչʾ��ЩAPK����
					appList = GAMEList;
					flag_game = true;
					
					return;
				case 1:
					resetStatus();
					//����û��Ӧ�õ�APK ������Ϸ��APK����
					appList = GAMEList;
					flag_app = true;
					
					return;
				case 2:
					//û������
					if (!isContectWIFI()) {
						showText(mContext.getResources().getString(R.string.unconnected));
					} else {
						//������ ����Ҳ�����Լ�����һ�� �����������
						showapk = new APKEntity("com.orange.juhelive", "GAME", 11, "���ֲ���", "31.1M", "֧������:VR�۾�",
								"���:��Western VR���ǰ�׿ƽ̨��һ��������ʵFPS��Ϸ,��������������ʵ���������һ������ţ��,"
										+ "�������˫ǹ��ǰ����ս�Ļ�����һ������!ţ��,�ϰ�!����Ϸ��ʹ��СG�ٿ�!",
								// "http://qnfile.orangelive.tv/JuheLive%20.apk",
								"http://qnfile.orangelive.tv/TXMV%28com.txmv%29.apk",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png",
								"http://img.cloudepg.net/preview/internettv/prev/starcor/epgimg/1/b/b/bec1f97302a16518b38035f1ee7c3d81.png");
						resetStatus();
						flag_down = true;
						//��ʾ�Ǵ���ҳ�������
						downfrom[1] = 1;
					}
					return;
				case 3:
					if (!isContectWIFI()) {
						showText(mContext.getResources().getString(R.string.unconnected));
					} else {
						resetStatus();
						//����û���������ֵ�APK ������Ϸ��APK����
						appList = GAMEList;
						flag_leisure = true;
					}
					return;
				case 4:
					if (!isContectWIFI()) {
						showText(mContext.getResources().getString(R.string.unconnected));
					} else {
						resetStatus();
						//����û�������APK ������Ϸ��APK����
						appList = GAMEList;
						flag_shoot = true;
						/*
						 * appList.clear(); APKEntity storeapk4 = new
						 * APKEntity("���ֲ���", R.drawable.headset,"31.1M",
						 * "֧������:VR�۾�",
						 * "���:��Western VR���ǰ�׿ƽ̨��һ��������ʵFPS��Ϸ,��������������ʵ���������һ������ţ��,"
						 * + "�������˫ǹ��ǰ����ս�Ļ�����һ������!ţ��,�ϰ�!����Ϸ��ʹ��СG�ٿ�!",
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
						showapk = new APKEntity("com.orange.juhelive", "GAME", 12, "���ֲ���", "31.1M", "֧������:VR�۾�",
								"���:��Western VR���ǰ�׿ƽ̨��һ��������ʵFPS��Ϸ,��������������ʵ���������һ������ţ��,"
										+ "�������˫ǹ��ǰ����ս�Ļ�����һ������!ţ��,�ϰ�!����Ϸ��ʹ��СG�ٿ�!",
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
					//Ĭ�Ͻ�ȥ���������صĽ���
					flag_downloading = true;
					return;
				default:
					return;
				}
				//�������
			} else if (flag_manage) {
				// ����ڵ���+1��ͼ��Ҳ��������������
				if (focusBtnFlags[3]) {
					isDeleteClick = !isDeleteClick;
					return;
				}
				// ����ڵ�һ+1��ͼ��Ҳ������һҳ��ͼ������
				if (focusBtnFlags[1]) {
					// goPreviousPage();
					return;
				}
				// ����ڵڶ�+1��ͼ��Ҳ������һҳ��ͼ������
				if (focusBtnFlags[2]) {
					// goNextPage();
					return;
				}
				//�������ر�ѡ��
				if (mTabFocus[0]) {
					resetStatus();
					//��ʾ�������������滭��ɫ����
					mTabNo = 0;
					flag_manage = true;
					flag_downloading = true;
				//������ɱ�ѡ��
				} else if (mTabFocus[1]) {
					resetStatus();
					//��ʾ������������滭��ɫ����
					mTabNo = 1;
					flag_manage = true;
					flag_downloaded = true;
				}
			} else if (flag_game || flag_leisure || flag_shoot || flag_app) {
				// ����ڵ�һ+1��ͼ��Ҳ������һҳ��ͼ������
				if (focusBtnFlags[1]) {
					goPreviousPage();
					return;
				}
				// ����ڵڶ�+1��ͼ��Ҳ������һҳ��ͼ������
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
					// �������һҳ��Ӱ
				} else if (currentLookPosition == 0) {
					if (currentPage != 1) {
						startAnimationTime = System.currentTimeMillis();
						isShowAnimation = true;
						animationOffset = 4.0F * appIconWidthWithMargin;
						animationEndOffset = animatedOffset + animationOffset;
						return;
					}
					// ����ڱ�ҳ�� APP��ѡ���� ȥ���ؽ���
				} else if (currentPosition != -1 && appList.size() > 0 && getPageIndex(currentPosition) == currentPage) {
					resetStatus();
					downfrom[0] = currentPage;
					showapk = appList.get(currentPosition);
					// System.out.println(showapk.getName()+"..............");
					// System.out.println(currentPosition+"....................");
					flag_down = true;
					return;
				}
				//�����ؽ���
			} else if (flag_down) {
				// ������������
				if (!APKDEL && down == 0 && downprogress == 0) {
					APKDEL = true;
					downAPK(DOWNAPKURL, SAVEAPKURL);
					// ������������
				} else if (APKDEL && down == 0) {
					APKDEL = false;

					// �������Ǵ�
				} else if (!APKDEL && down == 0 && downprogress == 1) {
					System.out.println("��" + showapk.getPackagename() + "app.........");
					//����ָ���ĳ���
					DataUtils.openAPK(mContext, showapk.getPackagename());
				}
			}
		}

	}
	//�Ż��Ƿ��ڻ��ܶ�APK�Ľ���
	public boolean isDrawGame() {
		return flag_game || flag_leisure || flag_shoot || flag_app;
	}

	// TODO
	public void onBackPressed() {
		//����ҳ���ؼ�ֱ���˳�
		if (flag_index) {
			((MainActivity) mContext).finish();
		//����Ϸ Ӧ�� �˺� �������� ����ȵ�ʱ���ص���ҳ			
		} else if (flag_game || flag_app || flag_account || flag_leisure || flag_shoot) {
			// ����״̬
			resetStatus();
			// �л�����ҳ
			flag_index = true;
			// �÷��ؼ�����ѡ��
			isBackFocused = false;
			currentPage = 1;
			return;
			//�ڹ������
		} else if (flag_manage) {
			//û�е�������� �ص���ҳ
			if (!isDeleteClick) {
				// ����״̬
				resetStatus();
				// �л�����ҳ
				flag_index = true;
				// �÷��ؼ�����ѡ��
				isBackFocused = false;
				currentPage = 1;
				return;
				//��������� �ĳ�û�е����״̬
			} else {
				// �Ѿ�����˾Ͱ����ĳ�û�е����״̬
				isDeleteClick = !isDeleteClick;
			}
			//������ҳ��
		} else if (flag_down) {
			//����downfrom��ֵ�ж���ͨ���ĸ������������ؽ��� �ٷ��ص���ͬ�ĵط�
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
	//ȥ��һҳ    ��������
	public void goNextPage() {
		if ((flag_game || flag_leisure || flag_shoot || flag_app) && !isShowAnimation
				&& currentPage < getTotalPageNum()) {
			startAnimationTime = System.currentTimeMillis();
			isShowAnimation = true;
			// ��X�Ḻ����ƽ��
			animationOffset = 4.0F * -appIconWidthWithMargin;
			animationEndOffset = animatedOffset + animationOffset;
		}
	}

	//ȥ��һҳ    ��������
	public void goPreviousPage() {
		if ((flag_game || flag_leisure || flag_shoot || flag_app) && !isShowAnimation && currentPage > 1) {
			startAnimationTime = System.currentTimeMillis();
			isShowAnimation = true;
			// ��X��������ƽ��
			animationOffset = 4.0F * appIconWidthWithMargin;
			animationEndOffset = animatedOffset + animationOffset;
		}
	}

	public void downAPK(final String apkurl, final String savepath) {
		//�̳߳�ִ�к�ʱ����  ������Ҫ�õ�����APK�����ֽ���
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
					System.out.println("TEST" + "�̵߳��������ǡ���������������" + responseCode);
					if (responseCode == 200) {
						is = httpurlc.getInputStream();
						//��Ҫ���ص�apk���ܵ��ֽ�����ֵ��APKlength
						APKlength = httpurlc.getContentLength();
						System.out.println("TEST" + "�����ļ����ܵ��ֽ����ǡ���������������" + APKlength);
						int threadblock = APKlength / 3;
						//������apk�Ľ�������Ϊ0
						handler.sendEmptyMessage(6);
						//�������߳�����APK
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

	//����AsyncTask��RandomAccessFileʵ�ֿ�����߳����ص�����
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

		@Override//���߳�ִ����������
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			InputStream is = null;
			try {
				URL url = new URL(params[0]);
				HttpURLConnection httpurlc = (HttpURLConnection) url.openConnection();
				httpurlc.setConnectTimeout(10000);
				httpurlc.setRequestMethod("GET");
				//�������صķ�Χ
				httpurlc.setRequestProperty("Range", "bytes=" + start + "-" + end);
				// System.out.println("TEST" + "�����ļ����ܵ��ֽ����ǡ���������������" + length);
				int responseCode = httpurlc.getResponseCode();
				System.out.println("TEST" + "�̵߳��������ǡ���������������" + responseCode);
				if (responseCode == 206) {
					is = httpurlc.getInputStream();
					System.out.println("TEST�߳�ID" + Thread.currentThread().getId() + "......" + start + "-" + end);
					//rws �ɶ���д��ִ��
					RandomAccessFile raf = new RandomAccessFile(savepath, "rws");
					//����start�ֽ��ٿ�ʼִ�б����ļ�������
					raf.seek(start);
					byte[] buffer = new byte[1024];
					int temp = 0;
					int progress = start;
					while ((temp = is.read(buffer)) != -1) {
						raf.write(buffer, 0, temp);
						progress += temp;
						//���� ��֤+tempһ��ִֻ��һ�� ����finalprogressֵ��С
						synchronized (this) {
							finalprogress += temp;
							//����onProgressUpdate�������½���
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

		@Override//���ظ��½���
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			//����handler��Ϣ���½���
			Message message = handler.obtainMessage();
			message.what = 7;
			Bundle bundle = new Bundle();
			bundle.putString("savepath", savepath);
			bundle.putInt("progress", values[0]);
			message.setData(bundle);
			handler.sendMessage(message);
		}

		@Override//���س���
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!result) {
				showText("���س���");
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
					System.out.println("��װʧ�ܡ������������������������յ��㲥");
				}
			} else {
				// downloadstate = 2;
				APKDEL = false;
				System.out.println("��װ�ɹ��������������������������յ��㲥");
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
			//����project����
			MatrixState.setProjectFrustum(-ratio, ratio, -1.0F, 1.0F, Z_NEAR, Z_FAR);
		}

		public void onSurfaceCreated(EGLConfig gl) {
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			// �������ͼ��Ĵ�С����Դ
			initWorld();
		}

		@Override
		public void onDrawEye(Eye eye) {
			//ִ�л�ͼ�߼�
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

	//������ص�ʱ��ע��㲥
	public void onResume() {
		super.onResume();
		mNeedUpdate = true;
		mSensorMode = true;

		// ��̬ע��㲥 ������װ�����º�������װ��
		if (installOkReceiver == null) {
			installOkReceiver = new InstallPackageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.PACKAGE_REPLACED");
			filter.addAction("android.intent.action.PACKAGE_ADDED");
			filter.addDataScheme("package");
			mContext.registerReceiver(installOkReceiver, filter);
		}
	}

	//�����˳���ʱ��ԭ���ݡ�ע���㲥
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
		System.out.println("�˳���ʱ�������onpause������������������������");
		if (installOkReceiver != null) {
			mContext.unregisterReceiver(installOkReceiver);
			installOkReceiver = null;
		}

	}
	//����StereoRenderer
	public void init(Context context) {
		System.out.println("init������ִ�С�����������������");
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
	//��������
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
	//������������
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
					// ��һ������ΪID�����ڶ���Ϊ��Դ������ID������Drawable��������Ϊ������
					// ����ҵ��ˣ�������ԴId������Ҳ���������0 ��
					int themeId = context.getResources().getIdentifier(theme, "drawable", context.getPackageName());
					senceLight = Utils.initTexture(context, themeId);
				}
			}
		} catch (Exception ex) {
			Log.i("MainSurfaceView", "����ͼƬcom.kamino.settings�����Ҳ���");
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
	//�ж�wifi�Ƿ�����
	public boolean isContectWIFI() {
		ConnectivityManager connectService = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = connectService.getActiveNetworkInfo();
		if (netinfo != null && netinfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	//����Toast������֮����ֵ���ʾ�������
	public static void showText(String text) {
		if (mtoast == null) {
			mtoast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		} else {
			mtoast.setText(text);
		}
		mtoast.show();
	}

}
