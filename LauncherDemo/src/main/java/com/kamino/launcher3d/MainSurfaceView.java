package com.kamino.launcher3d;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.kamino.launcher3d.StatMonitor.OnStatChangeListener;
import com.kamino.launcher3d.app.AppData;
import com.kamino.launcher3d.app.LocalAppManager;
import com.kamino.launcher3d.model.Anchor;
import com.kamino.launcher3d.model.Background;
import com.kamino.launcher3d.model.TextureArc;
import com.kamino.launcher3d.model.TextureBall;
import com.kamino.launcher3d.model.TextureRect;
import com.kamino.launcher3d.utils.MatrixState;
import com.kamino.launcher3d.utils.Utils;

public class MainSurfaceView extends CardboardView implements
		OnStatChangeListener {
	// private static final float PITCH_LIMIT = 0.08F;
	// private static final float YAW_LIMIT = 0.08F;
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
	private float appIconBgHeight = 1.05F;
	private float appIconMargin = 0.2F;
	private float appIconSize = 0.9F;
	private float appIconVerticalMargin = 0.25F;
	private float appIconWidthWithMargin;
	private List<AppData> appList;
	private LocalAppManager appManager;
	private TextureRect appTextureRect;
	private int backBgTextureId;
	private TextureRect backBtnBg;
	private int backDefaultTextureId;
	private int backFocusedTextureId;
	private float backIconSize = 0.3F;
	private int backTextureId;
	private float ballRadius = 50.0F;
	private int bluetoothConnectedTextureId;
	private int bluetoothOffTextureId;
	private int bluetoothOnTextureId;
	private float[] camera;
	private float[] cameraView;
	private int currentFocused;
	private int currentLookPosition = -1;
	private int currentPage;
	private int currentPosition = -1;
	int currentPower;
	private int[] defaultIconIds;
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
	private boolean flag_image;
	private boolean flag_index;
	private boolean flag_local;
	private boolean flag_tool;
	private boolean flag_setting;
	private boolean flag_popular;
	private boolean[] focusBtnFlags;
	private int[] focusedIconIds;
	private boolean hasDeleteAppFocused;
	private float[] headView;
	private TextureRect iconBackTextureRect;
	private float iconDistance = -4.0F;
	private float iconMargin = 0.4F;
	private float iconSize = 0.5F;
	private TextureRect iconStateTextureRect;
	private TextureRect iconTextureRect;
	private int id_rect_trans_arc;
	private int[] imageDefaultIconIds;
	private int[] imageFocusedIconIds;
	private TextureRect imageIconRect;
	private float imageIconSize;
	private float[] imageLabelAlpha;
	private int[] imageLabelTextureIds;
	private int inAnimPosition;
	private boolean isBackFocused;
	private boolean isDeleteClick;
	private boolean isHDMI;
	private boolean isHasAppFocused;
	private boolean isNeedHeadset;
	private boolean isNeedSound;
	boolean isPower;
	private boolean isShowAnimation;
	private TextureRect labelRect;
	private int[] labelTextureIds;
	private boolean lastIsPower;
	private int lastPower;
	private String lastTime;
	private int localAppTextureId;
	private Background localBtnBg;
	private int[] localBtnDefaultIcons;
	private int[] localBtnFocusedIcons;
	private float localBtnMargin = 0.3F;
	private float localBtnSize = 0.3F;
	private TextureRect localTitleRect;
	Context mContext;
	private String mCurrentLanguage;
	private String mCurrentTheme;
	private SimpleDateFormat mFormat;
	private boolean mNeedUpdate;
	MainSurfaceView.MyStereoRenderer mRenderer;
	private long mTimeStamp;
	private float[] modelView;
	private int outAnimPosition;
	private int panoramaFocusedTextureId;
	private int panoramaTextureId;
	private int panoramicVideoFocusedTextureId;
	private int panoramicVideoTextureId;
	private int photoWallFocusedTextureId;
	private int photoWallTextureId;
	Bitmap power;
	private float scale1;
	private float scale2;
	private float scaleAnimTime;
	private int sdcardInTextureId;
	private int sdcardOutTextureId;
	private int senceLight;
	private long startAnimationTime;
	private long startTime1;
	private long startTime2;
	private Background statusBtnBg;
	private int[] statusIconIds = new int[5];
	private float statusIconMargin = 0.2F;
	private float statusIconSize = 0.2F;
	private int statusIdHeadset;
	private int statusIdSound;
	private TextureRect textRect;
	private TextureArc textureArc;
	private TextureBall textureBall;
	private int timeTextureId;
	private int popularTextureId;
	private int[] toolIconFocusedIds;
	private int[] settingIconFocusedIds;
	private int[] toolIconIds;
	private int[] settingIconIds;
	private int[] popularlistFocusedIds;
	private int[] popularlistIds;
	private TextureRect toolIconRect;
	private TextureRect settingIconRect;
	private float toolIconSize;
	private float settingIconSize;
	private float[] toolLabelAlpha;
	private int[] toolsTextureIds;
	private int[] settingTextureIds;
	private int[] settingTextureIds2;
	private int[] videoDefaultIconIds;
	private int[] videoFocusedIconIds;
	private int videoFocusedTextureId;
	private int[] videoLabelTextureIds;
	private int videoTextureId;
	private int wifi1TextureId;
	private int wifi2TextureId;
	private int wifi3TextureId;
	private int wifi4TextureId;
	private int wifiOffTextureId;
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private boolean mIsDragMode;
	private float mPreviousX;
	private float mPreviousY;
	private float mTargetXAngle;
	private float mTargetYAngle;
	private float mXAngle;
	private float mYAngle;
	private float[] mXRotationMatrix;
	private float[] mYRotationMatrix;
	private boolean mSensorMode;
	private TextureRect popularRect;
	private TextureRect popularListRect;
	private float popularListwidth;
	private float popularListheight;

	public MainSurfaceView(Context context) {
		super(context);
		appIconWidthWithMargin = appIconSize + appIconMargin;
		deleteIconSize = 0.25F;
		dotIconSize = 0.03F;
		dotIconMargin = 0.2F;
		dotIconWidthWithMargin = dotIconSize + dotIconMargin;
		animTime = 350.0F;
		toolIconSize = 0.5F;
		settingIconSize = 0.5F;
		imageIconSize = 0.5F;
		popularListwidth = 4.5F;
		popularListheight = 0.35F;
		flag_index = true;
		isHasAppFocused = false;
		hasDeleteAppFocused = false;
		appList = new ArrayList<AppData>();
		currentPage = 1;
		currentFocused = -1;
		outAnimPosition = -1;
		inAnimPosition = -1;
		scaleAnimTime = 150.0F;
		toolLabelAlpha = new float[] { 0.8F, 0.8F, 0.8F, 0.8F, 0.8F };
		imageLabelAlpha = new float[] { 0.8F, 0.8F };
		focusBtnFlags = new boolean[4];
		isPower = false;
		currentPower = 0;
		mCurrentLanguage = "zh";
		lastTime = "";
		lastPower = -1;
		lastIsPower = false;
		mCurrentTheme = "sence_light1";
		mXAngle = 0.0f;
		mYAngle = 0.0f;
		mTargetXAngle = 0.0f;
		mTargetYAngle = 0.0f;
		mSensorMode = true;
		init(context);
	}

	public MainSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//ͼ��Ĵ�С + ͼ��֮��ļ�϶0.2F
		appIconWidthWithMargin = appIconSize + appIconMargin;
		deleteIconSize = 0.25F;
		dotIconSize = 0.03F;
		dotIconMargin = 0.2F;
		//СԲ��Ĵ�С+СԲ��֮��ļ��
		dotIconWidthWithMargin = dotIconSize + dotIconMargin;
		animTime = 350.0F;
		toolIconSize = 0.5F;
		settingIconSize = 0.5F;
		imageIconSize = 0.5F;
		popularListwidth = 4.5F;
		popularListheight = 0.35F;
		flag_index = true;
		isHasAppFocused = false;
		hasDeleteAppFocused = false;
		appList = new ArrayList<AppData>();
		currentPage = 1;
		currentFocused = -1;
		outAnimPosition = -1;
		inAnimPosition = -1;
		scaleAnimTime = 150.0F;
		toolLabelAlpha = new float[] { 0.8F, 0.8F, 0.8F, 0.8F, 0.8F };
		imageLabelAlpha = new float[] { 0.8F, 0.8F };
		focusBtnFlags = new boolean[4];
		isPower = false;
		currentPower = 0;
		mCurrentLanguage = "zh";
		lastTime = "";
		lastPower = -1;
		lastIsPower = false;
		mCurrentTheme = "sence_light1";
		mXAngle = 0.0f;
		mYAngle = 0.0f;
		mTargetXAngle = 0.0f;
		mTargetYAngle = 0.0f;
		mSensorMode = true;
		init(context);
	}

	private static int calculateInSampleSize(Options opt, int width, int height) {
		int result = 1;

		if (opt.outHeight > height || opt.outWidth > width) {
			int hr = Math.round((float) opt.outHeight / (float) height);
			int wr = Math.round((float) opt.outWidth / (float) width);
			if (hr >= wr) {
				System.out.println("calculateInSampleSize"+hr);
				return hr;
			}
			
			result = wr;
		}
		System.out.println("calculateInSampleSize"+result);
		return result;
	}

	//������App��������г���
	//TODO
	private void drawAllApp() {
		//�õ����еĳ���
		appList = appManager.getAppList();
		if (appList != null && appList.size() != 0) {
			//��ʾӦ�ó����Ƿ񱻿��� ����true û�п���false
			isHasAppFocused = false;
			//��ʾ�Ƿ��ƶ�����ɾ��ʱ���ֵ�С�������
			hasDeleteAppFocused = false;

			for (int i = 0; i < appList.size(); ++i) {
				//��ǰ����һҳ Ĭ���ڵ�һҳ
				if (getPageIndex(i) >= -1 + currentPage
						&& getPageIndex(i) <= 1 + currentPage) {
					//APP��TextureIdĬ����-1
					if (appList.get(i).getTextureId() == -1) {
						Bitmap bmp = appManager.getAppBitmap(appList.get(i)
								.getPackageName());
						if (bmp != null) {
							//��ʼ��ͼƬ������������
							int id = Utils.initAppTexture(bmp, appList.get(i)
									.getLabel());
							appList.get(i).setTextureId(id);
						}
					}
					//�����һҳ����һҳ��ʱ�������� true��ʾ���� false��ʾ����ʼ
					if (isShowAnimation) {
						long passTime = System.currentTimeMillis()
								- startAnimationTime;
						startAnimationTime = System.currentTimeMillis();
						//animationOffset>0��ʾ��X���������ƶ� ��ȥ��һҳ
						if (animationOffset > 0.0F) {
							if (animatedOffset < animationEndOffset) {
								animatedOffset += (float) passTime / animTime
										* animationOffset;
							} else {
								//��ʾ�������� 
								animatedOffset = animationEndOffset;
								isShowAnimation = false;
								currentPage += -1;
							}
						}
						//animationOffset<0��ʾ��X�Ḻ�����ƶ� ��ȥ��һҳ
						if (animationOffset < 0.0F) {
							if (animatedOffset > animationEndOffset) {
								animatedOffset += (float) passTime / animTime
										* animationOffset;
							} else {
								//��ʾ�������� 
								animatedOffset = animationEndOffset;
								isShowAnimation = false;
								++currentPage;
							}
						}
					}
					//5-8���������ƽ��һ��
					float x = appIconWidthWithMargin * ((float) (i % 4) - 1.5F)
							+ (float) (i / 8) * 4.0F * appIconWidthWithMargin
							+ animatedOffset;
					//5-8�������¶�ƽ��һ��
					float y = -(appIconBgHeight + appIconVerticalMargin)
							* (float) (i % 8 / 4)
							+ (appIconBgHeight + appIconVerticalMargin) / 2.0F
							- 0.55F;
					float alpha = 1.0F;
					//������ҳ����Ӱ ͸����0.3ʵ��
					if (getPageIndex(i) != currentPage) {
						alpha = 0.3F;
					}

					MatrixState.pushMatrix();
					MatrixState.translate(x, y, iconDistance);
					//�Ƿ��ƶ����˱���APP�����ͼ������
					if (isLookingAtObject(0.9F, 1.05F, iconDistance - 0.001F)) {
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

							if (getPageIndex(i) == currentPage) {
								//��Z���������ƶ���ӷŴ�
								MatrixState.translate(0.0F, 0.0F, scale1);
								//�Ŵ�
								MatrixState.scale(1.0F + scale1, 1.0F + scale1,
										1.0F);
							}
						}
						//����APP��״̬
						isHasAppFocused = true;
						currentPosition = i;
					} else if (outAnimPosition == i) {
						outAnimPosition = -1;
						inAnimPosition = i;
						scale2 = 0.0F;
						startTime2 = System.currentTimeMillis();
						if (getPageIndex(i) == currentPage) {
							MatrixState.translate(0.0F, 0.0F, scale1);
							MatrixState.scale(1.0F + scale1, 1.0F + scale1,
									1.0F);
						}
						//���ʾ��С�Ķ���
					} else if (inAnimPosition == i) {
						if (scale2 < 0.1F) {
							scale2 = 0.1F * ((float) (System
									.currentTimeMillis() - startTime2) / scaleAnimTime);
						} else {
							scale2 = 0.1F;
						}

						if (getPageIndex(i) == currentPage) {
							MatrixState.translate(0.0F, 0.0F, 0.1F - scale2);
							MatrixState.scale(1.1F - scale2, 1.1F - scale2,
									1.0F);
						}
					}
					//�����е�APPͼ���Լ��䱳��
					// draw app icon
					MatrixState.pushMatrix();
					MatrixState.translate(0.0F, 0.0F, 0.0F);
					appTextureRect.drawSelf(appList.get(i).getTextureId(),
							alpha);
					MatrixState.popMatrix();

					// draw del icon
					//�����ɾ����ť&&�ڵ�ǰҳ&&����ϵͳ��APP
					if (isDeleteClick && getPageIndex(i) == currentPage
							&& !appList.get(i).isSystemApp()) {
						MatrixState.pushMatrix();
						MatrixState.translate(0.325F, iconMargin, 0.003F);
						//�Ƿ��ƶ�����ж��ʱ���ֵ�С�������
						if (isLookingAtObject(deleteIconSize, deleteIconSize,
								0.003F + iconDistance)) {
							hasDeleteAppFocused = true;
							currentFocused = i;
							deleteRect.drawSelf(deleteIconFocusedTextureId,
									1.0F);
						} else {
							deleteRect.drawSelf(deleteIconTextureId, 1.0F);
						}

						MatrixState.popMatrix();
					}
					
					MatrixState.popMatrix();
				}
			}

			currentLookPosition = -1;

			//����������ҳ�е���һҳ
			for (int i = 0; i < 3; ++i) {
				MatrixState.pushMatrix();
				MatrixState.translate(5.0F * (float) (i - 1), -0.5F,
						iconDistance);
				//�Ƿ��ƶ�����ҳ����������ߵ���Ӱ���� �������Ӱ����ʵ������ҳ֮�����ת�� 
				if (isLookingAtObject(3.0F, 2.2F, iconDistance)) {
					currentLookPosition = i;
				}

				MatrixState.popMatrix();
			}

			if (!isHasAppFocused) {
				currentPosition = -1;
			}

			if (!hasDeleteAppFocused) {
				currentFocused = -1;
			}

			int totalPage = getTotalPageNum();

			// draw page dot
			//����ʾҳ����С��
			for (int i = 0; i < totalPage; ++i) {
				MatrixState.pushMatrix();
				//���м��СԲ��Ϊ����0
				float x = dotIconWidthWithMargin * (float) (i - totalPage / 2);
				if (totalPage % 2 == 0) {
					x = dotIconWidthWithMargin
							* ((float) i - (float) (totalPage - 1) / 2.0F);
				}

				MatrixState.translate(x, -2.1F, iconDistance);
				//����ǵ�ǰҳ ��ô�ͷŴ�2*2��
				if (i + 1 == currentPage) {
					MatrixState.scale(2.0F, 2.0F, 1.0F);
				}
				
				dotTextureRect.drawSelf(dotTextureId, 1.0F);
				MatrixState.popMatrix();
			}
		}
	}

	//TODO
	//�����ؼ�
	private void drawBack() {
		// draw background
		//���ذ�
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -1.9F, iconDistance);
		MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		backBtnBg.drawSelf(backBgTextureId, 0.8F);
		MatrixState.popMatrix();

		// draw back button
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, -1.8F, iconDistance);
		//�Ƿ��ƶ����˷��ؼ�ͼ������
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
	//TODO
	//�����ư�ť
	private void drawBtn() {
		// draw control buttons
		//�����ء���һҳ����һҳ��ɾ�� ����Щͼ��
		for (int i = 0; i < 4; ++i) {
			MatrixState.pushMatrix();
			MatrixState.translate((localBtnSize + localBtnMargin)
					* ((float) i - 1.5F), -2.8F, iconDistance);
			//�Ƿ��ƶ��������з��ء���һҳ����һҳ��ɾ�� ����Щͼ��
			if (isLookingAtObject(localBtnSize, localBtnSize, iconDistance)) {
				//��ʾ��ѡ����
				focusBtnFlags[i] = true;
				if (i == 0) {
					//��ʾ���ؼ���ѡ��
					isBackFocused = true;
				}

				if (i == 3) {
					//ɾ�������������
					if (isDeleteClick) {
						//����ɫ��������
						iconBackTextureRect.drawSelf(deleteRedTextureId);
					} else {
						//û�б�����ͻ�ϵͳĬ�ϱ�ѡ��״̬focused��������
						iconBackTextureRect.drawSelf(localBtnFocusedIcons[i]);
					}
				} else {
					iconBackTextureRect.drawSelf(localBtnFocusedIcons[i]);
				}
			} else {
				//û��ѡ�оͰ�״̬�Ĺ���
				focusBtnFlags[i] = false;
				if (i == 0) {
					//��ʾ���ؼ�û�б�ѡ��
					isBackFocused = false;
				}

				if (i == 3) {
					//ɾ�����Ѿ�������
					if (isDeleteClick) {
						//�����˾ͻ���ɫ��ͼ��
						iconBackTextureRect.drawSelf(deleteRedTextureId);
					} else {
						//û�б���ͻ�Ĭ�ϵ�defalutͼ��
						iconBackTextureRect.drawSelf(localBtnDefaultIcons[i]);
					}
				} else {
					iconBackTextureRect.drawSelf(localBtnDefaultIcons[i]);
				}
			}

			MatrixState.popMatrix();
		}
	}
	//TODO
	//������APP
	private void drawTools() {
		// draw icon background
		MatrixState.pushMatrix();
		MatrixState.rotate(-42.0F, 0.0F, 1.0F, 0.0F);
		MatrixState.translate(0.0F, -0.15F, 0.0F);
		textureArc.drawSelf(id_rect_trans_arc, 0.2F + -iconDistance, 1.2F,
				84.0F);
		MatrixState.popMatrix();
		//Ĭ��û�б�ѡ��
		currentPosition = -1;

		// draw icons
		for (int i = 0; i < toolIconIds.length; ++i) {
			MatrixState.pushMatrix();
			if (isHDMI) {
				MatrixState.rotate(32.0F + (i * -16), 0.0F, 1.0F, 0.0F);
			} else {
				MatrixState.rotate(27.0F + (i * -18), 0.0F, 1.0F, 0.0F);
			}
			
			MatrixState.translate(0.0F, 0.0F, iconDistance);
			//�Ƿ��ƶ����˹���APP�����ͼ��
			if (isLookingAtObject(toolIconSize, 0.7F + toolIconSize,
					iconDistance)) {
				if (outAnimPosition != i) {
					outAnimPosition = i;
					scale1 = 0.0F;
					startTime1 = System.currentTimeMillis();
				} else {
					if (scale1 < 0.8F) {
						scale1 = 0.8F * ((System.currentTimeMillis() - startTime1) / scaleAnimTime);
					} else {
						scale1 = 0.8F;
					}
					//�Ŵ�Ķ���
					MatrixState.translate(0.0F, 0.0F, scale1);
				}
				//��ʾ��������״̬
				currentPosition = i;
			} else if (outAnimPosition == i) {
				outAnimPosition = -1;
				inAnimPosition = i;
				scale2 = 0.0F;
				startTime2 = System.currentTimeMillis();
				MatrixState.translate(0.0F, 0.0F, scale1);
			} else if (inAnimPosition == i) {
				if (scale2 < 0.8F) {
					scale2 = 0.8F * ((System.currentTimeMillis() - startTime2) / scaleAnimTime);
				} else {
					scale2 = 0.8F;
				}
				//��ʾ��С�Ķ���
				MatrixState.translate(0.0F, 0.0F, 0.8F - scale2);
			}

			MatrixState.pushMatrix();
			//�Ƿ��ƶ����˹���APP�����ͼ��
			if (isLookingAtObject(toolIconSize, 0.7F + toolIconSize,
					iconDistance)) {
				//������ʱ��ͼƬ��һ��
				toolIconRect.drawSelf(toolIconFocusedIds[i]);
			} else {
				toolIconRect.drawSelf(toolIconIds[i]);
			}
			MatrixState.popMatrix();

			// draw icon text
			MatrixState.pushMatrix();
			MatrixState.translate(0.0F, -0.55F, 0.0F);
			setToolLabelAlpha();
			labelRect.drawSelf(toolsTextureIds[i], toolLabelAlpha[i]);
			MatrixState.popMatrix();
			MatrixState.popMatrix();
		}
	}
	//TODO
	//������APPͼƬ�������Լ�����
	private void drawSetting() {
		// draw icon background
		//������ǽ
		/*MatrixState.pushMatrix();
		MatrixState.rotate(-42.0F, 0.0F, 1.0F, 0.0F);
		MatrixState.translate(0.0F, -0.15F, 0.0F);
		textureArc.drawSelf(id_rect_trans_arc, 0.2F + -iconDistance, 1.5F,
				84.0F);
		MatrixState.popMatrix();*/
		//Ĭ��û�б�ѡ��
		currentPosition = -1; 

		// draw icons
		for (int i = 0; i < settingIconIds.length; ++i) {
			MatrixState.pushMatrix();
			MatrixState.rotate(32.0F + (i * -16), 0.0F, 1.0F, 0.0F);
			
			MatrixState.translate(0.0F, 0.0F, iconDistance+1.6F);
			//�Ƿ��ƶ���������APP�����ͼ��
			if (isLookingAtObject(settingIconSize, 0.7F + settingIconSize,
					iconDistance+1.6F)) {
				if (outAnimPosition != i) {
					outAnimPosition = i;
					scale1 = 0.0F;
					startTime1 = System.currentTimeMillis();
				} else {
					if (scale1 < 0.4F) {
						scale1 = 0.4F * ((System.currentTimeMillis() - startTime1) / scaleAnimTime);
					} else {
						scale1 = 0.4F;
					}
					//�Ŵ�Ķ���
					MatrixState.translate(0.0F, 0.0F, scale1);
				}
				//��ʾ��������״̬
				currentPosition = i;
			} else if (outAnimPosition == i) {
				outAnimPosition = -1;
				inAnimPosition = i;
				scale2 = 0.0F;
				startTime2 = System.currentTimeMillis();
				MatrixState.translate(0.0F, 0.0F, scale1);
			} else if (inAnimPosition == i) {
				if (scale2 < 0.4F) {
					scale2 = 0.4F * ((System.currentTimeMillis() - startTime2) / scaleAnimTime);
				} else {
					scale2 = 0.4F;
				}
				//��ʾ��С�Ķ���
				MatrixState.translate(0.0F, 0.0F, 0.4F - scale2);
			}

			MatrixState.pushMatrix();
			//�Ƿ��ƶ����˹���APP�����ͼ��
			if (isLookingAtObject(settingIconSize, 0.7F + settingIconSize,
					iconDistance+1.6F)) {
				//������ʱ��ͼƬ��һ��
				settingIconRect.drawSelf(settingIconFocusedIds[i]);
			} else {
				settingIconRect.drawSelf(settingIconIds[i]);
			}
			MatrixState.popMatrix();

		/*	// draw icon text ��ͼ���������
			MatrixState.pushMatrix();
			MatrixState.translate(0.0F, 0.25F, 0.0F);
			setToolLabelAlpha();
			labelRect.drawSelf(settingTextureIds[i], toolLabelAlpha[i]);
			MatrixState.popMatrix();
			
			// draw icon text ��ͼ���������
			MatrixState.pushMatrix();
			MatrixState.translate(0.0F, -0.55F, 0.0F);
			setToolLabelAlpha();
			labelRect.drawSelf(settingTextureIds2[i], toolLabelAlpha[i]);
			MatrixState.popMatrix();*/
			MatrixState.popMatrix();
		}
	}
	//TODO
	//��ͨ������������Լ�����
	private void drawpopular() {
		// draw rect background
		//����������
		MatrixState.pushMatrix();
		MatrixState.translate(0.0F, 0.0F, iconDistance-0.2F);
		popularRect.drawSelf(popularTextureId);
		MatrixState.popMatrix();
		//Ĭ��û�б�ѡ��
		currentPosition = -1; 

		//���б�
		for (int i = 0; i < popularlistIds.length; ++i) {
			MatrixState.pushMatrix();
		 	MatrixState.rotate(0, 0.0F, 1.0F, 0.0F);
			MatrixState.translate(0.0F, 0.5F-i*popularListheight, iconDistance);
			//�Ƿ��ƶ���������б�����
			if (isLookingAtObject(popularListwidth,  popularListheight,
					iconDistance)) {
				if (outAnimPosition != i) {
					outAnimPosition = i;
					scale1 = 0.0F;
					startTime1 = System.currentTimeMillis();
				} else {
					if (scale1 < 0.4F) {
						scale1 = 0.4F * ((System.currentTimeMillis() - startTime1) / scaleAnimTime);
					} else {
						scale1 = 0.4F;
					}
					//�Ŵ�Ķ���
					MatrixState.translate(0.0F, 0.0F, scale1);
				}
				//��ʾ��������״̬
				currentPosition = i;
			} else if (outAnimPosition == i) {
				outAnimPosition = -1;
				inAnimPosition = i;
				scale2 = 0.0F;
				startTime2 = System.currentTimeMillis();
				MatrixState.translate(0.0F, 0.0F, scale1);
			} else if (inAnimPosition == i) {
				if (scale2 < 0.4F) {
					scale2 = 0.4F * ((System.currentTimeMillis() - startTime2) / scaleAnimTime);
				} else {
					scale2 = 0.4F;
				}
				//��ʾ��С�Ķ���
				MatrixState.translate(0.0F, 0.0F, 0.4F - scale2);
			}

			MatrixState.pushMatrix();
			//�Ƿ��ƶ����˹���APP�����ͼ��
			if (isLookingAtObject(popularListwidth, popularListheight,
					iconDistance)) {
				//������ʱ��ͼƬ��һ��
				popularListRect.drawSelf(popularlistFocusedIds[i]);
			} else {
				popularListRect.drawSelf(popularlistIds[i]);
			}
			MatrixState.popMatrix();
			MatrixState.popMatrix();
		}
	}

	public static Bitmap getBackground(Context context, int resId) {
		InputStream is = context.getResources().openRawResource(resId);
		System.out.println(is.hashCode());
		Options opt = new Options();
		 //���inJustDecoedBounds����Ϊtrue�Ļ���
		 //����bitmapʱ����ֻ������ߡ����Mime���ͣ�������Ϊ�������ڴ棬�Ӷ���ʡ���ڴ�ռ䡣
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, opt);
		opt.inSampleSize = calculateInSampleSize(opt, 2000, 1000);
		opt.inJustDecodeBounds = false;
		System.out.println("OK");
		return BitmapFactory.decodeStream(is, null, opt);
	}

	//0-7����1 8-15����2 16-23����3 24-31����4 ��ʾ�ڼ�ҳ
	private int getPageIndex(int index) {
		return 1 + index / 8;
	}

	//����ܵ�ҳ��
	private int getTotalPageNum() {
		appList = appManager.getAppList();
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

	private void init(Context context) {
		mContext = context;
		camera = new float[16];
		headView = new float[16];
		modelView = new float[16];
		cameraView = new float[16];
		mXRotationMatrix = new float[16];
		mYRotationMatrix = new float[16];
		Matrix.setRotateM(mXRotationMatrix, 0, 0, 1.0f, 0, 0);
		Matrix.setRotateM(mYRotationMatrix, 0, 0, 0, 1.0f, 0);
		appManager = LocalAppManager.getInstance(mContext);
		power = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.power_run);

		setEGLContextClientVersion(2);
		setEGLConfigChooser(new EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 gl, EGLDisplay display) {
				int[] params = new int[] { EGL10.EGL_SURFACE_TYPE,
						EGL10.EGL_WINDOW_BIT, EGL10.EGL_RENDERABLE_TYPE, 4,
						EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8,
						EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_DEPTH_SIZE, 8,
						EGL10.EGL_SAMPLE_BUFFERS, 1, EGL10.EGL_SAMPLES, 4,
						EGL10.EGL_STENCIL_SIZE, 0, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				gl.eglChooseConfig(display, params, configs, 1, new int[1]);
				return configs[0];
			}
		});

		mRenderer = new MainSurfaceView.MyStereoRenderer();
		setRenderer(mRenderer);
		setRenderMode(RENDERMODE_CONTINUOUSLY);
	}
	// 
	//���۾�С�� 
	//radius������
	private boolean isLookingAtObject(float width, float height, float radius) {
		float[] initVec = new float[] { 0.0F, 0.0F, 0.0F, 1.0F };
		float[] objPosVec = new float[4];
		double yaw1 = Math.atan2((width / 2.0F), Math.abs(radius));
		double pitch1 = Math.atan2((height / 2.0F), Math.abs(radius));

		Matrix.multiplyMM(modelView, 0, headView, 0, MatrixState.getMMatrix(),
				0);
		Matrix.multiplyMV(objPosVec, 0, modelView, 0, initVec, 0);

		float yaw = (float) Math.atan2(objPosVec[0], (-objPosVec[2]));
		float pitch = (float) Math.atan2(objPosVec[1], (-objPosVec[2]));

		return Math.abs(yaw) < Math.abs(yaw1)
				&& Math.abs(pitch) < Math.abs(pitch1);
	}

	//TODO
	//����״̬����ҳ�棩
	private void resetStatus() {
		flag_index = false;
		flag_local = false;
		flag_image = false;
		flag_tool = false;
		flag_setting = false;
		flag_popular = false;
		//��һҳ��һҳ����ƽ�Ƶľ���
		animatedOffset = 0.0F;
		//��С
		inAnimPosition = -1;
		//�Ŵ�
		outAnimPosition = -1;
		scale1 = 0.0F;
		scale2 = 0.0F;
	}

	//����ͼƬAPP������ֵ�͸����
	private void setImageLabelAlpha() {
		//Ĭ������Ϊ0.8
		for (int i = 0; i < imageLabelAlpha.length; ++i) {
			imageLabelAlpha[i] = 0.8F;
		}
		 //��ѡ�о�����Ϊ1
		if (currentPosition != -1) {
			imageLabelAlpha[currentPosition] = 1.0F;
		}
	}

	//TODO
	//���ù���APP������ֵ�͸����
	private void setToolLabelAlpha() {
		//Ĭ�ϵ�͸����Ϊ0.8
		for (int i = 0; i < toolLabelAlpha.length; ++i) {
			toolLabelAlpha[i] = 0.8F;
		}
		//��������ʱ��͸���ȸĳ�1
		if (currentPosition != -1) {
			toolLabelAlpha[currentPosition] = 1.0F;
		}
	}

	private void updateLanguage() {
		String locale = mContext.getResources().getConfiguration().locale
				.getLanguage();
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

	private void updateTheme(boolean reload) {
		try {
			Context context = mContext.createPackageContext(
					"com.kamino.settings", 2);
			if (context != null) {
				String theme = context.getSharedPreferences("Theme", 5)
						.getString("Theme_id", "sence_light1");
				if (!TextUtils.equals(mCurrentTheme, theme) || reload) {
					mCurrentTheme = theme;
					if (senceLight > 0) {
						GLES20.glDeleteTextures(1, new int[] { senceLight }, 0);
					}
					//��һ������ΪID�����ڶ���Ϊ��Դ������ID������Drawable��������Ϊ������
					//����ҵ��ˣ�������ԴId������Ҳ���������0 ��
					int themeId = context.getResources().getIdentifier(theme,
							"drawable", context.getPackageName());
					senceLight = Utils.initTexture(getBackground(context,
							themeId));
				}  
			}
		}catch (Exception ex) {
			Log.i("MainSurfaceView", "����ͼƬcom.kamino.settings�����Ҳ���");
			senceLight = Utils.initTexture(getBackground(mContext,
					R.drawable.sence_light1));
//			senceLight = Utils.initTexture(mContext,  
//					R.drawable.sence_light1);  
		}
	}
	//TODO
	public void draw(Eye eye) {
		float eyeX;
		if (eye.getType() == Eye.Type.LEFT) {
			eyeX = 2.0E-4F;
		} else {
			eyeX = -2.0E-4F;
		}

		// set anchor camera
		Matrix.setLookAtM(camera, 0, eyeX, 0.0F, CAMERA_Z, 0.0F, 0.0F, 0.0F,
				0.0F, 1.0F, 0.0F);
		MatrixState.copyMVMatrix(camera);

		// draw anchor
		float xoff;
		if (eye.getType() == Eye.Type.LEFT) {
			xoff = 0.02F;
		} else {
			xoff = -0.02F;
		}
		MatrixState.pushMatrix();
		MatrixState.translate(xoff, 0.0F, -1.0F);
		anchor.drawSelf();
		MatrixState.popMatrix();

		// set object camera
		// add rotaion for x/y
		if (isSensorMode()) {
			Matrix.multiplyMM(cameraView, 0, mXRotationMatrix, 0,
					eye.getEyeView(), 0);
			Matrix.multiplyMM(cameraView, 0, mYRotationMatrix, 0, cameraView, 0);
		} else {
			Matrix.multiplyMM(cameraView, 0, mYRotationMatrix, 0,
					mXRotationMatrix, 0);
		}
		Matrix.multiplyMM(cameraView, 0, cameraView, 0, camera, 0);
		MatrixState.copyMVMatrix(cameraView);

		// draw scene ��������
		MatrixState.pushMatrix();
		MatrixState.scale(ballRadius, ballRadius, ballRadius);
		MatrixState.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		textureBall.drawSelf(senceLight, 1.0F);
		MatrixState.popMatrix();

		// if it is first level
		//�ж��Ƿ�����ҳ
		//TODO
		if (flag_index) {
			// draw arc icon-wall background
			//����ҳ�ı���ǽ
			MatrixState.pushMatrix();
			MatrixState.rotate(-40.0F, 0.0F, 1.0F, 0.0F);
	//		MatrixState.rotate(-50.0F, 0.0F, 1.0F, 0.0F);
			MatrixState.translate(0.0F, -0.15F, 0.0F);
//			textureArc.drawSelf(id_rect_trans_arc, 0.2F + -iconDistance, 1.2F,
//					100.0F);
			textureArc.drawSelf(id_rect_trans_arc, 0.2F + -iconDistance, 1.2F,
					81.0F);
			MatrixState.popMatrix();

			currentPosition = -1;

			// draw icons
			//����ҳAPP��ͼ��
			for (int i = 0; i < defaultIconIds.length; ++i) {
				MatrixState.pushMatrix();
				//MatrixState.rotate(37.5F + (float) (i * -15), 0.0F, 1.0F, 0.0F);
				MatrixState.rotate(30.0F + (float) (i * -15), 0.0F, 1.0F, 0.0F);
				MatrixState.translate(0.0F, 0.0F, iconDistance);
				//�Ƿ񿴵�����ҳAPP��ͼ��
				if (isLookingAtObject(iconSize, iconSize, iconDistance)) {
					currentPosition = i;
					if (outAnimPosition != i) {
						outAnimPosition = i;
						scale1 = 0.0F;
						startTime1 = System.currentTimeMillis();
					} else {
						if (scale1 < 1.0F) {
							scale1 = 1.0F * ((System.currentTimeMillis() - startTime1) / scaleAnimTime);
						} else {
							scale1 = 1.0F;
						}

						MatrixState.translate(0.0F, 0.0F, scale1);
					}

					iconTextureRect.drawSelf(focusedIconIds[i]);
				} else {
					if (outAnimPosition == i) {
						outAnimPosition = -1;
						inAnimPosition = i;
						scale2 = 0.0F;
						startTime2 = System.currentTimeMillis();
						MatrixState.translate(0.0F, 0.0F, scale1);
					} else if (inAnimPosition == i) {
						if (scale2 < 1.0F) {
							scale2 = 1.0F * ((System.currentTimeMillis() - startTime2) / scaleAnimTime);
						} else {
							scale2 = 1.0F;
						}

						MatrixState.translate(0.0F, 0.0F, 1.0F - scale2);
					}

					iconTextureRect.drawSelf(defaultIconIds[i]);
				}

				MatrixState.popMatrix();

				// draw current icon text
				//����ҳͼ���·������� Ҫ���㿴���� û������currentPosition == -1
				if (currentPosition != -1) {
					MatrixState.pushMatrix();
//					MatrixState.rotate(37.5F + (float) (-15 * currentPosition),
//							0.0F, 1.0F, 0.0F);
					MatrixState.rotate(30.0F + (float) (-15 * currentPosition),
							0.0F, 1.0F, 0.0F);
					MatrixState.translate(0.0F, -0.36F, iconDistance);
					MatrixState.translate(0.0F, 0.36F - 0.36F
							* (iconDistance - scale1) / iconDistance, scale1);
					MatrixState.scale(0.8F, 0.8F, 1.0F);
					labelRect.drawSelf(labelTextureIds[currentPosition]);
					MatrixState.popMatrix();
				}
			}

			// draw time text
			//����ҳ�Ϸ���ʱ�䡢����
			MatrixState.pushMatrix();
			MatrixState.translate(0.08F, 0.95F, iconDistance);
//			MatrixState.translate(0.16F, 0.95F, iconDistance);
			setTimeTextureId();
			textRect.drawSelf(timeTextureId);
			MatrixState.popMatrix();

			// draw status icon background
			//���ذ����
			MatrixState.pushMatrix();
			MatrixState.translate(0.0F, -1.9F, iconDistance);
			MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			//0.6F��ʾ�ذ��͸����
			statusBtnBg.drawSelf(0.6F);
			MatrixState.popMatrix();

			int count = 3;
			//�ж��Ƿ���˶���
			if (isNeedHeadset) {
				//Log.i("MainSurfaceView", "������+1");
				++count;
			}
			//�ж��Ƿ��Ǿ���
			if (isNeedSound) {
				//Log.i("MainSurfaceView", "����+1");
				++count;
			}

			// draw status icons
			//���ذ��Ϸ���wifi��������SD����������������
			for (int i = 0; i < count; ++i) {
				MatrixState.pushMatrix();
				//3��ͼ���ʱ��������ƶ��Ķ�һ��
				float x = (statusIconSize + statusIconMargin)
						* (float) (i - count / 2);
				if (count % 2 == 0) {
					x = (statusIconSize + statusIconMargin)
							* ((float) i - (float) (count - 1) / 2.0F);
				}

				MatrixState.translate(x, -1.75F, iconDistance);
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
				                
				if(i == 4){
					Log.i("MainSurfaceView", "count==5");
					iconStateTextureRect.drawSelf(statusIdSound);
				}else if (i == 3) {
					if (isNeedSound && isNeedHeadset) {
						Log.i("MainSurfaceView", "��������");
						iconStateTextureRect.drawSelf(statusIdHeadset);
						//iconStateTextureRect.drawSelf(statusIdSound);
					} else if (isNeedSound) {
						//Ϊʲô������ʱ��������
						//Log.i("MainSurfaceView", "����");
						//iconStateTextureRect.drawSelf(statusIdSound);
					}else if (isNeedHeadset) {
						//Log.i("MainSurfaceView", "������");
						iconStateTextureRect.drawSelf(statusIdHeadset);
					}
				}  else {
					iconStateTextureRect.drawSelf(statusIconIds[i]);
				}

				MatrixState.popMatrix();
			}
		} else {
			//ҳ�治����ҳ��
			// if it is images level
			//�Ƿ������ͼƬAPP����
			//TODO
			if (flag_image) {
				// draw icon-wall background
				MatrixState.pushMatrix();
				MatrixState.rotate(-25.0F, 0.0F, 1.0F, 0.0F);
				MatrixState.translate(0.0F, -0.15F, 0.0F);
				//��ͼƬAPP����ı���ǽ
				textureArc.drawSelf(id_rect_trans_arc, 0.2F + -iconDistance,
						1.2F, 50.0F);
				MatrixState.popMatrix();

				currentPosition = -1;

				// draw icons
				//��ͼƬAPP���������ͼ��
				for (int i = 0; i < imageDefaultIconIds.length; ++i) {
					MatrixState.pushMatrix();
					//��y����ת
					MatrixState.rotate(9.0F + (float) (i * -18), 0.0F, 1.0F,
							0.0F);
					MatrixState.translate(0.0F, 0.0F, iconDistance);
					//�Ƿ��ƶ�����ͼƬAPP�����ͼ������
					if (isLookingAtObject(imageIconSize, 0.7F + imageIconSize,
							iconDistance)) {
						if (outAnimPosition != i) {
							outAnimPosition = i;
							scale1 = 0.0F;
							startTime1 = System.currentTimeMillis();
						} else {
							if (scale1 < 0.8F) {
								scale1 = 0.8F * ((System.currentTimeMillis() - startTime1) / scaleAnimTime);
							} else {
								scale1 = 0.8F;
							}
							//��Z������˵� �˵��۾���������� ���˵������Ŵ���
							MatrixState.translate(0.0F, 0.0F, scale1);
						}

						currentPosition = i;
					} else if (outAnimPosition == i) {
						outAnimPosition = -1;
						inAnimPosition = i;
						scale2 = 0.0F;
						startTime2 = System.currentTimeMillis();
						MatrixState.translate(0.0F, 0.0F, scale1);
					} else if (inAnimPosition == i) {
						if (scale2 < 0.8F) {
							scale2 = 0.8F * ((float) (System
									.currentTimeMillis() - startTime2) / scaleAnimTime);
						} else {
							scale2 = 0.8F;
						}
						//��Z���ƶ���Խ��ԽС��˵���Ŵ��Խ��Խ��
						MatrixState.translate(0.0F, 0.0F, 0.8F - scale2);
					}

					MatrixState.pushMatrix();
					//�Ƿ��ƶ�����ͼƬAPP�����ͼ������
					//���ɱ������˵�ͼƬfocused
					if (isLookingAtObject(imageIconSize, 0.7F + imageIconSize,
							iconDistance)) {
						imageIconRect.drawSelf(imageFocusedIconIds[i]);
					} else {
						imageIconRect.drawSelf(imageDefaultIconIds[i]);
					}
					MatrixState.popMatrix();

					// draw icon text
					MatrixState.pushMatrix();
					MatrixState.translate(0.0F, -0.55F, 0.0F);
					setImageLabelAlpha();
					//��ͼƬAPP����APP������֣�͸����
					labelRect.drawSelf(imageLabelTextureIds[i],
							imageLabelAlpha[i]);
					MatrixState.popMatrix();
					MatrixState.popMatrix();
				}

				// draw back icon
				//���·��ķ��ؼ��͵ذ�
				drawBack();
				return;
			}
			// if it is local level
			//���Ƿ��ڱ���APP����
			//TODO
			if (flag_local) {
				// draw title text
				//������Ӧ�����ĸ���
				MatrixState.pushMatrix();
				MatrixState.translate(0.0F, 0.9F, iconDistance - 0.002F);
				localTitleRect.drawSelf(localAppTextureId);
				MatrixState.popMatrix();

				// draw app list
				//�����е�APP�Լ������С��
				drawAllApp();

				// draw status background
				//���·��ĵذ�
				MatrixState.pushMatrix();
				MatrixState.translate(0.0F, -3.1F, iconDistance);
				MatrixState.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				localBtnBg.drawSelf(0.6F);
				MatrixState.popMatrix();

				// draw control buttons
				//�����ǵذ��Ϸ����ĸ�����
				drawBtn();
				return;
			}

			// if it is tool level
			//���Ƿ��ڹ���APP����
			//TODO
			if (flag_tool) {
				// draw tools icon
				drawTools();

				// draw back icon
				//���·��ķ��ؼ��Լ��ذ� ��ͼƬAPP�����һ��
				drawBack();
				return;
			}
			if (flag_setting) {
				// draw tools icon
				//drawTools();
				drawSetting();
				// draw back icon
				//���·��ķ��ؼ��Լ��ذ� ��ͼƬAPP�����һ��
				drawBack();
				return;
			}
			if (flag_popular) {
				// draw popular list
				drawpopular();
				// draw back icon
				//���·��ķ��ؼ��Լ��ذ� ��ͼƬAPP�����һ��
				drawBack();
				return;
			}
		}
	}

	public void goNextPage() {
		if (flag_local && !isDeleteClick && !isShowAnimation
				&& currentPage < getTotalPageNum()) {
			startAnimationTime = System.currentTimeMillis();
			isShowAnimation = true;
			//��X�Ḻ����ƽ��
			animationOffset = 4.0F * -appIconWidthWithMargin;
			animationEndOffset = animatedOffset + animationOffset;
		}
	}

	public void goPreviousPage() {
		if (flag_local && !isDeleteClick && !isShowAnimation && currentPage > 1) {
			startAnimationTime = System.currentTimeMillis();
			isShowAnimation = true;
			//��X��������ƽ��
			animationOffset = 4.0F * appIconWidthWithMargin;
			animationEndOffset = animatedOffset + animationOffset;
		}
	}

	public void initTextTexture() {
		//�����Ǳ���APP����ı���Ӧ���ĸ���
		localAppTextureId = Utils.initTexture(
				mContext.getString(R.string.local_app), 24.0f, 210, 60);
		try {
			String[] labels = mContext.getResources().getStringArray(
					R.array.labels);
			labelTextureIds = new int[labels.length];
			//��������ҳAPP���������
			for (int i = 0; i < labels.length; ++i) {
				labelTextureIds[i] = Utils.initTexture(labels[i], 20.0f, 108,
						48);
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		//�����ǹ���APP�������á����ز��š��г�����HIDM�����ļ�������Щ��
		try {
			Resources res = mContext.getResources();
			int toolLabelId = isHDMI ? R.array.label_tools_hdmi
					: R.array.label_tools;
			String[] toolLabels = res.getStringArray(toolLabelId);
			toolsTextureIds = new int[toolLabels.length];
			for (int i = 0; i < toolLabels.length; ++i) {
				toolsTextureIds[i] = Utils.initTexture(toolLabels[i], 16.0f,
						108, 48);
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		//�����ǹ���APP���������APP����Ӧ�õ��������
		try {
			Resources res = mContext.getResources();
			String[] settingLabels = res.getStringArray(R.array.label_tools_setting);
			settingTextureIds = new int[settingLabels.length];
			for (int i = 0; i < settingLabels.length; ++i) {
				settingTextureIds[i] = Utils.initTexture(settingLabels[i], 16.0f,
						108, 48);
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		//�����ǹ���APP���������APP����Ӧ�õ��������
		try {
			Resources res = mContext.getResources();
			String[] settingLabels = {"��","����2","������","2.00.002","δ��¼"};
			settingTextureIds2 = new int[settingLabels.length];
			for (int i = 0; i < settingLabels.length; ++i) {
				settingTextureIds2[i] = Utils.initTexture(settingLabels[i], 16.0f,
						108, 48);
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		//������ͼƬAPP�������Ƭǽ��ȫ����Ƭ��Щ��
		imageLabelTextureIds = new int[2];
		imageLabelTextureIds[0] = Utils.initTexture(mContext.getResources()
				.getString(R.string.photo_wall), 16.0f, 108, 48);
		imageLabelTextureIds[1] = Utils.initTexture((String) mContext
				.getResources().getString(R.string.panorama), 16.0f, 108, 48);
		//��������Ƶ���ź�ȫ����Ƶ������û���õ�
		/*videoLabelTextureIds = new int[2];
		videoLabelTextureIds[0] = Utils.initTexture(mContext.getResources()
				.getString(R.string.normal_video), 20.0f, 108, 48);
		videoLabelTextureIds[1] = Utils.initTexture(mContext.getResources()
				.getString(R.string.panoramic_video), 20.0f, 108, 48);*/
	}

	//TODO
	public void initWorld() {
		GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
		MatrixState.setInitStack();
		anchor = new Anchor(mContext);
		anchor.setUnitSize(0.012f);
		textureBall = new TextureBall(mContext);
		iconTextureRect = new TextureRect(mContext, iconSize, iconSize);
		iconBackTextureRect = new TextureRect(mContext, backIconSize,
				backIconSize);
		iconStateTextureRect = new TextureRect(mContext,
				0.015f + statusIconSize, statusIconSize);
		textRect = new TextureRect(mContext, 2.0f, 0.5f);
		labelRect = new TextureRect(mContext, 1.08f, 0.48f);
		popularRect = new TextureRect(mContext, 5f, 3f);
		popularListRect = new TextureRect(mContext, popularListwidth, popularListheight);
		imageIconRect = new TextureRect(mContext, imageIconSize, imageIconSize);
		localTitleRect = new TextureRect(mContext, 2.0f, 0.5f);
		deleteRect = new TextureRect(mContext, deleteIconSize, deleteIconSize);
		appTextureRect = new TextureRect(mContext, appIconSize, appIconBgHeight);
		dotTextureRect = new TextureRect(mContext, dotIconSize, dotIconSize);
		toolIconRect = new TextureRect(mContext, toolIconSize, toolIconSize);
		settingIconRect = new TextureRect(mContext, settingIconSize, settingIconSize);
		localBtnBg = new Background(mContext, 3.0f, 0.8f);
		statusBtnBg = new Background(mContext, 2.3f, 0.8f);
		//statusBtnBg = new Background(mContext, 2.0f, 0.8f);
		backBtnBg = new TextureRect(mContext, 1.0f, 0.8f);
		textureArc = new TextureArc(mContext);

		setTimeTextureId();

		id_rect_trans_arc = Utils.initTexture((Bitmap) Utils
				.getBitmapRectWithEdge(512, 256,
							getResources().getColor(R.color.bg_trans)));
		backBgTextureId = Utils.initTexture((Bitmap) Utils.getBitmapRect(2, 2,
							getResources().getColor(R.color.bg_back)));
		photoWallTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_photo_wall_default);
		photoWallFocusedTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_photo_wall_focused);
		panoramaTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_panorama_default);
		panoramaFocusedTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_panorama_focused);
		/*//û���õ� 
		videoTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_video_default);
		videoFocusedTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_video_focused);
		//ȫ��OL
		panoramicVideoTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_panoramic_video_default);
		panoramicVideoFocusedTextureId = Utils.initTexture(
				mContext.getResources(), R.drawable.ic_panoramic_video_focused);*/
		//�����ؼ�
		backDefaultTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_back_default);
		backFocusedTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_back_focused);
		backTextureId = backDefaultTextureId;
		//��ɾ����
		deleteRedTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_delete_red);
		deleteIconTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.delete);
		deleteIconFocusedTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.delete_focused);
		//ҳ��ҳ����С���
		dotTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_page_indicator);

		//����ı���ͼƬ
		updateTheme(true);

		//����ҳ��appͼ��
		try {
			TypedArray defIcons = mContext.getResources().obtainTypedArray(
					R.array.icons_default);
			defaultIconIds = new int[defIcons.length()];
			for (int i = 0; i < defIcons.length(); i++) {
				defaultIconIds[i] = Utils.initTexture(mContext.getResources(),
						defIcons.getResourceId(i, 0));
			}
			defIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		//����ҳ��app��ѡ��ʱ���ͼ��
		try {
			TypedArray focusIcons = mContext.getResources().obtainTypedArray(
					R.array.icons_focused);
			focusedIconIds = new int[focusIcons.length()];
			for (int i = 0; i < focusIcons.length(); i++) {
				focusedIconIds[i] = Utils.initTexture(mContext.getResources(),
						focusIcons.getResourceId(i, 0));
			}
			focusIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		isHDMI = Utils.checkApkExist(mContext, "com.kamino.hdmiin");

		//������������ĸ�ͼ�걻ѡ�е�״̬
		try {
			Resources res = mContext.getResources();
			int id = isHDMI ? R.array.tool_icons_focused_hdmi
					: R.array.tool_icons_focused;
			TypedArray toolIcons = res.obtainTypedArray(id);
			toolIconFocusedIds = new int[toolIcons.length()];
			for (int i = 0; i < toolIcons.length(); i++) {
				toolIconFocusedIds[i] = Utils.initTexture(
						mContext.getResources(), toolIcons.getResourceId(i, 0));
			}
			toolIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		//������������ĸ�ͼ��
		try {
			Resources res = mContext.getResources();
			int id = isHDMI ? R.array.tool_icons_default_hdmi
					: R.array.tool_icons_default;
			TypedArray toolIcons = res.obtainTypedArray(id);
			toolIconIds = new int[toolIcons.length()];
			for (int i = 0; i < toolIcons.length(); i++) {
				toolIconIds[i] = Utils.initTexture(mContext.getResources(),
						toolIcons.getResourceId(i, 0));
			}
			toolIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		//��������������ͼ�걻ѡ�е�״̬
		try {
			Resources res = mContext.getResources();
			TypedArray settingIcons = res.obtainTypedArray(R.array.setting_icons_focused);
			String[] settingLabels = {"��","����2","������","2.00.002","δ��¼"};
			String[] settingLabels2 = res.getStringArray(R.array.label_tools_setting);
			settingIconFocusedIds = new int[settingIcons.length()];
			for (int i = 0; i < settingIcons.length(); i++) {
				settingIconFocusedIds[i] = Utils.initTexture(
						mContext.getResources(), settingIcons.getResourceId(i, 0),settingLabels2[i],settingLabels[i],180,250,Color.BLUE);
			}
			settingIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		
		//��������������ͼ��
		try {
			Resources res = mContext.getResources();
			TypedArray settingIcons = res.obtainTypedArray(R.array.setting_icons_default);
			String[] settingLabels = {"��","����2","������","2.00.002","δ��¼"};
			String[] settingLabels2 = res.getStringArray(R.array.label_tools_setting);
			settingIconIds = new int[settingIcons.length()];
			for (int i = 0; i < settingIcons.length(); i++) {
				settingIconIds[i] = Utils.initTexture(mContext.getResources(),
						settingIcons.getResourceId(i, 0),settingLabels2[i],settingLabels[i],180,250,Color.BLACK);
			}
			settingIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		//��ͨ������ı���
		popularTextureId = Utils.initTexture(mContext.getResources(),"ͨ��");
		
		//��ͨ��������б��������ⱻѡ�е�״̬
		try {
			Resources res = mContext.getResources();
			TypedArray popularIcons = res.obtainTypedArray(R.array.popular_icons_default);
			String[] popularLabels1= res.getStringArray(R.array.label_tools_setting_popular);
			String[] popularLabels2 = {"����","����","����","����1","�й���׼ʱ�䣨������"};
			popularlistFocusedIds = new int[popularIcons.length()];
			for (int i = 0; i < popularIcons.length(); i++) {
				popularlistFocusedIds[i] = Utils.initTexture(mContext.getResources(),
						popularIcons.getResourceId(i, 0),popularLabels1[i],popularLabels2[i],Color.BLUE);
			}
			popularIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		//��ͨ��������б���������
		try {
			Resources res = mContext.getResources();
			TypedArray popularIcons = res.obtainTypedArray(R.array.popular_icons_default);
			String[] popularLabels1= res.getStringArray(R.array.label_tools_setting_popular);
			String[] popularLabels2 = {"����","����","����","����1","�й���׼ʱ�䣨������"};
			popularlistIds = new int[popularIcons.length()];
			for (int i = 0; i < popularIcons.length(); i++) {
				popularlistIds[i] = Utils.initTexture(mContext.getResources(),
						popularIcons.getResourceId(i, 0),popularLabels1[i],popularLabels2[i],Color.BLACK);
			}
			popularIcons.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		//���·����ء���һҳ����һҳ��ɾ����
		try {
			TypedArray localBtnDefs = mContext.getResources().obtainTypedArray(
					R.array.local_btn_default);
			localBtnDefaultIcons = new int[localBtnDefs.length()];
			for (int i = 0; i < localBtnDefs.length(); i++) {
				localBtnDefaultIcons[i] = Utils.initTexture(
						mContext.getResources(),
						localBtnDefs.getResourceId(i, 0));
			}
			localBtnDefs.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		//���·����ء���һҳ����һҳ��ɾ������ѡ�е�״̬
		try {
			TypedArray locatBtnFocus = mContext.getResources()
					.obtainTypedArray(R.array.local_btn_focused);
			localBtnFocusedIcons = new int[locatBtnFocus.length()];
			for (int i = 0; i < locatBtnFocus.length(); i++) {
				localBtnFocusedIcons[i] = Utils.initTexture(
						mContext.getResources(),
						locatBtnFocus.getResourceId(i, 0));
			}
			locatBtnFocus.recycle();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		//ͼƬӦ�õ�����ͼƬ
		imageDefaultIconIds = new int[2];
		imageFocusedIconIds = new int[2];
		imageDefaultIconIds[0] = photoWallTextureId;//��Ƭǽ��ID
		imageDefaultIconIds[1] = panoramaTextureId;//ȫ��ͼƬ��ID
		imageFocusedIconIds[0] = photoWallFocusedTextureId;//��Ƭǽ��ѡ�е�ID
		imageFocusedIconIds[1] = panoramaFocusedTextureId;//ȫ��ͼƬ��ѡ�е�ID
		//û���õ���video��ȫ��OL
		/*videoDefaultIconIds = new int[2];
		videoFocusedIconIds = new int[2];
		videoDefaultIconIds[0] = videoTextureId;
		videoDefaultIconIds[1] = panoramicVideoTextureId;
		videoFocusedIconIds[0] = videoFocusedTextureId;
		videoFocusedIconIds[1] = panoramicVideoFocusedTextureId;*/
		//
		wifiOffTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_wifi_off);
		wifi1TextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_wifi_1);
		wifi2TextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_wifi_2);
		wifi3TextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_wifi_3);
		wifi4TextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_wifi_4);
		//
		bluetoothOffTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_bluetooth_off);
		bluetoothOnTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_bluetooth_on);
		bluetoothConnectedTextureId = Utils.initTexture(
				mContext.getResources(), R.drawable.ic_bluetooth_connected);
		//
		sdcardOutTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_sdcard_out);
		sdcardInTextureId = Utils.initTexture(mContext.getResources(),
				R.drawable.ic_sdcard_in);
		//
		statusIdHeadset = Utils.initTexture(mContext.getResources(),
				R.drawable.headset);
		//
		statusIdSound = Utils.initTexture(mContext.getResources(),
				R.drawable.sound);

		//
		statusIconIds[0] = wifiOffTextureId;
		statusIconIds[1] = bluetoothOffTextureId;
		statusIconIds[2] = sdcardOutTextureId;

		initTextTexture();
	}

	public boolean isDrawLocal() {
		return flag_local;
	}
	//TODO
	public void onBackPressed() {
		/*//������ҳ
		if (!flag_index) {
			//ɾ����ťû�б������
			if (!isDeleteClick) {
				//����״̬
				resetStatus();
				//�л�����ҳ
				flag_index = true;
				//�÷��ؼ�����ѡ��
				isBackFocused = false;
				currentPage = 1;
				return;
			} else {
				//�Ѿ�����˾Ͱ����ĳ�û�е����״̬
				isDeleteClick = !isDeleteClick;
			}
		}*/
		if(flag_local){
			if (!isDeleteClick) {
				//����״̬
				resetStatus();
				//�л�����ҳ
				flag_index = true;
				//�÷��ؼ�����ѡ��
				isBackFocused = false;
				currentPage = 1;
				return;
			} else {
				//�Ѿ�����˾Ͱ����ĳ�û�е����״̬
				isDeleteClick = !isDeleteClick;
			}
		}
		if(flag_image || flag_tool){
				//����״̬
				resetStatus();
				//�л�����ҳ
				flag_index = true;
				isBackFocused = false;
				currentPage = 1;
				return;
		}
		if(flag_setting){
			//����״̬
			resetStatus();
			//�л�����ҳ
			flag_tool = true;
			isBackFocused = false;
			currentPage = 1;
			return;
		}
		if(flag_popular){
			//����״̬
			resetStatus();
			//�л�����ҳ
			flag_setting = true;
			isBackFocused = false;
			currentPage = 1;
			return;
		}
	}

	public void onBatteryChanged(int percent) {
		currentPower = percent;
	}

	public void onBatteryPower(boolean charge) {
		isPower = charge;
	}

	public void onBluetoothStateChanged(int state) {
		if (state == 2) {
			statusIconIds[1] = bluetoothConnectedTextureId;
		} else if (state != 12 && state != 1 && state != 0 && state != 3) {
			statusIconIds[1] = bluetoothOffTextureId;
		} else {
			statusIconIds[1] = bluetoothOnTextureId;
		}
	}
	//TODO
	public void onConfirm() {
		//�жϹ���Ƿ��ڷ��ؼ�����
		if (isBackFocused) { 
			//ɾ����ťû�б������
			if (!isDeleteClick) {
				/*//����״̬
				resetStatus();
				//�ص���ҳ
				flag_index = true;
				//���ذ�������ѡ��
				isBackFocused = false;
				//���õ�ǰҳ
				currentPage = 1;
				return;*/
				if(flag_local || flag_image || flag_tool){
					//����״̬
					resetStatus();
					//�ص���ҳ
					flag_index = true;
					//���ذ�������ѡ��
					isBackFocused = false;
					//���õ�ǰҳ
					currentPage = 1;
					return;
				}else{
					if(flag_setting){
						//����״̬
						resetStatus();
						//�ص���ҳ
						flag_tool = true;  
						//���ذ�������ѡ��
						isBackFocused = false;
						//���õ�ǰҳ
						currentPage = 1;
						return;
					}else if(flag_popular){
						//����״̬
						resetStatus();
						//�ص���ҳ
						flag_setting = true;
						//���ذ�������ѡ��
						isBackFocused = false;
						//���õ�ǰҳ
						currentPage = 1;
						return;
					}
				}
			} else {
				//ɾ������ѡ�еĻ�������ĳɲ���ѡ��״̬
				isDeleteClick = !isDeleteClick;
			}
		} else if (flag_local) {
			//����ڵ���+1��ͼ��Ҳ��������������
			if (focusBtnFlags[3]) {
				isDeleteClick = !isDeleteClick;
				return;
			}
			//����ڵ�һ+1��ͼ��Ҳ������һҳ��ͼ������
			if (focusBtnFlags[1]) {
				goPreviousPage();
				return;
			}
			//����ڵڶ�+1��ͼ��Ҳ������һҳ��ͼ������
			if (focusBtnFlags[2]) {
				goNextPage();
				return;
			}
			//ɾ����ť�Ѿ��������
			if (isDeleteClick) {
				//���ҵ���С���
				if (currentFocused != -1 && appList.size() > 0) {
					//ж�س���
					Utils.uninstallApk(mContext, appList.get(currentFocused)
							.getPackageName());
					return;
				}
			} else if (!isDeleteClick && !isShowAnimation) {
				//�������һҳ��Ӱ
				if (currentLookPosition == 2) {
					if (1 + currentPage <= getTotalPageNum()) {
						isShowAnimation = true;
						startAnimationTime = System.currentTimeMillis();
						animationOffset = 4.0F * -appIconWidthWithMargin;
						animationEndOffset = animatedOffset + animationOffset;
						return;
					}
				//�������һҳ��Ӱ
				} else if (currentLookPosition == 0) {
					if (currentPage != 1) {
						startAnimationTime = System.currentTimeMillis();
						isShowAnimation = true;
						animationOffset = 4.0F * appIconWidthWithMargin;
						animationEndOffset = animatedOffset + animationOffset;
						return;
					}
				//����ڱ�ҳ�� APP��ѡ����
				} else if (currentPosition != -1 && appList.size() > 0
						&& getPageIndex(currentPosition) == currentPage) {
					AppData app = appList.get(currentPosition);
					//����APP
					Utils.startApp(mContext, app.getPackageName());
					return;
				}
			}
		} else {
			//����ҳ
			if (flag_index) {
				//����ҳ����currentPosition��ʾ6��Ӧ�õĵ�ǰλ��
				switch (currentPosition) {
				case 0:
					resetStatus();
					flag_local = true;
					return;
				case 1:
					//û��com.kamino.pptv���Ӧ�� ��������
//					Utils.startApp(mContext, "com.kamino.pptv");
					Utils.startApp(mContext, "com.actions.vrplayer");
					return;  
				case 2:
					//û��com.kamino.vrmedia360���Ӧ�� ��������
//					Utils.startApp(mContext, "com.kamino.vrmedia360");
					Utils.startApp(mContext, "com.android.browser");
					return;
				case 5:
					//û��com.kamino.vrlive���Ӧ�� ��������
					//Utils.startApp(mContext, "com.kamino.vrlive");
					//Utils.startApp(mContext, "android.intent.action.SEARCH");
					return;
				case 3:
					resetStatus();
					flag_image = true;
					return;
				case 4:
					resetStatus();
					flag_tool = true;
					return;
				default:
					return;
				}
			}
			//TODO
			//�ڹ���APP����
			if (flag_tool) {
				//����APP�����currentPosition��ʾ�����ĸ�ͼ��ĵ�ǰλ��
				switch (currentPosition) {
				case 0:
					//û��com.kamino.vrlive���Ӧ�� ��������
//					Utils.startApp(mContext, "com.kamino.settings");
					resetStatus();
					flag_setting = true;
					return;
				case 1:
					//û��com.kamino.vrlive���Ӧ�� ��������
					Utils.startApp(mContext, "com.kamino.player");
					return;
				case 2:
					//û��com.kamino.vrlive���Ӧ�� ��������
					Utils.startApp(mContext, "com.kamino.store");
					return;
					//Ҫ����HDMI��ڵĻ�case3��ʾ����HDMI û�оͱ�ʾ�ļ�����
				case 3:
					if (isHDMI) {
						//û��com.kamino.vrlive���Ӧ�� ��������
						Utils.startApp(mContext, "com.kamino.hdmiin");
						return;
					}
					//û��com.kamino.filemanager���Ӧ�� ��������
					Utils.startApp(mContext, "com.kamino.filemanager");
					return;
				case 4:
					//û��com.kamino.filemanager���Ӧ�� ��������
					Utils.startApp(mContext, "com.kamino.filemanager");
					return;
				default:
					return;
				}
			}
			//TODO
			//��ͼƬAPP����
			if (flag_image) {
				//��ͼƬAPP����currentPosition��ʾ��������ͼ��ĵ�ǰλ��
				if (currentPosition == 0) {
					//û��com.kamino.gallery.MAIN���Ӧ�� ��������
					Utils.startAction(mContext, "com.kamino.gallery.MAIN");
					return;
				}

				if (currentPosition == 1) {
					//û��com.kamino.gallery360.MAIN���Ӧ�� ��������
					Utils.startAction(mContext, "com.kamino.gallery360.MAIN");
					return;
				}
			}
			//������APP����
			if(flag_setting){
				switch (currentPosition) {
				case 0:
					
					break;
				case 1:
					resetStatus();
					flag_popular = true;
					break;
				case 2:

					break;
				case 3:

					break;
				case 4:
					
					break;
				default:
					break;
				}
			}
		}
	}

	public void onHeadsetStateChanged(boolean headset) {
		isNeedHeadset = headset;
	}

	public void onNetworkStateChanged(int type, int state, int rssi) {
		if (type == 1 && state == 0) {
			if (rssi == 0) {
				statusIconIds[0] = wifi1TextureId;
			} else if (rssi == 1) {
				statusIconIds[0] = wifi2TextureId;
			} else if (rssi == 2) {
				statusIconIds[0] = wifi3TextureId;
			} else if (rssi == 3) {
				statusIconIds[0] = wifi4TextureId;
			}
		} else {
			statusIconIds[0] = wifiOffTextureId;
		}
	}

	public void onResume() {
		super.onResume();
		mNeedUpdate = true;
	}

	public void onSoundStateChanged(boolean sound) {
		isNeedSound = sound;
	}

	public void onTFStateChanged(boolean plugin) {
		if (plugin) {
			statusIconIds[2] = sdcardInTextureId;
		} else {
			statusIconIds[2] = sdcardOutTextureId;
		}
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
			int[] textures = new int[] { timeTextureId };
			GLES20.glDeleteTextures(1, textures, 0);
			//���Ϸ���ʱ�䡢����
			lastTime = timeText;
			lastPower = currentPower;
			lastIsPower = isPower;
			timeTextureId = Utils.initTexture(Utils.generateWLT(currentPower,
					210, 60, power, isPower, timeText));
		}
	}

	public void updateHeadView(HeadTransform head) {
		if (isSensorMode()) {
			head.getHeadView(headView, 0);
			// add rotaion for x/y
			Matrix.multiplyMM(headView, 0, mXRotationMatrix, 0, headView, 0);
			Matrix.multiplyMM(headView, 0, mYRotationMatrix, 0, headView, 0);
		} else {
			boolean needUpdate = false;
			float minDiff = 0.2F;
			float xdiff = mXAngle - mTargetXAngle;
			float ydiff = mYAngle - mTargetYAngle;

			if (Math.abs(xdiff) > minDiff) {
				mXAngle -= (xdiff / 30);
				needUpdate = true;
			}

			if (Math.abs(ydiff) > minDiff) {
				mYAngle -= (ydiff / 30);
				needUpdate = true;
			}

			if (needUpdate) {
				// set rotation matrix
				Matrix.setRotateM(mXRotationMatrix, 0, mYAngle, 1.0f, 0f, 0);
				Matrix.setRotateM(mYRotationMatrix, 0, mXAngle, 0, 1.0f, 0);

				Matrix.multiplyMM(headView, 0, mYRotationMatrix, 0,
						mXRotationMatrix, 0);
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		// Log.d("onTouchEvent", e.toString());

		// ignore touch event in sensor mode
		if (isSensorMode()) {
			return super.onTouchEvent(e);
		}

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsDragMode = false;
			// stop animation
			mTargetXAngle = mXAngle;
			mTargetYAngle = mYAngle;
			break;

		case MotionEvent.ACTION_UP:
			if (!mIsDragMode) {
				onConfirm();
			}
			break;

		case MotionEvent.ACTION_MOVE:
			mIsDragMode = true;

			float dx = x - mPreviousX;
			float dy = y - mPreviousY;

			mTargetXAngle += dx * TOUCH_SCALE_FACTOR;
			mTargetYAngle += dy * TOUCH_SCALE_FACTOR;
			break;
		}

		mPreviousX = x;
		mPreviousY = y;

		return super.onTouchEvent(e);
	}

	// enable or disable sensor mode
	// default: enable
	public void enableSensorMode(boolean enable) {
		mSensorMode = enable;
	}

	public boolean isSensorMode() {
		return mSensorMode;
	}
	//TODO
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
			MatrixState.setProjectFrustum(-ratio, ratio, -1.0F, 1.0F, Z_NEAR,
					Z_FAR);
		}

		public void onSurfaceCreated(EGLConfig gl) {
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			//�������ͼ��Ĵ�С����Դ
			initWorld();
		}

		@Override
		public void onDrawEye(Eye eye) {
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
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
					| GLES20.GL_COLOR_BUFFER_BIT);
			updateHeadView(head);
			if (mNeedUpdate) {
				updateLanguage();
				updateTheme(false);
				mNeedUpdate = false;
			}

			if (flag_index) {
				setTimeTextureId();
			}
		}
	}
}
