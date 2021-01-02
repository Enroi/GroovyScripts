package org.vorlyanskiy.netbeans.groovy.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.vorlyanskiy.netbeans.groovy.datamodel.OptionsDataModel;
import org.vorlyanskiy.netbeans.groovy.utils.VariousProjectUtils;

public class GeneralGroovyProperties implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GENERAL = "General";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-groovy-project", position = 10)
    public static GeneralGroovyProperties createGeneral() {
        return new GeneralGroovyProperties();
    }

    public GeneralGroovyProperties() {
    }
    
    private OptionsDataModel getDataModel() {
        OptionsDataModel dataModel = new OptionsDataModel();
        Lookup.Result<Project> lookupResults2 = Utilities.actionsGlobalContext().lookupResult(Project.class);
        lookupResults2.allInstances()
                .stream()
                .findFirst()
                .ifPresent(project -> {
            Preferences preferences = ProjectUtils.getPreferences(project, OptionsDataModel.class, true);
            String groovyPath = preferences.get(VariousProjectUtils.GROOVY_PATH, "");
            if (!groovyPath.isEmpty()) {
                dataModel.setPathToGroovy(groovyPath);
            }
        });
        return dataModel;
    }

    @NbBundle.Messages("LBL_Config_General=General")
    @Override
    public Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create(
                GENERAL,
                Bundle.LBL_Config_General(),
                null);
    }

    @Override
    public JComponent createComponent(Category category, Lookup lkp) {
        OptionsDataModel dataModel = getDataModel();
        OptionsPanel optionsPanel = new OptionsPanel(dataModel);
        category.setOkButtonListener(new OKOptionListener(dataModel));
        return optionsPanel;
    }
    
    private class OKOptionListener implements ActionListener {

        private final OptionsDataModel dataModel;

        private OKOptionListener(OptionsDataModel dataModel) {
            this.dataModel = dataModel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Lookup.Result<Project> lookupResults2 = Utilities.actionsGlobalContext().lookupResult(Project.class);
            lookupResults2.allInstances()
                    .stream()
                    .findFirst()
                    .ifPresent(project -> {
                Preferences preferences = ProjectUtils.getPreferences(project, OptionsDataModel.class, true);
                preferences.put(VariousProjectUtils.GROOVY_PATH, dataModel.getPathToGroovy());
            });
        }

    }

}