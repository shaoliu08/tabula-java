package technology.tabula;

import org.junit.Test;

import java.awt.geom.Line2D;

public class TestClip {

    @Test
    public void clip() {
        Ruling r = new Ruling(100, 100, 300, 300);
        CohenSutherlandClipping csc = new CohenSutherlandClipping(r.getBounds());
        Line2D.Float line = new Line2D.Float(80, 100, 120, 100);
        boolean clipped = csc.clip(line);
        System.out.printf("new line: %f, %f, %f, %f, %b\n", line.x1, line.y1, line.x2, line.y2, clipped);

        line = new Line2D.Float(180, 200, 120, 100);
        clipped = csc.clip(line);
        System.out.printf("new line: %f, %f, %f, %f, %b\n", line.x1, line.y1, line.x2, line.y2, clipped);
    }
}
