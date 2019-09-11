package org.apache.cordova.gesture;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.united.hjb.R;

public class GestureToast extends Toast {

	public GestureToast(Context context) {
		super(context);
	}

	/**
	 * 生成一个只有文本的自定义Toast
	 * @param context  // 上下文对象
	 * @param text     // 要显示的文字
	 * @param duration // Toast显示时间
	 * @return
	 */
	public static GestureToast makeTextToast(Context context, CharSequence text, int duration) {
		GestureToast result = new GestureToast(context);
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.view_toast, null);
		TextView tv = (TextView) v.findViewById(android.R.id.message);
		tv.setText(text);
		result.setView(v);
		// setGravity方法用于设置位置，此处为垂直居中
		result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		result.setDuration(duration);
		return result;
	}
}