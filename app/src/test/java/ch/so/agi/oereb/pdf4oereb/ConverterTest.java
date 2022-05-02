package ch.so.agi.oereb.pdf4oereb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.so.agi.oereb.pdf4oereb.util.PdfPage;
import ch.so.agi.oereb.pdf4oereb.util.TestUtils;

public class ConverterTest {
    @Test 
    public void convert2Pdf_SO_Images_Geometry_Ok(@TempDir Path tempDir) throws ConverterException, IOException {
        Converter converter = new Converter();
        File resultFile = converter.runXml2Pdf("src/test/data/so/CH955832730623_geometry_images.xml", tempDir.toAbsolutePath().toString(), Locale.IT);

        List<PdfPage> pdfPages = TestUtils.extractPdf(resultFile);
        
    }
}
