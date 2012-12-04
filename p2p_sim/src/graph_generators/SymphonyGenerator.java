package graph_generators;
import java.util.ArrayList;
import java.util.Collections;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public abstract class SymphonyGenerator extends HashRingGenerator
{
	private static int k;
	
	public static Graph<HashRingNode, DefaultEdge> generateSymphonyGraph(final int n, final int k, final boolean isDirected)
	{
		SymphonyGenerator.k = k;
		Graph<HashRingNode, DefaultEdge> hashRing = generateHashRingGraph(n, isDirected);
		hashRing = createLongDistanceLinks(n, k, hashRing);
		
		return hashRing;
	}
	
	private static Graph<HashRingNode, DefaultEdge> createLongDistanceLinks(	final int 							 	n,
																				final int 							 	k, 
																				final Graph<HashRingNode, DefaultEdge> 	hashRing)
	{
		ArrayList<HashRingNode> nodeList = new ArrayList<HashRingNode>(hashRing.vertexSet());
		Collections.sort(nodeList);
		
		for (HashRingNode node : nodeList)
		{
			addLongDistanceLinksToNode(n, node, nodeList, hashRing);
		}
		
		return hashRing;
		
	}
	
	private static void addLongDistanceLinksToNode(	final int								n,
													final HashRingNode 						node,
													final ArrayList<HashRingNode> 			nodeList,
													final Graph<HashRingNode, DefaultEdge> 	hashRing)
	{
		HashRingNode successor = null;
		while (isLegalNumOfOutLinks(node, hashRing) == true)
		{
			while(true)
			{
				successor = HashRingGenerator.getSuccessorNode(pdf(n), nodeList);
				if (successor.equals(node) || isLegalInNumOfLinks(successor, hashRing) == false)
				{
					continue;
				}
				
				if (hashRing.addEdge(node, successor) != null)
				{
					break;
				}
			}
		}
	}

	private static double pdf(final int n)
	{
		if (n<=0)
		{
			throw new IllegalArgumentException("n should be larger than 0");
		}
		double x = Math.random();
		if (x < 1/(double)n)
		{
			return 0;
		}
		double result =  Math.exp(Math.log(n)*(x-1));
		if (result > 1)
		{
			throw new IllegalStateException();
		}
		return result;
	}
	
	private static boolean isLegalNumOfOutLinks(final HashRingNode node, Graph<HashRingNode, DefaultEdge> hashRing)
	{
		return (((AbstractBaseGraph<HashRingNode, DefaultEdge>)hashRing).outDegreeOf(node) <= k); 
	}
	
	private static boolean isLegalInNumOfLinks(final HashRingNode node, Graph<HashRingNode, DefaultEdge> hashRing)
	{
		return (((AbstractBaseGraph<HashRingNode, DefaultEdge>)hashRing).inDegreeOf(node) <= 2*k); 
	}
}
