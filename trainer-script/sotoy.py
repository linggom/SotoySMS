import pandas as pd
import re
import numpy as np
from sklearn.feature_extraction.text import CountVectorizer
import tensorflow as tf
from sklearn.cross_validation import train_test_split
data = pd.read_csv("data/train.csv")
df_train, df_test = train_test_split(data, test_size=0.9)

x = df_train[df_train.is_spam == 0].shape[0]
y = df_train[df_train.is_spam == 1].shape[0]

print x* 100./(x+y)

def transform_labels(labels):
    transformed_labels = []
    for label in labels:
        if label == 0:
            transformed_labels.append([1, 0])
        else:
            transformed_labels.append([0, 1])
    return np.array(transformed_labels)


def clean_text(text):
    text = text.lower()
    text = text.strip()
    text = re.sub('\n', ' ', text)
    text = re.sub('\r', ' ', text)
    text = re.sub('\d', ' ', text)
    text = re.sub(r'\W+', ' ', text)
    return text

feature_desc = data['description'].replace(np.nan, ' ')
feature = data['title'] + " " + feature_desc
feature = feature.replace('[^0-9a-zA-Z]+', ' ', regex=True)

desc = df_train['description'].replace(np.nan, ' ')
texts = df_train['title'] + " " + desc
texts = texts.replace('[^0-9a-zA-Z]+', ' ', regex=True)
labels = transform_labels(df_train.is_spam)


test_texts = []
test_labels = []
for row in df_test.values:
    text = row[0] + " "
    if pd.notnull(row[1]):
        text = text + row[1]
    text = clean_text(text)
    test_texts.append(text)
    if row[2] == 0:
        test_labels.append([1, 0])
    else:
        test_labels.append([0, 1])

test_labels = np.array(test_labels)
cv = CountVectorizer()
cv.fit(feature)

matrix = cv.transform(texts)
matrix_test = cv.transform(test_texts)
train_count, feature_wide = matrix.shape
label_size = 2


matrix = matrix.toarray()
x = tf.placeholder(tf.float32, [None, feature_wide])
W = tf.Variable(tf.zeros([feature_wide, label_size]))
b = tf.Variable(tf.zeros([label_size]))
y = tf.nn.softmax(tf.matmul(x, W) + b)
y_ = tf.placeholder(tf.float32, [None, label_size])

cross_entropy = tf.reduce_mean(-tf.reduce_sum(y_ * tf.log(y), reduction_indices=[1]))
train_step = tf.train.GradientDescentOptimizer(0.01).minimize(cross_entropy)
init = tf.initialize_all_variables()
sess = tf.Session()
sess.run(init)
sess.run(train_step, feed_dict={x: matrix, y_: labels})


correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
matrix_test = matrix_test.toarray()
print(sess.run(accuracy, feed_dict={x: matrix_test, y_: test_labels})) #0.933168


_W = W.eval(sess)
_b = b.eval(sess)
sess.close()


g_2 = tf.Graph()
with g_2.as_default():
    # Reconstruct graph
    print matrix.shape[1]
    x_2 = tf.placeholder("float", [None, matrix.shape[1]], name="input")
    W_2 = tf.constant(_W, name="constant_W")
    b_2 = tf.constant(_b, name="constant_b")
    y_2 = tf.nn.softmax(tf.matmul(x_2, W_2) + b_2, name="output")

    sess_2 = tf.Session()

    init_2 = tf.initialize_all_variables();
    sess_2.run(init_2)

    graph_def = g_2.as_graph_def()

    tf.train.write_graph(graph_def, 'model/',
                         'sotoy2.pb', as_text=False)

    # Test trained model
    y__2 = tf.placeholder("float", [None, 2])
    correct_prediction_2 = tf.equal(tf.argmax(y_2, 1), tf.argmax(y__2, 1))
    accuracy_2 = tf.reduce_mean(tf.cast(correct_prediction_2, "float"))
    print(accuracy_2.eval({x_2: matrix_test, y__2: test_labels}, sess_2))



