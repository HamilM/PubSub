package model.dynamic_types;

import graph_generators.HashRingNode;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;

public class MulticastPublisherMessageArrivalEvent extends MulticastMessageArrivalEvent
{
	
	public MulticastPublisherMessageArrivalEvent(Model owner, String name,
			boolean showInTrace)
	{
		super(owner, name, showInTrace);
	}

	@Override
	public void eventRoutine(Message message)
	{
		Queue<Message> queue = model.getMessageQueue(message.getSrc());
		queue.insert(message);
		//sendTraceNote("Message arrived at publisher with vHashKey " + message.getSrc());
		
		Message firstMessage = queue.first();
		queue.remove(firstMessage);
		
		if (model.networkTree == null)
		{
			model.networkTree = new DefaultDirectedGraph<HashRingNode, DefaultEdge>(DefaultEdge.class);
			model.networkTree.addVertex(model.getHashRingTraverser().getNodeByKey(firstMessage.getSrc()));
		}
		
		Message messageToSend = null;
		for (int id : model.getSubscribersIdList())
		{
			messageToSend = new Message(model, firstMessage.getSrc(), model.getNodeList().get(id).getHashKey(), true);
			routeMessage(messageToSend);
		}
	}

}
