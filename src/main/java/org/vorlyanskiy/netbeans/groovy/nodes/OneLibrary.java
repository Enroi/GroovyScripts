package org.vorlyanskiy.netbeans.groovy.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 */
public class OneLibrary extends AbstractNode implements Comparable<Node> {

    public OneLibrary() {
        super(Children.LEAF);
//        setIconBaseWithExtension("org/vorlyanskiy/netbeans/groovy/onelibrary.png");
        setIconBaseWithExtension("org/vorlyanskiy/netbeans/groovy/libraryStandard.png");
    }

    @Override
    public int compareTo(Node o) {
        if (o == null) {
            return -1;
        }
        return getName().compareTo(o.getName());
    }

}
