package org.purl.wf4ever.astrotaverna.vo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.apache.log4j.*;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.ivoa.xml.conesearch.v1.ConeSearch;
import net.ivoa.xml.sia.v1.SimpleImageAccess;
import net.ivoa.xml.slap.v0.SimpleLineAccess;
import net.ivoa.xml.ssa.v0.SimpleSpectralAccess;
import net.ivoa.xml.voresource.v1.Capability;
import net.ivoa.xml.voresource.v1.Service;
import net.sf.taverna.t2.workbench.ui.zaria.UIComponentSPI;
import net.ivoa.xml.slap.v0.SimpleLineAccess;
import net.ivoa.xml.ssa.v0.SimpleSpectralAccess;
import net.ivoa.xml.sia.v1.SimpleImageAccess;
import net.ivoa.xml.tapregext.v1.TableAccess;
import net.ivoa.xml.voresource.v1.Interface;
import net.ivoa.xml.voresource.v1.AccessURL;
import net.ivoa.xml.vodataservice.v1.ParamHTTP;

import org.apache.log4j.Logger;
import org.purl.wf4ever.astrotaverna.vo.utils.ModelIterator;
import org.purl.wf4ever.astrotaverna.vorepo.VORepository.Status;

import uk.ac.starlink.vo.TableSetPanel;
import uk.ac.starlink.vo.TapTableLoadDialog;
import uk.ac.starlink.vo.TableMeta;
import uk.ac.starlink.vo.TapQueryPanel;

public class VOServicesView extends JPanel implements UIComponentSPI {
	public class RegistryChange implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox combo = (JComboBox) e.getSource();
			getController().changeEndpoint((String) combo.getSelectedItem());
		}
	}

	public class AddToWorkflow extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public AddToWorkflow() {
			super("Add to workflow");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getController().addToWorkflow();
		}
	}

	public class ConeSearchAction extends SearchAction {
		private static final long serialVersionUID = 1L;

		public ConeSearchAction() {
			super("Cone Search");
			this.searchType = ConeSearch.class;
		}
	}

	public class SearchAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		protected Class<? extends Capability> searchType;

		public SearchAction(String label) {
			super(label);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String search = keywords.getText();
			getController().search(searchType, search);
			tapSearchTab.setVisible(Boolean.FALSE);

		}
	}

	public class SIASearchAction extends SearchAction {
		private static final long serialVersionUID = 1L;

		public SIASearchAction() {
			super("SIA Search");
			this.searchType = SimpleImageAccess.class;
		}
	}

	public class SSASearchAction extends SearchAction {
		private static final long serialVersionUID = 1L;

		public SSASearchAction() {
			super("SSA Search");
			this.searchType = SimpleSpectralAccess.class;
		}
	}

	public class TAPSearchAction extends SearchAction {
		private static final long serialVersionUID = 1L;

		public TAPSearchAction() {
			super("TAP Search");
			this.searchType = TableAccess.class;
		}
	}

	public class SLASearchAction extends SearchAction {
		private static final long serialVersionUID = 1L;

		public SLASearchAction() {
			super("SLA Search");
			this.searchType = SimpleLineAccess.class;
		}
	}

	static Logger logger = Logger.getLogger(VOServicesView.class);
	private static final int RESOURCE_COLUMN = 0;
	private static final long serialVersionUID = 1L;
	// Actions
	
	private VOServicesController controller;
	private JTextField keywords;
	private VOServicesModel model;
	private JTextField url_query;
	private JSplitPane ventanaCompleta;
	private JComboBox registry;
	private JSplitPane results;
	private JPanel resultsDetails;
	private JPanel tapSearchTab;
	private JPanel tapQuery;
	private JPanel tapTables;
	private JPanel contenedorPrincipal;
	
	private JTable resultsTable;

	private DefaultTableModel resultsTableModel;

	private SIASearchAction siaSearchAction = new SIASearchAction();
	private ConeSearchAction coneSearch = new ConeSearchAction();
	// private SLASearchAction slaSearchAction = new SLASearchAction();
	private SSASearchAction ssaSearchAction = new SSASearchAction();
	private TAPSearchAction tapSearchAction = new TAPSearchAction();

	// SWing stuff
	private JLabel status;
	private VOServiceDetails serviceDetails;
	private AddToWorkflow addToWorkflow;

	public VOServicesView() {
		initialize();
	}

	public void setSearch(String search) {
		keywords.setText(search);
	}

	public String getSearch() {
		return keywords.getText();
	}

	public void clearResults() {
		int rows = resultsTableModel.getRowCount();
		while (rows > 0) {
			resultsTableModel.removeRow(--rows);
		}

		// results.validate();
	}

	public VOServicesController getController() {
		if (controller == null) {
			controller = new VOServicesController();
			controller.setView(this);
			controller.setModel(getModel());
		}
		return controller;
	}

	@Override
	public ImageIcon getIcon() {
		return VOServiceIcon.voIcon;
	}

	public VOServicesModel getModel() {
		if (model == null) {
			model = new VOServicesModel();
			model.setView(this);
			model.setController(getController());
		}
		return model;
	}

	protected Service getServiceAtRow(int row) {
		if (row < 0) {
			return null;
		}
		row = resultsTable.convertRowIndexToModel(row);
		return ((Service) resultsTableModel.getValueAt(row, RESOURCE_COLUMN));
	}

	protected Service getTableSelection() {
		return getServiceAtRow(resultsTable.getSelectedRow());
	}

	protected void initialize() {
		removeAll();
		
		JPanel panelSuperior = new JPanel( new BorderLayout());
		JPanel panelInferior = new JPanel(new BorderLayout());
				
		setLayout(new BorderLayout());
		
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//
//		gbc.gridwidth = 3;
//		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		panelSuperior.add(makeSearchBox(), BorderLayout.NORTH);
//		gbc.weightx = 1.0;
//		gbc.weighty = 0.0;
//		gbc.fill = GridBagConstraints.BOTH;
//
//		gbc.weighty = 1.0;
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.gridwidth = 2;
		panelSuperior.add(makeResults(),BorderLayout.CENTER );

//		gbc.gridx = 2;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.gridwidth = 1;
//		gbc.weightx = 0.66;
//		gbc.weighty = 1.0;
//
//		gbc.gridx = 0;
//		gbc.gridy = 2;
		panelInferior.add(makeTapSearchTab(),BorderLayout.CENTER);
		tapSearchTab.setVisible(Boolean.FALSE);
				
		getController().checkEndpoint();
		updateDetails();
		updateServices();
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,panelSuperior,panelInferior);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(0.50);
		//split.setLayout(new GridBagLayout());
		this.add(split, BorderLayout.CENTER);
		
	}

	protected Component makeTapSearchTab() {
		/* Prepare a tabbed panel to contain the components. */
		tapSearchTab = new JPanel(new BorderLayout());
		
		//tapSearchTab.set
		tapSearchTab.add(makeTapSearch(), BorderLayout.CENTER);
		
        return tapSearchTab;
    }
	// TODO:
	
	protected Component makeTapSearch() {
		
		contenedorPrincipal = new JPanel ( new BorderLayout());
		tapTables = new JPanel(new BorderLayout());
		tapQuery = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbcUp = new GridBagConstraints();
		GridBagConstraints gbcDown = new GridBagConstraints();
		gbcUp.gridx = 0;
		gbcUp.gridy = 0;
		gbcUp.anchor = GridBagConstraints.CENTER;
		gbcUp.fill = GridBagConstraints.BOTH;
		tapQuery.add(new JLabel("TAP QUERY:"), gbcUp);

		gbcDown.anchor = GridBagConstraints.WEST;
		gbcDown.fill = GridBagConstraints.HORIZONTAL;
		gbcDown.gridx = 1;
		gbcDown.gridy = 0;
		gbcDown.weightx = 0.5;
		gbcDown.weighty = 1.0;
		tapQuery.add(new JTextField(), gbcDown);

		gbcDown.anchor = GridBagConstraints.SOUTH;
		gbcDown.fill = GridBagConstraints.RELATIVE;
		gbcDown.gridx = 0;
		gbcDown.gridy = 3;
		gbcDown.weightx = 0.0;
		gbcDown.weighty = 1.0;

		contenedorPrincipal.add(tapQuery,BorderLayout.NORTH);
		contenedorPrincipal.add(tapTables,BorderLayout.CENTER);
		return  contenedorPrincipal;
	}
	
	protected Component makeTapTables() {
		// TODO: Aqui insertar los componentes necesarios para mostrar las
		// tablas y sus columnas
		tapTables = new JPanel(new BorderLayout());

		return tapTables;
	}

	protected Component makeResults() {
		JPanel resultsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		status = new JLabel("");
		resultsPanel.add(status, gbc);

		results = new JSplitPane();
		results.setLeftComponent(makeResultsTable());
		results.setRightComponent(makeResultsDetails());
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		resultsPanel.add(results, gbc);
		return resultsPanel;
	}

	protected Component makeResultsDetails() {
		JButton buttonAddWorkFlow;
		resultsDetails = new JPanel(new GridBagLayout());
		resultsDetails.removeAll();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		serviceDetails = new VOServiceDetails();
		resultsDetails.add(new JScrollPane(serviceDetails,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), gbc);

		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		JPanel buttonPanel = new JPanel();
		addToWorkflow = new AddToWorkflow();
		buttonAddWorkFlow = new JButton(addToWorkflow);
		buttonPanel.add(buttonAddWorkFlow, gbc);
		
		
		resultsDetails.add(buttonPanel, gbc);

		// gbc.weighty = 0.1;
		// JPanel filler = new JPanel();
		// resultsDetails.add(filler, gbc); // filler
		return resultsDetails;
	}

	protected Component makeResultsTable() {
		resultsTableModel = new DefaultTableModel();
		resultsTableModel.addColumn("Service");
		resultsTableModel.addColumn("Short name");
		resultsTableModel.addColumn("Title");
		resultsTableModel.addColumn("Subjects");
		resultsTableModel.addColumn("Identifier");
		resultsTableModel.addColumn("Publisher");

		resultsTable = new JTable(resultsTableModel);
		// resultsTable.setAutoCreateColumnsFromModel(true);
		resultsTable.setAutoCreateRowSorter(true);
		// resultsTable.createDefaultColumnsFromModel();
		resultsTable.removeColumn(resultsTable.getColumn("Service"));

		resultsTable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);

		resultsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						
						Service tableSelection;
						resultsTable.revalidate();
						resultsTable.repaint();
						tapTables.removeAll(); 
						tableSelection = getTableSelection();
						// TODO:
						if (((JTextField) tapQuery.getComponent(1)).getText() != null) {
							AstroTapTableLoadDialog astroTapTableLoadDialog = new AstroTapTableLoadDialog();
							String service_url = ((JTextField) tapQuery.getComponent(1))
									.getText();
							// Internamente se hace una llamada asincrona, así pues será
							// el resultado de la llamada
							// La que dibuje las tablas en una lista
							astroTapTableLoadDialog.setSelectedService(service_url,
									tapTables);
							//tapTables.revalidate();
							//tapTables.repaint();
						}
						tapTables.revalidate();
						tapTables.repaint();
						getController().selectService(tableSelection);
					}
				});

		return new JScrollPane(resultsTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

	protected Component makeSearchBox() {
		JPanel searchBox = new JPanel(new GridBagLayout());
		GridBagConstraints gbcLeft = new GridBagConstraints();
		GridBagConstraints gbcMiddle = new GridBagConstraints();
		GridBagConstraints gbcRight = new GridBagConstraints();
		gbcLeft.gridx = 0;
		gbcLeft.anchor = GridBagConstraints.LINE_END;
		searchBox.add(new JLabel("Registry:"), gbcLeft);
		gbcMiddle.anchor = GridBagConstraints.WEST;
		gbcMiddle.fill = GridBagConstraints.HORIZONTAL;
		gbcMiddle.gridx = 1;
		gbcMiddle.weightx = 0.1;

		String[] registries = getEndpoints();
		registry = new JComboBox(registries);
		registry.setEditable(true);
		registry.addActionListener(new RegistryChange());

		searchBox.add(registry, gbcMiddle);

		searchBox.add(new JLabel("Keywords:"), gbcLeft);
		keywords = new JTextField(40);
		keywords.setAction(null);
		searchBox.add(keywords, gbcMiddle);

		gbcRight.gridx = 1;
		gbcRight.gridy = 2;
		gbcRight.anchor = GridBagConstraints.WEST;
		searchBox.add(makeSearchButtons(), gbcRight);

		GridBagConstraints filler = new GridBagConstraints();
		filler.gridx = 3;
		filler.weightx = 1.0;
		filler.fill = GridBagConstraints.HORIZONTAL;
		// searchBox.add(new JPanel(), filler);

		return searchBox;
	}

	protected String[] getEndpoints() {
		List<String> endpoints = new ArrayList<String>();
		for (URI registry : getModel().getEndpoints()) {
			endpoints.add(registry.toString());
		}
		return endpoints.toArray(new String[endpoints.size()]);
	}

	protected JPanel makeSearchButtons() {
		JPanel searchButtons = new JPanel(new FlowLayout());

		searchButtons.add(new JButton(coneSearch));
		searchButtons.add(new JButton(siaSearchAction));
		// Disabled as it generally gives 0 results
		// searchButtons.add(new JButton(slaSearchAction));
		searchButtons.add(new JButton(ssaSearchAction));
		searchButtons.add(new JButton(tapSearchAction));
		return searchButtons;
	}

	@Override
	public void onDisplay() {
	}

	@Override
	public void onDispose() {
		getController().cancelTaskIfNeeded();
	}

	public void setController(VOServicesController controller) {
		this.controller = controller;
	}

	public void setModel(VOServicesModel model) {
		this.model = model;
	}

	protected void setTableSelection(Service selectedService) {
		if (selectedService == getTableSelection()) {
			// Already there - perhaps selection was done in table?
			return;
		}
		for (int row = -1; row < resultsTableModel.getRowCount(); row++) {
			if (getServiceAtRow(row) == selectedService) {
				resultsTable.getSelectionModel().setSelectionInterval(row, row);
				break;
			}
		}
	}

	public void statusCancelled(CancellationException ex) {
		status.setText("<html><body><font color='#dd2222'>"
				+ "Cancelled search" + "</font><body></html>");
	}

	public void statusWarn(String msg) {
		status.setText("<html><body><font color='#dd2222'>" + msg
				+ "</font><body></html>");
	}

	public void statusFailed(Exception ex) {
		statusWarn("Search failed: " + ex.getLocalizedMessage());
	}

	public void statusFoundResults(int size) {
		status.setText(String
				.format("%d results for %s: %s", size, getModel()
						.getCurrentSearchType().getSimpleName(), getModel()
						.getSearch()));
	}

	public void statusInterrupted(InterruptedException ex) {
		statusWarn("Search interrupted: " + ex.getLocalizedMessage());
	}

	public void statusSearching(Class<? extends Capability> searchType,
			String search) {
		status.setText(String.format(
				"<html><body>Searching for %s: <b>%s</b> ...</body></html>",
				searchType.getSimpleName(), search));
		
	}

	protected void updateDetails() {
		Service service = getModel().getSelectedService();
		serviceDetails.setService(service);
		if (service == null) {
			addToWorkflow.setEnabled(false);
			return;
		} else {
			addToWorkflow.setEnabled(true);
			Boolean visible = false;
			for (Capability c : service.getCapability()) {
				if (c instanceof TableAccess || c.getStandardID().contains("TAP")) {
					tapSearchTab.setVisible(Boolean.TRUE);
					// Se obtiene la url del dato del servicio
					// Y se coloca en el input de query
					// Para que sea ejecutada
					for (Interface i : c.getInterface()) {
						if (!(i instanceof ParamHTTP)) {
							continue;
							// TODO: Handle WebService interface?
						}
						for (AccessURL accessURL : i.getAccessURL()) {
							((JTextField) tapQuery.getComponent(1))
									.setText(accessURL.getValue().trim());
						}
					}
					visible = true;
					break;
				}
			}
			if (!visible)
				tapSearchTab.setVisible(Boolean.FALSE);
		}
		addToWorkflow.setEnabled(true);
	}


	public void updateSelection() {
		updateDetails();
		setTableSelection(getModel().getSelectedService());
	}

	public void updateServices() {
		for (Service s : getModel().getServices()) {
			String shortName = s.getShortName();
			String title = s.getTitle();
			String subjects = s.getContent().getSubject().toString();
			String identifier = s.getIdentifier();
			String publisher = s.getCuration().getPublisher().getValue();
			resultsTableModel.addRow(new Object[] { s, shortName, title,
					subjects, identifier, publisher });
		}
	}

	public void statusInvalidEndpoint(Exception e) {
		statusWarn(String.format("Invalid registry %s %s", getModel()
				.getEndpoint(), e.getLocalizedMessage()));
	}

	public void statusEndpointOK() {
		status.setText("Endpoint OK");
	}

	public void statusEndpointStatus(Status endpointStatus) {
		statusWarn(String.format("Endpoint %s: %s", getModel().getEndpoint(),
				endpointStatus));
	}

	public void updateEndpoint() {
		URI endpoint = getModel().getEndpoint();
		String endpointStr = endpoint.toString();
		// Check if it's already there..
		for (String item : new ModelIterator<String>(registry.getModel())) {
			if (item.equals(endpointStr)) {
				registry.setSelectedItem(item);
				return;
			}
		}
		registry.addItem(endpointStr);
		registry.setSelectedItem(endpointStr);
	}

	public void statusEndpointChecking() {
		status.setText("Checking endpoint...");
	}
	
	public JPanel getTablePanel(){
		return tapTables;
	}
	
	public String TapTablePanelIsSynchronous(){
		TapQueryPanel tapQueryPanel = (TapQueryPanel) tapTables.getComponent(0);
		return String.valueOf(tapQueryPanel.isSynchronous());
	}
	
	public String TapTablePanelgetQuery(){
		TapQueryPanel tapQueryPanel = (TapQueryPanel) tapTables.getComponent(0);
		return tapQueryPanel.getAdql();
	}
	
	public String TapTablePanelgetMaxrec(){
		TapQueryPanel tapQueryPanel = (TapQueryPanel) tapTables.getComponent(0);
		return String
				.valueOf(tapQueryPanel.getCapabilityPanel().getMaxrec() == -1 ? 0
						: tapQueryPanel.getCapabilityPanel().getMaxrec());
	}
	
	public String TapTablePanelgetLang(){
		TapQueryPanel tapQueryPanel = (TapQueryPanel) tapTables.getComponent(0);
		return tapQueryPanel.getCapabilityPanel().getQueryLanguage();
	}

}
