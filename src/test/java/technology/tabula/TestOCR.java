package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixReadMem;

public class TestOCR {

    @Test
    public void pdfocr() {
        try {
            TessBaseAPI api = new TessBaseAPI();
            if (api.Init("/usr/local/Cellar/tesseract/4.1.1/share/tessdata/", "chi_sim+eng") != 0) {
                System.err.println("Could not initialize tesseract.");
                System.exit(1);
            }

            PDDocument doc = PDDocument.load(new File("/Users/sliu/Downloads/xlt-1.pdf"));
            PDFRenderer renderer = new PDFRenderer(doc);
            int dpi = 300;
            for (int i=0; i<doc.getNumberOfPages(); i++) {
                BufferedImage img = renderer.renderImageWithDPI(i, dpi, ImageType.GRAY);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                PIX pixImg = pixReadMem(baos.toByteArray(), baos.size());
                api.SetImage(pixImg);

                // Get OCR result
                BytePointer outText = api.GetUTF8Text();
                System.out.println("OCR output i :\n" + outText.getString());
                outText.deallocate();
                pixDestroy(pixImg);
            }

            // Open input image with leptonica library
//            PIX image = pixRead("/Users/sliu/Downloads/xlt-1.png");




            // Destroy used object and release memory
            api.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
