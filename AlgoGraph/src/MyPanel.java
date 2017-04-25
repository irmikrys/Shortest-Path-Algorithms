import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.geom.*;

import static java.awt.Color.*;


public class MyPanel extends JPanel {

    Color col;

    Graph g;
    double width, height;
    double x1, x2, y1, y2;
    List<Crossing> l;


    public MyPanel() {

    }

    public MyPanel(double x1, double y1, double x2, double y2, double width, double height, Graph g, List<Crossing> l){
        this.g = g;
        this.width = width;
        this.height = height;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.l = l;
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        final double scaleY = height / (y2 - y1); //scale
        final double scaleX = width / (x2 - x1); //scale1

        //System.out.print(width + " " + height + " ;");

        Graphics2D g2d = (Graphics2D) gr;
        Iterator<Crossing> keyIterator = g.crosses.keySet().iterator();
        while(keyIterator.hasNext()){
            Crossing nextCrossing = keyIterator.next();
            Ellipse2D.Double p = new Ellipse2D.Double((nextCrossing.getX() - x1) * scaleX - 2.5,
                    height - (nextCrossing.getY() - y1) * scaleY - 2.5, 5.0, 5.0);
            g2d.fill(p);
        }


        for(Street s : g.streets) {
            List<Crossing> cpNodes = new ArrayList<Crossing>(s.nodes);
            Crossing v1 = null;
            Crossing v2 = null;
            int counter = 0;
            for(Crossing c : s.nodes) {
                if (counter == 0) {
                } else {
                    v2 = v1;
                }
                v1 = cpNodes.get(counter++);
                if (v1 != null && v2 != null) {
                    Line2D.Double line =
                            new Line2D.Double((v1.getX() - x1) * scaleX, height - (v1.getY() - y1) * scaleY,
                                    (v2.getX() - x1) * scaleX, height - (v2.getY() - y1) * scaleY);
                    g2d.draw(line);
                }

            }
        }

        if(Main.dijkstra) col = PINK;
        if(Main.astar) col = BLUE;
        if(Main.dijkstraFib) col = GREEN;

        if(Main.dijkstra || Main.astar || Main.dijkstraFib){
            Crossing v1 = null;
            Crossing v2 = null;
            int counter = 0;
            for(Crossing v : l) {
                if(counter == 0) {

                }
                else {
                    v2 = v1;
                }
                v1 = l.get(counter++);
                if(v1 != null && v2 != null) {
                        Line2D.Double line =
                                new Line2D.Double((v1.getX() - x1) * scaleX, height - (v1.getY() - y1) * scaleY,
                                        (v2.getX() - x1) * scaleX, height - (v2.getY() - y1) * scaleY);
                    gr.setColor(col);
                    g2d.draw(line);
                }
                gr.setColor(col);
                    Ellipse2D.Double p = new Ellipse2D.Double((v.getX() - x1) * scaleX - 2.5,
                            height - (v.getY() - y1) * scaleY - 2.5, 5.0, 5.0);
                g2d.fill(p);
            }
        }

    }
}
