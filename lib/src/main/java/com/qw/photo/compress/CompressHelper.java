package com.qw.photo.compress;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.qw.photo.callback.CompressListener;
import com.qw.photo.constant.CompressStrategy;


/**
 * @author cd5160866
 */
public class CompressHelper {

    private Bitmap bitmap;

    private int degree;

    private CompressStrategy strategy;

    public static CompressHelper getDefault() {
        return new CompressHelper()
                .strategy(CompressStrategy.MATRIX)
                .degree(50);
    }

    public CompressHelper degree(int degree) {
        this.degree = degree;
        return this;
    }

    public CompressHelper strategy(CompressStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public void compress(@NonNull final String path, @NonNull final CompressListener listener) {
        listener.onStart(path);
        WorkThread.addWork(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = CompressFactory.INSTANCE
                            .create(strategy)
                            .compress(path, degree);
                } catch (Exception e) {
                    listener.onError(e);
                    return;
                }
                //check result
                if (null == bitmap) {
                    listener.onError(new NullPointerException("try to compress bitmap get a null result"));
                    return;
                }
                //post result
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinish(bitmap);
                    }
                });
            }
        });
    }
}
