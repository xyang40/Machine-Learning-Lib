

import java.io.Serializable;
import java.util.HashSet;

public class wrappedTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int key;
	private double value;
	private HashSet<Integer> left;
	private HashSet<Integer> right;

	public wrappedTree() {
		this.key = -1;
		this.value = -1000;
		this.left = null;
		this.right = null;
	}

	public wrappedTree(int k, double v, HashSet<Integer> l, HashSet<Integer> r) {
		this.key = k;
		this.value = v;
		this.left = l;
		this.right = r;
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

	public HashSet<Integer> getLeft() {
		return left;
	}

	public void setLeft(HashSet<Integer> left) {
		this.left = left;
	}

	public HashSet<Integer> getRight() {
		return right;
	}

	public void setRight(HashSet<Integer> right) {
		this.right = right;
	}

}
