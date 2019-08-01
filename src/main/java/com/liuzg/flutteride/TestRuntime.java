package com.liuzg.flutteride;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;

public class TestRuntime {

    public static void main(String[] args) throws IOException {

        SAXReader reader = new SAXReader();
        try{
            Document document = reader.read(new File("/Volumes/macdata/MyProjects/treebuildermvcprj/assets/design.xml"));
            Element root = document.getRootElement();
            Element widgetselems = root.element("widgets");
            for(Object widgetnode: widgetselems.elements()) {
                Element widgetelem = (Element) widgetnode;
                if(widgetelem.elements().size()>0) {
                    Element roottreenodexml = (Element) widgetelem.elements().get(0);

                    String controllername = widgetelem.attribute("controller").getStringValue();
                }

            }
        }catch (Exception e) {}


//        ProcessBuilder builder = new ProcessBuilder( "bash","-c","flutter run");
//
//        builder.directory( new File( "/Volumes/macdata/MyProjects/treebuildermvcprj" ).getAbsoluteFile() ); // this is where you set the root folder for the executable to run with
//        builder.redirectErrorStream(true);
//        Process process =  builder.start();
//        BufferedReader bin = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        BufferedReader berr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//
//        new Thread(() -> {
//            try {
//                String line = bin.readLine();
//                while (line != null){
//                    System.out.println(line);
//                    line = bin.readLine();
//                }
//            } catch (IOException e) {
//
//            }
//
//        }).start();
    }
}
