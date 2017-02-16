#!/usr/bin/python
# Alejandro Mera 2016
# This is a simple server that receives the gestures captured in your Android device
# Currently it supports mouse movements, click, double click and keyboard
# The keyboard does not support backspace, because this key cannot be captured in the Android APP as part of the text of a view.
# Solving this issue requires to extend the view class, and it would be included in future versions.
# To run this server just execute the script, and modify the IP in the Android APP. 

import os, os.path
from pymouse import PyMouse
from pykeyboard import PyKeyboard
import socket

UDP_IP = ''
UDP_PORT = 5005

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.bind((UDP_IP, UDP_PORT))
m=PyMouse()
k=PyKeyboard()

while True:
        data, addr = sock.recvfrom(128) # buffer size is 100 bytes
        splitdata=data.split(",")
        cm=splitdata[0]

        if cm=="1": #move
                print "moving mouse:"
                x=splitdata[1]
                y=splitdata[2]
                pos=m.position()
                m.move(pos[0]+int(x),pos[1]+int(y))
        if cm=="2": #tap
                pos=m.position()
                m.click(pos[0],pos[1],1,1) # x,y,button number, times to be clicked
        if cm=="3": #double tap
                pos=m.position()
                m.click(pos[0],pos[1],1,2)
        if cm=="4": #double tap
                key=splitdata[1]
                if key=="backspace_key":
                        k.tap_key(k.backspace_key)
                else:
                        k.tap_key(key)

        print "received message:", data
