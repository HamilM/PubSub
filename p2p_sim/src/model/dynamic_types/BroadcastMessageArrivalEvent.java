package model.dynamic_types;

import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;
import graph_generators.HashRingTraverser;

import java.util.ArrayList;

import model.AbstractPubSubModel;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;

public class BroadcastMessageArrivalEvent extends AbstractMessageEvent
{	
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
		
		/*
		 * For precaution.
		 */
		if (firstMessage.getSrc() == firstMessage.getDst() && firstMessage.isFirstMessage() == false)
		{
			sendTraceNote("Message reached final target at node with vHashKey = "+ message.getSrc());
			return;
		}
		
		Message[] messagesToSend = createDirectedBroadcastMessage(	firstMessage.getSrc(), 
																	firstMessage.getDst(),
																	model.getNodeList(),
																	model,
																	model.getHashRingTraverser());
		if (messagesToSend == null)
		{
			sendTraceNote("Message reached final target at node with vHashKey = "+ message.getSrc());
			return;
		}
		
		generateBroadcastMessagesArrivalEvents(model, messagesToSend, firstMessage.getSrc());
	}
	
	public static void generateBroadcastMessagesArrivalEvents(AbstractPubSubModel model, Message[] messages, double senderHashKey)
	{
		
		for (Message message : messages)
		{
			if (model.networkTree.containsVertex(model.getHashRingTraverser().getNodeByKey(message.getSrc())) == false)
			{
				model.networkTree.addVertex(model.getHashRingTraverser().getNodeByKey(message.getSrc()));
				model.networkTree.addEdge(	model.getHashRingTraverser().getNodeByKey(senderHashKey), 
											model.getHashRingTraverser().getNodeByKey(message.getSrc()));
			}
			
			BroadcastMessageArrivalEvent nearHalfMessageGeneration = 
					new BroadcastMessageArrivalEvent(		model, 
															"Message sent from vHashKey = " + message.getSrc() + " to " + message.getDst(), 
															fShowInTrace);
			nearHalfMessageGeneration.schedule(message, new TimeSpan(0));
		}
	}

	/**
	 * 
	 * @param vHashKey		- Hash key of the current node
	 * @param upperbound	- Upper bound of the DBC algorithm
	 * @param graph			- The graph.
	 * 
	 * @return A size 2 array with the 2 messages to be sent.
	 */
	public static Message[] createDirectedBroadcastMessage(		double 						vHashKey,
																double 						upperbound,
																ArrayList<HashRingNode>		nodeList,
																AbstractPubSubModel			model,
																HashRingTraverser			hashRingTraverser)
	{		
		
		/*
		 * Find the direct successor node of the current node.
		 */
		int vid = hashRingTraverser.getSuccessorNodeId(hashRingTraverser.nodeKeyToId(vHashKey));
		//vid = vid < nodeList.size()-1 ? vid+1 : 0;
		
		HashRingNode directSuccessor = nodeList.get(vid);
		
		/*
		 * Calculate the hashKey that represents the middle of the hash ring range between the locations of the current node and the upperbound.
		 */
		double destinationHashKey = vHashKey < upperbound ? (vHashKey + (upperbound-vHashKey)/2.0) : (vHashKey + (1-vHashKey+upperbound)/2.0)%1.0;
		
		/*
		 * Get the closest predecessor of the destinationHashKey which is linked to the current node 
		 */
		HashRingNode halfWaySuccessor = hashRingTraverser.getClosestLinkedPredecessor(vHashKey, destinationHashKey);
		
		/*
		 * If The there are no linked nodes between the half way successor and the current node,
		 * it means that the half way successor is between the current node and its direct successor
		 * (Because otherwise, the direct successor would have been the closest linked predecessor).
		 * 
		 * In this case, halfWaySuccessor will be the directSuccessor.
		 */
		if (halfWaySuccessor.getHashKey() == vHashKey)
		{
			halfWaySuccessor = directSuccessor;
		}
		
		/*
		 * If halfWaySuccessor is the directSuccessor, the near half message won't be needed.
		 */
		if (halfWaySuccessor.equals(directSuccessor))
		{
			if (halfWaySuccessor.getHashKey() == upperbound)
			{
				/*
				 * If halfWaySuccessor is the upperbound, it means that the current node is responsible only for itself, and we are done.
				 */
				return null;
			}
			Message[] messages = new Message[1];
			messages[0] = new Message(model, halfWaySuccessor.getHashKey(), upperbound, false);
			return messages;
		}
		
		/*
		 * The regular case - send 2 messages, one responsible for [vHashKey, halfWaySuccessor), and the other for [halfWaySuccessor,upperbound).
		 */
		Message[] messages = new Message[2];
			
		messages[0] = new Message(model, directSuccessor.getHashKey(), halfWaySuccessor.getHashKey(), false);
		messages[1] = new Message(model, halfWaySuccessor.getHashKey(), upperbound, false);
		
		return messages;
		
	}

}
