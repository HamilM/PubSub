package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jgrapht.Graph;
import org.jgrapht.ext.VisioExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;

import simulations.steiner;

import graph_generators.ChordGenerator;
import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;
import graph_generators.HashRingNode.Role;
import graph_generators.SymphonyGenerator;

public class Main
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
//		Graph<HashRingNode, DefaultEdge> g = SymphonyGenerator.generateSymphonyGraph(100, 2, true);
//		VisioExporter<HashRingNode,DefaultEdge> e = new VisioExporter<HashRingNode,DefaultEdge>();
//		File file = new File("C:\\Users\\seddie\\Documents\\out.csv");
//		e.export(new FileOutputStream(file), g);
		Graph<HashRingNode, DefaultEdge> x = ChordGenerator.generateChord(50, 1);
//		System.out.println(x);
		int k = 0;
		for(HashRingNode i : x.vertexSet())
		{
			if(k==0 || k==16 || k== 32)
				i.setRole(Role.SUBSCRIBER);
			++k;
		}
		System.out.println(steiner.steinerApprox(x));
	}

}
