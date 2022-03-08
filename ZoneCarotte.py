from sys import modules


class ZoneCarotte:
    upper_x: int
    upper_y: int
    lower_x: int
    lower_y: int
    w: int
    h: int
    
    def __init__(self, upper_x, upper_y, lower_x, lower_y, w, h):
        self.upper_x = upper_x
        self.upper_y = upper_y
        self.lower_x = lower_x
        self.lower_y = lower_y
        self.w = w
        self.h = h
        
    def getArea(self):
        return self.w * self.h

    def equals(self, other):
        if self.upper_x == other.upper_x and self.lower_x == other.lower_x and self.upper_y == other.upper_y and self.lower_y == other.lower_y:
            return True
        else:
            return False
        
    def existsInArray(self, array):
        exists = False
        for i in range(len(array)):
            if self.equals(array[i]):
                exists = True
        return exists