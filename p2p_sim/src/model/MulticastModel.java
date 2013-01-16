package model;

import java.util.Collections;
import java.util.List;

import javax.management.BadAttributeValueExpException;

import graph_generators.HashRingNode;

import model.dynamic_types.BroadcastMessageGeneratorEvent;
import model.dynamic_types.MulticastMessageGeneratorEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.TimeSpan;

public class MulticastModel extends AbstractPubSubModel
{

	public MulticastModel(Graph<HashRingNode, DefaultEdge> graph, double subToAllRatio) throws BadAttributeValueExpException
	{
		super(graph, subToAllRatio);
	}

	@Override
	public void doInitialSchedules()
	{
		MulticastMessageGeneratorEvent generator = new MulticastMessageGeneratorEvent(this, "Message Generator", true);
		generator.schedule(new TimeSpan(0));
	}

}
