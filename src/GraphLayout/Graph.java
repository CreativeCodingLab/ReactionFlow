package GraphLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import main.Pathway2;

import org.biopax.paxtools.model.level3.BiochemicalReaction;

import processing.core.PApplet;

//Copyright 2005 Sean McCullough
//banksean at yahoo

public class Graph {
	public static ArrayList<Node> nodes;
	public ArrayList<Edge> edges;
	public HashMap<Node, ArrayList<Edge>> edgesFrom;
	public HashMap<Node, ArrayList<Edge>> edgesTo;

	public Node selectedNode = null;
	public Node dragNode = null;
	public Node hoverNode = null;
	public PApplet parent;

	public Graph() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		edgesFrom = new HashMap<Node, ArrayList<Edge>>();
		edgesTo = new HashMap<Node, ArrayList<Edge>>();
	}

	public void setSelectedNode(Node n) {
		selectedNode = n;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}

	public void setHoverNode(Node n) {
		hoverNode = n;
	}

	public Node getHoverNode() {
		return hoverNode;
	}

	public void setDragNode(Node n) {
		dragNode = n;
	}

	public Node getDragNode() {
		return dragNode;
	}

	public Vector3D getCentroid() {
		float x = 0;
		float y = 0;
		for (int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			x += n.getX();
			y += n.getY();
		}

	return new Vector3D(x / nodes.size(), y / nodes.size(), 0);
	}

	public void addEdge(Edge e) {
		edges.add(e);

		ArrayList<Edge> f = getEdgesFrom(e.getFrom());
		f.add(e);

		ArrayList<Edge> t = getEdgesTo(e.getTo());
		t.add(e);

		e.setGraph(this);
	}
	
		
	public ArrayList<Edge> getEdgesFrom(Node n) {
		ArrayList<Edge> f = (ArrayList<Edge>) edgesFrom.get(n);
		if (f == null) {
			f = new ArrayList<Edge>();
			edgesFrom.put(n, f);
		}
		return f;
	}

	public ArrayList<Edge> getEdgesTo(Node n) {
		ArrayList<Edge> t = (ArrayList<Edge>) edgesTo.get(n);
		if (t == null) {
			t = new ArrayList<Edge>();
			edgesTo.put(n, t);
		}
		return t;
	}

	public boolean isConnected(Node a, Node b) {
		Iterator<Edge> i;
		i = edges.iterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			if (e.getFrom() == a && e.getTo() == b || e.getFrom() == b
					&& e.getTo() == a) {
				return true;
			}
		}
		return false;
	}

	public void addNode(Node n) {
		nodes.add(n);
		n.setGraph(this);
	}

	// Tuan
	public void removeNode(Node n) {
		ArrayList<Edge> eFroms = edgesFrom.get(n);
		ArrayList<Edge> eTos = edgesTo.get(n);
		if (eFroms!=null){
			for (int i=0;i<eFroms.size();i++){
				Edge e = eFroms.get(i);
				edges.remove(e);
			}
		}
		edgesFrom.remove(n);

		if (eTos !=null){
			for (int i=0;i<eTos.size();i++){
				Edge e = eTos.get(i);
				edges.remove(e);
			}
		}
		edgesTo.remove(n);
		nodes.remove(n);
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	 public static Node getNodeByReaction(BiochemicalReaction react){
		  for (int i=0; i<nodes.size();i++){
			  Node node = nodes.get(i);
			  if (node.reaction.equals(react))
				  return node;
		  }
		  return null;
	  }

	public void drawEdges() {
		// Reset brushing node
		for (int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			n.isConnected = false;
		}

		for (int i = 0; i < edges.size(); i++) {
			Edge e = (Edge) edges.get(i);
			if (Pathway2.bEdges!=null && Pathway2.bEdges.size()>0){
				if (Pathway2.bEdges.contains(e)){
					e.draw();
					e.from.isConnected=true;  // to highlight only the nodes connected to the selected reactions
					e.to.isConnected = true;
				}
			}
			else{
				e.draw();
			}
		}
	}
	
	
	public void drawNodes() {
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).draw();
		}

	}
}
