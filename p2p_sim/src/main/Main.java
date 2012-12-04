package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jgrapht.Graph;
import org.jgrapht.ext.VisioExporter;
import org.jgrapht.graph.DefaultEdge;

import graph_generators.HashRingGenerator;
import graph_generators.HashRingNode;
import graph_generators.SymphonyGenerator;

public class Main
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		Graph<HashRingNode, DefaultEdge> g = SymphonyGenerator.generateSymphonyGraph(10, 2, true);
		VisioExporter<HashRingNode,DefaultEdge> e = new VisioExporter<HashRingNode,DefaultEdge>();
		File file = new File("C:\\Users\\seddie\\Documents\\out.csv");
		e.export(new FileOutputStream(file), g);
		
	}

}
