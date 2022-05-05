package com.example.projet_ter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import com.argos.utils.FrameAnalyzer;
import com.argos.utils.PointOfInterest;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CameraListener implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "ProjectTER::CameraListener";

    public static final int CAMERA_STATE_ANALYSE = 0;
    public static final int CAMERA_STATE_BORDER = 1;
    public static final int CAMERA_STATE_PREVIEW = 2;
    public static final int CAMERA_STATE_MASK = 3;
    public static final int CAMERA_STATE_COLOR = 4;
    public static final int CAMERA_STATE_PICTURE = 5;

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
    private Mat rgba_matix_picture;

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
                        Log.d(TAG, "x2 = " + x2 + ", y2 = " + y2);
                        if (dx + dy < seuil) {
                            displayPOIAt((int) x2,(int) y2);
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
        if (mCameraState != CAMERA_STATE_PICTURE) {
            rgba_matix_picture = null;
        }
    }

    private Mat orientationRotation(final Mat image) {
        Mat img = image.clone();
        Size original_size;
        original_size = img.size();
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
                if (mCameraState == CAMERA_STATE_BORDER || mFilters.get(poi.getLabels().get(0))) {
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
                poiList = mFrameAnalyzer.getDetailedPOIsFromImage(subImage);
                subImage = drawPOIs(subImage, poiList);
                break;
            case CAMERA_STATE_MASK :
                subImage = mFrameAnalyzer.HSVTargetZoneFinder.getHsv_inverted_mask(subImage);
                break;
            case CAMERA_STATE_COLOR :
                subImage =  mFrameAnalyzer.targetZoneMaterialsExtractor.getKmeanMask(subImage);
                break;
            case CAMERA_STATE_BORDER :
                poiList =  PointOfInterest.toPOIList(mFrameAnalyzer.getTargetZonesFromImage(subImage));
                subImage = drawPOIs(subImage, poiList);
                break;
            case CAMERA_STATE_PICTURE:
                if (rgba_matix_picture == null) {
                    poiList = mFrameAnalyzer.getDetailedPOIsFromImage(subImage);
                    subImage = drawPOIs(subImage, poiList);
                    rgba_matix_picture = subImage.clone();
                } else {
                    subImage = rgba_matix_picture;
                }
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
    private void displayPOIAt(int x, int y) {
        float scale = Math.max((float) mJavaCamera2View.getWidth() / rgba_matrix.width(), (float) mJavaCamera2View.getHeight() / rgba_matrix.height());
        x = (int) (x / scale);
        y = (int) (y / scale);
        PointOfInterest resultPOI = null;
        Log.d(TAG, "touch x = " + x + " touch y = " + y);
        if (poiList != null) {
            List<PointOfInterest> POIs = new ArrayList<>(poiList);
            int i = 0;
            while (i < POIs.size() && resultPOI == null) {
                Log.d(TAG, "poi x = " + POIs.get(i).getX_coord() + " poi x2 = " + (POIs.get(i).getX_coord() + POIs.get(i).getWidth()));
                Log.d(TAG, "poi y = " + POIs.get(i).getY_coord() + " poi y2 = " + (POIs.get(i).getY_coord() + POIs.get(i).getHeight()));
                if (POIs.get(i).getX_coord() < x && POIs.get(i).getX_coord() + POIs.get(i).getWidth() > x) {
                    if (POIs.get(i).getY_coord() < y && POIs.get(i).getY_coord() + POIs.get(i).getHeight() > y) {
                        resultPOI = POIs.get(i);
                    }
                }
                i++;
            }
        }

        if (resultPOI != null) {
            Log.d(TAG, "POI found");
        } else {
            Log.d(TAG, "no POI found");
        }
    }

    public void storePicture() {
        Mat picture = rgba_matix_picture.clone();
        Bitmap bmp = Bitmap.createBitmap(picture.cols(), picture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(picture, bmp);
        try {
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator);
            f.mkdirs();
            File[] files = f.listFiles();
            ArrayList<String> arrayFiles = new ArrayList<String>();
            String filename = Environment.getExternalStorageDirectory()
                    + File.separator + "carotte_" + files.length + ".png";
            f = new File(filename);
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.d(TAG, "FILENAME = " + filename);
            Toast.makeText(mContext, "Image enregistré.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext, "Erreur enregistrement image.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}