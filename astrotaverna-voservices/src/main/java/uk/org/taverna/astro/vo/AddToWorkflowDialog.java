package uk.org.taverna.astro.vo;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.ivoa.xml.voresource.v1.Service;
import net.sf.taverna.t2.workbench.ui.impl.Workbench;

public class AddToWorkflowDialog extends JDialog {
	public class AddAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public AddAction() {
			super("Add to workflow");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Map<String, String> parameters = getParameters();
			restServiceDescription.setParameters(parameters);
			getController().addToWorkflow(restServiceDescription);
			dispose();
		}

	}

	public class CancelAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CancelAction() {
			super("Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private static final long serialVersionUID = 1L;

	private VOServicesController controller;

	private VOServicesModel model;

	private final VOServiceDescription restServiceDescription;

	private final Service service;

	protected AddAction addAction = new AddAction();
	protected CancelAction cancelAction = new CancelAction();

	protected Map<String, JTextField> fields = new HashMap<String, JTextField>();

	private boolean initialized = false;

	public AddToWorkflowDialog(VOServiceDescription restServiceDescription,
			Service service) {
		super(Workbench.getInstance());
		this.restServiceDescription = restServiceDescription;
		this.service = service;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getRootPane().registerKeyboardAction(new ActionListener() {
			// http://stackoverflow.com/questions/642925/swing-how-do-i-close-a-dialog-when-the-esc-key-is-pressed
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

	}

	@Override
	public void show() {
		if (!initialized) {
			// Delayed initialization
			initialize();
			initialized = true;
		}
		super.show();
	}

	public Map<String, String> getParameters() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		for (Entry<String, JTextField> s : fields.entrySet()) {
			String param = s.getKey();
			JTextField field = s.getValue();
			if (!field.getText().isEmpty()) {
				parameters.put(param, field.getText());
			}
		}
		return parameters;
	}

	public VOServicesController getController() {
		return controller;
	}

	public VOServicesModel getModel() {
		return model;
	}

	public VOServiceDescription getRestServiceDescription() {
		return restServiceDescription;
	}

	public Service getService() {
		return service;
	}

	protected void initialize() {
		setTitle("Add VO service to workflow");
		setLayout(new GridBagLayout());

		GridBagConstraints gbcLeft = new GridBagConstraints();
		gbcLeft.gridx = 0;
		gbcLeft.weightx = 0.2;
		gbcLeft.anchor = GridBagConstraints.LINE_END;

		GridBagConstraints gbcRight = new GridBagConstraints();
		gbcRight.gridx = 1;
		gbcRight.weightx = 0.7;
		gbcRight.fill = GridBagConstraints.HORIZONTAL;
		gbcRight.anchor = GridBagConstraints.LINE_START;

		GridBagConstraints gbcBoth = new GridBagConstraints();
		gbcBoth.gridx = 0;
		gbcBoth.gridwidth = 2;
		gbcBoth.weightx = 0.2;
		gbcBoth.weighty = 0.0;
		gbcBoth.fill = GridBagConstraints.HORIZONTAL;
		gbcBoth.anchor = GridBagConstraints.CENTER;

		add(new JLabel(String.format("<html><body>"
				+ "<h3>Add %s to workflow</h3>"
				+ "<p>You may specify constant parameters here, or leave the "
				+ "parameter field blank to supply the field by connecting "
				+ "the corresponding input port the workflow.</p>"
				+ "</body></html>", service.getShortName())), gbcBoth);
		add(new JPanel(), gbcBoth);

		for (Entry<String, Boolean> entry : getModel()
				.parametersForSearchType().entrySet()) {
			String param = entry.getKey();
			JLabel label = new JLabel();
			if ((entry.getValue())) {
				// required in bold
				label.setText(String.format("<html><body><b>%s</b></body></html>", param));
			} else {
				label.setText(String.format("<html><body>%s</body></html>", param));
			}
			add(label, gbcLeft);
			JTextField textField = new JTextField(10);
			add(textField, gbcRight);
			label.setLabelFor(textField);
			fields.put(param, textField);
		}

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(new JButton(addAction));
		buttonPanel.add(new JButton(cancelAction));
		add(buttonPanel, gbcBoth);

		GridBagConstraints gbcFiller = new GridBagConstraints();
		gbcRight.gridx = 2;
		gbcRight.weightx = 1.0;
		gbcRight.fill = GridBagConstraints.HORIZONTAL;
		add(new JPanel(), gbcFiller);

		setMinimumSize(new Dimension(450, 500));
	}

	public void setController(VOServicesController controller) {
		this.controller = controller;
	}

	public void setModel(VOServicesModel model) {
		this.model = model;
	}

}
