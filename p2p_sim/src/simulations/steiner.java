package simulations;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import java.lang.Integer;
import graph_generators.*;
import graph_generators.HashRingNode.Role;
public class steiner {

	public static double steinerApprox(Graph<HashRingNode,DefaultEdge> ring)
	{
		Set<DefaultWeightedEdge> es;
		Pseudograph<HashRingNode, DefaultWeightedEdge> tmp = new Pseudograph<>(DefaultWeightedEdge.class);
		init(tmp,ring);
		Pseudograph<HashRingNode,DefaultWeightedEdge> GC = getGC(tmp);
		System.gc();
		KruskalMinimumSpanningTree<HashRingNode,DefaultWeightedEdge> k = 
				new KruskalMinimumSpanningTree<>(computeSubGraph(GC));
		es = k.getEdgeSet();
		tmp = getSteinerTree(es, tmp, GC);
		return tmp.edgeSet().size();
	}
	
	private static Pseudograph<HashRingNode, DefaultWeightedEdge> getSteinerTree
		(Set<DefaultWeightedEdge> edges, Pseudograph<HashRingNode, DefaultWeightedEdge> graph,
				Pseudograph<HashRingNode, DefaultWeightedEdge>	GC
			)
	{
		Pseudograph<HashRingNode, DefaultWeightedEdge> $ = new Pseudograph<>(DefaultWeightedEdge.class);
		for(DefaultWeightedEdge e : edges)
		{
			List<DefaultWeightedEdge> d;
			d=DijkstraShortestPath.findPathBetween(graph, GC.getEdgeSource(e), GC.getEdgeTarget(e));
			for(DefaultWeightedEdge a : d)
			{
				HashRingNode n1 = graph.getEdgeSource(a);
				HashRingNode n2 = graph.getEdgeTarget(a);
				if(!$.containsVertex(n1))
					$.addVertex(n1);
				if(!$.containsVertex(n2))
					$.addVertex(n2);
				if(!$.containsEdge(n1,n2))
					$.addEdge(n1, n2);
			}	
		}
		return $;
	}
	private static Pseudograph<HashRingNode, DefaultWeightedEdge> 
				computeSubGraph(Pseudograph<HashRingNode,DefaultWeightedEdge> GC )
	{
		LinkedList<DefaultWeightedEdge> edgesToReomve = new LinkedList<>();
		for(DefaultWeightedEdge e : GC.edgeSet())
		{
			if(
					GC.getEdgeSource(e).getRole()==Role.NONE ||
					GC.getEdgeTarget(e).getRole() == Role.NONE
					)
			{
				edgesToReomve.add(e);
			}
		}
		for(DefaultWeightedEdge e : edgesToReomve)
		{
			GC.removeEdge(e);
		}
		edgesToReomve=null;
		LinkedList<HashRingNode> verticesToReomve = new LinkedList<>();
		for(HashRingNode n : GC.vertexSet())
		{
			if(n.getRole()==HashRingNode.Role.NONE)
			{
				verticesToReomve.add(n);
			}
		}
		for(HashRingNode n : verticesToReomve)
		{
			GC.removeVertex(n);
		}
		
		return GC;
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
	private static Pseudograph<HashRingNode,DefaultWeightedEdge> getGC(Graph<HashRingNode,DefaultWeightedEdge> ring)
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
