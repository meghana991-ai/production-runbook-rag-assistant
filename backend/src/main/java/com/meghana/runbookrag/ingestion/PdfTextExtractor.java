package com.meghana.runbookrag.ingestion;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String contentType, String filename) {
        return "application/pdf".equalsIgnoreCase(contentType)
                || filename.toLowerCase().endsWith(".pdf");
    }

    @Override
    public List<DocumentPage> extract(byte[] content) throws IOException {
        try (PDDocument document = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            List<DocumentPage> pages = new ArrayList<>();
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                pages.add(new DocumentPage(page, stripper.getText(document)));
            }
            return pages;
        }
    }
}
