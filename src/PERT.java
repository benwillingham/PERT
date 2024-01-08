/* Starter code for PERT algorithm (Project 4)
 * @author rbk
 */

// change to your netid

// replace sxa173731 with your netid below
import btw190002.Graph;
import btw190002.Graph.Vertex;
import btw190002.Graph.Edge;
import btw190002.Graph.GraphAlgorithm;
import btw190002.Graph.Factory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
    LinkedList<Vertex> finishList;
	
    public static class PERTVertex implements Factory {
		// Add fields to represent attributes of vertices here
		int duration;
		int ec;
		int lc;
		int slack;
		boolean critical;

		public PERTVertex(Vertex u) {
			this.duration = 0;
			this.ec = 0;
			this.lc = 0;
			this.slack = 0;
			this.critical = false;
		}
	public PERTVertex make(Vertex u) { return new PERTVertex(u); }
    }

    // Constructor for PERT is private. Create PERT instances with static method pert().
    private PERT(Graph g) {
	super(g, new PERTVertex(null));
    }

    public void setDuration(Vertex u, int d) {
		get(u).duration = d;
    }

    // Implement the PERT algorithm. Returns false if the graph g is not a DAG.
    public boolean pert() {
		finishList = topologicalOrder();
		if (finishList == null) {
			return false; // Graph is not a DAG
		}

		int cpl = 0;

		for (Vertex u : g) {
			get(u).ec = 0;
		}
		for (Vertex u : finishList) {
			for (Edge e : g.inEdges(u)) {
				Vertex v = e.fromVertex();
				get(u).ec = Math.max(get(u).ec, get(v).ec + get(v).duration);
				cpl = Math.max(cpl, get(u).ec);
			}
		}

		for (Vertex u : g) {
			get(u).lc = cpl;
		}
		Collections.reverse(finishList);
		for (Vertex u : finishList) {
			for (Edge e : g.outEdges(u)) {
				Vertex v = e.toVertex();
				get(u).lc = Math.min(get(u).lc, get(v).lc - get(u).duration);
			}
		}

		// Calculate slack and identify critical tasks
		for (Vertex u : g) {
			get(u).slack = get(u).lc - get(u).ec;
			get(u).critical = get(u).slack == 0;
		}

		// I made ec and lc the start instead of completion earlier and this is the simplest way to fix it
		for (Vertex u : g) {
			get(u).ec = get(u).ec + get(u).duration;
			get(u).lc = get(u).lc + get(u).duration;
		}

		return true;
    }

    // Find a topological order of g using DFS
	public LinkedList<Vertex> topologicalOrder() {
		finishList = new LinkedList<>();
		boolean[] visited = new boolean[g.size()];

		for (Vertex u : g) {
			if (!visited[u.getIndex()]) {
				topologicalOrderDFS(u, visited);
			}
		}

		// Check for cycles, easier to just see if the edges are properly oriented, if any backwards there's cycle
		int[] order = new int[g.size()];
		int i = g.size();
		for (Vertex u : finishList) {
			order[u.getIndex()] = i--;
		}

		for (Vertex u : g) {
			for (Edge e : g.incident(u)) {
				Vertex v = e.otherEnd(u);
				if (order[u.getIndex()] < order[v.getIndex()]) {
					// Edge is not properly oriented, indicating a cycle
					return null;
				}
			}
		}

		return finishList;
	}

	private void topologicalOrderDFS(Vertex u, boolean[] visited) {
		visited[u.getIndex()] = true;

		for (Edge e : g.outEdges(u)) {
			Vertex v = e.toVertex();
			if (!visited[v.getIndex()]) {
				topologicalOrderDFS(v, visited);
			}
		}

		finishList.addFirst(u); // Add the current vertex to the front of the list
	}


    // The following methods are called after calling pert().

    // Earliest time at which task u can be completed
	public int ec(Vertex u) {
		return get(u).ec;
	}

    // Latest completion time of u
    public int lc(Vertex u) {
		return get(u).lc;
    }

    // Slack of u
    public int slack(Vertex u) {
		return get(u).slack;
    }

    // Length of a critical path
    public int criticalPath() {
		int criticalPathLength = 0;
		for (Vertex u : g) {
			criticalPathLength = Math.max(criticalPathLength, ec(u));
		}
		return criticalPathLength;
    }

    // Is u a critical vertex?
    public boolean critical(Vertex u) {
		return get(u).critical;
    }

    // Number of critical vertices of g
    public int numCritical() {
		int count = 0;
		for (Vertex u : g) {
			if (critical(u)) {
				count++;
			}
		}
		return count;
    }

    /* Create a PERT instance on g, runs the algorithm.
     * Returns PERT instance if successful. Returns null if G is not a DAG.
     */
    public static PERT pert(Graph g, int[] duration) {
	PERT p = new PERT(g);
	for(Vertex u: g) {
	    p.setDuration(u, duration[u.getIndex()]);
	}
	// Run PERT algorithm.  Returns false if g is not a DAG
	if(p.pert()) {
			return p;
		} else {
			return null;
		}
    }
    
    public static void main(String[] args) throws Exception {
	String graph = "50 1201 1 2 1 1 3 1 1 4 1 1 6 1 1 10 1 1 12 1 1 13 1 1 16 1 1 17 1 1 18 1 1 19 1 1 20 1 1 21 1 1 22 1 1 23 1 1 24 1 1 27 1 1 34 1 1 36 1 1 38 1 1 40 1 1 41 1 1 43 1 1 44 1 1 45 1 1 49 1 2 3 1 2 4 1 2 6 1 2 12 1 2 13 1 2 16 1 2 19 1 2 21 1 2 22 1 2 23 1 2 24 1 2 33 1 2 34 1 2 36 1 2 38 1 2 43 1 2 44 1 2 45 1 3 4 1 3 6 1 3 12 1 3 13 1 3 19 1 3 23 1 3 24 1 3 34 1 3 36 1 4 6 1 4 12 1 4 13 1 4 19 1 4 24 1 4 34 1 4 36 1 5 1 1 5 2 1 5 3 1 5 4 1 5 6 1 5 10 1 5 12 1 5 13 1 5 16 1 5 17 1 5 18 1 5 19 1 5 20 1 5 21 1 5 22 1 5 23 1 5 24 1 5 28 1 5 33 1 5 34 1 5 35 1 5 36 1 5 38 1 5 40 1 5 41 1 5 43 1 5 44 1 5 45 1 5 49 1 6 1 1 6 19 1 6 23 1 6 34 1 7 1 1 7 2 1 7 3 1 7 4 1 7 5 1 7 6 1 7 10 1 7 12 1 7 13 1 7 14 1 7 16 1 7 17 1 7 18 1 7 19 1 7 20 1 7 21 1 7 22 1 7 23 1 7 24 1 7 25 1 7 27 1 7 28 1 7 33 1 7 34 1 7 35 1 7 36 1 7 37 1 7 38 1 7 39 1 7 40 1 7 41 1 7 42 1 7 43 1 7 44 1 7 45 1 7 46 1 7 47 1 7 49 1 7 50 1 8 1 1 8 2 1 8 3 1 8 4 1 8 5 1 8 6 1 8 7 1 8 10 1 8 12 1 8 13 1 8 14 1 8 15 1 8 16 1 8 17 1 8 18 1 8 19 1 8 20 1 8 21 1 8 22 1 8 23 1 8 24 1 8 25 1 8 27 1 8 28 1 8 29 1 8 30 1 8 31 1 8 32 1 8 33 1 8 34 1 8 35 1 8 36 1 8 38 1 8 39 1 8 40 1 8 41 1 8 42 1 8 44 1 8 45 1 8 46 1 8 47 1 8 49 1 8 50 1 9 2 1 9 3 1 9 5 1 9 6 1 9 7 1 9 8 1 9 10 1 9 11 1 9 12 1 9 13 1 9 14 1 9 15 1 9 16 1 9 17 1 9 18 1 9 19 1 9 20 1 9 21 1 9 22 1 9 23 1 9 24 1 9 25 1 9 26 1 9 27 1 9 28 1 9 29 1 9 30 1 9 31 1 9 32 1 9 33 1 9 34 1 9 35 1 9 36 1 9 37 1 9 38 1 9 39 1 9 40 1 9 41 1 9 42 1 9 43 1 9 44 1 9 45 1 9 46 1 9 47 1 9 48 1 9 49 1 9 50 1 10 2 1 10 4 1 10 6 1 10 12 1 10 13 1 10 16 1 10 17 1 10 18 1 10 19 1 10 21 1 10 22 1 10 23 1 10 24 1 10 33 1 10 36 1 10 38 1 10 40 1 10 41 1 10 43 1 10 44 1 10 45 1 10 49 1 11 1 1 11 2 1 11 3 1 11 4 1 11 5 1 11 6 1 11 7 1 11 8 1 11 10 1 11 12 1 11 13 1 11 14 1 11 15 1 11 16 1 11 17 1 11 18 1 11 19 1 11 20 1 11 21 1 11 22 1 11 23 1 11 24 1 11 25 1 11 26 1 11 27 1 11 28 1 11 29 1 11 30 1 11 31 1 11 32 1 11 33 1 11 34 1 11 35 1 11 36 1 11 37 1 11 38 1 11 39 1 11 40 1 11 41 1 11 42 1 11 43 1 11 44 1 11 45 1 11 46 1 11 47 1 11 48 1 11 50 1 12 6 1 12 13 1 12 19 1 12 23 1 12 24 1 12 34 1 12 36 1 13 6 1 13 19 1 13 23 1 13 24 1 13 34 1 13 36 1 14 1 1 14 2 1 14 3 1 14 4 1 14 5 1 14 6 1 14 10 1 14 12 1 14 13 1 14 16 1 14 17 1 14 18 1 14 19 1 14 20 1 14 21 1 14 22 1 14 23 1 14 24 1 14 27 1 14 28 1 14 33 1 14 34 1 14 35 1 14 36 1 14 39 1 14 40 1 14 41 1 14 43 1 14 44 1 14 45 1 14 49 1 14 50 1 15 1 1 15 2 1 15 3 1 15 4 1 15 5 1 15 6 1 15 10 1 15 12 1 15 13 1 15 14 1 15 16 1 15 17 1 15 18 1 15 19 1 15 20 1 15 21 1 15 22 1 15 23 1 15 24 1 15 27 1 15 28 1 15 33 1 15 34 1 15 35 1 15 36 1 15 37 1 15 38 1 15 39 1 15 40 1 15 41 1 15 42 1 15 43 1 15 44 1 15 45 1 15 46 1 15 47 1 15 49 1 15 50 1 16 3 1 16 4 1 16 6 1 16 12 1 16 13 1 16 19 1 16 21 1 16 23 1 16 24 1 16 33 1 16 34 1 16 36 1 16 40 1 16 44 1 17 2 1 17 3 1 17 4 1 17 6 1 17 12 1 17 13 1 17 16 1 17 18 1 17 19 1 17 21 1 17 22 1 17 23 1 17 24 1 17 33 1 17 34 1 17 36 1 17 38 1 17 40 1 17 43 1 17 44 1 17 45 1 18 2 1 18 3 1 18 4 1 18 6 1 18 12 1 18 13 1 18 16 1 18 19 1 18 21 1 18 22 1 18 23 1 18 24 1 18 33 1 18 34 1 18 36 1 18 38 1 18 40 1 18 43 1 18 44 1 18 45 1 19 23 1 19 34 1 20 2 1 20 3 1 20 4 1 20 6 1 20 10 1 20 12 1 20 13 1 20 16 1 20 17 1 20 18 1 20 19 1 20 21 1 20 22 1 20 23 1 20 24 1 20 27 1 20 33 1 20 34 1 20 36 1 20 38 1 20 40 1 20 41 1 20 43 1 20 44 1 20 45 1 20 49 1 21 3 1 21 4 1 21 6 1 21 12 1 21 13 1 21 19 1 21 23 1 21 24 1 21 33 1 21 34 1 21 36 1 21 40 1 21 44 1 22 3 1 22 4 1 22 6 1 22 12 1 22 13 1 22 16 1 22 19 1 22 21 1 22 23 1 22 24 1 22 33 1 22 34 1 22 36 1 22 40 1 22 43 1 22 44 1 22 45 1 24 6 1 24 19 1 24 23 1 24 34 1 25 1 1 25 2 1 25 3 1 25 4 1 25 5 1 25 6 1 25 10 1 25 12 1 25 13 1 25 14 1 25 16 1 25 17 1 25 18 1 25 19 1 25 20 1 25 21 1 25 22 1 25 23 1 25 24 1 25 27 1 25 28 1 25 33 1 25 34 1 25 35 1 25 36 1 25 37 1 25 38 1 25 39 1 25 40 1 25 41 1 25 43 1 25 44 1 25 45 1 25 46 1 25 47 1 25 49 1 25 50 1 26 1 1 26 2 1 26 3 1 26 4 1 26 5 1 26 7 1 26 8 1 26 10 1 26 12 1 26 13 1 26 14 1 26 15 1 26 16 1 26 17 1 26 18 1 26 19 1 26 20 1 26 21 1 26 22 1 26 23 1 26 24 1 26 25 1 26 27 1 26 28 1 26 29 1 26 30 1 26 31 1 26 32 1 26 33 1 26 34 1 26 35 1 26 36 1 26 37 1 26 38 1 26 39 1 26 40 1 26 41 1 26 42 1 26 43 1 26 44 1 26 45 1 26 46 1 26 47 1 26 49 1 26 50 1 27 2 1 27 3 1 27 4 1 27 6 1 27 10 1 27 12 1 27 13 1 27 16 1 27 17 1 27 18 1 27 19 1 27 21 1 27 22 1 27 23 1 27 24 1 27 33 1 27 34 1 27 36 1 27 38 1 27 40 1 27 41 1 27 43 1 27 44 1 27 45 1 27 49 1 28 1 1 28 2 1 28 3 1 28 4 1 28 6 1 28 10 1 28 12 1 28 13 1 28 16 1 28 17 1 28 18 1 28 19 1 28 20 1 28 21 1 28 22 1 28 23 1 28 24 1 28 27 1 28 33 1 28 34 1 28 35 1 28 38 1 28 40 1 28 41 1 28 43 1 28 44 1 28 45 1 28 49 1 29 1 1 29 2 1 29 3 1 29 4 1 29 5 1 29 7 1 29 10 1 29 12 1 29 13 1 29 14 1 29 15 1 29 16 1 29 17 1 29 18 1 29 19 1 29 20 1 29 21 1 29 22 1 29 23 1 29 24 1 29 25 1 29 27 1 29 28 1 29 33 1 29 34 1 29 35 1 29 36 1 29 37 1 29 38 1 29 39 1 29 40 1 29 41 1 29 42 1 29 43 1 29 44 1 29 45 1 29 46 1 29 47 1 29 49 1 29 50 1 30 1 1 30 2 1 30 3 1 30 4 1 30 5 1 30 6 1 30 7 1 30 10 1 30 12 1 30 13 1 30 14 1 30 15 1 30 16 1 30 17 1 30 18 1 30 19 1 30 20 1 30 21 1 30 22 1 30 23 1 30 24 1 30 25 1 30 27 1 30 28 1 30 29 1 30 31 1 30 33 1 30 34 1 30 35 1 30 36 1 30 37 1 30 38 1 30 39 1 30 40 1 30 41 1 30 42 1 30 43 1 30 44 1 30 45 1 30 46 1 30 47 1 30 49 1 30 50 1 31 1 1 31 2 1 31 3 1 31 4 1 31 5 1 31 6 1 31 7 1 31 10 1 31 12 1 31 13 1 31 14 1 31 15 1 31 16 1 31 17 1 31 18 1 31 19 1 31 20 1 31 21 1 31 22 1 31 23 1 31 24 1 31 25 1 31 27 1 31 28 1 31 29 1 31 33 1 31 34 1 31 35 1 31 36 1 31 37 1 31 38 1 31 39 1 31 40 1 31 41 1 31 42 1 31 43 1 31 44 1 31 45 1 31 46 1 31 47 1 31 49 1 31 50 1 32 1 1 32 2 1 32 3 1 32 4 1 32 5 1 32 6 1 32 7 1 32 10 1 32 12 1 32 13 1 32 14 1 32 15 1 32 16 1 32 17 1 32 18 1 32 19 1 32 20 1 32 21 1 32 22 1 32 23 1 32 24 1 32 25 1 32 27 1 32 28 1 32 29 1 32 30 1 32 31 1 32 33 1 32 34 1 32 35 1 32 36 1 32 37 1 32 38 1 32 39 1 32 40 1 32 41 1 32 42 1 32 43 1 32 44 1 32 45 1 32 46 1 32 47 1 32 49 1 32 50 1 33 3 1 33 4 1 33 6 1 33 12 1 33 13 1 33 19 1 33 23 1 33 24 1 33 34 1 33 36 1 34 23 1 35 1 1 35 2 1 35 3 1 35 4 1 35 6 1 35 10 1 35 12 1 35 13 1 35 16 1 35 17 1 35 18 1 35 19 1 35 20 1 35 21 1 35 22 1 35 23 1 35 24 1 35 27 1 35 33 1 35 34 1 35 36 1 35 38 1 35 40 1 35 41 1 35 43 1 35 44 1 35 45 1 35 49 1 36 6 1 36 19 1 36 23 1 36 24 1 36 34 1 37 1 1 37 2 1 37 3 1 37 4 1 37 5 1 37 6 1 37 10 1 37 12 1 37 13 1 37 14 1 37 16 1 37 17 1 37 18 1 37 19 1 37 20 1 37 21 1 37 22 1 37 23 1 37 24 1 37 27 1 37 28 1 37 33 1 37 34 1 37 35 1 37 36 1 37 38 1 37 39 1 37 40 1 37 41 1 37 43 1 37 44 1 37 45 1 37 49 1 37 50 1 38 3 1 38 4 1 38 6 1 38 12 1 38 13 1 38 16 1 38 19 1 38 21 1 38 22 1 38 23 1 38 24 1 38 33 1 38 34 1 38 36 1 38 40 1 38 43 1 38 44 1 38 45 1 39 1 1 39 2 1 39 3 1 39 4 1 39 5 1 39 6 1 39 10 1 39 12 1 39 13 1 39 16 1 39 17 1 39 18 1 39 19 1 39 20 1 39 21 1 39 23 1 39 24 1 39 27 1 39 28 1 39 33 1 39 34 1 39 35 1 39 36 1 39 38 1 39 40 1 39 41 1 39 43 1 39 44 1 39 45 1 39 49 1 39 50 1 40 3 1 40 4 1 40 6 1 40 12 1 40 13 1 40 19 1 40 23 1 40 24 1 40 33 1 40 34 1 40 36 1 41 2 1 41 3 1 41 4 1 41 6 1 41 12 1 41 13 1 41 16 1 41 17 1 41 18 1 41 19 1 41 21 1 41 22 1 41 23 1 41 24 1 41 33 1 41 34 1 41 36 1 41 38 1 41 40 1 41 43 1 41 44 1 41 45 1 42 1 1 42 2 1 42 3 1 42 4 1 42 5 1 42 6 1 42 10 1 42 12 1 42 14 1 42 16 1 42 17 1 42 18 1 42 19 1 42 20 1 42 21 1 42 22 1 42 23 1 42 24 1 42 27 1 42 28 1 42 33 1 42 34 1 42 35 1 42 36 1 42 37 1 42 38 1 42 39 1 42 40 1 42 41 1 42 43 1 42 44 1 42 45 1 42 46 1 42 47 1 42 49 1 42 50 1 43 3 1 43 4 1 43 6 1 43 12 1 43 13 1 43 16 1 43 19 1 43 21 1 43 23 1 43 24 1 43 33 1 43 34 1 43 36 1 43 40 1 43 44 1 43 45 1 44 3 1 44 4 1 44 6 1 44 12 1 44 13 1 44 19 1 44 23 1 44 24 1 44 33 1 44 34 1 44 36 1 44 40 1 45 3 1 45 4 1 45 6 1 45 12 1 45 13 1 45 16 1 45 19 1 45 21 1 45 23 1 45 24 1 45 33 1 45 34 1 45 36 1 45 40 1 45 44 1 46 1 1 46 2 1 46 3 1 46 4 1 46 5 1 46 6 1 46 10 1 46 12 1 46 13 1 46 14 1 46 16 1 46 17 1 46 18 1 46 19 1 46 20 1 46 21 1 46 22 1 46 23 1 46 24 1 46 27 1 46 28 1 46 33 1 46 34 1 46 35 1 46 36 1 46 37 1 46 38 1 46 39 1 46 40 1 46 41 1 46 43 1 46 44 1 46 45 1 46 47 1 46 49 1 46 50 1 47 1 1 47 2 1 47 3 1 47 4 1 47 5 1 47 10 1 47 12 1 47 13 1 47 14 1 47 16 1 47 17 1 47 18 1 47 19 1 47 20 1 47 21 1 47 22 1 47 23 1 47 24 1 47 27 1 47 28 1 47 33 1 47 34 1 47 35 1 47 36 1 47 37 1 47 38 1 47 39 1 47 40 1 47 41 1 47 43 1 47 44 1 47 45 1 47 49 1 47 50 1 48 1 1 48 2 1 48 3 1 48 4 1 48 5 1 48 6 1 48 7 1 48 8 1 48 10 1 48 12 1 48 13 1 48 14 1 48 15 1 48 16 1 48 17 1 48 18 1 48 19 1 48 20 1 48 21 1 48 22 1 48 23 1 48 24 1 48 25 1 48 26 1 48 28 1 48 29 1 48 30 1 48 31 1 48 32 1 48 33 1 48 34 1 48 35 1 48 36 1 48 38 1 48 39 1 48 41 1 48 42 1 48 43 1 48 44 1 48 45 1 48 46 1 48 47 1 48 49 1 48 50 1 49 2 1 49 3 1 49 4 1 49 6 1 49 12 1 49 13 1 49 16 1 49 17 1 49 18 1 49 19 1 49 21 1 49 22 1 49 23 1 49 24 1 49 33 1 49 34 1 49 36 1 49 38 1 49 40 1 49 41 1 49 43 1 49 44 1 49 45 1 50 1 1 50 2 1 50 3 1 50 4 1 50 5 1 50 6 1 50 10 1 50 12 1 50 13 1 50 16 1 50 17 1 50 18 1 50 19 1 50 20 1 50 21 1 50 22 1 50 23 1 50 27 1 50 28 1 50 33 1 50 34 1 50 35 1 50 36 1 50 38 1 50 40 1 50 41 1 50 43 1 50 44 1 50 45 1 50 49 1 2 3 2 1 3 1 1 2 3 1 2 2 3 1 3 3 1 1 2 1 2 2 2 3 3 1 1 3 3 2 2 2 2 3 2 1 2 2 2 2 3 1 2 1 3 1 3 3 2 3";
	Scanner in;
	// If there is a command line argument, use it as file from which
	// input is read, otherwise use input from string.
	in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
	Graph g = Graph.readDirectedGraph(in);
	g.printGraph(false);

	int[] duration = new int[g.size()];
	for(int i=0; i<g.size(); i++) {
	    duration[i] = in.nextInt();
	}
	PERT p = pert(g, duration);
	if(p == null) {
	    System.out.println("Invalid graph: not a DAG");
	} else {
		System.out.println("Completion time :" + p.criticalPath());
	    System.out.println("Number of critical vertices: " + p.numCritical());
	    System.out.println("u\tEC\tLC\tSlack\tCritical");
	    for(Vertex u: g) {
		System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
	    }
	}
    }
}
