package graph_generators;


import java.util.SortedSet;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;

public class ChordGenerator extends HashRingGenerator {
	private static int successorLength=1;
	public static void setSuccessorLength(int ui)
	{
		if(ui<=0)
		{
			throw(new IllegalArgumentException());
		}
		successorLength = ui;
	}
	public static int getSuccessorLength()
	{
		return successorLength;
	}
	public static Graph<HashRingNode, DefaultEdge> generateChord(int numberOfNodes,int successorLen)
	{
		if(successorLen<=0 || numberOfNodes <= 0)
			throw new IllegalArgumentException("Non positive argument to generateChord!");
		Graph<HashRingNode,DefaultEdge> $ = HashRingGenerator.generateHashRingGraph(numberOfNodes, true);
		addFingers($);
		HashRingGenerator.addSuccessors($, successorLen);
		return $;
	}
	private static void addFingers(Graph<HashRingNode,DefaultEdge> graph)
	{
		SortedSet<HashRingNode> s = new TreeSet<>(graph.vertexSet());
		for(HashRingNode node: graph.vertexSet())
		{
			for(int i = 0 ; i < 64 ; ++i)
			{
				double next = (node.getHashKey()+((double)1)/(1<<64-i))%1;
				HashRingNode successor = s.tailSet(new HashRingNode(next)).first();
				if(!graph.containsEdge(node, successor))
					graph.addEdge(node,successor);
			}
		}
	}
}
