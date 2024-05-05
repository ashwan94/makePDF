package org.example;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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

        // 1. 파일 저장 경로 지정 + document 객체 생성
        try(FileOutputStream os = new FileOutputStream("/Users/na/Desktop/makePDF/src/main/output/test.pdf")){

            Document document = new Document(PageSize.A4, 50, 50, 50,50);

            PdfWriter writer = PdfWriter.getInstance(document, os);

            document.open();

            // 2. 폰트 정보 설정
            String fontFace = "/Users/na/Desktop/makePDF/src/main/resources/HSSanTokki2.0(2024).ttf";
            String vFont = "UniKS-UCS2-V";

            BaseFont bf = BaseFont.createFont(fontFace, BaseFont.IDENTITY_V, BaseFont.NOT_EMBEDDED);
            Font newFont = new Font(bf, 20);

            BaseFont objBaseFont = BaseFont.createFont(fontFace, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font objFont = new Font(objBaseFont, 20);



            // 3. CSS 입히기
            StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver();

            try(FileInputStream cssStream = new FileInputStream("/Users/na/Desktop/makePDF/src/main/resources/cssFile.css")) {
                cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
            }

            XMLWorkerFontProvider font = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);

            font.register("/Users/na/Desktop/makePDF/src/main/resources/HSSanTokki2.0(2024).ttf", "Rabit");

            CssAppliersImpl cssAppliers = new CssAppliersImpl(font);



            // 4. HTML to PDF 변환
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

            // 5. 이미지 넣기
            Image png = Image.getInstance("/Users/na/Desktop/Screenshot 2024-05-05 at 10.24.22 AM.png");
            png.scalePercent(20);
            document.add(png);


            // 6. 추가 문구 입력
            document.add(new Paragraph("하이!", objFont));
            document.add(new Paragraph("이제 문제가 됐던 한글 폰트도 잘 인식하게 되었다", objFont));
            document.add(new Paragraph("세로로 글자를 출력한다", newFont));

            // 7. 테이블 생성
            PdfPTable table = new PdfPTable(2 );
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("제목", objFont));
            cell.setColspan(3);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("내용도 한글 작성 가능", objFont));
            cell.setRowspan(2);
            table.addCell(cell);

            table.addCell(cell);
            table.addCell(cell);
            table.addCell(cell);
            table.addCell(cell);
            table.addCell("row 2; cell 2");
            table.addCell("row 2; cell 2");
            table.addCell("row 2; cell 2");
            table.addCell("row 2; cell 2");
            table.addCell("row 2; cell 2");
            table.addCell("row 2; cell 2");

            document.add(table);

            // 8. 자원 정리
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
