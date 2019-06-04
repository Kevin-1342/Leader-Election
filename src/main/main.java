package main;

import java.util.ArrayList;
import java.util.Scanner;

import hs.HSNodeAlgorithm;
import hs.HSNodeMessage;
import lcr.LCRNodeAlgorithm;
import lcr.LCRNodeMessage;
import network.Network;

public class main {
	//Ring type
	private final static int CLOCK_INCREASE = 0;
	private final static int COUNTERCLOCK_INCREASE = 1;
	private final static int RANDOM = 2;
	
	private final static int LCR = 0;
	private final static int HS = 1;
	private final static int LCR_AND_HS = 2;
	
	private static int Start_Node_Num;
	private static int End_Node_Num;
	private static int Ring_Order;
	private static int Algorithm_Type;
	private static int Avarge_Times;
	private static int Add_Node_Each_Time;
	
	public static boolean Display_Each_Round_Process;
	
	
	public static void main(String[] args) {
		
		/*The loop will start with this number of nodes*/
		Start_Node_Num =10;
		
		/*The loop will end with this number of nodes*/
		End_Node_Num = 10;
		
		/*How many distinct simulations*/
		Avarge_Times = 1;
		//Add_Size_Each_Time = 100;
		
		/*Add how many nodes each loop*/
		Add_Node_Each_Time = 1;
		//Add_Size_Each_Time = 50;
		
		/*Ring order*/
		//Ring_Order = RANDOM;
		Ring_Order = CLOCK_INCREASE;
		//Ring_Order = COUNTERCLOCK_INCREASE;
		
		/*Algorithm type*/
		Algorithm_Type = LCR_AND_HS;
		//Algorithm_Type = LCR;
		//Algorithm_Type = HS;
		
		/*Display the whole transfer process of each round for each size*/
		//Display_Each_Round_Process = true;//Not recommend to set this true
		
		/*Only display the round and message result*/
		Display_Each_Round_Process = false;
		
		
		/*Can just change 7 variables above, or call the UI method below. Both are same*/
		userInterface();	
		
		//Main method
		if(!Display_Each_Round_Process) {
			System.out.print("Nodes Num"+"\t"+"LCR average round"+"\t"+"LCR average message"+"\t"+"LCR max num of message"+"\t"+"LCR min num of message"+"\t"+"HS average round"+"\t"+"HS average message"+"\t"+"HS max num of message"+"\t"+"HS min num of message");
		}
		while(Start_Node_Num<=End_Node_Num) {

			//For random
			float lcr_average_round = 0;
			float lcr_average_msg = 0;
			float lcr_min_msg=0x7fffffff;
			float lcr_max_msg=0;
			float hs_average_round = 0;
			float hs_average_msg = 0;
			float hs_min_msg=0x7fffffff;
			float hs_max_msg=0;
			
			int loop_times = 1;
			if(!Display_Each_Round_Process) {
				System.out.print("\n"+Start_Node_Num+"\t");
			}
			
			while(loop_times<=Avarge_Times) {
				int roundCount = 0;
				int leaderRound = 0;
				boolean leaderTerminated = false;
				
				loop_times++;
				
				Network network = new Network();
				network.setDisplayRound(Display_Each_Round_Process);
				if(Display_Each_Round_Process) {
					System.out.println("ring:");
				}
				network.createRingNetwork(Start_Node_Num,Ring_Order);
				int [] networkRing = network.getNodeRing();
				if(Display_Each_Round_Process) {
					System.out.println("\n--------------");
				}

				if(Algorithm_Type == LCR||Algorithm_Type==LCR_AND_HS) {
					if(Display_Each_Round_Process) {
						System.out.println("\n--------------");
						System.out.println("LCR algorithm");
						System.out.println("--------------");
					}
					ArrayList<LCRNodeAlgorithm> lcrNode = new ArrayList<LCRNodeAlgorithm>();
					
					for(int i =0;i< network.getNodeSize();i++) {
						LCRNodeAlgorithm node = new LCRNodeAlgorithm(networkRing[i],network);
						node.setDisplayRound(Display_Each_Round_Process);
						lcrNode.add(node);
					}
					while(!checkLCRTerminated(network,lcrNode)) {
						roundCount++;	
						if(Display_Each_Round_Process) {
							System.out.println("********Round: "+roundCount+"*************");
						}
						for(int i =0;i< network.getNodeSize();i++) {
							lcrNode.get(i).mainAlgorithm();	
						}
						network.transferMessageLCR();
						
						if(!leaderTerminated) {
							for(int i =0;i< network.getNodeSize();i++) {
								leaderTerminated = leaderTerminated||lcrNode.get(i).getTerminated();
							}
							if(leaderTerminated) {
								leaderRound = roundCount;
							}
						}
					}
					if(Display_Each_Round_Process) {
						System.out.println("\n--------------");
						System.out.println("LCR algorithm");
						System.out.println("--------------");
						System.out.println("Node number: "+Start_Node_Num);
						System.out.println("LeaderID: "+lcrNode.get(0).getLeaderID());
						System.out.println("Total rounds including boardcast: "+roundCount);
						System.out.println("Total Messages: "+checkLCRMsgCount(network,lcrNode));
					}

					
					lcr_average_round+=roundCount;
					float LCRmsg = checkLCRMsgCount(network,lcrNode);
					lcr_average_msg+=LCRmsg;
					if(LCRmsg>lcr_max_msg) {
						lcr_max_msg = LCRmsg;
					}
					if(LCRmsg<lcr_min_msg) {
						lcr_min_msg = LCRmsg;
					}
					//System.out.println("Total Messages: "+checkLCRMsgCount(network,lcrNode));
					//System.out.println("----");
					//System.out.println("Leader terminated in round: "+leaderRound);
					
				}
				
				roundCount = 0;
				leaderRound = 0;
				leaderTerminated = false;
				
				if(Algorithm_Type == HS||Algorithm_Type==LCR_AND_HS) {
					if(Display_Each_Round_Process) {
						System.out.println("\n--------------");
						System.out.println("HS algorithm");
						System.out.println("--------------");
					}
					ArrayList<HSNodeAlgorithm> hsNode = new ArrayList<HSNodeAlgorithm>();
					for(int i =0;i< network.getNodeSize();i++) {
						HSNodeAlgorithm node = new HSNodeAlgorithm(networkRing[i],network);
						node.setDisplayRound(Display_Each_Round_Process);
						hsNode.add(node);
					}
					while(!checkHSTerminated(network,hsNode)) {
						roundCount++;
						if(Display_Each_Round_Process) {
							System.out.println("********Round: "+roundCount+"*************");
						}
						for(int i =0;i< network.getNodeSize();i++) {
							hsNode.get(i).mainAlgorithm();	
						}
						network.transferMessageHS();
						
						if(!leaderTerminated) {
							for(int i =0;i< network.getNodeSize();i++) {
								leaderTerminated = leaderTerminated||hsNode.get(i).getTerminated();
							}
							if(leaderTerminated) {
								leaderRound = roundCount;
							}
						}
					}
					if(Display_Each_Round_Process) {
						System.out.println("\n--------------");
						System.out.println("HS algorithm");
						System.out.println("--------------");
						System.out.println("Node number: "+Start_Node_Num);
						System.out.println("LeaderID: "+hsNode.get(0).getLeaderID());
						System.out.println("Total rounds including boardcast: "+roundCount);
						System.out.println("Total Messages: "+checkHSMsgCount(network,hsNode));
					}

					hs_average_round += roundCount;
					float HSmsg = checkHSMsgCount(network,hsNode);
					hs_average_msg+= HSmsg;
					if(HSmsg>hs_max_msg) {
						hs_max_msg = HSmsg;
					}
					if(HSmsg<hs_min_msg) {
						hs_min_msg = HSmsg;
					}
					//System.out.println("Total Messages: "+checkHSMsgCount(network,hsNode));
					//System.out.println("----");
					//System.out.println("Leader terminated in round: "+leaderRound);
				}
				
			}
			lcr_average_round = lcr_average_round/Avarge_Times;
			lcr_average_msg = lcr_average_msg/Avarge_Times;
			hs_average_round = hs_average_round/Avarge_Times;
			hs_average_msg = hs_average_msg/Avarge_Times;
			if(Display_Each_Round_Process) {
				System.out.println("\n-----Average------");
				System.out.print("Nodes Num"+"\t"+"LCR average round"+"\t"+"LCR average message"+"\t"+"LCR max num of message"+"\t"+"LCR min num of message"+"\t"+"HS average round"+"\t"+"HS average message"+"\t"+"HS max num of message"+"\t"+"HS min num of message\n"+Start_Node_Num+"\t");
			}
			System.out.print(lcr_average_round+"\t"+lcr_average_msg+"\t"+lcr_max_msg+"\t"+lcr_min_msg+"\t"+hs_average_round+"\t"+hs_average_msg+"\t"+hs_max_msg+"\t"+hs_min_msg);
			Start_Node_Num+=Add_Node_Each_Time;

		}
		
	}
	
//-------------------------------------------------------------------------------------------------	
	
	//UI
	private static void userInterface() {
		//Start_Node_Num: 
		System.out.println("*Please input start at how many nodes:");
		boolean inputRight = false;
		while(!inputRight) {
			try {
				Scanner in =new Scanner(System.in);
		        Start_Node_Num=in.nextInt();
		        inputRight = true;
		        if(Start_Node_Num<2) {
		        	inputRight = false;
		        	System.out.println("Should larger than 1, Please input node number:");
		        }
			} catch (Exception e) {
				System.out.println("Error! Please input node number:");
			}
		}
		
		//End_Node_Num: 
		System.out.println("*Please input stop at how many nodes:");
		inputRight = false;
		while(!inputRight) {
			try {
				Scanner in =new Scanner(System.in);
				End_Node_Num=in.nextInt();
		        inputRight = true;
		        if(End_Node_Num<Start_Node_Num) {
		        	inputRight = false;
		        	System.out.println("Should not smaller than start number, Please input node number:");
		        }
			} catch (Exception e) {
				System.out.println("Error! Please input node number:");
			}
		}
		
		//Avarge_Times: 
		System.out.println("*Please input how many distinct simulations are needed for each size:");
		inputRight = false;
		while(!inputRight) {
			try {
				Scanner in =new Scanner(System.in);
				Avarge_Times=in.nextInt();
		        inputRight = true;
		        if(Avarge_Times<1) {
		        	inputRight = false;
		        	System.out.println("Should not smaller than 1, Please input average time:");
		        }
			} catch (Exception e) {
				System.out.println("Error! Please input average time:");
			}
		}
		
		//Add_Node_Each_Time: 
		System.out.println("*Please input gap between each size in simulation:");
		inputRight = false;
		while(!inputRight) {
			try {
				Scanner in =new Scanner(System.in);
				Add_Node_Each_Time=in.nextInt();
		        inputRight = true;
		        if(Add_Node_Each_Time<1) {
		        	inputRight = false;
		        	System.out.println("Should not smaller than 1, Please input repeat time:");
		        }
			} catch (Exception e) {
				System.out.println("Error! Please input repeat time:");
			}
		}
		
		
		//Ring_Order:
		inputRight = false;
		System.out.println("*Please select node ID order: 0. increase clockwise, 1. increase counterclockwise, 2. random");
		while(!inputRight) {
			try {
				Scanner in =new Scanner(System.in);
				Ring_Order=in.nextInt();
		        inputRight = true;
		        if(Ring_Order>2||Ring_Order<0) {
		        	inputRight = false;
		        	System.out.println("Should be 0-2: 0. increase clockwise, 1. increase counterclockwise, 2. random");
		        }
			} catch (Exception e) {
				System.out.println("Error! Please input number:0. increase clockwise, 1. increase counterclockwise, 2. random");
			}
		}
		
		//Algorithm_Type: 
		inputRight = false;
		System.out.println("*Please select algorithm: 0. LCR, 1. HS, 2.LCR and HS");
		while(!inputRight) {
			try {
				Scanner in =new Scanner(System.in);
				Algorithm_Type=in.nextInt();
		        inputRight = true;
		        if(Algorithm_Type>2||Algorithm_Type<0) {
		        	inputRight = false;
		        	System.out.println("Should be 0-1: 0. LCR, 1. HS, 2.LCR and HS");
		        }
			} catch (Exception e) {
				System.out.println("Error! Please input number:0. LCR, 1. HS, 2.LCR and HS");
			}
		}
		
		//Display_Each_Round_Process: 
		inputRight = false;
		System.out.println("*Please select: 0. display the whole transfer progress(not recommended), 1. Only show the result");
		while(!inputRight) {
			try {
				Scanner in =new Scanner(System.in);
				int typeIN = in.nextInt();
		        inputRight = true;
		        if(typeIN>1||typeIN<0) {
		        	inputRight = false;
		        	System.out.println("Should be 0-1: 0. display the whole transfer progress(not recommended), 1. Only show the result");
		        }else{
		        	if(typeIN==0) {
		        		Display_Each_Round_Process = true;
		        	}else {
		        		Display_Each_Round_Process = false;
		        	}
		        }
			} catch (Exception e) {
				System.out.println("Error! Please input number:0. display the whole transfer progress(not recommended), 1. Only show the result");
			}
		}

		
	}
	
	private static boolean checkHSTerminated(Network net,ArrayList<HSNodeAlgorithm> hsNode) {
		boolean terminated = true;
		for(int i =0;i< net.getNodeSize();i++) {
			terminated = terminated&&hsNode.get(i).getTerminated();
		}
		
		return terminated;
	}
	
	private static boolean checkLCRTerminated(Network net,ArrayList<LCRNodeAlgorithm> lcrNode) {
		boolean terminated = true;
		for(int i =0;i< net.getNodeSize();i++) {
			terminated = terminated&&lcrNode.get(i).getTerminated();
		}
		return terminated;
	}
	
	private static int checkHSMsgCount(Network net,ArrayList<HSNodeAlgorithm> hsNode) {
		int msgCount = 0;
		for(int i =0;i< net.getNodeSize();i++) {
			msgCount += hsNode.get(i).getMesCounter();
		}
		return msgCount;
	}
	
	private static int checkLCRMsgCount(Network net,ArrayList<LCRNodeAlgorithm> lcrNode) {
		int msgCount = 0;
		for(int i =0;i< net.getNodeSize();i++) {
			msgCount += lcrNode.get(i).getMesCounter();
		}
		return msgCount;
	}

}
