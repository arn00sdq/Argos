from unittest import result
import cv2 as cv
import numpy as np
from collections import Counter
from sklearn.cluster import KMeans
import matplotlib.pyplot as plt

def rgb_to_hex(rgb_color):
    hex_color = "#"
    for i in rgb_color:
        i = int(i)
        hex_color += ("{:02x}".format(i))
    print(hex_color)
    return hex_color

def prep_image(raw_img):
    modified_img = cv.resize(raw_img, (900, 600), interpolation = cv.INTER_AREA)
    modified_img = modified_img.reshape(modified_img.shape[0]*modified_img.shape[1], 3)
    return modified_img

def color_analysis(img):
    clf = KMeans(n_clusters = 5)
    color_labels = clf.fit_predict(img)
    center_colors = clf.cluster_centers_
    counts = Counter(color_labels)
    ordered_colors = [center_colors[i] for i in counts.keys()]
    hex_colors = [rgb_to_hex(ordered_colors[i]) for i in counts.keys()]
    plt.figure(figsize = (12, 8))
    plt.pie(counts.values(), colors = hex_colors)
    plt.savefig("color_analysis_report.png")
    print(hex_colors)


img = cv.imread('Image_de_test/carrote2.jpg');
im_rgb = cv.cvtColor(img, cv.COLOR_BGR2RGB)

img_cp = img.copy()
img_cp = cv.resize(img, (540,300))
img = cv.resize(img, (540,300))

h,w,_ = img.shape
img_hsv = cv.cvtColor(img, cv.COLOR_BGR2HSV)

# define rgb 
lower = np.array([0,45,0])
upper = np.array([255,255,255])

mask = cv.inRange(img_hsv,lower,upper)

#Inverser les couleurs avec un thresold
ret,th = cv.threshold(mask,0,255,cv.THRESH_BINARY_INV)

result = cv.bitwise_and(img,img, mask=th)
image_rgb =  cv.cvtColor(result, cv.COLOR_BGR2RGB)
cv.cvtColor(result, cv.COLOR_BGR2RGB)



cv.imshow('result',result) #2,71,75
cv.imshow('rgb',image_rgb) #2,71,75

modified_image = prep_image(image_rgb)
color_analysis(modified_image)

plt.show()

#On peut exploiter les couleurs grace au mask

   

cv.waitKey(0)