package org.vorlyanskiy.netbeans.groovy;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 */
public class GroovyProjectLogicalView implements LogicalViewProvider {

    private final GroovyProject project;
    
    @StaticResource()
    public static final String GROOVY_ICON = "org/vorlyanskiy/netbeans/groovy/icon16.png";
    
    private static final Logger LOG = Logger.getLogger(GroovyProjectLogicalView.class.getName());

    public GroovyProjectLogicalView(GroovyProject project) {
        this.project = project;
    }

    @Override
    public Node createLogicalView() {
        try {
            //Obtain the project directory's node:
            FileObject projectDirectory = project.getProjectDirectory();
            DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
            Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
            return new ProjectNode(nodeOfProjectFolder, project);
        } catch (DataObjectNotFoundException donfe) {
            LOG.log(Level.SEVERE, "Can not create View Provider", donfe);
            //Fallback-the directory couldn't be created -
            //read-only filesystem or something evil happened
            return new AbstractNode(Children.LEAF);
        }
    }

    @Override
    public Node findPath(Node root, Object target) {
        return null;
    }

}
