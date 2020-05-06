package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestMarkContinuousText {

    @Test
    public void testMergeText() {
        String filename = "/Users/sliu/Downloads/H2_AN202002201375316506_1.pdf";

        try {
            PDDocument pd = PDDocument.load(new File(filename));
            ObjectExtractor oe = new ObjectExtractor(pd);
            Map<Integer, Page> pages = new HashMap<Integer, Page>();
            for (int i=1; i<=pd.getNumberOfPages(); i++) {
                if (i != 339) continue;
                Page page = oe.extractPage(i);
                pages.put(i, page);
            }
            pd.close();


            pd = PDDocument.load(new File(filename));
            for (int i=1; i<=pd.getNumberOfPages(); i++) {
                if (i != 339) continue;

                PDPageContentStream contentStream = new PDPageContentStream(pd, pd.getPage(i-1),
                        PDPageContentStream.AppendMode.APPEND, true, true);
                Page page = pages.get(i);

                List<TextChunk> tcs = page.getChunks();
                for (TextChunk tc : tcs) {
                    float bottom = page.height - tc.getBottom() - tc.getMaxWidthOfSpace()/2;
                    float height = tc.height + tc.getMaxWidthOfSpace();

                    contentStream.moveTo(tc.x, bottom);
                    contentStream.lineTo(tc.x + tc.width, bottom);
                    contentStream.lineTo(tc.x + tc.width, bottom + height);
                    contentStream.lineTo(tc.x, bottom + height);
                    contentStream.lineTo(tc.x, bottom);
                    contentStream.setStrokingColor((int)Math.round(Math.random()*255), 0, 0);
                    contentStream.stroke();
                }

//                List<Rectangle> imgs = page.getImageBoundaries();
//                for (Rectangle img : imgs) {
//                    contentStream.addRect(img.x, img.y, img.width, img.height);
//                    contentStream.setStrokingColor(255, 0, 0);
//                    contentStream.stroke();
//                }



                contentStream.close();
                System.out.printf("Ink Ratio: %f\n", page.inkRatio());
            }
            String fn = new SimpleDateFormat("YYYYMMddHH").format(new Date(System.currentTimeMillis()));
            System.out.println(fn);
            pd.save("/Users/sliu/Downloads/"+fn+".pdf");
            pd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mark() {
        String filename = "/Users/sliu/Downloads/H2_AN202002201375316506_1.pdf";

        try {
            PDDocument pd = PDDocument.load(new File(filename));
            ObjectExtractor oe = new ObjectExtractor(pd);
            Map<Integer, Page> pages = new HashMap<Integer, Page>();
            for (int i=1; i<=pd.getNumberOfPages(); i++) {
                if (i != 10) continue;
                Page page = oe.extractPage(i);
                pages.put(i, page);
            }
            pd.close();


            pd = PDDocument.load(new File(filename));
            for (int i=1; i<=pd.getNumberOfPages(); i++) {
                if (i != 10) continue;

                PDPageContentStream contentStream = new PDPageContentStream(pd, pd.getPage(i-1),
                        PDPageContentStream.AppendMode.APPEND, true, true);
                Page page = pages.get(i);

                List<Ruling> rulings = page.getRulings();
                for (Ruling ruling : rulings) {
                    contentStream.moveTo(ruling.x1, page.height - ruling.y1);
                    contentStream.lineTo(ruling.x2, page.height - ruling.y2);
                    contentStream.setStrokingColor((int)Math.round(Math.random()*255), (int)Math.round(Math.random()*255), (int)Math.round(Math.random()*255));
                    contentStream.stroke();
                }

                float avggap = 0;
                TextElement cur = new TextElement();
                List<TextElement> tes = page.getText();
                for (TextElement te : tes) {
                    if (te.getText().equals(" ")) { // treat empty text as a split
                        continue;
                    }

                    if (cur.width == 0 && cur.height ==0) {
                        cur = te;
                    } else {
                        float gap = Math.abs(cur.x + cur.width - te.x);
//                        avggap = avggap +
                        if (Math.abs(cur.x + cur.width - te.x) < cur.getWidthOfSpace()) { // merge
                            cur.appText(te.getText());
                            cur.setRight(te.x + te.width);
                            cur.height = Math.max(cur.height, te.height);
                            cur.setWidthOfSpace(Math.max(cur.getWidthOfSpace(), te.getWidthOfSpace()));
                        } else { // draw and reinit
                            float bottom = page.height - cur.getBottom() - cur.getWidthOfSpace()/2;
                            float height = cur.height + cur.getWidthOfSpace();

                            contentStream.moveTo(cur.x, bottom);
                            contentStream.lineTo(cur.x + cur.width, bottom);
                            contentStream.lineTo(cur.x + cur.width, bottom + height);
                            contentStream.lineTo(cur.x, bottom + height);
                            contentStream.lineTo(cur.x, bottom);
                            contentStream.setStrokingColor((int)Math.round(Math.random()*255), 0, 0);
                            contentStream.stroke();

                            System.out.println(cur.toString());

                            cur = te;
                        }
                    }
                }
                if (cur.width > 0) {
                    float bottom = page.height - cur.getBottom();
                    float height = cur.height + cur.getWidthOfSpace();

                    contentStream.moveTo(cur.x, bottom);
                    contentStream.lineTo(cur.x + cur.width, bottom);
                    contentStream.lineTo(cur.x + cur.width, bottom + height);
                    contentStream.lineTo(cur.x, bottom + height);
                    contentStream.lineTo(cur.x, bottom);
                    contentStream.setStrokingColor((int)Math.round(Math.random()*255), 0, 0);
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
