package com.example.leetcodecloneapi.executer;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExecuterService {
    public String runCode(CodeEntity codeEntity){
        try {
            String currentWorkingDirectory = System.getProperty("user.dir");
            String codeDirectory = null;
            String fileName;
            String className = "";
            String type = codeEntity.getType();
            String no = codeEntity.getNo();
            String code = codeEntity.getCode();

            if(type.equalsIgnoreCase("python")){
                codeDirectory = currentWorkingDirectory+"/code/python";
                fileName = no+".py";
            }else if(type.equalsIgnoreCase("java")){
                codeDirectory = currentWorkingDirectory+"/code/java";
                className = findJavaClass(code);
                if(className == null){
                    return "Java 未傳入正確類別名稱。";
                }
                fileName = className+".java";
                compileJava(codeDirectory, fileName);
            }else{
                return "傳入不支援的語言類型。";
            }
            // Write code to file system
            writeCodeToFile(code, codeDirectory, fileName);
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

    private String findJavaClass(String code) {
        // 定義正規表達式模式
        String pattern = "public class (\\w+).*?public static void main\\(.*\\)";

        // 使用 Pattern 和 Matcher 進行匹配
        Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = r.matcher(code);
        // 如果找到匹配，就印出結果
        if (m.find()) {
            System.out.println("Found value: " + m.group(1));
            return m.group(1);
        } else {
            System.out.println("No match found");
            return null;
        }
    }

    private static void compileJava(String fileDirectory, String fileName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("javac", fileDirectory + "/" + fileName);
        // 啟動進程
        processBuilder.start();
    }

    private static void writeCodeToFile(String code, String fileDirectory, String fileName) throws IOException{
        System.out.println(fileDirectory + "/"+ fileName);
        File file = new File(fileDirectory + "/"+ fileName);  // 修改路徑
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(code);
        fileWriter.close();
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
