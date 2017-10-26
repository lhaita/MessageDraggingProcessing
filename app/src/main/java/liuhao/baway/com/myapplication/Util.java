package liuhao.baway.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.Window;

public class Util {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     *  获取两点之间的距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * 判断指定点是否在圆心之内
     * @param x1 当前点x坐标
     * @param y1 当前点y坐标
     * @param x2 圆心x坐标
     * @param y2 圆心y坐标
     * @param radius 圆的半径
     * @return
     */
    public static boolean isInCircle(float x1, float y1, float x2, float y2, int radius) {
        return distance(x1, y1, x2, y2) < radius;
    }

    /**
     * 获取状态栏高度＋标题栏高度
     *
     * @param activity
     * @return
     */
    public static int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }
}
