package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {

    private String imageUrl;
    private OnImageLoaderListener mImageLoaderListener;

    public ImageDownloader(String imageUrl, @NonNull OnImageLoaderListener mImageLoaderListener) {
        this.imageUrl = imageUrl;
        this.mImageLoaderListener = mImageLoaderListener;
    }

    public interface OnImageLoaderListener {
        void onError(ImageError error);
        void onComplete(Bitmap bitmap);
    }

    public void start() {
        if(!TextUtils.isEmpty(this.imageUrl)) {
            download(this.imageUrl);
        } else {
            ImageError error = new ImageError("imageUrl is empty!")
                    .setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
            this.mImageLoaderListener.onError(error);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void download(@NonNull final String imageUrl) {
        new AsyncTask<String, Bitmap, Bitmap>() {

            private ImageError error;

            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap bitmap = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                ByteArrayOutputStream out = null;
                try {
                    connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                    is = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (Throwable e) {
                    if (!this.isCancelled()) {
                        error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                        this.cancel(true);
                    }
                } finally {
                    try {
                        if (connection != null)
                            connection.disconnect();
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                        if (is != null)
                            is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return bitmap;
            }

            @Override
            protected void onCancelled() {
                mImageLoaderListener.onError(error);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result == null) {
                    mImageLoaderListener.onError(new ImageError("downloaded file could not be decoded as bitmap")
                            .setErrorCode(ImageError.ERROR_DECODE_FAILED));
                } else {
                    mImageLoaderListener.onComplete(result);
                }
                System.gc();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static final class ImageError extends Throwable {

        private int errorCode;

        static final int ERROR_GENERAL_EXCEPTION = -1;

        public static final int ERROR_INVALID_FILE = 0;

        static final int ERROR_DECODE_FAILED = 1;

        ImageError(@NonNull String message) {
            super(message);
        }

        ImageError(@NonNull Throwable error) {
            super(error.getMessage(), error.getCause());
            this.setStackTrace(error.getStackTrace());
        }

        ImageError setErrorCode(int code) {
            this.errorCode = code;
            return this;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }
}
