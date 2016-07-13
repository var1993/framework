package com.fxtv.framework.system;

import com.fxtv.framework.frame.BaseSystem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SystemManager {
	private static SystemManager mInstance;
	private HashMap<String, BaseSystem> mSystemPool = new HashMap<>();

	private SystemManager() {
	}

	public static SystemManager getInstance() {
		if (mInstance == null) {
			synchronized (SystemManager.class) {
				mInstance = new SystemManager();
			}
		}
		return mInstance;
	}

	public void destroySystem(String className) {
		BaseSystem baseSystem;
		if ((baseSystem = mSystemPool.get(className)) != null) {
			baseSystem.destroySystem();
			mSystemPool.remove(baseSystem);
		}
	}

	public void destroyAllSystem() {
		Iterator<Entry<String, BaseSystem>> iterator = mSystemPool.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, BaseSystem> next = iterator.next();
			next.getValue().destroySystem();
		}
		mSystemPool.clear();
	}

	public <T extends BaseSystem> T getSystem(Class<T> className) {
		if(className==null){
			return null;
		}
		T instance= (T) mSystemPool.get(className.getName());
		if(instance==null){
			try {
				instance=className.newInstance();
				instance.createSystem();
//				className.getMethod("createSystem").invoke(instance);
				mSystemPool.put(className.getName(),instance);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		return instance;
	}
}
