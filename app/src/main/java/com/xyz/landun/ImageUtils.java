package com.xyz.landun;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

public class ImageUtils {




    public static Drawable convertDrawable(Resources resources, int resId) {
        return convertDrawable(resources, resId, 1.0f);
    }

    public static Drawable convertDrawable(Resources resources, int resId, float p) {
        Drawable drawable = null;
        if (resources != null) {
            if (p <= 0) {
                p = 1f;
            }
            drawable = resources.getDrawable(resId);
            drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() * p), (int) (drawable.getMinimumHeight() * p));

        }
        return drawable;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }


    public static Bitmap rotate(Bitmap b, float degrees) {
        if (degrees != 0 && b != null) {
            try {
                Matrix m = new Matrix();
                m.setRotate(degrees, (float) b.getWidth() / 2,
                        (float) b.getHeight() / 2);

                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                if (b != b2) {
                    b = b2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    public static Bitmap scaleBitmap(Bitmap bitmapOrg, int newWidth, int newHeight) {
        if (bitmapOrg == null) {
            return null;
        }
        Matrix m = new Matrix();
        int bitmapWidth = bitmapOrg.getWidth();
        int bitmapHeight = bitmapOrg.getHeight();
        float scaleWidth = ((float) newWidth) / bitmapWidth;
        float scaleHeight = ((float) newHeight) / bitmapHeight;
        Bitmap newBitmap = null;
        try {
            if (scaleWidth != 1.0f && scaleWidth > 0) {
                m.postScale(scaleWidth, scaleHeight);
                newBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapWidth, bitmapHeight, m, true);
            }
        } catch (Exception e) {
        } catch (OutOfMemoryError error) {
        }
        if (newBitmap != null) {
            if (bitmapOrg != null && !bitmapOrg.isRecycled() && !bitmapOrg.equals(newBitmap)) {
                bitmapOrg.recycle();
            }
            return newBitmap;
        } else {
            return bitmapOrg;
        }
    }


}