package org.vorlyanskiy.netbeans.groovy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 */
public class RunScript implements Runnable {

    private final FileObject fileObject;
    private final String pathToGroovy;
    private final InputOutput io;

    public RunScript(FileObject fileObject, String pathToGroovy, InputOutput io) {
        this.fileObject = fileObject;
        this.pathToGroovy = pathToGroovy;
        this.io = io;
    }

    @Override
    public void run() {
        try {
            String pathToFile = fileObject.getPath();
            ProcessBuilder builder = new ProcessBuilder(pathToGroovy, pathToFile);
            Process process = builder.start();
            ProgressHandle ph = ProgressHandle.createHandle(fileObject.getName(), () -> {
                process.destroyForcibly();
                return true;
            });
            ph.start();
            inheritIO(process.getInputStream(), io.getOut(), ph);
            inheritIO(process.getErrorStream(), io.getOut(), null);
        } catch (IOException ex) {
            Arrays.asList(ex.getStackTrace()).stream().forEach(ste -> {
                io.getOut().println(ste);
            });
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void inheritIO(final InputStream src, final OutputWriter dest, ProgressHandle ph) {
        new Thread(() -> {
            Scanner sc = new Scanner(src);
            while (sc.hasNextLine()) {
                dest.println(sc.nextLine());
            }
            dest.close();
            if (ph != null) {
                ph.finish();
            }
        }).start();
    }
}
