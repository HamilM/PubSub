package model.dynamic_types;

import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;

import java.util.ArrayList;

import model.AbstractPubSubModel;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;

public class BroadcastMessageArrivalEvent extends AbstractMessageEvent
{

	private final static boolean fShowInTrace = true;
	
	public BroadcastMessageArrivalEvent(Model owner, String name, boolean showInTrace)
	{
		super(owner, name, showInTrace);
	}

	@Override
	public void eventRoutine(Message message)
	{
		Queue<Message> queue = model.getMessageQueue(message.getSrc());
		queue.insert(message);
		sendTraceNote("Message arrived at node with vHashKey " + message.getSrc());
		
		Message firstMessage = queue.first();
		queue.remove(firstMessage);
		
		if (firstMessage.getSrc() == firstMessage.getDst() && firstMessage.isFirstMessage() == false)
		{
			sendTraceNote("Message reached final target at node with vHashKey = "+ message.getSrc());
			return;
		}
		
		Message[] messagesToSend = createDirectedBroadcastMessage(	firstMessage.getSrc(), 
																	firstMessage.getDst(), 
																	model.getNodeList(), 
																	model.getGraph(), 
																	model);
		if (messagesToSend == null)
		{
			sendTraceNote("Message reached final target at node with vHashKey = "+ message.getSrc());
			return;
		}
		
		generateBroadcastMessagesArrivalEvents(model, messagesToSend);
	}
	
	public static void generateBroadcastMessagesArrivalEvents(AbstractPubSubModel model, Message[] messages)
	{
		
		for (Message message : messages)
		{
			BroadcastMessageArrivalEvent nearHalfMessageGeneration = 
					new BroadcastMessageArrivalEvent(		model, 
															"Message sent from vHashKey = " + message.getSrc() + " to " + message.getDst(), 
															fShowInTrace);
			nearHalfMessageGeneration.schedule(message, new TimeSpan(0));
		}
	}

	/**
	 * 
	 * @param vHashKey
	 * @param upperbound
	 * @param graph
	 * 
	 * @return A size 2 array with the 2 messages to be sent.
	 */
	public static Message[] createDirectedBroadcastMessage(		double 								vHashKey,
																double 								upperbound,
																ArrayList<HashRingNode>				nodeList,
																Graph<HashRingNode, DefaultEdge> 	graph,
																AbstractPubSubModel					model)
	{		
		int vid = HashRingGenerator.getSuccessorNodeId(vHashKey, nodeList);
		vid = vid < nodeList.size()-1 ? vid+1 : 0;
		
		HashRingNode directSuccessor = nodeList.get(vid);
		
		double destinationHashKey = vHashKey < upperbound ? (vHashKey + (upperbound-vHashKey)/2.0) : (vHashKey + (1-vHashKey+upperbound)/2.0)%1.0;
		
		HashRingNode halfWaySuccessor = HashRingGenerator.getClosestLinkedPredecessor(vHashKey, destinationHashKey, nodeList, graph);
		
		if (halfWaySuccessor.getHashKey() == vHashKey)
		{
			halfWaySuccessor = directSuccessor;
		}
		
		if (halfWaySuccessor.equals(directSuccessor))
		{
			if (halfWaySuccessor.getHashKey() == upperbound)
			{
				return null;
			}
			Message[] messages = new Message[1];
			messages[0] = new Message(model, halfWaySuccessor.getHashKey(), upperbound, false);
			return messages;
		}
		
		
		Message[] messages = new Message[2];
			
		messages[0] = new Message(model, directSuccessor.getHashKey(), halfWaySuccessor.getHashKey(), false);
		messages[1] = new Message(model, halfWaySuccessor.getHashKey(), upperbound, false);
		
		return messages;
		
	}

}
