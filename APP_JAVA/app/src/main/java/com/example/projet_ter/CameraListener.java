package com.example.projet_ter;

/*
public class CameraListener {

    private static final String TAG = "ProjectTER::Camera";

    private final Activity context;

    private final FrameAnalyzer mFrameAnalyzer = new FrameAnalyzer();

    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;

    private int frameCount=0;
    private long frameTime=0;

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
            Object[] objects=initFPSNew("Measure fps is-->",frameTime,frameCount);
            frameTime=(long)objects[0];
            frameCount=(int)objects[1];
            /*try {
                mCaptureRequestSession.capture(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }*/
            /*
            Bitmap frame = Bitmap.createBitmap(mTextureView.getWidth(), mTextureView.getHeight(), Bitmap.Config.ARGB_8888);
            mTextureView.getBitmap(frame);
            Mat frameMat = new Mat (frame.getWidth(), frame.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(frame, frameMat);
            //List<TargetZone> poiList = frameAnalyzer.getTargetZonesFromImage(frameMat);
            Imgproc.rectangle(frameMat, new Point(500, 100), new Point(1000, 150), new Scalar(255, 0, 0));
            Utils.matToBitmap(frameMat, frame);
            Log.d(TAG, String.valueOf(poiList.size()));
            *//*
        }
    };

    public static Object[] initFPSNew(String message,long startTime,int counter){

        Object[] mObjectTime=new Object[2];
        if(startTime==0){

            startTime=System.currentTimeMillis();
            mObjectTime[0]=startTime;
            counter+=1;
            mObjectTime[1]=counter;
        }else{
            long difference=System.currentTimeMillis()-startTime;
            //We wil check count only after 1 second laps
            double seconds = difference / 1000.0;

            if(seconds>=1)
            {
                Log.v(TAG,message+ counter);
                counter=0;
                mObjectTime[0]=System.currentTimeMillis();
                mObjectTime[1]=counter;

            }else{
                counter++;
                mObjectTime[0]=startTime;
                mObjectTime[1]=counter;
            }

        }
        return mObjectTime;
    }

    private ImageReader mImageReader;
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Log.d(TAG, "imageGot");
            //ImageAnalyzer imageAnalyzer = new ImageAnalyzer(mTextureView, imageReader);
            try {
                Image image = imageReader.acquireNextImage();
                mImageAnalyzer.setImage(image);
                mBackgroundHandler.post(mImageAnalyzer);
            } catch (Exception e) {
                //image.close();
                e.printStackTrace();
            }
        }
    };

    private ImageAnalyzer mImageAnalyzer;
    private ImageAnalyzer.ImageAnalyzerListener mImageAnalyserListener = new ImageAnalyzer.ImageAnalyzerListener() {
        @Override
        public void onImageDrawComplete() {
            //Log.d(TAG, "onImageDrawComplete");
            //mImageAnalyzer = null;
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

    private String mCameraId;
    private Size mPreviewSize;
    private Size mImageSize;
    private final Range<Integer> fpsRange = new Range<>(30, 30);

    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraCaptureSession mCaptureRequestSession;

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
     *//*
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
        *//*
    }

    /**
     * Start the pipeline thread
     *//*
    private void startBackgroundThread() {
        this.mBackgroundHandlerThread = new HandlerThread("Projet_TER:CameraPipelineThread");
        this.mBackgroundHandlerThread.start();
        this.mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    /**
     * Stop the pipeline thread
     *//*
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
/*
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
    }*//*

    /**
     * Start the camera render pipeline
     * @throws CameraAccessException if the camera cannot be accessed (Wrong camera id for exemple)
     *//*
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
     *//*
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
     *//*
    private void setupCamera(int width, int height) throws CameraAccessException{
        CameraManager cameraManager = (CameraManager) this.context.getSystemService(Context.CAMERA_SERVICE);
        // Getting the Camera list
        // The camera are represented by a string ID
        String[] cameraIds = cameraManager.getCameraIdList();
        // Getting the back camera
        // Iterates the cameras until we get the id of the back camera
        if (cameraIds.length == 0) {
            Log.d(TAG, "Error : camera isn't detected.");
        }
        int i = 0;
        CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIds[i]);
        // Optie possible mais je comprend pas
        while (i < cameraIds.length && cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
            i++;
            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIds[i]);
        }
        this.mCameraId = cameraIds[i];
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
        this.mImageReader = ImageReader.newInstance(this.mPreviewSize.getWidth(), this.mPreviewSize.getHeight(), ImageFormat.YUV_420_888, 1);
        this.mImageReader.setOnImageAvailableListener(this.mOnImageAvailableListener, this.mBackgroundHandler);
        Log.d(TAG, "Width = " + rotatedWidth);
        Log.d(TAG, "Height = " + rotatedHeight);
        Log.d(TAG, "swap = " + swapRotation);
        Log.d(TAG, "Preview Width = " + this.mPreviewSize.getWidth());
        Log.d(TAG, "Preview Height = " + this.mPreviewSize.getHeight());

        Log.d(TAG, "Camera id got " + this.mCameraId + " length " + cameraIds.length);
    }

    /**
     * Connect the camera to the render pipeline
     * @throws CameraAccessException if the camera cannot be accessed (Wrong camera id for exemple)
     *//*
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
     *//*
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
        this.mCameraDevice.createCaptureSession(Arrays.asList(/*previewSurface,*//* this.mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                try {
                    //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange);
                    //cameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
                    mCaptureRequestSession = cameraCaptureSession;
                    mImageAnalyzer = new ImageAnalyzer(mTextureView);
                    mImageAnalyzer.setImageAnalyzerListener(mImageAnalyserListener);
                    mImageAnalyzer.setFrameAnalyzer(mFrameAnalyzer);
                    mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    mCaptureRequestSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
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
     *//*
    public FrameAnalyzer getFrameAnalyzer() {
        return this.mFrameAnalyzer;
    }

    /**
     * Get the poi on the preview by an x and a y
     * @param x the x coord
     * @param y the y coord
     * @return the poi touched
     *//*
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Optional<PointOfInterest> getPoiAt(int x, int y) {
        if (this.poiList == null) return null;
        return this.poiList.stream().filter( poi -> {
            return poi.getX_coord() > x && poi.getX_coord() + poi.getWidth() < x
                    && poi.getY_coord() > y && poi.getY_coord() + poi.getHeight() < y;
        }).findFirst();
    }

    /**
     * Compute the rotation from a camera
     * @param cameraCharacteristics the camera characteristics
     * @param deviceOrientation the current device orientation
     * @return The current orientation
     *//*
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
     *//*
    private static Size chooseOptimalSize(Size[] choises, int width, int height) {
        List<Size> bigEnough = new ArrayList<>();
        Size bestRatio = choises[0];
        for(Size option : choises) {
            if (option.getHeight() == option.getWidth() * height / width && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
            if (bestRatio.getWidth() / bestRatio.getHeight() > option.getWidth() / option.getHeight()) {
                bestRatio = option;
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return bestRatio;
        }
    }

}
*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.core.Core;
import org.opencv.core.Mat;
//import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.xml.sax.XMLFilter;

import com.argos.utils.FrameAnalyzer;
import com.argos.utils.PointOfInterest;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CameraListener implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "ProjectTER::CameraListener";

    public static final int CAMERA_STATE_PREVIEW = 0;
    public static final int CAMERA_STATE_ANALYSE = 1;
    public static final int CAMERA_STATE_MASK = 2;
    public static final int CAMERA_STATE_COLOR = 3;

    private final String FILTER_ARGILE = "Argile";
    private final String FILTER_SAND = "Sable massif";
    private final String FILTER_CONGLOMERA = "Conglomerat";
    private final String FILTER_UNKNOWN = "unknown";

    public static final FrameAnalyzer mFrameAnalyzer = new FrameAnalyzer();
    private final Activity mContext;
    private final JavaCamera2View mJavaCamera2View;

    private List<PointOfInterest> poiList;

    private Map<String, Boolean> mFilters = new HashMap<>();

    private int mCameraState = CAMERA_STATE_PREVIEW;

    private Mat rgba_matrix;

    private float x1 = 0;
    private float y1 = 0;
    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d(TAG, "Touch");
            Log.d(TAG, "mCameraState = " + mCameraState);
            Log.d(TAG, "motion Action = " + motionEvent.getAction());
            if (mCameraState == CAMERA_STATE_ANALYSE) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        y1 = motionEvent.getY();
                    case MotionEvent.ACTION_UP:
                        float seuil = 50;
                        float x2 = motionEvent.getX();
                        float y2 = motionEvent.getY();
                        float dx = x1 - x2;
                        float dy = y1 - y2;
                        Log.d(TAG, "dx = " + dx + ", dy = " + dy);
                        if (dx + dy < seuil) {
                            displayPOIAt(x1, y1);
                            return true;
                        }
                        break;
                }
            }
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public CameraListener(Activity context) {
        mContext = context;
        mJavaCamera2View = mContext.findViewById(R.id.javaCamera2View);
        mJavaCamera2View.setCvCameraViewListener(this);
        mJavaCamera2View.setOnTouchListener(mOnTouchListener);
        mFilters.put(FILTER_ARGILE, true);
        mFilters.put(FILTER_SAND, true);
        mFilters.put(FILTER_CONGLOMERA, true);
        mFilters.put(FILTER_UNKNOWN, false);
    }

    public FrameAnalyzer getFrameAnalyzer() {
        return mFrameAnalyzer;
    }

    public void enable() {
        this.mJavaCamera2View.setCameraPermissionGranted();
        this.mJavaCamera2View.enableView();
    }

    public void disable() {
        this.mJavaCamera2View.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        this.rgba_matrix = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        this.rgba_matrix.release();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.rgba_matrix = inputFrame.rgba();
        this.rgba_matrix = orientationRotation(this.rgba_matrix);
        this.rgba_matrix = transformMat(this.rgba_matrix);
        return this.rgba_matrix;
    }

    public void setCameraState(int cameraState) {
        mCameraState = cameraState;
    }

    private Mat orientationRotation(final Mat image) {
        Mat img = image.clone();

        Size original_size;
        original_size = img.size();
        /* TODO GET ORIENTATION AND APPLY ROTATION */
        Core.rotate(img, img, Core.ROTATE_90_CLOCKWISE);
        // Getting the new ratio
        double img_ratio = (double) (original_size.width / img.size().width);
        // Resizing the mat with the new ratio
        Size new_size = new Size(img.size().width * img_ratio,
                Math.floor((int) img.size().height * img_ratio));
        Imgproc.resize(img, img, new_size, Imgproc.INTER_CUBIC);
        // getting the sub mat to match the mat size needed
        img = img.submat((int) ((new_size.height / 2) - (original_size.height / 2)),
                (int) ((new_size.height / 2) + (original_size.height / 2)),
                (int) ((new_size.width / 2) - (original_size.width / 2)),
                (int) ((new_size.width / 2) + (original_size.width / 2)));
        return img;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Mat drawPOIs(final Mat image, final List<PointOfInterest> POIs) {
        Mat img = image.clone();
        if (POIs.size() > 0) {
            POIs.forEach( poi -> {
                if (mFilters.get(poi.getLabels().get(0))) {
                    Imgproc.rectangle(img, new Point(poi.getX_coord(), poi.getY_coord()),
                            new Point(poi.getX_coord() + poi.getWidth(), poi.getY_coord() + poi.getHeight()),
                            new Scalar(poi.getLineColor().red(), poi.getLineColor().green(), poi.getLineColor().blue()), 5);
                    Size textSize = Imgproc.getTextSize(poi.getLabels().get(0), 0, 1, 3, null);
                    if (poi.getHeight() > textSize.height && poi.getWidth() > textSize.width) {
                        Imgproc.putText(img, poi.getLabels().get(0), new Point(poi.getX_coord() + 10, poi.getY_coord() + 30), 0, 1,
                                new Scalar(poi.getLineColor().red(), poi.getLineColor().green(), poi.getLineColor().blue()), 3);
                    }
                }
            });
        }
        return img;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Mat transformMat(Mat image) {
        float scale = Math.max((float) mJavaCamera2View.getWidth() / image.width(), (float) mJavaCamera2View.getHeight() / rgba_matrix.height());
        Size scaled_mat = new Size(image.width() * scale, image.height() * scale);
        int x_gap = (int) (scaled_mat.width - mJavaCamera2View.getWidth()) / 2;
        int y_gap = (int) (scaled_mat.height - mJavaCamera2View.getHeight()) / 2;
        x_gap /= scale;
        y_gap /= scale;
        Mat subImage = image.submat(y_gap, image.rows() - y_gap, x_gap, image.cols() - x_gap );
        switch (mCameraState) {
            case CAMERA_STATE_ANALYSE :
                poiList = mFrameAnalyzer.getDetailedPOIsFromImage(image);
                subImage = drawPOIs(subImage, poiList);
                break;
            case CAMERA_STATE_MASK :
                subImage = mFrameAnalyzer.HSVTargetZoneFinder.getHsv_inverted_mask(subImage);
                break;
            case CAMERA_STATE_COLOR :
                subImage =  mFrameAnalyzer.targetZoneMaterialsExtractor.getKmeanMask(subImage);
                break;
        }
        subImage.copyTo(image.rowRange(y_gap, y_gap + subImage.rows()).colRange(x_gap, x_gap + subImage.cols()));
        return image;
    }

    public void setFilters(boolean[] filters) {
        mFilters.put(FILTER_ARGILE, filters[0]);
        mFilters.put(FILTER_SAND, filters[1]);
        mFilters.put(FILTER_CONGLOMERA, filters[2]);
        mFilters.put(FILTER_UNKNOWN, filters[3]);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayPOIAt(float x, float y) {
        Mat image = this.rgba_matrix.clone();
        List<PointOfInterest> POIs = poiList;
        float scale = Math.max((float) mJavaCamera2View.getWidth() / image.width(), (float) mJavaCamera2View.getHeight() / rgba_matrix.height());
        Size scaled_mat = new Size(image.width() * scale, image.height() * scale);
        int x_gap = (int) (scaled_mat.width - mJavaCamera2View.getWidth()) / 2;
        int y_gap = (int) (scaled_mat.height - mJavaCamera2View.getHeight()) / 2;
        x_gap /= scale;
        y_gap /= scale;
        x = x + x_gap
        PointOfInterest resultPOI = null;
        int i = 0;
        while (i < POIs.size() && resultPOI == null) {
            if (POIs.get(i).getX_coord() < x + x_gap && POIs.get(i).getX_coord() + POIs.get(i).getWidth() > x + x_gap) {
                if (POIs.get(i).getY_coord() < y + y_gap && POIs.get(i).getY_coord() + POIs.get(i).getHeight() > y + y_gap) {
                    resultPOI = POIs.get(i);
                }
            }
            i++;
        }
        if (resultPOI != null) {
            Log.d(TAG, "POI found");
        } else {
            Log.d(TAG, "no POI found");
        }
    }

}