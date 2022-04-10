package com.example.financialassessment.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.Map;
import java.util.UUID;

@Component
public class PdfGeneratorUtil {
    @Autowired
    private TemplateEngine templateEngine;
    public File createPdf(String fileName, Map map, String... templateName) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");
        Context ctx = new Context();
        if (map != null) {
            for (Object o : map.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

//        String processedHtml = templateEngine.process(templateName, ctx);
        FileOutputStream os = null;
//        String fileName = name + "_" + UUID.randomUUID().toString();
        File directory = new File("/Users/ishween.kaur/Documents/workspace/financialAssessment/");
        final File outputFile = File.createTempFile(fileName, ".pdf", directory);

        try {
            System.out.println(outputFile.getAbsolutePath() + " : " + outputFile.getPath() + " : " + outputFile.getCanonicalPath());
            os = new FileOutputStream(outputFile);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(templateEngine.process(templateName[0],ctx));
            renderer.layout();
            renderer.createPDF(os, false, 0);
            // each page after the first we add using layout() followed by writeNextDocument()
            for (int i = 1; i < templateName.length; i++) {
                renderer.setDocumentFromString(templateEngine.process(templateName[i],ctx));
                renderer.layout();
                renderer.writeNextDocument(i);
            }

            renderer.finishPDF();
            System.out.println("PDF created successfully");
        }
        finally {
            outputFile.deleteOnExit();
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { /*ignore*/ }
            }
        }
        return outputFile;
    }
}
