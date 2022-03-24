from fileinput import FileInput
from mimetypes import types_map
from ZoneCarotte import ZoneCarotte
import cv2 as cv
import numpy as np

target = 3
maxDetected = 0
area_threshhold = 500
t_value = 5
t_value_of_max = 5
d_value = 1
d_value_of_max = 1
finished = False

def checkio(data):
    for index in range(len(data) - 1, -1, -1):
        if data.count(data[index]) == 1:
            del data[index]
    return data

while True:

    img = cv.imread('./Image_de_test/carrote.jpg')
    img2 = cv.imread('./Image_de_test/carrote.jpg')

    #resize
    img = cv.resize(img, None,fx=0.5, fy=0.5, interpolation = cv.INTER_CUBIC)
    img2 = cv.resize(img, None,fx=1, fy=1, interpolation = cv.INTER_CUBIC)

    #image en nuance de gris
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    #superposition rectangle
    ret,th = cv.threshold(img_gray,t_value,200,cv.THRESH_BINARY_INV)

    #définit la grille
    kernel = np.ones((d_value,d_value),np.float32)/25

    #suppression du bruit, fais grossir les zones lumineuses ou assombris à l'inverse
    th_dilation = cv.dilate(th,kernel,iterations = 1)

    contours, hierarchy = cv.findContours(th_dilation,
                                        cv.RETR_CCOMP,
                                        cv.CHAIN_APPROX_NONE)

    # sort the contour
    sorted_contour = sorted(contours,key=cv.contourArea,reverse=True)

    #:3 Les 3er
    for i,cont in enumerate(sorted_contour,1):
        cv.drawContours(img2,cont,-1,(0,255,0),2)     
                     

    #draw time
    """print('contours = ' + str(len(contours)))
    print('hierarchy = ' + str(len(hierarchy[0])))"""

    tempTab = []
    index = 0

    # Pour explication ce lien :  https://docs.opencv.org/3.4/d9/d8b/tutorial_py_contours_hierarchy.html

    for i in range(len(hierarchy[0])):
        if (hierarchy[0][i][3] != 0 and hierarchy[0][i][3] != -1  and hierarchy[0][i][2] == -1  ): 
            tempTab.append(hierarchy[0][i][3])
            index = index + 1

    FinalTab = checkio(tempTab)

    rectangles = []

    #enleve les doublons du tableau
    nonDuplicate =  list(dict.fromkeys(FinalTab))

    """print('nonDuplicate = ' + str(nonDuplicate))"""

    for i in range(len(nonDuplicate)):
        if(cv.contourArea(contours[nonDuplicate[i]]) >= area_threshhold):
            """print("Coordonnées rectangle " + str(cv.contourArea(contours[nonDuplicate[i]])))"""
            x, y, w, h = cv.boundingRect(contours[nonDuplicate[i]])
            cv.rectangle(img, (x, y), (x + w, y + h), (0, 0, 255), 2)
            
            zone = ZoneCarotte(x, y, x+w, y+h, w, h)
            """ print ('Aire de la zone' + str(zone.getArea()))
            print('N zones: ' + str(len(rectangles) + 1)) """
            
            if not zone.existsInArray(rectangles):
                rectangles.append(zone)
    
    print('t: ' + str(t_value))
    print('d: ' + str(d_value))
    print('max det: ' + str(maxDetected) + 't_max' + str(t_value_of_max) + 'd_max' + str(d_value_of_max))
    
    if not finished and d_value < 3:
        if t_value < 100:
            t_value += 2
        else:
            t_value = 0
            d_value += 1
        if len(rectangles) + 1 > maxDetected:
            maxDetected = len(rectangles)
            t_value_of_max = t_value
            d_value_of_max = d_value
    
    else:
        finished = True
    
    if finished:
        t_value = t_value_of_max
        d_value = d_value_of_max
    
    """ if len(rectangles) < target:
        t_value += 1
    else:
        t_value -=1 """

    #Nombre total de pixels
    whole_area = th_dilation.size

    #Nombre de pixels dans la partie blanche
    white_area = cv.countNonZero(th_dilation)

    #Nombre de pixels dans la partie noire
    black_area = whole_area - white_area

    #Afficher chaque pourcentage
    #print('White_Area =' + str(white_area / whole_area * 100) + ' %')
    #print('Black_Area =' + str(black_area / whole_area * 100) + ' %')

    #cv.imshow('gray',img_gray)
    cv.imshow('carrote',img)
    cv.imshow('contour',img2)
    #cv.imshow('th_dilation', th_dilation)

    cv.waitKey(1)