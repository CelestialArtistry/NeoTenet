package org.teneted.neotenet.launcher.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class FileUtils {
    public static File copyToTempFile(InputStream inputStream) throws IOException {
        File temp = File.createTempFile("neotent", new Random().nextInt() + "");
        copyTo(inputStream, new FileOutputStream(temp));
        return temp;
    }

    public static void copyTo(InputStream inputStream, OutputStream outputStream) throws IOException {
        inputStream.transferTo(outputStream);
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    public static void download(String urlStr, File target) throws IOException {
        URL url = new URL(urlStr);
        File root = target.getParentFile();
        if (root.isFile()) root.delete();
        if (!root.exists()) root.mkdirs();
        target.createNewFile();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        copyTo(conn.getInputStream(), new FileOutputStream(target));
        conn.disconnect();
    }

    public static boolean checkFile(File target, String sha1) {
        return true;
    }

    public static void copy(File src, File target) {
        try {
            if (!target.exists()) target.createNewFile();
            FileUtils.copyTo(new FileInputStream(src), new FileOutputStream(target));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String readText(File argsFile) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileUtils.copyTo(new FileInputStream(argsFile), bos);
        return bos.toString(StandardCharsets.UTF_8);
    }

    public static List<String> readTexts(File argsFile) throws IOException {
        return List.of(readText(argsFile).split("\n"));
    }
}
