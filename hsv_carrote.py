from turtle import color, width
import cv2 as cv
import numpy as np

#cap= cv.VideoCapture(0)
#cap.set(cv.CAP_PROP_FRAME_WIDTH,1280)
#cap.set(cv.CAP_PROP_FRAME_HEIGHT,720)

def empty(a):
    pass


cv.namedWindow("TrackBars")
cv.resizeWindow("TrackBars",740,240)
cv.createTrackbar("X","TrackBars",10,2000,empty)
cv.createTrackbar("Y","TrackBars",10,2000,empty) #58

while True:

    #_, frame = cap.read()


    X_value = cv.getTrackbarPos("X","TrackBars")
    Y_value = cv.getTrackbarPos("Y","TrackBars")

    img = cv.imread('Image_de_test/carrote.jpg')

    h,w,_ = img.shape
    img_hsv = cv.cvtColor(img, cv.COLOR_BGR2HSV)

    cx = int(X_value/2)
    cy = int(Y_value/2)

    pixel_center = img_hsv[X_value,Y_value]
    hue_value = pixel_center[0]

    print(pixel_center)

    if hue_value < 5:
        color = "Red"

    cv.circle(img, (X_value, Y_value), 5, (255,0,0),3)
    cv.circle(img_hsv, (X_value, Y_value), 5, (255,0,0),3)

    cv.imshow('hsv',img_hsv)
    cv.imshow('img',img)

    cv.waitKey(1)