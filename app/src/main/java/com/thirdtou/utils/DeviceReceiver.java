package com.thirdtou.utils;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2018-03-27.
 */

public class DeviceReceiver extends DeviceAdminReceiver{

    @Override
    public void onEnabled(Context context, Intent intent) {

    }

    @Override
    public void onDisabled(Context context, Intent intent) {

    }
}
