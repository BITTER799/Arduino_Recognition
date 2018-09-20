import os
import cv2
import numpy as np
import skimage
from skimage import io
import matplotlib.pyplot as plt


def adjustandmove(mypath,p,count1):
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
                                    cv2.imwrite(path + '/' + str(count1) + 'cutcut.jpg', temp1)
                                    count1 += 1
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
                                else:
                                    break



def locateobject(mypath,count1):
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
                # cv2.imshow("Image", cropImg)
                # cv2.waitKey(0)

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
cat = 0
pathroot= '/Users/tangdou/Desktop/分类'
resultroot=os.listdir(pathroot)
logfile=open ('/Users/tangdou/Desktop/分类/日志/logfile.txt','a')
count=0

locateobject(resultroot,count)
count=0
adjustandmove(resultroot,0.7,count)
count=0


#修改分辨率并覆盖
for pathtemp in resultroot:
    if pathtemp!='.DS_Store':
        path = os.path.join(pathroot, pathtemp)
        result = os.listdir(path)
        im = [os.path.join(path, f) for f in result if f.endswith('.jpg')]
        for i, f in enumerate(im[:]):
            i = i + 1
            im_c = cv2.imread(f)
            res = cv2.resize(im_c, (128, 128), interpolation=cv2.INTER_CUBIC)
            cv2.imwrite(f, res)


for pathtemp in resultroot:
    if pathtemp!='.DS_Store':
        path = os.path.join(pathroot, pathtemp)
        result = os.listdir(path)
        im = [os.path.join(path, f) for f in result if f.endswith('.jpg')]
        for i, f in enumerate(im[:]):
            if pathtemp =='blue':
                cat=0
            if pathtemp =='red':
                cat=1
            if pathtemp =='green':
                cat=2
            if pathtemp =='yellow':
                cat=3
            i = i + 1

            # origin
            im_origin = cv2.imread(f)

            # 旋转180度
            im_180 = cv2.imread(f)
            img180 = cv2.flip(im_180, -1)

            # 旋转90度
            im_90 = cv2.imread(f)
            img90 = np.rot90(im_90)


            # 镜像翻转
            im_flip = cv2.imread(f)
            imgflip = cv2.flip(im_flip, 1)

            # 垂直翻转
            im_ver = cv2.imread(f)
            imgver = cv2.flip(im_ver, 0)

            # 椒盐噪声0.001
            im_sp1 = cv2.imread(f)
            imgsaltnoise1 = saltpepper(im_sp1, 0.001)

            # 椒盐噪声0.01
            im_sp2 = cv2.imread(f)
            imgsaltnoise2 = saltpepper(im_sp2, 0.01)

            # 椒盐噪声0.02
            im_sp3 = cv2.imread(f)
            imgsaltnoise3 = saltpepper(im_sp3, 0.02)

            # 盐噪声0.02
            im_s1 = cv2.imread(f)
            imgsaltnoise4 = salt(im_s1, 0.02)

            # 椒噪声0.02
            im_p1 = cv2.imread(f)
            imgsaltnoise5 = pepper(im_p1, 0.02)

            # 高斯噪声
            img_gauss = skimage.io.imread(f)
            gaussianResult = skimage.util.random_noise(img_gauss, mode='gaussian', seed=None, clip=True)


            # 对比度调节
            img_contrast = cv2.imread(f)
            imgcontrast= contrast_demo(img_contrast, 1.3, 0)

            #饱和度调节
            img_sat = skimage.io.imread(f)
            imgsat = saturation_demo(img_sat)

            if count<506:
                for k in range(13):
                    logfile.write(str(cat) + '\n')
                cv2.imwrite(path + '/' + str(count) + '.jpg', im_origin)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', img180)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', img90)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgflip)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgver)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgsaltnoise1)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgsaltnoise2)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgsaltnoise3)
                count += 1
                skimage.io.imsave(path + '/' + str(count) + '.jpg', gaussianResult)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgsaltnoise4)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgsaltnoise5)
                count += 1
                cv2.imwrite(path + '/' + str(count) + '.jpg', imgcontrast)
                count += 1
                skimage.io.imsave(path + '/' + str(count) + '.jpg', imgsat)
                count += 1
            else:
                break




            #cv2.imwrite(path + '/' + str(i) + '.180.jpg', im_origin)
            #cv2.imwrite(path + '/' + str(i) + '.180.jpg', img180)
            #cv2.imwrite(path + '/' + str(i) + '.90.jpg', img90)
            #cv2.imwrite(path + '/' + str(i) + '.flip.jpg', imgflip)
            #cv2.imwrite(path + '/' + str(i) + '.ver.jpg', imgver)
            #cv2.imwrite(path + '/' + str(i) + '.spnoise1.jpg', imgsaltnoise1)
            #cv2.imwrite(path + '/' + str(i) + '.spnoise2.jpg', imgsaltnoise2)
            #cv2.imwrite(path + '/' + str(i) + '.spnoise3.jpg', imgsaltnoise3)
            #skimage.io.imsave(path + '/' + str(i) + '.gaussnoise1.jpg', gaussianResult)
            #cv2.imwrite(path + '/' + str(i) + '.salt.jpg', imgsaltnoise4)
            #cv2.imwrite(path + '/' + str(i) + '.pepper.jpg', imgsaltnoise5)



logfile.close()







#for f in path:
    #if f.endswith('.jpg'):
        #im_file = [os.path.join(path, f)]
        #im = cv2.imread(im_file)
# go into def demo(net, image_name):
#im_file = os.path.join(path, im_name)

#im = im_o[:, :, (2, 1, 0)] # change channel


  #cv2.imshow('', res)
  #cv2.waitKey(0)
  #cv2.destroyAllWindows()

#M_crop_elephant = np.array([
   # [1.6, 0, -150],
    #[0, 1.6, -240]
#], dtype=np.float32)

#img_elephant = cv2.warpAffine(im, M_crop_elephant, (400, 600))
#thumb = cv2.CreateImage((im.width / 2, im.height / 2), 8, 3) #Create an image that is twice smaller than the original

#cv2.Resize(im, thumb) #resize the original image into thumb
#fig, ax = plt.subplots(figsize=(12, 12))
#ax.imshow(res, aspect='equal')
#plt.axis('off')
#plt.tight_layout()
#plt.draw()
#plt.show()