

import java.io.Serializable;

import cs475.ClassificationLabel;

public class Tree implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int key;
	private double value;
	private ClassificationLabel label;
	private Tree left;
	private Tree right;

	public Tree(ClassificationLabel l) {
		this.key = -1;
		this.value = -1000;
		this.label = l;
		this.left = null;
		this.right = null;
	}

	public Tree() {
		this.key = -1;
		this.value = -1000;
		this.label = null;
		// Attention!
		this.left = null;
		this.right = null;
	}

	public Tree getLeft() {
		return left;
	}

	public void setLeft(Tree left) {
		this.left = left;
	}

	public Tree getRight() {
		return right;
	}

	public void setRight(Tree right) {
		this.right = right;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public ClassificationLabel getLabel() {
		return label;
	}

	public void setLabel(ClassificationLabel label) {
		this.label = label;
	}

}