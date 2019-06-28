package top.dadagum.oss.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @Description TODO
 * @Author Honda
 * @Date 2019/6/27 20:58
 **/
public class FileUtil {

    /**
     * @param fileName
     * @return ["item1,item2", "item3,item4"]
     * @throws IOException
     */
    public static List<String> readCsv(String fileName) throws IOException {
        List<String> items = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
        String line;
        while ((line = br.readLine()) != null) {
            items.add(line);
        }
        br.close();
        return items;
    }

    public static List<String> readCsv(String fileName, int cnt) throws IOException {
        List<String> items = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
        String line = br.readLine();
        while (cnt > 0 && (line = br.readLine()) != null) {
            items.add(line);
            cnt--;
        }
        br.close();
        return items;
    }

    public static void appendCsv(String fileName, String str) throws IOException {
        File file = new File(fileName);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(file.length());
        raf.write(str.getBytes());
        raf.close();
    }

    /**
     * 得到压缩图片的二进制流
     * @param url
     * @return
     * @throws IOException
     */
    public static InputStream getJpeg(String url) throws IOException {
        return new URL(url).openStream();
    }

    /**
     * 获取压缩图片并保存到本地磁盘
     * @param url
     * @param path
     * @throws IOException
     */
    public static void getJpegAndSave(String url, String path) throws IOException {
        InputStream is = new URL(url).openStream();
        FileOutputStream fos = new FileOutputStream(new File(path));
        is.transferTo(fos);
        fos.close();
    }

    /**
     * 简单创建 pdf
     * 参考：https://www.vogella.com/tutorials/JavaPDF/article.html
     *       https://howtodoinjava.com/library/read-generate-pdf-java-itext/
     *       https://www.baeldung.com/java-pdf-creation
     * 解决中文乱码：https://blog.csdn.net/xxj_jing/article/details/70888801
     */
    public static void createPdf(String paragraph, String path) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
        String font_cn = getChineseFont();
        BaseFont baseFont = BaseFont.createFont(font_cn + ",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont, 12);
        document.add(new Paragraph(paragraph, font));
        document.close();
        writer.close();
    }

    public static void createPdfWithPdfBox(String paragraph, String path) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(PDType1Font.COURIER, 12);
        contentStream.beginText();
        contentStream.showText(paragraph);
        contentStream.endText();
        contentStream.close();

        document.save(path);
        document.close();
    }

    /**
     * 简单创建 docx
     * 参考：https://stackoverflow.com/questions/2592579/how-can-i-create-a-simple-docx-file-with-apache-poi
     */
    public static void createDocx(String paragraph, String path) throws IOException {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph tmpParagraph = document.createParagraph();
        XWPFRun tmpRun = tmpParagraph.createRun();
        tmpRun.setText(paragraph);
        tmpRun.setFontSize(18);
        document.write(new FileOutputStream(new File(path)));
        document.close();
    }

    public static boolean validIdFileName(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }
        for (int i = 0; i < fileName.length(); i++) {
            if (fileName.charAt(i) < '0' || fileName.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    private static String getChineseFont(){
        //宋体（对应css中的 属性 font-family: SimSun; /*宋体*/）
        String font1 ="C:/Windows/Fonts/simsun.ttc";

        //判断系统类型，加载字体文件
        java.util.Properties prop = System.getProperties();
        String osName = prop.getProperty("os.name").toLowerCase();
       // System.out.println(osName);
        if (osName.contains("linux")) {
            font1="/usr/share/fonts/simsun.ttc";
        }
        if(!new File(font1).exists()){
            throw new RuntimeException("字体文件不存在,影响导出pdf中文显示！"+font1);
        }
        return font1;
    }


}
