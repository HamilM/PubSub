package model.dynamic_types;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

public class Message extends Entity 
{
	private final static String fName = "Message";
	private final static boolean fShowInTrace = true;
	
	private double srcId;
	private double dstId;
	private boolean isFirstMessage;

	public Message(Model owner, double src, double dst, boolean isFirstMessage) 
	{
		super(owner, fName, fShowInTrace);
		
		this.srcId = src;
		this.dstId = dst;
		
		this.isFirstMessage = isFirstMessage;
	}

	public double getSrc() 
	{
		return srcId;
	}

	public double getDst() 
	{
		return dstId;
	}
	
	public boolean isFirstMessage()
	{
		return isFirstMessage;
	}
	
}
