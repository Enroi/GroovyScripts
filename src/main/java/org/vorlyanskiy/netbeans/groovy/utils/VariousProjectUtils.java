package org.vorlyanskiy.netbeans.groovy.utils;

import java.util.prefs.Preferences;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.util.NbPreferences;
import org.vorlyanskiy.netbeans.groovy.datamodel.OptionsDataModel;

/**
 *
 */
public class VariousProjectUtils {
    
    public static final String GROOVY_PATH = "GroovyPath";
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
    
    public static String getGlobalGroovyPath() {
        return NbPreferences.forModule(VariousProjectUtils.class).get(GLOBAL_GROOVY_PATH, "");
    }
    
    public static final void putGlobalGroovyPath(String globalGroovyPath) {
        NbPreferences.forModule(VariousProjectUtils.class).put(GLOBAL_GROOVY_PATH, globalGroovyPath);
    }
}
