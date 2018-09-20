#!/usr/bin/env python3
# -*- coding:utf-8 -*-
'''
训练数据
author:Administrator
datetime:2018/3/24/024 19:52
software: PyCharm
'''

# 对样本进行预处理
from keras.preprocessing.image import ImageDataGenerator
from keras.models import Sequential
from keras.layers import Conv2D, MaxPooling2D,BatchNormalization
from keras.layers import Dropout, Flatten, Dense
from keras.callbacks import ModelCheckpoint,ReduceLROnPlateau
from keras.optimizers import SGD

# 设置训练参数
nb_train_samples =  66951  # 训练样本数
nb_validation_samples = 7762  # 测试样本数
nb_epoch =100  # 训练轮数
batch_size = 64 # 批次大小

# 图片尺寸
img_width, img_height, channels = 128, 128, 3
input_shape = (img_width, img_height, channels)

# 训练和测试数据路径
target = './data/'
train_data_dir = target + 'train_7Class'
validation_data_dir = target + 'validation_7Class'

# 图片生成器ImageDataGenerator
train_pic_gen = ImageDataGenerator(
    rescale=1. / 255,  # 对输入图片进行归一化到0-1区间
    # 根据需求进行进一步调整
    rotation_range=5,
    width_shift_range=0.1,
    height_shift_range=0.1,
)

# 测试集不做变形处理，只需归一化。
validation_pic_gen = ImageDataGenerator(rescale=1. / 255)

# 按文件夹生成训练集流和标签，
train_flow = train_pic_gen.flow_from_directory(
    train_data_dir,
    target_size=(img_width, img_height),  # 调整图像大小
    batch_size=batch_size,
    #color_mode='grayscale',  # 输入图片为灰度图片
    color_mode='rgb',
    classes=[str(i) for i in range(1, 8, 1)],
    class_mode='categorical')

# 按文件夹生成测试集流和标签，
validation_flow = validation_pic_gen.flow_from_directory(
    validation_data_dir,
    target_size=(img_width, img_height),  # 调整图像大小
    batch_size=batch_size,
    #color_mode='grayscale',  # 输入图片为灰度图片
    color_mode='rgb',
    classes=[str(i) for i in range(1, 8, 1)],  # 标签
    class_mode='categorical'  # 多分类
)

# 搭建模型
model = Sequential()

model.add(Conv2D(32, (3, 3), activation='relu', input_shape=input_shape))
model.add(Conv2D(32, (3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Dropout(0.25))

model.add(Conv2D(64, (3, 3), activation='relu'))
model.add(Conv2D(64, (3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Dropout(0.25))


model.add(Flatten())
model.add(Dense(256, activation='relu'))
model.add(Dropout(0.5))
model.add(Dense(7, activation='softmax'))
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

model.summary()

# 回调函数，保存最佳训练参数
#checkpointer=ReduceLROnPlateau(monitor='val_loss', factor=0.1, patience=1, verbose=1, mode='auto', epsilon=0.0001, cooldown=0, min_lr=0)
checkpointer2 = ModelCheckpoint(filepath="./weights/weights_7Class.h5", monitor='val_acc',verbose=1, save_best_only=True)

# 导入上次训练的权重
try:
    model.load_weights('./weights/weights_7Class.h5')
    print("load weights...")
except:
    print("not weights")
    pass

# 数据流训练API
model.fit_generator(
    train_flow,
    steps_per_epoch=nb_train_samples / batch_size,
    epochs=nb_epoch,
    validation_data=validation_flow,
    validation_steps=nb_validation_samples / batch_size,
    callbacks=[checkpointer2]
)

