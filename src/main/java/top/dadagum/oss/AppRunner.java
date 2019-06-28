package top.dadagum.oss;

import com.itextpdf.text.DocumentException;
import top.dadagum.oss.utils.FileUtil;
import top.dadagum.oss.utils.SimpleUploader;

import java.io.*;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * @Description TODO
 * @Author Honda
 * @Date 2019/6/27 20:18
 **/
public class AppRunner {

    // oss 上传路径
    private static final String OSS_IMG = "img/";
    private static final String OSS_DOC = "doc/";
    private static final String OSS_PDF = "pdf/";

    // doc, pdf 本地暂存路径
    private static final String DOC_TMP = "D:\\testing\\tmp\\doc_pdf\\";

    // 最终映射 csv 文件保存路径
    private static final String DOCX_CSV = "D:\\testing\\tmp\\docx.csv";
    private static final String PDF_CSV = "D:\\testing\\tmp\\pdf.csv";

    private static Random random = new Random();


    public static void main(String[] args) throws IOException {
        StringBuilder docStr = new StringBuilder();
        StringBuilder pdfStr = new StringBuilder();
        String fileName;

        // 读取 data.csv 文件
        List<String> list = FileUtil.readCsv("D:\\coding\\javaWorkstation\\sparrow\\test_data\\data.csv");
        for (int i = 1; i < list.size(); i++) {
            String[] items = list.get(i).split(",");

            // 过滤垃圾数据
            if (items.length < 2) {
                continue;
            }

            String id = items[0];
            String url = items[1];

            // 不是 id
            if (!FileUtil.validIdFileName(id)) {
                continue;
            }
            // 获取压缩图片 jpeg，保存到 oss
            try {
                InputStream jpeg = FileUtil.getJpeg(url);
                SimpleUploader.uploadFileStream(jpeg, OSS_IMG + id + ".jpeg");
                jpeg.close();
            } catch (Exception e) {
                System.out.println( "i = " + i + " ,id = " + id + " does not have an image:" + list.get(i));
            }

            // 将一条记录转换成为 doc / pdf 文件
            // 保存转换后的文件到 oss
            // 本地文件记录 id -> 文件属性(pdf / docx)，这里暂时保存到内存中
            boolean sign = random.nextBoolean();
            try {
                if (sign) { // docx
                    fileName = DOC_TMP + id + ".docx";
                    FileUtil.createDocx(list.get(i), fileName);
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
                    String ossName = OSS_DOC + id + ".docx";
                    SimpleUploader.uploadFileStream(bis, ossName);
                    bis.close();
                    docStr.append(id);
                    docStr.append('\n');
                } else {
                    fileName = DOC_TMP + id + ".pdf";
                    FileUtil.createPdf(list.get(i), fileName);
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
                    String ossName = OSS_PDF + id + ".pdf";
                    SimpleUploader.uploadFileStream(bis, ossName);
                    bis.close();
                    pdfStr.append(id);
                    pdfStr.append('\n');
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("i = " + i + " ,id = " + id + " fail to write to file:" + list.get(i));
                FileUtil.appendCsv(DOCX_CSV, docStr.toString());
                FileUtil.appendCsv(PDF_CSV, pdfStr.toString());
                docStr = new StringBuilder();
                pdfStr = new StringBuilder();
            }
        }

        // 保存到本地磁盘
        FileUtil.appendCsv(DOCX_CSV, docStr.toString());
        FileUtil.appendCsv(PDF_CSV, pdfStr.toString());

    }

    public static void countLines() throws IOException {
        List<String> list = FileUtil.readCsv("D:\\testing\\tmp\\docx.csv");
        System.out.println("docx gets " + list.size());
        List<String> list1 = FileUtil.readCsv("D:\\testing\\tmp\\pdf.csv");
        System.out.println("pdf gets " + list1.size());
    }
}
