package org.vorlyanskiy.netbeans.groovy.nodes;

import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 */
public class GroovyScriptNode extends FilterNode implements Comparable<Node> {
    
    public GroovyScriptNode(Node original, org.openide.nodes.Children children) {
        super(original, children);
    }
    
    @Override
    public int compareTo(Node o) {
        if (o == null) {
            return -1;
        }
        return getName().compareTo(o.getName());
    }

}
