package org.vorlyanskiy.netbeans.groovy.nodes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class GroovyChildren extends Children.SortedArray {

    private final FileObject fo;
    
    private boolean rootProjectFolder = false;
    
    private static final Logger LOG = Logger.getLogger(GroovyChildren.class.getName());

    public GroovyChildren(FileObject fo, boolean rootProjectFolder) {
        this(fo);
        this.rootProjectFolder = rootProjectFolder;
        setComparator(new RootLevelComparator());
    }
    
    public GroovyChildren(FileObject fo) {
        this.fo = fo;
        LocalFileChangeAdapter localFileChangeAdapter = new LocalFileChangeAdapter();
        fo.addFileChangeListener(localFileChangeAdapter);
    }

    @Override
    protected Collection<Node> initCollection() {
        List<Node> result = Arrays.asList(fo.getChildren())
                .stream()
                .filter(fo -> {
                    return isGroovyScriptOrFolder(fo);
                })
                .map(fob -> {
                    Node nodeDelegate = null;
                    try {
                        nodeDelegate = DataObject.find(fob).getNodeDelegate();
                        GroovyChildren groovyChildren = null;
                        if (!nodeDelegate.isLeaf()) {
                            groovyChildren = new GroovyChildren(fob);
                        }
                        nodeDelegate = new GroovyScriptNode(nodeDelegate, groovyChildren);
                    } catch (DataObjectNotFoundException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                    return nodeDelegate;
                })
                .collect(Collectors.toList());
        if (rootProjectFolder) {
            result.add(new LibrariesNode(fo));
        }
        return result;
    }

    private boolean isGroovyScriptOrFolder(FileObject fo1) {
        return fo1.isFolder() 
                || (fo1.getExt() != null && 
                    (fo1.getExt().equalsIgnoreCase("groovy") || fo1.getExt().equalsIgnoreCase("java")));
    }

    class LocalFileChangeAdapter extends FileChangeAdapter {


        @Override
        public void fileFolderCreated(FileEvent fe) {
            FileObject created = fe.getFile();
            if (nodes != null
                    && created.getParent() == GroovyChildren.this.fo) {
                try {
                    LocalFileChangeAdapter localFileChangeAdapter = new LocalFileChangeAdapter();
                    created.addFileChangeListener(localFileChangeAdapter);
                    Node createdNode = DataObject.find(created).getNodeDelegate();
                    createdNode = new GroovyScriptNode(createdNode, new GroovyChildren(created));
                    nodes.add(createdNode);
                    refresh();
                } catch (DataObjectNotFoundException ex) {

                }
            }
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            FileObject created = fe.getFile();
            if (isGroovyScriptOrFolder(created) 
                    && nodes != null
                    && created.getParent() == GroovyChildren.this.fo) {
                try {
                    Node createdNode = DataObject.find(created).getNodeDelegate();
                    createdNode = new GroovyScriptNode(createdNode, null);
                    nodes.add(createdNode);
                    refresh();
                } catch (DataObjectNotFoundException ex) {
                    
                }
            }
        }

    }
    
    class RootLevelComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            if (o1 instanceof LibrariesNode && o2 instanceof GroovyScriptNode) {
                return 1;
            } else if (o1 instanceof LibrariesNode && o2 instanceof GroovyScriptNode) {
                return -1;
            } else if (o1.getName() != null && o2.getName() == null) {
                return -1;
            } else if (o1.getName() == null && o2.getName() != null) {
                return 1;
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        }
    
    }
    
}
