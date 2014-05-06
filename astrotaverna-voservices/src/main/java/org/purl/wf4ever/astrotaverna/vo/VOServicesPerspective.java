package org.purl.wf4ever.astrotaverna.vo;

import java.io.InputStream;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.purl.wf4ever.astrotaverna.myexperiment.ExampleAstrotavernaWorkflowsPanel;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.spi.SPIRegistry.SPIRegistryEvent;
import net.sf.taverna.t2.ui.perspectives.AbstractPerspective;
import net.sf.taverna.t2.ui.perspectives.PerspectiveRegistry;
import net.sf.taverna.t2.ui.perspectives.myexperiment.MainComponent;
import net.sf.taverna.t2.ui.perspectives.myexperiment.MyExperimentPerspective;
import net.sf.taverna.t2.ui.perspectives.myexperiment.model.Util;
import net.sf.taverna.t2.workbench.ui.zaria.PerspectiveSPI;

public class VOServicesPerspective extends AbstractPerspective implements
		PerspectiveSPI {

	static boolean launched = false;

	
	
	public static class PerspectiveCheckerThread extends Thread {
		private Logger logger = Logger.getLogger(MyExperimentPerspective.class);
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				
			}
			if(Util.isRunningInTaverna()) {
				for(PerspectiveSPI p : PerspectiveRegistry.getInstance().getPerspectives()) {
					if(p.getText().equals(MyExperimentPerspective.PERSPECTIVE_NAME)) {
						net.sf.taverna.t2.ui.perspectives.myexperiment.MainComponent originalComponent = 
								(net.sf.taverna.t2.ui.perspectives.myexperiment.MainComponent) 
								((net.sf.taverna.t2.ui.perspectives.myexperiment.MyExperimentPerspective) p)
								.getMainComponent();
						if(originalComponent == null) {
							PerspectiveCheckerThread checker = new PerspectiveCheckerThread();
							checker.start();
							return;
						} else {
							ExampleAstrotavernaWorkflowsPanel panel = 
									new ExampleAstrotavernaWorkflowsPanel(originalComponent, 
											originalComponent.getMyExperimentClient(), logger);
							((net.sf.taverna.t2.ui.perspectives.myexperiment.MyExperimentPerspective) p)
							.getMainComponent().getMainTabs()
							.add("Astro Pack", panel);
							new AstroPackInitializer(panel).start();
							return;
						}
					}
				}
			}
		}
	}

	public static final class AstroPackInitializer extends Thread {
		private final ExampleAstrotavernaWorkflowsPanel panel;

		public AstroPackInitializer(ExampleAstrotavernaWorkflowsPanel mPanel) {
			this.panel = mPanel;
		}

		@Override
		public void run() {
			this.panel.refresh();
		}
	}

	@Override
	public String getText() {
		if(!launched) {
			launched = true;
			PerspectiveCheckerThread checker = new PerspectiveCheckerThread();
			checker.start();
		}
		return "VO services";
	}

	@Override
	public ImageIcon getButtonIcon() {
		return VOServiceIcon.voIcon;
	}

	@Override
	protected InputStream getLayoutResourceStream() {
		return getClass().getResourceAsStream("voservices-perspective.xml");
	}

	@Override
	public int positionHint() {
		// Just before BioCatalogue perspective
		return 38;
	}

}
