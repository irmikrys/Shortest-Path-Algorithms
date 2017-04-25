import com.thoughtworks.xstream.XStream;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.List;

import static java.lang.Double.POSITIVE_INFINITY;


public class Main {

    public static Graph g = new Graph();
    public static boolean dijkstra = false;
    public static boolean astar = false;
    public static boolean dijkstraFib = false;
    public static List<Crossing> res = new ArrayList<>();

    ///////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        odczyt("smallresult.txt");
        System.out.println(g.crosses.size());

        //////////////////////////SERIALIZACJA/////////////////////////////////

        Graph serializedGraph = g;
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("SerGraph.bin"))) {
            outputStream.writeObject(serializedGraph);
        }

        Graph deserializedGraph = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("/Users/irmi/Documents/Java /NioServer/ClientGraph.bin"))) {
            deserializedGraph = (Graph) inputStream.readObject();
            //System.out.println(deserializedGraph.crosses);
            //System.out.println(deserializedGraph.streets);
        }

        System.out.println(serializedGraph == deserializedGraph);

        //////////////////////SERIALIZACJA XML/////////////////////////

        XStream xstream = new XStream();

        xstream.alias("point", Crossing.class);
        xstream.alias("graph", Graph.class );
        xstream.alias("street", Street.class);
        xstream.alias("edge", Edge.class);

        File xml = new File("XMLGraph.xml");
        PrintStream ps = new PrintStream(xml);
        ps.print(xstream.toXML(g));
        Graph newGraph = (Graph)xstream.fromXML(xml);

        System.out.println(newGraph.crosses.size());

        ///////////////////////////////////////////////////////////////

        //System.out.println(g.minlon + " " + g.minlat + "; " + g.maxlon + " " + g.maxlat);

        Crossing c1 = null, c2 = null;

        /*System.out.println("Podaj współrzędne dwóch skrzyżowań: ");
        Scanner cr = new Scanner(System.in);
        double x1 = cr.nextDouble();
        double y1 = cr.nextDouble();
        double x2 = cr.nextDouble();
        double y2 = cr.nextDouble();*/

        //double x1 = 19.9440748; double y1 = 50.0611082; double x2 = 19.9429591; double y2 = 50.0591514; //TS
        //double x1 = 19.9336735; double y1 = 50.0642122; double x2 = 19.9408262; double y2 = 50.0596307; //TS
        //double x1 = 19.9460623; double y1 = 50.0646204; double x2 = 19.9434121; double y2 = 50.0648972; //TS
        //double x1 = 19.9342694; double y1 = 50.0645844; double x2 = 19.9439988; double y2 = 50.0612006;

        double x1 = 19.9342694; double y1 = 50.0645844; double x2 = 19.9426880; double y2 = 50.0647876;


        g = deserializedGraph;

        for (Crossing c: g.vertex) {
            if((c.getX() == x1 && c.getY() == y1) || (c.getX() == x2 && c.getY() == y2)){
                if(c1 == null && c2 == null) c1 = c;
                else if(c1 != null && c2 == null) c2 = c;
                if(c1 != null && c2 != null) break;
            }
        }


        System.out.println("Którego algorytmu chcesz użyć?");
        System.out.println("1 - Dijkstra");
        System.out.println("2 - A*");
        System.out.println("3 - Dijkstra - kopiec Fibonacciego");
        Scanner k = new Scanner(System.in);
        int choice = k.nextInt();


        if(c1 != null && c2 != null) {

            if(choice == 1){dijkstra = true; res = dijkstra(c1, c2);}

            if(choice == 2){astar = true; res = astar(c1, c2);}

            if(choice == 3){dijkstraFib = true; res = dijkstraFib(c1, c2);}

        }

        else System.out.println("Nie ma takich skrzyżowań...");

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MyFrame(g.minlon, g.minlat, g.maxlon, g.maxlat, g, res);
            }
        });
    }

    ///////////////////////////////////////////////////////

    public static void odczyt(String nazwa) throws IOException {
        BufferedReader brFile = null;
        boolean dimensions = false;
        boolean intersections = false;
        boolean edges = false;
        try {
            brFile = new BufferedReader(new FileReader(nazwa));
            String l = brFile.readLine();
            while (l != null) {
                if (l.equals("Dimensions:")) {
                    //System.out.println("#dimensions");
                    dimensions = true;
                } else if (l.equals("Intersections:")) {
                    //System.out.println("#begin");
                    dimensions = false;
                    intersections = true;
                } else if (l.equals("Edges:")) {
                    //System.out.println("#edges");
                    intersections = false;
                    edges = true;
                }
                //else if(l.equals("Nodes:"))
                else if (dimensions) {
                    String noBlanks = l.replaceAll(" ", "");
                    StringTokenizer stringTokenizer = new StringTokenizer(noBlanks, "(,):=");
                    while (stringTokenizer.hasMoreTokens()) {
                        stringTokenizer.nextToken();
                        g.minlat = Double.parseDouble(stringTokenizer.nextToken());
                        stringTokenizer.nextToken();
                        g.minlon = Double.parseDouble(stringTokenizer.nextToken());
                        stringTokenizer.nextToken();
                        g.maxlat = Double.parseDouble(stringTokenizer.nextToken());
                        stringTokenizer.nextToken();
                        g.maxlon = Double.parseDouble(stringTokenizer.nextToken());
                    }
                } else if (intersections) {
                    List<String> names = new ArrayList<String>();
                    String lat = new String();
                    String lon = new String();
                    String subl = l.substring(0, l.length() - 1);
                    String noBlanks = subl.replaceAll(" ", "");
                    //usun biale znaki i inne
                    StringTokenizer stringTokenizer = new StringTokenizer(noBlanks, "(,)=");
                    while (stringTokenizer.hasMoreTokens()) {
                        lat = stringTokenizer.nextToken();
                        lon = stringTokenizer.nextToken();
                        while (stringTokenizer.hasMoreTokens()) {
                            String a = new String(stringTokenizer.nextToken());
                            //jak jest duża litera w środku to zrób spacje
                            StringBuilder s = new StringBuilder(a);
                            for (int i = 1; i < s.length(); ++i) {
                                if (Character.isUpperCase(s.charAt(i))) {
                                    s.insert(i++, ' ');
                                }
                            }
                            String res = new String(s);
                            //System.out.println(res);
                            names.add(res);
                        }
                    }
                    Crossing c = new Crossing(Double.parseDouble(lon), Double.parseDouble(lat));
                    //System.out.println(c.getX() + "-" + c.getY());
                    //System.out.println(names);
                    g.crosses.put(c, names);

                } else if (edges) {
                    StringTokenizer stringTokenizer = new StringTokenizer(l, ":,");
                    double lat, lon;
                    String name = new String(stringTokenizer.nextToken());
                    Street s = new Street(name);
                    while (stringTokenizer.hasMoreTokens()) {
                        lat = Double.parseDouble(stringTokenizer.nextToken());
                        lon = Double.parseDouble(stringTokenizer.nextToken());
                        Crossing c = new Crossing(lon, lat);
                        if(g.addVertex(c)){
                            s.nodes.add(c);
                        }
                        else {
                            for(Crossing cc : g.vertex) {
                                if((cc.getX() == lon) && (cc.getY() == lat)) {
                                    s.nodes.add(cc);
                                }
                            }
                        }
                    }
                    g.streets.add(s);

                } else {
                    System.out.println("R.I.P.");
                }
                l = brFile.readLine();
            }
        } finally {
            if (brFile != null) {
                brFile.close();
            }
        }
    }
    /////////////////////////////////////////////////////////////////////

    public static double distance(Crossing c1, Crossing c2) {
        return Math.sqrt((Math.pow(c2.getX() - c1.getX(), 2)) + (Math.pow(c2.getY() - c1.getY(), 2))) * 111196.672;
    }

    ////////////////////////////////////////////////////////////////////

    public static List<Crossing> dijkstra(Crossing source, Crossing target) {
        List<Crossing> result = new ArrayList<Crossing>();
        //g.vertex --> all nodes
        int size = g.vertex.size();
        double[] dist = new double[size]; //distances from source to each node
        Crossing[] prev = new Crossing[size]; //previous nodes in optimal path from source
        PriorityQueue<Crossing> Q = new PriorityQueue<Crossing>(new PQComparator(dist));

        //inicjalizacja
        for (int i = 0; i < size; i++) {
            dist[i] = POSITIVE_INFINITY;
            prev[i] = null;
            Q.offer(g.vertex.get(i));
        }

        dist[g.vertex.indexOf(source)] = 0;

        while (!Q.isEmpty()) {

            Crossing c = Q.poll();

            //foreach (neighbor n of c)  {alt <- dist[c] + distance(c, n); if(alt < dist[v]) {dist[v] = alt; prev[v] = c;} }
            for (Street s: g.streets) {
                int posInStreet = s.nodes.indexOf(c);
                int posInGraph = g.vertex.indexOf(c);

                if(posInStreet > 0) {
                    int prevPos = g.vertex.indexOf(s.nodes.get(posInStreet - 1));
                    Crossing prevNode = g.vertex.get(prevPos);
                    double alt = dist[posInGraph] + distance(c, prevNode);
                    if(alt < dist[prevPos]) {
                        dist[prevPos] = alt;
                        prev[prevPos] = c;
                        Q.offer(prevNode);
                    }
                }

                if(posInStreet >= 0 && posInStreet < s.nodes.size() - 1) {
                    int nextPos = g.vertex.indexOf(s.nodes.get(posInStreet + 1));
                    Crossing nextNode = g.vertex.get(nextPos);
                    double alt = dist[posInGraph] + distance(c, nextNode);
                    if(alt < dist[nextPos]) {
                        dist[nextPos] = alt;
                        prev[nextPos] = c;
                        Q.offer(nextNode);
                    }
                }
            }
            //System.out.println(Q.size());

        }

        Crossing e = target;
        result.add(e);
        System.out.println("=============== DROGA ===============");
        System.out.println("Distance[m]: " + dist[g.vertex.indexOf(target)]);
        System.out.println();
        System.out.println(e);
        if(dist[g.vertex.indexOf(target)] != Double.POSITIVE_INFINITY) {

            while (e != source) {
                e = prev[g.vertex.indexOf(e)];
                System.out.println(e);
                result.add(e);
            }
        }
        else{System.out.println("Nie ma drogi między punktami...");}

        return result;
    }

    ///////////////////////////////////////////////////////////////////

    public static List<Crossing> dijkstraFib(Crossing source, Crossing target) {
        List<Crossing> result = new ArrayList<Crossing>();
        //g.vertex --> all nodes
        int size = g.vertex.size();
        double[] dist = new double[size]; //distances from source to each node
        Crossing[] prev = new Crossing[size]; //previous nodes in optimal path from source
        PriorityQueue<Crossing> Q = new PriorityQueue<Crossing>(new PQComparator(dist));

        //inicjalizacja
        for (int i = 0; i < size; i++) {
            dist[i] = POSITIVE_INFINITY;
            prev[i] = null;
            Q.offer(g.vertex.get(i));
        }

        dist[g.vertex.indexOf(source)] = 0;

        while (!Q.isEmpty()) {

            Crossing c = Q.poll();

            //foreach (neighbor n of c)  {alt <- dist[c] + distance(c, n); if(alt < dist[v]) {dist[v] = alt; prev[v] = c;} }
            for (Street s: g.streets) {
                int posInStreet = s.nodes.indexOf(c);
                int posInGraph = g.vertex.indexOf(c);

                if(posInStreet > 0) {
                    int prevPos = g.vertex.indexOf(s.nodes.get(posInStreet - 1));
                    Crossing prevNode = g.vertex.get(prevPos);
                    double alt = dist[posInGraph] + distance(c, prevNode);
                    if(alt < dist[prevPos]) {
                        dist[prevPos] = alt;
                        prev[prevPos] = c;
                        Q.offer(prevNode);
                    }
                }

                if(posInStreet >= 0 && posInStreet < s.nodes.size() - 1) {
                    int nextPos = g.vertex.indexOf(s.nodes.get(posInStreet + 1));
                    Crossing nextNode = g.vertex.get(nextPos);
                    double alt = dist[posInGraph] + distance(c, nextNode);
                    if(alt < dist[nextPos]) {
                        dist[nextPos] = alt;
                        prev[nextPos] = c;
                        Q.offer(nextNode);
                    }
                }
            }
            //System.out.println(Q.size());

        }

        Crossing e = target;
        result.add(e);
        System.out.println("=============== DROGA ===============");
        System.out.println("Distance[m]: " + dist[g.vertex.indexOf(target)]);
        System.out.println();
        System.out.println(e);
        if(dist[g.vertex.indexOf(target)] != Double.POSITIVE_INFINITY) {

            while (e != source) {
                e = prev[g.vertex.indexOf(e)];
                System.out.println(e);
                result.add(e);
            }
        }
        else{System.out.println("Nie ma drogi między punktami...");}

        return result;
    }

    ///////////////////////////////////////////////////////////////////

    public static List<Crossing> astar(Crossing source, Crossing target) {
        int size = g.vertex.size();
        double tentativeGScore = 0;
        boolean tentativeIsBetter = false;
        int[] cameFrom = new int[size];
        double[] gScore = new double[size];
        double[] fScore = new double[size];
        double[] hScore = new double[size];
        List<Crossing> closedSet = new ArrayList<Crossing>();
        List<Crossing> openSet = new ArrayList<Crossing>();
        boolean[] bOpen = new boolean[size];
        double minValue;
        int minIndex;

        //inicjalizacja
        openSet.add(source);
        bOpen[g.vertex.indexOf(source)] = true;
        for(int i = 0; i < size; i++){
            gScore[i] = POSITIVE_INFINITY;
            fScore[i] = POSITIVE_INFINITY;
            cameFrom[i] = -1;
        }
        gScore[g.vertex.indexOf(source)] = 0;
        fScore[g.vertex.indexOf(source)] = distance(source, target);

        while(!openSet.isEmpty()) {
            // current := the node in openSet having the lowest fScore[] value

            minValue = -1;
            minIndex = -1;

            for(int i = 0; i < fScore.length; i++) {
                if(bOpen[i]) {
                    minValue = fScore[i];
                    minIndex = i;
                    break;
                }
            }

            for(int i = 0; i < fScore.length; ++i) {
                if(fScore[i] < minValue && bOpen[i]) {
                    minValue = fScore[i];
                    minIndex = i;
                }
            }
            Crossing current = g.vertex.get(minIndex);
            openSet.remove(current);
            bOpen[minIndex] = false;
            //if current = goal return reconstruct_path(cameFrom, current)
            if(current == target) {
                System.out.println("=============== DROGA ===============");
                System.out.println("Distance[m]: " + fScore[g.vertex.indexOf(target)]);
                System.out.println();
                return reconstructPath(cameFrom, g.vertex.indexOf(target));
            }

            closedSet.add(current);

            for (Street s: g.streets) {
                int posInStreet = s.nodes.indexOf(current);
                int posInGraph = g.vertex.indexOf(current);

                if(posInStreet > 0) {
                    int prevPos = g.vertex.indexOf(s.nodes.get(posInStreet - 1));
                    Crossing prevNode = g.vertex.get(prevPos);
                    if(closedSet.contains(prevNode)) {
                        //ignore
                    } else {
                        tentativeGScore = gScore[posInGraph] + distance(current, prevNode);
                        tentativeIsBetter = false;
                        if(!openSet.contains(prevNode)) {
                            openSet.add(prevNode);
                            bOpen[prevPos] = true;
                            hScore[prevPos] = distance(prevNode, target);
                            tentativeIsBetter = true;
                        } else if(tentativeGScore >= gScore[prevPos]){
                            //not a better path
                            tentativeIsBetter = false;
                        } else if(tentativeGScore < gScore[prevPos]) {
                            //a better path
                            tentativeIsBetter = true;
                        }

                        if(tentativeIsBetter) {
                            cameFrom[prevPos] = posInGraph;
                            gScore[prevPos] = tentativeGScore;
                            fScore[prevPos] = gScore[prevPos] + hScore[prevPos];
                        }

                    }

                }

                if(posInStreet >= 0 && posInStreet < s.nodes.size() - 1) {
                    int nextPos = g.vertex.indexOf(s.nodes.get(posInStreet + 1));
                    Crossing nextNode = g.vertex.get(nextPos);
                    if(closedSet.contains(nextNode)) {
                        //ignore
                    } else {
                        tentativeGScore = gScore[posInGraph] + distance(current, nextNode);
                        tentativeIsBetter = false;
                        if(!openSet.contains(nextNode)) {
                            openSet.add(nextNode);
                            bOpen[nextPos] = true;
                            hScore[nextPos] = distance(nextNode, target);
                            tentativeIsBetter = true;
                        } else if(tentativeGScore >= gScore[nextPos]){
                            //not a better path
                            tentativeIsBetter = false;
                        } else if(tentativeGScore < gScore[nextPos]) {
                            //a better path
                            tentativeIsBetter = true;
                        }

                        if(tentativeIsBetter) {
                            cameFrom[nextPos] = posInGraph;
                            gScore[nextPos] = tentativeGScore;
                            fScore[nextPos] = gScore[nextPos] + hScore[nextPos];
                        }

                    }

                }
            }
        }

        System.out.println("Nie ma drogi pomiędzy punktami...");
        return null;
    }


    ///////////////////////////////////////////////////////////////////

    public static List<Crossing> reconstructPath(int[] cameFrom, int target) {
        List<Crossing> result = new ArrayList<Crossing>();

        if(cameFrom.length > 0) {
            result.add(g.vertex.get(target));
            System.out.println(g.vertex.get(target));
            while(true) {
                if(cameFrom[target] == -1) break;
                System.out.println(g.vertex.get(cameFrom[target]));
                result.add(g.vertex.get(cameFrom[target]));
                target = cameFrom[target];
            }
        }

        return result;
    }

    ///////////////////////////////////////////////////////////////////
    public static class PQComparator implements Comparator<Crossing> {
        double[] d;
        PQComparator(double[] d){this.d = d;}
        @Override
        public int compare(Crossing o1, Crossing o2) {
            int pos1 = g.vertex.indexOf(o1);
            int pos2 = g.vertex.indexOf(o2);
            double d1 = d[pos1];
            double d2 = d[pos2];
            return Double.compare(d1, d2);
        }
    }

    ////////////////////////////////////////////////////////////////////

}


