package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.BadAttributeValueExpException;
import javax.xml.ws.Service.Mode;

import graph_generators.ChordGenerator;
import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;
import graph_generators.SymphonyGenerator;

import model.AbstractPubSubModel;
import model.BroadcastMulticastModel;
import model.CSHModel;
import model.DirectedBroadcastModel;
import model.MulticastModel;

import org.jfree.io.FileUtilities;
import org.jgrapht.Graph;
import org.jgrapht.ext.VisioExporter;
import org.jgrapht.graph.DefaultEdge;

import simulations.TreeStatistics;
import simulations.steiner;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;
import desmoj.extensions.experimentation.util.FileUtil;

public class Simulation
{

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws BadAttributeValueExpException 
	 */
	public static final int ChordNum = 1;
	private static final File file = new File("results.csv");
	private static PrintStream out;
	public static final int SymphonyNum = 0;
	public static void main(String[] args) throws FileNotFoundException, BadAttributeValueExpException
	{
		out = new PrintStream(new FileOutputStream(file));
		out.println("Iteration,Overlay,Nodes,Algorithm,k,Subscribers,Successors,LDL,Maxpath,Minpath,Avgpath,Messages,Steinger_Messages,Steiner_Approximation");
		for(int iteration = 0; iteration < 4; iteration++) 
		{
		for(int overlay = 0 ; overlay < 2 ; ++overlay)
		{
			for( int nodes = 16 ; nodes < 1<<17 ; nodes*=2 )
			{
				for( double subscribers = 0.05 ; subscribers < 1 ; subscribers+=0.1)
				{
					int[] successors = new int[2];
					successors[0] = 1;
					successors[1] = (int) (Math.log(nodes)/Math.log(2));
					for(int successor : successors)
					{
						if(overlay == ChordNum) 
							runAlgorithms(ChordGenerator.generateChord(nodes, successor), subscribers,nodes,successor,"Chord",-1,iteration);
						else 
						{
							int[] longDistanceLinks = new int[3];
							longDistanceLinks[0] = 1;
							longDistanceLinks[1] = 4;
							longDistanceLinks[2] = (int) (Math.log(nodes)/Math.log(2));
							for(int links : longDistanceLinks)
								runAlgorithms(SymphonyGenerator.generateSymphonyGraph(nodes, links, true, successor), subscribers,nodes,successor,"Symphony",links,iteration);
						}
					}
					out.flush();
				}
			}
		}
		}
		out.close();
	}
	private static void runAlgorithms(Graph<HashRingNode,DefaultEdge> graph, double subscribers,int nodes,int successor,String overlay ,int LDL ,int iteration) throws BadAttributeValueExpException, FileNotFoundException
	{
		Experiment exp = new Experiment("Experiment1");
		AbstractPubSubModel DBModel = new DirectedBroadcastModel(graph, subscribers);
		out.println(iteration+","+overlay+","+nodes+","+"BCF,"+"-1,"+subscribers+","+successor+","+LDL+","+runExperiment(DBModel, exp,false));
		exp = new Experiment("Experiment2");
		AbstractPubSubModel MModel =  new MulticastModel(graph, subscribers);
		out.println(iteration+","+overlay+","+nodes+","+"MBU,"+"-1,"+subscribers+","+successor+","+LDL+","+runExperiment(MModel, exp,true));

		int[] k = {1,2,4, 8, 16};
		for (int i = 0; i < k.length; i++) {
			exp = new Experiment("Experiment"+(i+3));
			AbstractPubSubModel cshModel = new CSHModel(graph, subscribers, k[i]);
			out.println(iteration+","+overlay+","+nodes+","+"CSH,"+k[i]+","+subscribers+","+successor+","+LDL+","+runExperiment(cshModel, exp,false));
			//testCSH((CSHModel)cshModel); DON'T DELETE
		}
		return;
	}
	
	// making sure csh reaches all subscribers
	private static void testCSH(CSHModel model) {
		Set<HashRingNode> reachedNodes = model.networkTree.vertexSet();
		Set<HashRingNode> nodesInGraph = model.getGraph().vertexSet();
		Set<HashRingNode> subscriberNodes = new HashSet<HashRingNode>();
		for (HashRingNode node : nodesInGraph) {
			if (node.getRole() == HashRingNode.Role.SUBSCRIBER) subscriberNodes.add(node);
		}
		if (reachedNodes.containsAll(subscriberNodes)) System.out.println("Well Done");
		else System.out.println("oh no");
		return;
	}
	
	private static String runExperiment(AbstractPubSubModel model, Experiment exp,boolean isMBU)
	{
		model.connectToExperiment(exp);
		exp.setShowProgressBar(false);
		exp.stop(new TimeInstant(10, TimeUnit.MINUTES));
		exp.tracePeriod(new TimeInstant(0), new TimeInstant(0, TimeUnit.SECONDS));
		exp.debugPeriod(new TimeInstant(0), new TimeInstant(0, TimeUnit.MINUTES));
		exp.start();
		exp.report();
		exp.finish();
		TreeStatistics s = new TreeStatistics(	model.getHashRingTraverser().
				getNodeById(model.getPublisherIndex()),
			model.networkTree);
		int approx = -1;
		if(model.getGraph().vertexSet().size() <= 1<<11)
		{
			approx = steiner.steinerApprox(model.getGraph());
		}
		String $;
		if(isMBU)
			$ = s.getMax()+","+s.getMin()+","+s.getAvg()+","+s.getSumMBU()+","+s.getMBUSteinerMessages()+","+approx;
		else
			$ = s.getMax()+","+s.getMin()+","+s.getAvg()+","+model.networkTree.edgeSet().size()+
				","+(model.networkTree.edgeSet().size()-model.getSubscribersIdList().size())+","+approx;
		return $;
	}

}
//Graph<HashRingNode, DefaultEdge> g = SymphonyGenerator.generateSymphonyGraph(20, 2, true,  2);
//
//VisioExporter<HashRingNode,DefaultEdge> e = new VisioExporter<HashRingNode,DefaultEdge>();
//File file = new File("out.csv");
//e.export(new FileOutputStream(file), g);
//
//AbstractPubSubModel model = new MulticastModel(g, 0.2);
////AbstractPubSubModel model = new DirectedBroadcastModel(g, 0.2);
//Experiment exp = new Experiment("Experiment1");
//model.connectToExperiment(exp);
//exp.setShowProgressBar(true);
//exp.stop(new TimeInstant(10, TimeUnit.MINUTES));
//exp.tracePeriod(new TimeInstant(0), new TimeInstant(0, TimeUnit.SECONDS));
//exp.debugPeriod(new TimeInstant(0), new TimeInstant(0, TimeUnit.MINUTES));
//exp.start();
//exp.report();
//exp.finish();
//
//file = new File("tree.csv");
//e.export(new FileOutputStream(file), model.networkTree);
//TreeStatistics s = new TreeStatistics(	model.getHashRingTraverser().
//											getNodeById(model.getPublisherIndex()),
//										model.networkTree);
////System.out.println(model.getSubscribersIdList());
//System.out.println("Average distance = " + s.avg + ", Min distance = " + s.min + ", Max distance = " + s.max);

