package simulations;

import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;
import graph_generators.HashRingNode.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

public class TreeStatistics {
	public Map<HashRingNode,Integer> distances = null;
	public Map<Integer,Integer> histogram = new HashMap<Integer,Integer>();
	public int max = -1;
	public int min = 0;
	public int numOfSteinerMessages=0;
	public double avg = 0 ;
	public  TreeStatistics(HashRingNode root,
			DefaultDirectedGraph<HashRingNode, DefaultEdge> tree)
	{
		BreadthFirstIterator<HashRingNode, DefaultEdge> bfs = 
				new BreadthFirstIterator<>(tree,root);
		distances = new HashMap<HashRingNode, Integer>
					(tree.vertexSet().size());
		HashMap<HashRingNode,Integer> steinerMessages = new HashMap<HashRingNode,Integer>
				(tree.vertexSet().size()); 
		distances.put(root, 0);
		steinerMessages.put(root,0);
		bfs.next();
		while(bfs.hasNext())
		{
			HashRingNode n = bfs.next();
			HashRingNode minimalNeighbour = getMinimalNeighbour(distances, n, tree);
			distances.put(n, distances.get(minimalNeighbour)+1);
			if(minimalNeighbour.getRole() == Role.NONE)
				steinerMessages.put(n, steinerMessages.get(minimalNeighbour)+1);
			else
				steinerMessages.put(n,steinerMessages.get(minimalNeighbour));
		}
		min = tree.vertexSet().size();
		HashRingNode[] keySetArray = new HashRingNode[distances.keySet().size()];
		keySetArray = distances.keySet().toArray(keySetArray);
		for(int j = 0; j < keySetArray.length; j++/*HashRingNode n : distances.keySet()*/)
			if(keySetArray[j].getRole() != Role.SUBSCRIBER)
				distances.remove(keySetArray[j]);
		for(Integer i : distances.values())
		{
			if(i<min)
				min = i;
			if(i> max)
				max = i;
			if(!histogram.containsKey(i))
				histogram.put(i, 0);
			histogram.put(i, histogram.get(i)+1);
			avg += i;
		}
		avg = avg/distances.size();
		for(Integer i : steinerMessages.values())
			numOfSteinerMessages+= i;
	}
	public int getMax()
	{
		return max;
	}
	public int getMin()
	{
		return min;
	}
	public double getAvg()
	{
		return avg;
	}
	public Map<Integer,Integer> getHistogram()
	{
		return histogram;
	}
	public int getMBUSteinerMessages()
	{
		return numOfSteinerMessages;
	}
	private static HashRingNode getMinimalNeighbour(Map<HashRingNode,Integer> distances,
			HashRingNode node , DefaultDirectedGraph<HashRingNode,DefaultEdge> tree)
	{
		int min = tree.vertexSet().size()+1;
		HashRingNode minNode = null;
		for(DefaultEdge n : tree.incomingEdgesOf(node))
			if(distances.containsKey(tree.getEdgeSource(n)) && 
					distances.get(tree.getEdgeSource(n)) < min)
			{
				min = distances.get(tree.getEdgeSource(n));
				minNode = tree.getEdgeSource(n);
			}
		return minNode;
	}
}
