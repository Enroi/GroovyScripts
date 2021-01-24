package org.vorlyanskiy.netbeans.groovy;

import groovy.lang.GroovyShell;
import java.io.IOException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.windows.InputOutput;

/**
 *
 */
public class RunScriptInternal implements Runnable {
    
    private final FileObject fileObject;
    private final InputOutput io;

    public RunScriptInternal(FileObject fileObject, InputOutput io) {
        this.fileObject = fileObject;
        this.io = io;
    }

    @Override
    public void run() {
        try {
            GroovyShell gs = new GroovyShell();
            gs.evaluate(fileObject.toURI());
        } catch (CompilationFailedException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
