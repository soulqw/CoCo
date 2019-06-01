package com.qw.photo.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.qw.photo.Utils;

/**
 * @author cd5160866
 * @date 2019-06-01
 */
public class CompressHelper {

    private CompressListener listener;

    private Bitmap bitmap;

    public void compress(final String path, @NonNull final CompressListener listener) {
        listener.onStart(path);
        WorkThread.addWork(new Runnable() {
            @Override
            public void run() {
                bitmap = BitmapFactory.decodeFile(path);
                if (null != bitmap) {
                    bitmap = Utils.INSTANCE.zoomBitmap(bitmap, 0.5f);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFinish(bitmap);
                        }
                    });
                } else {
                    listener.onError(new NullPointerException("get bitmap with null result"));
                }
            }
        });
    }


    public interface CompressListener {

        void onStart(String path);

        void onFinish(Bitmap result);

        void onError(Exception e);
    }
}
