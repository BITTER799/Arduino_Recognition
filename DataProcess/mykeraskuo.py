
from PIL import Image
import os
import cv2
import numpy as np
from keras.preprocessing.image import ImageDataGenerator,array_to_img, img_to_array, load_img


# 生成文件夹
'''def ensure_dir(dir_path):
    if not os.path.exists(dir_path):
        try:
            os.makedirs(dir_path)
        except OSError:
            pass'''


# 图片生成器ImageDataGenerator
pic_gen = ImageDataGenerator(
    rotation_range=5,
    width_shift_range=0.1,
    height_shift_range=0.1,
    shear_range=0.2,
    zoom_range=0.2,
    fill_mode='nearest')

# 生成图片
def img_create(img_dir, save_dir, img_prefix, num=100):
    img = load_img(img_dir)
    x = img_to_array(img)
    x = x.reshape((1,) + x.shape)
    img_flow = pic_gen.flow(
        x,
        batch_size=1,
        save_to_dir=save_dir,
        save_prefix=img_prefix,
        save_format="jpg"
    )
    i = 0
    for batch in img_flow:
        i += 1
        print('完成扩展'+str(i))
        if i > num:
            break

'''def adjustandmove(mypath,p,count1):
    for pathtemp in mypath:
        if pathtemp != '.DS_Store':
            path = os.path.join(pathroot, pathtemp)
            result = os.listdir(path)
            im = [os.path.join(path, f) for f in result if f.endswith('.jpg')]
            for i, f in enumerate(im[:]):
                image = cv2.imread(f)
                height, width = image.shape[:2]
                if height > width:
                    widthlow = width / 2 - width / 2 * p
                    widthhigh = width / 2 + width / 2 * p
                    widthlow = int(widthlow)
                    widthhigh = int(widthhigh)
                    widthrange = widthhigh - widthlow
                    heightlow = height * 0.2
                    heightlow = int(heightlow)
                    heighthigh = heightlow + widthrange
                    for roll in range(10):
                        twidthlow = widthlow + roll * int(widthrange * 0.05)
                        twidthheigh = widthhigh + roll * int(widthrange * 0.1)
                        if twidthheigh <= width:
                            for round in range(10):
                                theightlow = heightlow + round * int(widthrange * 0.1)
                                theighthigh = heighthigh + round * int(widthrange * 0.1)
                                if theighthigh <= height:
                                    temp1 = image[theightlow:theighthigh,
                                            twidthlow:twidthheigh]
                                    cv2.imwrite(path + '/' +'cutcut' + str(count1) + '.jpg', temp1)
                                    count1 += 1
                                    print('正在移动中'+str(count1))
                                else:
                                    break
                        else:
                            break



                else:
                    heightlow = height / 2 - height / 2 * p
                    heighthigh = height / 2 + height / 2 * p
                    heightlow = int(heightlow)
                    heighthigh = int(heighthigh)
                    heightrange = heighthigh - heightlow
                    widthlow = width * 0.2
                    widthlow = int(widthlow)
                    widthhigh = widthlow + heightrange
                    for roll in range(10):
                        theightlow = heightlow + roll * int(heightrange * 0.05)
                        theighthigh = heighthigh + roll * int(heightrange * 0.05)
                        if theighthigh<=height:
                            for round in range(10):
                                twidthlow = widthlow + round * int(heightrange * 0.1)
                                twidthheigh = widthhigh + round * int(heightrange * 0.1)
                                if twidthheigh <= width:
                                    temp1 = image[theightlow:theighthigh,
                                            twidthlow:twidthheigh]
                                    cv2.imwrite(path + '/' +'cutcut' + str(count1) + '.jpg', temp1)
                                    count1 += 1
                                    print('正在移动中' + str(count1))
                                else:
                                    break'''



'''def locateobject(mypath,count1):
    for pathtemp in mypath:
        if pathtemp != '.DS_Store':
            path = os.path.join(pathroot, pathtemp)
            result = os.listdir(path)
            im = [os.path.join(path, f) for f in result if f.endswith('.jpg')]
            for i, f in enumerate(im[:]):
                i = i + 1
                image = cv2.imread(f)
                gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

                gradX = cv2.Sobel(gray, ddepth=cv2.CV_32F, dx=1, dy=0, ksize=-1)
                gradY = cv2.Sobel(gray, ddepth=cv2.CV_32F, dx=0, dy=1, ksize=-1)

                # subtract the y-gradient from the x-gradient
                gradient = cv2.subtract(gradX, gradY)
                gradient = cv2.convertScaleAbs(gradient)

                # blur and threshold the image
                blurred = cv2.blur(gradient, (9, 9))
                (_, thresh) = cv2.threshold(blurred, 90, 255, cv2.THRESH_BINARY)

                # cv2.imshow("Image", thresh)
                # cv2.waitKey(0)

                kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (25, 25))
                closed = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)

                # perform a series of erosions and dilations
                closed = cv2.erode(closed, None, iterations=4)
                closed = cv2.dilate(closed, None, iterations=4)

                # cv2.imshow("Image", closed)
                # cv2.waitKey(0)

                (_, cnts, _) = cv2.findContours(closed.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
                c = sorted(cnts, key=cv2.contourArea, reverse=True)[0]

                # compute the rotated bounding box of the largest contour
                rect = cv2.minAreaRect(c)
                box = np.int0(cv2.boxPoints(rect))

                # cv2.drawContours(image, [box], -1, (0, 255, 0), 3)
                # cv2.imshow("Image", image)
                # cv2.waitKey(0)

                Xs = [i[0] for i in box]
                Ys = [i[1] for i in box]
                x1 = min(Xs)
                x2 = max(Xs)
                y1 = min(Ys)
                y2 = max(Ys)
                hight = y2 - y1
                width = x2 - x1
                cropImg = image[y1:y1 + hight, x1:x1 + width]
                cv2.imwrite(path + '/' + 'cut'+str(count1) + '.jpg', cropImg)
                count1 += 1
                print('剪裁完成' + str(count1))
                # cv2.imshow("Image", cropImg)
                # cv2.waitKey(0)'''

#定义饱和度函数
def saturation_demo(img1):
    img = img1 * 1.0
    img_out = img1 * 1.0

    # -1 ~ 1
    Increment = 0.5

    img_min = img.min(axis=2)
    img_max = img.max(axis=2)

    Delta = (img_max - img_min) / 255.0
    value = (img_max + img_min) / 255.0
    L = value / 2.0

    mask_1 = L < 0.5

    s1 = Delta / (value + 0.001)
    s2 = Delta / (2 - value + 0.001)
    s = s1 * mask_1 + s2 * (1 - mask_1)

    if Increment >= 0:
        temp = Increment + s
        mask_2 = temp > 1
        alpha_1 = s
        alpha_2 = s * 0 + 1 - Increment
        alpha = alpha_1 * mask_2 + alpha_2 * (1 - mask_2)
        alpha = 1 / (alpha + 0.001) - 1
        img_out[:, :, 0] = img[:, :, 0] + (img[:, :, 0] - L * 255.0) * alpha
        img_out[:, :, 1] = img[:, :, 1] + (img[:, :, 1] - L * 255.0) * alpha
        img_out[:, :, 2] = img[:, :, 2] + (img[:, :, 2] - L * 255.0) * alpha

    else:
        alpha = Increment
        img_out[:, :, 0] = L * 255.0 + (img[:, :, 0] - L * 255.0) * (1 + alpha)
        img_out[:, :, 1] = L * 255.0 + (img[:, :, 1] - L * 255.0) * (1 + alpha)
        img_out[:, :, 2] = L * 255.0 + (img[:, :, 2] - L * 255.0) * (1 + alpha)

    img_out = img_out / 255.0

    # 饱和处理
    mask_1 = img_out < 0
    mask_2 = img_out > 1

    img_out = img_out * (1 - mask_1)
    img_out = img_out * (1 - mask_2) + mask_2
    return img_out


#定义亮度与对比度函数
def contrast_demo(img1, c, b):  # 亮度就是每个像素所有通道都加上b
    rows, cols, chunnel = img1.shape
    blank = np.zeros([rows, cols, chunnel], img1.dtype)  # np.zeros(img1.shape, dtype=uint8)
    dst = cv2.addWeighted(img1, c, blank, 1-c, b)
    return dst


#定义椒盐噪声
def saltpepper(img,n):
    m=int((img.shape[0]*img.shape[1])*n)
    for a in range(m):
        i=int(np.random.random()*img.shape[1])
        j=int(np.random.random()*img.shape[0])
        if img.ndim==2:
            img[j,i]=255
        elif img.ndim==3:
            img[j,i,0]=255
            img[j,i,1]=255
            img[j,i,2]=255
    for b in range(m):
        i=int(np.random.random()*img.shape[1])
        j=int(np.random.random()*img.shape[0])
        if img.ndim==2:
            img[j,i]=0
        elif img.ndim==3:
            img[j,i,0]=0
            img[j,i,1]=0
            img[j,i,2]=0
    return img


#定义盐噪声
def salt(img,n):
    m=int((img.shape[0]*img.shape[1])*n)
    for a in range(m):
        i=int(np.random.random()*img.shape[1])
        j=int(np.random.random()*img.shape[0])
        if img.ndim==2:
            img[j,i]=255
        elif img.ndim==3:
            img[j,i,0]=255
            img[j,i,1]=255
            img[j,i,2]=255
    return img


#定义椒噪声
def pepper(img,n):
    m=int((img.shape[0]*img.shape[1])*n)
    for b in range(m):
        i=int(np.random.random()*img.shape[1])
        j=int(np.random.random()*img.shape[0])
        if img.ndim==2:
            img[j,i]=0
        elif img.ndim==3:
            img[j,i,0]=0
            img[j,i,1]=0
            img[j,i,2]=0
    return img



for mode in range(2):
    pathroot = './data/train/'
    
    
    if mode==0:
        pathroot = './data/train/'
        
    if mode==1:
        pathroot = './data/validation_process/'
        

    cat = 0
    resultroot = os.listdir(pathroot)
    count = 0


    for pathtemp in resultroot:
        if pathtemp != '.DS_Store':
            path = os.path.join(pathroot, pathtemp)
            result = os.listdir(path)
            save_dir=path+'/'
            im = [os.path.join(path, f) for f in result if f.endswith('.jpg')]
            for i, f in enumerate(im[:]):
                i = i + 1
                '''if mode == 0:
                    print('开始扩充')
                    img_create(f, save_dir, str(i), num=99)
                    print("train: ", i)'''
                if mode == 1:
                    print('开始扩充')
                    img_create(f, save_dir, str(i), num=9)
                    print("test: ", i)



