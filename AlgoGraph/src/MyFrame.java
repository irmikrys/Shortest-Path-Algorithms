import javax.swing.*;
import java.util.List;
import java.awt.Dimension;
import java.awt.Toolkit;

public class MyFrame extends JFrame {

    double width;
    double height;
    Graph g;
    List<Crossing> l;

    public MyFrame() {

    }
    public MyFrame(double x1, double y1, double x2, double y2, Graph g, List<Crossing> l){

        super("Mapa");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.getWidth();
        height = screenSize.getHeight();
        this.g = g;
        this.l = l;

        JPanel panel = new MyPanel(x1, y1, x2, y2, width, height, g, l);

        add(panel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize((int)width, (int)height);
    }
}