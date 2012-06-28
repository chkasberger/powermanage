import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;


public class ComPortShell {

	protected Shell shlPortConfig;

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();		
		shlPortConfig.open();
		shlPortConfig.layout();
		while (!shlPortConfig.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {

		shlPortConfig = new Shell();
		shlPortConfig.setMinimumSize(new Point(100, 30));
		shlPortConfig.setSize(378, 165);
		shlPortConfig.setText("Port Config");
		shlPortConfig.setLayout(new FormLayout());
		
		Group grpBaudRate = new Group(shlPortConfig, SWT.NONE);
		FormData fd_grpBaudRate = new FormData();
		fd_grpBaudRate.bottom = new FormAttachment(0, 119);
		fd_grpBaudRate.right = new FormAttachment(0, 90);
		grpBaudRate.setLayoutData(fd_grpBaudRate);
		grpBaudRate.setText("Baud Rate");
		
		Button rbBaud_19200 = new Button(grpBaudRate, SWT.RADIO);
		rbBaud_19200.setBounds(10, 20, 60, 16);
		rbBaud_19200.setText("19200");
		
		Button rbBaud_9600 = new Button(grpBaudRate, SWT.RADIO);
		rbBaud_9600.setBounds(10, 40, 60, 16);
		rbBaud_9600.setText("9600");
		
		Group grpParity = new Group(shlPortConfig, SWT.NONE);
		FormData fd_grpParity = new FormData();
		fd_grpParity.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpParity.right = new FormAttachment(grpBaudRate, 92, SWT.RIGHT);
		fd_grpParity.left = new FormAttachment(grpBaudRate, 12);
		fd_grpParity.bottom = new FormAttachment(0, 119);
		grpParity.setLayoutData(fd_grpParity);
		grpParity.setText("Parity");
		
		Button rbParity_NONE = new Button(grpParity, SWT.RADIO);
		rbParity_NONE.setBounds(10, 20, 60, 16);
		rbParity_NONE.setText("NONE");
		
		Button rbParity_ODD = new Button(grpParity, SWT.RADIO);
		rbParity_ODD.setBounds(10, 40, 60, 16);
		rbParity_ODD.setText("ODD");
		
		Button rbParity_EVEN = new Button(grpParity, SWT.RADIO);
		rbParity_EVEN.setBounds(10, 60, 60, 16);
		rbParity_EVEN.setText("EVEN");
		
		Group grpStopBits = new Group(shlPortConfig, SWT.NONE);
		FormData fd_grpStopBits = new FormData();
		fd_grpStopBits.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpStopBits.left = new FormAttachment(grpParity, 6);
		fd_grpStopBits.bottom = new FormAttachment(0, 119);
		fd_grpStopBits.right = new FormAttachment(0, 268);
		grpStopBits.setLayoutData(fd_grpStopBits);
		grpStopBits.setText("Stop Bits");
		
		Button rbStop_ONE = new Button(grpStopBits, SWT.RADIO);
		rbStop_ONE.setBounds(10, 20, 60, 16);
		rbStop_ONE.setText("ONE");
		
		Button rbStop_TWO = new Button(grpStopBits, SWT.RADIO);
		rbStop_TWO.setText("TWO");
		rbStop_TWO.setBounds(10, 40, 60, 16);
		
		Group grpDataBits = new Group(shlPortConfig, SWT.NONE);
		FormData fd_grpDataBits = new FormData();
		fd_grpDataBits.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpDataBits.left = new FormAttachment(grpStopBits, 6);
		fd_grpDataBits.bottom = new FormAttachment(0, 119);
		fd_grpDataBits.right = new FormAttachment(0, 354);
		grpDataBits.setLayoutData(fd_grpDataBits);
		grpDataBits.setText("Data Bits");
		
		Button button_2 = new Button(grpDataBits, SWT.RADIO);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		button_2.setText("8");
		button_2.setBounds(10, 22, 60, 16);
		
		Button button_3 = new Button(grpDataBits, SWT.RADIO);
		button_3.setText("7");
		button_3.setBounds(10, 44, 60, 16);
		
		Combo combo = new Combo(shlPortConfig, SWT.NONE);
		fd_grpBaudRate.top = new FormAttachment(combo, 6);
		fd_grpBaudRate.left = new FormAttachment(combo, 0, SWT.LEFT);
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 10);
		fd_combo.left = new FormAttachment(0, 10);
		combo.setLayoutData(fd_combo);

	}
}
