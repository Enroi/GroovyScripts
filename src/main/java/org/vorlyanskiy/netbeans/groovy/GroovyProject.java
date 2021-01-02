package org.vorlyanskiy.netbeans.groovy;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 */
public class GroovyProject implements Project {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lkp;

    public GroovyProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        this.state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{ 
                this,
                new GroovyProjectInfo(this),
                new GroovyProjectLogicalView(this),
                new GroovyCustomizerProvider(this),
                new GroovySources(this),
            });
        }
        return lkp;
    }
}