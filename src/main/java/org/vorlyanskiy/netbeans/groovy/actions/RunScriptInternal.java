package org.vorlyanskiy.netbeans.groovy.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.vorlyanskiy.netbeans.groovy.utils.VariousProjectUtils;

@ActionID(
        category = "Build",
        id = "org.vorlyanskiy.netbeans.groovy.actions.RunScriptInternal"
)
@ActionRegistration(
        displayName = "#CTL_RunGroovyScriptInternal"
)
@ActionReferences({
    @ActionReference(path = "Loaders/text/x-groovy/Actions", position = 565),
    @ActionReference(path = "Editors/text/x-groovy/Popup", position = 800),
    @ActionReference(path = "Menu/BuildProject", position = -190),
})
@Messages("CTL_RunGroovyScriptInternal=Run with bundled Groovy")
public final class RunScriptInternal implements ActionListener {

    private final DataObject context;

    public RunScriptInternal(DataObject context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        FileObject primaryFile = context.getPrimaryFile();
        Project project = VariousProjectUtils.getParentProjectDirectory(primaryFile);
        File projectFolder = Utilities.toFile(project.getProjectDirectory().toURI());
        org.openide.windows.InputOutput io = IOProvider.getDefault().getIO(primaryFile.getName(), true);
        io.setFocusTaken(true);
        Task task = new Task(new RunnerScriptInternal(primaryFile, io, projectFolder));
        RequestProcessor rp = new RequestProcessor("GroovyScriptRunner");
        rp.post(task);
    }
}
