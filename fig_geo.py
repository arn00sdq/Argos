from pickle import FALSE
import cv2 as cv
import numpy as np

while True:

    img = cv.imread('Image_de_test/fig_geo.jpg')

    # image en nuance de gris
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    
    ret,th = cv.threshold(img_gray,200,255,cv.THRESH_BINARY_INV+ cv.THRESH_OTSU)

    contours, hierarchy = cv.findContours(th,
                                        cv.RETR_EXTERNAL,
                                        cv.CHAIN_APPROX_NONE)

    sorted_contour = sorted(contours, key=cv.contourArea, reverse=True)

    #:3 Les 3er
    # for i, cont in enumerate(sorted_contour[:10], 1):
    for i, cont in enumerate(sorted_contour):
        area = cv.contourArea(cont)
        cv.drawContours(img, contours, -1, (0, 255, 0), 2)
        peri = cv.arcLength(cont, True)
        approx = cv.approxPolyDP(cont,0.02 * peri , True)

        x, y, w, h = cv.boundingRect(contours[i])
        cv.rectangle(img, (x, y), (x + w, y + h), (0, 0, 255), 2)

        #cv.putText(img, str(i), (cont[0, 0, 0], cont[0, 0, 1]),cv.FONT_HERSHEY_SIMPLEX, 1.4, (0.255, 0), 3)
        cv.putText(img, "Points : " + str(len(approx)),(x + w + 5, y + 20),cv.FONT_HERSHEY_COMPLEX, .7, (0.255, 0), 2)
        cv.putText(img, "Area : " + str(int(area)),(x + w + 5, y + 45),cv.FONT_HERSHEY_COMPLEX, .7, (0.255, 0), 2)


        

    cv.imshow('seuil', th)
    cv.imshow('contour', img)
    cv.imshow('originam', img_gray)

    cv.waitKey(1)
