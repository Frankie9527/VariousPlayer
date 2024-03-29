package org.various.demo;

import android.annotation.SuppressLint;
import android.app.Application;

import org.easy.tools.EasySdk;
import org.various.demo.utils.CrashHandler;
import org.various.player.PlayerConfig;

public class App extends Application {
    @SuppressLint({"StaticFieldLeak"})
    private static Application mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        PlayerConfig.init(this);
        CrashHandler.getInstance().init();
        EasySdk.init(this);
    }
    public static Application getContext() {
        return mContext;
    }
}
