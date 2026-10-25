package com.github.chenpeeer.alphaclockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

/**
 * AlphaClockView：圆形时钟。
 *
 * 属性：
 * - alpha_clock_color：圆环和指针颜色
 * - alpha_clock_show_second：是否显示秒针
 */
public class AlphaClockView extends View {

    private final Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mHourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mMinutePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Calendar mCalendar = Calendar.getInstance();

    private int mClockColor = Color.parseColor("#2196F3");
    private boolean mShowSecond = true;

    private boolean mRunning = false;

    public AlphaClockView(Context context) {
        this(context, null);
    }

    public AlphaClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(
                    attrs, R.styleable.AlphaClockView, defStyleAttr, 0);
            try {
                mClockColor = ta.getColor(
                        R.styleable.AlphaClockView_alpha_clock_color, mClockColor);
                mShowSecond = ta.getBoolean(
                        R.styleable.AlphaClockView_alpha_clock_show_second, mShowSecond);
            } finally {
                ta.recycle();
            }
        }

        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(dp(3));
        mCirclePaint.setColor(mClockColor);

        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(dp(2));
        mTickPaint.setColor(mClockColor);

        mHourPaint.setStyle(Paint.Style.STROKE);
        mHourPaint.setStrokeWidth(dp(6));
        mHourPaint.setStrokeCap(Paint.Cap.ROUND);
        mHourPaint.setColor(mClockColor);

        mMinutePaint.setStyle(Paint.Style.STROKE);
        mMinutePaint.setStrokeWidth(dp(4));
        mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
        mMinutePaint.setColor(mClockColor);

        mSecondPaint.setStyle(Paint.Style.STROKE);
        mSecondPaint.setStrokeWidth(dp(2));
        mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecondPaint.setColor(Color.parseColor("#F44336"));

        mCenterPaint.setStyle(Paint.Style.FILL);
        mCenterPaint.setColor(mClockColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = resolveSize((int) dp(200), widthMeasureSpec);
        int h = resolveSize((int) dp(200), heightMeasureSpec);
        int size = Math.min(w, h);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRunning = true;
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        mRunning = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        float r = Math.min(cx, cy) - dp(8);

        // 圆环
        canvas.drawCircle(cx, cy, r, mCirclePaint);

        // 12 个刻度
        for (int i = 0; i < 12; i++) {
            canvas.save();
            canvas.rotate(i * 30f, cx, cy);
            float inner = r - (i % 3 == 0 ? dp(14) : dp(8));
            canvas.drawLine(cx, cy - r, cx, cy - inner, mTickPaint);
            canvas.restore();
        }

        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        int second = mCalendar.get(Calendar.SECOND);
        int millis = mCalendar.get(Calendar.MILLISECOND);

        // 时针
        canvas.save();
        canvas.rotate((hour + minute / 60f) * 30f, cx, cy);
        canvas.drawLine(cx, cy, cx, cy - r * 0.5f, mHourPaint);
        canvas.restore();

        // 分针
        canvas.save();
        canvas.rotate((minute + second / 60f) * 6f, cx, cy);
        canvas.drawLine(cx, cy, cx, cy - r * 0.7f, mMinutePaint);
        canvas.restore();

        // 秒针
        if (mShowSecond) {
            canvas.save();
            canvas.rotate((second + millis / 1000f) * 6f, cx, cy);
            canvas.drawLine(cx, cy, cx, cy - r * 0.85f, mSecondPaint);
            canvas.restore();
        }

        // 中心点
        canvas.drawCircle(cx, cy, dp(4), mCenterPaint);

        if (mRunning) {
            postInvalidateOnAnimation();
        }
    }

    // ===== 对外 API =====

    public void setClockColor(int color) {
        mClockColor = color;
        mCirclePaint.setColor(color);
        mTickPaint.setColor(color);
        mHourPaint.setColor(color);
        mMinutePaint.setColor(color);
        mCenterPaint.setColor(color);
        invalidate();
    }

    public int getClockColor() {
        return mClockColor;
    }

    public void setShowSecond(boolean show) {
        mShowSecond = show;
        invalidate();
    }

    public boolean isShowSecond() {
        return mShowSecond;
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}