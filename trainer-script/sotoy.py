import pandas as pd
import re
import numpy as np
from sklearn.feature_extraction.text import CountVectorizer
import tensorflow as tf
from sklearn.cross_validation import train_test_split
from sklearn.cross_validation import StratifiedKFold


def transform_labels(labels):
    transformed_labels = []
    for label in labels:
        if label == 0:
            transformed_labels.append([1, 0])
        else:
            transformed_labels.append([0, 1])
    return np.array(transformed_labels)


def clean_text(texts):
    texts = texts.replace(np.nan, ' ')
    texts = texts.replace('[^0-9a-zA-Z]+', ' ', regex=True)
    return texts


def train(sess, matrix_train, train_labels):
    x = tf.placeholder(tf.float32, [None, feature_wide])
    W = tf.Variable(tf.zeros([feature_wide, label_size]))
    b = tf.Variable(tf.zeros([label_size]))
    y = tf.nn.softmax(tf.matmul(x, W) + b)
    y_ = tf.placeholder(tf.float32, [None, label_size])

    cross_entropy = tf.reduce_mean(-tf.reduce_sum(y_ * tf.log(y), reduction_indices=[1]))
    train_step = tf.train.GradientDescentOptimizer(0.01).minimize(cross_entropy)
    init = tf.initialize_all_variables()
    sess.run(init)
    sess.run(train_step, feed_dict={x: matrix_train, y_: train_labels})


    _W = W.eval(sess)
    _b = b.eval(sess)
    
    return W, b, _W, _b, x, y, y_

def evaluate_accuracy(sess, y, y_, x, matrix_test, test_labels):
    correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
    accuracy_result = sess.run(accuracy, feed_dict={x: matrix_test, y_: test_labels})
    return accuracy_result 

data = pd.read_csv("data/train.csv")
feature = clean_text(data['title'])
labels = transform_labels(data.is_spam)

spam_labels_count = data[data.is_spam == 0].shape[0]
non_spam_labels_count = data[data.is_spam == 1].shape[0]
print "Komposisi Spam : ", (spam_labels_count * 100. / (non_spam_labels_count + spam_labels_count))
cv = CountVectorizer(ngram_range=(1,1), stop_words=("di", "anda", "info", "no", "in", "http", "pin", "ke", "com","www"))
cv.fit(feature) 
matrix_all = cv.transform(feature).toarray()
#import ipdb;ipdb.set_trace()
skfld = StratifiedKFold(data.is_spam, n_folds=10)

accuracy_sum = []

for idx, row in enumerate(skfld):
    train_index, test_index  = row
    df_train, df_test = data.iloc[train_index], data.iloc[test_index]    

    train_texts = clean_text(df_train['title'])
    train_labels = transform_labels(df_train.is_spam)

    test_texts = clean_text(df_test['title'])
    test_labels = transform_labels(df_test.is_spam)

    matrix_train = cv.transform(train_texts).toarray()
    matrix_test = cv.transform(test_texts).toarray()

    train_count, feature_wide = matrix_train.shape
    label_size = 2

    sess = tf.Session()
    W, b, _W, _b, x, y, y_ = train(sess, matrix_train, train_labels)
    accuracy_result = evaluate_accuracy(sess, y, y_, x, matrix_test, test_labels )
    print "Iteration %s : %s" % (idx, accuracy_result) 
    accuracy_sum.append(accuracy_result)


    _W = W.eval(sess)
    _b = b.eval(sess)
    sess.close()

print "Avarage  10 cross_validation accuracy = ", np.mean(accuracy_sum) * 100., "%"


print "Fit all data"
sess = tf.Session()
W, b, _W, _b, x, y, y_ = train(sess, matrix_all, labels)
_W = W.eval(sess)
_b = b.eval(sess)
sess.close()


g_2 = tf.Graph()
with g_2.as_default():
    # Reconstruct graph
    print "Please change the shape to : ", matrix_all.shape[1]
    x_2 = tf.placeholder("float", [None, matrix_all.shape[1]], name="input")
    W_2 = tf.constant(_W, name="constant_W")
    b_2 = tf.constant(_b, name="constant_b")
    y_2 = tf.nn.softmax(tf.matmul(x_2, W_2) + b_2, name="output")

    sess_2 = tf.Session()

    init_2 = tf.initialize_all_variables();
    sess_2.run(init_2)

    graph_def = g_2.as_graph_def()

    tf.train.write_graph(graph_def, 'model/',
                         'sotoy2.pb', as_text=False)

    print "Finish write model to file"



