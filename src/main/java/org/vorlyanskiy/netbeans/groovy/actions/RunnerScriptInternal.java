package org.vorlyanskiy.netbeans.groovy.actions;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final Logger LOG = Logger.getLogger(RunnerScriptInternal.class.getName());

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
            GroovyShell gs = new GroovyShell();
            gs.setProperty("out", io.getOut());
            gs.evaluate(fileObject.toURI());
            io.getOut().flush();
            io.getOut().close();
            ph.finish();
            ph.close();
        } catch (CompilationFailedException | IOException ex) {
            Arrays.asList(ex.getStackTrace()).stream().forEach(ste -> {
                io.getOut().println(ste);
            });
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
