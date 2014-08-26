

public class Pair implements Comparable<Pair> {
	final private int first;
	final private int second;
	
	public Pair(int first, int second) {
		this.first = first;
		this.second = second;
	}
	
	public int first() {
		return this.first;
	}
	
	public int second() {
		return this.second;
	}

	public int compareTo(Pair pair) {
		if (this.first < pair.first) {
			return -1;
		} else if (this.first > pair.first) {
			return 1;
		}
		return 0;
	}		
}