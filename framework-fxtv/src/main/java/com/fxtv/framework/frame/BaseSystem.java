package com.fxtv.framework.frame;

import android.content.Context;

import com.fxtv.framework.Profile;
import com.fxtv.framework.frame.interfaces.ISystem;
import com.fxtv.framework.system.SystemManager;

public class BaseSystem implements ISystem {
	protected Context mContext;

	//不能修改此public void createSystem()方法，在fxtv/proguard-rules.pro将不混淆此方法
	@Override
	public void createSystem() {
		mContext = Profile.mContext;
		init();
	}

	@Override
	public void destroySystem() {
		destroy();
		mContext = null;
	}

	protected void init(){
	}
	
	protected void destroy(){
	}

	//SystemFrameworkConfig
	protected <T extends BaseSystem> T getSystem(Class<T> className) {
		return SystemManager.getInstance().getSystem(className);
	}

}
