import cv2 as cv
import numpy as np
import ZoneCarotte

def empty(a):
    pass

 #enleve les index uniques : reduit les contours inutiles
def checkio(data):
    for index in range(len(data) - 1, -1, -1):
        if data.count(data[index]) == 1:
            del data[index]
    return data

cv.namedWindow("TrackBars")
cv.resizeWindow("TrackBars",740,240)
cv.createTrackbar("Treshold Min","TrackBars",5,255,empty) #58
cv.createTrackbar("Dilatation","TrackBars",1,10,empty) # 1

while True:

<<<<<<< HEAD
    img = cv.imread('Image_de_test/carrote2.jpg')
    img2 = cv.imread('Image_de_test/carrote2.jpg')
=======
    img = cv.imread('Image_de_test/chat.jpg')
    img2 = cv.imread('Image_de_test/chat.jpg')
>>>>>>> 44d1fa8d91167f4256de3b042d87e21484768001

    #resize
    img = cv.resize(img, None,fx=0.5, fy=0.5, interpolation = cv.INTER_CUBIC)
    img2 = cv.resize(img, None,fx=1, fy=1, interpolation = cv.INTER_CUBIC)

    t_value = cv.getTrackbarPos("Treshold Min","TrackBars")
    d_value = cv.getTrackbarPos("Dilatation","TrackBars")

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
                                        cv.CHAIN_APPROX_SIMPLE)

    # sort the contour
    sorted_contour = sorted(contours,key=cv.contourArea,reverse=True)

    #:3 Les 3er
    for i,cont in enumerate(sorted_contour,1):
        cv.drawContours(img2,cont,-1,(0,255,0),2)     
                     

    #draw time
    print('contours = ' + str(len(contours)))
    print('hierarchy = ' + str(len(hierarchy[0])))

    tempTab = []
    index = 0

    # Pour explication ce lien :  https://docs.opencv.org/3.4/d9/d8b/tutorial_py_contours_hierarchy.html

    for i in range(len(hierarchy[0])):
        if (hierarchy[0][i][3] != 0 and hierarchy[0][i][3] != -1  and hierarchy[0][i][2] == -1  ): 
            tempTab.append(hierarchy[0][i][3])
            index = index + 1

    FinalTab = checkio(tempTab)

    #enleve les doublons du tableau
    nonDuplicate =  list(dict.fromkeys(FinalTab))

    print('nonDuplicate = ' + str(nonDuplicate))

    for i in range(len(nonDuplicate)):
        if(cv.contourArea(contours[nonDuplicate[i]]) >= 1000):
            print("Coordonnées rectangle" + str(cv.contourArea(contours[nonDuplicate[i]])))
            x, y, w, h = cv.boundingRect(contours[nonDuplicate[i]])
            cv.rectangle(img, (x, y), (x + w, y + h), (0, 0, 255), 2)
                

    #Nombre total de pixels
    whole_area = th_dilation.size

    #Nombre de pixels dans la partie blanche
    white_area = cv.countNonZero(th_dilation)

    #Nombre de pixels dans la partie noire
    black_area = whole_area - white_area

    #Afficher chaque pourcentage
    #print('White_Area =' + str(white_area / whole_area * 100) + ' %')
    #print('Black_Area =' + str(black_area / whole_area * 100) + ' %')

    cv.imshow('gray',img_gray)
    cv.imshow('carrote',img)
    cv.imshow('contour',img2)
    cv.imshow('th_dilation', th_dilation)

    cv.waitKey(1)