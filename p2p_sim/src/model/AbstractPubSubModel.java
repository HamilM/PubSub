package model;

import graph_generators.HashRingNode;
import graph_generators.HashRingTraverser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.management.BadAttributeValueExpException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import model.dynamic_types.Message;
import model.utils.RoutingTable;

import desmoj.core.simulator.*;
import desmoj.core.dist.*;

public abstract class AbstractPubSubModel extends Model 
{
	/*
	 * Constants
	 */
	final public TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS; //In what units we count time.
	final protected double MESSAGE_ARRIVAL_DIST_PARAM = 1000;	  //Time unit for the distribution.
	final protected String MESSAGE_QUEUE_NAME = "MessageQueue"; //Name to assign to message queues
	
	/*
	 * Graph members
	 */
	
	protected Graph<HashRingNode, DefaultEdge> graph;
	protected ArrayList<HashRingNode> nodeList;
	protected HashRingTraverser hashRingTraverser;
	protected RoutingTable routingTable;
	protected boolean[] isNodeSubscriber;		/*Is the node represented in the nodeList by
												 *the chosen index a subscriber*/
	protected ArrayList<Integer> subscribersIdList;
	protected long publisherIndex;			/*The node represented in the nodeList by
											  the index the publisher*/
	protected double numOfSubs;
	
	public DefaultDirectedGraph<HashRingNode, DefaultEdge> networkTree; 
	
	/*
	 * Static Model Components
	 */
	protected HashMap<Double, Queue<Message>> messageQueues;
	protected ContDistExponential messageArrivalTime;
	protected DiscreteDistUniform messageTargetNode;
	
	public AbstractPubSubModel(final Graph<HashRingNode, DefaultEdge> graph, final double subToAllRatio) throws BadAttributeValueExpException 
	{
		super(	null,	//Owner of the model 
				"PubSub Simulation",	//Name of the model 
				true,	//showInReport flag
				true	//showInTrace flag
					);
		
		if (subToAllRatio <= 0 || subToAllRatio >= 1)
		{
			throw new BadAttributeValueExpException(subToAllRatio);
		}
		
		this.graph = graph;
		this.messageQueues = new HashMap<Double, Queue<Message>>();
		this.nodeList = new ArrayList<HashRingNode>(graph.vertexSet());
		this.numOfSubs = subToAllRatio*nodeList.size();
		Collections.sort(nodeList);
		this.hashRingTraverser = new HashRingTraverser(nodeList, graph);
		this.isNodeSubscriber = new boolean[nodeList.size()];
		this.subscribersIdList = new ArrayList<Integer>();
		this.routingTable = new RoutingTable(nodeList.size(), hashRingTraverser);
	}
	
	public RoutingTable getRoutingTable()
	{
		return routingTable;
	}

	public HashRingTraverser getHashRingTraverser()
	{
		return hashRingTraverser;
	}

	public Graph<HashRingNode, DefaultEdge> getGraph()
	{
		return graph;
	}

	public ArrayList<HashRingNode> getNodeList()
	{
		return nodeList;
	}

	public boolean IsNodeSubscriber(int index)
	{
		return isNodeSubscriber[index];
	}
	
	public ArrayList<Integer> getSubscribersIdList()
	{
		return subscribersIdList;
	}

	public int getPublisherIndex()
	{
		return (int)publisherIndex;
	}



	public double getMessageArrivalTime()
	{
		return messageArrivalTime.sample();
	}
	
	public long getMessageTargetNode()
	{
		return messageTargetNode.sample();
	}

	public Queue<Message> getMessageQueue(double nodeId)
	{
		return messageQueues.get(nodeId);
	}

	@Override
	public String description() 
	{
		return "TODO add description";
	}

	@Override
	public void init() 
	{
		initDistributions();					
		initMessageQueues();
		initPublishersAndSubscribers();
	}

	private void initPublishersAndSubscribers()
	{
		publisherIndex = getMessageTargetNode();
		hashRingTraverser.getNodeById((int)publisherIndex).setRole(HashRingNode.Role.PUBLISHER);
		long subCount = 0;
		long current = -1;
		while (subCount < numOfSubs)
		{
			current = getMessageTargetNode();
			if (current != publisherIndex && isNodeSubscriber[(int) current] == false)
			{
				subCount++;
				isNodeSubscriber[(int)current] = true;
				subscribersIdList.add((int)current);
				hashRingTraverser.getNodeById((int)current).setRole(HashRingNode.Role.SUBSCRIBER);
			}
		}
		
		return;
		
	}

	private void initDistributions()
	{
		messageTargetNode = new DiscreteDistUniform(	this,					//Owner model 
														"MessageTargetNode", 	//Name of stream
														0, 						//Min Value
														graph.vertexSet().size()-1, //Max Value
														true, 					//Show in report
														true);					//Show in Trace
		
		messageArrivalTime = new ContDistExponential(	this,					//Owner Model 
														"MessageArrivalTime", 	//Name of stream
														MESSAGE_ARRIVAL_DIST_PARAM, //dist parameter
														true, 					//Show in report
														true					//Show in trace
																);
		messageArrivalTime.setNonNegative(true);
	}

	private void initMessageQueues()
	{
		
		for (int i = 0; i < nodeList.size(); i++)
		{
			messageQueues.put(nodeList.get(i).getHashKey(), new Queue<Message>(this, MESSAGE_QUEUE_NAME+i,true,true));
		}
	}

}
