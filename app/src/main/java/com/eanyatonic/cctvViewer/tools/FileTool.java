
package com.eanyatonic.cctvViewer.tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class FileTool {

    private Context context;

    public FileTool(Context context) {
        this.context = context;
    }

    public String readFileContent(String fileName) {
        InputStream inputStream = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();

        try {
            //得到资源中的asset数据流
            inputStream = context.getResources().getAssets().open(fileName);
            reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);// 字符流
            bufferedReader = new BufferedReader(reader); //缓冲流
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                result.append(temp);
            }
            Log.i("fileTool", "result:" + result);
        } catch (Exception e) {
            Log.e("fileError", e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return result.toString();
    }
}
