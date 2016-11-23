from mnist import MNIST
import sklearn.metrics as metrics
import numpy as np
import numpy as np
import scipy.io as test
import matplotlib.pyplot as plt
def load_dataset():
	data = test.loadmat('spam.mat')
	return data

def prepare_normalize(Xtrain):
	X_train = Xtrain.copy()
	for k in range(len(X_train)):
		X_train[k] = (X_train[k] - np.mean(X_train[k])) / np.std(X_train[k])
	return X_train.T

def prepare_log(Xtrain):
	return np.log(Xtrain +.1)
	# for i in range(np.shape(X_train)[0]):
	# 	for j in range(np.shape(X_train)[1]):
	# 		X_train[i][j] = np.log(X_train[i][j] + 0.1)

	# return X_train
def prepare_binary(Xtrain):
	return np.where(Xtrain>0, 1, 0)

def plot_loss(X,y,iterations = 2500):
	losses = []
	losses2 =[]
	for it in range(0,iterations,20):
		print(it)
		model = train_sgd(X,y,alpha =0.001,reg = .1,num_iter=it,alt_learn=False)
		model2 = train_sgd(X,y,alpha =0.15,reg = .1,num_iter=it,alt_learn=False)
		u = 1/(1+np.exp(-np.dot(X,model)))
		u2 = 1/(1+np.exp(-np.dot(X,model2)))
		loss = .1*np.dot(model.T,model) - (np.dot(y.T,np.log(u)) + np.dot((1-y).T, np.log(1-u)))
		loss2 = .1*np.dot(model2.T,model2) - (np.dot(y.T,np.log(u2)) + np.dot((1-y).T, np.log(1-u2)))
		losses.append(loss[0][0])
		losses2.append(loss2[0][0])
	plt.plot(np.arange(0,iterations,20),np.asarray(losses), label = 'GD', color = 'blue')
	plt.plot(np.arange(0,iterations,20),np.asarray(losses2), label = 'GD with alt learn', color = 'green')
	plt.legend(['stochastic', 'stochastic with alt_learn'])
	plt.show()



def predict(model, X):
	''' From model and data points, output prediction vectors '''
	s = np.dot(X,model)
	sTerm = 1/(1+np.exp(-s))
	return np.where(sTerm>0.5,1,0)

def train_gd(X_train, y_train, alpha=0.1, reg=0, num_iter=10000, alt_learn = False):
	''' Build a model from X_train -> y_train using batch gradient descent '''
	k = 0
	W = np.zeros((X_train.shape[1], y_train.shape[1]))
	alpha_copy = alpha
	for k in range(num_iter):
		if (alt_learn):
			alpha = alpha_copy/(k+1)
		s = np.dot(X_train,W)
		sTerm = 1/(1+np.exp(-s))
		term2 = np.subtract(y_train,sTerm)
		term3 = np.dot(X_train.T,term2)
		W = W - ((alpha/X_train.shape[0]) * np.subtract(2*reg*W,term3))
	return W
def train_sgd(X_train, y_train, alpha=0.1, reg=0, num_iter=10000, alt_learn = False):
	''' Build a model from X_train -> y_train using stochastic gradient descent '''
	k = 0
	alpha_copy = alpha
	np.random.seed(2016)
	W = np.zeros((X_train.shape[1], y_train.shape[1]))
	for k in range(num_iter):
		if (alt_learn):
			alpha = alpha_copy/(k + 1)
		index = np.random.randint(0,X_train.shape[0],1)
		s = np.dot(W.T,X_train[index].T)
		sTerm = 1/(1+np.exp(-s))
		term2 = np.subtract(y_train[index],sTerm)
		term3 = np.dot(X_train[index].T,term2)
		W = W - ((alpha) * np.subtract(2*reg*W,term3))
	return W

if __name__ == "__main__":
	data = load_dataset()
	X_train = data['Xtrain']
	y_train = data['ytrain']
	X_test = data['Xtest']
	X_train1 = prepare_normalize(X_train.T)
	X_train2 = prepare_log(X_train)
	X_train3 = prepare_binary(X_train)
	plot_loss(X_train2,y_train)
	# model = train_gd(X_train1, y_train, alpha= .001, reg=0.1, num_iter=60000)
	# pred_labels_train1 = predict(model, X_train1)
	# print("Train accuracy GD-normalize: {0}".format(metrics.accuracy_score(y_train, pred_labels_train1)))

	# model = train_gd(X_train2, y_train, alpha= .001, reg=0.1, num_iter=60000)
	# pred_labels_train2 = predict(model, X_train2)
	# X_test = prepare_log(X_test)
	# pred_labels_test = predict(model, X_test)
	# print("Train accuracy GD-log: {0}".format(metrics.accuracy_score(y_train, pred_labels_train2)))
	# np.savetxt('new.csv',np.dstack((np.arange(1,pred_labels_test.size+1),pred_labels_test.T))[0],"%d,%d",header="Id,Category")

	# model = train_gd(X_train3, y_train, alpha= .001, reg=0.1, num_iter=60000)
	# pred_labels_train3 = predict(model, X_train3)
	# print("Train accuracy GD-binarize: {0}".format(metrics.accuracy_score(y_train, pred_labels_train3)))

	# model = train_gd(X_train1, y_train, alpha= .15, reg=0.1, num_iter=60000,alt_learn = True)
	# pred_labels_train1 = predict(model, X_train1)
	# print("Train accuracy GD-normalize-alt: {0}".format(metrics.accuracy_score(y_train, pred_labels_train1)))

	# model = train_gd(X_train2, y_train, alpha= .15, reg=0.1, num_iter=60000,alt_learn = True)
	# pred_labels_train2 = predict(model, X_train2)
	# print("Train accuracy GD-log-alt: {0}".format(metrics.accuracy_score(y_train, pred_labels_train2)))

	# model = train_gd(X_train3, y_train, alpha= .15, reg=0.1, num_iter=60000,alt_learn = True)
	# pred_labels_train3 = predict(model, X_train3)
	# print("Train accuracy GD-binarize-alt: {0}".format(metrics.accuracy_score(y_train, pred_labels_train3)))



	# model = train_sgd(X_train1, y_train, alpha= .001, reg=0.1, num_iter=60000)
	# pred_labels_train1 = predict(model, X_train1)
	# print("Train accuracy SGD-normalize: {0}".format(metrics.accuracy_score(y_train, pred_labels_train1)))

	# model = train_sgd(X_train2, y_train, alpha= .001, reg=0.1, num_iter=60000)
	# pred_labels_train2 = predict(model, X_train2)
	# print("Train accuracy SGD-log: {0}".format(metrics.accuracy_score(y_train, pred_labels_train2)))

	# model = train_sgd(X_train3, y_train, alpha= .001, reg=0.1, num_iter=60000)
	# pred_labels_train3 = predict(model, X_train3)
	# print("Train accuracy SGD-binarize: {0}".format(metrics.accuracy_score(y_train, pred_labels_train3)))

	# model = train_sgd(X_train1, y_train, alpha= .15, reg=0.1, num_iter=60000,alt_learn = True)
	# pred_labels_train1 = predict(model, X_train1)
	# print("Train accuracy SGD-normalize-alt: {0}".format(metrics.accuracy_score(y_train, pred_labels_train1)))

	# model = train_sgd(X_train2, y_train, alpha= .15, reg=0.1, num_iter=60000,alt_learn = True)
	# pred_labels_train2 = predict(model, X_train2)
	# print("Train accuracy SGD-log-alt: {0}".format(metrics.accuracy_score(y_train, pred_labels_train2)))

	# model = train_sgd(X_train3, y_train, alpha= .15, reg=0.1, num_iter=60000,alt_learn = True)
	# pred_labels_train3 = predict(model, X_train3)
	# print("Train accuracy SGD-binarize-alt: {0}".format(metrics.accuracy_score(y_train, pred_labels_train3)))