package model.dynamic_types;

import model.PubSubModel;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;

public abstract class MessasgeGeneratorEvent extends ExternalEvent
{

	public MessasgeGeneratorEvent(Model owner, String name, boolean showInTrace)
	{
		super(owner, name, showInTrace);
	}

}
