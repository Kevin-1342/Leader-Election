package network;

import java.util.ArrayList;
import java.util.HashMap;

import hs.HSNodeMessage;
import lcr.LCRNodeAlgorithm;
import lcr.LCRNodeMessage;
import lcr.Message;
import main.RingSimulation;

public class Network {
	private HashMap<Integer, ArrayList<LCRNodeMessage>> inBufferLCR = new HashMap<>();//myID, Message
	private HashMap<Integer, ArrayList<LCRNodeMessage>> outBufferLCR = new HashMap<>();//myID, Message
	private HashMap<Integer, ArrayList<Integer>> outDirectionLCR = new HashMap<>();//myID, Direction

	private HashMap<Integer, ArrayList<HSNodeMessage>> inBufferHS = new HashMap<>();//myID, Message
	private HashMap<Integer, ArrayList<HSNodeMessage>> outBufferHS = new HashMap<>();//myID, Message
	private HashMap<Integer, ArrayList<Integer>> outDirectionHS = new HashMap<>();//myID, Direction
	private HashMap<Integer, ArrayList<Integer>> inDirectionHS = new HashMap<>();//myID, Direction
	private int[] ringID;
	
	private final static int CLOCKWISE_DIRECTION= 0;
	private final static int COUNTERCLOCKWISE_DIRECTION = 1;
	//Ring type
	private final int CLOCKINCREASE = 0;
	private final int COUNTERCLOCKINCREASE = 1;
	private final int RANDOM = 2;
	
	//display round process
	private boolean Display_Each_Round_Process = false;
	
	public Network() {
		
	}
	
	public void createRingNetwork(int size, int type) {
		RingSimulation simulation = new RingSimulation(size,type);
		this.ringID = simulation.getNetworkRing();
		if(Display_Each_Round_Process) {
			for(int i = 0 ; i < ringID.length;i++) {
				System.out.print(ringID[i]+"\t");
				if(i%20==19) {
					System.out.println("");
				}
			}
		}
	}
	
	public boolean outMessage(int outID,LCRNodeMessage message) {
		ArrayList<LCRNodeMessage> nodeMessages;
		ArrayList<Integer> nodeDirection;
		if(outBufferLCR.containsKey(outID)) {
			nodeMessages= outBufferLCR.get(outID);
			nodeDirection = outDirectionLCR.get(outID);
		}else {
			nodeMessages = new ArrayList<LCRNodeMessage>();
			nodeDirection = new ArrayList<Integer>();
		}	
		nodeMessages.add(message);
		nodeDirection.add(CLOCKWISE_DIRECTION);
		outBufferLCR.put(outID, nodeMessages);
		outDirectionLCR.put(outID, nodeDirection);
		return true;
	}
	
	public boolean outMessage(int outID,HSNodeMessage message, int direction) {
		ArrayList<HSNodeMessage> nodeMessages;
		ArrayList<Integer> nodeDirection;
		
		if(outBufferHS.containsKey(outID)) {
			nodeMessages= outBufferHS.get(outID);
			nodeDirection = outDirectionHS.get(outID);
		}else {
			nodeMessages = new ArrayList<HSNodeMessage>();
			nodeDirection = new ArrayList<Integer>();
		}	
		nodeMessages.add(message);
		nodeDirection.add(direction);
		outBufferHS.put(outID, nodeMessages);
		outDirectionHS.put(outID, nodeDirection);
		return true;
	}
	
	public void transferMessageLCR() {
		//
		if(this.ringID.length>2){
			for(int i=1;i<this.ringID.length-1;i++) {
				ArrayList<LCRNodeMessage> loadMessage = new ArrayList<LCRNodeMessage>();
				ArrayList<Integer> saveDirection = new ArrayList<Integer>();
				if(outBufferLCR.containsKey(ringID[i])) {
					loadMessage= outBufferLCR.get(ringID[i]);
					outBufferLCR.remove(ringID[i]);
					saveDirection = outDirectionLCR.get(ringID[i]);
					outDirectionLCR.remove(ringID[i]);
				}
				
				ArrayList<LCRNodeMessage> saveMessagesClock;
				ArrayList<LCRNodeMessage> saveMessagesCounterClock;
				//clock
				if(inBufferLCR.containsKey(ringID[i+1])) {
					saveMessagesClock= inBufferLCR.get(ringID[i+1]);
				}else {
					saveMessagesClock = new ArrayList<LCRNodeMessage>();
				}	
				//counter
				if(inBufferLCR.containsKey(ringID[i-1])) {
					saveMessagesCounterClock= inBufferLCR.get(ringID[i-1]);
				}else {
					saveMessagesCounterClock = new ArrayList<LCRNodeMessage>();
				}
				
				for(int j=0;j<loadMessage.size();j++) {
					
					if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
						saveMessagesClock.add(loadMessage.get(j));
					}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
						saveMessagesCounterClock.add(loadMessage.get(j));
					}
				}

				
				inBufferLCR.put(ringID[i+1], saveMessagesClock);
				inBufferLCR.put(ringID[i-1], saveMessagesCounterClock);
				
			}
				
			for(int i = 0; i<ringID.length;i+=ringID.length-1) {
				
				ArrayList<LCRNodeMessage> loadMessage = new ArrayList<LCRNodeMessage>();
				ArrayList<Integer> saveDirection = new ArrayList<Integer>();
				if(outBufferLCR.containsKey(ringID[i])) {
					loadMessage= outBufferLCR.get(ringID[i]);
					outBufferLCR.remove(ringID[i]);
					saveDirection = outDirectionLCR.get(ringID[i]);
					outDirectionLCR.remove(ringID[i]);
				}
				
				ArrayList<LCRNodeMessage> saveMessagesClock;
				ArrayList<LCRNodeMessage> saveMessagesCounterClock;

				if(i==0) {
					//clock
					if(inBufferLCR.containsKey(ringID[1])) {
						saveMessagesClock= inBufferLCR.get(ringID[1]);
					}else {
						saveMessagesClock = new ArrayList<LCRNodeMessage>();
					}	
					//counter
					if(inBufferLCR.containsKey(ringID[ringID.length-1])) {
						saveMessagesCounterClock= inBufferLCR.get(ringID[ringID.length-1]);
					}else {
						saveMessagesCounterClock = new ArrayList<LCRNodeMessage>();
					}
					
					for(int j=0;j<loadMessage.size();j++) {
						
						if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
							saveMessagesClock.add(loadMessage.get(j));
						}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
							saveMessagesCounterClock.add(loadMessage.get(j));
						}
					}


					inBufferLCR.put(ringID[1], saveMessagesClock);
					inBufferLCR.put(ringID[ringID.length-1], saveMessagesCounterClock);
				}else if(i == ringID.length-1) {
					if(inBufferLCR.containsKey(ringID[0])) {
						saveMessagesClock= inBufferLCR.get(ringID[0]);
					}else {
						saveMessagesClock = new ArrayList<LCRNodeMessage>();
					}	
					//counter
					if(inBufferLCR.containsKey(ringID[ringID.length-2])) {
						saveMessagesCounterClock= inBufferLCR.get(ringID[ringID.length-2]);
					}else {
						saveMessagesCounterClock = new ArrayList<LCRNodeMessage>();
					}
					
					for(int j=0;j<loadMessage.size();j++) {
						if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
							saveMessagesClock.add(loadMessage.get(j));
							
						}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
							saveMessagesCounterClock.add(loadMessage.get(j));
						}
					}

					
					inBufferLCR.put(ringID[0], saveMessagesClock);
					inBufferLCR.put(ringID[ringID.length-2], saveMessagesCounterClock);
				}
				
			}
		}
		
		if(this.ringID.length==2) {
			//0
			ArrayList<LCRNodeMessage> loadMessage = new ArrayList<LCRNodeMessage>();
			ArrayList<Integer> saveDirection = new ArrayList<Integer>();
			if(outBufferLCR.containsKey(ringID[0])) {
				loadMessage= outBufferLCR.get(ringID[0]);
				outBufferLCR.remove(ringID[0]);
				saveDirection = outDirectionLCR.get(ringID[0]);
				outDirectionLCR.remove(ringID[0]);
			}
			
			ArrayList<LCRNodeMessage> saveMessages;
			if(inBufferLCR.containsKey(ringID[1])) {
				saveMessages= inBufferLCR.get(ringID[1]);
			}else {
				saveMessages = new ArrayList<LCRNodeMessage>();
			}	
			
			for(int j=0;j<loadMessage.size();j++) {
				
				if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}
			}
			inBufferLCR.put(ringID[1], saveMessages);
			
			//1
			loadMessage = new ArrayList<LCRNodeMessage>();
			saveDirection = new ArrayList<Integer>();
			if(outBufferLCR.containsKey(ringID[1])) {
				loadMessage= outBufferLCR.get(ringID[1]);
				outBufferLCR.remove(ringID[1]);
				saveDirection = outDirectionLCR.get(ringID[1]);
				outDirectionLCR.remove(ringID[1]);
			}
			
			saveMessages = new ArrayList<LCRNodeMessage>();
			if(inBufferLCR.containsKey(ringID[0])) {
				saveMessages= inBufferLCR.get(ringID[0]);
			}else {
				saveMessages = new ArrayList<LCRNodeMessage>();
			}	
			
			for(int j=0;j<loadMessage.size();j++) {
				
				if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}
			}
			inBufferLCR.put(ringID[0], saveMessages);

		}
		
		//	
	}
	
	public void transferMessageHS() {
		//
		if(this.ringID.length>2){
			
			for(int i=1;i<this.ringID.length-1;i++) {
				ArrayList<HSNodeMessage> loadMessage = new ArrayList<HSNodeMessage>();
				ArrayList<Integer> saveDirection = new ArrayList<Integer>();
				if(outBufferHS.containsKey(ringID[i])) {
					loadMessage= outBufferHS.get(ringID[i]);
					outBufferHS.remove(ringID[i]);
					saveDirection = outDirectionHS.get(ringID[i]);
					outDirectionHS.remove(ringID[i]);
				}
				
				ArrayList<HSNodeMessage> saveMessagesClock;
				ArrayList<HSNodeMessage> saveMessagesCounterClock;
				ArrayList<Integer> saveDirectionClock;
				ArrayList<Integer> saveDirectionCounterClock;
				//clock
				if(inBufferHS.containsKey(ringID[i+1])) {
					saveMessagesClock= inBufferHS.get(ringID[i+1]);
					saveDirectionClock = inDirectionHS.get(ringID[i+1]);
				}else {
					saveMessagesClock = new ArrayList<HSNodeMessage>();
					saveDirectionClock = new ArrayList<Integer>();
				}	
				//counter
				if(inBufferHS.containsKey(ringID[i-1])) {
					saveMessagesCounterClock= inBufferHS.get(ringID[i-1]);
					saveDirectionCounterClock = inDirectionHS.get(ringID[i-1]);
				}else {
					saveMessagesCounterClock = new ArrayList<HSNodeMessage>();
					saveDirectionCounterClock = new ArrayList<Integer>();
				}
				
				for(int j=0;j<loadMessage.size();j++) {
					
					if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
						saveMessagesClock.add(loadMessage.get(j));
						saveDirectionClock.add(COUNTERCLOCKWISE_DIRECTION);
					}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
						saveMessagesCounterClock.add(loadMessage.get(j));
						saveDirectionCounterClock.add(CLOCKWISE_DIRECTION);
					}
				}

				
				inBufferHS.put(ringID[i+1], saveMessagesClock);
				inBufferHS.put(ringID[i-1], saveMessagesCounterClock);
				inDirectionHS.put(ringID[i+1], saveDirectionClock);
				inDirectionHS.put(ringID[i-1], saveDirectionCounterClock);
				
			}
			
			//The last 2
			for(int i = 0; i<ringID.length;i+=ringID.length-1) {
				
				ArrayList<HSNodeMessage> loadMessage = new ArrayList<HSNodeMessage>();
				ArrayList<Integer> saveDirection = new ArrayList<Integer>();
				if(outBufferHS.containsKey(ringID[i])) {
					loadMessage= outBufferHS.get(ringID[i]);
					outBufferHS.remove(ringID[i]);
					saveDirection = outDirectionHS.get(ringID[i]);
					outDirectionHS.remove(ringID[i]);
				}
				
				ArrayList<HSNodeMessage> saveMessagesClock;
				ArrayList<HSNodeMessage> saveMessagesCounterClock;
				ArrayList<Integer> saveDirectionClock;
				ArrayList<Integer> saveDirectionCounterClock;

				if(i==0) {
					//clock
					if(inBufferHS.containsKey(ringID[1])) {
						saveMessagesClock= inBufferHS.get(ringID[1]);
						saveDirectionClock = inDirectionHS.get(ringID[1]);
					}else {
						saveMessagesClock = new ArrayList<HSNodeMessage>();
						saveDirectionClock = new ArrayList<Integer>();
					}	
					//counter
					if(inBufferHS.containsKey(ringID[ringID.length-1])) {
						saveMessagesCounterClock= inBufferHS.get(ringID[ringID.length-1]);
						saveDirectionCounterClock = inDirectionHS.get(ringID[ringID.length-1]);
					}else {
						saveMessagesCounterClock = new ArrayList<HSNodeMessage>();
						saveDirectionCounterClock = new ArrayList<Integer>();
					}
					
					for(int j=0;j<loadMessage.size();j++) {
						
						if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
							saveMessagesClock.add(loadMessage.get(j));
							saveDirectionClock.add(COUNTERCLOCKWISE_DIRECTION);
						}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
							saveMessagesCounterClock.add(loadMessage.get(j));
							saveDirectionCounterClock.add(CLOCKWISE_DIRECTION);
						}
					}


					inBufferHS.put(ringID[1], saveMessagesClock);
					inBufferHS.put(ringID[ringID.length-1], saveMessagesCounterClock);
					inDirectionHS.put(ringID[1], saveDirectionClock);
					inDirectionHS.put(ringID[ringID.length-1], saveDirectionCounterClock);
				}else if(i == ringID.length-1) {
					if(inBufferHS.containsKey(ringID[0])) {
						saveMessagesClock= inBufferHS.get(ringID[0]);
						saveDirectionClock = inDirectionHS.get(ringID[0]);
					}else {
						saveMessagesClock = new ArrayList<HSNodeMessage>();
						saveDirectionClock = new ArrayList<Integer>();
					}	
					//counter
					if(inBufferHS.containsKey(ringID[ringID.length-2])) {
						saveMessagesCounterClock= inBufferHS.get(ringID[ringID.length-2]);
						saveDirectionCounterClock = inDirectionHS.get(ringID[ringID.length-2]);
					}else {
						saveMessagesCounterClock = new ArrayList<HSNodeMessage>();
						saveDirectionCounterClock = new ArrayList<Integer>();
					}
					
					for(int j=0;j<loadMessage.size();j++) {
						if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
							saveMessagesClock.add(loadMessage.get(j));
							saveDirectionClock.add(COUNTERCLOCKWISE_DIRECTION);
							
						}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
							saveMessagesCounterClock.add(loadMessage.get(j));
							saveDirectionCounterClock.add(CLOCKWISE_DIRECTION);
						}
					}

					
					inBufferHS.put(ringID[0], saveMessagesClock);
					inBufferHS.put(ringID[ringID.length-2], saveMessagesCounterClock);
					inDirectionHS.put(ringID[0], saveDirectionClock);
					inDirectionHS.put(ringID[ringID.length-2], saveDirectionCounterClock);
				}
				
			}
		}
		
		if(this.ringID.length==2) {
			//0
			ArrayList<HSNodeMessage> loadMessage = new ArrayList<HSNodeMessage>();
			ArrayList<Integer> saveDirection = new ArrayList<Integer>();
			if(outBufferHS.containsKey(ringID[0])) {
				loadMessage= outBufferHS.get(ringID[0]);
				outBufferHS.remove(ringID[0]);
				saveDirection = outDirectionHS.get(ringID[0]);
				outDirectionHS.remove(ringID[0]);
			}
			
			ArrayList<HSNodeMessage> saveMessages;
			if(inBufferHS.containsKey(ringID[1])) {
				saveMessages= inBufferHS.get(ringID[1]);
			}else {
				saveMessages = new ArrayList<HSNodeMessage>();
			}	
			
			for(int j=0;j<loadMessage.size();j++) {
				
				if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}
			}
			inBufferHS.put(ringID[1], saveMessages);
			
			//1
			loadMessage = new ArrayList<HSNodeMessage>();
			saveDirection = new ArrayList<Integer>();
			if(outBufferHS.containsKey(ringID[1])) {
				loadMessage= outBufferHS.get(ringID[1]);
				outBufferHS.remove(ringID[1]);
				saveDirection = outDirectionHS.get(ringID[1]);
				outDirectionHS.remove(ringID[1]);
			}
			
			saveMessages = new ArrayList<HSNodeMessage>();
			if(inBufferHS.containsKey(ringID[0])) {
				saveMessages= inBufferHS.get(ringID[0]);
			}else {
				saveMessages = new ArrayList<HSNodeMessage>();
			}	
			
			for(int j=0;j<loadMessage.size();j++) {
				
				if(saveDirection.get(j)==CLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}else if(saveDirection.get(j)==COUNTERCLOCKWISE_DIRECTION) {
					saveMessages.add(loadMessage.get(j));
				}
			}
			inBufferHS.put(ringID[0], saveMessages);

		}
		
		//	
	}
	
	
	public LCRNodeMessage receiveMessage(int inID) {
		LCRNodeMessage inNodeMessage = null;
		ArrayList<LCRNodeMessage> nodeMessages;
		if(inBufferLCR.containsKey(inID)) {
			nodeMessages= inBufferLCR.get(inID);
			if(nodeMessages.size()>0) {
				inNodeMessage = nodeMessages.get(0);
				inBufferLCR.get(inID).remove(0);
			}
			
		}
		
		return inNodeMessage;
	}
	
	public HSNodeMessage receiveMessage(int inID, int inOut,int direction) {
		HSNodeMessage inNodeMessage = null;
		ArrayList<HSNodeMessage> nodeMessages;
		ArrayList<Integer> nodeDirection;
		if(ringID.length>2) {
			if(inBufferHS.containsKey(inID)) {
				nodeMessages= inBufferHS.get(inID);
				nodeDirection = inDirectionHS.get(inID);
				if(nodeMessages.size()>0) {
					for(int i=0; i<nodeMessages.size();i++) {
						if(nodeMessages.get(i).getInOut()==inOut&&nodeDirection.get(i)==direction) {
							inNodeMessage = nodeMessages.get(i);
							inBufferHS.get(inID).remove(i);
							inDirectionHS.get(inID).remove(i);
							i=nodeMessages.size();
						}
					}
				}
				
			}
		}else {
			if(inBufferHS.containsKey(inID)) {
				nodeMessages= inBufferHS.get(inID);
				if(nodeMessages.size()>0) {
					for(int i=0; i<nodeMessages.size();i++) {
						if(nodeMessages.get(i).getInOut()==inOut) {
							inNodeMessage = nodeMessages.get(i);
							inBufferHS.get(inID).remove(i);
							i=nodeMessages.size();
						}
					}
				}
			}
			
			
		}

		return inNodeMessage;
	}
	
	public HSNodeMessage receiveMessage(int inID, int inOut,int hop,int direction) {
		HSNodeMessage inNodeMessage = null;
		ArrayList<HSNodeMessage> nodeMessages;
		ArrayList<Integer> nodeDirection;
		if(ringID.length>2) {
			if(inBufferHS.containsKey(inID)) {
				nodeMessages= inBufferHS.get(inID);
				nodeDirection = inDirectionHS.get(inID);
				if(nodeMessages.size()>0) {
					for(int i=0; i<nodeMessages.size();i++) {
						if(nodeMessages.get(i).getInOut()==inOut&&nodeDirection.get(i)==direction&&nodeMessages.get(i).getHopCount()==hop) {
							inNodeMessage = nodeMessages.get(i);
							inBufferHS.get(inID).remove(i);
							inDirectionHS.get(inID).remove(i);
							i=nodeMessages.size();
						}
					}
				}
				
			}
		}else {
			if(inBufferHS.containsKey(inID)) {
				nodeMessages= inBufferHS.get(inID);
				if(nodeMessages.size()>0) {
					for(int i=0; i<nodeMessages.size();i++) {
						if(nodeMessages.get(i).getInOut()==inOut&&nodeMessages.get(i).getHopCount()==hop) {
							inNodeMessage = nodeMessages.get(i);
							inBufferHS.get(inID).remove(i);
							i=nodeMessages.size();
						}
					}
				}
			}
			
		}
		
		return inNodeMessage;
	}
	
	public void setDisplayRound(boolean state) {
		this.Display_Each_Round_Process = state;
	}
	public int getNodeSize() {
		return this.ringID.length;
	}
	
	public int[] getNodeRing() {
		return this.ringID;
	}

}
