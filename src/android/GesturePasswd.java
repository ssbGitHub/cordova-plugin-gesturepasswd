package org.apache.cordova.gesture;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.united.hjb.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class GesturePasswd extends CordovaPlugin implements ChaosGestureView.GestureCallBack {

    private CallbackContext callbackContext = null;

    private ImageView tv_back = null;
    private TextView resetPwd = null;
    private ChaosGestureView gestureView = null;

    private Dialog gestureDialog = null;

    private String redirect = null;
    private String userName = null;

    private TextView tvUserName = null;
    private TextView forgetGestureId = null;
    private TextView passwdLoginId = null;
    private TextView hintLoginInfo = null;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("showVerify")) {
            this.callbackContext = callbackContext;
            showVerifyGestureScreen();
            return true;
        } else if (action.equals("showSetting")) {
            this.callbackContext = callbackContext;
            showSettingGestureScreen();
            return true;
        } else if (action.equals("showLogin")) {
            redirect = args.getString(0);
            userName = args.getString(1);
            this.callbackContext = callbackContext;
            showLoginGestureScreen();
            return true;
        } else if (action.equals("loginFail")) {
            this.callbackContext = callbackContext;
            String msg = args.getString(0);
            loginFail(msg);
            return true;
        } else if (action.equals("hideLogin") || action.equals("hideSetting") || action.equals("hideVerify")) {
            removeGestureScreen();
            return true;
        }
        return false;
    }

    private void loginFail(String msg) {
        if (gestureView != null) {
            gestureView.resetGesture(msg, false);
        }

        if (hintLoginInfo != null) {
            hintLoginInfo.setTextColor(Color.RED);
            hintLoginInfo.setText(msg);
        }
    }

    private void showVerifyGestureScreen() {
        if (gestureDialog != null && gestureDialog.isShowing()) {
            return;
        }

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Display display = cordova.getActivity().getWindowManager().getDefaultDisplay();
                Context context = webView.getContext();

                gestureDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                if ((cordova.getActivity().getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    gestureDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                View view = View.inflate(context, R.layout.activity_verify_pattern_psw, null);
                view.setBackgroundColor(preferences.getInteger("backgroundColor", Color.WHITE));
                view.setMinimumHeight(display.getHeight());
                view.setMinimumWidth(display.getWidth());

                tv_back = view.findViewById(R.id.tv_setting_back);
                tv_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeGestureScreen();
                    }
                });

                gestureView = view.findViewById(R.id.gesture_setting);
                gestureView.initCallBack(GesturePasswd.this);

                changeStatusBarColor(gestureDialog.getWindow(), "#ee7700", false);

                gestureDialog.setContentView(view);
                gestureDialog.show();

                Window window = gestureDialog.getWindow();
                window.setWindowAnimations(R.style.right_menu_animation);  //添加动画
            }
        });
    }

    private void showSettingGestureScreen() {
        if (gestureDialog != null && gestureDialog.isShowing()) {
            return;
        }

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Display display = cordova.getActivity().getWindowManager().getDefaultDisplay();
                Context context = webView.getContext();

                gestureDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                if ((cordova.getActivity().getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    gestureDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                View view = View.inflate(context, R.layout.activity_setting_pattern_psw, null);
                view.setBackgroundColor(preferences.getInteger("backgroundColor", Color.WHITE));
                view.setMinimumHeight(display.getHeight());
                view.setMinimumWidth(display.getWidth());

                tv_back = view.findViewById(R.id.tv_setting_back);
                tv_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeGestureScreen();
                    }
                });

                gestureView = view.findViewById(R.id.gesture_setting);
                gestureView.initCallBack(GesturePasswd.this);

                resetPwd = view.findViewById(R.id.reset_pwd);
                resetPwd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gestureView.resetGesture("请重新设置", true);
                    }
                });

                //状态栏背景颜色
                changeStatusBarColor(gestureDialog.getWindow(), "#ee7700", true);

                gestureDialog.setContentView(view);
                gestureDialog.show();

                Window window = gestureDialog.getWindow();
                window.setWindowAnimations(R.style.right_menu_animation);  //添加动画

                gestureDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP
                                && keyCode == KeyEvent.KEYCODE_BACK) {
                            callbackContext.error("KEYCODE_BACK");
                            removeGestureScreen();
                            return true;
                        } else {
                            return true;
                        }
                    }
                });
            }
        });
    }

    private void showLoginGestureScreen() {
        if (gestureDialog != null && gestureDialog.isShowing()) {
            return;
        }

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Display display = cordova.getActivity().getWindowManager().getDefaultDisplay();
                Context context = webView.getContext();

                gestureDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                if ((cordova.getActivity().getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    gestureDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                View view = View.inflate(context, R.layout.activity_close_pattern_psw, null);
                view.setBackgroundColor(preferences.getInteger("backgroundColor", Color.WHITE));
                view.setMinimumHeight(display.getHeight());
                view.setMinimumWidth(display.getWidth());

                tvUserName = view.findViewById(R.id.tv_user_name);
                tvUserName.setText("尊敬的用户：" + userName + " 您好！");

                hintLoginInfo = view.findViewById(R.id.hint_login_info);

                gestureView = view.findViewById(R.id.gesture_login);
                gestureView.initCallBack(GesturePasswd.this);

                forgetGestureId = view.findViewById(R.id.forget_gesture_id);
                forgetGestureId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                                .setMessage("忘记手势登录密码需要通过登录重新设置").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        JSONArray json = new JSONArray();
                                        json.put("forgetGesture");
                                        callbackContext.success(json);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                });
                passwdLoginId = view.findViewById(R.id.passwd_login_id);
                passwdLoginId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONArray json = new JSONArray();
                        json.put("passwdLogin");
                        callbackContext.success(json);
                    }
                });

                //状态栏背景颜色
                changeStatusBarColor(gestureDialog.getWindow(), "#ffffff", true);

                gestureDialog.setContentView(view);

                //gestureDialog.setCancelable(false);
                gestureDialog.setCanceledOnTouchOutside(true);
                gestureDialog.show();

                Window window = gestureDialog.getWindow();
                window.setWindowAnimations(R.style.right_menu_animation);  //添加动画

                gestureDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP
                                && keyCode == KeyEvent.KEYCODE_BACK) {
                            callbackContext.error("KEYCODE_BACK");
                            removeGestureScreen();
                            return true;
                        } else {
                            return true;
                        }
                    }
                });
            }
        });
    }

    private void removeGestureScreen() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (gestureDialog != null && gestureView != null && gestureDialog.isShowing()) {
                    gestureDialog.dismiss();
                    gestureDialog = null;
                    tv_back = null;
                    resetPwd = null;
                    gestureView = null;
                    redirect = null;
                    tvUserName = null;
                    forgetGestureId = null;
                    passwdLoginId = null;
                }
            }
        });
    }

    @Override
    public void gestureVerifySuccessListener(int stateFlag, List<ChaosGestureView.GestureBean> data, boolean success) {

        if (stateFlag == ChaosGestureView.STATE_LOGIN) {
            JSONArray json= new JSONArray();
            json.put(getGesturePwd(data));
            json.put(redirect);

            callbackContext.success(json);
        } else if (stateFlag == ChaosGestureView.STATE_REGISTER) {
            callbackContext.success(getGesturePwd(data));
        } else if (stateFlag == ChaosGestureView.STATE_VERIFY) {
            callbackContext.success(getGesturePwd(data));
        }

    }

    private String getGesturePwd(List<ChaosGestureView.GestureBean> data) {
        String pwd = "";

        for (int i = 0; data != null && i < data.size(); i++) {
            pwd += data.get(i).toNumber();
        }
        return pwd;
    }

    private void changeStatusBarColor(Window window, String color, boolean dark) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        window.clearFlags(0x04000000);
        window.addFlags(0x80000000);

        try {
            window.getClass().getMethod("setStatusBarColor", int.class).invoke(window, Color.parseColor(color));
        } catch (Exception ignore) {
        }

        View decorView = window.getDecorView();
        int uiOptions = decorView.getSystemUiVisibility();

        if (dark) {
            decorView.setSystemUiVisibility(uiOptions | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decorView.setSystemUiVisibility(uiOptions & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
