package org.apache.cordova.gesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.united.hjb.R;

import java.util.ArrayList;
import java.util.List;


public class ChaosGestureView extends View {
    //验证手势密码
    public static final int STATE_VERIFY = 102;
    //手势初始化录入状态
    public static final int STATE_REGISTER = 101;
    //手势确认 使用状态
    public static final int STATE_LOGIN = 100;
    //设置一个参数记录当前是出于初始化阶段还是使用阶段，默认为确认状态
    private int stateFlag;
    //获取上下
    private Context mContext;
    //最小设置手势的点数
    private int minPointNums;
    //定义一个接口
    private GestureCallBack gestureCallBack;
    //定义两个存储的list
    private List<GestureBean> listDatas;
    private List<GestureBean> listDatasCopy;

    //用于储存最后一个点的坐标
    private GestureBean lastGestrue = null;

    //记录down的位置是否在滑动区域
    private boolean isSlideArea = false;

    private Bitmap selectedBitmap;
    private Bitmap unSelectedBitmap;
    private Bitmap selectedBitmapSmall;
    private Bitmap unSelectedBitmapSmall;

    //一行3*1单位行高
    private float mLineHeight;
    //给小手势view留的空间
    private int panelHeight;
    //view经过measure之后的宽度
    private int mPanelWidth;
    //单元控件的宽度
    private float pieceWidth;
    private float pieceWidthSmall;

    private float currX;
    private float currY;

    private Paint mPaint;


    public ChaosGestureView(Context context) {
        this(context, null);
    }

    public ChaosGestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChaosGestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SecurityCenter);
        Drawable dw_selected = ta.getDrawable(R.styleable.SecurityCenter_selectedBitmap);
        Drawable dw_unSeclect = ta.getDrawable(R.styleable.SecurityCenter_unselectedBitmap);
        Drawable dw_selected_small = ta.getDrawable(R.styleable.SecurityCenter_selectedBitmapSmall);
        Drawable dw_unSeclect_small = ta.getDrawable(R.styleable.SecurityCenter_unselectedBitmapSmall);
        if (dw_selected != null) {
            selectedBitmap = ((BitmapDrawable) dw_selected).getBitmap();
        } else {
            selectedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_finger_selected);
        }
        if (dw_unSeclect != null) {
            unSelectedBitmap = ((BitmapDrawable) dw_unSeclect).getBitmap();
        } else {
            unSelectedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_finger_unselected);
        }
        if (dw_selected_small != null) {
            selectedBitmapSmall = ((BitmapDrawable) dw_selected_small).getBitmap();
        } else {
            selectedBitmapSmall = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_finger_selected_small);
        }
        if (dw_unSeclect_small != null) {
            unSelectedBitmapSmall = ((BitmapDrawable) dw_unSeclect_small).getBitmap();
        } else {
            unSelectedBitmapSmall = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_finger_unselected_new);
        }
        //最小设置的点,默认5个
        minPointNums = ta.getInteger(R.styleable.SecurityCenter_minPoint, 5);
        //获取登录or注册状态
        stateFlag = ta.getInteger(R.styleable.SecurityCenter_stateFlag, 100);
        //给小手势view留的空间
        panelHeight= ta.getInteger(R.styleable.SecurityCenter_panelHeight, 200);

        //设置画笔的颜色
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        //画笔的颜色
        int color = ta.getColor(R.styleable.SecurityCenter_paintColor, context.getResources().getColor(R.color.colorBlack));
        mPaint.setColor(color);
        //字体的大小
        float textsize = ta.getDimension(R.styleable.SecurityCenter_paintTextSize, 40);
        mPaint.setTextSize(textsize);
        //避免重新创建时候的错误
        ta.recycle();

        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;

        //让当前的Activity继承View的接口
        /*try {
            gestureCallBack = (GestureCallBack) mContext;
        } catch (final ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement GestureCallBack");
        }*/

        listDatas = new ArrayList<>();
        listDatasCopy = new ArrayList<>();
    }

    public void initCallBack(GesturePasswd gestPwd) {
        try {
            gestureCallBack = (GestureCallBack) gestPwd;
        } catch (final ClassCastException e) {
            throw new ClassCastException(gestPwd.toString() + " must implement GestureCallBack");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //width即为大View的单位宽 高
        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        //大View一行3*1单位行高
        mLineHeight = width / 3;
        //大手势View为边长width的正方形,panelHeight是给小手势view预留的空间
        setMeasuredDimension(width, width + panelHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = Math.min(w, h);
        //大手势点宽度，为单位宽高的0.6倍，显得更好看一些不会很满
        pieceWidth = (int) (mLineHeight * 0.60f);
        //小手势点宽度，同理
        pieceWidthSmall = (int) (mLineHeight * 0.20f);
        //画出对应手势点的大小
        selectedBitmap = Bitmap.createScaledBitmap(selectedBitmap, (int) pieceWidth, (int) pieceWidth, false);
        unSelectedBitmap = Bitmap.createScaledBitmap(unSelectedBitmap, (int) pieceWidth, (int) pieceWidth, false);
        selectedBitmapSmall = Bitmap.createScaledBitmap(selectedBitmapSmall, (int) pieceWidthSmall, (int) pieceWidthSmall, false);
        unSelectedBitmapSmall = Bitmap.createScaledBitmap(unSelectedBitmapSmall, (int) pieceWidthSmall, (int) pieceWidthSmall, false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //如果处于初始化状态
        if (stateFlag == STATE_REGISTER) {
            //绘制上面的提示点  不需要提示点
            drawTipsPoint(canvas);
        } else if (stateFlag == STATE_VERIFY) {
            //上面的是文字 点没了
            drawTipsText(canvas);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                canvas.drawBitmap(unSelectedBitmap, (float) (mLineHeight * (j + 0.5) - pieceWidth / 2), (float) (mLineHeight * (i + 0.5) - pieceWidth / 2 + panelHeight), mPaint);
            }
        }
        //用于判断状态
        GestureBean firstGestrue = null;
        GestureBean currGestrue = null;
        if (!listDatas.isEmpty()) {

            firstGestrue = listDatas.get(0);
            //画连接线
            for (int i = 1; i < listDatas.size(); i++) {
                currGestrue = listDatas.get(i);
                canvas.drawLine((float) (mLineHeight * (firstGestrue.getX() + 0.5)), (float) (mLineHeight * (firstGestrue.getY() + 0.5) + panelHeight), (float) (mLineHeight * (currGestrue.getX() + 0.5)), (float) (mLineHeight * (currGestrue.getY() + 0.5) + panelHeight), mPaint);
                firstGestrue = currGestrue;
            }
            //最后一条线
            lastGestrue = listDatas.get(listDatas.size() - 1);
            canvas.drawLine((float) (mLineHeight * (lastGestrue.getX() + 0.5)), (float) (mLineHeight * (lastGestrue.getY() + 0.5) + panelHeight), currX, currY, mPaint);

            //遍历数组，把把选中的点更换图片
            for (GestureBean bean : listDatas) {
                canvas.drawBitmap(selectedBitmap, (float) (mLineHeight * (bean.getX() + 0.5) - pieceWidth / 2), (float) (mLineHeight * (bean.getY() + 0.5) + panelHeight - pieceWidth / 2), mPaint);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //判断手势点在大View内
        if (event.getY() >= ((mLineHeight * (0 + 0.5) - pieceWidth / 2 + panelHeight) - 50)
                || (event.getAction() == MotionEvent.ACTION_UP && isSlideArea)) {

            //得到XY用于判断 手指处于哪个点
            int x = (int) ((event.getY() - panelHeight) / mLineHeight);
            int y = (int) (event.getX() / mLineHeight);

            //当前手指的坐标
            currX = event.getX();
            currY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSlideArea = true;

                    lastGestrue = null;

                    if (currX >= 0 && currX <= mPanelWidth && currY >= panelHeight && currY <= panelHeight + mPanelWidth) {
                        if (currY <= (x + 0.5) * mLineHeight + pieceWidth / 2 + panelHeight && currY >= (x + 0.5) * mLineHeight - pieceWidth / 2 + panelHeight &&
                                currX <= (y + 0.5) * mLineHeight + pieceWidth / 2 && currX >= (y + 0.5) * mLineHeight - pieceWidth / 2) {
                            //判断当前手指处于哪个点范围内，如果点没存在listData,存进去，第一个点
                            if (!listDatas.contains(new GestureBean(y, x))) {
                                listDatas.add(new GestureBean(y, x));
                            }
                        }
                    }
                    //重绘一次，第一个点显示被选中了
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //手指移动在大View范围内
                    if (currX >= 0 && currX <= mPanelWidth && currY >= panelHeight && currY <= panelHeight + mPanelWidth) {
                        //缩小响应范围 在此处需要注意的是 x跟currX在物理方向上是反的哦
                        if (currY <= (x + 0.5) * mLineHeight + pieceWidth / 2 + panelHeight && currY >= (x + 0.5) * mLineHeight - pieceWidth / 2 + panelHeight &&
                                currX <= (y + 0.5) * mLineHeight + pieceWidth / 2 && currX >= (y + 0.5) * mLineHeight - pieceWidth / 2) {
                            //滑倒的店处于哪个点范围内，如果点没存在listData,存进去
                            if (!listDatas.contains(new GestureBean(y, x))) {
                                listDatas.add(new GestureBean(y, x));
//
                            }
                        }
                    }
                    //重绘
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    isSlideArea = false;

                    if (lastGestrue != null) {
                        currX = (float) ((lastGestrue.getX() + 0.5) * mLineHeight);
                        currY = (float) ((lastGestrue.getY() + 0.5) * mLineHeight);
                    }

                    //如果View处于认证状态
                    if (stateFlag == STATE_LOGIN || stateFlag == STATE_VERIFY) {
                        postListener(true);
                        invalidate();
                        listDatas.clear();
                        return true;
                    }
                    //View处于注册状态
                    else if (stateFlag == STATE_REGISTER) {
                        //第一次认证状态
                        if (listDatasCopy == null || listDatasCopy.isEmpty()) {
                            if (listDatas.size() < minPointNums) {
                                listDatas.clear();
                                AlertUtil.t(mContext, "点数不能小于" + minPointNums + "个");
                                invalidate();
                                return true;
                            }
                            listDatasCopy.addAll(listDatas);
                            listDatas.clear();
                            AlertUtil.t(mContext, "请再一次绘制");
                        } else {
                            //两次认证成功
                            if (listDatas.equals(listDatasCopy)) {
                                postListener(true);
                            } else {
                                AlertUtil.t(mContext, "与上次手势绘制不一致,请重新设置");
                            }
                            listDatas.clear();
                            invalidate();
                            return true;
                        }
                    }
                    invalidate();
                    break;
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isSlideArea = false;
            }
        }
        return true;
    }

    //绘制提示语
    private void drawTipsText(Canvas canvas) {
        String strHint = stateFlag == STATE_LOGIN ? "输入手势来解锁" : "请输入原手势密码";
        float widthMiddleX = mPanelWidth / 2;

        mPaint.setStyle(Paint.Style.FILL);
        int widthStr1 = (int) mPaint.measureText(strHint);
        float baseX = widthMiddleX - widthStr1 / 2;
        float baseY = panelHeight / 2 + 50;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom - 30;
        float newY = baseY + offY;
        canvas.drawText(strHint, baseX, newY, mPaint);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(10);
    }


    //绘制提示点
    private void drawTipsPoint(Canvas canvas) {
        //宽度为View宽度的一半
        float widthMiddleX = mPanelWidth / 2;
        //确定好相关坐标，找出第一个点的中心点
        float firstX = widthMiddleX - pieceWidthSmall / 4 - pieceWidthSmall / 2 - pieceWidthSmall;
        float firstY = panelHeight / 2 - pieceWidthSmall / 2 - pieceWidthSmall - pieceWidthSmall / 4 - 10;
        //画点，由于没有选中，画9个未选中点
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                canvas.drawBitmap(unSelectedBitmapSmall, (float) (firstX + j * (pieceWidthSmall * 1.25)), (float) (firstY + i * (pieceWidthSmall * 1.25)), mPaint);
            }
        }
        //第二次确认前的小手势密码·显示第一次划过的痕迹
        if (listDatasCopy != null && !listDatasCopy.isEmpty()) {
            for (GestureBean bean : listDatasCopy) {
                canvas.drawBitmap(selectedBitmapSmall, (float) (firstX + bean.getX() * (pieceWidthSmall * 1.25)), (float) (firstY + bean.getY() * (pieceWidthSmall * 1.25)), mPaint);
            }
        }
        //随着手指ActionMove来改变选中点的颜色
        else if (listDatas != null && !listDatas.isEmpty()) {
            for (GestureBean bean : listDatas) {
                canvas.drawBitmap(selectedBitmapSmall, (float) (firstX + bean.getX() * (pieceWidthSmall * 1.25)), (float) (firstY + bean.getY() * (pieceWidthSmall * 1.25)), mPaint);
            }
        }
        drawMessage(canvas, "绘制解锁图案");
    }

    private void drawMessage(Canvas canvas, String message) {
        float widthMiddleX = mPanelWidth / 2;
        //获取Y坐标显示在小View下面
        float firstY = (float) (panelHeight / 2 - pieceWidthSmall / 2 + pieceWidthSmall * 1.25 + 90);

        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL);

        //得到字体的宽度
        int widthStr1 = (int) mPaint.measureText(message);
        float baseX = widthMiddleX - widthStr1 / 2;
        float baseY = firstY + 50;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY = baseY + offY;
        canvas.drawText(message, baseX, newY, mPaint);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(10);
    }

    public void resetGesture(String msg, boolean showToast) {
        listDatas.clear();
        listDatasCopy.clear();
        if (showToast) {
            AlertUtil.t(mContext, msg);
        }
        invalidate();
    }

    //定义接口 ，传递View状态
    public interface GestureCallBack {
        void gestureVerifySuccessListener(int stateFlag, List<GestureBean> data, boolean success);
    }

    //给接口传递数据
    private void postListener(boolean success) {
        if (gestureCallBack != null) {
            gestureCallBack.gestureVerifySuccessListener(stateFlag, listDatas, success);
        }
    }

    //定义Bean，来存储手势坐标
    public class GestureBean {
        private int x;
        private int y;

        @Override
        public String toString() {
            return "GestureBean{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public GestureBean(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int toNumber() {
            return this.y * 3 + this.x + 1;
        }

        @Override
        public boolean equals(Object o) {
            return ((GestureBean) o).getX() == x && ((GestureBean) o).getY() == y;
        }
    }
}
