package org.teneted.neotenet.launcher.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
}
