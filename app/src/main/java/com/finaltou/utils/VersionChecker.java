package com.finaltou.utils;


import android.content.pm.PackageManager;

import android.os.AsyncTask;

import com.finaltou.MarketVersionChecker;
import com.finaltou.VersionCheckCallback;

public class VersionChecker extends AsyncTask<String, Void, Boolean> {

    PackageManager packageManager;
    String packageName;
    VersionCheckCallback checkCallback;

    public VersionChecker(PackageManager packageManager, String packageName, VersionCheckCallback checkCallback) {
        this.packageManager = packageManager;
        this.packageName = packageName;
        this.checkCallback = checkCallback;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        boolean update;
        String store_version = MarketVersionChecker.getMarketVersion(params[0]);
      //  Log.d("event123", store_version);
        try {

            String device_version = packageManager.getPackageInfo(packageName, 0).versionName;
            if (store_version != null) {
                if (store_version.compareTo(device_version) > 0) {
                    //업데이트 필요
                    update = true;
                } else {
                    update = false;

                }
            } else {
                update = false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            update = false;
        }

        return update;

    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        checkCallback.getResult(aVoid);
    }

}