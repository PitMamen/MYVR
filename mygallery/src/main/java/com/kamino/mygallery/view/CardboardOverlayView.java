package com.kamino.mygallery.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CardboardOverlayView extends LinearLayout {
	private final CardboardOverlayEyeView leftView;
	private final CardboardOverlayEyeView rightView;
	private AlphaAnimation textFadeAnimation;
	private Toast toast;

	public CardboardOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams layout = new LayoutParams(-1, -1, 1.0F);
		layout.setMargins(0, 0, 0, 0);
		leftView = new CardboardOverlayEyeView(context, attrs);
		leftView.setLayoutParams(layout);
		addView(leftView);
		rightView = new CardboardOverlayEyeView(context, attrs);
		rightView.setLayoutParams(layout);
		addView(rightView);
		setDepthOffset(0.01F);
		setColor(Color.rgb(150, 255, 180));
		setVisibility(VISIBLE);
		textFadeAnimation = new AlphaAnimation(1.0F, 0.0F);
		textFadeAnimation.setDuration(2000L);
		toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
	}

	private void setColor(int color) {
		leftView.setColor(color);
		rightView.setColor(color);
	}

	private void setDepthOffset(float offset) {
		leftView.setOffset(offset);
		rightView.setOffset(-offset);
	}

	private void setText(String text) {
		leftView.setText(text);
		rightView.setText(text);
	}

	private void setTextAlpha(float alpha) {
		leftView.setTextViewAlpha(alpha);
		rightView.setTextViewAlpha(alpha);
	}

	public void show3DToast(String text) {
		setText(text);
		setTextAlpha(1.0F);
		textFadeAnimation.setAnimationListener(new EndAnimationListener() {
			public void onAnimationEnd(Animation a) {
				setTextAlpha(0.0F);
			}
		});
		startAnimation(textFadeAnimation);
	}

	public void showToast(String text) {
		toast.setText(text);
		toast.show();
	}

	private class CardboardOverlayEyeView extends ViewGroup {
		private final ImageView imageView;
		private float offset;
		private final TextView textView;

		public CardboardOverlayEyeView(Context context, AttributeSet attrs) {
			super(context, attrs);
			imageView = new ImageView(context, attrs);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			imageView.setAdjustViewBounds(true);
			addView(imageView);
			textView = new TextView(context, attrs);
			textView.setTextSize(1, 14.0F);
			textView.setTypeface(textView.getTypeface(), 1);
			textView.setGravity(17);
			textView.setShadowLayer(3.0F, 0.0F, 0.0F, -12303292);
			addView(textView);
		}

		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			int w = r - l;
			int h = b - t;
			float xoff = offset;
			if (w > 1000) {
				xoff = 3.8F * offset;
			}

			float il = (float) ((int) ((float) w * (0.45F + xoff)));
			float it = (float) ((int) ((float) h * (-0.07F + 0.45F)));
			imageView.layout((int) il, (int) it, (int) (il + 0.1F * (float) w),
					(int) (it + 0.1F * (float) h));
			float tl = xoff * (float) w;
			float tt = 0.52F * (float) h;
			textView.layout((int) tl, (int) tt, (int) (tl + (float) w),
					(int) (tt + 0.48000002F * (float) h));
		}

		public void setColor(int color) {
			imageView.setColorFilter(color);
			textView.setTextColor(color);
		}

		public void setOffset(float off) {
			offset = off;
		}

		public void setText(String text) {
			textView.setText(text);
		}

		@SuppressLint({ "NewApi" })
		public void setTextViewAlpha(float alpha) {
			textView.setAlpha(alpha);
		}
	}

	private abstract class EndAnimationListener implements AnimationListener {
		private EndAnimationListener() {
		}

		public void onAnimationRepeat(Animation a) {
		}

		public void onAnimationStart(Animation a) {
		}
	}
}
