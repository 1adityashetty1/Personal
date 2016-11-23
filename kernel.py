from mnist import MNIST
import sklearn.metrics as metrics
import numpy as np
import numpy as np
import scipy.io as test
import matplotlib.pyplot as plt
def create_points():
	np.random.seed(2017)
	wMat = []
	vMat = []
	for i in range(100):
		theta = np.random.uniform(0,2*np.pi)
		w1 = np.random.normal(0,1)
		w2 = np.random.normal(0,1)
		
		x1 = 8*np.cos(theta) + w1
		x2 = 8*np.sin(theta) + w2
		wMat.append(np.array([x1,x2]))
		vMat.append(np.random.normal(0,1,2))

	return np.vstack((np.array(wMat),np.array(vMat))), np.append(np.ones(100),-1*np.ones(100))

def lift(X_train, lFunction,sigma):
	mat = np.ones((X_train.shape[0],X_train.shape[0]))
	for i in range(X_train.shape[0]):
		z = X_train[i,:]
		for j in range(X_train.shape[0]):
			x = X_train[j,:]
			mat[i,j]= lFunction(x,z,sigma)

	return mat
def classify(X_train,new_data,lFunction,sigma):
	mat = np.ones((X_train.shape[0],new_data.shape[0]))
	for i in range(new_data.shape[0]):
		z = new_data[i,:]
		for j in range(X_train.shape[0]):
			x = X_train[j,:]
			mat[j,i] = lFunction(x,z,sigma)
	return mat		
def gaussian_lift(x,z,sigma = .001):
	return np.exp((-(np.linalg.norm(x-z, axis=None)**2))*sigma)
def quadratic_lift(x,z,sigma =.001):
	return (1 + np.dot(x,z.T))**2
def train(X_train,y_train,lFunction,sigma=1,reg = 1e-6):
	kernel = lift(X_train,lFunction,sigma)
	alpha = np.dot(np.linalg.inv(kernel+reg*np.eye(kernel.shape[0])),y_train)
	return alpha,kernel
def predict(alpha,kernel):
	return np.where(np.dot(kernel,alpha) > 0 , 1, -1)

if __name__ == "__main__":
	X_train,y_train = create_points()
	perm = np.random.permutation(X_train.shape[0])
	# plt.scatter(X_train[:100,0],X_train[:100,1], color = 'blue')
	# plt.scatter(X_train[100:,0],X_train[100:,1], color = 'yellow')
	# plt.legend(['Class 1', 'Class 2'])
	#plt.show()
	X_train =X_train[perm]
	y_train =y_train[perm]
	alpha,kernel = train(X_train,y_train,quadratic_lift,.0010)
	
	pred_labels_train = predict(alpha,kernel)

	x_min, x_max = X_train[:, 0].min() - 5, X_train[:, 0].max() + 5
	y_min, y_max = X_train[:, 1].min() - 5, X_train[:, 1].max() + 5
	xx,yy = np.meshgrid(np.arange(x_min, x_max, (x_max-x_min)/20 ),np.arange(y_min, y_max, (y_max-y_min)/20))
	new_data = np.c_[xx.ravel(), yy.ravel()]
	new_kernel = classify(X_train,new_data,quadratic_lift,.0010).T
	Z = predict(alpha,new_kernel)

	Z = Z.reshape(xx.shape)
	plt.contourf(xx,yy,Z,cmap=plt.cm.Paired)
	plt.axis('on')
	plt.scatter(X_train[:, 0], X_train[:, 1], c=y_train, cmap=plt.cm.Paired)
	plt.show()
	print("Train accuracy: {0}".format(metrics.accuracy_score(y_train, pred_labels_train)))
