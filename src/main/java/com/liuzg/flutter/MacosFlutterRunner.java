package com.liuzg.flutter;

import java.io.*;

public class MacosFlutterRunner extends FlutterRunner{

    private BufferedWriter bout;
    private Process process;

    @Override
    public void startapp() {
        ProcessBuilder builder = new ProcessBuilder( "bash","-c","flutter run");

        builder.directory( new File( "/Volumes/macdata/MyProjects/treebuildermvcprj" ).getAbsoluteFile() ); // this is where you set the root folder for the executable to run with
        builder.redirectErrorStream(true);
        process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(process!=null) {
            BufferedReader bin = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader berr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            bout = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            new Thread(() -> {
                try {
                    String line = bin.readLine();
                    while (line != null){
                        System.out.println(line);
                        line = bin.readLine();
                    }
                } catch (IOException e) {
                }

            }).start();

            new Thread(() -> {
                try {
                    String line = berr.readLine();
                    while (line != null){
                        System.out.println(line);
                        line = berr.readLine();
                    }
                } catch (IOException e) {
                }
            }).start();


        }

    }

    @Override
    public void hotload() {
        try {
            bout.write("r\n");
            bout.flush();
        } catch (IOException e) {
        }
    }

    @Override
    public void hotRestart() {
        try {
            bout.write("R\n");
            bout.flush();
        } catch (IOException e) {
        }
    }

    @Override
    public void stopapp() {
        try {
            bout.write("q\n");
            bout.flush();
            Thread.sleep(1000);
            process.destroy();
        } catch (Exception e) {
        }
    }
}
