package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import data.Edge;
import data.Node;
import data.TransportationInstance;

public class InstanceConverter {

public static TransportationInstance convert(String srcPath) throws Exception {
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				srcPath)));

		List<Edge> edges = new ArrayList<Edge>();
		List<Node> sources = new ArrayList<Node>();
		List<Node> sinks = new ArrayList<Node>();

		// HEADER
		reader.readLine();//instance
		// INSTANCE INFO (name, sources, sinks)
		String info = reader.readLine();
		Scanner sc = new Scanner(info);
		
		String name = sc.next();
		sc.close();
		// ARCS
		reader.readLine();//ARCS
		
		String line = reader.readLine();
		StringBuilder edgesBuilder = new StringBuilder(line + "\n");
		while (!line.equals("S")) {
			
			line = reader.readLine();
			if(!line.equals("S")) {
				edgesBuilder.append(line + "\n");
			}
		}
		sc = new Scanner(edgesBuilder.toString());
		sc.useLocale(Locale.ENGLISH);
		while(sc.hasNext()) {
			int source = sc.nextInt();
			int sink = sc.nextInt();
			double varCost = sc.nextDouble();
			double fixedCost = sc.nextDouble();
			sc.nextLine();
			edges.add(new Edge(source, sink, varCost, fixedCost));
		}
		sc.close();
		// SOURCE NODES
		line = reader.readLine();
		StringBuilder sourceBuilder = new StringBuilder(line + "\n");
		while (!line.equals("D")) {
			
			line = reader.readLine();
			if(!line.equals("D")) {
				sourceBuilder.append(line + "\n");
			}
		}
		sc = new Scanner(sourceBuilder.toString());
		sc.useLocale(Locale.ENGLISH);
		while(sc.hasNext()) {
			int source = sc.nextInt();
			double resource = sc.nextDouble();
			sources.add(new Node(source, resource));
			sc.nextLine();
		}
		sc.close();
		
		line = reader.readLine();
		StringBuilder sinkBuilder = new StringBuilder(line + "\n");
		while (reader.ready()) {
			
			line = reader.readLine();
			if(!line.equals("END")) {
				sinkBuilder.append(line + "\n");
			}
		}
		sc = new Scanner(sinkBuilder.toString());
		sc.useLocale(Locale.ENGLISH);
		
		while(sc.hasNext()) {
			int sink = sc.nextInt();
			double demand = sc.nextDouble();
			sinks.add(new Node(sink, demand));
			sc.nextLine();
		}
		
		sc.close();

		reader.close();
		
		TransportationInstance ti = new TransportationInstance(name, sources, sinks,edges);
		return ti;
	}
}
