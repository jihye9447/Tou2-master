package com.tou.utils;

import android.accessibilityservice.AccessibilityService;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.tou.LockApplication;

/**
 * Created by Administrator on 2018-03-01.
 */

public class LockWindowAccessibilityService extends AccessibilityService{

    @Override
    protected  boolean onKeyEvent(KeyEvent event){

        LockScreen.getInstance().init(this);
        if(((LockApplication) getApplication()).lockScreenShow){

            if(event.getKeyCode()==KeyEvent.KEYCODE_HOME||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_CENTER){
                return true;
            }
        }
        return super.onKeyEvent(event);
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }
}
