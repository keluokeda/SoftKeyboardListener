package com.ufind.softkeyboardlistener;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private View rootLayout;
    private int keyboardHeight = -1;
    private View mView;

    //正常情况下 当前屏幕高度 = activity高度+状态栏高度+导航栏高度
    //如果小于此值 差值就是软键盘高度
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            com.orhanobut.logger.Logger.d("statusBarHeight = " + getStatusBarHeight() + " ,rootView height = " + rootLayout.getRootView().getHeight() + " ,view height = " + rootLayout.getHeight());
            if (rootLayout.getHeight() < rootLayout.getRootView().getHeight() - getStatusBarHeight() - getNavigationBarHeight()) {
                com.orhanobut.logger.Logger.d("show");
                if (keyboardHeight == -1) {
                    keyboardHeight = rootLayout.getRootView().getHeight() - rootLayout.getHeight() - getStatusBarHeight() - getNavigationBarHeight();
                    com.orhanobut.logger.Logger.d("keyboard height = " + keyboardHeight);
                }
                if (mView.getVisibility() == View.VISIBLE) {
                    mView.setVisibility(View.GONE);
                }
            } else {
                com.orhanobut.logger.Logger.d("hide");
            }
        }

    };

    protected final int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressWarnings("all")
    //获取是否存在NavigationBar
    public boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = this.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }


    protected int getNavigationBarHeight() {
        if (!checkDeviceHasNavigationBar()) {
            return 0;
        }
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(R.id.container);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        mView = findViewById(R.id.view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void show(View view) {
        hideSoftKeyBoard();
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.height = keyboardHeight;
        mView.setVisibility(View.VISIBLE);

    }

    public final void hideSoftKeyBoard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//隐藏软键盘
    }
}
