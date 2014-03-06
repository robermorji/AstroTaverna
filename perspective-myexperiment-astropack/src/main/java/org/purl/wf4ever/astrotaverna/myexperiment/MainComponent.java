/*******************************************************************************
 * Copyright (C) 2009 The University of Manchester
 * 
 * Modifications to the initial code base are copyright of their respective
 * authors, or their employers as appropriate.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
/**
 * 
 */
package org.purl.wf4ever.astrotaverna.myexperiment;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.awt.Desktop;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.sf.taverna.t2.lang.ui.ShadedLabel;
import net.sf.taverna.t2.ui.perspectives.PerspectiveRegistry;
import net.sf.taverna.t2.ui.perspectives.myexperiment.MyExperimentPerspective;
import net.sf.taverna.t2.ui.perspectives.myexperiment.model.MyExperimentClient;
import net.sf.taverna.t2.ui.perspectives.myexperiment.model.Resource;
import net.sf.taverna.t2.ui.perspectives.myexperiment.model.Util;
import net.sf.taverna.t2.ui.perspectives.myexperiment.model.Workflow;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.FileType;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;
import net.sf.taverna.t2.workbench.file.importworkflow.gui.ImportWorkflowWizard;
import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;
import net.sf.taverna.t2.workbench.ui.zaria.PerspectiveSPI;
import net.sf.taverna.t2.workbench.ui.zaria.UIComponentSPI;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLSerializationConstants;

import org.apache.log4j.Logger;

/**
 * @author Sergejs Aleksejevs, Emmanuel Tagarira, Jiten Bhagat
 */
public final class MainComponent extends JPanel implements UIComponentSPI, ChangeListener {
  // myExperiment client, logger and the stylesheet will be made available
  // throughout the whole perspective
  //private MyExperimentClient myExperimentClient;
  private final Logger logger = Logger.getLogger(MainComponent.class);
  
  public static Logger LOGGER;

  public MainComponent() {
    super();

    
//    ExampleAstrotavernaWorkflowsPanel astrotavernaPanel = null;
//    // determine which shutdown operations to use
//    if (Util.isRunningInTaverna()) {
//      for (PerspectiveSPI perspective : PerspectiveRegistry.getInstance().getPerspectives()) {
//        if (perspective.getText().equals(net.sf.taverna.t2.ui.perspectives.myexperiment.MyExperimentPerspective.PERSPECTIVE_NAME)) {
//        	//this.getMainTabs().add("Astro Pack",astrotavernaPanel);
//        	net.sf.taverna.t2.ui.perspectives.myexperiment.MainComponent originalComponent = 
//        			(net.sf.taverna.t2.ui.perspectives.myexperiment.MainComponent)((MyExperimentPerspective) perspective).getMainComponent();
//        	astrotavernaPanel = new ExampleAstrotavernaWorkflowsPanel(originalComponent, originalComponent.getMyExperimentClient(), logger);
//          ((MyExperimentPerspective) perspective).getMainComponent().getMainTabs().add("Astro Pack",astrotavernaPanel);
//          break;
//        }
//      }
//    }
//
//    // Do the rest in a separate thread to avoid hanging the GUI.
//    // Remember to use SwingUtilities.invokeLater to update the GUI directly.
//    new AstroPackInitializer(astrotavernaPanel).start();

  }
  
//  public class AstroPackInitializer extends Thread {
//	  private final ExampleAstrotavernaWorkflowsPanel panel;
//	  public AstroPackInitializer(ExampleAstrotavernaWorkflowsPanel mPanel) {
//		  this.panel = mPanel;
//	  }
//	  
//	  @Override
//	  public void run() {
//		this.panel.refresh();
//	  }
//  }

  public ImageIcon getIcon() {
    return WorkbenchIcons.databaseIcon;
  }

  @Override
  public String getName() {
    return "myExperiment Perspective Main Component";
  }

  public void onDisplay() {
  }

  public void onDispose() {
  }

  public void stateChanged(ChangeEvent e) {
    
  }

}
