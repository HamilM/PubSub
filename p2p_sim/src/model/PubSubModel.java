package model;

import graph_generators.HashRingNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import model.dynamic_types.Message;

import desmoj.core.simulator.*;
import desmoj.core.dist.*;

public class PubSubModel extends Model 
{
	/*
	 * Constants
	 */
	final protected TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS; //In what units we count time.
	final protected double MESSAGE_ARRIVAL_DIST_PARAM = 50;	  //Time unit for the distribution.
	final protected String MESSAGE_QUEUE_NAME = "MessageQueue"; //Name to assign to message queues
	
	/*
	 * Graph members
	 */
	
	protected Graph<HashRingNode, DefaultEdge> graph;
	protected ArrayList<HashRingNode> nodeList;
	protected boolean[] isNodeSubscriber;		/*Is the node represented in the nodeList by
												 *the chosen index a subscriber*/
	protected long publisherIndex;			/*The node represented in the nodeList by
											  the index the publisher*/
	protected double numOfSubs;
	
	/*
	 * Static Model Components
	 */
	protected HashMap<Integer, Queue<Message>> messageQueues;
	protected ContDistExponential messageArrivalTime;
	protected DiscreteDistUniform messageTargetNode;

	public PubSubModel(final Graph<HashRingNode, DefaultEdge> graph, final long numOfSubs) 
	{
		super(	null,	//Owner of the model 
				"PubSub Simulation",	//Name of the model 
				true,	//showInReport flag
				true	//showInTrace flag
					);
		
		this.graph = graph;
		this.numOfSubs = numOfSubs;
		
		messageQueues = new HashMap<Integer, Queue<Message>>();
		nodeList = new ArrayList<HashRingNode>(graph.vertexSet());
		Collections.sort(nodeList);
		isNodeSubscriber = new boolean[nodeList.size()];
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

	public long getPublisherIndex()
	{
		return publisherIndex;
	}



	public double getMessageArrivalTime()
	{
		return messageArrivalTime.sample();
	}
	
	public long getMessageTargetNode()
	{
		return messageTargetNode.sample();
	}

	public Queue<Message> getMessageQueue(int nodeId)
	{
		return messageQueues.get(nodeId);
	}

	@Override
	public String description() 
	{
		return "This model simulates the Cord algorithm for distributed netowrks";
	}

	@Override
	public void doInitialSchedules() 
	{
		// TODO Auto-generated method stub

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
		
		long subCount = 0;
		long current = -1;
		while (subCount < numOfSubs)
		{
			current = getMessageTargetNode();
			if (current != publisherIndex && isNodeSubscriber[(int) current] == false)
			{
				subCount++;
				isNodeSubscriber[(int)current] = true;
			}
		}
		
		return;
		
	}

	private void initDistributions()
	{
		messageTargetNode = new DiscreteDistUniform(	this,					//Owner model 
														"MessageArrivalTime", 	//Name of stream
														0, 						//Min Value
														graph.vertexSet().size()-1, //Max Value
														true, 					//Show in report
														true);					//Show in Trace
		
		messageArrivalTime = new ContDistExponential(	this,					//Owner Model 
														"messageArrivalTime", 	//Name of stream
														MESSAGE_ARRIVAL_DIST_PARAM, //dist parameter
														true, 					//Show in report
														true					//Show in trace
																);
	}

	private void initMessageQueues()
	{
		
		for (int i = 0; i < nodeList.size(); i++)
		{
			messageQueues.put(i, new Queue<Message>(this, MESSAGE_QUEUE_NAME+"1",true,true));
		}
	}

}
