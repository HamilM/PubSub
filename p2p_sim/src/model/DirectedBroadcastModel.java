package model;

import javax.management.BadAttributeValueExpException;

import graph_generators.HashRingNode;

import model.dynamic_types.BroadcastMessageGeneratorEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.TimeSpan;

public class DirectedBroadcastModel extends AbstractPubSubModel
{

	public DirectedBroadcastModel(Graph<HashRingNode, DefaultEdge> graph, double subToAllRatio) throws BadAttributeValueExpException
	{
		super(graph, subToAllRatio);
	}

	@Override
	public void doInitialSchedules()
	{
		BroadcastMessageGeneratorEvent generator = new BroadcastMessageGeneratorEvent(this, "Message Generator", true);
		generator.schedule(new TimeSpan(0));
	}

}
