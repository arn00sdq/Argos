import cv2 as cv
import numpy as np

img = cv.imread('carrote.jpg')

#image en nuance de gris
img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

#applique une valeur de seuil pour 1 pixel si inferieur blanc sinon noir
#Trouver une technnique pour definir le seuil
#Moyenne des couleurs situés au centre de l'image ?
ret,th = cv.threshold(img_gray,40,130,cv.THRESH_BINARY)

kernel = np.ones((1,1),np.uint8)

#suppression du bruit, fais grossir les zones lumineuses ou assombris à l'inverse
th_dilation = cv.dilate(th,kernel,iterations = 1)

contours, hierarchy = cv.findContours(th_dilation,
                                       cv.RETR_TREE,
                                       cv.CHAIN_APPROX_SIMPLE)

#draw time
print('contours =' + str(len(contours)))
print('hierarchy =' + str(len(hierarchy[0])))

tempTab = []
index = 0

# Pour explication ce lien :  https://docs.opencv.org/3.4/d9/d8b/tutorial_py_contours_hierarchy.html

for i in range(len(hierarchy[0])):
    if (hierarchy[0][i][3] != 0 and hierarchy[0][i][3] != -1 ): #Pour affiner la recherche enlever le dernier enfant
        tempTab.append(hierarchy[0][i][3])
        index = index + 1


#enleve les index uniques : reduit les contours inutiles
def checkio(data):
    for index in range(len(data) - 1, -1, -1):
        if data.count(data[index]) == 1:
            del data[index]
    return data

FinalTab = checkio(tempTab)


#enleve les doublons du tableau
nonDuplicate =  list(dict.fromkeys(FinalTab))

print('nonDuplicate =' + str(nonDuplicate))

#print(tempTab)
#r1, r2 = sorted(contours, key=cv.contourArea)[-3:-1]
#print(np.r_[r1, r2])

for i in range(len(nonDuplicate)):
    x, y, w, h = cv.boundingRect(contours[nonDuplicate[i]])
    cv.rectangle(img, (x, y), (x + w, y + h), (0, 0, 255), 2)

#Nombre total de pixels
whole_area = th_dilation.size

#Nombre de pixels dans la partie blanche
white_area = cv.countNonZero(th_dilation)

#Nombre de pixels dans la partie noire
black_area = whole_area - white_area

#Afficher chaque pourcentage
print('White_Area =' + str(white_area / whole_area * 100) + ' %')
print('Black_Area =' + str(black_area / whole_area * 100) + ' %')


cv.imshow('carrote',img)
cv.imshow('th_dilation', th_dilation)

cv.waitKey(0)