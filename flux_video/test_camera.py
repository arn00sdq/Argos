import cv2

# Load Camera
cap = cv2.VideoCapture(0)


while True:
    
    ret, frame = cap.read()

    
    #creating x2 y1 as top right point and x1 y2 as bottom left point
    y1, x2, y2, x1 = 120, 420, 280, 276

    #making a rectangle 
    cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 0, 200), 2)

    #putting text on top of the rectangle
    cv2.putText(frame, "c'est un rectangle", (x1 , y1 -10), cv2.FONT_HERSHEY_DUPLEX, 1, (0, 0, 200), 2)

    #show frame by frame of the capture
    cv2.imshow("Frame", frame)

    #break of the camera capture with the key "echap"
    key = cv2.waitKey(1)
    if key == 27:
        break

cap.release()
cv2.destroyAllWindows()