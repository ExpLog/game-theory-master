package utils;

import data.Edge;
import data.Node;
import data.TransportationInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
		while (!line.equals("S")) {
			sc = new Scanner(line);
			sc.useLocale(Locale.ENGLISH);
			int source = sc.nextInt();
			int sink = sc.nextInt();
			double varCost = sc.nextDouble();
			double fixedCost = sc.nextDouble();
			edges.add(new Edge(source, sink, varCost, fixedCost));
			sc.close();
			line = reader.readLine();
		}
		// SOURCE NODES
		line = reader.readLine();
		while (!line.equals("D")) {
			sc = new Scanner(line);
			sc.useLocale(Locale.ENGLISH);
			int source = sc.nextInt();
			double resource = sc.nextDouble();
			sources.add(new Node(source, resource));
			line = reader.readLine();
		}

		line = reader.readLine();
		// SINK NODES
		while (reader.ready()) {
			sc = new Scanner(line);
			sc.useLocale(Locale.ENGLISH);
			int sink = sc.nextInt();
			double demand = sc.nextDouble();
			sinks.add(new Node(sink, demand));
			line = reader.readLine();
		}
		reader.close();
		
		TransportationInstance ti = new TransportationInstance(name, sources, sinks,edges);
		return ti;
	}
}
