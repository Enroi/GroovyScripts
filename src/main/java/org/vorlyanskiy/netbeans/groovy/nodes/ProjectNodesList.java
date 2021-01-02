package org.vorlyanskiy.netbeans.groovy.nodes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.vorlyanskiy.netbeans.groovy.GroovyProject;

/**
 *
 */
public class ProjectNodesList implements NodeList<FileObject> {

    private final GroovyProject project;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public ProjectNodesList(GroovyProject project) {
        this.project = project;
    }

    @Override
    public List<FileObject> keys() {
        FileObject projectDirectory = project.getProjectDirectory().getFileObject(".");
        List<FileObject> fileObjects = Arrays.asList(projectDirectory.getChildren()).stream().filter(fo -> {
            return fo.isFolder() || (fo.getExt() != null && fo.getExt().equalsIgnoreCase("groovy"));
        }).sorted((fo1, fo2) -> {
            return fo1.getName().compareToIgnoreCase(fo2.getName());
        }).collect(Collectors.toList());
        return fileObjects;
    }

    @Override
    public Node node(FileObject fo) {
        Node nodeDelegate = null;
        try {
            nodeDelegate = DataObject.find(fo).getNodeDelegate();
            GroovyChildren groovyChildren = null;
            if (!nodeDelegate.isLeaf()) {
                groovyChildren = new GroovyChildren(fo);
            }
            nodeDelegate = new GroovyScriptNode(nodeDelegate, groovyChildren);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return nodeDelegate;
    }

    @Override
    public void addNotify() {
    }

    @Override
    public void removeNotify() {
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }

}
