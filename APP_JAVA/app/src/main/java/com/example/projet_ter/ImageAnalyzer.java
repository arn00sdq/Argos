package com.example.projet_ter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.argos.utils.FrameAnalyzer;
import com.argos.utils.PointOfInterest;
import com.argos.utils.TargetZone;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class ImageAnalyzer implements Runnable {

    private static String TAG = "Projet_TER::ImageAnalyzer";

    public static interface ImageAnalyzerListener {
        public default void onImageAnalyzerStarted() {
        }

        public default void onImageAnalyzeComplete(List<PointOfInterest> POIs) {
        }

        public default void onImageDrawComplete() {
        }
    }

    private final TextureView mTextureView;
    private final ImageReader mImageReader;
    private FrameAnalyzer mFrameAnalyzer;
    private ImageAnalyzerListener mImageAnalyzerListener;
    private Mat rgbMatrix;
    private Bitmap mBitmapImage;
    private List<PointOfInterest> mPOIs;

    public ImageAnalyzer(TextureView textureView, ImageReader imageReader) {
        super();
        this.mImageReader = imageReader;
        this.mTextureView = textureView;
    }

    public void setImageAnalyzerListener(ImageAnalyzerListener imageAnalyzerListener) {
        this.mImageAnalyzerListener = imageAnalyzerListener;
    }

    public void setFrameAnalyzer(FrameAnalyzer frameAnalyzer) {
        this.mFrameAnalyzer = frameAnalyzer;
    }

    public List<PointOfInterest> getPOIs() {
        return this.mPOIs;
    }

    public Bitmap getBitmap() {
        return this.mBitmapImage;
    }
/*
    public static byte[] yuvImageToByteArray(Image image) {

        assert(image.getFormat() == ImageFormat.YUV_420_888);

        int width = image.getWidth();
        int height = image.getHeight();

        Image.Plane[] planes = image.getPlanes();
        byte[] result = new byte[width * height * 3 / 2];

        int stride = planes[0].getRowStride();
        assert (1 == planes[0].getPixelStride());
        if (stride == width) {
            planes[0].getBuffer().get(result, 0, width*height);
        }
        else {
            for (int row = 0; row < height; row++) {
                planes[0].getBuffer().position(row*stride);
                planes[0].getBuffer().get(result, row*width, width);
            }
        }

        stride = planes[1].getRowStride();
        assert (stride == planes[2].getRowStride());
        int pixelStride = planes[1].getPixelStride();
        assert (pixelStride == planes[2].getPixelStride());
        byte[] rowBytesCb = new byte[stride];
        byte[] rowBytesCr = new byte[stride];

        for (int row = 0; row < height/2; row++) {
            int rowOffset = width*height + width/2 * row;
            planes[1].getBuffer().position(row*stride);
            planes[1].getBuffer().get(rowBytesCb);
            planes[2].getBuffer().position(row*stride);
            planes[2].getBuffer().get(rowBytesCr);

            for (int col = 0; col < width/2; col++) {
                result[rowOffset + col*2] = rowBytesCr[col*pixelStride];
                result[rowOffset + col*2 + 1] = rowBytesCb[col*pixelStride];
            }
        }
        return result;
    }

    public static Bitmap convertYUV(byte[] data, int width, int height, Rect crop) {
        if (crop == null) {
            crop = new Rect(0, 0, width, height);
        }
        Bitmap image = Bitmap.createBitmap(crop.width(), crop.height(), Bitmap.Config.ARGB_8888);
        int yv = 0, uv = 0, vv = 0;
        for (int y = crop.top; y < crop.bottom; y += 1) {
            for (int x = crop.left; x < crop.right; x += 1) {
                yv = data[y * width + x] & 0xff;
                uv = (data[width * height + (x / 2) * 2 + (y / 2) * width + 1] & 0xff) - 128;
                vv = (data[width * height + (x / 2) * 2 + (y / 2) * width] & 0xff) - 128;
                image.setPixel(x, y, convertPixel(yv, uv, vv));
            }
        }
        return image;
    }

    public static int convertPixel(int y, int u, int v) {
        int r = (int) (y + 1.13983f * v);
        int g = (int) (y - .39485f * u - .58060f * v);
        int b = (int) (y + 2.03211f * u);
        r = (r > 255) ? 255 : Math.max(r, 0);
        g = (g > 255) ? 255 : Math.max(g, 0);
        b = (b > 255) ? 255 : Math.max(b, 0);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static Bitmap ImageToBitmap(Image image) {
        // Transformation of the image in YUV_420_888 format into an ByteArray
        byte[] data = yuvImageToByteArray(image);
        // Buffer to Bitmap
        Bitmap bitmap = convertYUV(data, image.getWidth(), image.getHeight(), null);
        if (bitmap == null) {
            Log.d(TAG, "Bitmap is null ");
        }
        return bitmap;
    }
*/
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

    private final void draw() {
        // Create bitmap from BGR matrix
        Utils.matToBitmap(this.rgbMatrix, this.mBitmapImage);
        // Obtain the canvas and draw the bitmap on top of it
        final Canvas canvas = this.mTextureView.lockCanvas();
        canvas.drawBitmap(this.mBitmapImage, null, new Rect(0, 0, this.mTextureView.getWidth(), this.mTextureView.getHeight()), null);
        mTextureView.unlockCanvasAndPost(canvas);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try {
            if (this.mImageAnalyzerListener != null) {
                this.mImageAnalyzerListener.onImageAnalyzerStarted();
            }
            // Convert image to Bitmap to opencv Mat
            Image image = this.mImageReader.acquireLatestImage();
            Log.d(TAG, "Texture height = " + this.mTextureView.getHeight());
            Log.d(TAG, "Image height = " + image.getHeight());
            /*this.mBitmapImage = ImageToBitmap(image);
            // Close a fast as possible to release memory
            // Can make freezes if it is not done
            image.close();
            this.rgbMatrix = new Mat(this.mBitmapImage.getWidth(), this.mBitmapImage.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(this.mBitmapImage, this.rgbMatrix);*/
            Mat Yuv420Mat = new Mat(image.getHeight() * 3 / 2, image.getWidth(), CvType.CV_8UC1);
            Mat bgrMat = new Mat(image.getHeight() * 3 / 2, image.getWidth(), CvType.CV_8UC3);
            imageToMat(image, Yuv420Mat, null, null);
            Imgproc.cvtColor(Yuv420Mat, bgrMat, Imgproc.COLOR_YUV420p2BGR);
            Core.transpose(bgrMat, bgrMat);
            Core.flip(bgrMat, bgrMat, 1);
            image.close();
            this.rgbMatrix = bgrMat;
            this.mBitmapImage = Bitmap.createBitmap(bgrMat.width(), bgrMat.height(), Bitmap.Config.ARGB_8888);
            Log.d(TAG, "Bitmap height = " + this.mBitmapImage.getHeight());
            //this.mBitmapImage = Bitmap.createBitmap(this.mTextureView.getWidth(), this.mTextureView.getHeight(), Bitmap.Config.ARGB_8888);
            //Utils.matToBitmap(Yuv420Mat, this.mBitmapImage);
            // Draw the bimap on the canvas
            this.draw();
            //this.mCanvas.drawBitmap(this.mBitmapImage, new Matrix(), null);
            if (this.mImageAnalyzerListener != null) {
                this.mImageAnalyzerListener.onImageDrawComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
