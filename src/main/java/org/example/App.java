package org.example;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import java.io.*;
import java.nio.charset.Charset;

public class App {
    public static void main(String[] args) {

        String htmlStr = "<html>" +
                "<head></head>" +
                "<body style=\"font-family:Rabit;\">" +
                "<div>PDF 가 잘 나오는지 테스트</div>" +
                "<div>그런데 왜 CSS 는 적용이 안되는걸까</div>" +
                "</body>" +
                "</html>";

        try(FileOutputStream os = new FileOutputStream("/Users/na/Desktop/makePDF/src/main/output/test.pdf")){

            Document document = new Document(PageSize.A4, 50, 50, 50,50);

            PdfWriter writer = PdfWriter.getInstance(document, os);

            document.open();

            BaseFont objBaseFont = BaseFont.createFont("/Users/na/Desktop/makePDF/src/main/resources/HSSanTokki2.0(2024).ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font objFont = new Font(objBaseFont, 20);

            StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver();

            try(FileInputStream cssStream = new FileInputStream("/Users/na/Desktop/makePDF/src/main/resources/cssFile.css")) {
                cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
            }

            XMLWorkerFontProvider font = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);

            font.register("/Users/na/Desktop/makePDF/src/main/resources/HSSanTokki2.0(2024).ttf", "Rabit");

            CssAppliersImpl cssAppliers = new CssAppliersImpl(font);



            // HTML to PDF 변환
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

            PdfWriterPipeline pdfPipeline = new PdfWriterPipeline(document, writer);

            HtmlPipeline htmlPipeline = new HtmlPipeline(htmlContext, pdfPipeline);

            CssResolverPipeline cssResolverPipeline = new CssResolverPipeline(cssResolver, htmlPipeline);

            XMLWorker worker = new XMLWorker(cssResolverPipeline, true);

            XMLParser xmlParser = new XMLParser(true, worker, Charset.forName("UTF-8"));

            try(StringReader strReader = new StringReader(htmlStr)){
                xmlParser.parse(strReader);
            }

            Image png = Image.getInstance("/Users/na/Desktop/Screenshot 2024-05-05 at 10.24.22 AM.png");
            png.scalePercent(20);
            document.add(png);

            document.add(new Paragraph("하이!", objFont));
            document.add(new Paragraph("이제 문제가 됐던 한글 폰트도 잘 인식하게 되었다", objFont));

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
