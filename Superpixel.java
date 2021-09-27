// Superpixel.java: Bresenham algorithms for lines and circles
// demonstrated by using superpixels.
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Superpixel extends Frame {
    public static void main(String[] args) throws FileNotFoundException {
        List<int[]> values = readInput();
        new Superpixel(values);
    }
    Superpixel(List<int[]> values) {
        super("Bresenham");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {System.exit(0);}
        });
        setSize(340, 230);
        add("Center", new CvBresenham(values));
        setVisible(true);
    }

    static List<int[]> readInput() throws FileNotFoundException {
        File file = new File("input.txt");
        Scanner sc = new Scanner(file);
        int[] circleDetails = new int[3];
        List<int[]> endPoints = new ArrayList<>();

        //Stores number of lines into variable
        int numOfLines = Integer.parseInt(sc.nextLine());

        //Stores end points into arraylist
        for (int i = 0; i < (numOfLines - 1); i++) {
            //Stores a line and splits by spaces
            String[] parsedLine = sc.nextLine().split(" ");
            //Stores end points into array
            int[] endPointDetails = new int[4];
            endPointDetails[0] = Integer.parseInt(parsedLine[0]);
            endPointDetails[1] = Integer.parseInt(parsedLine[1]);
            endPointDetails[2] = Integer.parseInt(parsedLine[2]);
            endPointDetails[3] = Integer.parseInt(parsedLine[3]);
            endPoints.add(endPointDetails);
        }

        //Stores circle details
        String[] parsedLine = sc.nextLine().split(" ");
        circleDetails[0] = Integer.parseInt(parsedLine[0]);
        circleDetails[1] = Integer.parseInt(parsedLine[1]);
        circleDetails[2] = Integer.parseInt(parsedLine[2]);
        
        endPoints.add(circleDetails);

        /*
        N (int: number of lines up to 10)
        X1a Y1a X1b Y1b (int: endpoints of line 1)
        X2a Y2a X2b Y2b (int: endpoints of line 2)
        …
        XNa YNa XNb YNb (int: endpoints of line N)
        Xc Yc R (int: Center point co-ordinates and Radius of circle)

         */

        return endPoints;
    }
}
class CvBresenham extends Canvas {
    float rWidth = 10.0F, rHeight = 7.5F, pixelSize;
    int centerX, centerY, dGrid = 10, maxX, maxY;
    List<int[]> values;

    public CvBresenham(List<int[]> data) {
        values = data;
    }

    void initgr() {
        Dimension d = getSize();
        maxX = d.width - 1; maxY = d.height - 1;
        pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
        centerX = maxX / 2; centerY = maxY / 2;
    }
    int iX(float x) {return Math.round(centerX + x / pixelSize);}
    int iY(float y) {return Math.round(centerY - y / pixelSize);}
    void putPixel(Graphics g, int x, int y) {
        int x1 = x * dGrid, y1 = y * dGrid, h = dGrid / 2;
        g.drawOval(x1 - h, y1 - h, dGrid, dGrid);
    }
    void drawLine(Graphics g, int xP, int yP, int xQ, int yQ) {
        int x = xP, y = yP, D = 0, HX = xQ - xP, HY = yQ - yP,
                c, M, xInc = 1, yInc = 1;
        if (HX < 0) {xInc = -1; HX = -HX;}
        if (HY < 0) {yInc = -1; HY = -HY;}
        if (HY <= HX) {
            c = 2 * HX; M = 2 * HY;
            for (;;) {
                putPixel(g, x, y);
                if (x == xQ) break;
                x += xInc;
                D += M;
                if (D > HX) {y += yInc; D -= c;}
            }
        } else {
            c = 2 * HY; M = 2 * HX;
            for (;;) {
                putPixel(g, x, y);
                if (y == yQ) break;
                y += yInc; D += M;
                if (D > HY) {x += xInc; D -= c;}
            }
        }
    }
    void drawCircle(Graphics g, int xC, int yC, int r) {
        int x = 0, y = r, u = 1, v = 2 * r - 1, E = 0;
        while (x < y) {
            putPixel(g, xC + x, yC + y); // NNE
            putPixel(g, xC + y, yC - x); // ESE
            putPixel(g, xC - x, yC - y); // SSW
            putPixel(g, xC - y, yC + x); // WNW
            x++; E += u; u += 2;
            if (v < 2 * E) {y--; E -= v; v -= 2;}
            if (x > y) break;
            putPixel(g, xC + y, yC + x); // ENE
            putPixel(g, xC + x, yC - y); // SSE
            putPixel(g, xC - y, yC - x); // WSW
            putPixel(g, xC - x, yC + y); // NNW
        }
    }
    void showGrid(Graphics g) {
        for (int x = dGrid; x <= maxX; x += dGrid)
            for (int y = dGrid; y <= maxY; y += dGrid)
                g.drawLine(x, y, x, y);
    }
    public void paint(Graphics g) {
        initgr();
        showGrid(g);

        //Outputs points
        for (int i = 0; i < values.size() - 1; i++)
        {
            int xP1 = values.get(i)[0];
            int yP1 = values.get(i)[1];
            int xP2 = values.get(i)[2];
            int yP2 = values.get(i)[3];
            drawLine(g, xP1, yP1, xP2, yP2);
        }

        //Outputs circle
        int xCircle = values.get(values.size() - 1)[0];
        int yCircle = values.get(values.size() - 1)[1];
        int rCircle = values.get(values.size() - 1)[2];
        drawCircle(g, xCircle, yCircle, rCircle);
    }
}