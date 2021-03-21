package org.vorlyanskiy.netbeans.groovy.nodes;

import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 */
public class LibrariesNode extends AbstractNode implements Comparable<Node> {

    public LibrariesNode(FileObject fo) {
        super (new LibraryChildren(fo));
        setDisplayName("Libraries");
        setName("Libraries");
    }

    @Override
    public int compareTo(Node o) {
        if (o == null) {
            return -1;
        }
        return getName().compareTo(o.getName());
    }

}
