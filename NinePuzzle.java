/* NinePuzzle.java
   
   This project includes some testing code to help verify the implementation.
   Input boards can be provided with standard input or read from a file.
   
   To provide test inputs with standard input, run the program with
	java NinePuzzle
   To terminate the input, use Ctrl-D (which signals EOF).
   
   To read test inputs from a file (e.g. boards.txt), run the program with
    java NinePuzzle boards.txt
	
   The input format for both input methods is the same. Input consists
   of a series of 9-puzzle boards, with the '0' character representing the 
   empty square. For example, a sample board with the middle square empty is
   
    1 2 3
    4 0 5
    6 7 8
   
   And a solved board is
   
    1 2 3
    4 5 6
    7 8 0
   
   An input file can contain an unlimited number of boards; each will be 
   processed separately.
*/

import java.util.Scanner;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;

//This class is taken from:
//Algoritm 4th Edition by Robert Sedgewick, Kevin Wayne
//Pg(155)
class Bag<Item> implements Iterable<Item> {
  private Node firstNode;
  private class Node {
    Item data;
    Node nextNode;
  }
  
  public void add(Item data) {
    Node oldfirstNode = firstNode;
    firstNode = new Node();
    firstNode.data = data;
    firstNode.nextNode = oldfirstNode;
  }
  
  public Iterator<Item> iterator() {
    return new ListIterator();
  }
  
  private class ListIterator implements Iterator<Item> {
    private Node currentNode = firstNode;

    public boolean hasNext() {
      return currentNode != null;
    }

    public void remove() {}

    public Item next() {
      Item data = currentNode.data;
      currentNode = currentNode.nextNode;
      return data;
    }
  }
}

//This class is taken from:
//Algoritm 4th Edition by Robert Sedgewick, Kevin Wayne
//Pg(527)
class Graph {
  private int E;
  private final int V;
  private Bag<Integer>[] adjacent;
  
  public Graph(int V) {
    this.V = V;
    this.E = 0;
    adjacent = (Bag<Integer>[]) new Bag[V];
    for (int v = 0; v < V; v++) {
      adjacent[v] = new Bag<Integer>();
    }
  }
  
  public int getVertex() {
    return V;
  }

 //addNewEdge
  public void addNewEdge(int v, int w) {
    adjacent[v].add(w);
    adjacent[w].add(v);
    E += 1;
  }

  public Iterable<Integer> adj(int v) {
    return adjacent[v];
  }
}

//This class is taken from:
//Algoritm 4th Edition by Robert Sedgewick, Kevin Wayne
//Pg(540)
class pathsForBFS {
  private boolean[] markedArray;
  private int[] edgeToArray;
  private final int source;

  public pathsForBFS(Graph graph1, int source) {
    markedArray = new boolean[graph1.getVertex()];
    edgeToArray = new int[graph1.getVertex()];
    this.source = source;
    bfs(graph1, source);
  }

  private void bfs(Graph graph1, int source) {
    Queue<Integer> queue = new LinkedList<Integer>();
    markedArray[source] = true;
    queue.add(source);
    while (!queue.isEmpty()) {
      int v = queue.remove();
      for (int w : graph1.adj(v)) {
        if (!markedArray[w]) {
          edgeToArray[w] = v;
          markedArray[w] = true; 
          queue.add(w);
        }
      } 
    }
  }

  public boolean checkPathToVertex(int v) {
    return markedArray[v];
  }

  public Iterable<Integer> pathTo(int v) {
    if (!checkPathToVertex(v)) {
      return null;
    }
    Stack<Integer> path = new Stack<Integer>();
    for (int x = v; x != source; x = edgeToArray[x]) {
      path.push(x);
    }
    path.push(source);
    return path;
  }
}

public class NinePuzzle{

	//The total number of possible boards is 9! = 1*2*3*4*5*6*7*8*9 = 362880
	public static final int NUM_BOARDS = 362880;

	public static void updateToBoard(int[][] B, int initialI, int initialJ, int updatedI, int updatedJ, int v, Graph graph1){
		int newB[][] = new int[3][3];
		for(int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++) 
			{
				newB[i][j] = B[i][j];	
			}
		}

		int temp = newB[updatedI][updatedJ];
		newB[updatedI][updatedJ] = 0;
		newB[initialI][initialJ] = temp;
		int u = getIndexFromBoard(newB);
		graph1.addNewEdge(u, v);

	}	

	public static Graph makeGraph(){

		Graph graph1 = new Graph(NUM_BOARDS);

		for(int v = 0; v < graph1.getVertex(); v++){
			int[][] B = getBoardFromIndex(v);

			int i = 0;
			int j = 0;
			boolean checker = false;
			for (i=0; i < 3; i++)
			{
				for (j=0; j < 3; j++)
				{
					if (B[i][j] == 0) 
					{
						checker = true;
						break;	
					}
				}
				if(checker)
				{
					break;
				}
			}

			if(i > 0)
				updateToBoard(B, i, j, i-1, j, v, graph1);
			if(i < 2)
				updateToBoard(B, i, j, i+1, j, v, graph1);
			if(j > 0)
				updateToBoard(B, i, j, i, j-1, v, graph1);
			if(j < 2)
				updateToBoard(B, i, j, i, j+1, v, graph1);
		}

		return graph1;
	}


	/*  SolveNinePuzzle(B)
		Given a valid 9-puzzle board (with the empty space represented by the 
		value 0),return true if the board is solvable and false otherwise. 
		If the board is solvable, a sequence of moves which solves the board
		will be printed, using the printBoard function below.
	*/
	public static boolean SolveNinePuzzle(int[][] B){
		
		Graph graph1 = makeGraph();

		int v = getIndexFromBoard(B);

		int u = 0;

		pathsForBFS bfs = new pathsForBFS(graph1, u);

		if (bfs.checkPathToVertex(v))
		{
			for (int path : bfs.pathTo(v)) {
				printBoard(getBoardFromIndex(path));
			}
		}

		return bfs.checkPathToVertex(v);
		
	}
	
	/*  printBoard(B)
		Print the given 9-puzzle board. The SolveNinePuzzle method above should
		use this method when printing the sequence of moves which solves the input
		board. If any other method is used (e.g. printing the board manually), the
		submission may lose marks.
	*/
	public static void printBoard(int[][] B){
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++)
				System.out.printf("%d ",B[i][j]);
			System.out.println();
		}
		System.out.println();
	}
	
	
	/* Board/Index conversion functions
	   These should be treated as black boxes (i.e. don't modify them, don't worry about
	   understanding them). The conversion scheme used here is adapted from
		 W. Myrvold and F. Ruskey, Ranking and Unranking Permutations in Linear Time,
		 Information Processing Letters, 79 (2001) 281-284. 
	*/
	public static int getIndexFromBoard(int[][] B){
		int i,j,tmp,s,n;
		int[] P = new int[9];
		int[] PI = new int[9];
		for (i = 0; i < 9; i++){
			P[i] = B[i/3][i%3];
			PI[P[i]] = i;
		}
		int id = 0;
		int multiplier = 1;
		for(n = 9; n > 1; n--){
			s = P[n-1];
			P[n-1] = P[PI[n-1]];
			P[PI[n-1]] = s;
			
			tmp = PI[s];
			PI[s] = PI[n-1];
			PI[n-1] = tmp;
			id += multiplier*s;
			multiplier *= n;
		}
		return id;
	}
		
	public static int[][] getBoardFromIndex(int id){
		int[] P = new int[9];
		int i,n,tmp;
		for (i = 0; i < 9; i++)
			P[i] = i;
		for (n = 9; n > 0; n--){
			tmp = P[n-1];
			P[n-1] = P[id%n];
			P[id%n] = tmp;
			id /= n;
		}
		int[][] B = new int[3][3];
		for(i = 0; i < 9; i++)
			B[i/3][i%3] = P[i];
		return B;
	}
	

	public static void main(String[] args){
		/* Code to test your implementation */
		/* You may modify this, but nothing in this function will be marked */

		
		Scanner s;

		if (args.length > 0){
			//If a file argument was provided on the command line, read from the file
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			//Otherwise, read from standard input
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		int graphNum = 0;
		double totalTimeSeconds = 0;
		
		//Read boards until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading board %d\n",graphNum);
			int[][] B = new int[3][3];
			int valuesRead = 0;
			for (int i = 0; i < 3 && s.hasNextInt(); i++){
				for (int j = 0; j < 3 && s.hasNextInt(); j++){
					B[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < 9){
				System.out.printf("Board %d contains too few values.\n",graphNum);
				break;
			}
			System.out.printf("Attempting to solve board %d...\n",graphNum);
			long startTime = System.currentTimeMillis();
			boolean isSolvable = SolveNinePuzzle(B);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;
			
			if (isSolvable)
				System.out.printf("Board %d: Solvable.\n",graphNum);
			else
				System.out.printf("Board %d: Not solvable.\n",graphNum);
		}
		graphNum--;
		System.out.printf("Processed %d board%s.\n Average Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>1)?totalTimeSeconds/graphNum:0);

	}

}