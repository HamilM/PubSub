package graph_generators;

import java.util.HashMap;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class HashRingTraverser
{
	
	protected List<HashRingNode> nodeList;
	protected HashMap<Double, Integer> keyToIdMap;
	protected Graph<HashRingNode, DefaultEdge> graph;
	
	public HashRingTraverser(final List<HashRingNode> nodeList, Graph<HashRingNode, DefaultEdge> graph)
	{	
		this.nodeList = nodeList;
		this.graph = graph;
		keyToIdMap = new HashMap<Double, Integer>();
		
		for (int i = 0; i < nodeList.size(); i++)
		{
			keyToIdMap.put(nodeList.get(i).getHashKey(), i);
		}
	}
	
	public Integer nodeKeyToId (double nodeHashKey)
	{
		return keyToIdMap.get(nodeHashKey);
	}
	
	public double nodeIdToNodeKey (int nodeId)
	{
		return nodeList.get(nodeId).getHashKey();
	}
	
	public int getSuccessorNodeId (final double hashKey)
	{
		int lower = 0;
		int upper = nodeList.size()-1;
		int middle = (upper-lower)/2;
		if (nodeList.get(lower).getHashKey() > hashKey || nodeList.get(upper).getHashKey() < hashKey)
		{
			return lower;
		}
		
		if (nodeList.get(upper-1).getHashKey() < hashKey)
		{
			return upper;
		}
		
		while (true)
		{
			if (nodeList.get(middle).getHashKey() == hashKey)
			{
				return middle;
			}
			
			if (nodeList.get(middle).getHashKey() < hashKey)
			{
				lower = middle;
			}
			else //nodeList.get(middle).getHashKey() > hashKey
			{
				if (nodeList.get((middle-1)%nodeList.size()).getHashKey() < hashKey)
				{
					return middle;
				}
				
				upper = middle;
			}
			middle = (upper-lower)/2 + lower;
		}
	}
	
	public int getSuccessorNodeId (final int id)
	{
		return (id+1)%nodeList.size();
	}
	
	public int getSuccessorNodeId (final HashRingNode node)
	{
		return nodeKeyToId(node.getHashKey());
	}
	
	public int getPredcessorNodeId (final double hashKey)
	{
		int successorId = getSuccessorNodeId(hashKey);
		if (successorId == 0)
		{
			return nodeList.size()-1;
		}
		return successorId-1;
	}
	
	public int getPredcessorNodeId (final int id)
	{
		return (id-1)%nodeList.size();
	}
	
	public HashRingNode getSuccessorNode (final double hashKey)
	{
		return nodeList.get(getSuccessorNodeId(hashKey));
	}
	
	public HashRingNode getSuccessorNode(final int id)
	{
		return nodeList.get(getSuccessorNodeId(id));
	}
	
	public HashRingNode getSuccessorNode (final HashRingNode node)
	{
		return nodeList.get(getSuccessorNodeId(node));
	}
	
	public HashRingNode getPredcessorNode (final double hashKey)
	{
		return nodeList.get(getPredcessorNodeId(hashKey));
	}
	
	public HashRingNode getPredcessorNode (final int id)
	{
		return nodeList.get(getPredcessorNodeId(id));
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
	public int getClosestLinkedPredecessorId(final double currentNodeHashKey, final double destinationHashKey)
	{
		int unlinkedPredecessorId;
		int currentNodeId = nodeKeyToId(currentNodeHashKey);
		
//		if (getSuccessorNode(destinationHashKey, nodeList).getHashKey() == destinationHashKey)
//		{
//			unlinkedPredecessorId = getSuccessorNodeId(destinationHashKey, nodeList);
//		}
		if (nodeKeyToId(destinationHashKey) != null)
		{
			unlinkedPredecessorId = nodeKeyToId(destinationHashKey);
		}
		else
		{
			unlinkedPredecessorId = getPredcessorNodeId(destinationHashKey);
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
	public HashRingNode getClosestLinkedPredecessor(final double currentNodeHashKey, final double destinationHashKey)
	{
		return nodeList.get(getClosestLinkedPredecessorId(currentNodeHashKey, destinationHashKey));
	}
	
}
