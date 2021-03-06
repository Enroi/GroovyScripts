package org.vorlyanskiy.netbeans.groovy.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import javax.swing.JOptionPane;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.vorlyanskiy.netbeans.groovy.utils.VariousProjectUtils;

@ActionID(
        category = "Build",
        id = "org.vorlyanskiy.netbeans.groovy.actions.RunScriptExternal"
)
@ActionRegistration(
        displayName = "#CTL_RunGroovySProject"
)
@ActionReferences({
    @ActionReference(path = "Loaders/text/x-groovy/Actions", position = 565),
    @ActionReference(path = "Editors/text/x-groovy/Popup", position = 800),
    @ActionReference(path = "Menu/BuildProject", position = -90)
})
@Messages("CTL_RunGroovySProject=Run with External Groovy")
public final class RunScriptExternal implements ActionListener {

    private final DataObject context;

    public RunScriptExternal(DataObject context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void actionPerformed(ActionEvent ev) {
        FileObject primaryFile = context.getPrimaryFile();
        Project project = VariousProjectUtils.getParentProjectDirectory(primaryFile);
        File projectFolder = Utilities.toFile(project.getProjectDirectory().toURI());
        org.openide.windows.InputOutput io = IOProvider.getDefault().getIO(primaryFile.getName(), true);
        io.setFocusTaken(true);
        io.setOutputVisible(true);
        io.getOut().println("Script started " + new Date());
        String pathToGroovy = detectPathToGroovy(primaryFile);
        if (pathToGroovy != null && !pathToGroovy.isEmpty()) {
            String jars = VariousProjectUtils.getJars(primaryFile);
            Task task = new Task(new RunnerScriptExternal(primaryFile, pathToGroovy, io, projectFolder, jars));
            RequestProcessor rp = new RequestProcessor("GroovyScriptRunner");
            rp.post(task);
        } else {
            JOptionPane.showMessageDialog(null, "Set path to Groovy executable in menu \"Tools\"-\"Options\" or in Properties of project. ");
        }
    }
    
    private String detectPathToGroovy(FileObject primaryFile) {
        String pathToGroovy = getPathToGroovy(primaryFile);
        if (pathToGroovy == null || pathToGroovy.trim().length() == 0) {
            pathToGroovy = VariousProjectUtils.getGlobalGroovyPath();
        }
        return pathToGroovy;
    }
    
    private String getPathToGroovy(FileObject primaryFile) {
        Project project = FileOwnerQuery.getOwner(primaryFile);
        String pathToGroovy = VariousProjectUtils.getPath(project);
        if ((pathToGroovy == null || pathToGroovy.trim().length() == 0)
                && primaryFile.getParent() != null) {
            return getPathToGroovy(primaryFile.getParent());
        }
        return pathToGroovy;
    }
    
}
