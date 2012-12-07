package model.dynamic_types;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

public class Message extends Entity 
{
	private int srcId;
	private int dstId;

	public Message(Model owner, String name, boolean showInTrace, int src, int dst) 
	{
		super(owner, name, showInTrace);
		
		this.srcId = src;
		this.dstId = dst;
	}

	public int getSrc() 
	{
		return srcId;
	}

	public int getDst() 
	{
		return dstId;
	}
	
	

}
