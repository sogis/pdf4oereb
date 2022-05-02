package ch.so.agi.oereb.pdf4oereb.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

public class TestUtils {
    public static List<PdfPage> extractPdf(File file) throws IOException {
        List<PdfPage> pdfPages = new ArrayList<>();
        
        try (PDDocument document = PDDocument.load(file)) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                PdfPage pdfPage = new PdfPage();
                
                // Set the page interval to extract. If you don't, then all pages would be
                // extracted.
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                // let the magic happen
                String text = stripper.getText(document);
                
                pdfPage.setPageNo(p);
                pdfPage.setContent(text.trim());
                
                pdfPages.add(pdfPage);

                // do some nice output with a header
//                String pageStr = String.format("page %d:", p);
//                System.out.println(pageStr);
//                for (int i = 0; i < pageStr.length(); ++i) {
//                    System.out.print("-");
//                }
//                System.out.println();
//                System.out.println(text.trim());
//                System.out.println();

                // If the extracted text is empty or gibberish, please try extracting text
                // with Adobe Reader first before asking for help. Also read the FAQ
                // on the website:
                // https://pdfbox.apache.org/2.0/faq.html#text-extraction
            }
            
            return pdfPages;
        }
    } 
}
