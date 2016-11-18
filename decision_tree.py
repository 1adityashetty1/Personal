from mnist import MNIST
import sklearn.metrics as metrics
import numpy as np

import scipy.io as test
import scipy.stats as stats
import matplotlib.pyplot as plt
MAX_DEPTH = 9

def load_dataset():
	data = test.loadmat('hw5_data/spam_data/spam_data2.mat')
	return data
def impurity(left_label_hist, right_label_hist):
	totalLeft = np.sum(left_label_hist)
	totalRight = np.sum(right_label_hist)
	if(totalLeft == 0):
		p0 = 0
	else:
		p0= float(left_label_hist[0]/totalLeft)

		p1 = 1.0 - p0
	if(p0 == 0 or p0== 1):	
		eLeft = 0
	else:
		eLeft = -1*(p0 * np.log2(p0) + p1 * np.log2(p1))
	if (totalRight == 0):
		q0 = 0
	else:
		q0 = float(right_label_hist[0]/totalRight)
		q1 = 1.0- q0
	if(q0 == 0 or q0 == 1):
		eRight = 0
	else:
		eRight = -1*(q0 * np.log2(q0) + q1 * np.log2(q1))
	return (totalLeft * eLeft + totalRight*eRight) / (totalLeft + totalRight)

def histogram(data,labels,threshold,feature):
	left = np.zeros(2)
	right = np.zeros(2)
	col = data[:,feature]
	greater = np.where(col>=threshold,True,False)
	right[1] = np.sum(labels[greater])
	right[0] = labels[greater].shape[0] - right[1]
	left[1] = np.sum(labels[np.invert(greater)])
	left[0] = labels[np.invert(greater)].shape[0] - left[1]
	return left,right 


def segmenter(data, labels,skip_set): 
	lowest_impurity = float(2e9)
	best_feature = None
	best_threshold = None
	for i in range(data.shape[1]):
		if(i in skip_set):

			continue
		for j in set(data[:,i]):
			threshold = j
			left,right = histogram(data,labels,threshold,i)
			imp = impurity(left,right)
		
			if(imp<= lowest_impurity):
				
				lowest_impurity = imp
				best_threshold = threshold
				best_feature = i 

		# print(max(data[:,i]))
		# print(i)
	return lowest_impurity,(best_feature,best_threshold)
class Forest:
	def __init__(self,data,labels,num_trees = 50):
		self.num_trees = num_trees
		self.data = data
		self.labels = labels
		self.treeList = [None]*num_trees
	def train(self,data,labels):
		for i in range(self.num_trees):
	
			perm = np.random.choice(data.shape[1],.12*data.shape[1],replace = False)
			# print(np.sort(perm))
			# print(perm.shape)
			skipset = set(np.sort(perm))
			new_data = data
			newTree = DecisionTree(new_data,labels)
			newTree.train(new_data,labels,skipset)
			print(i)
			self.treeList[i] = newTree
	def predict(self,data):
		predict_mat = np.ones((data.shape[0],self.num_trees))
		for i in range(self.num_trees):
			curr_tree = self.treeList[i]
			c=curr_tree.predict(data)
			
			predict_mat[:,i] = c
		results = np.mean(predict_mat,axis = 1)
		return np.where(results>=.5,1,0)
	def unique_roots(self):
		new_dict = {}
		for tree in self.treeList:
			if(tree.get_root().split in new_dict.keys()):
				new_dict[tree.get_root().split]+=1
			else:
				new_dict[tree.get_root().split] = 1
		return new_dict 

class Node:
	def __init__(self,split = None,label = None):
		self.split = split
		self.label = label 
		self.left = None
		self.right = None
	def set_left(self,node):
		self.left = node
	def set_right(self,node):
		self.right = node
	def get_left(self):
		return self.left
	def get_right(self):
		return self.right
class DecisionTree:
	def __init__(self,data,labels):
		self.root = None
		self.data = data
		self.labels = labels
		self.follow = None
	def get_root(self):
		return self.root
	def train(self,data,labels,skipset = None):
		if (skipset is None):
			skipset = set()
		self.root = self.train_helper(data,labels,0,.1,skipset)
	def train_helper(self,data, labels,curr_depth,impurity,skip_set):
		if (data.shape[0] < 1 or data.shape is None):
			return None
		num_zeros = len(np.where(labels == 0)[0])
		num_ones = len(np.where(labels > 0)[0])
		if(num_zeros == 0):
			
			curr_node = Node(label = 1)
			return curr_node
		if(num_ones == 0):
			
			curr_node = Node(label = 0)
			return curr_node
		if(data.shape[0] <=4 or data.shape[1] <=3 or curr_depth>MAX_DEPTH):
			
			curr_node = Node(label = stats.mode(labels)[0].flatten()[0])
			return curr_node
		imp,split = segmenter(data,labels,skip_set)
	
		if(imp < impurity or split[0] is None):
			
			curr_node = Node(label = stats.mode(labels)[0].flatten()[0])
			return curr_node
		curr_node = Node(split = split)
		# mask = np.ones(data.shape[1], dtype=bool)
		# mask[split[0]] = False
		col = data[:,split[0]]
		skip_set.add(split[0])
		
		#data = data[:,mask]
		right = np.where(col>=split[1])
		left = np.where(col<split[1])
		right_labels = labels[right]
		left_labels = labels[left]
		right_data =data[right]
		left_data = data[left]
		curr_node.set_left(self.train_helper(left_data,left_labels,curr_depth+1,impurity,skip_set.copy()))
		curr_node.set_right(self.train_helper(right_data,right_labels,curr_depth+1,impurity,skip_set.copy()))
		return curr_node


	def predict(self,data,fall = 0):
		predictions = []
		follow = []

		for i in range(data.shape[0]):
			point = data[i]
			curr_node = self.root
			
			while curr_node.label == None:
				if(point[curr_node.split[0]] >= curr_node.split[1]):
					if(fall==1):
						follow.append(curr_node.split)
					curr_node = curr_node.right

					
				else:
					if(fall==1):
						follow.append(curr_node.split)
					curr_node = curr_node.left 


			predictions.append(curr_node.label)
		print(follow)
		return np.array(predictions)


if __name__ == "__main__":
	data = load_dataset()
	X_train = data['training_data']
	y_train = data['training_labels'].T
	X_test = data['test_data']
	# # left,right = histogram(X_train,y_train.T,1,1)
	# # i,j=segmenter(X_train,y_train.T)
	# print(X_train.shape)
	perm = np.random.permutation(X_train.shape[0])
	X_train = X_train[perm]
	y_train = y_train[perm]
	X_valid = X_train[4000:,:]
	X_train = X_train[:4000,:]
	y_valid = y_train[4000:]
	y_train = y_train[:4000]
	# X_train = np.array([[10,8],[9,10],[8,1],[1,2],[3,4],[4,5]])
	# y_train = np.array([0,0,0,0,1,1])
	# tree = DecisionTree(X_train,y_train)
	# tree.train(X_train,y_train)
	# print(X_train.shape)
	# pred_labels_train = tree.predict(X_train) 
	# print(np.reshape(X_valid[1,:],(-1,38)))
	# pred_labels_valid = tree.predict(X_valid)
	# pred_labels_test = tree.predict(X_test)
	forest = Forest(X_train,y_train,num_trees=20)
	forest.train(X_train,y_train)
	pred_labels_train = forest.predict(X_train)
	pred_labels_valid = forest.predict(X_valid)
	pred_labels_test = forest.predict(X_test)
	print("Train accuracy: {0}".format(metrics.accuracy_score(y_train, pred_labels_train)))
	print("Valid accuracy: {0}".format(metrics.accuracy_score(y_valid, pred_labels_valid)))
	np.savetxt('new2.csv',np.dstack((np.arange(1,pred_labels_test.size+1),pred_labels_test.T))[0],"%d,%d",header="Id,Category")