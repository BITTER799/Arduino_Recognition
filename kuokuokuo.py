#!/usr/bin/env python3
# -*- coding:utf-8 -*-
'''
摄像头采集图片验证
author:Administrator
datetime:2018/3/25/025 9:27
software: PyCharm
'''

from picamera.array import PiRGBArray
from picamera import PiCamera
import time
import cv2
import numpy as np


camera = PiCamera()
camera.resolution = (640, 640)
camera.framerate = 32
rawCapture = PiRGBArray(camera, size=(640, 640))
count=0
control=1
# allow the camera to warmup
time.sleep(0.1)

# capture frames from the camera
for frame in camera.capture_continuous(rawCapture, format="bgr", use_video_port=True):
    # grab the raw NumPy array representing the image, then initialize the timestamp
    # and occupied/unoccupied text
    image1 = frame.array

    # show the frame
    cv2.imshow("Frame", image1)
    key = cv2.waitKey(1) & 0xFF
    # clear the stream in preparation for the next frame
    rawCapture.truncate(0)

    
    if key == ord("s"):
       cv2.imwrite("/home/pi/kuochong/"+str(control)+'/'+str(count)+"temp.jpg", image1)
       count+=1
    if key == ord("k"):
        control+=1
        print("正在扩充第"+str(control)+“组”)
       
    

    # if the `q` key was pressed, break from the loop
    if key == ord("q"):
        break
    










