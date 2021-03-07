package org.vorlyanskiy.netbeans.groovy.actions;

import groovy.lang.GroovyShell;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileObject;
import org.openide.windows.InputOutput;
import org.vorlyanskiy.netbeans.groovy.utils.VariousProjectUtils;

/**
 *
 */
public class RunnerScriptInternal implements Runnable {

    private final FileObject fileObject;
    private final InputOutput io;
    private static final Logger LOG = Logger.getLogger(RunnerScriptInternal.class.getName());
    private final File projectFolder;

    public RunnerScriptInternal(FileObject fileObject, InputOutput io, File projectFolder) {
        this.fileObject = fileObject;
        this.io = io;
        this.projectFolder = projectFolder;
    }

    @Override
    public void run() {
        try {
            ProgressHandle ph = ProgressHandle.createHandle(fileObject.getName(), () -> {
                return true;
            });
            ph.start();
            GroovyShell gs = new GroovyShell();
            gs.getContext().setProperty("out", io.getOut());
            gs.setProperty("out", io.getOut());
            compileJavaFiles(gs, projectFolder, "java");
            compileJavaFiles(gs, projectFolder, "groovy");
            gs.evaluate(fileObject.toURI());
            io.getOut().flush();
            io.getOut().close();
            ph.finish();
            ph.close();
        } catch (CompilationFailedException | IOException ex) {
            VariousProjectUtils.logException(ex, LOG, io);
        }
    }

    private void compileJavaFiles(GroovyShell gs, File folder, String testFilesForExtension) {
        Arrays.stream(folder.listFiles())
                .filter(file -> {
                    String filePath = file.getPath();
                    filePath = filePath.replace("\\", "/");;
                    return !filePath.equals(fileObject.getPath());
                })
                .forEach(file -> {
                    if (file.isDirectory()) {
                        compileJavaFiles(gs, file, testFilesForExtension);
                    } else {
                        Optional<String> extension = VariousProjectUtils.getExtensionByStringHandling(file.getAbsolutePath());
                        if (extension.isPresent() && extension.get().equalsIgnoreCase(testFilesForExtension)) {
                            try {
                                gs.getClassLoader().parseClass(file);
                            } catch (CompilationFailedException | IOException ex) {
                                // Wrong files can be present in script related project.
                            }
                        }
                    }
                });
    }
    
}
