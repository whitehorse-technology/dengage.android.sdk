package com.dengage.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;

import com.dengage.sdk.models.CarouselItem;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

class ImageDownloader {

    private Logger logger = Logger.getInstance();
    private Context context;
    private ArrayList<CarouselItem> carousalItems;
    private OnDownloadsCompletedListener onDownloadsCompletedListener;
    private int numberOfImages;
    private static int currentDownloadTaskIndex = 0;
    private CarouselItem currentItem;

    ImageDownloader(Context context, CarouselItem[] carousalItems, @NonNull OnDownloadsCompletedListener onDownloadsCompletedListener) {
        ArrayList<CarouselItem> items = new ArrayList<>();
        for (CarouselItem item : carousalItems) {
            if (!TextUtils.isEmpty(item.getMediaUrl())) {
                items.add(item);
            }
        }
        this.carousalItems = items;
        this.context = context;
        this.onDownloadsCompletedListener = onDownloadsCompletedListener;
        this.numberOfImages = items.size();
    }

    private OnImageLoaderListener mImageLoaderListener = new OnImageLoaderListener() {
        @Override
        public void onError(ImageError error) {
            updateDownLoad(null);
        }

        @Override
        public void onComplete(String resultPath) {
            updateDownLoad(resultPath);
        }
    };

    private void updateDownLoad(String filePath) {
        for (int i = (currentDownloadTaskIndex + 1); i < carousalItems.size(); i++) {
            if (!TextUtils.isEmpty(carousalItems.get(i).getMediaUrl())) {
                currentDownloadTaskIndex = i;
                currentItem = carousalItems.get(i);
                downloadImage(currentItem.getMediaUrl());
                break;
            }
        }
        --numberOfImages;
        if (numberOfImages < 1 || currentDownloadTaskIndex > carousalItems.size() - 1) {
            onDownloadsCompletedListener.onComplete(carousalItems.toArray(new CarouselItem[0]));
        }
    }

    void startAllDownloads() {
        if (carousalItems != null && carousalItems.size() > 0) {
            for (int i = 0; i < carousalItems.size(); i++) {
                if (!TextUtils.isEmpty(carousalItems.get(i).getMediaUrl())) {
                    currentDownloadTaskIndex = i;
                    currentItem = carousalItems.get(i);
                    downloadImage(currentItem.getMediaUrl());
                    break;
                }
            }
        }
    }

    public interface OnImageLoaderListener {
        /**
         * Invoked if an error has occurred and thus
         * the download did not complete
         *
         * @param error the occurred error
         */
        void onError(ImageError error);

        /**
         * Invoked after the image has been successfully downloaded
         *
         * @param resultPath the downloaded image
         */
        void onComplete(String resultPath);
    }

    public interface OnDownloadsCompletedListener {

        /**
         * invoked after all files are downloaded
         */
        void onComplete(CarouselItem[] items);
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadImage(@NonNull final String imageUrl) {

        new AsyncTask<Void, Integer, String>() {

            private ImageError error;
            private long currentTimeInMillis;


            @Override
            protected void onCancelled() {
                mImageLoaderListener.onError(error);
            }

            @Override
            protected String doInBackground(Void... params) {
                currentTimeInMillis = System.currentTimeMillis();
                Bitmap bitmap;
                String imagePath = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                ByteArrayOutputStream out = null;
                try {
                    connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                    is = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    if (bitmap != null) {
                        int sampleSize = Utils.calculateInSampleSize(bitmap.getWidth(), bitmap.getHeight(), 250, 250);
                        Bitmap bit = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / sampleSize, bitmap.getHeight() / sampleSize, false);
                        imagePath = Utils.saveBitmapToInternalStorage(context, bit, Constants.CAROUSAL_IMAGE_BEGINNING + currentTimeInMillis);
                    }

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
                return imagePath;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    logger.Debug("factory returned a null result");
                    mImageLoaderListener.onError(new ImageError("downloaded file could not be decoded as bitmap")
                            .setErrorCode(ImageError.ERROR_DECODE_FAILED));
                } else {
                    logger.Debug("download complete");
                    if (currentItem != null) {
                        currentItem.setMediaFileLocation(result);
                        currentItem.setMediaFileName(Constants.CAROUSAL_IMAGE_BEGINNING + currentTimeInMillis);
                    }
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