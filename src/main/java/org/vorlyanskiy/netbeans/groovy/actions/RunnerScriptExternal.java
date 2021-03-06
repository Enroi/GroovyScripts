package org.vorlyanskiy.netbeans.groovy.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileObject;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 */
public class RunnerScriptExternal implements Runnable {

    private final FileObject fileObject;
    private final String pathToGroovy;
    private final InputOutput io;
    private final File projectFolder;
    private final String jars;
    private final ExecutorService executorService;
    private Path tempDir;
    private static final Logger LOG = Logger.getLogger(RunnerScriptExternal.class.getName());

    public RunnerScriptExternal(FileObject fileObject, String pathToGroovy, InputOutput io, File projectFolder, String jars) {
        this.fileObject = fileObject;
        this.pathToGroovy = pathToGroovy;
        this.projectFolder = projectFolder;
        this.jars = jars;
        this.io = io;
        executorService = Executors.newFixedThreadPool(10);
        
    }

    @Override
    public void run() {
        ProgressHandle ph = null;
        try {
            tempDir = Files.createTempDirectory("GroovyScripts");
            compileOther(projectFolder);
            shotdownExecutor();
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(pathToGroovy, 
                    "-cp",
                    generateClassPath(),
                    fileObject.getPath());
            Process process = builder.start();
            ph = ProgressHandle.createHandle(fileObject.getName(), () -> {
                process.destroyForcibly();
                return true;
            });
            ph.start();
            inheritIO(process.getInputStream(), io.getOut(), ph);
            inheritIO(process.getErrorStream(), io.getOut(), null);
            recursiveDeleteOnExit();
        } catch (Exception ex) {
            Arrays.asList(ex.getStackTrace()).stream().forEach(ste -> {
                io.getOut().println(ste);
            });
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            io.getOut().flush();
            io.getOut().close();
            if (ph != null) {
                ph.finish();
                ph.close();
            }
        }
    }
    
    private String generateClassPath() {
        String result = tempDir.toString();
        if (jars != null && !jars.isEmpty()) {
            String[] splitted = jars.split(",");
            String classpath = String.join(File.pathSeparator, splitted);
            result += File.pathSeparator + classpath;
        }
        return result;
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

    private void compileOther(File folder) throws IOException, InterruptedException {
        Arrays.stream(folder.listFiles())
                .filter(file -> {
                    return file.getName().toLowerCase().endsWith("java") 
                            || file.getName().toLowerCase().endsWith("groovy")
                            || file.isDirectory();
                })
                .forEach(file -> {
                    if (file.isDirectory()) {
                        try {
                            compileOther(file);
                        } catch (IOException | InterruptedException ex) {
                            //possible in script related project
                        }
                    } else {
                        compileOneFile(file);
                    }
                });
    }

    private void compileOneFile(File file) {
        Runnable runnable = () -> {
            Path pathToGroovyC = getPathToGroovyC();
            try {
                ProcessBuilder pb = new ProcessBuilder();
                pb.command(pathToGroovyC.toString(),
                        "-j",
                        "-d",
                        tempDir.toString(),
                        file.getAbsolutePath());
                File tempFile = File.createTempFile("groovycompileout", ".txt");
                pb.redirectError(tempFile);
                Process process = pb.start();
                int result = process.waitFor();
                if (result == 0) {
                    tempFile.delete();
                } else {
                    tempFile.deleteOnExit();
                }
            } catch (IOException | InterruptedException ex) {
                //possible in script related project
            }
        };
        executorService.execute(runnable);
    }

    private Path getPathToGroovyC() {
        Path ptg = Paths.get(pathToGroovy);
        Path parent = ptg.getParent();
        String groovyc = detectGroovyC();
        Path pathToGroovyC = parent.resolve(groovyc);
        return pathToGroovyC;
    }

    private String detectGroovyC() {
        if (pathToGroovy.toLowerCase().endsWith("bat")) {
            return "groovyc.bat";
        } else {
            return "groovyc";
        }
    }

    private void shotdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
        }
    }
    
    private void recursiveDeleteOnExit() throws IOException {
      Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, @SuppressWarnings("unused") BasicFileAttributes attrs) {
          file.toFile().deleteOnExit();
          return FileVisitResult.CONTINUE;
        }
        @Override
        public FileVisitResult preVisitDirectory(Path dir, @SuppressWarnings("unused") BasicFileAttributes attrs) {
          dir.toFile().deleteOnExit();
          return FileVisitResult.CONTINUE;
        }
      });
    }    
}
