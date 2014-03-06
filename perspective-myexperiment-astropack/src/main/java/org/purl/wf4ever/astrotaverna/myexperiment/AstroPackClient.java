package org.purl.wf4ever.astrotaverna.myexperiment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import net.sf.taverna.t2.ui.perspectives.myexperiment.model.MyExperimentClient;
import net.sf.taverna.t2.ui.perspectives.myexperiment.model.Resource;
import net.sf.taverna.t2.ui.perspectives.myexperiment.model.Workflow;

public class AstroPackClient {

	  private static final int EXAMPLE_ASTROTAVERNA_WORKFLOWS_PACK_ID = 420;
	  private Logger logger = Logger.getLogger(AstroPackClient.class);
	  private MyExperimentClient mClient;

	  public AstroPackClient(MyExperimentClient client) {
		  this.mClient = client;
	  }
	  
	  @SuppressWarnings("unchecked")
	  public List<Workflow> getExampleAstrotavernaWorkflows() {
	    List<Workflow> workflows = new ArrayList<Workflow>();

	    try {
	      String strExampleWorkflowsPackUrl = mClient.getBaseURL() + "/pack.xml?id="
	          + EXAMPLE_ASTROTAVERNA_WORKFLOWS_PACK_ID + "&elements=internal-pack-items";
	      Document doc = mClient.doMyExperimentGET(strExampleWorkflowsPackUrl)
	          .getResponseBody();

	      if (doc != null) {
	        List<Element> allInternalItems = doc.getRootElement().getChild(
	            "internal-pack-items").getChildren("workflow");
	        for (Element e : allInternalItems) {
	          String itemUri = e.getAttributeValue("uri");
	          Document itemDoc = mClient.doMyExperimentGET(itemUri).getResponseBody();
	          String workflowUri = itemDoc.getRootElement().getChild("item")
	              .getChild("workflow").getAttributeValue("uri");
	          Document docCurWorkflow = mClient.getResource(Resource.WORKFLOW,
	              workflowUri, Resource.REQUEST_FULL_LISTING);
	          workflows.add(Workflow.buildFromXML(docCurWorkflow, this.logger));
	        }
	      }
	    } catch (Exception e) {
	      this.logger.error("Failed to retrieve example workflows", e);
	    }

	    logger.debug(workflows.size()
	        + " example workflows retrieved from myExperiment");

	    return (workflows);
	  } 
}
