package com.arextest.web.accurate.util;

import java.io.*;
import java.net.*;
/**
 * Created by Qzmo on 2023/7/18
 */
public class HttpDownload {
    public static String downloadFile(URI fileUrl, String fileName) throws IOException {
        HttpURLConnection httpConn = (HttpURLConnection) fileUrl.toURL().openConnection();
        int responseCode = httpConn.getResponseCode();

        // Check if HTTP response code is successful
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return outputStream.toString();
        } else {
            return null;
        }
    }
}
