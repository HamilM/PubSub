package model;

import graph_generators.HashRingNode;

import model.dynamic_types.BroadcastMessageGeneratorEvent;
import model.dynamic_types.MulticastMessageGeneratorEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.TimeSpan;

public class MulticastModel extends AbstractPubSubModel
{

	public MulticastModel(Graph<HashRingNode, DefaultEdge> graph, long numOfSubs)
	{
		super(graph, numOfSubs);
	}

	@Override
	public void doInitialSchedules()
	{
		MulticastMessageGeneratorEvent generator = new MulticastMessageGeneratorEvent(this, "Message Generator", true);
		generator.schedule(new TimeSpan(0));
	}

}
