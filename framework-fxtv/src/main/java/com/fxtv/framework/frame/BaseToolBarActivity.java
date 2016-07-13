package com.fxtv.framework.frame;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.fxtv.framework.R;
import com.fxtv.framework.system.SystemAnalytics;
import com.fxtv.framework.system.SystemCrashLogCollect;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemManager;
import com.fxtv.framework.system.SystemPage;
import com.fxtv.framework.system.SystemPush;
import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.widget.ToolBarHelperView;

import java.io.Serializable;

/**
 * Created by wzh on 2016/3/2.
 */
public abstract class BaseToolBarActivity extends AppCompatActivity{

    protected SystemManager mSystemManager;
    protected LayoutInflater mLayoutInflater;
    protected Bundle baseSavedInstance;

    private String TAG="BaseToolBarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseSavedInstance=savedInstanceState;
        if(baseSavedInstance==null){
            baseSavedInstance=getIntent().getExtras();
        }

        mLayoutInflater = LayoutInflater.from(this);
        mSystemManager = SystemManager.getInstance();
        mSystemManager.getSystem(SystemPage.class).addActivity(this);
        mSystemManager.getSystem(SystemCrashLogCollect.class).activityOnCreate();
        mSystemManager.getSystem(SystemPush.class).activityOnCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isTransNavigation()){
            //透明底部导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
            addToolBar(LayoutInflater.from(this).inflate(layoutResID,null));
    }

    private void addToolBar(View rootView){
        ToolBarHelperView toolBarHelper=new ToolBarHelperView(this);
        toolBarHelper.addRootView(rootView);
        setContentView(toolBarHelper);

        Toolbar toolbar=toolBarHelper.getToolBar();
        if(toolbar!=null){
            TextView tool_title= (TextView) toolbar.findViewById(R.id.tool_title);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);//禁用tool的Title

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backClick(v);
                }
            });
            String title=initToolBar(toolbar);
            if(!TextUtils.isEmpty(title)){
                tool_title.setText(title);
                tool_title.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSystemManager.getSystem(SystemAnalytics.class).activityResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSystemManager.getSystem(SystemAnalytics.class).activityPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLayoutInflater = null;
        mSystemManager.getSystem(SystemHttp.class).cancelRequest(this);
        mSystemManager.getSystem(SystemPage.class).finishActivity(this);
        mSystemManager.getSystem(SystemFragmentManager.class).destroyFragmentManager(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(baseSavedInstance!=null){
            outState.putAll(baseSavedInstance);
        }
    }
    public void showToast(String msg) {
        FrameworkUtils.showToast(msg);
    }

    protected String getStringExtra(String key){
        return baseSavedInstance==null?null:baseSavedInstance.getString(key);
    }
    protected Serializable getSerializable(String key){
        return baseSavedInstance==null?null:baseSavedInstance.getSerializable(key);
    }

    protected <T extends BaseSystem> T getSystem(Class<T> className) {
        return SystemManager.getInstance().getSystem(className);
    }

    /**
     * 返回点击事件,如果要自定义事件，请重写此方法
     */
    public void backClick(View v){
        finish();
    }


    /**
     * 对ToolBar封装,请返回Title，没有return null
     * @param toolbar
     * @return
     */
    public abstract String initToolBar(Toolbar toolbar);

    /**
     * 可重写此方法控制 是否透明虚拟按键
     * @return
     */
    public boolean isTransNavigation(){
        return false;
    }
}
