# Argos

<img src="./logo.png" align="right" alt="Logo" margin="auto" width="300px">

Argos is a real-time application bundled into an APK that automatically finds geological core samples in the camera stream in order to extract useful visible data of the cores.

## APK installation

1. Get the apk following one of these steps:
    - Get the "app-debug.apk" file in this folder
    - Build your own apk
        - Open the "App" folder with AndroidStudio
        - Click "Build" / "Build Bundle(s) / Apk(s)" / "Build Apk(s)"
3. Install the apk
4. Open the apk
5. Accept required permisions
6. Enjoy


## How to use the app

1. Open the app
2. Configure the settings to suit your needs:
    - Click on the second tab button on the bottom of the screen in order to change the values of the detection settings until the cores you are pointing at are white and the background is black. Use the "Invert" switch button if necessary.
    - Use the "Masque" camera filter by clicking on it to help you for configure the detection settings.
    - (Optional) Click on the first tab button to configure the optional values.
    - (Optional) Use the "Couleur" camera filter by clicking on it to help you for the configuration.
3. Switch on the "Contour" or "Analyse" camera filter option as you need.
4. The detected elements will appear on screen.

## How to use the analysis API

The following operations need an image converted to a Mat.
```java
Mat img = Imgcodecs.imread("./example.png");

FrameAnalyzer analyzer = new FrameAnalyzer();

analyzer.HSVTargetZoneFinder.setSaturation_value(45);
analyzer.HSVTargetZoneFinder.setHue_value(45);
analyzer.HSVTargetZoneFinder.setValue_value(45);
analyzer.HSVTargetZoneFinder.setMin_area_contour(1000);
/*AND / OR*/
analyzer.HSVTargetZoneFinder.setAutomaticallyCalibratedS(img);

analyzer.targetZoneMaterialsExtractor.setConfidence(90);
analyzer.targetZoneMaterialsExtractor.setLengthOfCut(5);
analyzer.targetZoneMaterialsExtractor.setNumberOfClusters(4);

List<PointOfInterest> poiList = analyzer.getDetailedPOIsFromImage(img);
```
