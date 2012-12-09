package model;

import graph_generators.HashRingNode;

import model.dynamic_types.BroadcastMessageGeneratorEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.TimeSpan;

public class DirectedBroadcastModel extends AbstractPubSubModel
{

	public DirectedBroadcastModel(Graph<HashRingNode, DefaultEdge> graph, long numOfSubs)
	{
		super(graph, numOfSubs);
	}

	@Override
	public void doInitialSchedules()
	{
		BroadcastMessageGeneratorEvent generator = new BroadcastMessageGeneratorEvent(this, "Message Generator", true);
		generator.schedule(new TimeSpan(0));
	}

}
