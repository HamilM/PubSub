package model.dynamic_types;

import graph_generators.HashRingNode;

import java.util.ArrayList;

import model.AbstractPubSubModel;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class MulticastMessageGeneratorEvent extends AbstractMessasgeGeneratorEvent
{	
	public MulticastMessageGeneratorEvent(Model owner, String name,
			boolean showInTrace)
	{
		super(owner, name, showInTrace);
	}

	@Override
	public void eventRoutine()
	{
		AbstractPubSubModel model = (AbstractPubSubModel)getModel();
		ArrayList<HashRingNode> nodeList = model.getNodeList();
		double publisherHashKey = nodeList.get(model.getPublisherIndex()).getHashKey();
		Message message = new Message(model, publisherHashKey, publisherHashKey, true);
		
		MulticastMessageArrivalEvent messageArrivalEvent = new MulticastPublisherMessageArrivalEvent(model, "Publisher sends new message", fShowInTrace);
		messageArrivalEvent.schedule(message, new TimeSpan(0));
		
		//schedule(new TimeSpan(model.getMessageArrivalTime(), model.TIME_UNIT));
		
		
	}

}
