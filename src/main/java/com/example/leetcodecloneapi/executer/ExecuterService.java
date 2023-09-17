package com.example.leetcodecloneapi.executer;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExecuterService {
    public String runCode(CodeEntity codeEntity){
        try {
//            String currentWorkingDirectory = System.getProperty("user.dir");
            String codeDirectory = null;
            String fileName;
            String className = "";
            String type = codeEntity.getType();
            String no = codeEntity.getNo();
            String code = codeEntity.getCode();

            checkFolderExit();

            if(type.equalsIgnoreCase("python")){
//                codeDirectory = currentWorkingDirectory+"/code/python";
                codeDirectory = "code/python";
                fileName = no+".py";
                // Write code to file system
                writeCodeToFile(code, codeDirectory, fileName);
            }else if(type.equalsIgnoreCase("java")){
//                codeDirectory = currentWorkingDirectory+"/code/java";
                codeDirectory = "code/java";
                className = findJavaClass(code);
                if(className == null){
                    return "Java 未傳入正確類別名稱。";
                }
                fileName = className+".java";
                // Write code to file system
                writeCodeToFile(code, codeDirectory, fileName);
                Process processCompile = compileJava(codeDirectory, fileName);
                InputStream inputStream = processCompile.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                // 讀取輸出
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                }
            }else{
                return "傳入不支援的語言類型。";
            }
            Process process = runProcess(type, className, codeDirectory, fileName);
            // 等待進程結束
            int exitCode = process.waitFor();
            // 取得進程的輸出流
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // 讀取輸出
            String res = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                res += line + "\n";
            }
//            res = res.substring(0,res.lastIndexOf("\n"));
            System.out.println("Exit code: " + exitCode);

            return res;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Something get Wrong";
        }
    }

    private void checkFolderExit() {
        File pyFolder, javaFolder, codeFolder;
        codeFolder = new File("code");
        pyFolder = new File("code/python");
        javaFolder = new File("code/java");
//        System.out.println("codeFolder is "+codeFolder.getAbsolutePath());
//        System.out.println("pyFolder is "+pyFolder.getAbsolutePath());
//        System.out.println("javaFolder is "+javaFolder.getAbsolutePath());
        if(!codeFolder.exists()){
            boolean success = codeFolder.mkdir();
            if (success) {
                System.out.println("Code directory create success");
            } else {
                System.out.println("Code directory create fail");
            }
        }
        if(!pyFolder.exists()){
            boolean success = pyFolder.mkdir();
            if (success) {
                System.out.println("Python directory create success");
            } else {
                System.out.println("Python directory create fail");
            }
        }
        if(!javaFolder.exists()){
            boolean success = javaFolder.mkdir();
            if (success) {
                System.out.println("Java directory create success");
            } else {
                System.out.println("Java directory create fail");
            }
        }
    }

    private String findJavaClass(String code) {
        // 定義正規表達式模式
        // (\w+): 用來匹配一個或多個字符（字母、數字、底線）
        // .*?: 這部分用來匹配任意字符（除了換行符）零次或多次，表示我們會匹配class定義後的所有可能的內容，直到接下來的模式
        // 這個正規表達式尋找一個以 "public class" 開頭的類定義，並捕獲了class名稱。接著，它會匹配class定義後的所有內容，直到找到包含 public static void main(...) method的定義。
        String pattern = "public class (\\w+).*?public static void main\\(.*\\)";

        // 使用 Pattern 和 Matcher 進行匹配
        Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = r.matcher(code);
        // 如果找到匹配，就印出結果
        if (m.find()) {
            System.out.println("Found class: " + m.group(1));
            return m.group(1);
        } else {
            System.out.println("No class found");
            return null;
        }
    }

    private static Process compileJava(String fileDirectory, String fileName) throws IOException {
        System.out.println("Compile:  javac "+ fileDirectory + "/" + fileName);
        ProcessBuilder processBuilder = new ProcessBuilder("javac", fileDirectory + "/" + fileName);
//        ProcessBuilder processBuilder = new ProcessBuilder("pwd");
        // 啟動進程

        return processBuilder.start();
    }

    private static void writeCodeToFile(String code, String fileDirectory, String fileName) throws IOException{
        code = encodeUTF8(code);
        System.out.println("Write to "+fileDirectory + "/"+ fileName);
        File file = new File(fileDirectory + "/"+ fileName);  // 修改路徑
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(code);
        fileWriter.close();
    }

    private static String encodeUTF8(String code) {
        try {
            byte[] utf8Bytes = code.getBytes("UTF-8"); // 將字串轉換為UTF-8編碼的位元組陣列
            String utf8String = new String(utf8Bytes, "UTF-8"); // 將UTF-8編碼的位元組陣列轉換回字串

            // System.out.println("原始字串: " + code);
            // System.out.println("UTF-8 字串: " + utf8String);
            return utf8String;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Process runProcess(String type, String className, String fileDirectory, String fileName) throws IOException {
        // 建立一個 ProcessBuilder
        ProcessBuilder processBuilder;
        if(type.equalsIgnoreCase("python")){
            processBuilder = new ProcessBuilder("python3", fileDirectory + "/" + fileName);
        }else{
//            System.out.println("java "+ className);
            processBuilder = new ProcessBuilder("java", className);
            // 設定工作目錄
            processBuilder.directory(new File(fileDirectory));
        }

        // 啟動進程
        return processBuilder.start();
    }
}
