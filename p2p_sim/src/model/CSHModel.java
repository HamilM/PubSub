package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.management.BadAttributeValueExpException;

import graph_generators.HashRingNode;

import model.dynamic_types.CSHMessageGeneratorEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.TimeSpan;



public class CSHModel extends AbstractPubSubModel
{
	// number of closest subscribers known
	final int k;
	protected HashMap<Double, List<Double>> keyToClosest;
	
	public CSHModel(Graph<HashRingNode, DefaultEdge> graph, double subToAllRatio, int k) throws BadAttributeValueExpException
	{
		super(graph, subToAllRatio);
		this.k = k;
	}

	@Override
	public void init() 
	{
		super.init();
		updateClosestSubscribers();

	}
	
	private void updateClosestSubscribers()
	{
		HashRingNode r;
		ArrayList<Double> kQueue;
		
		keyToClosest = new HashMap<Double, List<Double>>();
		for (int i = 0; i < nodeList.size(); i++)
		{
			kQueue = new ArrayList<Double>();
			for (int j = 1; j < nodeList.size() && kQueue.size() < k; j++)
			{
				r = nodeList.get((i+j)%nodeList.size());
				if (IsNodeSubscriber(hashRingTraverser.nodeKeyToId(r.getHashKey())) == true)
				{
					kQueue.add(r.getHashKey());
				}
			}
			keyToClosest.put(nodeList.get(i).getHashKey(), kQueue);
		}
		return;
	}
	
	public List<Double> getClosestSubscribers(Double vHashKey)
	{
		return keyToClosest.get(vHashKey);
	}
	
	public int getK()
	{
		return k;
	}
	
	@Override
	public void doInitialSchedules()
	{
		CSHMessageGeneratorEvent generator = new CSHMessageGeneratorEvent(this, "Message Generator", true);
		generator.schedule(new TimeSpan(0));
	}

}
