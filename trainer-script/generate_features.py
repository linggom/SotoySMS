import pandas as pd
from sklearn.feature_extraction.text import CountVectorizer
import numpy as np

df = pd.read_csv('data/train.csv')
f = open('feature.txt','w')
cv = CountVectorizer()
desc = df['description'].replace(np.nan, ' ')
texts = df['title'] + " " + desc
texts = texts.replace('[^0-9a-zA-Z]+', ' ', regex=True)
cv.fit(texts)
texts = cv.get_feature_names()
for text in texts:
    f.write(text+"\n")
f.close()
print len(texts)
