package model.utils;

import graph_generators.HashRingNode;
import graph_generators.HashRingTraverser;

import java.util.ArrayList;
import java.util.HashMap;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;

public class RoutingTable
{
	/*
	 * Each index in the ArrayList represents the routing table of the HashRingNode of the same index
	 */
	protected ArrayList<HashMap<Double, HashRingNode>> tables;
	protected HashRingTraverser hashRingTraverser;
	
	
	public RoutingTable(int numberOfNodes, HashRingTraverser hashRingTraverser)
	{
		this.tables = new ArrayList<HashMap<Double, HashRingNode>>();
		this.hashRingTraverser = hashRingTraverser;
		for (int i = 0; i < numberOfNodes; i++)
		{
			tables.add(i, new HashMap<Double, HashRingNode>());
		}
	}
	
	public HashRingNode nextTarget(double srcNodeKey, double dstNodeKey)
	{
		HashMap<Double, HashRingNode> table = tables.get(hashRingTraverser.nodeKeyToId(srcNodeKey));
		HashRingNode $ = table.get(dstNodeKey);
		if ($ == null)
		{
			$ = hashRingTraverser.getClosestLinkedPredecessor(srcNodeKey, dstNodeKey);
			table.put(dstNodeKey, $);
		}
		return $;
	}
}
