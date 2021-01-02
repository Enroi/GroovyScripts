package org.vorlyanskiy.netbeans.groovy;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 */
public class GroovySourrceGroup implements SourceGroup {

    private final FileObject fo;

    public GroovySourrceGroup(FileObject fo) {
        this.fo = fo;
    }

    @Override
    public FileObject getRootFolder() {
        return fo;
    }

    @Override
    public String getName() {
        return fo.getNameExt();
    }

    @Override
    public String getDisplayName() {
        return fo.getNameExt();
    }

    @Override
    public Icon getIcon(boolean opened) {
        return null;
    }

    @Override
    public boolean contains(FileObject file) {
        return FileUtil.isParentOf(fo, file);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

}
