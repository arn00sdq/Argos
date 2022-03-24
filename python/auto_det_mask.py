from unittest import result
import cv2 as cv
import numpy as np
from ZoneCarotte import ZoneCarotte

#cap= cv.VideoCapture(0)
#cap.set(cv.CAP_PROP_FRAME_WIDTH,1280)
#cap.set(cv.CAP_PROP_FRAME_HEIGHT,720)

target = 18
minDiff = target
optimalSaturation = 1
analysis_completed = False

s_val = 1


def empty(a):
    pass


cv.namedWindow("TrackBars")
cv.resizeWindow("TrackBars",740,240)
cv.createTrackbar("X","TrackBars",10,2000,empty)
cv.createTrackbar("Y","TrackBars",10,2000,empty) #58
cv.createTrackbar("H","TrackBars",0,255,empty)
cv.createTrackbar("S","TrackBars",0,255,empty) 
cv.createTrackbar("V","TrackBars",0,255,empty)
cv.createTrackbar("T","TrackBars",0,255,empty)

img = cv.imread('Image_de_test/carrote2.jpg')

img = cv.resize(img, (540,300))

h,w,_ = img.shape
img_hsv = cv.cvtColor(img, cv.COLOR_BGR2HSV)
    
while True:

    #_, frame = cap.read()
    X_value = cv.getTrackbarPos("X","TrackBars")
    Y_value = cv.getTrackbarPos("Y","TrackBars")

    H_value = cv.getTrackbarPos("H","TrackBars")
    S_value = cv.getTrackbarPos("S","TrackBars")
    V_value = cv.getTrackbarPos("V","TrackBars")
    
    threshold = cv.getTrackbarPos("T","TrackBars")


    

    img_cp = img.copy()

    img_cp = cv.resize(img, (540,300))

    #pointer
    """ cx = int(X_value/2)
    cy = int(Y_value/2)

    pixel_center = img_hsv[X_value,Y_value]
    hue_value = pixel_center[0]

    print(pixel_center)

    cv.circle(img, (X_value, Y_value), 5, (255,0,0),3)
    cv.circle(img_hsv, (X_value, Y_value), 5, (255,0,0),3) """

    # define rhsv
    lower = np.array([H_value,s_val,V_value])
    upper = np.array([255,255,255])

    masked = cv.inRange(img_hsv,lower,upper)
    
    #Inverser les couleurs avec un thresold
    ret,th = cv.threshold(masked,0,255,cv.THRESH_BINARY_INV)

    result = cv.bitwise_and(img,img, mask=th)
        
    contours, hierarchy = cv.findContours(masked,
                                        cv.RETR_CCOMP,
                                        cv.CHAIN_APPROX_NONE)
    
    # sort the contour
    sorted_contour = sorted(contours,key=cv.contourArea,reverse=True)
    
    for i,cont in enumerate(sorted_contour,1):
        cv.drawContours(result,cont,-1,(0,255,0),2)   

    detected = []

    for cont in sorted_contour:
        x,y,w,h = cv.boundingRect(cont)
        newZone = ZoneCarotte(x, y, x+w, y+h, w, h)
        if newZone.getArea() > 1500 and not newZone.existsInArray(detected):
            detected.append(newZone)
            
    
        
    for z in detected:
        cv.rectangle(result,(z.upper_x,z.upper_y),(z.lower_x,z.lower_y),(0,255,0),1)
        cv.rectangle(img_cp,(z.upper_x,z.upper_y),(z.lower_x,z.lower_y),(0,255,0),1)
    #On peut exploiter les couleurs grace au mask

    print('detected: ' + str(len(detected)))
    
    if not analysis_completed:
        if s_val < 150:
            s_val += 2
        else:
            analysis_completed = True
        if abs(target - len(detected)+1) < minDiff:
            minDiff = abs(target - len(detected)+1)
            optimalSaturation = s_val
    else:
        s_val = optimalSaturation
        
        
    cv.imshow('hsv',img_hsv)
    cv.imshow('img',img_cp)
    cv.imshow('mask',masked)
    cv.imshow('th',th)
    cv.imshow('result',result) #2,71,75

    cv.waitKey(1)