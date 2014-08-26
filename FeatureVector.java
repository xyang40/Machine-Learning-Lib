
import java.io.Serializable;


public class FeatureVector implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int[] keys;
	private double[] values;
	private int size;
	private int pos;
	//private ArrayList<Integer> list;

	public FeatureVector(int size) {
		this.keys = new int[size];
		this.values = new double[size];
	//	this.list = new ArrayList<Integer>();
		this.size = size;
		this.pos = 0;
	}
	
	

	public void add(int index, double value) {

		keys[getPos()] = index;
		values[getPos()] = value;

		setPos(getPos() + 1);

	}

	public double get(int index) {
		double result = -1;
		for (int i = 0; i < size; i++) {
			if (keys[i] == index) {
				result = values[i];
				break;
			}
		}
		return result;
	}

	public int[] getKeys() {
		return keys;
	}

	public void setKeys(int[] keys) {
		this.keys = keys;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public void setSpecificValuetoValues(int index, double value) {
		this.values[index] = value;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
