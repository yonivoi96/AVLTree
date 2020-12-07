import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;

/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {

	private IAVLNode root;
	private int size;
	private AVLNode VNode= new AVLNode();
	public  AVLTree(){
		root=VNode;
		size=0;
	}
	/** AVL Tree constructor that receives a node and sets the node as the root of the tree */
	public  AVLTree(IAVLNode node){
		size=node.getSize();
		root=node;
		root.setParent(null);
	}
    /**AVL Tree constructor that creates the root node from the details that are given*/
	public  AVLTree(int x, String s1){
		this.size=1;
		this.root=new AVLNode(x,s1, null);
	}

  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */

  public boolean empty() {
	  if (root.getSize() == 0)
		  return true;
	  return false;
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */

  public String search(int k)
  {
  	IAVLNode tmp=TreePosition(root,k);
	if (k==tmp.getKey())
		return tmp.getValue();
	else
		return null;
  }

	/** returns 0 is node doesnt have parent, 1 if left child, 2 if right child */
	private int whatChild(IAVLNode node){
		if(node.getParent()==null) return 0;
		if(node.getParent().getLeft()==node) return 1;
		return 2;
	}

	/** Finds and returns the position of a given key. if the key doesnt appear in the tree, returns the leaf
	 * that should be his parent */
	private IAVLNode TreePosition(IAVLNode node,int key){
		IAVLNode temp =node;
		while(node!=VNode){
			temp=node;
			if (key==node.getKey())
				return node;
			else if (key < node.getKey())
				node=node.getLeft();
			else
				node=node.getRight();
		}
		return temp;
	}


  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
   	if (root.getSize()==0) {
   		root=new AVLNode(k,i,null);
   		return 0;
	}
	   IAVLNode node=TreePosition(root,k);
   	if (node.getKey()==k) return -1;
   	IAVLNode ins=new AVLNode(k,i,node);
   	if (node.getKey()>k ){
   		node.setLeft(ins);
	}
	else{
		node.setRight(ins);
	   }

	/** re balancing and promotion */
	return reBalanceIns(node);
   }

   /** Does all the rebalancing operations that should be done after insertion into the tree for all case
	* Also handles rebalancing the Tree after joining two trees*/
   private int reBalanceIns(IAVLNode node){
   	int cnt=0;
   	int leftrank,leftrank1;
   	int  rightrank,rightrank1;
	   while(node!=null){

	   	 leftrank=node.getRank()-node.getLeft().getRank();
	  	 rightrank=node.getRank()-node.getRight().getRank();
	  	 if (rightrank==1 && leftrank==1) {
			 sizeheightFix(node);
			 return cnt;
		 }
	  	 if ((rightrank==0 && leftrank==1)|| (rightrank==1 && leftrank==0)){
	  	 	promote(node);
	  	 	node.setHeight(-1);
	  	 	node.setSize();
	  	 	cnt++;
	  	 	node=node.getParent();
	  	 	continue;
	   }
	  	 if(rightrank==0 && leftrank==2){
			 rightrank1=node.getRight().getRank()-node.getRight().getRight().getRank();
			 leftrank1=node.getRight().getRank()-node.getRight().getLeft().getRank();
			 if (leftrank1==2 && rightrank1==1){
				 naiveleftRotate(node);
				 sizeheightFix(node);
				 demote(node);
				 return cnt+2;
			 }
			 else if (leftrank1==1 && rightrank1==2){
				 demote(node);
				 demote(node.getRight());
				 promote(node.getRight().getLeft());
				 naiverightRotate(node.getRight());
				 naiveleftRotate(node);
				 node.getParent().getRight().setSize();
				 node.getParent().getRight().setHeight(-1);
				sizeheightFix(node);
				 return cnt+=5;
			 }
			 else{
				 promote(node.getRight());
				 naiveleftRotate(node);
				 sizeheightFix(node);
				 return cnt+2;
			 }
		 }
	  	 if(rightrank==2 && leftrank==0){
	  	 	leftrank1=node.getLeft().getRank()-node.getLeft().getLeft().getRank();
	  	 	rightrank1=node.getLeft().getRank()-node.getLeft().getRight().getRank();
	  	 	if (leftrank1==1 && rightrank1==2){
	  	 		naiverightRotate(node);
	  	 		sizeheightFix(node);
	  	 		demote(node);
	  	 		return cnt+2;
	  	 	}
	  	 	else if (leftrank1==2 && rightrank1==1){
	  	 		demote(node);
	  	 		demote(node.getLeft());
	  	 		promote(node.getLeft().getRight());
	  	 		naiveleftRotate(node.getLeft());
	  	 		naiverightRotate(node);
	  	 		node.getParent().getLeft().setSize();
				node.getParent().getLeft().setHeight(-1);
				sizeheightFix(node);
	  	 		return cnt+=5;
			}
	  	 	else{
					promote(node.getLeft());
					naiverightRotate(node);
				    sizeheightFix(node);
				    return cnt+2;
			 }

		 }

   }
	   this.size=root.getSize();
	   return cnt;
   }

  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k was not found in the tree.
   */
	public int delete(int k){
		IAVLNode node=TreePosition(root,k);
		return deleteNode(node);

	}

	/** handles all the different cases when deleting a node from the tree */
   public int deleteNode(IAVLNode node) {
		int child;
	   if(node.getKey()==-1) return -1;
	   /** if is a leave */
	   if  (node.getLeft()==VNode && node.getRight()==VNode){
			child=whatChild(node);
			if(child==0)
			{
				this.root=VNode;
				return 0;
			}

			else if(child==1)
			{
				node.getParent().setLeft(VNode);
			}
			else
			{
				node.getParent().setRight(VNode);
			}
			/** make re-Balancing */
			return reBalanceDel(node.getParent());
	   }
	   /** if has 1 child */
	   else if((node.getLeft()==VNode && node.getRight()!=VNode) || (node.getRight()==VNode && node.getLeft()!=VNode)){
			child=whatChild(node);
			if (child==0){
				if (node.getLeft()==VNode){
					root=node.getRight();
					node.getRight().setParent(null);
				}
				else {
					root = node.getLeft();
					node.getLeft().setParent(null);
				}
				return 0;
			}
			else if( child==1){
				if (node.getLeft()==VNode){
					node.getParent().setLeft(node.getRight());
					node.getRight().setParent(node.getParent());

				}
				else {
					node.getParent().setLeft(node.getLeft());
					node.getLeft().setParent(node.getParent());
				}

			}
			else {
				if (node.getLeft() == VNode) {
					node.getParent().setRight(node.getRight());
					node.getRight().setParent(node.getParent());

				} else {
					node.getParent().setRight(node.getLeft());
					node.getLeft().setParent(node.getParent());
				}
			}
			/** re-Balancing */
			return reBalanceDel(node.getParent());
		   }
	   /** if has two childs */
	   else {
		   IAVLNode suc = getSuccessor(node);
		   int cnt=deleteNode(suc);
		   node.getRight().setParent(suc);
		   node.getLeft().setParent(suc);
		   suc.setLeft(node.getLeft());
		   suc.setRight(node.getRight());
		   child = whatChild(node);
		   if (child == 0) {
			   suc.setParent(null);
			   root = suc;
		   } else if (child == 1) {
			   node.getParent().setLeft(suc);
		   } else if (child == 2) {
			   node.getParent().setRight(suc);
		   }
		   suc.setParent(node.getParent());
		   suc.setHeight(node.getHeight());
		   suc.setSize();
		   suc.setRank(node.getRank());
		   return cnt;

	   }
	}
	/** Does all the rebalancing operations that should be done after deleting a node from the tree for all case */
	private int reBalanceDel(IAVLNode node){
		int leftrank,rightrank;
		int cnt=0;
		while (node!=null){
			leftrank=node.getRank()-node.getLeft().getRank();
			rightrank=node.getRank()-node.getRight().getRank();
			if ((leftrank==2 && rightrank==1 )|| (leftrank==1 && rightrank==2)) {
				sizeheightFix(node);
				return cnt;
			}
			if(leftrank==rightrank){
				if(leftrank==2){
					demote(node);
					cnt++;
					node.setHeight(-1);
				}
				node.setSize();
				node=node.getParent();
				continue;
			}
			if (leftrank==3 && rightrank==1){
				cnt+=leftRotate(node);
				node=node.getParent().getParent();
				continue;
			}
			if (leftrank==1 && rightrank==3){
				cnt+=rightRotate(node);
				node=node.getParent().getParent();
			}
			else break;
		}
		return cnt;
   }
   /** Demote a node's rank by one*/
   private void demote(IAVLNode node){
		node.setRank(node.getRank()-1);
   }
   /** Promote a node's rank by one*/
   private void promote(IAVLNode node){
	   node.setRank(node.getRank()+1);
   }

   /** Handles the changes of the parents and children pointers of each node that's involved in a single left rotation*/
   private void naiveleftRotate(IAVLNode node){
	   IAVLNode newroot1=node.getRight();
	   node.setRight(newroot1.getLeft());
	   newroot1.getLeft().setParent(node);
	   newroot1.setLeft(node);
	   int child=whatChild(node);
	   if (child==1) {
	   	node.getParent().setLeft(newroot1);
	   	newroot1.setParent(node.getParent());
	   	node.setParent(newroot1);
	   }

	   else if (child==2) {
	   	node.getParent().setRight(newroot1);
	   	newroot1.setParent(node.getParent());
	   	node.setParent(newroot1);
	   }
	   else {
		   root = newroot1;
		   node.setParent(newroot1);
		   newroot1.setParent(null);
	   }
   }
	/** Handles the changes of the parents and children pointers of each node that's involved in a single right rotation*/
   private void naiverightRotate(IAVLNode node){
	   IAVLNode newroot1=node.getLeft();
	   node.setLeft(newroot1.getRight());
	   newroot1.getRight().setParent(node);
	   newroot1.setRight(node);
	   int child=whatChild(node);
	   if (child==1){
	   	node.getParent().setLeft(newroot1);
	   	newroot1.setParent(node.getParent());
	   	node.setParent(newroot1);
	   }

	   else if (child==2) {
	   	node.getParent().setRight(newroot1);
	   	newroot1.setParent(node.getParent());
	   	node.setParent(newroot1);
	   }
	   else{
	   		this.root=newroot1;
		   node.setParent(newroot1);
		   newroot1.setParent(null);
	   }
   }
	/** Does the operations that needs to be done when rotating nodes towards left, including rebalncing operations such as
	 * promoting and demoting nodes' ranks, and updating the height and size of each node */
   private int leftRotate(IAVLNode node){
		int leftrank=node.getRight().getRank()-node.getRight().getLeft().getRank();
	    int rightrank=node.getRight().getRank()-node.getRight().getRight().getRank();
	   if (leftrank==1 && rightrank==2){
			naiverightRotate(node.getRight());
	   }
		IAVLNode newroot=node.getRight();
		node.setRight(newroot.getLeft());
		node.getRight().setParent(node);
		newroot.setLeft(node);
		int child=whatChild(node);
		if(child==0){
			root=newroot;
			newroot.setParent(null);
		}
		else if (child==1) {
			node.getParent().setLeft(newroot);
			newroot.setParent(node.getParent());
		}
		else {
			node.getParent().setRight(newroot);
			newroot.setParent(node.getParent());
		}
	   node.setParent(newroot);
		if (leftrank==1 && rightrank==1){
			demote(node);
			promote(newroot);
			node.setHeight(-1);
			node.setSize();
			node.getParent().setHeight(-1);
			node.getParent().setSize();
			return 3;
		}
		if (leftrank==2 && rightrank==1){
			demote(node);
			demote(node);
			node.setHeight(-1);
			node.setSize();
			node.getParent().setHeight(-1);
			node.getParent().setSize();
			return 3;
		}
		if (leftrank==1 && rightrank==2) {
			demote(node);
			demote(node);
			promote(newroot);
			demote(newroot.getRight());
			node.setHeight(-1);
			node.setSize();
			node.getParent().getRight().setHeight(-1);
			node.getParent().getRight().setSize();
			node.getParent().setHeight(-1);
			node.getParent().setSize();
			return 5;
		}
	return 0;
   }

	/** Does the operations that needs to be done when rotating nodes towards left, including rebalncing operations such as
	 * promoting and demoting nodes' ranks, and updating the height and size of each node */
   private int rightRotate(IAVLNode node){
	   int rightrank=node.getLeft().getRank()-node.getLeft().getRight().getRank();
	   int leftrank=node.getLeft().getRank()-node.getLeft().getLeft().getRank();
	   if (leftrank==2 && rightrank==1){
		   naiveleftRotate(node.getLeft());
	   }
	   IAVLNode newroot=node.getLeft();
	   node.setLeft(newroot.getRight());
	   node.getLeft().setParent(node);
	   newroot.setRight(node);
	   int child=whatChild(node);
	   if(child==0) {
		   root = newroot;
		   newroot.setParent(null);
	   }
	   else if (child==1) {
	   	node.getParent().setLeft(newroot);
	   	newroot.setParent(node.getParent());
	   }
	   else{
	   	node.getParent().setRight(newroot);
		   newroot.setParent(node.getParent());
	   }
	   node.setParent(newroot);
	   if (leftrank==1 && rightrank==1){
		   demote(node);
		   promote(newroot);
		   node.setHeight(-1);
		   node.setSize();
		   node.getParent().setHeight(-1);
		   node.getParent().setSize();
		   return 3;
	   }
	   if (leftrank==1 && rightrank==2){
		   demote(node);
		   demote(node);
		   node.setHeight(-1);
		   node.setSize();
		   node.getParent().setHeight(-1);
		   node.getParent().setSize();
		   return 3;
	   }
	   if (leftrank==2 && rightrank==1) {
		   demote(node);
		   demote(node);
		   promote(newroot);
		   demote(newroot.getLeft());
		   node.setHeight(-1);
		   node.setSize();
		   node.getParent().getLeft().setHeight(-1);
		   node.getParent().getLeft().setSize();
		   node.getParent().setHeight(-1);
		   node.getParent().setSize();
		   return 5;
	   }
	   return 0;
   }
	/** return the successor for node of key k
	 * return virtual-node if node is maximum */
	private IAVLNode getSuccessor(IAVLNode node){
   	int child;
   	if (node.getRight()!=VNode){
   		node=node.getRight();
   		while(node.getLeft()!=VNode){
   			node=node.getLeft();
		}
   		return node;
	}
   	else {
   		child=whatChild(node);
   		while(child==2){
   			node=node.getParent();
			child=whatChild(node);
		}
   		if (child==0) return VNode;
   		else return node.getParent();
	}
	}

	/** Updates the height and size of a node*/
   private void sizeheightFix(IAVLNode node){
		while(node!=null){
			node.setSize();
			node.setHeight(-1);
			node=node.getParent();
		}
   }
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */

   //Working
   public String min()
   {
	   if (empty())
		   return null;
	   else {
		   IAVLNode x = root;
		   while (x.getLeft().getKey() != -1)
			   x = x.getLeft();
		   return x.getValue();
	   }
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */

   //Working
   public String max()
   {
	   if (empty())
		   return null;
	   else {
		   IAVLNode x = root;
		   while (x.getRight().getKey() != -1)
			   x = x.getRight();
		   return x.getValue();
	   }
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */

  public int[] keysToArray()
  {
	  int[] arr = new int[size()];
	  ArrayList<IAVLNode> lst=new ArrayList<>();
	  lst=InOrder(lst,root);
	  for (int i=0; i<lst.size(); i++)
		  arr[i]=lst.get(i).getKey();
	  return arr;
  }

  /** Returns a list of all the tree nodes sorted in-order */
  public static ArrayList<IAVLNode> InOrder(ArrayList<IAVLNode> lst, IAVLNode root){
		if (root.getKey()==-1)
			return lst;
		InOrder(lst,root.getLeft());
		lst.add(root);
		InOrder(lst, root.getRight());
		return lst;
		}

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */

  public String[] infoToArray()
  {
	  String[] arr = new String[size()];
	  ArrayList<IAVLNode> lst=new ArrayList<>();
	  lst=InOrder(lst,root);
	  for (int i=0; i<arr.length; i++)
		  arr[i]=lst.get(i).getValue();
	  return arr;
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
   	 size=root.getSize();
   	 return size;
   	//return size;
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    */
   public IAVLNode getRoot()
   {
	   return root;
   }
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	  * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   public AVLTree[] split(int x)
   {
	   IAVLNode node= TreePosition(root,x);
	   AVLTree T1=new AVLTree(node.getLeft());
	   AVLTree T2=new AVLTree(node.getRight());
	   AVLTree[] arr=new AVLTree[]{T1,T2};
	   while (node!=root) {
		   /**
			* if node is a right child
			*/
		   if (whatChild(node) == 2) {
			   node = node.getParent();
			   AVLTree Tree1 = new AVLTree(node.getLeft());
			   IAVLNode tmp=new AVLNode(node.getKey(),node.getValue(),null);
			   T1.join(tmp, Tree1);
		   }
		   /**
			* if node is a left child
			*/
		   else if (whatChild(node) == 1 ) {
			   node = node.getParent();
			   AVLTree Tree1 = new AVLTree(node.getRight());
			   IAVLNode tmp=new AVLNode(node.getKey(),node.getValue(),null);
			   T2.join(tmp, Tree1);
		   }
	   }
	   return arr;
   }

   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	  * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */
   public int join(IAVLNode x, AVLTree t) {
	   int comp = Math.abs(root.getRank() - t.root.getRank()) + 1;
	   boolean bigkey = x.getKey() < root.getKey();
	   if (root.getRank() == t.root.getRank()) {
		   if (bigkey) {
			   x.setLeft(t.root);
			   x.setRight(root);

		   }
		   else{
			   x.setLeft(root);
			   x.setRight(t.root);
		   }
		   root.setParent(x);
		   t.root.setParent(x);
		   root = x;
	   }
	   else{
	   boolean bigrank = t.root.getRank() < root.getRank();
	   boolean flag = !((bigkey && !bigrank) || (!bigkey && bigrank));
	   if (!bigrank) { // to change
		   IAVLNode tmp = root;
		   root = t.root;
		   t.root = tmp;
	   }
	   IAVLNode node = root;
	   while (node.getRank() > t.root.getRank()) {
		   if (flag) {
			   node = node.getLeft();
		   } else {
			   node = node.getRight();
		   }
	   }
	   IAVLNode newpar = node.getParent();
	   x.setRank(t.root.getRank() + 1);
	   if (flag) {
		   newpar.setLeft(x);
		   x.setLeft(t.root);
		   x.setRight(node);
	   } else {
		   newpar.setRight(x);
		   x.setRight(t.root);
		   x.setLeft(node);
	   }
	   t.root.setParent(x);
	   node.setParent(x);
	   x.setParent(newpar);
   }
	   x.setSize();
	   x.setHeight(-1);
	   reBalanceIns(x.getParent());

	   return comp;
   }
	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public int getSize();// returns size of sub tree whose root is node
		public void setSize();//sets size
		public void setRank(int rank);// sets rank
		public int getRank();// return rank of node
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
	}
	

   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   */
  public class AVLNode implements IAVLNode{

  	private final int key;
  	private final String value;
  	private IAVLNode parent;
  	private IAVLNode left;
  	private IAVLNode right;
  	private int rank;
  	private int height;
  	private int size;


  		public AVLNode(){
  			key=-1;
  			value=null;
  			rank=-1;
  			height=-1;
		}
		public AVLNode(int key, String value,IAVLNode parent){
  			this.parent=parent;
  			this.key=key;
  			this.value=value;
			left=VNode;
			right=VNode;
			rank=0;
			height=0;
			size=1;
		}

		public int getSize(){
  			return size;
		}

		public void setSize(){
  			size=left.getSize()+right.getSize()+1;
		}

		public int getKey()
		{
			return key;
		}

		public String getValue()
		{
			return value;
		}

		public void setLeft(IAVLNode node)
		{
			left=node;
		}

		public IAVLNode getLeft()
		{
			return left;
		}

		public void setRight(IAVLNode node)
		{
			right=node;
		}

		public IAVLNode getRight()
		{
			return right;
		}

		public void setParent(IAVLNode node)
		{
			parent=node;
		}

		public IAVLNode getParent()
		{
			return parent;
		}

		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode()
		{
			return  key!=-1;
		}

		public void setHeight(int height)
    	{
    		if (height<0) {
				this.height = Math.max(left.getHeight(), right.getHeight()) + 1;
			}
    		else this.height=height;
    	}

    	public int getHeight()
    {
      return height;
    }

	    public void setRank(int rank)
	   {
		   this.rank=rank;
	   }

	    public int getRank()
	   {
		   return rank;
	   }


  }

}
  

