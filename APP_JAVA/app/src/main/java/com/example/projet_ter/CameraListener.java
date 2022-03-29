package com.example.projet_ter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.argos.utils.FrameAnalyzer;
import com.argos.utils.PointOfInterest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CameraListener implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "ProjectTER::Camera";

    private final FrameAnalyzer frameAnalyzer = new FrameAnalyzer();

    private final JavaCameraView camera_view;

    private Mat rgba_matrix;
    private Mat gray_matrix;

    private boolean analyseStarted = false;

    private List<PointOfInterest> poiList = new ArrayList<>();

    private int startX;
    private int startY;

    @SuppressLint("ClickableViewAccessibility")
    public CameraListener(JavaCameraView camera_view) {
        this.camera_view = camera_view;
        this.camera_view.setCvCameraViewListener(this);
        this.camera_view.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(analyseStarted && poiList.size() > 0) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY() - camera_view.getLayoutParams().height / 2 - rgba_matrix.rows() / 2;
                    System.out.println(x + " x, " + y + " y");
                    System.out.println(poiList);
                    System.out.println("IMG size x = " + rgba_matrix.rows() + ", IMG size y = " + rgba_matrix.cols());
                    System.out.println("Surface size x = " + camera_view.getLayoutParams().width + " Surface view size y = " + camera_view.getLayoutParams().height);
                    /*Optional<PointOfInterest> poi = getPoiAt(x, y);
                    if(poi.isPresent()) {
                        new carotDataDialog(camera_view.getContext(), "TEST").show();
                    }*/
                } else {
                    analyseStarted = true;
                }
                return true;
            }
        });
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        this.rgba_matrix = new Mat();
        this.gray_matrix = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        this.rgba_matrix.release();
        this.gray_matrix.release();
    }

    /**
     * This function is called each time a frame is send by the camera
     *
     * @param inputFrame The frame
     * @return the matrix to display
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
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
    }

    public void enable() {
        this.camera_view.setCameraPermissionGranted();
        this.camera_view.enableView();
    }

    public void disable() {
        this.camera_view.disableView();
    }

    public FrameAnalyzer getFrameAnalyzer() {
        return this.frameAnalyzer;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Optional<PointOfInterest> getPoiAt(int x, int y) {
        if (this.poiList == null) return null;
        return this.poiList.stream().filter( poi -> {
            boolean b = poi.getX_coord() > x && poi.getX_coord() + poi.getWidth() < x
                    && poi.getY_coord() > y && poi.getY_coord() + poi.getHeight() < y;
            return b;
        }).findFirst();
    }

}
