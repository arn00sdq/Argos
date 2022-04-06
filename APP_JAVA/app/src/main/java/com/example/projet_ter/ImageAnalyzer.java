package com.example.projet_ter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
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
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.Arrays;
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
    /*private final ImageReader mImageReader;
    private final Surface mSurface;*/
    private Image mImage;
    private FrameAnalyzer mFrameAnalyzer;
    private ImageAnalyzerListener mImageAnalyzerListener;
    private Mat rgbMatrix;
    private Bitmap mBitmapImage;
    private List<PointOfInterest> mPOIs;

    /*public ImageAnalyzer(TextureView textureView, ImageReader imageReader) {
        super();
        this.mImageReader = imageReader;
        this.mTextureView = textureView;
    }*/

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
        /*int width = Math.max(this.mBitmapImage.getWidth(), canvas.getWidth());
        int height = Math.max(this.mBitmapImage.getHeight(), canvas.getHeight());*/
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
            /*Image.Plane[] planes = this.mImage.getPlanes();
            //assert(planes[0].getPixelStride() == 1);
            Log.d(TAG, "planes[2].getPixelStride() = " + planes[2].getPixelStride() );
            Log.d(TAG, "Image format = " + this.mImage.getFormat());
            ByteBuffer y_plane = planes[0].getBuffer();
            ByteBuffer u_plane = planes[1].getBuffer();
            ByteBuffer v_plane = planes[2].getBuffer();

            Mat y_mat = new Mat(this.mImage.getHeight(), this.mImage.getWidth(), CvType.CV_8UC1, y_plane);
            Mat u_mat = new Mat(this.mImage.getHeight() / 2, this.mImage.getWidth() / 2, CvType.CV_8UC2, uv_plane);
            this.rgbMatrix = new Mat(this.mImage.getHeight(), this.mImage.getWidth(), CvType.CV_8UC1);
            Imgproc.cvtColorTwoPlane(y_mat, uv_mat, this.rgbMatrix, Imgproc.COLOR_YUV2RGBA_NV21);
            this.mImage.close();*/
            //Imgproc.cvtColor(y_mat, this.rgbMatrix, Imgproc.COLOR_YUV2RGBA_NV21, 4);
            //Imgproc.cvtColor(y_mat, this.rgbMatrix, Imgproc.COLOR_YUV2RGB_I420, 4);
            // Convert image to Bitmap to opencv Mat
            //Image image = this.mImageReader.acquireNextImage();
            //Log.d(TAG, "Texture height = " + this.mTextureView.getHeight());
            //Log.d(TAG, "Image height = " + this.mImage.getHeight());
            /*this.mBitmapImage = ImageToBitmap(image);
            // Close a fast as possible to release memory
            // Can make freezes if it is not done
            image.close();
            this.rgbMatrix = new Mat(this.mBitmapImage.getWidth(), this.mBitmapImage.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(this.mBitmapImage, this.rgbMatrix);*/
            Mat Yuv420Mat = new Mat(this.mImage.getHeight() * 3 / 2, this.mImage.getWidth(), CvType.CV_8UC1);
            Mat bgrMat = new Mat(this.mImage.getHeight() * 3 / 2, this.mImage.getWidth(), CvType.CV_8UC3);
            imageToMat(this.mImage, Yuv420Mat, null, null);
            Imgproc.cvtColor(Yuv420Mat, bgrMat, Imgproc.COLOR_YUV420p2BGR);
            Core.transpose(bgrMat, bgrMat);
            Core.flip(bgrMat, bgrMat, 1);
            //Imgproc.resize(bgrMat, bgrMat, new Size(640, 480), Imgproc.INTER_CUBIC);
            this.mImage.close();
            this.rgbMatrix = bgrMat;
            this.mBitmapImage = Bitmap.createBitmap(this.rgbMatrix.width(), this.rgbMatrix.height(), Bitmap.Config.ARGB_8888);
            //Log.d(TAG, "Bitmap height = " + this.mBitmapImage.getHeight());
            //this.mBitmapImage = Bitmap.createBitmap(this.mTextureView.getWidth(), this.mTextureView.getHeight(), Bitmap.Config.ARGB_8888);
            //Utils.matToBitmap(Yuv420Mat, this.mBitmapImage);
            // Draw the bimap on the canvas
            if (this.mFrameAnalyzer != null) {
                Mat rgb32f4c = new Mat((int) this.rgbMatrix.total(), 1, CvType.CV_32FC3);
                this.rgbMatrix.convertTo(rgb32f4c, CvType.CV_32F);
                Imgproc.cvtColor(rgb32f4c, rgb32f4c, Imgproc.COLOR_BGR2BGRA);
                this.mPOIs = PointOfInterest.toPOIList(this.mFrameAnalyzer.getTargetZonesFromImage(rgb32f4c));
            }
            this.draw();
            //this.mCanvas.drawBitmap(this.mBitmapImage, new Matrix(), null);
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
