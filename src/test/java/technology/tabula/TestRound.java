package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;
import org.junit.Ignore;
import org.junit.Test;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestRound {
    public static float round1(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
//        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
    public static float round2(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
//        bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Ignore
    public void round() {
        double d = Math.PI;
        float pi1 = round1(d, 5);
        float pi2 = round2(d, 5);
        assertEquals(pi1, pi2, 0.00001);
    }

    private boolean isPrintable(String s) {
        Character c;
        Character.UnicodeBlock block;
        boolean printable = false;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            block = Character.UnicodeBlock.of(c);
            printable |= !Character.isISOControl(c) && block != null && block != Character.UnicodeBlock.SPECIALS;
        }
        return printable;
    }

    @Ignore
    public void printable() {
        assertEquals(Boolean.TRUE, isPrintable("\u001Ahello"));;
    }


    public void textExtract() {
        String filename = "";
        try {
            PDDocument doc = PDDocument.load(new File(filename));
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

//            pdfTextStripper.process();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pdfExtract() {
        String filename = "/Users/sliu/Downloads/xlt-1.pdf";
        try {
            PDDocument pd = PDDocument.load(new File(filename));
            PDFont font = PDType0Font.load(pd, new FileInputStream("/Users/sliu/Downloads/arialuni.ttf"), false);
            ObjectExtractor oe = new ObjectExtractor(pd);
            PageIterator pi = oe.extract();
            List<Page> pages = new ArrayList<Page>();
            while (pi.hasNext()) {
                Page page = pi.next();
                pages.add(page);
            }
            pd.close();

            pd = PDDocument.load(new File(filename));
            for (int i=0; i<pages.size(); i++) {
                PDPageContentStream contentStream = new PDPageContentStream(pd, pd.getPage(i),
                        PDPageContentStream.AppendMode.APPEND, true, true);
                Page page = pages.get(i);

                List<Ruling> rulings = page.getRulings();
                for (Ruling ruling : rulings) {
                    contentStream.moveTo(ruling.x1, page.height - ruling.y1);
                    contentStream.lineTo(ruling.x2, page.height - ruling.y2);
                    contentStream.setStrokingColor((int)Math.round(Math.random()*255), (int)Math.round(Math.random()*255), (int)Math.round(Math.random()*255));
                    contentStream.stroke();
                }

                List<TextElement> tes = page.getText();
                for (TextElement te : tes) {
                    if (te.getText().equals(" ")) continue;
                    float teheight = (te.getFont().getFontDescriptor().getCapHeight()) / 1000 * te.getFontSize();
                    float tewidth = (te.getFont().getFontDescriptor().getMaxWidth()) / 1000 * te.getFontSize();
                    contentStream.moveTo(te.x, page.height - te.y - te.height);
                    contentStream.lineTo(te.x + te.width, page.height - te.y - te.height);
//                contentStream.lineTo((float) te.x + te.width, page.height - te.y + height);
//                contentStream.lineTo((float) te.x, (float) te.y+te.height);
//                contentStream.lineTo((float) te.x, (float) te.y);
//                    contentStream.addRect(te.x, page.height - te.y - te.height, te.width, te.height);
                    contentStream.setStrokingColor((int)Math.round(Math.random()*255), 0, 0);
                    contentStream.stroke();

//                contentStream.moveTo((float) te.x, (float) te.y);
//                contentStream.beginText();

//                contentStream.setTextMatrix(new Matrix(1f, 0f, 0f, -1f, 0f, page.height));
//                contentStream.newLineAtOffset(te.x, page.height - te.y);
//                contentStream.setFont(te.getFont(), te.getFontSize());
//                contentStream.showText(te.getText());
//                contentStream.endText();
//                System.out.printf("Text(%s)\n", te.toString());
                }
                contentStream.close();

            }
//            PDDocument out = new PDDocument();
//            PDPage newpage = new PDPage();
//            out.addPage(newpage);
//            PDPageContentStream contentStream = new PDPageContentStream(pd, pd.getPage(0),
//                    PDPageContentStream.AppendMode.APPEND, true, true);
//            BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
//            bea.extract(page);
//            contentStream.transform(new Matrix(new AffineTransform(1, 0, 0, -1, 0, page.height)));
//            newpage.setCropBox(new PDRectangle(0, 0, page.width, page.height));
//            contentStream.concatenate2CTM(new AffineTransform(1, 0, 0, -1, 0, page.height));
//            newpage.setRotation(180);


//            out.save(new File("/Users/sliu/Downloads/04262.pdf"));
//            out.close();
            pd.save("/Users/sliu/Downloads/04263.pdf");
            pd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
