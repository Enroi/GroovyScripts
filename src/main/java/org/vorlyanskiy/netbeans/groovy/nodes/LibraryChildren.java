package org.vorlyanskiy.netbeans.groovy.nodes;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.vorlyanskiy.netbeans.groovy.utils.VariousProjectUtils;

/**
 *
 */
public class LibraryChildren extends Children.SortedArray {

    private final FileObject projectFolder;
    
    public LibraryChildren(FileObject fo) {
        this.projectFolder = fo;
    }

    @Override
    protected Collection<Node> initCollection() {
        List<Node> libraries = Collections.EMPTY_LIST;
        String stringJars = VariousProjectUtils.getJars(projectFolder);
        if (stringJars != null && !stringJars.isEmpty()) {
            String[] splitted = stringJars.split(",");
            libraries = Arrays.stream(splitted)
                    .filter(oneJar -> !oneJar.isEmpty())
                    .map(this::convertStringToNode)
                    .collect(Collectors.toList());
        }
        return libraries;
    }
    
    private Node convertStringToNode(String path) {
        int lastIndex = path.lastIndexOf(File.separator);
        String last = path.substring(lastIndex + 1);
        OneLibrary oneLibrary = new OneLibrary();
        oneLibrary.setDisplayName(last);
        oneLibrary.setName(last);
        oneLibrary.setShortDescription(path);
        return oneLibrary;
        
    }

}
