package org.vorlyanskiy.netbeans.groovy;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.openide.util.ImageUtilities;

/**
 *
 */
public class GroovyProjectInfo implements ProjectInformation {

    private final GroovyProject project;

    public GroovyProjectInfo(GroovyProject project) {
        this.project = project;
    }

    @StaticResource()
    public static final String CUSTOMER_ICON = "org/vorlyanskiy/netbeans/groovy/icon16.png";

    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage(CUSTOMER_ICON));
    }

    @Override
    public String getName() {
        return project.getProjectDirectory().getName();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }

    @Override
    public Project getProject() {
        return project;
    }

}
