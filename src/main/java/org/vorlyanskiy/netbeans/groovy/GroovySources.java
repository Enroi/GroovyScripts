package org.vorlyanskiy.netbeans.groovy;

import java.util.Collection;
import java.util.LinkedList;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;

/**
 *
 */
public class GroovySources implements org.netbeans.api.project.Sources {

    private final GroovyProject project;

    public GroovySources(GroovyProject groovyProject) {
        this.project = groovyProject;
    }

    @Override
    public SourceGroup[] getSourceGroups(String type) {
        Collection<SourceGroup> sgCollection = getSourceGroups(project.getProjectDirectory());
        SourceGroup[] result = sgCollection.toArray(new SourceGroup[sgCollection.size()]);
        return result;
    }
    
    private Collection<SourceGroup> getSourceGroups(FileObject fo) {
        Collection<SourceGroup> result = new LinkedList<>();
        if (fo.isFolder()) {
            GroovySourrceGroup sgs = new GroovySourrceGroup(fo);
            result.add(sgs);
        }
        
        return result;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        
    }

}
