package com.fxtv.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.R;

/**
 * Created by wzh on 2016/3/7.
 * 0 view 自定义内容view
 * 1 view 状态栏
 * 2 view toolbar
 */
public class ToolBarHelperView extends FrameLayout {

    private String TAG = "ToolBarHelperView";
    /*toolbar*/
    private Toolbar mToolBar;
    private int mToolBarHeight;
    /*
   * 1、toolbar是否悬浮在窗口之上
   * 2、toolbar的高度获取
   * 3、是否透明状态栏
   * */
    private static int[] ATTRS = {
            android.R.attr.windowActionBarOverlay,
            android.R.attr.actionBarSize,
            android.R.attr.windowTranslucentStatus,
            android.R.attr.colorPrimaryDark
    };
    private View toolbarView;
    private View statusView;

    public ToolBarHelperView(Context context) {
        super(context);
    }

    public ToolBarHelperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBarHelperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addRootView(View rootView) {
        /*初始化整个内容*/
        initContentView();

        TypedArray array = getContext().obtainStyledAttributes(ATTRS); /*获取主题中定义的悬浮标志*/
        /*初始化toolbar*/
        initToolBar(array);
        /*初始化用户定义的布局*/
        initUserView(rootView, array);
        array.recycle();
    }

    private void initContentView() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);

    }

    private void initToolBar(TypedArray typedArray) {
        toolbarView = LayoutInflater.from(getContext()).inflate(R.layout.view_toolbar, this, false);
        mToolBar = (Toolbar) toolbarView.findViewById(R.id.toolbar);

        changeStatusHeight(mToolBar, typedArray);

    }

    private void initUserView(View rootView, TypedArray typedArray) {
        ViewGroup.LayoutParams oldParams = rootView.getLayoutParams();
        int width, height;
        if (oldParams != null) {
            width = oldParams.width;
            height = oldParams.height;
        } else {
            width = LayoutParams.MATCH_PARENT;
            height = LayoutParams.MATCH_PARENT;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);

        boolean overly = typedArray.getBoolean(0, false);

        /*获取主题中定义的toolbar的高度*/
        if (mToolBarHeight <= 0) {
            mToolBarHeight = (int) typedArray.getDimension(1, 100);
        }
        // Logger.d(TAG," overly="+overly+" toolBarSize="+mToolBarHeight +" "+getContext().getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
        /*如果是悬浮状态 或者toolbar隐藏时，则不需要设置间距*/
        params.topMargin = overly || toolbarView.getVisibility() == View.GONE ? 0 : mToolBarHeight;
        addView(rootView, params);

        //先添加rootview,再添加toolbar
        if (statusView != null) {
            addView(statusView);
        }
        addView(toolbarView, toolbarView.getLayoutParams());
    }

    /**
     * 改变高度,加上status高
     *
     * @param toolbar
     */
    private void changeStatusHeight(Toolbar toolbar, TypedArray array) {
        //sdk 19以上支持 是否设置为透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && array.getBoolean(2, false)) {
            FrameLayout.LayoutParams params;
            if (toolbar != null && (params = (LayoutParams) toolbar.getLayoutParams()) != null) {

                int StatusBarHeight = FrameworkUtils.getStatusBarHeight(getContext());
                //创建一个状态栏高的view
                statusView = new View(getContext());
               /* int color=getResources().getColor(R.color.color_primary_dark);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    color=array.getColor(3,color);
                }*/
//                StatusBarHeight=(int)((double)StatusBarHeight*4/5);//原状态栏的4/5高
                statusView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarHeight));
                statusView.setBackgroundResource(R.drawable.status_background);

                if (params.height > 0) {
                    params.topMargin += StatusBarHeight;
                }
                mToolBarHeight = params.height + StatusBarHeight;

            }
        }
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }

    public View getStatusView() {
        return statusView;
    }
}


