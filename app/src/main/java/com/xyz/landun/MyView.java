package com.xyz.landun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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

    final int DEFAULT_SINGLE_GRID_WIDTH = 30;
    int singleGridWidth = DEFAULT_SINGLE_GRID_WIDTH;
    final int LINE_WIDTH = 2;

    final int DEFAULT_VALUE = 0;
    final int WHITE = 1;
    final int BLACK = 2;
    final int ANT_LEFT = 3;
    final int ANT_TOP = 4;
    final int ANT_RIGHT = 5;
    final int ANT_BOTTOM = 6;
    Bitmap antBitmap;
    Rect bitmapRect;

    final int size = 150;

    private int delayMillis = 10;

    int[][] grid = new int[size][size];
    int currentX;
    int currentY;
    int currentDir;

    Handler handler;
    Scroller mScroller;

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
        handler = new Handler();
        setBackgroundColor(0x88888888);

        rect = new Rect();
        bitmapRect = new Rect();
        paint = new Paint();

        currentDir = ANT_RIGHT;
        currentX = size / 2;
        currentY = size / 2;
        minX = currentX;
        maxX = currentX;
        minY = currentY;
        maxY = currentY;

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ant);
        antBitmap = ImageUtils.drawable2Bitmap(drawable);

        mScroller = new Scroller(getContext());
        goNext();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                scrollToCenter();
                return true;
            }
        });

    }

    int centerX, centerY;

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

    Rect rect;
    Paint paint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < size; x++) {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(LINE_WIDTH);
            canvas.drawLine(singleGridWidth * x, 0, singleGridWidth * x, singleGridWidth * (size - 1), paint);
            for (int y = 0; y < size; y++) {
                paint.setColor(Color.RED);
                paint.setStrokeWidth(LINE_WIDTH);
                canvas.drawLine(0, singleGridWidth * y, singleGridWidth * (size - 1), singleGridWidth * y, paint);
                rect.set(x * (int) singleGridWidth + LINE_WIDTH, y * (int) singleGridWidth + LINE_WIDTH, (x + 1) * (int) singleGridWidth - LINE_WIDTH,
                        (y + 1) * (int) singleGridWidth - LINE_WIDTH);
                if (grid[x][y] == WHITE) {
                    paint.setColor(Color.WHITE);
                    canvas.drawRect(rect, paint);
                } else if (grid[x][y] == BLACK) {
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(rect, paint);
                }
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
        rect.set(minX * singleGridWidth, minY * singleGridWidth, (maxX + 1) * singleGridWidth, (maxY + 1) * singleGridWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(rect, paint);
    }

    int lastX;
    int lastY;
    int minX, maxX, minY, maxY;


    private void goNext() {
        lastX = currentX;
        lastY = currentY;
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
                goNext();
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

    private double spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    private void scrollToCenter() {
        int sX = currentX * singleGridWidth - centerX;
        int sY = currentY * singleGridWidth - centerY;
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), sX, sY, 0);
    }

}
