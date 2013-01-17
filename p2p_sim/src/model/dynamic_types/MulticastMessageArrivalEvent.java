package model.dynamic_types;

import model.AbstractPubSubModel;
import model.utils.RoutingTable;
import graph_generators.HashRingNode;
import graph_generators.HashRingTraverser;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;

public class MulticastMessageArrivalEvent extends AbstractMessageEvent
{

	public MulticastMessageArrivalEvent(Model owner, String name, boolean showInTrace)
	{
		super(owner, name, showInTrace);
	}

	@Override
	public void eventRoutine(Message message)
	{
		Queue<Message> queue = model.getMessageQueue(message.getSrc());
		queue.insert(message);
		//sendTraceNote("Message arrived at node with vHashKey " + message.getSrc());
		
		Message firstMessage = queue.first();
		queue.remove(firstMessage);
		
		if (firstMessage.getSrc() == firstMessage.getDst())
		{
			//sendTraceNote("Message successfully arrived at subscriber " + firstMessage.getDst());
			return;
		}
		
		routeMessage(firstMessage);
	}
	
	protected void routeMessage(Message message)
	{
		HashRingNode target = model.getRoutingTable().nextTarget(message.getSrc(), message.getDst());
		/*
		 * src is the hashKey of the node that is receiving the message. Dst is the final destination.
		 */
		if (model.networkTree.containsVertex(target) == false)
		{
			model.networkTree.addVertex(target);
			model.networkTree.addEdge(model.getHashRingTraverser().getNodeByKey(message.getSrc()), target);
		}
		else if (model.networkTree.containsEdge(model.getHashRingTraverser().getNodeByKey(message.getSrc()), target) == false)
		{
			model.networkTree.addEdge(model.getHashRingTraverser().getNodeByKey(message.getSrc()), target);
		}
		Message nextMessage = new Message(model, target.getHashKey(), message.getDst(),false);
		
		MulticastMessageArrivalEvent messageGeneration = 
				new MulticastMessageArrivalEvent(		model, 
														"Message sent from vHashKey = " + message.getSrc() + " to " + target.getHashKey(), 
														fShowInTrace);
		messageGeneration.schedule(nextMessage, new TimeSpan(0));
	}

}
