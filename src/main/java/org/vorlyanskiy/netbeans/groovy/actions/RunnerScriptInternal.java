package org.vorlyanskiy.netbeans.groovy.actions;

import groovy.lang.GroovyShell;
import java.io.IOException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.windows.InputOutput;

/**
 *
 */
public class RunnerScriptInternal implements Runnable {
    
    private final FileObject fileObject;
    private final InputOutput io;

    public RunnerScriptInternal(FileObject fileObject, InputOutput io) {
        this.fileObject = fileObject;
        this.io = io;
    }

    @Override
    public void run() {
        try {
            ProgressHandle ph = ProgressHandle.createHandle(fileObject.getName(), () -> {
                return true;
            });
            
            ph.start();
            io.getOut().println("test line before");
            GroovyShell gs = new GroovyShell();
            gs.evaluate(fileObject.toURI());
            io.getOut().println("test line after");
            io.getOut().flush();
            io.getOut().close();
            ph.finish();
            ph.close();
        } catch (CompilationFailedException | IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            Exceptions.printStackTrace(ex);
        }
    }

}
