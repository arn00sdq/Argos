import cv2 as cv
import numpy as np
import sys

def empty(a):
    pass

#trackbar

cv.namedWindow("TrackBars")
cv.resizeWindow("TrackBars",840,240)
cv.createTrackbar("Treshold1 Min","TrackBars",0,255,empty) 
cv.createTrackbar("Treshold2 Min","TrackBars",0,255,empty) 
while True:

    # Load the image
    image = cv.imread('../Image_de_test/carrote.jpg')
    image = cv.cvtColor(image, cv.COLOR_BGR2RGB)

    # Grayscale 
    gray = cv.cvtColor(image, cv.COLOR_BGR2GRAY)

    #track value
    t_value1 = cv.getTrackbarPos("Treshold1 Min","TrackBars")
    t_value2 = cv.getTrackbarPos("Treshold2 Min","TrackBars")

    # Canny Edges
    edges = cv.Canny(gray, t_value1, t_value2)
    # Run HoughLines Fucntion 
    lines = cv.HoughLines(edges, 1, np.pi/180, 150)
    # Run for loop through each line
    for line in lines:
        rho, theta = line[0]
        a = np.cos(theta)
        b = np.sin(theta)
        x0 = a * rho
        y0 = b * rho
        x_1 = int(x0 + 1000 * (-b))
        y_1 = int(y0 + 1000 * (a))
        x_2 = int(x0 - 1000 * (-b))
        y_2 = int(y0 - 1000 * (a))
        cv.line(image, (x_1, y_1), (x_2, y_2), (255, 0, 0), 2)
    # Show Final output
    cv.imshow('carrote',image)
    cv.imshow('canny',edges)
    cv.waitKey(1)