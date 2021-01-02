package org.vorlyanskiy.netbeans.groovy;

import java.io.IOException;
import java.util.Arrays;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=ProjectFactory.class)
public class GroovyProjectFactory implements ProjectFactory {
    
    @Override
    public boolean isProject(FileObject projectDirectory) {
        boolean anyMatch = Arrays.asList(projectDirectory.getChildren())
                .stream()
                .filter(fo -> fo.getExt() != null)
                .anyMatch(fo -> {
                    return fo.getExt().equalsIgnoreCase("groovy");
                });
        return anyMatch;
    }

    @Override
    public Project loadProject(FileObject fo, ProjectState ps) throws IOException {
        return isProject(fo) ? new GroovyProject(fo, ps) : null;
    }

    @Override
    public void saveProject(Project prjct) throws IOException, ClassCastException {
        
    }

}
