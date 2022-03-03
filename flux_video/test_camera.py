import cv2

# Load Camera
cap = cv2.VideoCapture(0)


while True:
    
    ret, frame = cap.read()

    
    #creating x1 y1 as top right point and x2 y2 as bottom left point
    y1, x1, y2, x2 = 120, 420, 280, 276

    #making a rectangle 
    cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 0, 200), 2)



    #show frame by frame of the capture
    cv2.imshow("Frame", frame)

    #break of the camera capture
    key = cv2.waitKey(1)
    if key == 27:
        break

cap.release()
cv2.destroyAllWindows()