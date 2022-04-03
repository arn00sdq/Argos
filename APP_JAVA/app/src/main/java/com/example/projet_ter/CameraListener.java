package com.example.projet_ter;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.argos.utils.FrameAnalyzer;
import com.argos.utils.PointOfInterest;
import com.argos.utils.TargetZone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CameraListener {

    private static final String TAG = "ProjectTER::Camera";
    private final Activity context;

    private final FrameAnalyzer frameAnalyzer = new FrameAnalyzer();

    private final TextureView mTextureView;
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
            try {
                // The listener as been bind to the SurfaceTexture
                setupCamera(width, height);
                connectCamera();
                Log.d(TAG, "Surface Available");
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
            Log.d(TAG, "Suface updated");
            /*
            Bitmap frame = Bitmap.createBitmap(mTextureView.getWidth(), mTextureView.getHeight(), Bitmap.Config.ARGB_8888);
            mTextureView.getBitmap(frame);
            Mat frameMat = new Mat (frame.getWidth(), frame.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(frame, frameMat);
            //List<TargetZone> poiList = frameAnalyzer.getTargetZonesFromImage(frameMat);
            Imgproc.rectangle(frameMat, new Point(500, 100), new Point(1000, 150), new Scalar(255, 0, 0));
            Utils.matToBitmap(frameMat, frame);
            Log.d(TAG, String.valueOf(poiList.size()));
            */
        }
    };
    private ImageReader mImageReader;
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Log.d(TAG, "imageGot");
            ImageAnalyzer imageAnalyzer = new ImageAnalyzer(mTextureView, imageReader);
            imageAnalyzer.setImageAnalyzerListener(mImageAnalyserListener);
            mBackgroundHandler.post(imageAnalyzer);
        }
    };

    private ImageAnalyzer mImageAnalyzer;
    private ImageAnalyzer.ImageAnalyzerListener mImageAnalyserListener = new ImageAnalyzer.ImageAnalyzerListener() {
        @Override
        public void onImageDrawComplete() {
            Log.d(TAG, "onImageDrawComplete");
        }
    };

    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            try {
                mCameraDevice = cameraDevice;
                startPreview();
                Log.d(TAG, "Camera device opened");
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "Camera device not open");
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

    };

    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;

    private String mCameraId;
    private Size mPreviewSize;
    private Size mImageSize;
    private final Range<Integer> fpsRange = new Range<>(30, 30);

    private CaptureRequest.Builder mCaptureRequestBuilder;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }
    private static class CompareSizeByArea implements Comparator<android.util.Size> {

        @Override
        public int compare(Size lsize, Size rsize) {
            return Long.signum( (long) lsize.getWidth() * lsize.getHeight() / (long) rsize.getWidth() * rsize.getHeight());
        }
    }

    private boolean analyseStarted = false;

    private List<PointOfInterest> poiList = new ArrayList<>();

    private int startX;
    private int startY;

    /**
     * Constructor
     * @param context The app activity
     * @param textureView The TextureView context
     */
    public CameraListener(Activity context, TextureView textureView) {
        this.context = context;
        this.mTextureView = textureView;

        /*
        this.mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(analyseStarted && poiList.size() > 0) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY() - mTextureView.getLayoutParams().height / 2 - rgba_matrix.rows() / 2;
                    System.out.println(x + " x, " + y + " y");
                    System.out.println(poiList);
                    System.out.println("IMG size x = " + rgba_matrix.rows() + ", IMG size y = " + rgba_matrix.cols());
                    System.out.println("Surface size x = " + mTextureView.getLayoutParams().width + " Surface view size y = " + mTextureView.getLayoutParams().height);
                    /*Optional<PointOfInterest> poi = getPoiAt(x, y);
                    if(poi.isPresent()) {
                        new carotDataDialog(camera_view.getContext(), "TEST").show();
                    }
                } else {
                    analyseStarted = true;
                }
                return true;
            }
        });
        */
    }

    /**
     * This function is called each time a frame is send by the camera
     *
     * @param inputFrame The frame
     * @return the matrix to display
     */
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Size original_size;

        this.rgba_matrix = inputFrame.rgba();
        this.gray_matrix = inputFrame.gray();

        original_size = this.rgba_matrix.size();

        // Rotate the image
        Core.rotate(this.rgba_matrix, this.rgba_matrix, Core.ROTATE_90_CLOCKWISE);
        // Getting the new ratio
        double img_ratio = (double) (original_size.width / this.rgba_matrix.size().width);
        // Resizing the mat with the new ratio
        Size new_size = new Size(this.rgba_matrix.size().width * img_ratio,
                Math.floor((int) this.rgba_matrix.size().height * img_ratio));
        Imgproc.resize(this.rgba_matrix, this.rgba_matrix, new_size, Imgproc.INTER_CUBIC);
        // getting the sub mat to match the mat size needed
        this.rgba_matrix = this.rgba_matrix.submat((int) ((new_size.height / 2) - (original_size.height / 2)),
                (int) ((new_size.height / 2) + (original_size.height / 2)),
                (int) ((new_size.width / 2) - (original_size.width / 2)),
                (int) ((new_size.width / 2) + (original_size.width / 2)));

        if (this.analyseStarted) {
            // Start the analyze
            this.poiList = PointOfInterest.toPOIList(this.frameAnalyzer.getTargetZonesFromImage(this.rgba_matrix));
            //
            // ! On passe une matrice rgba (potentiellement 4 channels) au lieu d'une matrice rgb (3 channel)
            // A verifier
            List<PointOfInterest> poiArray = PointOfInterest.toPOIList(this.frameAnalyzer.getTargetZonesFromImage(this.rgba_matrix));
            System.out.println("TAILLE " + poiArray.size());
            // Draw the data
            this.poiList.forEach( poi -> {
                Imgproc.rectangle(this.rgba_matrix, new Point(poi.getX_coord(), poi.getY_coord()),
                        new Point(poi.getX_coord() + poi.getWidth(), poi.getY_coord() + poi.getHeight()),
                        new Scalar(0, 0, 255), 5);
            });
        }

        return this.rgba_matrix;
    }*/

    /**
     * Start the camera render pipeline
     * @throws CameraAccessException if the camera cannot be accessed (Wrong camera id for exemple)
     */
    public void startCamera() throws CameraAccessException {
        // starting the pipeline thread
        this.startBackgroundThread();
        if (this.mTextureView.isAvailable()) {
            this.setupCamera(this.mTextureView.getWidth(), this.mTextureView.getHeight()); // throws CameraAccesException
            this.connectCamera();                                                          //
            Log.d(TAG, "available");
        } else {
            // the texture view is not available
            // Binding the listener to it
            this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
            Log.d(TAG, "not available");
        }
    }

    /**
     * Stop the camera render pipeline
     */
    public void closeCamera() throws InterruptedException {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.close();
            this.mCameraDevice = null;
        }
        this.stopBackgroundThread();
    }

    /**
     * Setup the camera render pipeline parameter
     * @param width the width of the surface destination
     * @param height the height of the serface destination
     */
    private void setupCamera(int width, int height) throws CameraAccessException{
        CameraManager cameraManager = (CameraManager) this.context.getSystemService(Context.CAMERA_SERVICE);
        // Getting the Camera list
        // The camera are represented by a string ID
        String[] cameraIds = cameraManager.getCameraIdList();
        // Getting the back camera
        // Iterates the cameras until we get the id of the back camera
        int i = 0;
        CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIds[i]);
        // Optie possible mais je comprend pas
        while (i < cameraIds.length && cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
            i++;
            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIds[i]);
        }
        // Getting camera format
        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        // Getting the camera rotation to handle portrait and landscape mode
        int deviceOrientation = this.context.getWindowManager().getDefaultDisplay().getRotation();
        int totalRotation = sensorToRotation(cameraCharacteristics, deviceOrientation);
        boolean swapRotation = totalRotation == 90 || totalRotation == 270;
        int rotatedWidth = width;
        int rotatedHeight = height;
        if (swapRotation) {
            rotatedWidth = height;
            rotatedHeight = width;
        }
        // set the preview format to the BEST format available
        this.mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
        this.mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.YUV_420_888), rotatedWidth, rotatedHeight);
        Log.d(TAG, "OutputSizeOption = " + Arrays.toString(map.getOutputSizes(SurfaceTexture.class)));
        Log.d(TAG, "OutputSizeOption = " + Arrays.toString(map.getOutputSizes(ImageFormat.YUV_420_888)));
        // setting the imageReader
        // Despite what the documentation says, the JPEG format is not available on all devices.
        // Use the YUV_420_888 format instead.
        this.mImageReader = ImageReader.newInstance(this.mImageSize.getWidth(), this.mImageSize.getHeight(), ImageFormat.YUV_420_888, 1);
        this.mImageReader.setOnImageAvailableListener(this.mOnImageAvailableListener, this.mBackgroundHandler);
        Log.d(TAG, "Width = " + rotatedWidth);
        Log.d(TAG, "Height = " + rotatedHeight);
        Log.d(TAG, "swap = " + swapRotation);
        Log.d(TAG, "Preview Width = " + this.mPreviewSize.getWidth());
        Log.d(TAG, "Preview Height = " + this.mPreviewSize.getHeight());
        this.mCameraId = cameraIds[i];

        Log.d(TAG, "Camera id got " + this.mCameraId + " length " + cameraIds.length);
    }

    /**
     * Connect the camera to the render pipeline
     * @throws CameraAccessException if the camera cannot be accessed (Wrong camera id for exemple)
     */
    private void connectCamera() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) this.context.getSystemService(Context.CAMERA_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this.context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(this.mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            } else {
                if (context.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this.context, "This Application can not run without camera services.", Toast.LENGTH_SHORT).show();
                }
                this.context.requestPermissions(new String[] {Manifest.permission.CAMERA}, MainActivity.REGUEST_CAMERA_PERMISSION_RESULT);
            }
        } else {
            cameraManager.openCamera(this.mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
        }
    }

    /**
     * Start the pipeline camera render loop
     * @throws CameraAccessException
     */
    private void startPreview() throws CameraAccessException {
        // Getting the surface to preview
        SurfaceTexture surfaceTexture = this.mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(this.mPreviewSize.getWidth(), this.mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        // Binding the surface to the pipeline render and
        this.mCaptureRequestBuilder = this.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        //this.mCaptureRequestBuilder.addTarget(previewSurface);
        this.mCaptureRequestBuilder.addTarget(this.mImageReader.getSurface());
        this.mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, this.fpsRange);

        // Starting the capture sessions
        this.mCameraDevice.createCaptureSession(Arrays.asList(/*previewSurface,*/ this.mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                try {
                    cameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                Toast.makeText(context.getApplicationContext(), "Unable to setup the camera preview", Toast.LENGTH_SHORT).show();
            }
        }, null);
    }

    /**
     * Return the frame analyser object
     * @return the frame analyser object
     */
    public FrameAnalyzer getFrameAnalyzer() {
        return this.frameAnalyzer;
    }

    /**
     * Get the poi on the preview by an x and a y
     * @param x the x coord
     * @param y the y coord
     * @return the poi touched
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Optional<PointOfInterest> getPoiAt(int x, int y) {
        if (this.poiList == null) return null;
        return this.poiList.stream().filter( poi -> {
            return poi.getX_coord() > x && poi.getX_coord() + poi.getWidth() < x
                    && poi.getY_coord() > y && poi.getY_coord() + poi.getHeight() < y;
        }).findFirst();
    }

    /**
     * Start the pipeline thread
     */
    private void startBackgroundThread() {
        this.mBackgroundHandlerThread = new HandlerThread("Projet_TER:CameraPipelineThread");
        this.mBackgroundHandlerThread.start();
        this.mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    /**
     * Stop the pipeline thread
     */
    private void stopBackgroundThread() throws InterruptedException {
        // Some device pause before destroy
        // We handle this case by checking if the thread is already clear
        if (this.mBackgroundHandlerThread != null) {
            this.mBackgroundHandlerThread.quitSafely();
            this.mBackgroundHandlerThread.join();
            this.mBackgroundHandlerThread = null;
            this.mBackgroundHandler = null;
        }
    }

    /**
     * Compute the rotation from a camera
     * @param cameraCharacteristics the camera characteristics
     * @param deviceOrientation the current device orientation
     * @return The current orientation
     */
    private static int sensorToRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(sensorOrientation);
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    /**
     * Compute the optimal format from a Size list and a width and a height target
     * @param choises the size list available
     * @param width the width target
     * @param height the height target
     * @return the optimal size from the list
     */
    private static Size chooseOptimalSize(Size[] choises, int width, int height) {
        List<Size> bigEnough = new ArrayList<>();
        for(Size option : choises) {
            if (option.getHeight() == option.getWidth() * height / width && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choises[0];
        }
    }

}
