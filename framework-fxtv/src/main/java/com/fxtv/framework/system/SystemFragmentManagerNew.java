package com.fxtv.framework.system;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.fxtv.framework.R;
import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.utils.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * @author FXTV-Android
 */
public class SystemFragmentManagerNew extends BaseSystem {
    private static final String TAG = "SystemFragmentManager";
    private HashMap<Context, FragmentManager> mFragmentManagerPool;

    @Override
    protected void init() {
        super.init();
        mFragmentManagerPool = new HashMap<Context, FragmentManager>();
    }

    @Override
    protected void destroy() {
        super.destroy();
        mFragmentManagerPool.clear();
        mFragmentManagerPool = null;
    }

    public Fragment getFragmentByName(Context context, String fragmentName) {
        FragmentManager manager = getFragmentManager(context);
        Fragment fragment = manager.findFragmentByTag(fragmentName);
        return fragment;
    }

    public void hideFragment(Context context, String fragmentName) {
        FragmentManager manager = getFragmentManager(context);
        Fragment fragment = manager.findFragmentByTag(fragmentName);
        FragmentTransaction transaction = getTransaction(context);
//        transaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
//                R.animator.fragment_slide_right_exit);
        transaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
        transaction.remove(fragment).commit();
    }

    public void destroyFragmentManager(Context context) {
        mFragmentManagerPool.remove(context);
    }

    /**
     * 带过度动画的Fragment
     *
     * @param containerViewId
     * @param className
     */
    public void addAnimFragment(final int containerViewId, final String className, Activity activity) {
        try {
            String tag = className;
            FragmentManager fragmentManager = getFragmentManager(activity);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);

            Fragment currentFragment = getCurrentFragment(fragmentManager);
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = (Fragment) Class.forName(className).newInstance();
                transaction.add(containerViewId, fragment, tag);
            } else {
                transaction.show(fragment);
            }

            transaction.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "addAnimFragment,e=" + e.getMessage());
        }
    }

    /**
     * 带过度动画的Fragment
     *
     * @param
     * @param containerViewId
     * @param className
     */
    public void addAnimFragment(final int containerViewId, final String className, final Bundle bundle, Activity activity) {
        try {
            String tag = className;
            FragmentManager fragmentManager = getFragmentManager(activity);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);

            Fragment currentFragment = getCurrentFragment(fragmentManager);
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = (Fragment) Class.forName(className).newInstance();
                fragment.setArguments(bundle);
                transaction.add(containerViewId, fragment, tag);
            } else {
                transaction.show(fragment);
            }

            transaction.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "addAnimFragment,e=" + e.getMessage());
        }
    }

    public Fragment addAloneFragment(final int containerViewId, final String className, final Bundle bundle) {
        Fragment fragment = null;
        try {
            FragmentManager fragmentManager = getFragmentManager(getCurrentActivity());
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            fragment = (Fragment) Class.forName(className).newInstance();
            fragment.setArguments(bundle);
            transaction.add(containerViewId, fragment, className);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragment;
    }

    private FragmentManager getFragmentManager(Context context) {
        FragmentManager fragmentManager = null;
        if ((fragmentManager = mFragmentManagerPool.get(context)) != null && !fragmentManager.isDestroyed()) {
            return fragmentManager;
        } else {
            return createFragmentManager(context);
        }
    }

    // 需要private
    public FragmentTransaction getTransaction(Context context) {
        return getFragmentManager(context).beginTransaction();
    }

    private FragmentManager createFragmentManager(Context context) {
        FragmentManager fragmentManager = null;
        fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        mFragmentManagerPool.put(context, fragmentManager);
        return fragmentManager;
    }

    private Fragment getCurrentFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null && fragments.size() != 0) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    private Activity getCurrentActivity() {
        return SystemManager.getInstance().getSystem(SystemPage.class).getCurrActivity();
    }
}
