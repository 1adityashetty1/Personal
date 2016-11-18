
from mnist import MNIST
import numpy as np
import sklearn.metrics as metrics
import scipy.io as test
import matplotlib.pyplot as plt
import time
import  scipy.stats as stats
NUM_CLASSES = 10

"""
This is an additional file that contains all the post-processing code for the neural network (plotting and Kaggle)
"""
def calculate_loss(labels,data,V,W):
	a=np.dot(relu(np.dot(data,V.T)),W.T)
	c = np.double(a - np.max(a))
	b=softMax(c)
	return np.sum(np.log(b+.0001)*labels)
def prepare_normalize(Xtrain):
	X_train = Xtrain.copy()
	mat = []
	for k in range(X_train.shape[1]):
		mat.append((X_train.T[k] - np.mean(X_train.T[k])) / 255)
	mat = np.array(mat)

	return mat.T
def one_hot(labels_train):
	'''Convert categorical labels 0,1,2,....9 to standard basis vectors in R^{10} '''
	return np.eye(NUM_CLASSES)[labels_train]
def predictNeuralNetwork(data,V,W):
	a=np.dot(relu(np.dot(data,V.T)),W.T)
	#print(np.exp(a - np.max(a)).shape)
	#print(np.sum(np.exp(a - np.max(a)), axis=1).shape)
	#print(np.max(a))
	c = np.double(a - np.max(a))
	b=softMax(a - np.max(a))
	return np.argmax(b,axis = 1)
def relu(x,derivative=False):
	if(derivative == True):
		return np.where(x>0, 1, 0)
	return np.where(x>0, x, 0)
def softMax(x):
	return np.double(np.exp(x)) / np.double(np.array([np.sum(np.exp(x), axis=1)]).T)
def load_dataset():
    mndata = MNIST('./kaggle/')
    X_train, labels_train = map(np.array, mndata.load_training())
    # The test labels are meaningless,
    # since you're replacing the official MNIST test set with our own test set
    X_test, _ = map(np.array, mndata.load_testing())
    # Remember to center and normalize the data...
    return X_train, labels_train, X_test
def plot_error(labels_train,X_train,tuple_array):
	one_train = one_hot(labels_train)
	loss = []
	error = []
	for t in tuple_array:
		V = t[0]
		W = t[1]
		pred_labels_train=predictNeuralNetwork(X_train,V,W)
		lo = calculate_loss(one_train,X_train,V,W)
		error.append(metrics.accuracy_score(labels_train, pred_labels_train))
		loss.append(-1*lo)
	return np.array(loss),np.array(error)
if __name__ == "__main__":
	data = load_dataset()
	print(data[2].shape)
	X_test = prepare_normalize(data[2])
	V = np.load("v.npy")
	W = np.load("W.npy")
	labels_train = np.load("labels_train.npy")
	X_train = np.load("X_train.npy")
	tuple_array = np.load("tuple_array.npy")
	loss,error = plot_error(labels_train,X_train,tuple_array)
	for i in range(len(loss)):
		if (np.isnan(loss[i])):
			loss[i] = (loss[i+1] + loss[i-1])/2
	xarray = np.linspace(0,150000,num =15)
	plt.plot(xarray,loss,label = 'Loss over 3 Epochs', color = 'blue')
	plt.ylabel('Cross Entropy Loss')
	plt.xlabel('Iterations')
	plt.show()
	plt.plot(xarray,error,label = 'Error over 3 Epochs', color = 'green')
	plt.ylabel('Training Accuracy')
	plt.xlabel('Iterations')
	plt.show()
	pred_labels_test = predictNeuralNetwork(X_test,V,W)
	np.savetxt('new.csv',np.dstack((np.arange(1,pred_labels_test.size+1),pred_labels_test.T))[0],"%d,%d",header="Id,Category")
