package model.dynamic_types;

import graph_generators.HashRingNode;
import graph_generators.HashRingTraverser;

import java.util.ArrayList;
import model.AbstractPubSubModel;
import model.CSHModel;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;

public class CSHMessageArrivalEvent extends AbstractMessageEvent 
{
	
	public CSHMessageArrivalEvent(Model owner, String name, boolean showInTrace)
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
		ArrayList<Message> messagesToSend = createCSHMessage(	firstMessage.getSrc(), 
																	firstMessage.getDst(),
																	model.getNodeList(),
																	model,
																	model.getHashRingTraverser());
		if (messagesToSend.size() == 0)
		{
			sendTraceNote("Message reached final target at node with vHashKey = "+ message.getSrc());
			return;
		}
		
		generateCSHMessagesArrivalEvents(model, messagesToSend, firstMessage.getSrc());
	}
	
	public static void generateCSHMessagesArrivalEvents(AbstractPubSubModel model, ArrayList<Message> messages, double senderHashKey)
	{
		Message message;
		if (messages.size() == 0) return;
		for (int i = 0; i < messages.size(); i++)
		{
			message = messages.get(i);
			if (model.networkTree.containsVertex(model.getHashRingTraverser().getNodeByKey(message.getSrc())) == false)
			{
				model.networkTree.addVertex(model.getHashRingTraverser().getNodeByKey(message.getSrc()));
			}
			model.networkTree.addEdge(	model.getHashRingTraverser().getNodeByKey(senderHashKey), 
					model.getHashRingTraverser().getNodeByKey(message.getSrc()));
			CSHMessageArrivalEvent MessageGeneration = 
					new CSHMessageArrivalEvent(		model, 
															"Message sent from vHashKey = " + message.getSrc() + " to " + message.getDst(), 
															fShowInTrace);
			MessageGeneration.schedule(message, new TimeSpan(0));
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
	public static ArrayList<Message> createCSHMessage(		double 						vHashKey,
																double 						upperbound,
																ArrayList<HashRingNode>		nodeList,
																AbstractPubSubModel			model,
																HashRingTraverser			hashRingTraverser)
	{				
		/*
		 * Find the closest subscriber nodes of the current node.
		 */
		CSHModel cshModel = (CSHModel)model;
		ArrayList<Double> csList = (ArrayList<Double>) cshModel.getClosestSubscribers(vHashKey);
		int k = csList.size();
		ArrayList<Message> messages = new ArrayList<Message>();
		if (k == 0) return messages;
		Double nHashKey0 = csList.get(0);
		HashRingNode next0 = null;
		Double nHashKey = null;
		HashRingNode next = null;
		// if the closest subscriber is out of our jurisdiction, do nothing
		if (vHashKey == nHashKey0 || (vHashKey < upperbound && (upperbound <= nHashKey0 || vHashKey >= nHashKey0)) || 
			(vHashKey > upperbound && upperbound <= nHashKey0 && nHashKey0 <= vHashKey)) return messages;
		// find the link closest to closest subscriber
		next0 = hashRingTraverser.getClosestLinkedPredecessor(vHashKey, nHashKey0);
		nHashKey = csList.get(k-1);
		next = hashRingTraverser.getClosestLinkedPredecessor(vHashKey, nHashKey);
		if (0 != k-1) {
			// if the furthest known subscriber is out of our jurisdiction, send with upperbound
			if (vHashKey == nHashKey || (vHashKey < upperbound && (upperbound <= nHashKey || vHashKey >= nHashKey)) || 
					(vHashKey > upperbound && upperbound <= nHashKey && nHashKey <= vHashKey)) {
				messages.add(new Message(model, next0.getHashKey(), upperbound, false));
			}
			else {
				messages.add(new Message(model, next0.getHashKey(), nHashKey, false));
			}
		}
		if (vHashKey == nHashKey || (vHashKey < upperbound && (upperbound <= nHashKey || vHashKey >= nHashKey)) || 
				(vHashKey > upperbound && upperbound <= nHashKey && nHashKey <= vHashKey)) return messages;
		// if we're here, nHashkey should be in the right place
		double destinationHashKey = nHashKey < upperbound ? 
						(nHashKey + (upperbound-nHashKey)/2.0) : (nHashKey + (1-nHashKey+upperbound)/2.0)%1.0;
		HashRingNode halfWay = hashRingTraverser.getClosestLinkedPredecessor(vHashKey, destinationHashKey);
		// halfWay is the closest to middle between closest subscriber and upperbound
		// TODO closest linked successor
		/*
		if (halfWay.getHashKey() == next.getHashKey()) {
			halfWay = hashRingTraverser.getClosestLinkedSuccessor(vHashKey, destinationHashKey);
			if ((vHashKey < upperbound && (halfWay.getHashKey() <= vHashKey || halfWay.getHashKey() >= upperbound)) ||
			(vHashKey > upperbound && halfWay.getHashKey() >= upperbound && halfWay.getHashKey() <= vHashKey) {
				messages.add(new Message(model, next.getHashKey(), upperbound, false));
				break;
			}
		}*/
		if (halfWay.getHashKey() == next.getHashKey()) {
			messages.add(new Message(model, next.getHashKey(), upperbound, false));
			return messages;
		}
		messages.add(new Message(model, next.getHashKey(), halfWay.getHashKey(), false));
		messages.add(new Message(model, halfWay.getHashKey(), upperbound, false));
		return messages;
	}

}
