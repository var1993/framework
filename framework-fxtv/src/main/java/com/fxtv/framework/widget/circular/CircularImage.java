package com.fxtv.framework.widget.circular;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fxtv.framework.R;
import com.fxtv.framework.utils.FrameworkUtils;

public class CircularImage extends MaskedImage {
	private Paint mBorderPaint;

	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

	private int mBorderColor = DEFAULT_BORDER_COLOR;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;

	private Bitmap mSkinBitmap;
	/**
	 * 设置显示红点
	 * @param value
     */
	public void setRedCircle(boolean value) {
		redCircle = value;
		invalidate();
	}

	private boolean redCircle = false;

	public CircularImage(Context paramContext) {
		this(paramContext,null);
	}

	public CircularImage(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet,0);
	}

	public CircularImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);

		TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CircularImage, paramInt, 0);
		mBorderWidth = a.getDimensionPixelSize(R.styleable.CircularImage_civ_border_width, DEFAULT_BORDER_WIDTH);
		mBorderColor = a.getColor(R.styleable.CircularImage_civ_border_color, DEFAULT_BORDER_COLOR);
		a.recycle();

		init();
	}
	private void init() {
		mBorderPaint = new Paint();
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);
		mBorderPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
		if (redCircle) {
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paramCanvas.drawCircle(getWidth() - 10, 10, 10, paint);
		}
		if(mBorderWidth>0){
			int radius=Math.min(getWidth(),getHeight());
			paramCanvas.drawCircle(radius/2,radius/2,(radius-mBorderWidth)/2,mBorderPaint);
		}

		if(mSkinBitmap!=null){//绘制皮肤
			int bW=mSkinBitmap.getWidth();
			int bH=mSkinBitmap.getHeight();
			if (bW>0 && bH>0 ) {
				if(bW != getWidth() || bH != getHeight()){//大小不等，强制拉伸
					Bitmap scaledBit = Bitmap.createScaledBitmap(mSkinBitmap, getWidth(), getHeight(), false);
					if (scaledBit != null) {
						this.mSkinBitmap = scaledBit;
					}
				}
				paramCanvas.drawBitmap(mSkinBitmap,0,0,null);
			}
		}
	}

	public Bitmap createMask() {
		int i = getWidth();
		int j = getHeight();
		Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
		Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);
		Canvas localCanvas = new Canvas(localBitmap);
		Paint localPaint = new Paint(1);
		localPaint.setColor(Color.BLACK);
		float f1 = getWidth();
		float f2 = getHeight();
		RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);
		localCanvas.drawOval(localRectF, localPaint);

		return localBitmap;
	}

	public void setSkinUri(String imgUri){
		if(!FrameworkUtils.isHttpUri(imgUri)){
			setSkinBitmap(null);
		}
		Glide.with(getContext()).load(imgUri).asBitmap().into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
				setSkinBitmap(resource);
			}
		}); //方法中设置asBitmap可以设置回调类型
	}

	/**
	 *    设置皮肤的Bitmap
	 */
	public void setSkinBitmap(Bitmap skinBitmap){
		if (mSkinBitmap == skinBitmap) {
			return;
		}
		this.mSkinBitmap=skinBitmap;
		postInvalidate();
	}

	public void setBorderColor(@ColorInt int borderColor) {
		if (borderColor == mBorderColor) {
			return;
		}
		mBorderColor = borderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}


	public int getBorderColor() {
		return mBorderColor;
	}


	public int getBorderWidth() {
		return mBorderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		if (borderWidth == mBorderWidth) {
			return;
		}
		mBorderWidth = borderWidth;
		mBorderPaint.setStrokeWidth(borderWidth);
		invalidate();
	}

}
