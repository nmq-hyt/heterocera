import matplotlib.pyplot as plt
import numpy as np

import time
import PIL
import tensorflow as tf
import tensorflow_hub as hub
from tensorflow import *
from keras import layers
from keras.models import Sequential
import seaborn as sns
from sklearn.preprocessing import normalize
data_filepath = '/home/nmq-hyt/Pictures/moths_common_work'
efficinet = 'https://tfhub.dev/google/efficientnet/b3/classification/1'
test_filepath = '/home/nmq-hyt/Pictures/test_moths'

# create the feature transfer neural network model
feature_extractor_layer = hub.KerasLayer(
    efficinet,
    input_shape=(300, 300, 3),
    trainable=False)

# data split, training and validation, and testing
train_ds = tf.keras.utils.image_dataset_from_directory(
    data_filepath,
    validation_split=0.2,
    subset="training",
    seed=123)
val_ds = tf.keras.utils.image_dataset_from_directory(
    data_filepath,
    validation_split=0.2,
    subset="validation",
    seed=123)   
test_ds = tf.keras.utils.image_dataset_from_directory(
    test_filepath,
)

#the sixty-nine moth labels we're trying to assig
labels = train_ds.class_names
print(labels)
num_class = 69
# efficinet requires images to be scaled to 224px
IMG_SIZE = 224
resize_and_rescale = tf.keras.Sequential([
  layers.Resizing(IMG_SIZE, IMG_SIZE),
  layers.Rescaling(1./255)
])
#cache the dataset instead of trying to load a thousand images from disk
AUTOTUNE = tf.data.AUTOTUNE
train_ds = train_ds.cache().prefetch(buffer_size=AUTOTUNE)
val_ds = val_ds.cache().prefetch(buffer_size=AUTOTUNE)

# data augmentation
# generate images by absolutely messing up your original images
# this is a preprocessing layer, data_augmentation is really a variable
# that will be applied to the dataset
data_augmentation = tf.keras.Sequential([
  layers.RandomFlip("horizontal_and_vertical"),
  layers.RandomRotation(0.3),
  layers.RandomBrightness(factor=0.2,seed=123),
  layers.RandomContrast(factor=0.2,seed=123),
  layers.RandomTranslation(height_factor=(-0.2,0.2),width_factor=(-0.2,0.2),fill_mode='reflect',seed=123),
  layers.RandomCrop(height=270,width=270,seed=123),
])


#construct the neural network learning model
# augment data -> rescale to suit efficientnet -> run it through efficinent N times, where n is the number of classes in your machine-learning model.
model = tf.keras.Sequential([
  data_augmentation,
  resize_and_rescale,
  feature_extractor_layer,
  tf.keras.layers.Dense(num_class)

])

# compile the model/training
model.compile(
  optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
  loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
  metrics=['accuracy'])

#fit the model
model.fit(
  train_ds,
  validation_data=val_ds,
  epochs=10,
  workers=1,
  use_multiprocessing=True

)

# set up a bunch of stuff in order to export the model
# to a tensorflowlite model that can be used in the app
t = time.time()
export_path = "/home/nmq-hyt/Projects/python/dumper"
hurd = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68]
hurd1 = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
actual = [labels for _, labels in val_ds.unbatch()]
predicted = model.predict(val_ds,69)
print(type(predicted))
print(predicted)
actual = tf.stack(actual,0)
predicted = tf.concat (predicted,axis=0)
predicted = tf.argmax(predicted,axis=1)
print(actual)

# trying to get "both" heatmaps for validation test set causes my linux to kill the process,
# so i have to comment part of it
# in hindsight this could've been a function
# but python makes me want to write the laziest code possible


# plt.figure(1)
# cm = tf.math.confusion_matrix(actual,predicted)
# ax = sns.heatmap(cm, annot=True, fmt='g')
# sns.set(rc={'figure.figsize':(12, 12)})
# sns.set(font_scale=1.4)
# ax.set_title('Confusion matrix of moth recognition for validation')
# ax.set_xlabel('Predicted moth')
# ax.set_ylabel('Actual moth')


# plt.yticks(rotation=0)
# ax.set_xticks(hurd)
# ax.set_xticklabels(labels)
# ax.set_yticks(hurd)
# ax.set_yticklabels(labels)

labels1 = labels[0:12]

# actual = [labels for _, labels in train_ds.unbatch()]
# predicted = model.predict(valid_Ds,69)
# actual = tf.stack(actual,0)
# predicted = tf.concat (predicted,axis=0)
# predicted = tf.argmax(predicted,axis=1)
# print(actual)

plt.figure(1)
cm = tf.math.confusion_matrix(actual,predicted)

# normalize because it makes the vastly different ranges of classifications make some sort of sense
cm = normalize(cm.numpy(),norm='l1')
np.save('validation',cm)
print(cm.shape)
cm1 = cm[0:12,0:12]
ax = sns.heatmap(cm1, annot=True,cmap='Blues',square=True,fmt=".2%")

sns.set(rc={'figure.figsize':(250, 250)})
sns.set(font_scale=1)
ax.set_title('Confusion matrix of action recognition for training')
ax.set_xlabel('Predicted Moth')
ax.set_ylabel('Actual Moth')

plt.yticks(rotation=0)
ax.set_xticks(hurd1)
ax.set_xticklabels(labels1)
ax.set_yticks(hurd)
ax.set_yticklabels(labels)


tp = np.diag(cm)
precision = dict()
recall = dict()

for i in range(len(labels)):
    col = cm[:, i]
    fp = np.sum(col) - tp[i] # Sum of column minus true positive is false negative

    row = cm[i, :]
    fn = np.sum(row) - tp[i] # Sum of row minus true positive, is false negative

    precision[labels[i]] = tp[i] / (tp[i] + fp) # Precision 

    recall[labels[i]] = tp[i] / (tp[i] + fn) # Recall

print("Precision: Ratio of positively predicted moths (correctly identified) to predicted moths - validity")
print(precision)
print("Recall: Proportion of predicted moths that are correct matches, to positive matches - completeness")
print(recall)
plt.show()

# actually convert the model we've made to tflite form, then write it
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open('model.tflite', 'wb') as f:
  f.write(tflite_model)

