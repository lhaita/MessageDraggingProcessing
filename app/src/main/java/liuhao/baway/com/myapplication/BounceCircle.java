package liuhao.baway.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

public class BounceCircle extends View {
    private Context mContext;

    private Paint circlePaint; // 圆形/连线画笔
    private TextPaint textPaint; // 文字画笔
    private Paint.FontMetrics textFontMetrics; // 字体
    private Path path;

    private int radius; // 移动圆形半径
    private float textMove; // 为了让文字居中，需要移动的距离

    private float curX; // 当前x坐标
    private float curY; // 当前y坐标
    private float circleX; // 固定圆的圆心x坐标
    private float circleY; // 固定圆的圆心y坐标
    private float ratio = 1; // 圆缩放的比例，随着手指的移动，固定的圆越来越小
    private float ratioLimit = 0.2f; // 固定圆最小的缩放比例，小于该比例时就直接消失
    private int distanceLimit = 100; // 固定圆和移动圆的圆心之间距离的限值，单位DP（配合ratioLimit使用）
    private int textSize; // 字体大小，单位SP

    private int animationTime = 200; // 抖动动画执行的时间
    private int animationTimes = 1; //  抖动动画执行次数
    private boolean needDraw = true; // 是否需要执行onDraw方法

    private FinishListener mFinishListener; // 自定义接口，用来回调
    private String message = "1"; // 显示的数字的初始值

    private Bitmap[] explosionAnim; // 爆炸动画
    private boolean animStart; // 动画开始
    private int animNumber = 5; // 动画帧的个数
    private int curAnimNumber; // 动画播放的当前帧
    private int animInterval = 200; // 动画帧之间的间隔
    private int animWidth; // 动画帧的宽度
    private int animHeight; // 动画帧的高度

    private View originalView;

    public BounceCircle(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initPaint();
    }

    /**
     * 初始化Paint
     */
    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setAntiAlias(true);

        distanceLimit = Util.dip2px(mContext, distanceLimit);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);

        path = new Path();
    }

    /**
     * 初始化爆炸动画
     */
    private void initAnim() {
        if (explosionAnim == null) {
            explosionAnim = new Bitmap[animNumber];
            explosionAnim[0] = BitmapFactory.decodeResource(getResources(), R.mipmap.explosion_one);
            explosionAnim[1] = BitmapFactory.decodeResource(getResources(), R.mipmap.explosion_two);
            explosionAnim[2] = BitmapFactory.decodeResource(getResources(), R.mipmap.explosion_three);
            explosionAnim[3] = BitmapFactory.decodeResource(getResources(), R.mipmap.explosion_four);
            explosionAnim[4] = BitmapFactory.decodeResource(getResources(), R.mipmap.explosion_five);

            // 动画每帧的长宽都是一样的，取一个即可
            animWidth = explosionAnim[0].getWidth();
            animHeight = explosionAnim[0].getHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (needDraw) {
            // 画固定圆
            if (ratio >= ratioLimit) {
                canvas.drawCircle(circleX, circleY, radius * ratio, circlePaint);
            }

            // 画移动圆和连线
            if (curX != 0 && curY != 0) {
                canvas.drawCircle(curX, curY, radius, circlePaint);
                if (ratio >= ratioLimit) {
                    drawLinePath(canvas);
                }
            }

            // 数字要最后画，否则会被连线遮掩
            if (curX != 0 && curY != 0) { // 移动圆里面的数字
                canvas.drawText(message, curX, curY + textMove, textPaint);
            } else { // 只有初始时需要绘制固定圆里面的数字
                canvas.drawText(message, circleX, circleY + textMove, textPaint);
            }
        }

        if (animStart) { // 动画进行中
            if (curAnimNumber < animNumber) {
                canvas.drawBitmap(explosionAnim[curAnimNumber], curX - animWidth / 2, curY - animHeight / 2, null);
                curAnimNumber++;
                if (curAnimNumber == 1) { // 第一帧立即执行
                    invalidate();
                } else { // 其余帧每隔固定时间执行
                    postInvalidateDelayed(animInterval);
                }
            } else { // 动画结束
                animStart = false;
                curAnimNumber = 0;
                recycleBitmap();
                setVisibility(View.INVISIBLE); // 隐藏BounceCircle
                curX = 0;
                curY = 0;

                MainActivity.isTouchable = true;

                // 删除后的回调
                if (mFinishListener != null) {
                    mFinishListener.onFinish();
                }
            }
        }
    }

    /**
     * 回收Bitmap资源
     */
    private void recycleBitmap() {
        if (explosionAnim != null && explosionAnim.length != 0) {
            for (int i = 0; i < explosionAnim.length; i++) {
                if (explosionAnim[i] != null && !explosionAnim[i].isRecycled()) {
                    explosionAnim[i].recycle();
                    explosionAnim[i] = null;
                }
            }

            explosionAnim = null;
        }
    }

    /**
     * 画固定圆和移动圆之间的连线
     * @param canvas
     */
    private void drawLinePath(Canvas canvas) {
        path.reset();

        float distance = (float) Util.distance(circleX, circleY, curX, curY); // 移动圆和固定圆圆心之间的距离
        float sina = (curY - circleY) / distance; // 移动圆圆心和固定圆圆心之间的连线与X轴相交形成的角度的sin值
        float cosa = (circleX - curX) / distance; // 移动圆圆心和固定圆圆心之间的连线与X轴相交形成的角度的cos值

        path.moveTo(circleX - sina * radius * ratio, circleY - cosa * radius * ratio); // A点坐标
        path.lineTo(circleX + sina * radius * ratio, circleY + cosa * radius * ratio); // AB连线
        path.quadTo((circleX + curX) / 2, (circleY + curY) / 2, curX + sina * radius, curY + cosa * radius); // 控制点为两个圆心的中间点，二阶贝塞尔曲线，BC连线
        path.lineTo(curX - sina * radius, curY - cosa * radius); // CD连线
        path.quadTo((circleX + curX) / 2, (circleY + curY) / 2, circleX - sina * radius * ratio, circleY - cosa * radius * ratio); // 控制点也是两个圆心的中间点，二阶贝塞尔曲线，DA连线

        canvas.drawPath(path, circlePaint);
    }

    /**
     * 计算固定圆缩放的比例
     * @param distance
     * @return
     */
    private void calculateRatio(float distance) {
        ratio = (distanceLimit - distance) / distanceLimit;
    }

    /**
     * 抖动动画
     * @param counts
     */
    public void shakeAnimation(int counts) {
        // 避免动画抖动的频率过大，所以除以2，另外，抖动的方向跟手指滑动的方向要相反
        Animation translateAnimation = new TranslateAnimation((circleX - curX) / 2, 0, (circleY - curY) / 2, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(animationTime);
        startAnimation(translateAnimation);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) { // 抖动动画结束时，显示以前的RoundNumber，隐藏BounceCircle
                if (originalView != null) {
                    originalView.setVisibility(View.VISIBLE);
                }

                setVisibility(View.INVISIBLE);

                MainActivity.isTouchable = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public interface FinishListener {
        void onFinish();
    }

    public void setFinishListener(FinishListener finishListener) {
        mFinishListener = finishListener;
    }

    public void move(float curX, float curY) {
        this.curX = curX;
        this.curY = curY;
        calculateRatio((float) Util.distance(curX, curY, circleX, circleY));

        invalidate();
    }

    public void up() {
        if (ratio > ratioLimit) { // 没有超出最大移动距离，手抬起时需要让移动圆回到固定圆的位置
            shakeAnimation(animationTimes);

            curX = 0;
            curY = 0;
            ratio = 1;


        } else { // 超出最大移动距离
            needDraw = false;
            animStart = true;

            initAnim();
        }

        invalidate();
    }

    public void down(int radius, float circleX, float circleY, String message) {
        needDraw = true; // 由于BounceCircle是公用的，每次进来时都要确保needDraw的值为true

        this.radius = radius;
        this.circleX = circleX;
        this.circleY = circleY;
        this.message = message;

        textSize = radius;
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(Util.sp2px(mContext, textSize));
        textFontMetrics = textPaint.getFontMetrics();
        textMove = -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2; // drawText从baseline开始，baseline的值为0，baseline的上面为负值，baseline的下面为正值，即这里ascent为负值，descent为正值,比如ascent为-20，descent为5，那需要移动的距离就是20 - （20 + 5）/ 2

        invalidate();
    }

    /**
     * 设置按下时被隐藏的View
     * @param view
     */
    public void setOrginView(View view) {
        originalView = view;
    }
}
