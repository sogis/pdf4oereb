package ch.so.agi.oereb.pdf4oereb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConverterTest {
    @Test 
    public void convert2Pdf_SO_Images_Geometry_Ok(@TempDir Path tempDir) throws ConverterException, IOException {
        Converter converter = new Converter();
        File resultFile = converter.runXml2Pdf("src/test/data/so/CH955832730623_geometry_images.xml", tempDir.toAbsolutePath().toString(), Locale.DE);

        try (PDDocument document = PDDocument.load(resultFile)) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            
            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                // Set the page interval to extract. If you don't, then all pages would be extracted.
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                // let the magic happen
                String text = stripper.getText(document);

                // do some nice output with a header
                String pageStr = String.format("page %d:", p);
                System.out.println(pageStr);
                for (int i = 0; i < pageStr.length(); ++i)
                {
                    System.out.print("-");
                }
                System.out.println();
                System.out.println(text.trim());
                System.out.println();

                // If the extracted text is empty or gibberish, please try extracting text
                // with Adobe Reader first before asking for help. Also read the FAQ
                // on the website: 
                // https://pdfbox.apache.org/2.0/faq.html#text-extraction
            }

            
        }
        
        
    }
}
