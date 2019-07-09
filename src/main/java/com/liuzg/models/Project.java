package com.liuzg.models;

import com.liuzg.storage.TreeReader;
import com.liuzg.treenodes.TreeNodeDefinitionManager;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectPath;
    private List<File> designXmlFiles;
    private List<String> ignoredPrefixList;
    private TreeNodeDefinitionManager treeNodeDefinitionManager;
    private List<Design> designs;

    public Project() {
        designXmlFiles = new ArrayList<>();
        designs = new ArrayList<>();
        ignoredPrefixList = new ArrayList<>();
        ignoredPrefixList.add("/build");

    }

    // getters and setters
    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public void setTreeNodeDefinitionManager(TreeNodeDefinitionManager treeNodeDefinitionManager) {
        this.treeNodeDefinitionManager = treeNodeDefinitionManager;
    }

    // outlets

    public void scanFile() {
        // scan design files
        scanDesignfiles();
    }

    public List<Design> getDesigns() {
        return designs;
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
            List<Widget> widgets = readWidgets(designXmlFile);
            Design design = new Design();
            design.setProject(this);
            design.setRelativePath(relativePath(new File(projectPath), designXmlFile));
            design.setWidgets(widgets);
            for (Widget widget: widgets) {
                widget.setDesign(design);
            }
            designs.add(design);
        }
    }

    private List<Widget> readWidgets(File designFile) {
        TreeReader reader = new TreeReader(treeNodeDefinitionManager);
        reader.readFile(designFile.getAbsolutePath());
        return reader.getWidgets();
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
            Element widgetselems = root.element("widgets");
            return widgetselems!=null;
        }catch (Exception ignored) {}
        return false;
    }

    private static String relativePath(File dir, File file) {
        String path1 = dir.getAbsolutePath();
        String path2 = file.getAbsolutePath();
        return path2.substring(path1.length());
    }

}
