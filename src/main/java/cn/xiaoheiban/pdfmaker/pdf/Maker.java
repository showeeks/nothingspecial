package cn.xiaoheiban.pdfmaker.pdf;

import com.aspose.words.*;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Maker {
    private String dest;

    private String data;

    private String watermarkFilename;

    public Maker(String filename, String data, String watermarkFilename) throws Exception {
        this.dest = filename(5);
        this.data = data;
        this.watermarkFilename = watermarkFilename;
        java.util.List<String> values = new LinkedList<String>();
        FileReader reader = new FileReader(data);
        char[] buf = new char[1024];
        int num = reader.read(buf);
        if (num < 0 || num > 1024) {
            throw new Exception("JSON 解析异常");
        }
        JSONObject json = new JSONObject(new String(buf, 0, num));
        Iterator<String> fieldIt = json.keys();
        List<String> fieldNames = new LinkedList<>();
        fieldIt.forEachRemaining(obj -> {
            values.add(json.getString(obj));
            fieldNames.add(obj);
        });
        Document doc = new Document(filename);
        doc.getMailMerge().execute(fieldNames.toArray(new String[0]), values.toArray());
        doc.save(dest + ".docx");
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
