package com.fxtv.framework.system;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.utils.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * @author FXTV-Android
 */
public class SystemFragmentManager extends BaseSystem {
    private static final String TAG = "SystemFragmentManager";
    private HashMap<Context, FragmentManager> mFragmentManagerPool;

    @Override
    protected void init() {
        super.init();
        mFragmentManagerPool = new HashMap<>();
    }

    @Override
    protected void destroy() {
        super.destroy();
        mFragmentManagerPool.clear();
        mFragmentManagerPool = null;
    }

    public void destroyFragmentManager(Context context) {
        mFragmentManagerPool.remove(context);
    }

    public <F extends Fragment> F showFragment(final Activity activity, final int containerViewId, final Class<F> fragmentClass, final Bundle bundle) {
        return showFragmentWithAnim(activity, containerViewId, fragmentClass, false, bundle, 0);
    }

    public <F extends Fragment> F showFragment(final Activity activity, final int containerViewId, final Class<F> fragmentClass, boolean newInstance, final Bundle bundle) {
        return showFragmentWithAnim(activity, containerViewId, fragmentClass, newInstance, bundle, 0);
    }

    // 解决ViewPager中使用该系统时，FragmentManager不一样导致的bug
    public <F extends Fragment> F showFragment(final FragmentManager manager, final int containerViewId, final Class<F> fragmentClass, final Bundle bundle) {
        return showFragmentWithAnim(manager, containerViewId, fragmentClass, false, bundle, 0);
    }

    public <F extends Fragment> F showFragmentWithAnim(final Activity activity, final int containerViewId, final Class<F> fragmentClass, final boolean newInstance, final Bundle bundle, final int anim) {
        return showFragmentWithAnim(getFragmentManager(activity), containerViewId, fragmentClass, newInstance, bundle, anim);
    }

    public <F extends Fragment> F showFragmentWithAnim(final FragmentManager fManager, final int containerViewId, final Class<F> fragmentClass, final boolean newInstance, final Bundle bundle, final int anim) {
        Logger.d(TAG, "showFragmentWithAnim");
        FragmentTransaction transaction = null;
        Fragment fragment = null;
        try {
            transaction = fManager.beginTransaction();
            if (anim != 0) {//是否有进入动画
                transaction.setCustomAnimations(anim, 0);
            }
            hideAllFragmentInContainerWithFm(transaction, containerViewId, fManager);

            if (newInstance) {
                fragment = addNewFragment(transaction, containerViewId, fragmentClass, bundle);
            } else {
                String className = fragmentClass.getName();
                fragment = fManager.findFragmentByTag(className);
                if (fragment == null) {
                    fragment = addNewFragment(transaction, containerViewId, fragmentClass, bundle);
                } else {
                    transaction.show(fragment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "showFragmentWithAnim,e=" + e.getMessage());
        } finally {
            if (transaction != null) {
                transaction.commitAllowingStateLoss();
            }
        }
        return (F) fragment;
    }

    /**
     * 替换fragment,如果该容器中有其他fragment,则remove(fragment缓存对象也会删除)
     *
     * @param activity
     * @param containerViewId
     * @param bundle
     */
    public <F extends Fragment> F replaceFragment(final Activity activity, final int containerViewId, final Class<F> fragmentClass, final Bundle bundle) {
        Logger.d(TAG, "showFragmentIfExistHide");
        return replaceFragmentWithAnim(activity, containerViewId, fragmentClass, false, bundle, 0);
    }

    /**
     * 替换fragment,如果该容器中有其他fragment,则remove(fragment缓存对象也会删除)
     *
     * @param activity
     * @param containerViewId
     * @param bundle
     */
    public <F extends Fragment> F replaceFragmentWithAnim(final Activity activity, final int containerViewId, final Class<F> fragmentClass, final boolean newInstance, final Bundle bundle, final int anim) {
        Logger.d(TAG, "replaceFragmentWithAnim");
        Fragment fragment = null;
        try {
            FragmentManager fragmentManager = getFragmentManager(activity);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (anim != 0) {
                transaction.setCustomAnimations(anim, 0);
            }
            if (newInstance) {
                fragment = newFragment(fragmentClass, bundle);
                transaction.replace(containerViewId, fragment, fragmentClass.getName()).commitAllowingStateLoss();
            } else {
                String className = fragmentClass.getName();
                fragment = fragmentManager.findFragmentByTag(className);//找到当前想要的Fragment
                if (fragment != null) {
                    Fragment cFragment = fragmentManager.findFragmentById(containerViewId);
                    if (cFragment == fragment) {
                    } else {
                        transaction.replace(containerViewId, fragment, className);
                        transaction.commitAllowingStateLoss();
                    }
                } else {
                    fragment = newFragment(fragmentClass, bundle);
                    transaction.replace(containerViewId, fragment, className);
                    transaction.commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "showFragmentIfExistHide,e=" + e.getMessage());
        }
        return (F) fragment;
    }

    /**
     * 添加新的Fragment到指定containerViewId,需要手动commit();
     *
     * @param transaction
     * @param containerViewId
     * @param fragmentClass
     * @param bundle
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <F extends Fragment> F addNewFragment(FragmentTransaction transaction, int containerViewId, Class<F> fragmentClass, Bundle bundle) throws InstantiationException, IllegalAccessException {
        F fragment = newFragment(fragmentClass, bundle);
        transaction.add(containerViewId, fragment, fragmentClass.getName());
        return fragment;
    }

    private <F extends Fragment> F newFragment(Class<F> fragmentClass, Bundle bundle) throws IllegalAccessException, InstantiationException {
        F fragment = fragmentClass.newInstance();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public void hideFragment(Activity activity, final Class<? extends Fragment> fragmentClass) {
        hideFragmentWithAnim(activity, fragmentClass, 0);
    }

    public void hideFragmentWithAnim(final Activity activity, final Class<? extends Fragment> fragmentClass, final int anim) {
        Logger.d(TAG, "hideFragmentWithAnim,fragmentName=" + fragmentClass);
        FragmentManager manager = getFragmentManager(activity);
        Fragment fragment = manager.findFragmentByTag(fragmentClass.getName());
        if (fragment != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            if (anim != 0) {
                transaction.setCustomAnimations(0, anim);
            }
            transaction.hide(fragment);
            transaction.commitAllowingStateLoss();
        } else {
            Logger.e(TAG, "hideFragmentWithAnim,the fragment is null");
        }
    }

    private FragmentManager getFragmentManager(Context context) {
        FragmentManager fragmentManager = null;
        if ((fragmentManager = mFragmentManagerPool.get(context)) != null && !fragmentManager.isDestroyed()) {
            return fragmentManager;
        } else {
            return createFragmentManager(context);
        }
    }

    private FragmentManager createFragmentManager(Context context) {
        FragmentManager fragmentManager = null;
        fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        mFragmentManagerPool.put(context, fragmentManager);
        return fragmentManager;
    }

//    private Fragment getCurrentFragment(int containerViewId, FragmentManager fragmentManager) {
//        List<Fragment> fragments = fragmentManager.getFragments();
//        if (fragments != null && fragments.size() != 0) {
//            for (Fragment fragment : fragments) {
//                if (fragment != null && !fragment.isHidden() && fragment.getId() == containerViewId)
//                    return fragment;
//            }
//        }
//        return null;
//    }

    private void hideAllFragmentInContainerWithFm(FragmentTransaction transaction, int containerViewId, FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && !fragment.isHidden() && fragment.getId() == containerViewId) {
                    transaction.hide(fragment);
                }
            }
        }
    }

    public void removeFragment(Context context, Class<? extends Fragment> fragmentClass, int anim) {
        FragmentManager manager = getFragmentManager(context);
        Fragment fragment = manager.findFragmentByTag(fragmentClass.getName());
        if (fragment != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            if (anim != 0) {
                transaction.setCustomAnimations(0, anim);
            }
            transaction.remove(fragment);
            transaction.commitAllowingStateLoss();
        } else {
            Logger.e(TAG, "hideFragmentWithAnim,the fragment is null");
        }
    }
}
