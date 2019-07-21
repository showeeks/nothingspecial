package cn.xiaoheiban.pdfmaker.pdf;

import com.aspose.words.*;

import java.io.FileNotFoundException;
import java.util.Random;

public class FileMaker {
    private String filename;

    private String watermarkFilename;

    private static final String string_Num = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public FileMaker(String filename, String watermarkFilename) {
        this.filename = filename;
        this.watermarkFilename = watermarkFilename;
    }

    /**
     *
     * @throws FileNotFoundException 无法打开文件时会抛出异常的
     */
    public void generatePDF(String filename) throws FileNotFoundException {
        try {
            Document doc = new Document(this.filename);
            insertWatermarkText(doc, this.watermarkFilename);
            doc.save("generate-dir/" + filename);
        } catch (Exception e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public static String filename(int length) {

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(string_Num.charAt(number));
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
