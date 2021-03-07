package org.vorlyanskiy.netbeans.groovy.utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.NbPreferences;
import org.openide.windows.InputOutput;
import org.vorlyanskiy.netbeans.groovy.datamodel.OptionsDataModel;

/**
 *
 */
public class VariousProjectUtils {
    
    public static final String GROOVY_PATH = "GroovyPath";
    public static final String CLASSPATH_JARS = "ClasspathJars";
    private static final String GLOBAL_GROOVY_PATH = "GlobalGroovyPath";

    public static String getPath(Project project) {
        String path = null;
        if (project != null) {
            Preferences preferences = ProjectUtils.getPreferences(project, OptionsDataModel.class, true);
            String groovyPath = preferences.get(GROOVY_PATH, "");
            if (!groovyPath.isEmpty()) {
                path = groovyPath;
            }
        }
        return path;
    }
    
    public static String getClasspathJars(Project project) {
        String jars = null;
        if (project != null) {
            Preferences preferences = ProjectUtils.getPreferences(project, OptionsDataModel.class, true);
            String classpathJars = preferences.get(CLASSPATH_JARS, "");
            if (!classpathJars.isEmpty()) {
                jars = classpathJars;
            }
        }
        return jars;
    }
    
    public static String getGlobalGroovyPath() {
        return NbPreferences.forModule(VariousProjectUtils.class).get(GLOBAL_GROOVY_PATH, "");
    }
    
    public static final void putGlobalGroovyPath(String globalGroovyPath) {
        NbPreferences.forModule(VariousProjectUtils.class).put(GLOBAL_GROOVY_PATH, globalGroovyPath);
    }
    
    public static void logException(final Exception ex, final Logger log, InputOutput io) {
        Arrays.asList(ex.getStackTrace()).stream().forEach(ste -> {
            io.getOut().println(ste);
        });
        log.log(Level.SEVERE, ex.getMessage(), ex);
    }
    
    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }    
    
    
    public static Project getParentProjectDirectory(FileObject folder) {
        Project project = FileOwnerQuery.getOwner(folder);
        if (project != null) {
            Project projectForParent = getParentProjectDirectory(project.getProjectDirectory().getParent());
            if (projectForParent != null) {
                return projectForParent;
            }
        }
        return project;
    }
    
}
