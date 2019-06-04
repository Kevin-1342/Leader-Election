package main;

import java.util.HashSet;

public class RingSimulation {
	public int ringSize;
	private int[] ringID;
	private int counter = 0;
	
	private final int CLOCKINCREASE = 0;
	private final int COUNTERCLOCKINCREASE = 1;
	private final int RANDOM = 2;
	
	public RingSimulation(int size,int type) {
		this.ringSize = size;
		ringID = new int [this.ringSize];
		if(type == CLOCKINCREASE) {
			generateClockIncreaseRing();
		}else if(type == COUNTERCLOCKINCREASE) {
			generateCounterClockIncreaseRing();
		}else if(type == RANDOM) {
			HashSet<Integer> set = new HashSet<Integer>();
			generateRandomRing(this.ringSize,set);
		}
		for(int i = 0 ; i < ringID.length;i++) {
			//System.out.print(ringID[i]+"\t");
			//if(i%20==19) {
				//System.out.println("");
			//}
		}
	}
	
	private void generateRandomRing(int size, HashSet<Integer> numberSet) {
		
		if(size>this.ringSize) {
			return;
		}
		for(int i = 0; i < this.ringSize; i++) {
			int num = (int) (Math.random()*this.ringSize+1);
			if(numberSet.add(num)) {
				ringID[this.counter] = num;
				this.counter++;
			}
		}
		int setSize = numberSet.size();
		if(setSize<this.ringSize) {
			generateRandomRing(this.ringSize - setSize, numberSet);
		}
	}
	
	private void generateClockIncreaseRing() {
		for(int i = 0; i < this.ringSize; i++) {
			ringID[i] = i+1;
		}
	}
	
	private void generateCounterClockIncreaseRing() {
		for(int i = 0; i < this.ringSize; i++) {
			ringID[i] = this.ringSize-(i);
		}
	}
	
	public int[] getNetworkRing() {
		return this.ringID;
	}
}
