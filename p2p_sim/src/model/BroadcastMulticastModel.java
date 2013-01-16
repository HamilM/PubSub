package model;

import graph_generators.HashRingNode;

import javax.management.BadAttributeValueExpException;

import model.dynamic_types.AbstractMessasgeGeneratorEvent;
import model.dynamic_types.BroadcastMessageGeneratorEvent;
import model.dynamic_types.MulticastMessageGeneratorEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.TimeSpan;

public class BroadcastMulticastModel extends AbstractPubSubModel
{
	final private double RATIO_LIMIT = 0.5;
	private double subToAllRatio;
	
	public BroadcastMulticastModel(	Graph<HashRingNode, DefaultEdge> 	graph,
									double 								subToAllRatio) 
											throws BadAttributeValueExpException
	{
		super(graph, subToAllRatio);
		this.subToAllRatio = subToAllRatio;
	}

	@Override
	public void doInitialSchedules()
	{
		AbstractMessasgeGeneratorEvent generator;
		if (subToAllRatio < RATIO_LIMIT)
		{
			generator = new MulticastMessageGeneratorEvent(this, "Message Generator", true);
		}
		else
		{
			generator = new BroadcastMessageGeneratorEvent(this, "Message Generator", true);
		}
		generator.schedule(new TimeSpan(0));

	}

}
