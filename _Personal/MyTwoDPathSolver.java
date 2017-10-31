import java.util.Queue;
import java.util.LinkedList;

public class MyTwoDPathSolver {
	public static void main(String[] args) {
		int[][] map = { {0,0,0,0,0}, 
						{1,1,0,1,0}, 
						{0,0,1,0,0}, 
						{0,0,0,0,1}, 
						{1,1,0,0,0} }; 
		Solver solver = new Solver(map);
		solver.solve(new Point(3,0), new Point(0, 4));
	}
}

class Solver {
	int[][] map;
	Point[][] visited;
	Queue<Point> q;
	
	public Solver(int[][] map) {
		this.map = map;
		visited = new Point[map.length][map[0].length];
		q = new LinkedList<Point>();
	}
	
	public void solve(Point orig, Point dest) {
		Point p;
		visited[orig.row][orig.col] = orig;
		q.add(orig);
		
		while(q.peek() != null) {
			Point currentPoint = q.poll();
			System.out.println("VISTIED: " + currentPoint.row + "," + currentPoint.col + " DISTANCE: " + currentPoint.distance);
			if (currentPoint.row == dest.row && currentPoint.col == dest.col) {
				break;
			}
			if (isValid(currentPoint.row - 1, currentPoint.col)) {
				p = new Point(currentPoint.row - 1, currentPoint.col);
				p.distance = currentPoint.distance + 1;
				visited[currentPoint.row - 1][currentPoint.col] = p;
				q.add(p);
			}
			if (isValid(currentPoint.row, currentPoint.col + 1)) {
				p = new Point(currentPoint.row, currentPoint.col + 1);
				p.distance = currentPoint.distance + 1;
				visited[currentPoint.row][currentPoint.col + 1] = p;
				q.add(p);
			}
			if (isValid(currentPoint.row + 1, currentPoint.col)) {
				p = new Point(currentPoint.row + 1, currentPoint.col);
				p.distance = currentPoint.distance + 1;
				visited[currentPoint.row + 1][currentPoint.col] = p;
				q.add(p);
			}
			if (isValid(currentPoint.row, currentPoint.col - 1)) {
				p = new Point(currentPoint.row, currentPoint.col - 1);
				p.distance = currentPoint.distance + 1;
				visited[currentPoint.row][currentPoint.col - 1] = p;
				q.add(p);
			}
		}
	}
	
	private boolean isValid(int row, int col) {
		if (row < 0 || row == map.length || col < 0 || col == map[0].length || map[row][col] == 1)
			return false;
		if (visited[row][col] != null)
			return false;
		return true;
	}
}

class Point {
	public int row;
	public int col;
	public int distance;
	public Point(int row, int col) {
		this.row = row;
		this.col = col;
		distance = 0;
	}
}