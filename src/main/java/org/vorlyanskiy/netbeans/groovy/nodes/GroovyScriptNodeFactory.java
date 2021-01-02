package org.vorlyanskiy.netbeans.groovy.nodes;

import org.vorlyanskiy.netbeans.groovy.GroovyProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;

@NodeFactory.Registration(projectType = "org-groovy-project", position = 10)
public class GroovyScriptNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        GroovyProject p = project.getLookup().lookup(GroovyProject.class);
        if (p == null) {
            throw new RuntimeException("Can not find project");
        }
        return new ProjectNodesList(p);
    }

}
