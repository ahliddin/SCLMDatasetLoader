package org.pradb2.dsloader;

import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

enum SclmLevel {
	WK1999S,
	WK2999S,
	WK3999S,
	WK4999S,
	WK5999S,
	WK6999S,
	WK7999S,
	WRK999S,
	HLD999S,
	PRD999S
}

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
	
	private String SCLM_LEVEL;
	private String MEMBER;
	private String TYPE;
	private String LOCAL_DIR;
	
	
	
	public DSLoader() {
		display = new Display();
		shell = new Shell(display);
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
		if ( (SCLM_LEVEL=txtSclmLevel.getText()) == "") { 
			throw (new DSLoaderException("Empty SCLM level"));
		}
		
		if ( (TYPE = txtType.getText()) == "") { 
			throw (new DSLoaderException("Empty member"));
		}

		if ( (MEMBER = txtMember.getText()) == "") { 
			throw (new DSLoaderException("Empty type name"));
		}
		
		LOCAL_DIR = txtSaveTo.getText();
		File dir = new File(LOCAL_DIR);
		if (LOCAL_DIR == "" && !dir.isDirectory()) { 
			throw (new DSLoaderException("Directory is not specified correctly\n"));
		}
		
	}
	
	List<String> orderUp (String product, String level) {
		List<String> levels = new ArrayList<String> ();
		
		if (level.startsWith("WK")) {
			levels.add(product + "." + level + "." + TYPE);
			levels.add(product + "." + SclmLevel.WRK999S.name() + "." + TYPE);
			levels.add(product + "." + SclmLevel.HLD999S.name() + "."+ TYPE);
			levels.add(product + "." + SclmLevel.PRD999S.name() + "."+ TYPE);
		}
		else if (level.startsWith("WRK")) {
			levels.add(product + "." + level + "." + TYPE);
			levels.add(product + "." + SclmLevel.HLD999S.name() + "."+ TYPE);
			levels.add(product + "." + SclmLevel.PRD999S.name() + "."+ TYPE);
		}
		else if (level.startsWith("HLD")) {
			levels.add(product + "." + level + "." + TYPE);
			levels.add(product + "." + SclmLevel.PRD999S.name() + "."+ TYPE);
		}
		else {
			levels.add(product + "." + level + "." + TYPE);
		}
		return levels;
	}
	
	List<String> getOrderedSclmLevels () throws DSLoaderException {
//		neatUp(txtSclmLevel.getText());
		String[] splitSclmLevel = SCLM_LEVEL.split("\\.");
		List<String> orderedSclmLevels = new ArrayList<String>();
		
		if (splitSclmLevel.length == 1){
			
			orderedSclmLevels = orderUp ("PRA", splitSclmLevel[0]);
		}
		else if (splitSclmLevel.length == 2) {
			orderedSclmLevels = orderUp (splitSclmLevel[0], splitSclmLevel[1]);
		}
		else if (splitSclmLevel[2] != TYPE) {
				throw new DSLoaderException ("Value of Type field doesn't match with type in SCLM level\n");
		}
		
		orderedSclmLevels = orderUp (splitSclmLevel[0], splitSclmLevel[1]);
				
		return orderedSclmLevels;
	}
	
	private void fillMembers (DSLoaderFTPClient ftpClient, List<String> members, 
							  String level, String pattern,
							  Set<String> memberCheck) throws Exception {
		String[] memberList = null;
		
		ftpClient.changeDirectory("'" + level + "'");
		try {
			memberList = ftpClient.listNames();
		}
		catch ( Exception e ) {
			e.printStackTrace(System.out);
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
		if (memberList != null) {
			for (String member : memberList) {
				if (!memberCheck.contains(member)) {
					if (member.matches(pattern)) {
						members.add(member);
						memberCheck.add(member);
					}
				}
			}
		}
	}
	
	private void fillMemberLists (String pattern,
								  List<String> wknMembers, 
								  List<String> wrkMembers,
								  List<String> hldMembers,
								  List<String> prdMembers,
								  DSLoaderFTPClient ftpClient,
								  List<String> orderedSclmLevels) throws Exception {
		Set<String> memberCheck = new HashSet<String>();
		
		for (String level : orderedSclmLevels) {
			if (level.matches(".*WK.999S.*")) {
				fillMembers (ftpClient, wknMembers, level, pattern, memberCheck);
			}
			if (level.matches (".*WRK999S.*")) {
				fillMembers (ftpClient, wrkMembers, level, pattern, memberCheck);
			}
			if (level.matches(".*HLD999S.*")) {
				fillMembers (ftpClient, hldMembers, level, pattern, memberCheck);
			}
			if (level.matches(".*PRD999S.*")) {
				fillMembers (ftpClient, prdMembers, level, pattern, memberCheck);
			}
		}
	}
	
	private void downloadMembers (List<String> wknMembers, 
								  List<String> wrkMembers,
								  List<String> hldMembers,
								  List<String> prdMembers,
								  DSLoaderFTPClient ftpClient,
								  List<String> orderedSclmLevels) throws DSLoaderException, Exception {
		
		
		for (String level : orderedSclmLevels) {
			File localFile;
			
			ftpClient.changeDirectory("'" + level + "'");
			
			if (!wknMembers.isEmpty()) {
				for (String member : wknMembers) {
					localFile = new File (LOCAL_DIR + "\\" + member + "." + TYPE);
					ftpClient.download(member, localFile);
					txtProgressLog.append(String.format("Pulling dataset %s(%s)..\n", level, member));
				}
			}
			if (!wrkMembers.isEmpty()) {
				for (String member : wrkMembers) {
					localFile = new File (LOCAL_DIR + "\\" + member + "." + TYPE);
					ftpClient.download(member, localFile);
					txtProgressLog.append(String.format("Pulling dataset %s(%s)..\n", level, member));
				}
			}
			if (!hldMembers.isEmpty()) {
				for (String member : hldMembers) {
					localFile = new File (LOCAL_DIR + "\\" + member + "." + TYPE);
					ftpClient.download(member, localFile);
					txtProgressLog.append(String.format("Pulling dataset %s(%s)..\n", level, member));
				}
			}
			if (!prdMembers.isEmpty()) {
				for (String member : prdMembers) {
					localFile = new File (LOCAL_DIR + "\\" + member + "." + TYPE);
					ftpClient.download(member, localFile);
					txtProgressLog.append(String.format("Pulling dataset %s(%s)..\n", level, member));
				}
			}
		}
		
	}
	
	private void pullData (DSLoaderFTPClient ftpClient)   {
		//defining regex pattern
		String pattern = MEMBER.replace("*", ".*");
		List<String> wknMembers = new ArrayList<String>();
		List<String> wrkMembers = new ArrayList<String>();
		List<String> hldMembers = new ArrayList<String>();
		List<String> prdMembers = new ArrayList<String>();
		List<String> orderedSclmLevels;
		
		//here we get the ordered list of SCLM libs.
		try {
			orderedSclmLevels = getOrderedSclmLevels();
		}
		catch (DSLoaderException e) {
			txtProgressLog.append ("DSLoaderException: " + e.getMessage());
			return;
		}
		
		//we go from down to top and fill in members into appropriate lists
		try { 
			fillMemberLists (pattern, wknMembers, wrkMembers, hldMembers, prdMembers, ftpClient, orderedSclmLevels);
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
			txtProgressLog.append ("fillMemberLists FTPexception: " + e.getMessage() + "\n");
			return;
		}
		
		try {
			downloadMembers (wknMembers, wrkMembers, hldMembers, prdMembers, ftpClient, orderedSclmLevels);
		}
		catch (FTPException e) {
			e.printStackTrace(System.out);
			txtProgressLog.append ("Download FTPexception: " + e.getMessage() + "\n");
			return;
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
			txtProgressLog.append ("Download Exception: " + e.getMessage() + "\n");
			return;
		}
		txtProgressLog.append("Success! Pulled all members.");
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
				
				ftpClient.setType(DSLoaderFTPClient.TYPE_TEXTUAL);	
				try {
					ftpClient.connect(SERVER);
					ftpClient.login(txtUsername.getText(), txtPassword.getText());
				}
				catch (FTPException e) {
					e.printStackTrace(System.out);
					txtProgressLog.append("FTP exception: " + e.getMessage() + "\n");
					return;
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					txtProgressLog.append(e.getStackTrace().toString() + "\n");
					return;
				}
				
				//No exception was thrown while connecting
				txtProgressLog.append("Connection successfull..\n");
				
				pullData (ftpClient);
				if (ftpClient.isConnected()){
					try {
						ftpClient.disconnect(true);
					}
					catch (Exception e) {
						txtProgressLog.append("Exception while disconnecting: " + e.getMessage() + "\n");
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

	public static void main (String[] args) {
		
		DSLoader dsLoader = new DSLoader();
		dsLoader.init();
		dsLoader.run();
	}
}
