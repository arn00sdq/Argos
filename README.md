# Argos

<img src="./logo.png" align="right" alt="Logo" margin="auto" width="300px">

Argos is a real-time application bundled into an APK that automatically finds geological core samples in the camera stream in order to extract useful visible data of the cores.

## How It Works

1. Run the app
2. You have to configure the app
    - Click on the seconde tab button on the bottom of the screen and change the change the value so that the core you are shooting is white and the rest is black. Used the "Invert" switch if necessary.
    - Use the "Masque" camera filter by clicking on it to help you for the configuration
    - (Optional) Click on the first tab button to configure optionnal value
    - (Optional) Use the "Couleur" camera filter by clickin on it to help you for the configuration
3. Switch on the "Contour" or "Analyse" camera filter as you need
4. The data should be visible

## How to use the analysis API

The following operations need an image converted to a Mat.
```java
Mat img;

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
## APK installation

1. Get an apk
    - Take the "app-debug.apk" file in this folder
    - Build your own apk
        - Open the "App" folder with AndroidStudio
        - Click "Build" / "Build Bundle(s) / Apk(s)" / "Build Apk(s)"
3. Install the apk
4. Run the apk
5. Accept required permision
6. Enjoy
