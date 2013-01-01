package model.dynamic_types;

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
		
		Message messageToSend = null;
		for (int id : model.getSubscribersIdList())
		{
			messageToSend = new Message(model, firstMessage.getSrc(), model.getNodeList().get(id).getHashKey(), true);
			routeMessage(messageToSend);
		}
	}

}
