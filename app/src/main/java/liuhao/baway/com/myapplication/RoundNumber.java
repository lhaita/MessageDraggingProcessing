package liuhao.baway.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RoundNumber extends View {
    private int radius; // 圆形半径
    private float circleX; // 圆心x坐标
    private float circleY; // 圆心y坐标

    private Paint circlePaint; // 圆形画笔
    private TextPaint textPaint; // 文字画笔
    private int textSize; // 字体大小，单位SP
    private Paint.FontMetrics textFontMetrics; // 字体
    private float textMove; // 为了让文字居中，需要移动的距离

    private String message = "1";
    private boolean firstInit = true;
    private Context mContext;

    private ClickListener mClickListener;

    public RoundNumber(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initPaint();
    }

    /**
     * 初始化
     */
    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (firstInit) {
            firstInit = false;

            radius = w / 2;
            int[] position = new int[2];
            getLocationOnScreen(position);

            circleX = radius;
            circleY = radius;

            textSize = radius; // 根据圆半径来设置字体大小
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(Util.sp2px(mContext, textSize));
            textFontMetrics = textPaint.getFontMetrics();
            textMove = -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2; // drawText从baseline开始，baseline的值为0，baseline的上面为负值，baseline的下面为正值，即这里ascent为负值，descent为正值,比如ascent为-20，descent为5，那需要移动的距离就是20 - （20 + 5）/ 2
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(circleX, circleY, radius, circlePaint);
        canvas.drawText(message, circleX, circleY + textMove, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (MainActivity.isTouchable) {
                    if (mClickListener != null) {
                        MainActivity.isTouchable = false;
                        getParent().requestDisallowInterceptTouchEvent(true); // 不允许父控件处理TouchEvent，当父控件为ListView这种本身可滑动的控件时必须要控制
                        mClickListener.onDown();
                    }
                    return true;
                }

                return false;
            case MotionEvent.ACTION_MOVE:
                if (mClickListener != null) { // 注意这里要用getRaw来获取手指当前所处的相对整个屏幕的坐标
                    mClickListener.onMove(event.getRawX(), event.getRawY() - Util.getTopBarHeight((Activity)  mContext));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mClickListener != null) {
                    getParent().requestDisallowInterceptTouchEvent(false); // 将控制权还给父控件
                    mClickListener.onUp();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置显示内容
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取显示内容
     * @return
     */
    public String getMessage() {
        return message;
    }

    public interface ClickListener {
        void onDown(); // 手指按下

        void onMove(float curX, float curY); // 手指移动

        void onUp(); // 手指抬起
    }

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }
}
