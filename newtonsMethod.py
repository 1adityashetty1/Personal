import numpy as np
import scipy.io as test
import matplotlib.pyplot as plt
import scipy
#X = [.8,1,1.2,2.5,2.6,3,4.3,4,5,5.6,6.8,7,7.2,8]
#X2 = [.8,1,1.2,2.5,2.6,3,4.3,4,5,5.6,6.8,7,7.2,8,3]
#x_append = [1,1,  1,  1,1, 1, 1, 1,1,  1,  1,  1,1,  1]
#y = [ 0,0,  0,  0,  0,0,  0,1,1,  1,  1,1, 1, 1,1]

def train_newton_logistic(X_train, y_train, num_iter=3):
	''' Build a model from X_train -> y_train using batch gradient descent '''
	k = 0
	reg = .07
	W = np.matrix([1, 0]).T
	for k in range(num_iter):

		s = np.dot(X_train,W)
		sTerm = 1/(1+np.exp(-s))
		term2 = np.subtract(y_train,sTerm)
		term3 = np.dot(X_train.T,term2)
		wtferm = np.asarray(sTerm.T)[0]
		diagonal = np.dot(np.diag(wtferm),np.diag(1-wtferm))
		term4 =scipy.linalg.inv(np.add(2*reg*np.eye(2),np.dot(np.dot(X_train.T,diagonal),X_train)))
		W = W - np.dot(term4, np.subtract(2*reg*W,term3))
	return W

def train_newton_ridge(X_train,y_train,num_iter=3):
	reg = .07
	W = np.matrix([1,0]).T
	for k in range(num_iter):
		hessian = np.add(2*reg*np.eye(2),np.dot(X_train.T,X_train))
		
		gradient = np.subtract(2*reg*W ,np.dot(X_train.T,np.subtract(y_train,np.dot(X_train,W))))
		
		W= W - np.dot(scipy.linalg.inv(hessian),gradient)
	return W
def prepare_normalize(Xtrain):
	X_train = Xtrain.copy()
	for k in range(len(X_train)):
		X_train[k] = (X_train[k] - np.mean(X_train[k])) / np.std(X_train[k])
	return X_train.T

if __name__ == "__main__":


	X_train = np.matrix([X]).T	
	X_train = np.concatenate((prepare_normalize(X_train.T),np.matrix(x_append).T),axis = 1)
	newrow = [3, 1]
	X_train = np.insert(X_train,14,newrow,axis = 0)
	y_train = np.matrix(y).T
	logistic = train_newton_logistic(X_train,y_train)
	print("Logistic B")
	print(logistic)
	ridge = train_newton_ridge(X_train,y_train)
	print("ridge B")
	print(ridge)

	s = np.dot(X_train,logistic)
	sTerm = 1/(1+np.exp(-s))

	ridgeS= np.dot(X_train,ridge)
	Xplot = np.asarray(X_train[:,0])
	plt.plot(Xplot,sTerm)
	plt.plot(Xplot,ridgeS)

	plt.scatter(Xplot,y)
	plt.show()
	
