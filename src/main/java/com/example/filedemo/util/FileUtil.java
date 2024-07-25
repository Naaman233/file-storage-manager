package com.example.filedemo.util;

import org.jetbrains.annotations.NotNull;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

//    public static byte[] compressFile(byte[] data) {
//        Deflater deflater = new Deflater();
//        deflater.setLevel(Deflater.BEST_COMPRESSION);
//        deflater.setInput(data);
//        deflater.finish();
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
//        byte[] temp = new byte[4*1024];
//        while (!deflater.finished()) {
//            int foo = deflater.deflate(temp);
//            outputStream.write(temp,0,foo);
//
//        }
//        try{
//            outputStream.close();
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//        return outputStream.toByteArray();
//    }

    @NotNull
    public static byte[] compressFile(byte[] data, String filename) {

        int memory = data.length;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(memory);
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        ZipEntry entry = new ZipEntry(filename);
        try {
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(data, 0, memory);

            zipOutputStream.close();
            outputStream.close();

            return outputStream.toByteArray();
        } catch(IOException ex) {
            logger.error("An error occured while compressing the file", ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

//    public static byte[] decompressFile(byte[] data) {
//        Inflater inflater = new Inflater();
//        inflater.setInput(data);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
//        byte[] temp = new byte[data.length];
//        try{
//            while (!inflater.finished()) {
//                int count = inflater.inflate(temp);
//                outputStream.write(temp,0,count);
//            }
//            outputStream.close();
//        }catch(DataFormatException | IOException ex) {
//            ex.printStackTrace();;
//        }
//        return outputStream.toByteArray();
//    }
    public static byte[] decompressFile(byte[] data, String filename) {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ZipEntry entry;;
        int len;
        byte[] byteArray = new byte[data.length];
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals(filename)) {
                    while ((len = zipInputStream.read(byteArray)) > 0) {
                        outputStream.write(byteArray, 0, len);
                    }
                    zipInputStream.closeEntry();
                    break;
                }
            }
            zipInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch(IOException ex) {
            logger.error("Error occured decompressing the file", ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static String extractFilter(byte[] data)  {
        try(ByteArrayInputStream stream = new ByteArrayInputStream(data);
            XWPFDocument document = new XWPFDocument(stream)
        ) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        } catch (IOException ex) {
            logger.error("Error extracting bytes from file", ex.getMessage());
        }
        return null;
    }
}
