import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Graph implements Serializable {

    public double minlat, minlon, maxlat, maxlon;

    List<Crossing> vertex = new ArrayList<Crossing>();
    List<Edge> edges = new ArrayList<Edge>();
    List<Street> streets = new ArrayList<Street>();
    Map<Crossing, List<String>> crosses = new HashMap<Crossing, List<String>>();
    public Graph(){}

    public boolean addVertex(Crossing v) {
        for (Crossing c: vertex) {
            if((c.getX() == v.getX()) && (c.getY() == v.getY())) return false;
        }
        vertex.add(v);
        return true;
    }

}

////////////////////////////////////////////////////////

class Crossing implements Serializable{

    public double x, y;

    Crossing(double x, double y){this.x = x; this.y = y;}
    Crossing(){}

    public double getX(){return this.x;}
    public double getY(){return this.y;}

    @Override
    public String toString() {
        return "" + x + "-" + y + " ";
    }

}

/////////////////////////////////////////////////////////

class Edge implements Serializable{

    public Crossing cross1, cross2;
    public double dist;

    Edge(){}
    Edge(Crossing c1, Crossing c2, double d){this.cross1 = c1; this.cross2 = c2; this.dist = d;}
}

/////////////////////////////////////////////////////////

class Street implements Serializable{

    List<Crossing> nodes = new ArrayList<Crossing>();
    String name = new String();

    Street(){}
    Street(String name) {this.name = name;}

}

