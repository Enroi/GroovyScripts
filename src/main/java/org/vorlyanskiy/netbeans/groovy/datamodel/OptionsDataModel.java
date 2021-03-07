package org.vorlyanskiy.netbeans.groovy.datamodel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class OptionsDataModel {
    
    private String pathToGroovy;
    private final List<String> classpathJars;

    public OptionsDataModel() {
        this.classpathJars = new LinkedList<>();
    }

    public List<String> getClasspathJars() {
        return classpathJars;
    }
    
    public String getClasspathJarsAsSstring() {
        return String.join(",", classpathJars);
    }

    public void setClasspathJars(String strClasspathJars) {
        Arrays.stream(strClasspathJars.split(","))
                .forEach(classpathJars::add);
                
    }

    public String getPathToGroovy() {
        return pathToGroovy;
    }

    public void setPathToGroovy(String pathToGroovy) {
        this.pathToGroovy = pathToGroovy;
    }

    public void addJar(String path) {
        classpathJars.add(path);
    }

    public void removeJar(String selectedValue) {
        classpathJars.remove(selectedValue);
    }
}
