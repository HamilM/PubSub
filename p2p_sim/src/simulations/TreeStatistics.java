package simulations;

import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.BreadthFirstIterator;

public class TreeStatistics {
	public static Map<Integer, Integer> getTreeStatistics(HashRingNode root,
			Graph<HashRingNode, DefaultEdge> tree)
	{
		BreadthFirstIterator<HashRingNode, DefaultEdge> bfs = 
				new BreadthFirstIterator<>(tree,root);
		Map<HashRingNode, Integer> distances = new HashMap<HashRingNode, Integer>
					(tree.vertexSet().size());
		distances.put(root, 0);
		bfs.next();
		while(bfs.hasNext())
		{
			HashRingNode node = bfs.next();
			List<HashRingNode> neighbors = Graphs.neighborListOf(tree, node);
			
		}
		return null;
	}
	private static int getMinimalNeighbour(Map<HashRingNode,Integer> distances,
			HashRingNode node , Graph<HashRingNode,DefaultEdge> tree)
	{
		
		return 0;
	}
}
