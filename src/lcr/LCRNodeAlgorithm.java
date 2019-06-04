package lcr;

import network.Network;

public class LCRNodeAlgorithm {
	private final int myID;
	private int sendID;
	private boolean status;
	
	private int inID;
	private int round = 1;
	private Network network;
	
	private final boolean UNKNOWNLEADER_STATE = false;//non-leader
	private final boolean LEADER_STATE = true;
	
	//terminate and know leader
	private int leaderID;
	private boolean terminated;
	
	private int mesCounter = 0;
	
	//display round process
	private boolean Display_Each_Round_Process = false;
	
	public LCRNodeAlgorithm(int ID, Network net) {
		this.myID = ID;
		this.sendID = this.myID;
		this.status = UNKNOWNLEADER_STATE;
		this.network = net;
		this.terminated = false;
	}
	
	public void mainAlgorithm() {
		if(round == 1) {//Start
			round++;
			//send
			this.network.outMessage(myID,new LCRNodeMessage(sendID,this.status));
		}else {
			
			//receiving
			LCRNodeMessage inMessage = this.network.receiveMessage(myID);
			round++;
			if(inMessage!=null) {
				//elected
				messageCounter();
				if(inMessage.getLeaderState()) {//round (n+1) to 2n
					leaderID = inMessage.getSendID();
					this.terminated = true;
					if(leaderID != myID) {
						this.network.outMessage(myID, inMessage);
					}else {//all terminating
						//finish
					}
				}else {//round 2 to n
					inID = inMessage.getSendID();
					
					if(inID>myID) {
						sendID = inID;
						//send
						this.network.outMessage(myID,new LCRNodeMessage(this.sendID,this.status));
					}else if(inID == myID) {
						this.status = LEADER_STATE;
						this.sendID = inID;
						this.terminated = true;
						this.leaderID = inID;
						//announce
						this.network.outMessage(myID, new LCRNodeMessage(this.sendID,this.status));
					}else if(inID<myID) {
						//Nothing
					}
				}
			}else {
				inID = 0;
			}
			
			
		}
		
		
		if(this.Display_Each_Round_Process) {
			testOutput();
		}
		
	}
	
	
	
	//Message compl
	private void messageCounter() {
		mesCounter++;
	}
	public int getMesCounter() {
		return this.mesCounter;
	}
	public boolean getTerminated() {
		return this.terminated;
	}
	public int getLeaderID() {
		return this.leaderID;
	}
	public void setDisplayRound(boolean state) {
		this.Display_Each_Round_Process = state;
	}
	private void testOutput() {
		System.out.println("---------ID: "+this.myID+"---------");
		System.out.println("in ID: "+this.inID);
		System.out.println("This node is leader: "+this.status);
		System.out.println("leaderID: "+this.leaderID);
		System.out.println("Terminated: "+this.terminated);
	}
}
