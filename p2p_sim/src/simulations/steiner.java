package simulations;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import java.lang.Integer;
import graph_generators.*;
public class steiner {

	public static double steinerApprox(Graph<HashRingNode,DefaultEdge> ring)
	{
		Pseudograph<HashRingNode, DefaultWeightedEdge> tmp = new Pseudograph<>(DefaultWeightedEdge.class);
		init(tmp,ring);
		Graph<HashRingNode,DefaultWeightedEdge> GC = getGC(tmp);
		tmp = computeSubGraph(GC);
		System.out.println(tmp);
		for(DefaultWeightedEdge n : tmp.edgeSet())
		{
			System.out.println(n+","+tmp.getEdgeWeight(n));
		}
		KruskalMinimumSpanningTree<HashRingNode,DefaultWeightedEdge> k = 
				new KruskalMinimumSpanningTree<>(tmp);
		return k.getSpanningTreeCost();
	}
	private static Pseudograph<HashRingNode, DefaultWeightedEdge> 
				computeSubGraph(Graph<HashRingNode,DefaultWeightedEdge> GC )
	{
		Pseudograph<HashRingNode, DefaultWeightedEdge> tmp = 
				new Pseudograph<>(DefaultWeightedEdge.class);
		for(HashRingNode n : GC.vertexSet())
		{
			if(n.getRole()!=HashRingNode.Role.NONE)
			{
				tmp.addVertex(n);
			}
		}
		for(HashRingNode n : tmp.vertexSet())
		{
			for(DefaultWeightedEdge e : GC.edgesOf(n))
			{
				if(
						!tmp.containsEdge(tmp.getEdgeSource(e),tmp.getEdgeTarget(e)) &&
						tmp.vertexSet().contains(tmp.getEdgeSource(e))&&
						tmp.vertexSet().contains(tmp.getEdgeTarget(e))
						)
				{
					tmp.setEdgeWeight(tmp.addEdge(GC.getEdgeSource(e), GC.getEdgeTarget(e)),GC.getEdgeWeight(e));
				}
			}
		}
		return tmp;
	}
	private static void init(Graph<HashRingNode,DefaultWeightedEdge> tmp, Graph<HashRingNode,DefaultEdge> ring)
	{
		for(HashRingNode i : ring.vertexSet())
		{
			tmp.addVertex(i);
		}
		for(DefaultEdge j: ring.edgeSet())
		{
			tmp.addEdge(ring.getEdgeSource(j),ring.getEdgeTarget(j));
		}
	}
	private static Graph<HashRingNode,DefaultWeightedEdge> getGC(Graph<HashRingNode,DefaultWeightedEdge> ring)
	{
		Pseudograph<HashRingNode,DefaultWeightedEdge> $ = new Pseudograph<>(DefaultWeightedEdge.class);
		for(HashRingNode i : ring.vertexSet())
		{
			$.addVertex(i);
		}
		for(HashRingNode i : $.vertexSet())
		{
			Map<HashRingNode,Integer> m = BFS(i,ring);
			for(HashRingNode j : m.keySet())
			{
				$.setEdgeWeight($.addEdge(i, j), m.get(j));
			}
			
		}
		
		return $;
		
	}
	
	private static Map<HashRingNode, Integer> BFS(HashRingNode src, Graph<HashRingNode,DefaultWeightedEdge> ring)
	{
		Map<HashRingNode,Integer> $ = new HashMap<>(ring.vertexSet().size());
		BreadthFirstIterator<HashRingNode, DefaultWeightedEdge> i = new BreadthFirstIterator<>(ring,src);
		try{
			while(true)
			{
				HashRingNode t = i.next();
				Iterator<DefaultWeightedEdge> edges = ring.edgesOf(t).iterator();
				$.put(t, getMinNeighbour(edges, $, ring)+1);
			}
		}
		catch(NoSuchElementException x){};
		return $;
	}
	/**
	 * @param edges
	 * @param m
	 * @param g
	 * @return minimum value from all the vertices in edges in graph g which have a value in m.
	 */
	private static int getMinNeighbour(	Iterator<DefaultWeightedEdge> edges,
										Map<HashRingNode,Integer> m,
										Graph<HashRingNode,DefaultWeightedEdge> g)
	{
		int min = Integer.MAX_VALUE;
		try{
			while(true)
			{
				DefaultWeightedEdge e = edges.next();
				HashRingNode n1 = g.getEdgeSource(e);
				HashRingNode n2 = g.getEdgeTarget(e);
				if(m.containsKey(n1))
				{
					min = min > m.get(n1) ? m.get(n1) : min;
				}
				if(m.containsKey(n2))
				{
					min = min > m.get(n2) ? m.get(n2) : min;
				}
			}
		}
		catch(NoSuchElementException x){};
		if(min == Integer.MAX_VALUE)
			min = -1;
		return min;
	}
}
