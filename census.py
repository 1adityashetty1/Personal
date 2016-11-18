import decision_tree as dt
import csv
from sklearn.feature_extraction import DictVectorizer
from mnist import MNIST
import sklearn.metrics as metrics
import numpy as np
import scipy.io as test
import scipy.stats as stats
import matplotlib.pyplot as plt


if __name__ == "__main__":
	with open('hw5_data/census_data/train_data.csv') as csvfile:
		trainlist = []
		labels = []
		v = DictVectorizer(sparse=False)
		train = csv.DictReader(csvfile)
		
		for t in train:
			for key in t.keys():
				try:
					if (key == "label"):
						labels.append(int(t[key]))
					elif(t[key] == "?"):
						t[key] = np.nan
					else:
						t[key] = float(t[key])
				except ValueError:
					pass
			t.pop("label")
			trainlist.append(t)
		X_train = v.fit_transform(trainlist)
		col_mean = stats.nanmean(X_train,axis=0) +1
		inds = np.where(np.isnan(X_train))
		X_train[inds]=np.take(col_mean,inds[1])
		y_train = np.array(labels)

	with open('hw5_data/census_data/test_data.csv') as testfile:
		testlist = []
		test = csv.DictReader(testfile)
		
		for t in test:
			for key in t.keys():
				try:
					if(t[key] == "?"):
						t[key] = np.nan
					t[key] = float(t[key])
				except ValueError:
					pass
			testlist.append(t)
		X_test = v.transform(testlist)
		col_mean = stats.nanmean(X_test,axis=0)+1
		inds = np.where(np.isnan(X_test))
		X_test[inds]=np.take(col_mean,inds[1])


	perm = np.random.permutation(X_train.shape[0])
	X_train = X_train[perm]
	y_train = y_train[perm]
	th = .8 * X_train.shape[0]
	X_valid = X_train[th:,:]
	X_train = X_train[:th,:]
	y_valid = y_train[th:]
	y_train = y_train[:th]
	# tree = dt.DecisionTree(X_train,y_train)
	# tree.train(X_train,y_train)
	# print(X_train.shape)
	# pred_labels_train = tree.predict(X_train) 
	# #print(np.reshape(X_valid[1,:],(-1,108)))
	# pred_labels_valid = tree.predict(np.reshape(X_valid[1,:],(-1,108)),fall=1)
	#pred_labels_test = tree.predict(X_test)
	forest = dt.Forest(X_train,y_train,num_trees = 10)
	forest.train(X_train,y_train)
	pred_labels_train = forest.predict(X_train)
	pred_labels_valid = forest.predict(X_valid)
	pred_labels_test = forest.predict(X_test)
	print(forest.unique_roots())
	print("Train accuracy: {0}".format(metrics.accuracy_score(y_train, pred_labels_train)))
	print("Valid accuracy: {0}".format(metrics.accuracy_score(y_valid, pred_labels_valid)))
	np.savetxt('new5.csv',np.dstack((np.arange(1,pred_labels_test.size+1),pred_labels_test.T))[0],"%d,%d",header="Id,Category")