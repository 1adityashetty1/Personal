from mnist import MNIST
import numpy as np
import sklearn.metrics as metrics
import scipy.io as test
import matplotlib.pyplot as plt
import time
NUM_CLASSES = 10
"""
This is the actual body of the neural network with preprocessing
"""
def load_dataset():
	mndata = MNIST('./data/')
	X_train, labels_train = map(np.array,mndata.load_training())
	X_test,_ = map(np.array,mndata.load_testing())
	return X_train,labels_train,X_test
def one_hot(labels_train):
	'''Convert categorical labels 0,1,2,....9 to standard basis vectors in R^{10} '''
	return np.eye(NUM_CLASSES)[labels_train]

def relu(x,derivative=False):
	if(derivative == True):
		return np.where(x>0, 1, 0)
	return np.where(x>0, x, 0)
def softMax(x):
	return np.double(np.exp(x)) / np.double(np.array([np.sum(np.exp(x), axis=1)]).T)

def trainNeuralNetwork(data,labels,V = None, W = None, learning_rate = .0153846, decay_rate =.65,iterations=60000):
	np.random.seed(2016)
	tuple_array = []
	if(V is None):
		V= np.double(np.random.normal(0,.2,size = (200,784)))
		# V = np.ones((200,785))
		# V[:,:-1] = V
		W = np.double(np.random.normal(0,.2,size = (10,200)))
		# W = np.ones((10,201))
		# W[:,:-1] = W
		
	#gradient_descent update
	for it in range(iterations):
		#print(data.shape[0])
		index = np.random.randint(0,data.shape[0] ,1)
		#print(index.shape)
	# 	#first layer
		l0 = data[index]
		#print(l0.shape)
		l0_bias=np.insert(l0,l0.shape[1],1, axis = 1)
		#print(l0_bias.shape)
		#add bias term
		#hidden layer
		l1 = relu(np.dot(l0_bias,np.c_[V,np.ones(200)].T))
		#print(l1.shape)
		#add bias term
		l1_bias =np.insert(l1,l1.shape[1],1,axis =1)
		#print(l1_bias.shape)
		l2 = np.dot(l1_bias,np.c_[W,np.ones(10)].T)
		#stabilize L2
		#print((l2- np.max(l2)).shape)
		#print(l2)
		#print(np.max(l2))
		l2out = softMax(l2-np.max(l2))
		error = np.array(l2out - labels[index]).T
		#print(error.shape)
		#print(np.array([l1]).shape)
		gradient = np.dot(error,np.array(l1))
		#print(error.shape)
		W = W-(learning_rate * gradient)

		error2 = np.dot(W.T,error) * relu(l1,derivative=True).T
		#print(np.array(l0).shape)
		gradient2 = np.dot(error2,np.array(l0))
		#print(gradient2.shape)

		V = V-(learning_rate * gradient2)
		#print(np.c_[V,np.ones(200)])
		# if(np.isnan(V).any()):
		# 	return V,W
		if(it%10000 == 0):
			tuple_array.append((V,W))
			print(it)
		if(it%data.shape[0] == 0):
			learning_rate = learning_rate*decay_rate

		#cross-entrophy loss
		#J = np.dot(l2,labels)
		#where do we multiply it with the error term?
	return V,W,tuple_array


def predictNeuralNetwork(data,V,W):
	a=np.dot(relu(np.dot(data,V.T)),W.T)
	#print(np.exp(a - np.max(a)).shape)
	#print(np.sum(np.exp(a - np.max(a)), axis=1).shape)
	#print(np.max(a))
	c = np.double(a - np.max(a))
	b=softMax(a - np.max(a))
	return np.argmax(b,axis = 1)


def prepare_normalize(Xtrain):
	X_train = Xtrain.copy()
	mat = []
	for k in range(X_train.shape[1]):
		mat.append((X_train.T[k] - np.mean(X_train.T[k])) / 255)
	mat = np.array(mat)

	return mat.T

if __name__ == "__main__":
	data = load_dataset()
	# print(data[0].shape)
	# print(data[1].shape)
	# print(data[2].shape)
	# one_hot(data[1])
	np.random.seed(2016)
	perm = np.random.permutation(data[0].shape[0])
	X = prepare_normalize(data[0][perm])
	labels = data[1][perm]
	X_train = X[:50000,:]
	labels_train=labels[:50000]
	one_train = one_hot(labels_train)
	X_test = X[50000:,:]

	labels_test = labels[50000:]
	t0 = time.time()
	V,W,tuple_array = trainNeuralNetwork(X_train,one_train,iterations=100000)
	t1 = time.time()
	print(t1-t0)
	np.save("X_train",X_train)
	np.save("labels_train",labels_train)
	np.save("tuple_array",tuple_array)
	np.save("v",V)
	np.save("W",W)
	pred_labels_train=predictNeuralNetwork(X_train,V,W)
	pred_labels_test = predictNeuralNetwork(X_test,V,W)
	print("Train accuracy: {0}".format(metrics.accuracy_score(labels_train, pred_labels_train)))
	print("Test accuracy: {0}".format(metrics.accuracy_score(labels_test, pred_labels_test)))
	