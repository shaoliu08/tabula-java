package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.Test;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestBlock {

    @Test
    public void block() {
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
                Set<Float> xs = new HashSet<>();
                Set<Float> ys = new HashSet<>();
                PDPageContentStream contentStream = new PDPageContentStream(pd, pd.getPage(i-1),
                        PDPageContentStream.AppendMode.APPEND, true, true);
                Page page = pages.get(i);


                TextElement cur = new TextElement();
                List<TextElement> tes = page.getText();
                for (TextElement te : tes) {
                    if (te.getText().equals(" ")) { // treat empty text as a split
                        continue;
                    }

                    if (cur.width == 0 && cur.height ==0) {
                        cur = te;
                    } else {
                        if (Math.abs(cur.x + cur.width - te.x) < cur.getWidthOfSpace()) { // merge
                            cur.appText(te.getText());
                            cur.setRight(te.x + te.width);
                            cur.height = Math.max(cur.height, te.height);
                            cur.setWidthOfSpace(Math.max(cur.getWidthOfSpace(), te.getWidthOfSpace()));
                        } else {
                            float bottom = page.height - cur.getBottom() - cur.getWidthOfSpace()/2;
                            float height = cur.height + cur.getWidthOfSpace();

                            xs.add(cur.x);
                            ys.add(bottom);
                            xs.add(cur.x + cur.width);
                            ys.add(bottom + height);

                            cur = te;
                        }
                    }
                }
                if (cur.width > 0) {
                    float bottom = page.height - cur.getBottom();
                    float height = cur.height + cur.getWidthOfSpace();
                    xs.add(cur.x);
                    ys.add(bottom);
                    xs.add(cur.x + cur.width);
                    ys.add(bottom + height);
                }

                List<Ruling> rulings = page.getRulings();
                for (Ruling ruling : rulings) {
                    if (ruling.length()>50 &&
                            (ruling.vertical() || ruling.horizontal())) {
                        xs.add(ruling.x1);
                        xs.add(ruling.x2);
                        ys.add(page.height - ruling.y1);
                        ys.add(page.height - ruling.y2);
                    }
                }

                float minX=Float.MAX_VALUE, minY=Float.MAX_VALUE, maxX=Float.MIN_VALUE, maxY=Float.MIN_VALUE;
                for (Float x : xs) {
                    minX = Float.min(minX, x);
                    maxX = Float.max(maxX, x);
                }
                for (Float y : ys) {
                    minY = Float.min(minY, y);
                    maxY = Float.max(maxY, y);
                }

                List<Ruling> verticalRulings = new ArrayList<>();
                List<Ruling> horizontalRulings = new ArrayList<>();
                for (Float x : xs) {
                    verticalRulings.add(new Ruling(x, minY, x, maxY));
                }
                for (Float y : ys) {
                    horizontalRulings.add(new Ruling(minX, y, maxX, y));
                }

                verticalRulings = Ruling.collapseOrientedRulings(verticalRulings, 5);
                horizontalRulings = Ruling.collapseOrientedRulings(horizontalRulings, 5);
//                List<? extends Rectangle> cells = SpreadsheetExtractionAlgorithm.findCells(horizontalRulings, verticalRulings);
                System.out.printf("min and max: %f, %f, %f, %f\n", minX, minY, maxX, maxY);
                System.out.printf("page width and height: %f, %f\n", page.width, page.height);
//                System.out.printf("cells: %d\n", cells.size());

                for (List<Ruling> list : Arrays.asList(verticalRulings, horizontalRulings)) {
                    for (Ruling ruling : list) {
                        contentStream.moveTo(ruling.x1, ruling.y1);
                        contentStream.lineTo(ruling.x2, ruling.y2);
                        if (ruling.horizontal()) {
                            contentStream.setStrokingColor(0, 100 + (int) Math.round(Math.random() * 155), 0);
                        } else {
                            contentStream.setStrokingColor(100 + (int) Math.round(Math.random() * 155), 0, 0);
                        }
                        contentStream.stroke();
                    }
                }
                System.out.printf("Total x lines: %d, y lines: %d\n", xs.size(), ys.size());
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
