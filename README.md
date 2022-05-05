# Argos

<img src="./logo.png" align="right" alt="Logo" margin="auto" width="30vwmin">

Argos is a real-time application bundled into an APK that automatically finds geological core samples in the camera stream in order to extract useful visible data of the cores.

## How It Works

1. dwaf
2. asdf
3. adf
4. adf
5. adf


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

1. Install the apk
2. Run the apk
3. Enjoy
