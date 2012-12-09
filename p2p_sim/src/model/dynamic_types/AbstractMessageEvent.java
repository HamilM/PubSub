package model.dynamic_types;

import model.AbstractPubSubModel;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;

public abstract class AbstractMessageEvent extends Event<Message>
{
	protected AbstractPubSubModel model;

	public AbstractMessageEvent(Model owner, String name, boolean showInTrace)
	{
		super(owner, name, showInTrace);
		model = (AbstractPubSubModel)owner;
	}

}
