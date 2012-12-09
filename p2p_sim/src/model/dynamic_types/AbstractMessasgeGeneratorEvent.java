package model.dynamic_types;

import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;

public abstract class AbstractMessasgeGeneratorEvent extends ExternalEvent
{

	public AbstractMessasgeGeneratorEvent(Model owner, String name, boolean showInTrace)
	{
		super(owner, name, showInTrace);
	}

}
