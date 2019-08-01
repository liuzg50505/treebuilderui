package com.liuzg.flutteride.flutter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MacosFlutterRunner extends FlutterRunner{

    private BufferedWriter bout;
    private Process process;
    private boolean running;

    private List<OutputListener> outputListeners;

    public MacosFlutterRunner() {
        outputListeners = new ArrayList<>();
    }

    // methods

    public void notifyOutputListeners(String output) {
        for(OutputListener outputListener: outputListeners){
            outputListener.onOutput(output);
        }
    }

    // outlets

    @Override
    public void addOutputListener(OutputListener listener) {
        if(listener!=null) outputListeners.add(listener);
    }

    @Override
    public String name() {
        return "MacOS Runner";
    }

    @Override
    public void startapp() {
        if(running) stopapp();
        ProcessBuilder builder = new ProcessBuilder( "bash","-c","flutter run -t lib/main_dev.dart");

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
                        notifyOutputListeners(line);
                        line = bin.readLine();
                    }
                } catch (IOException e) {
                }

            }).start();

            new Thread(() -> {
                try {
                    String line = berr.readLine();
                    while (line != null){
                        notifyOutputListeners(line);
                        line = berr.readLine();
                    }
                } catch (IOException e) {
                }
            }).start();
            running = true;

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
        if(running) {
            try {
                bout.write("q\n");
                bout.flush();
                Thread.sleep(1000);
                process.destroy();
            } catch (Exception e) {
            }
            running = false;
        }

    }
}
