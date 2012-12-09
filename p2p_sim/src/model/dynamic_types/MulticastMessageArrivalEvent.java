package model.dynamic_types;

import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;
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
		sendTraceNote("Message arrived at node with vHashKey " + message.getSrc());
		
		Message firstMessage = queue.first();
		queue.remove(firstMessage);
		
		if (firstMessage.getSrc() == firstMessage.getDst())
		{
			sendTraceNote("Message successfully arrived at subscriber " + firstMessage.getDst());
			return;
		}
		
		routeMessage(firstMessage);
	}
	
	protected void routeMessage(Message message)
	{
		HashRingNode target = HashRingGenerator.getClosestLinkedPredecessor(message.getSrc(), message.getDst(), model.getNodeList(), model.getGraph());
		Message nextMessage = new Message(model, target.getHashKey(), message.getDst(),false);
		
		MulticastMessageArrivalEvent messageGeneration = 
				new MulticastMessageArrivalEvent(		model, 
														"Message sent from vHashKey = " + message.getSrc() + " to " + target.getHashKey(), 
														fShowInTrace);
		messageGeneration.schedule(nextMessage, new TimeSpan(0));
	}

}
