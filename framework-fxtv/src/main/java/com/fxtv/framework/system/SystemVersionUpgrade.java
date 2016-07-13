package com.fxtv.framework.system;

import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.component.ComponentUpgradeApk;
import com.fxtv.framework.model.RequestData;

/**
 * app版本升级系统
 * 
 * @author FXTV-Android
 * 
 */
public class SystemVersionUpgrade extends BaseSystem {
	private static final String TAG = "SystemVersionUpgrade";

	private ComponentUpgradeApk mComponentUpgradeApk;

	@Override
	protected void init() {
		super.init();
		mComponentUpgradeApk = new ComponentUpgradeApk(mContext, SystemManager.getInstance()
				.getSystem(SystemFrameworkConfig.class).mCacheDir);
	}

	@Override
	protected void destroy() {
		super.destroy();
		if (mComponentUpgradeApk != null) {
			mComponentUpgradeApk = null;
		}
	}

	public void checkApkUpdate(final RequestData requestData, final IApkUpgradeCallBack callBack) {
		mComponentUpgradeApk.checkApkUpdate(requestData, callBack);
	}

	public void upgradeApk() {
		mComponentUpgradeApk.upgradeApk();
	}

	public interface IApkUpgradeCallBack {
		void onResult(boolean shouldUpgrade, boolean compulsive);

		void onError(String msg);
	}
}
