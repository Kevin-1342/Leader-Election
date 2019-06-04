package hs;

import network.Network;

public class HSNodeAlgorithm {
	private final int myID;
	private boolean status;
	private int phase;
	private Network network;
	
	private int inID;
	private int inHopCount;
	private int round = 1;

	private final int IN = 0;
	private final int OUT = 1;
	
	private final boolean UNKNOWN_LEADER_STATE = false;//non-leader
	private final boolean LEADER_STATE = true;
	private final int CLOCKWISE_DIRECTION= 0;
	private final int COUNTERCLOCKWISE_DIRECTION = 1;
	
	//terminate and know leader
	private int leaderID;
	private boolean terminated;
	private int mesCounter = 0;
	
	//display round process
	private boolean Display_Each_Round_Process = false;
	
	public HSNodeAlgorithm(int ID, Network net) {
		this.myID = ID;
		this.phase = 0;
		this.network = net;
	}
	
	public void mainAlgorithm() {
		//test output
		if(this.Display_Each_Round_Process) {
		System.out.println("---------"+this.myID+"---------");
		}
		HSNodeMessage counterClockMsg,clockMsg = null;
		HSNodeMessage sendClock, sendCounterClock = null;
		if(round==1) {
			int newPhase = (int)Math.pow(2, this.phase);
			sendClock = new HSNodeMessage(this.myID, OUT, newPhase, UNKNOWN_LEADER_STATE);
			sendCounterClock = new HSNodeMessage(this.myID, OUT, newPhase, UNKNOWN_LEADER_STATE);
			this.network.outMessage(myID, sendClock,CLOCKWISE_DIRECTION);
			this.network.outMessage(myID, sendCounterClock,COUNTERCLOCKWISE_DIRECTION);
		}
		round++;
		
		//From Counter to clock
		counterClockMsg = this.network.receiveMessage(myID,OUT, COUNTERCLOCKWISE_DIRECTION);
		if(counterClockMsg != null) {
			messageCounter();
			inID = counterClockMsg.getSendID();
			//test output
			if(this.Display_Each_Round_Process) {
			System.out.println("OUT, counter neighbour inID: "+this.inID);
			}
			inHopCount = counterClockMsg.getHopCount();
			//Boardcast leader ID
			if(counterClockMsg.getLeaderState()==LEADER_STATE) {
				if(this.terminated==false) {
					leaderID = inID;
					terminated = true;
					sendClock = new HSNodeMessage(inID, OUT, 1, LEADER_STATE);
					this.network.outMessage(myID, sendClock, CLOCKWISE_DIRECTION);
				}else {//terminted
					
				}

			}else {
				
				if(inID>myID && inHopCount>1) {
					sendClock = new HSNodeMessage(inID, OUT, inHopCount-1, UNKNOWN_LEADER_STATE);
					this.network.outMessage(myID, sendClock, CLOCKWISE_DIRECTION);
				}else if(inID>myID && inHopCount==1) {
					sendCounterClock = new HSNodeMessage(inID, IN, 1, UNKNOWN_LEADER_STATE);
					this.network.outMessage(myID, sendCounterClock,COUNTERCLOCKWISE_DIRECTION);
				}else if(inID == myID) {
					this.status = LEADER_STATE;
					this.terminated = true;
					this.leaderID = inID;
					sendClock = new HSNodeMessage(inID, OUT, this.phase, this.status);
					sendCounterClock = new HSNodeMessage(inID, OUT, this.phase, this.status);
					this.network.outMessage(myID, sendClock,CLOCKWISE_DIRECTION);
					this.network.outMessage(myID, sendCounterClock,COUNTERCLOCKWISE_DIRECTION);
				}
			}
			

		}	
		
		//Clock
		clockMsg = this.network.receiveMessage(myID, OUT,CLOCKWISE_DIRECTION);
		if(clockMsg != null) {
			messageCounter();
			inID = clockMsg.getSendID();
			//test output
			if(this.Display_Each_Round_Process) {
				System.out.println("OUT, clock neighbour inID: "+this.inID);
			}
			
			inHopCount = clockMsg.getHopCount();
			if(clockMsg.getLeaderState()==LEADER_STATE) {
				if(this.terminated==false) {
					leaderID = inID;
					terminated = true;
					sendCounterClock = new HSNodeMessage(inID, OUT, 1, LEADER_STATE);
					this.network.outMessage(myID, sendCounterClock, COUNTERCLOCKWISE_DIRECTION);
				}else {//terminted
					
				}

			}else {
				if(inID>myID && inHopCount>1) {
					sendCounterClock = new HSNodeMessage(inID, OUT, inHopCount-1, UNKNOWN_LEADER_STATE);
					this.network.outMessage(myID, sendCounterClock, COUNTERCLOCKWISE_DIRECTION);
				}else if(inID>myID && inHopCount==1) {
					sendClock = new HSNodeMessage(inID, IN, 1, UNKNOWN_LEADER_STATE);
					this.network.outMessage(myID, sendClock,CLOCKWISE_DIRECTION);
				}else if(inID == myID) {
					if(this.status == LEADER_STATE) {
						
					}else {
						//
						this.status = LEADER_STATE;
						this.terminated = true;
						this.leaderID = inID;
						sendClock = new HSNodeMessage(inID, OUT, this.phase, this.status);
						sendCounterClock = new HSNodeMessage(inID, OUT, this.phase, this.status);
						this.network.outMessage(myID, sendClock,CLOCKWISE_DIRECTION);
						this.network.outMessage(myID, sendCounterClock,COUNTERCLOCKWISE_DIRECTION);
						
					}
					
				}
			}
			
		}
		
		//counter
		counterClockMsg = this.network.receiveMessage(myID,IN,1, COUNTERCLOCKWISE_DIRECTION);
		if(counterClockMsg!=null) {
			messageCounter();
			inID = counterClockMsg.getSendID();
			//test output
			if(this.Display_Each_Round_Process) {
				System.out.println("IN, counter neighbour inID: "+this.inID);
			}
			
			
			if(inID != this.myID) {
				sendClock = new HSNodeMessage(inID, IN, 1, UNKNOWN_LEADER_STATE);
				this.network.outMessage(myID, sendClock,CLOCKWISE_DIRECTION);
			}
		}
		
		//clock
		clockMsg = this.network.receiveMessage(myID,IN,1, CLOCKWISE_DIRECTION);
		if(clockMsg!=null) {
			messageCounter();
			inID = clockMsg.getSendID();
			//test output
			if(this.Display_Each_Round_Process) {
				System.out.println("IN, clock neighbour inID: "+this.inID);
			}	
			
			if(inID != this.myID) {
				sendCounterClock = new HSNodeMessage(inID, IN, 1, UNKNOWN_LEADER_STATE);
				this.network.outMessage(myID,sendCounterClock, COUNTERCLOCKWISE_DIRECTION);
			}
		}
		//Both
		if(counterClockMsg!=null && clockMsg!=null) {
			if(counterClockMsg.getSendID()==myID && clockMsg.getSendID()==myID) {
				this.phase++;
				int newPhase = (int)Math.pow(2, this.phase);
				sendClock = new HSNodeMessage(this.myID, OUT, newPhase, UNKNOWN_LEADER_STATE);
				sendCounterClock = new HSNodeMessage(this.myID, OUT, newPhase, UNKNOWN_LEADER_STATE);
				this.network.outMessage(myID, sendClock,CLOCKWISE_DIRECTION);
				this.network.outMessage(myID, sendCounterClock,COUNTERCLOCKWISE_DIRECTION);
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
		//System.out.println("---------ID: "+this.myID+"---------");
		//System.out.println("in ID: "+this.inID);
		System.out.println("This node is leader: "+this.status);
		System.out.println("leaderID: "+this.leaderID);
		System.out.println("Terminated: "+this.terminated);
		System.out.println("------------------");
	}
}
