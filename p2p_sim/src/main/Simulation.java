package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import javax.management.BadAttributeValueExpException;

import graph_generators.HashRingNode;
import graph_generators.SymphonyGenerator;

import model.AbstractPubSubModel;
import model.DirectedBroadcastModel;
import model.MulticastModel;

import org.jgrapht.Graph;
import org.jgrapht.ext.VisioExporter;
import org.jgrapht.graph.DefaultEdge;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;

public class Simulation
{

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws BadAttributeValueExpException 
	 */
	public static void main(String[] args) throws FileNotFoundException, BadAttributeValueExpException
	{
		Graph<HashRingNode, DefaultEdge> g = SymphonyGenerator.generateSymphonyGraph(10000, 4, true);
		
		VisioExporter<HashRingNode,DefaultEdge> e = new VisioExporter<HashRingNode,DefaultEdge>();
		File file = new File("out.csv");
		e.export(new FileOutputStream(file), g);
		
		AbstractPubSubModel model = new MulticastModel(g, 0.2);
		//AbstractPubSubModel model = new DirectedBroadcastModel(g, 0.2);
		Experiment exp = new Experiment("Experiment1");
		
		model.connectToExperiment(exp);
		exp.setShowProgressBar(true);
		exp.stop(new TimeInstant(10, TimeUnit.MINUTES));
		exp.tracePeriod(new TimeInstant(0), new TimeInstant(0, TimeUnit.SECONDS));
		exp.debugPeriod(new TimeInstant(0), new TimeInstant(0, TimeUnit.MINUTES));
		exp.start();
		exp.report();
		exp.finish();
		
	}

}
