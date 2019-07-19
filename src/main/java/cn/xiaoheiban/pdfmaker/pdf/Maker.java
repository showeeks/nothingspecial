package cn.xiaoheiban.pdfmaker.pdf;

import com.aspose.words.*;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Maker {
    private String filename;
    private String dest;

//    private String data;

    private String watermarkFilename;

    public Maker(String filename, String watermarkFilename) {
        this.filename = filename;
        this.dest = filename(5);
        this.watermarkFilename = watermarkFilename;
    }

    /**
     *
     * @return 文件名（不含后缀名）
     * @throws Exception
     */
    public String generatePDF() throws Exception {
        Document doc = new Document(this.filename);
        insertWatermarkText(doc, this.watermarkFilename);
        doc.save("generate-dir/" + this.dest + ".pdf");
        return this.dest;
    }

    private static String filename(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private static void insertWatermarkText(Document doc, String watermarkFile) throws Exception {
        DocumentBuilder builder = new DocumentBuilder(doc);
        Shape watermark = builder.insertImage(watermarkFile, 600, 800);
        watermark.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
        watermark.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
        watermark.setWrapType(WrapType.NONE);
        watermark.setVerticalAlignment(VerticalAlignment.CENTER);
        watermark.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Paragraph watermarkPara = new Paragraph(doc);
        watermarkPara.appendChild(watermark);
        for (Section sect : doc.getSections()) {
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_PRIMARY);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_FIRST);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_EVEN);
        }
    }

    private static void insertWatermarkIntoHeader(Paragraph watermarkPara, Section sect, int headerType) throws Exception {
        HeaderFooter header = sect.getHeadersFooters().getByHeaderFooterType(headerType);
        if (header == null) {
            header = new HeaderFooter(sect.getDocument(), headerType);
            sect.getHeadersFooters().add(header);
        }
        header.appendChild(watermarkPara.deepClone(true));
    }


}
