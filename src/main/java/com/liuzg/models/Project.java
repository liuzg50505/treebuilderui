package com.liuzg.models;

import com.liuzg.def.DefinitionManager;
import com.liuzg.storage.TreeReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectPath;
    private List<File> designXmlFiles;
    private List<String> ignoredPrefixList;
    private DefinitionManager definitionManager;
    private List<Design> designs;

    public Project() {
        designXmlFiles = new ArrayList<>();
        designs = new ArrayList<>();
        ignoredPrefixList = new ArrayList<>();
        ignoredPrefixList.add("/build");
        ignoredPrefixList.add("/desktop");

    }

    // getters and setters
    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public void setTreeNodeDefinitionManager(DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
    }

    // outlets

    public void scanFile() {
        // scan design files
        scanDesignfiles();
    }

    public List<Design> getDesigns() {
        return designs;
    }

    public void addIgnorePrefix(String prefix) {
        ignoredPrefixList.add(prefix);
    }

    // private methods
    private void scanDesignfiles() {
        // find all xmlfile
        ArrayList<File> xmlFiles = new ArrayList<>();
        findXmlFiles(new File(projectPath), xmlFiles);
        // find all design files
        designXmlFiles.clear();
        for(File xmlFile: xmlFiles) {
            if(isDesignFile(xmlFile)&&!matchIgnorePrefix(xmlFile)){
                designXmlFiles.add(xmlFile);
            }
        }
        // read all widgets
        designs.clear();
        for(File designXmlFile: designXmlFiles) {
            Widget widget = readWidget(designXmlFile);
            if(widget==null) continue;
            Design design = new Design();
            design.setProject(this);
            design.setRelativePath(relativePath(new File(projectPath), designXmlFile));
            design.setWidget(widget);
            widget.setDesign(design);
            designs.add(design);
        }
    }

    private Widget readWidget(File designFile) {
        TreeReader reader = new TreeReader(definitionManager);
        return reader.readFile(designFile.getAbsolutePath());
    }

    private boolean matchIgnorePrefix(File xmlFile) {
        String rpath = relativePath(new File(projectPath), xmlFile);
        for (String ignoreprefix: ignoredPrefixList) {
            if(rpath.startsWith(ignoreprefix)){
                return true;
            }
        }
        return false;
    }

    // utils methods
    private static void findXmlFiles(File dir, List<File> result) {
        File[] list = dir.listFiles();
        if (list != null)
            for (File file : list) {
                if (file.isDirectory()) {
                    findXmlFiles(file, result);
                } else if (file.getName().toLowerCase().endsWith(".xml")) {
                    result.add(file);
                }
            }
    }

    private static boolean isDesignFile(File xmlFile) {
        SAXReader reader = new SAXReader();
        try{
            Document document = reader.read(xmlFile);
            Element root = document.getRootElement();
            if(root.getName().equals("widget")){
                return root.attribute("name")!=null;
            }
        }catch (Exception ignored) {}
        return false;
    }

    private static String relativePath(File dir, File file) {
        String path1 = dir.getAbsolutePath();
        String path2 = file.getAbsolutePath();
        return path2.substring(path1.length());
    }

}
