package hs;

import lcr.Message;

public class HSNodeMessage extends Message {
	private int sendID;
	private int hopCount;
	private int in_out;
	private boolean leader;
	private final int IN = 0;
	private final int OUT = 1;

	
	public HSNodeMessage(int send, int inOut,int hop, boolean leaderState) {
		super();
		this.sendID = send;
		this.in_out = inOut;
		this.hopCount = hop;
		this.leader = leaderState;

	}
	
	public Integer getSendID() {
		return this.sendID;
	}
	
	public Integer getInOut() {
		return this.in_out;
	}
	
	public Integer getHopCount() {
		return this.hopCount;
	}
	
	public boolean getLeaderState() {
		
		return this.leader;
	}
		
}
