package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.Test;
import technology.tabula.detectors.NurminenDetectionAlgorithm;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestTableDetect {

    @Test
    public void detect() {
        String filename = "/Users/sliu/Downloads/AL Distance Learning Daily Tasks.pdf";

        try {
            NurminenDetectionAlgorithm nda = new NurminenDetectionAlgorithm();
            PDDocument pd = PDDocument.load(new File(filename));
            ObjectExtractor oe = new ObjectExtractor(pd);
            Map<Integer, Page> pages = new HashMap<Integer, Page>();

            for (int i=1; i<=pd.getNumberOfPages(); i++) {
//                if (i != 10) continue;
                Page page = oe.extractPage(i);
                pages.put(i, page);
                List<Rectangle> rectangles = nda.detect(page);
                page.setRectangles(rectangles);
            }
            pd.close();


            pd = PDDocument.load(new File(filename));
            for (int i=1; i<=pd.getNumberOfPages(); i++) {
//                if (i != 10) continue;

                PDPageContentStream contentStream = new PDPageContentStream(pd, pd.getPage(i-1),
                        PDPageContentStream.AppendMode.APPEND, true, true);
                Page page = pages.get(i);
                List<? extends Rectangle2D.Float> rectangles = page.getRectangles();
                System.out.printf("total rectangles: %d\n", rectangles.size());

                for (Rectangle2D.Float rect : rectangles) {
                    contentStream.moveTo(rect.x, page.height - rect.y);
                    contentStream.lineTo(rect.x + rect.width, page.height - rect.y);
                    contentStream.lineTo(rect.x + rect.width, page.height - rect.y + rect.height);
                    contentStream.lineTo(rect.x, page.height - rect.y + rect.height);
                    contentStream.lineTo(rect.x, page.height - rect.y);
                    contentStream.setStrokingColor(0, 100+(int)Math.round(Math.random()*155), 0);
                    contentStream.stroke();
                }
                contentStream.close();

            }
            String fn = new SimpleDateFormat("YYYYMMddHH").format(new Date(System.currentTimeMillis()));
            System.out.println(fn);
            pd.save("/Users/sliu/Downloads/"+fn+".pdf");
            pd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
