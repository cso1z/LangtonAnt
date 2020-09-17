package com.xyz.landun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * created by shenyonghui on 2020/9/15
 */
public class MyView extends View {

    //默认单个表格宽高值
    private final int DEFAULT_SINGLE_GRID_WIDTH = 30;
    //单个表格宽高值
    private int singleGridWidth = DEFAULT_SINGLE_GRID_WIDTH;
    //表格线宽度
    private final int LINE_WIDTH = 2;

    //表格中默认数据
    private final int DEFAULT_VALUE = 0;
    //白色方格
    private final int WHITE = 1;
    //黑色方格
    private final int BLACK = 2;

    //蚂蚁头方向
    private final int ANT_LEFT = 3;
    private final int ANT_TOP = 4;
    private final int ANT_RIGHT = 5;
    private final int ANT_BOTTOM = 6;

    // 表格宽、高数量
    private final int size = 300;

    //屏幕中间点
    private int centerX, centerY;


    //自动下一步间隔时间（毫秒）
    private int delayMillis;

    //“蚂蚁”图片
    private Bitmap antBitmap;
    private Rect bitmapRect;

    //表格数据
    private int[][] grid;
    //当前蚂蚁处于表格中的下标
    private int currentX;
    private int currentY;
    //当前蚂蚁头方向
    private int currentDir;

    //蚂蚁走过的表格下标最值
    int minX, maxX, minY, maxY;

    //是否自动下一步
    boolean autoNext = false;

    private Rect rect;
    private Paint paint;

    private Handler handler;
    private Scroller mScroller;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        handler = new Handler(Looper.getMainLooper());
        //设置背景色
        setBackgroundColor(0x88888888);

        rect = new Rect();
        bitmapRect = new Rect();
        paint = new Paint();

        initData();

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ant);
        antBitmap = ImageUtils.drawable2Bitmap(drawable);

        mScroller = new Scroller(getContext());
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //绘制完成后、移动至表格中间
                getViewTreeObserver().removeOnPreDrawListener(this);
                scrollToCenter();
                return true;
            }
        });
    }

    private void initData() {
        grid = new int[size][size];
        //蚂蚁初始位置为表格中间
        currentX = size / 2;
        currentY = size / 2;
        //蚂蚁初始方向为“右”
        currentDir = ANT_RIGHT;
        minX = currentX;
        maxX = currentX;
        minY = currentY;
        maxY = currentY;
        //默认不自动下一步
        autoNext = false;
        //默认自动下一步间隔为5ms
        delayMillis = 5;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        centerX = widthSize / 2;
        centerY = heightSize / 2;
    }


    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public void setSingleGridWidth(int width) {
        int x = (width - singleGridWidth) * currentX;
        int y = (width - singleGridWidth) * currentY;
        this.singleGridWidth = width;
        requestLayout();
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), x, y, 0);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < size; x++) {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(LINE_WIDTH);
            //绘制表格竖线
            canvas.drawLine(singleGridWidth * x, 0, singleGridWidth * x, singleGridWidth * (size - 1), paint);
            for (int y = 0; y < size; y++) {
                paint.setColor(Color.RED);
                paint.setStrokeWidth(LINE_WIDTH);
                //绘制表格横线
                canvas.drawLine(0, singleGridWidth * y, singleGridWidth * (size - 1), singleGridWidth * y, paint);
                rect.set(x * (int) singleGridWidth + LINE_WIDTH, y * (int) singleGridWidth + LINE_WIDTH, (x + 1) * (int) singleGridWidth - LINE_WIDTH,
                        (y + 1) * (int) singleGridWidth - LINE_WIDTH);
                //绘制单个表格颜色
                if (grid[x][y] == WHITE) {
                    paint.setColor(Color.WHITE);
                    canvas.drawRect(rect, paint);
                } else if (grid[x][y] == BLACK) {
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(rect, paint);
                }

                //绘制当前蚂蚁（位置、头方向）
                if (x == currentX && y == currentY) {
                    int degrees = 0;
                    if (currentDir == ANT_TOP) {
                        degrees = -90;
                    } else if (currentDir == ANT_BOTTOM) {
                        degrees = 90;
                    } else if (currentDir == ANT_LEFT) {
                        degrees = 180;
                    }
                    Bitmap temp = ImageUtils.rotate(antBitmap, degrees);
                    bitmapRect.set(0, 0, temp.getWidth(), temp.getHeight());
                    canvas.drawBitmap(temp, bitmapRect, rect, paint);
                }
            }
        }
        //绘制蚂蚁走过的矩形范围
        rect.set(minX * singleGridWidth, minY * singleGridWidth, (maxX + 1) * singleGridWidth, (maxY + 1) * singleGridWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(rect, paint);
    }

    //设置蚂蚁移动下一步
    public void autoNext() {
        autoNext = true;
        next();
    }

    //停止下一步停止
    public void stop() {
        autoNext = false;
    }

    //蚂蚁走下一步
    public void goNext() {
        autoNext = false;
        next();
    }

    //格式化表格
    public void reset() {
        initData();
        invalidate();
    }

    //计算蚂蚁下一位置以及修改当前位置颜色
    private void next() {
        int lastX = currentX;
        int lastY = currentY;
        if (grid[currentX][currentY] == DEFAULT_VALUE || grid[currentX][currentY] == WHITE) {
            switch (currentDir) {
                case ANT_LEFT:
                    currentY = currentY - 1;
                    currentDir = ANT_TOP;
                    break;
                case ANT_TOP:
                    currentX = currentX + 1;
                    currentDir = ANT_RIGHT;
                    break;
                case ANT_RIGHT:
                    currentY = currentY + 1;
                    currentDir = ANT_BOTTOM;
                    break;
                case ANT_BOTTOM:
                    currentX = currentX - 1;
                    currentDir = ANT_LEFT;
                    break;
                default:
            }
        } else if (grid[currentX][currentY] == BLACK) {
            switch (currentDir) {
                case ANT_LEFT:
                    currentY = currentY + 1;
                    currentDir = ANT_BOTTOM;
                    break;
                case ANT_TOP:
                    currentX = currentX - 1;
                    currentDir = ANT_LEFT;
                    break;
                case ANT_RIGHT:
                    currentY = currentY - 1;
                    currentDir = ANT_TOP;
                    break;
                case ANT_BOTTOM:
                    currentX = currentX + 1;
                    currentDir = ANT_RIGHT;
                    break;
                default:
            }
        }

        if (grid[lastX][lastY] == DEFAULT_VALUE || grid[lastX][lastY] == WHITE) {
            grid[lastX][lastY] = BLACK;
        } else if (grid[lastX][lastY] == BLACK) {
            grid[lastX][lastY] = WHITE;
        }

        if (minX > currentX) {
            minX = currentX;
        }
        if (maxX < currentX) {
            maxX = currentX;
        }
        if (minY > currentY) {
            minY = currentY;
        }
        if (maxY < currentY) {
            maxY = currentY;
        }
        invalidate();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (autoNext) {
                    next();
                }
            }
        }, delayMillis);
    }

    int mLastDirY, mLastDirX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastDirY = y;
                mLastDirX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                //只有一个手指的时候才有移动的操作
                int dy = mLastDirY - y;
                int dx = mLastDirX - x;
                mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 0);
                invalidate();
                mLastDirY = y;
                mLastDirX = x;
                break;
            default:
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    //滚动到中心点
    private void scrollToCenter() {
        int sX = currentX * singleGridWidth - centerX;
        int sY = currentY * singleGridWidth - centerY;
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), sX, sY, 0);
    }

}
