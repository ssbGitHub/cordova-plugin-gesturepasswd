package org.apache.cordova.gesture;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.united.hjb.R;

public class ProgressAnimAlert extends Dialog {

    private AnimationDrawable animationDrawable;

    public ProgressAnimAlert(Context context) {
        super(context, R.style.Translucent_NoTitle);  //这里设置了Dialog的Style让其背景为透明的，并且没有title，具体代码就不贴了
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_alert_layout);

        //禁用返回键
        setCancelable(false);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        ImageView progressImageView = findViewById(R.id.progress_image); //在ImageView上使用帧动画
        animationDrawable = (AnimationDrawable) getContext().getResources().getDrawable(R.drawable.progress_alert_anim); //帧动画的初始化
        progressImageView.setImageDrawable(animationDrawable); //将动画设置在ImageView上
    }

    /**
     * 开始帧动画
     */
    @Override
    protected void onStart() {
        animationDrawable.start();
        super.onStart();
    }

    /**
     * 停止帧动画
     */
    @Override
    protected void onStop() {
        animationDrawable.stop();
        super.onStop();
    }

}
