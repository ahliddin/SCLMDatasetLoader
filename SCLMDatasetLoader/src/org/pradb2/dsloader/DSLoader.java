package org.pradb2.dsloader;

import java.util.logging.Logger;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.pradb2.exceptions.DSLoaderException;

public class DSLoader {
	private static Logger LOGGER = Logger.getLogger("Global Logger"); 
	
	Display display;
	Shell shell;
	private Text txtSclmLevel;
	private Text txtMember;
	private Text txtType;
	private Text txtSaveTo;
	private Text txtProgressLog;
	private Button btnBrowse;
	private Label lblType;
	private Label lblMember;
	private Button btnPull;
	
	
	public DSLoader() {
		display = new Display();
		shell = new Shell(display);
	}
	
	public void run () {
//		shell.pack(); //Causes the receiver to be resized to its preferred size. 
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		shell.dispose();
	}
	
	private void setUpShell() {
		shell.setText("SCLM Dataset Loader");
		shell.setSize(571 , 369);
		GridLayout globalShellLayout = new GridLayout(4, false);
		shell.setLayout(globalShellLayout);
	}
	
	private void setUpLabel (Label label, GridData gdLayout,  String labelText) {
		
		label.setLayoutData(gdLayout);
		label.setText(labelText);
	}
	
//	private void setUpText (Text text, GridData gdData) {
//		
//		text.setLayoutData(gdData);
//	}
//
//	public void displayFiles(Text txtSaveTo, String[] files) {
//		for (int i = 0; files != null && i < files.length; i++) {
//			txtSaveTo.setText(files[i]);
//			txtSaveTo.setEditable(true);
//		}
//	}
	private void defBtnBrowse () {
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NULL);
				String path = dialog.open();
				
				LOGGER.info("Path: " + path);
				
				if (path != null) {
					txtSaveTo.setText(path.toString());
					LOGGER.info("Directory name: " + txtSaveTo.getText());
//					File file = new File(path);
//					System.out.println (file.getName());
//					
//					if (file.isFile())
//						displayFiles(txtSaveTo, new String[] { file.toString() });
//					else
//						displayFiles(txtSaveTo, file.list());
				}
			}
		});
	}
	private void checkInputFields() throws DSLoaderException {
		if (txtSclmLevel.getText() == "") { 
			throw (new DSLoaderException("Empty SCLM level"));
		}
		
		if (txtMember.getText() == "") { 
			throw (new DSLoaderException("Empty member"));
		}

		if (txtType.getText() == "") { 
			throw (new DSLoaderException("Empty type name"));
		}

		if (txtSaveTo.getText() == "") { 
			throw (new DSLoaderException("You have to define the directory"));
		}

	}
	
	private void defBtnPull () {
		btnPull.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					checkInputFields();
				}
				catch (DSLoaderException exception) {
					txtProgressLog.setText(exception.getMessage() + "\n");
				}
			}
			
		});
	}
	
	
	private void init () {
		
		setUpShell();
		
		Label labelSclmLevel = new Label(shell, SWT.NONE);
		labelSclmLevel.setAlignment(SWT.RIGHT);
		GridData gd_labelSclmLevel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_labelSclmLevel.widthHint = 90;
		setUpLabel (labelSclmLevel,
					gd_labelSclmLevel, 
					"SCLM Level:");
		
		txtSclmLevel = new Text(shell, SWT.BORDER);
		GridData gdText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gdText.widthHint = 150;
		gdText.minimumWidth = 50;
		txtSclmLevel.setLayoutData(gdText);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblType = new Label(shell, SWT.NONE);
		lblType.setAlignment(SWT.RIGHT);
		GridData gd_lblType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblType.widthHint = 90;
		lblType.setLayoutData(gd_lblType);
		lblType.setText("Type:");
		
		txtMember = new Text(shell, SWT.BORDER);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 150;
		txtMember.setLayoutData(gd_text);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblMember = new Label(shell, SWT.NONE);
		lblMember.setAlignment(SWT.RIGHT);
		GridData gd_lblMember = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblMember.widthHint = 90;
		lblMember.setLayoutData(gd_lblMember);
		lblMember.setText("Member:");
		
		txtType = new Text(shell, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		txtType.setLayoutData(gd_text_1);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Label labelSaveTo = new Label(shell, SWT.NONE);
		labelSaveTo.setAlignment(SWT.RIGHT);
		GridData gd_labelSaveTo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_labelSaveTo.widthHint = 90;
		labelSaveTo.setLayoutData(gd_labelSaveTo);
		labelSaveTo.setText("Save To:");
		setUpLabel (labelSaveTo,
					new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1),
					"Save to:");
		
		txtSaveTo = new Text(shell, SWT.BORDER);
		GridData gd_txtSaveTo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtSaveTo.widthHint = 200;
		txtSaveTo.setLayoutData(gd_txtSaveTo);
		
		btnBrowse = new Button(shell, SWT.NONE);
		GridData gd_btnBrowse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse.widthHint = 90;
		btnBrowse.setLayoutData(gd_btnBrowse);
		btnBrowse.setText("Browse");
		
		btnPull = new Button(shell, SWT.NONE);
		GridData gd_btnPull = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnPull.widthHint = 89;
		btnPull.setLayoutData(gd_btnPull);
		btnPull.setText("Pull");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		txtProgressLog = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtProgressLog.setEditable(false);
		txtProgressLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		defBtnBrowse ();
		defBtnPull();
	}

	public static void main (String[] args) {
		
		DSLoader dsLoader = new DSLoader();
		dsLoader.init();
		dsLoader.run();
	}
}
