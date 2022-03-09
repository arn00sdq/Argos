from turtle import color, width
from unittest import result
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
cv.createTrackbar("H","TrackBars",0,255,empty)
cv.createTrackbar("S","TrackBars",0,255,empty) 
cv.createTrackbar("V","TrackBars",0,255,empty)

while True:

    #_, frame = cap.read()
    X_value = cv.getTrackbarPos("X","TrackBars")
    Y_value = cv.getTrackbarPos("Y","TrackBars")

    B_value = cv.getTrackbarPos("H","TrackBars")
    G_value = cv.getTrackbarPos("S","TrackBars")
    R_value = cv.getTrackbarPos("V","TrackBars")


    img = cv.imread('Image_de_test/carrote.jpg');
    img_cp = img.copy()
    img_cp = cv.resize(img, (540,300))
    img = cv.resize(img, (540,300))

    h,w,_ = img.shape
    img_hsv = cv.cvtColor(img, cv.COLOR_BGR2HSV)

    #pointer
    cx = int(X_value)
    cy = int(Y_value)

    pixel_center = img_hsv[X_value,Y_value]
    hue_value = pixel_center[0]

    print(pixel_center)

    cv.circle(img, (X_value, Y_value), 5, (255,0,0),3)
    cv.circle(img_hsv, (X_value, Y_value), 5, (255,0,0),3)

    # define rgb 
    lower = np.array([B_value,G_value,R_value])
    upper = np.array([255,255,255])

    mask = cv.inRange(img_hsv,lower,upper)

    #Inverser les couleurs avec un thresold
    ret,th = cv.threshold(mask,0,255,cv.THRESH_BINARY_INV)

    result = cv.bitwise_and(img,img, mask=th)

    #On peut exploiter les couleurs grace au mask


    cv.imshow('hsv',img_hsv)
    cv.imshow('img',img_cp)
    cv.imshow('mask',mask)
    cv.imshow('th',th)
    cv.imshow('result',result) #2,71,75

    cv.waitKey(1)