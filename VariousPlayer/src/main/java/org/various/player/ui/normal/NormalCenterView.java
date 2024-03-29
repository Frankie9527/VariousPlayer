package org.various.player.ui.normal;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.various.player.NotificationCenter;
import org.various.player.PlayerConstants;
import org.various.player.R;

import org.various.player.core.PlayerManager;
import org.various.player.core.VariousPlayerManager;
import org.various.player.ui.base.BaseCenterView;
import org.various.player.utils.LogUtils;
import org.various.player.utils.OrientationUtils;
import org.various.player.utils.UiUtils;
import org.various.player.widget.BrightnessPopupWindow;
import org.various.player.widget.PicPopupWindow;
import org.various.player.widget.ProgressPopupWindow;
import org.various.player.widget.VolumePopupWindow;

/**
 * Created by Frankie on 2020/8/14
 * Email：847145851@qq.com
 * func:
 */
public class NormalCenterView extends BaseCenterView implements View.OnClickListener, NotificationCenter.NotificationCenterDelegate {
    private static final String TAG = "NormalCenterView";
    ProgressBar video_progress;
    ImageView img_lock_screen;
    ImageView img_take_pic;

    BrightnessPopupWindow mBrightnessPop;
    VolumePopupWindow mVolumePop;
    ProgressPopupWindow mProgressPopup;
    PicPopupWindow mPicPop;

    DoubleBackForWardView double_forward_view;
    public NormalCenterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected int setLayoutId() {
        return R.layout.various_normal_view_center;
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.user_onclick_take_pic_data);
        video_progress = findViewById(R.id.video_progress);
        img_status = findViewById(R.id.img_status);
        img_status.setOnClickListener(this);
        UiUtils.viewSetVisible(img_status);
        img_status.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.video_play));
        rl_play_err = findViewById(R.id.rl_play_err);
        tv_replay = findViewById(R.id.tv_replay);
        img_take_pic = findViewById(R.id.img_take_pic);
        img_take_pic.setOnClickListener(this);
        img_lock_screen = findViewById(R.id.img_lock_screen);
        img_lock_screen.setOnClickListener(this);
        mVolumePop = new VolumePopupWindow(context);
        mBrightnessPop = new BrightnessPopupWindow(context);
        mProgressPopup = new ProgressPopupWindow(context);
        mPicPop = new PicPopupWindow(context);
        double_forward_view=findViewById(R.id.double_forward_view);
    }

    @Override
    public void showLoading() {
        UiUtils.viewSetVisible(this);
        UiUtils.viewSetGone(img_status);
        UiUtils.viewSetGone(rl_play_err);
        UiUtils.viewSetVisible(video_progress);
    }

    @Override
    public void hideLoadingAndPlayIcon() {
        UiUtils.viewSetGone(video_progress);
        UiUtils.viewSetGone(img_status);
    }

    @Override
    public void showEnd() {
        UiUtils.viewSetVisible(this);
        UiUtils.viewSetGone(video_progress);
        img_status.setVisibility(VISIBLE);
        img_status.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.video_replay));
    }

    @Override
    public void showError() {
        UiUtils.viewSetVisible(this);
        UiUtils.viewSetGone(video_progress);
        UiUtils.viewSetVisible(rl_play_err);
    }

    @Override
    public void hideAll() {
        if (!VariousPlayerManager.isPlaying()){
            return;
        }
        UiUtils.viewSetGone(video_progress);
        UiUtils.viewSetGone(img_status);
        UiUtils.viewSetGone(rl_play_err);
        UiUtils.viewSetGone(this);
    }

    @Override
    public void showStatus() {
        setVisibility(View.VISIBLE);
        int type = VariousPlayerManager.getCurrentStatus();
        LogUtils.e(TAG, "type=" + type);
        UiUtils.viewSetGone(video_progress);
        UiUtils.viewSetGone(img_status);
        UiUtils.viewSetGone(rl_play_err);
        switch (type) {
            case PlayerConstants.IDLE:
            case PlayerConstants.READY:
                UiUtils.viewSetVisible(img_status);
                img_status.setImageDrawable(ContextCompat.getDrawable(getContext(), VariousPlayerManager.isPlaying() ? R.drawable.video_pause : R.drawable.video_play));
                break;
            case PlayerConstants.BUFFERING:
                showLoading();
                break;
            case PlayerConstants.END:
                showEnd();
                break;
            case PlayerConstants.ERROR:
                showError();
                break;
            default:
                UiUtils.viewSetVisible(img_status);
                img_status.setImageDrawable(ContextCompat.getDrawable(getContext(),   R.drawable.video_play));
                break;
        }
    }


    @Override
    public void showBrightnessChange(int changePercent) {
        mBrightnessPop.showBrightnessChange(changePercent, (View) getParent(), currentBrightness);
    }

    @Override
    public void showVolumeChange(int changePercent) {
        mVolumePop.showVolumeChange(changePercent, (View) getParent(), currentVolume);
    }

    @Override
    public void showProgressChange(long lastVideoPlayTime, int arrowDirection, long videoDurationTime) {
        mProgressPopup.showProgressChange((View) getParent(), lastVideoPlayTime, arrowDirection, videoDurationTime);
    }

    @Override
    protected void dismissAllView() {
        if (mVolumePop != null) {
            mVolumePop.dismissPop();
        }
        if (mBrightnessPop != null) {
            mBrightnessPop.dismissPop();
        }
        if (mProgressPopup != null) {
            mProgressPopup.dismissPop();
        }
    }

    @Override
    public void setOnCenterClickListener(OnClickListener listener) {
        if (img_status!=null){
            img_status.setOnClickListener(listener);
        }
        if (tv_replay!=null){
            tv_replay.setOnClickListener(listener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_status) {
            if (VariousPlayerManager.isPlaying()) {
                VariousPlayerManager.pause();
                return;
            }
            VariousPlayerManager.resume();
            return;
        }
        if (v.getId() == R.id.img_take_pic) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.user_onclick_take_pic);
            return;
        }
        if (v.getId() == R.id.img_lock_screen) {

            return;
        }

    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.user_onclick_take_pic_data) {
            Bitmap bitmap = (Bitmap) args[0];
            mPicPop.showPic((View) getParent(), bitmap);
        }
    }

    @Override
    public void onScreenOrientationChanged() {
        LogUtils.d(TAG,"onScreenOrientationChanged");
        if (OrientationUtils.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            UiUtils.viewSetVisible(img_lock_screen);
            UiUtils.viewSetVisible(img_take_pic);
            return;
        }
        img_lock_screen.setVisibility(INVISIBLE);
        img_take_pic.setVisibility(INVISIBLE);

    }
    @Override
    public void onLeftDoubleTop() {
        double_forward_view.onLeftDoubleTop();
    }
    @Override
    public void onRightDoubleTap() {
        double_forward_view.onRightDoubleTap();
    }


}
