package com.ufind.softkeyboardlistener;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private View rootLayout;
    private int keyboardHeight = -1;
    private View mView;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            com.orhanobut.logger.Logger.d("statusBarHeight = "+getStatusBarHeight()+" ,rootView height = "+rootLayout.getRootView().getHeight()+" ,view height = "+rootLayout.getHeight());
            if (rootLayout.getHeight() < rootLayout.getRootView().getHeight() - getStatusBarHeight()){
                com.orhanobut.logger.Logger.d("show");
                if (keyboardHeight ==-1){
                    keyboardHeight = rootLayout.getRootView().getHeight() - rootLayout.getHeight() - getStatusBarHeight();
                    com.orhanobut.logger.Logger.d("keyboard height = "+keyboardHeight);
                }
                if (mView.getVisibility() == View.VISIBLE){
                    mView.setVisibility(View.GONE);
                }
            }else {
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

    public void show(View view){
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
