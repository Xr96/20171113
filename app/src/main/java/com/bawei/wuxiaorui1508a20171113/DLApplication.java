package com.bawei.wuxiaorui1508a20171113;

import android.app.Application;
import android.content.Intent;

import com.bawei.wuxiaorui1508a20171113.fu.DownLoadService;


public class DLApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.startService(new Intent(this, DownLoadService.class));
    }

}
