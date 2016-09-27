import pandas as pd
from sklearn.feature_extraction.text import CountVectorizer
import numpy as np

df = pd.read_csv('data/train.csv')
f = open('feature.txt','w')
cv = CountVectorizer(stop_words=("di", "anda", "info", "no", "in", "http", "pin", "ke", "com","www"))
texts = df['title']
texts = texts.replace('[^0-9a-zA-Z]+', ' ', regex=True)
cv.fit(texts)
texts = cv.get_feature_names()
size = len(texts)
for i, text in enumerate(texts):
    if i == 0 :
        f.write(text)
    else:
        f.write("\n" + text)
f.close()
print len(texts)
