package org.pradb2.dsloader;

import it.sauronsoftware.ftp4j.FTPException;

import java.io.File;
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
import org.eclipse.swt.widgets.Composite;

public class DSLoader {
	private static Logger LOGGER = Logger.getLogger("Global Logger"); 
	
	Display display;
	Shell shell;
	private Text txtSclmLevel;
	private Text txtType;
	private Text txtMember;
	private Text txtSaveTo;
	private Text txtProgressLog;
	private Button btnBrowse;
	private Label lblType;
	private Label lblMember;
	private Button btnPull;
	private Text txtUsername;
	private Label lblUsername;
	private Label lblPassword;
	private Text txtPassword;
	private Composite composite;
	final private String SERVER = "usilca31.ca.com";
	
	
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
		shell.setSize(628 , 446);
		GridLayout globalShellLayout = new GridLayout(5, false);
		shell.setLayout(globalShellLayout);
	}
	
	private void setUpShellComponents() {
		composite = new Composite(shell, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite.widthHint = 300;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(3, false));
		
		Label lblSclmLevel = new Label(composite, SWT.NONE);
		lblSclmLevel.setAlignment(SWT.RIGHT);
		lblSclmLevel.setText("SCLM Level: ");
		
		txtSclmLevel = new Text(composite, SWT.BORDER);
		txtSclmLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		lblType = new Label(composite, SWT.NONE);
		lblType.setAlignment(SWT.RIGHT);
		lblType.setText("Type:");
		
		txtType = new Text(composite, SWT.BORDER);
		GridData gd_txtType = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtType.widthHint = 50;
		txtType.setLayoutData(gd_txtType);
		new Label(composite, SWT.NONE);
		
		lblMember = new Label(composite, SWT.NONE);
		lblMember.setAlignment(SWT.RIGHT);
		lblMember.setText("Member:");
		
		txtMember = new Text(composite, SWT.BORDER);
		GridData gd_txtMember = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtMember.widthHint = 60;
		txtMember.setLayoutData(gd_txtMember);
		new Label(composite, SWT.NONE);
		
		Label lblSaveTo = new Label(composite, SWT.NONE);
		lblSaveTo.setAlignment(SWT.RIGHT);
		lblSaveTo.setText("Save To:");
		
		txtSaveTo = new Text(composite, SWT.BORDER);
		txtSaveTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.setText("Browse");
		
		lblUsername = new Label(shell, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblUsername.setText("Username:");
		
		txtUsername = new Text(shell, SWT.BORDER);
		GridData gd_txtUsername = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_txtUsername.widthHint = 70;
		txtUsername.setLayoutData(gd_txtUsername);
		
		lblPassword = new Label(shell,  SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblPassword.setText("Password:");
		
		txtPassword = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		GridData gd_txtPassword = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_txtPassword.widthHint = 70;
		txtPassword.setLayoutData(gd_txtPassword);
		
		txtProgressLog = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtProgressLog.setEditable(false);
		txtProgressLog.setEnabled(false);
		txtProgressLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		btnPull = new Button(shell, SWT.NONE);
		GridData gd_btnPull = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_btnPull.widthHint = 70;
		btnPull.setLayoutData(gd_btnPull);
		btnPull.setText("Pull");
	}

	private void checkInputFields() throws DSLoaderException {
		if (txtSclmLevel.getText() == "") { 
			throw (new DSLoaderException("Empty SCLM level"));
		}
		
		if (txtType.getText() == "") { 
			throw (new DSLoaderException("Empty member"));
		}

		if (txtMember.getText() == "") { 
			throw (new DSLoaderException("Empty type name"));
		}
		
		File dir = new File(txtSaveTo.getText());
		if (txtSaveTo.getText() == "" && !dir.isDirectory()) { 
			throw (new DSLoaderException("Directory is not specified correctly\n"));
		}

	}
	
	private void pullData (DSLoaderFTPClient ftpClient) throws DSLoaderException  {
		String fullFilePath = txtSaveTo.getText() + "\\" + txtMember.getText() + "." + txtType.getText(); 
		File localFile = new File (fullFilePath);
		
		try {
			ftpClient.changeDirectory(txtSclmLevel.getText());
		}
		catch (FTPException e) {
			txtProgressLog.append("changeDirectory FTPexception: " + e.getMessage() + "\n");
			return;
		}
		catch (Exception e) {
			txtProgressLog.append("changeDirectory Exception: " + e.getMessage() + "\n");
			return;
		}
		
		try {
			ftpClient.download(fullFilePath, localFile);
		}
		catch (FTPException e) {
			txtProgressLog.append("Download FTPexception: " + e.getMessage() + "\n");
			return;
		}
		catch (Exception e) {
			txtProgressLog.append("Download Exception: " + e.getMessage() + "\n");
			return;
		}
		
		
		txtProgressLog.append("Pulling some data \n");
	}
	
	private void defineBtnBrowse () {
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NULL);
				String path = dialog.open();
				
				LOGGER.info("Path: " + path);
				
				if (path != null) {
					txtSaveTo.setText(path.toString());
					LOGGER.info("Directory name: " + txtSaveTo.getText());
				}
			}
		});
	}

	private void defineBtnPull ()  {
		btnPull.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					checkInputFields();
				}
				catch (DSLoaderException exception) {
					txtProgressLog.setText(exception.getMessage() + "\n");
					return;
				}
				
				DSLoaderFTPClient ftpClient = new DSLoaderFTPClient();
				
				try {
					ftpClient.connect(SERVER);
					ftpClient.login(txtUsername.getText(), txtPassword.getText());
				}
				catch (FTPException e) {
					txtProgressLog.append("FTP exception: " + e.getMessage() + "\n");
					return;
				}
				catch (Exception e) {
					txtProgressLog.append(e.getStackTrace().toString() + "\n");
					return;
				}
				
				//No exception was thrown while connecting
				txtProgressLog.append("Connection successfull..\n");
				
				try {
					pullData (ftpClient);
				}
				catch (DSLoaderException e) {
					txtProgressLog.append(e.getMessage());
					return;
				}
				finally {
					if (ftpClient.isConnected()){
						try {
							ftpClient.disconnect(true);
						}
						catch (Exception e) {
							txtProgressLog.append("Exception while disconnecting: " + e.getMessage() + "\n");
						}
					}
				}
				
			}
			
		});
	}
	
	public void init () {
		
		setUpShell();
		setUpShellComponents();
		defineBtnBrowse ();
		defineBtnPull();
		
	}

	public static void main (String[] args) {
		
		DSLoader dsLoader = new DSLoader();
		dsLoader.init();
		dsLoader.run();
	}
}
