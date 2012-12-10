package simulations;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import graph_generators.*;
public class steiner {

	public static Graph<HashRingNode,DefaultEdge> steinerApprox(Graph<HashRingNode,DefaultEdge> ring)
	{
//		Graph<HashRingNode,DefaultEdge> GC = 
		return ring;
	}
	
	private Graph<HashRingNode,DefaultEdge> getGC(Graph<HashRingNode,DefaultEdge> ring)
	{
		Graph<HashRingNode,DefaultEdge> $ = new SimpleGraph<>(DefaultEdge.class);
		for(HashRingNode i : ring.vertexSet())
		{
			$.addVertex(i);
		}
		
		return $;
		
	}
	
	private static Map<HashRingNode, Integer> BFS(HashRingNode src, Graph<HashRingNode,DefaultEdge> ring)
	{
		Map<HashRingNode,Integer> $ = new HashMap<>(ring.vertexSet().size());
		
		return $;
	}
}
