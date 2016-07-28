package com.fxtv.framework.system;

import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.model.Cache;
import com.fxtv.framework.orm.DatabaseHelperFramework;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * http缓存系统
 * 
 * @author FXTV-Android
 * 
 */
public class SystemHttpCache extends BaseSystem {
	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void destroy() {
		super.destroy();
	}

	/**
	 * 读取缓存
	 * 
	 * @return
	 */
	public Cache getCache(String key) {
		Cache cache = null;
		try {
			Dao dao = DatabaseHelperFramework.getHelper(mContext).getDao(Cache.class);
			cache = (Cache) dao.queryForId(key);
		} catch (SQLException e) {
			e.printStackTrace();
//			throw new RuntimeException("getCache error");
		}
		return cache;
	}

	/**
	 * key更新缓存
	 * 
	 * @param key
	 * @param value
	 */
	public void updateCache(String key, String value) {
		Cache cache = new Cache(key, value);
		try {
			Dao dao = DatabaseHelperFramework.getHelper(mContext).getDao(Cache.class);
			dao.createOrUpdate(cache);
		} catch (SQLException e) {
			e.printStackTrace();
//			throw new RuntimeException("updateCache Error");
		}
	}
}
