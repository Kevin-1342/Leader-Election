package lcr;

public class LCRNodeMessage extends Message {
	private int sendID;
	private boolean leader;
	

	public LCRNodeMessage(int send, boolean learderState) {
		super();
		this.sendID = send;
		this.leader = learderState;
	}

	public Integer getSendID() {
		
		return this.sendID;
	}
	
	public boolean getLeaderState() {
		
		return this.leader;
	}
}
