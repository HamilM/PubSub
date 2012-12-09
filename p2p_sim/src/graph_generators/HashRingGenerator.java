package graph_generators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public abstract class HashRingGenerator
{
	protected static ArrayList<HashRingNode> nodeList;
	
	public static int getSuccessorNodeId (final double hashKey, final List<HashRingNode> nodeList)
	{
		for (int i = 0; i < nodeList.size(); i++)
		{
			if (hashKey <= nodeList.get(i).getHashKey())
			{
				return i;
			}
		}
		return 0;
	}
	
	public static int getPredcessorNodeId (final double hashKey, final List<HashRingNode> nodeList)
	{
		int successorId = getSuccessorNodeId(hashKey, nodeList);
		if (successorId == 0)
		{
			return nodeList.size()-1;
		}
		return successorId-1;
	}
	
	public static HashRingNode getSuccessorNode (final double hashKey, final List<HashRingNode> nodeList)
	{
		return nodeList.get(getSuccessorNodeId(hashKey, nodeList));
	}
	
	public static int getSuccessorNodeId (final HashRingNode node, final List<HashRingNode> nodeList)
	{
		int nodeId = getSuccessorNodeId(node.getHashKey(), nodeList);
		if (nodeId == nodeList.size()-1)
		{
			return 0;
		}
		return nodeId+1;
	}
	
	public static HashRingNode getSuccessorNode (final HashRingNode node, final List<HashRingNode> nodeList)
	{
		return nodeList.get(getSuccessorNodeId(node, nodeList));
	}
	
	public static HashRingNode getPredcessorNode (final double hashKey, final List<HashRingNode> nodeList)
	{
		return nodeList.get(getPredcessorNodeId(hashKey, nodeList));
	}
	
	/**
	 * Returns the id of the first node preceding destinationHashKey (if destinationHashKey is a node, it will start from his predecessor) 
	 * on the ring and has an incoming edge from currentNodeHashKey.
	 * @param currentNodeHashKey
	 * @param destinationHashKey
	 * @param nodeList
	 * @param graph
	 * @return
	 */
	public static int getClosestLinkedPredecessorId(final double currentNodeHashKey, final double destinationHashKey, 
															 final List<HashRingNode> nodeList, Graph<HashRingNode, DefaultEdge> graph)
	{
		int unlinkedPredecessorId = getPredcessorNodeId(destinationHashKey, nodeList);
		int currentNodeId = getSuccessorNodeId(currentNodeHashKey, nodeList);
		
		if (getSuccessorNode(destinationHashKey, nodeList).getHashKey() == destinationHashKey)
		{
			unlinkedPredecessorId = getSuccessorNodeId(destinationHashKey, nodeList);
		}
		
		for (int i = unlinkedPredecessorId; i >= 0; i--)
		{
			if (graph.containsEdge(nodeList.get(currentNodeId), nodeList.get(i)) || i == currentNodeId)
			{
				return i;
			}
		}
		
		for (int i = nodeList.size()-1; i > unlinkedPredecessorId; i--)
		{
			if (graph.containsEdge(nodeList.get(currentNodeId), nodeList.get(i)) || i == currentNodeId)
			{
				return i;
			}
		}
		return -1;
	}
	
	
	/**
	 * Returns the first node preceding destinationHashKey (if destinationHashKey is a node, it will start from his predecessor) 
	 * on the ring and has an incoming edge from currentNodeHashKey.
	 * @param currentNodeHashKey
	 * @param destinationHashKey
	 * @param nodeList
	 * @param graph
	 * @return
	 */
	public static HashRingNode getClosestLinkedPredecessor(final double currentNodeHashKey, final double destinationHashKey,
			 													final List<HashRingNode> nodeList, Graph<HashRingNode, DefaultEdge> graph)
	{
		return nodeList.get(getClosestLinkedPredecessorId(currentNodeHashKey, destinationHashKey, nodeList, graph));
	}
	
//	public static int getClosestLinkedSuccessorId(double currentNodeHashKey, double destinationHashKey, double upperbound,
//															ArrayList<HashRingNode> nodeList, Graph<HashRingNode, DefaultEdge> graph)
//	{
//		int unlinkedPredecessorId = getSuccessorNodeId(destinationHashKey, nodeList);
//		int currentNodeId = getPredcessorNodeId(currentNodeHashKey, nodeList);
//		int upperboundNodeId = getSuccessorNodeId(upperbound, nodeList);
//		
//		for (int i = unlinkedPredecessorId; i < nodeList.size(); i++)
//		{
//			if (i == upperboundNodeId)
//			{
//				return currentNodeId;
//			}
//			
//			if (graph.containsEdge(nodeList.get(currentNodeId), nodeList.get(i)) || i == currentNodeId)
//			{
//				return i;
//			}
//			
//		}
//		
//		for (int i = 0; i < unlinkedPredecessorId; i++)
//		{
//			if (i == upperboundNodeId)
//			{
//				return currentNodeId;
//			}
//			
//			if (graph.containsEdge(nodeList.get(currentNodeId), nodeList.get(i)) || i == currentNodeId)
//			{
//				return i;
//			}
//		}
//		return -1;
//	}
//	
//	public static HashRingNode getClosestLinkedSuccessor(double currentNodeHashKey, double destinationHashKey, double upperbound,
//															ArrayList<HashRingNode> nodeList, Graph<HashRingNode, DefaultEdge> graph)
//	{
//		return nodeList.get(getClosestLinkedSuccessorId(currentNodeHashKey, destinationHashKey, upperbound, nodeList, graph));
//	}
	
	/**
	 * Generates a hash ring graph with n vertexes.
	 * @param n
	 * @param isDirected	- Should the graph be directed?
	 * @return
	 */
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
	/**
	 * Assuming the input graph is a ring - add to each node edges to its k successors.
	 * @param graph
	 * @param k
	 * @author Matan
	 */
	private static void addSuccessors(Graph<HashRingNode,DefaultEdge> graph,int k)
	{
		if(k<=0)
		{
			throw new IllegalArgumentException("Can add only a positive number of successors!");
		}
		Set<DefaultEdge> edgesToAdd = new HashSet<>();
		HashRingNode next = null;
		for(HashRingNode node : graph.vertexSet())
		{
			HashRingNode tmp = getNext(graph, node);
			for(int i = 0 ; i<k-1 ; ++i)
			{
				if(!graph.containsEdge(node, tmp))
					graph.addEdge(node, tmp);
				tmp = getNext(graph, tmp);
			}
		}
	}
	
	/**
	 * @param ring A ring of nodes.
	 * @param node Node in the ring.
	 * @return The node following the given node in the graph.
	 * @Author Matan
	 */
	private static HashRingNode getNext(Graph<HashRingNode,DefaultEdge> ring,HashRingNode node)
	{
		if(ring.vertexSet().contains(node)==false)
		{
			throw new IllegalArgumentException("The node is not in the ring!");
		}
		for(DefaultEdge e : ring.edgesOf(node))
			if(ring.getEdgeSource(e) == node)
				return ring.getEdgeTarget(e);
		throw new IllegalArgumentException("The ring is not well formed!");
	}
}
