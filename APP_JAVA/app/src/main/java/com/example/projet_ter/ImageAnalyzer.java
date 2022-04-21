package com.example.projet_ter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;

import android.graphics.Rect;

import android.media.Image;
import android.os.Build;

import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.argos.utils.FrameAnalyzer;
import com.argos.utils.PointOfInterest;


import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import org.opencv.core.Scalar;

import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

import java.util.List;

public class ImageAnalyzer implements Runnable {

    private static final String TAG = "Projet_TER::ImageAnalyzer";

    public static interface ImageAnalyzerListener {
        public default void onImageAnalyzerStarted() {
        }

        public default void onImageAnalyzeComplete(List<PointOfInterest> POIs) {
        }

        public default void onImageDrawComplete() {
        }
    }

    private final TextureView mTextureView;
    private Image mImage;
    private FrameAnalyzer mFrameAnalyzer;
    private ImageAnalyzerListener mImageAnalyzerListener;
    private Mat rgbMatrix;
    private Bitmap mBitmapImage;
    private List<PointOfInterest> mPOIs;

    public ImageAnalyzer(TextureView textureView) {
        super();
        this.mTextureView = textureView;
    }

    public void setImageAnalyzerListener(ImageAnalyzerListener imageAnalyzerListener) {
        this.mImageAnalyzerListener = imageAnalyzerListener;
    }

    public void setFrameAnalyzer(FrameAnalyzer frameAnalyzer) {
        this.mFrameAnalyzer = frameAnalyzer;
    }

    public void setImage(Image image) {
        this.mImage = image;
    }

    public List<PointOfInterest> getPOIs() {
        return this.mPOIs;
    }

    public static void imageToMat(final Image image, final Mat mat, byte[] data, byte[] rowData) {
        ByteBuffer buffer;
        int rowStride, pixelStride, width = image.getWidth(), height = image.getHeight(), offset = 0;
        Image.Plane[] planes = image.getPlanes();
        if (data == null || data.length != width * height) data = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
        if (rowData == null || rowData.length != planes[0].getRowStride()) rowData = new byte[planes[0].getRowStride()];
        for (int i = 0; i < planes.length; i++) {
            buffer = planes[i].getBuffer();
            rowStride = planes[i].getRowStride();
            pixelStride = planes[i].getPixelStride();
            int
                    w = (i == 0) ? width : width / 2,
                    h = (i == 0) ? height : height / 2;
            for (int row = 0; row < h; row++) {
                int bytesPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
                if (pixelStride == bytesPerPixel) {
                    int length = w * bytesPerPixel;
                    buffer.get(data, offset, length);
                    // Advance buffer the remainder of the row stride, unless on the last row.
                    // Otherwise, this will throw an IllegalArgumentException because the buffer
                    // doesn't include the last padding.
                    if (h - row != 1)
                        buffer.position(buffer.position() + rowStride - length);
                    offset += length;
                } else {
                    // On the last row only read the width of the image minus the pixel stride
                    // plus one. Otherwise, this will throw a BufferUnderflowException because the
                    // buffer doesn't include the last padding.
                    if (h - row == 1)
                        buffer.get(rowData, 0, width - pixelStride + 1);
                    else
                        buffer.get(rowData, 0, rowStride);
                    for (int col = 0; col < w; col++)
                        data[offset++] = rowData[col * pixelStride];
                }
            }
        }
        mat.put(0, 0, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void draw() {
        if (this.mPOIs != null) {
            // Draw the data
            this.mPOIs.forEach( poi -> {
                Imgproc.rectangle(this.rgbMatrix, new Point(poi.getX_coord(), poi.getY_coord()),
                        new Point(poi.getX_coord() + poi.getWidth(), poi.getY_coord() + poi.getHeight()),
                        new Scalar(0, 0, 255), 5);
            });
        }
        // Create bitmap from BGR matrix
        Utils.matToBitmap(this.rgbMatrix, this.mBitmapImage);
        // Obtain the canvas and draw the bitmap on top of it
        final Canvas canvas = this.mTextureView.lockCanvas();
        canvas.drawBitmap(this.mBitmapImage, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
        mTextureView.unlockCanvasAndPost(canvas);
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try {
            if (this.mImageAnalyzerListener != null) {
                this.mImageAnalyzerListener.onImageAnalyzerStarted();
            }
            Mat Yuv420Mat = new Mat(this.mImage.getHeight() * 3 / 2, this.mImage.getWidth(), CvType.CV_8UC1);
            Mat bgrMat = new Mat(this.mImage.getHeight() * 3 / 2, this.mImage.getWidth(), CvType.CV_8UC3);
            imageToMat(this.mImage, Yuv420Mat, null, null);
            Imgproc.cvtColor(Yuv420Mat, bgrMat, Imgproc.COLOR_YUV420p2BGR);
            Core.transpose(bgrMat, bgrMat);
            Core.flip(bgrMat, bgrMat, 1);
            this.mImage.close();
            this.rgbMatrix = bgrMat;
            this.mBitmapImage = Bitmap.createBitmap(this.rgbMatrix.width(), this.rgbMatrix.height(), Bitmap.Config.ARGB_8888);
            // Draw the bimap on the canvas
            if (this.mFrameAnalyzer != null) {
                Mat rgb32f4c = new Mat((int) this.rgbMatrix.total(), 1, CvType.CV_32FC3);
                this.rgbMatrix.convertTo(rgb32f4c, CvType.CV_32F);
                Imgproc.cvtColor(rgb32f4c, rgb32f4c, Imgproc.COLOR_BGR2BGRA);
                this.mPOIs = this.mFrameAnalyzer.getDetailedPOIsFromImage(rgb32f4c);
            }
            this.draw();
            if (this.mImageAnalyzerListener != null) {
                this.mImageAnalyzerListener.onImageDrawComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.mImage.close();
            this.mImage = null;
        }
    }
}
