package graph_generators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public abstract class HashRingGenerator
{
	protected static ArrayList<HashRingNode> nodeList;
	
	public static HashRingNode getSuccessorNode (final double hashKey, List<HashRingNode> nodeList)
	{
		for (int i = 0; i < nodeList.size(); i++)
		{
			if (hashKey <= nodeList.get(i).getHashKey())
			{
				return nodeList.get(i);
			}
		}
		return nodeList.get(0);
	}
	
	public static Graph<HashRingNode, DefaultEdge> generateHashRingGraph(final int n, final boolean isDirected)
	{
		
		Graph<HashRingNode, DefaultEdge> hashRing = null;
		if (isDirected == true)
		{
			hashRing = new DefaultDirectedGraph<HashRingNode, DefaultEdge>(DefaultEdge.class);
		}
		else
		{
			throw new UnsupportedOperationException("The undirected graphs case is unimplemented yet");
		}
		
		hashRing = initWithNodesOnly(n, hashRing);
		addEdges(hashRing);
		return hashRing;
		
	}
	
	private static void addEdges(final Graph<HashRingNode, DefaultEdge> hashRing)
	{
		Collections.sort(nodeList);
		
		for (int i = 0; i < nodeList.size()-1; i++)
		{
			hashRing.addEdge(nodeList.get(i), nodeList.get(i+1));
		}
		hashRing.addEdge(nodeList.get(nodeList.size()-1), nodeList.get(0));
	}

	private static Graph<HashRingNode, DefaultEdge> initWithNodesOnly(final int n, final Graph<HashRingNode, DefaultEdge> hashRing)
	{
		HashRingNode nodeToAdd = null;
		nodeList = new ArrayList<HashRingNode>();
		
		for (int i = 0; i < n; i++)
		{
			do
			{
				nodeToAdd = new HashRingNode(Math.random());
			}
			while (hashRing.addVertex(nodeToAdd) == false);
			nodeList.add(nodeToAdd);
		}
		
		return hashRing;
	}
}
