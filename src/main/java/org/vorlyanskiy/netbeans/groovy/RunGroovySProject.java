package org.vorlyanskiy.netbeans.groovy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.openide.windows.IOProvider;
import org.vorlyanskiy.netbeans.groovy.utils.VariousProjectUtils;

@ActionID(
        category = "Build",
        id = "org.vorlyanskiy.netbeans.groovy.RunGroovySProject"
)
@ActionRegistration(
        displayName = "#CTL_RunGroovySProject"
)
@ActionReferences({
    @ActionReference(path = "Loaders/text/x-groovy/Actions", position = 565),
    @ActionReference(path = "Editors/text/x-groovy/Popup", position = 800),
    @ActionReference(path = "Menu/BuildProject", position = -90)
})
@Messages("CTL_RunGroovySProject=Run Groovy script")
public final class RunGroovySProject implements ActionListener {

    private final DataObject context;

    public RunGroovySProject(DataObject context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void actionPerformed(ActionEvent ev) {
        FileObject primaryFile = context.getPrimaryFile();
        String pathToGroovy = getPathToGroovy(primaryFile);
        if (pathToGroovy == null || pathToGroovy.trim().length() == 0) {
            pathToGroovy = VariousProjectUtils.getGlobalGroovyPath();
            if (pathToGroovy == null || pathToGroovy.trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "Set path to Groovy executable in menu \"Tools\"-\"Options\" or in Properties of project. ");
                return;
            }
        }
        org.openide.windows.InputOutput io = IOProvider.getDefault().getIO(primaryFile.getName(), true);
        io.setFocusTaken(true);
        Task task = new Task(new RunScript(primaryFile, pathToGroovy, io));
        RequestProcessor rp = new RequestProcessor("GroovyScriptRunner");
        rp.post(task);
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
