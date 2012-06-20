import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import org.apache.log4j.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import java.awt.*;
import java.awt.event.*;

public class mainFrame {

	private static Logger logger = Logger.getRootLogger();
	ComPortSelection comPortSelection = new ComPortSelection();
	
	protected Shell shell;
	private Text text;
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		createLogger(Level.DEBUG);

		try {
			mainFrame window = new mainFrame();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void createLogger(Level logLevel) {
		try {
			SimpleLayout layout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.setLevel(logLevel);
			logger.addAppender(consoleAppender);
			// FileAppender fileAppender = new FileAppender( layout, "logs/"+
			// logLevel +".log", false );
			// logger.addAppender( fileAppender );
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:

		} catch (Exception ex) {
			System.out.println(ex);
		}
		logger.debug("Meine Debug-Meldung");
		logger.info("Meine Info-Meldung");
		logger.warn("Meine Warn-Meldung");
		logger.error("Meine Error-Meldung");
		logger.fatal("Meine Fatal-Meldung");
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		comPortSelection.close();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setTouchEnabled(true);
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		ArrayList<String> availableComPorts = new ArrayList<String>(
				ComPortSelection.listPorts());

		Combo comboPortList = new Combo(shell, SWT.NONE);
		comboPortList.addSelectionListener(new SelectionAdapter() {
			
			//String[] str = {"COM5"};
			public void widgetSelected(SelectionEvent e) {
				comPortSelection.open("");
				setupConnection();
			}
		});

		comboPortList.setBounds(10, 10, 91, 23);

		for (String s : availableComPorts) {
			// text.append(s + "\n\r");
			comboPortList.add(s);
		}

		text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		text.setBounds(107, 12, 236, 218);

		/**
		 * Create button.
		 */
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setBounds(349, 8, 75, 25);
		btnNewButton.setText("New Button");

		/**
		 * Create menu.
		 */

		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		MenuItem menuBarSubmenuPortConfig = new MenuItem(menuBar, SWT.CASCADE);
		menuBarSubmenuPortConfig.setText("Port Config");

		Menu menu = new Menu(menuBarSubmenuPortConfig);
		menuBarSubmenuPortConfig.setMenu(menu);

		MenuItem SubmenuPortConfigSubmenuBR = new MenuItem(menu, SWT.CASCADE);
		SubmenuPortConfigSubmenuBR.setText("Baud Rate");

		Menu menu_BR = new Menu(SubmenuPortConfigSubmenuBR);
		SubmenuPortConfigSubmenuBR.setMenu(menu_BR);

		MenuItem menuItem19200 = new MenuItem(menu_BR, SWT.RADIO);
		menuItem19200.setSelection(true);
		menuItem19200.setText("19200");
		menuItem19200.addSelectionListener(comPortSelection);

		MenuItem menuItem9600 = new MenuItem(menu_BR, SWT.RADIO);
		menuItem9600.setText("9600");
		menuItem9600.addSelectionListener(comPortSelection);

		MenuItem SubmenuPortConfigSubmenuPR = new MenuItem(menu, SWT.CASCADE);
		SubmenuPortConfigSubmenuPR.setText("Parity");

		Menu menu_PR = new Menu(SubmenuPortConfigSubmenuPR);
		SubmenuPortConfigSubmenuPR.setMenu(menu_PR);

		MenuItem menuItemNone = new MenuItem(menu_PR, SWT.RADIO);
		menuItemNone.setSelection(true);
		menuItemNone.setText("NONE");
		menuItemNone.setData(0);
		menuItemNone.addSelectionListener(comPortSelection);

		MenuItem menuItemOdd = new MenuItem(menu_PR, SWT.RADIO);
		menuItemOdd.setText("ODD");
		menuItemOdd.setData(1);
		menuItemOdd.addSelectionListener(comPortSelection);

		MenuItem menuItemEven = new MenuItem(menu_PR, SWT.RADIO);
		menuItemEven.setText("EVEN");
		menuItemEven.setData(2);
		menuItemEven.addSelectionListener(comPortSelection);

		MenuItem SubmenuPortConfigSubmenuSB = new MenuItem(menu, SWT.CASCADE);
		SubmenuPortConfigSubmenuSB.setText("Stop Bits");

		Menu menu_SB = new Menu(SubmenuPortConfigSubmenuSB);
		SubmenuPortConfigSubmenuSB.setMenu(menu_SB);

		MenuItem menuItemZero = new MenuItem(menu_SB, SWT.RADIO);
		menuItemZero.setText("0");
		menuItemZero.addSelectionListener(comPortSelection);

		MenuItem menuItemOne = new MenuItem(menu_SB, SWT.RADIO);
		menuItemOne.setSelection(true);
		menuItemOne.setText("1");
		menuItemOne.addSelectionListener(comPortSelection);

		MenuItem menuItemOnePointFive = new MenuItem(menu_SB, SWT.RADIO);
		menuItemOnePointFive.setText("1,5");
		menuItemOnePointFive
				.addSelectionListener(comPortSelection);

		MenuItem menuItemTwo = new MenuItem(menu_SB, SWT.RADIO);
		menuItemTwo.setText("2");
		menuItemTwo.addSelectionListener(comPortSelection);

		MenuItem SubmenuPortConfigSubmenuDB = new MenuItem(menu, SWT.CASCADE);
		SubmenuPortConfigSubmenuDB.setText("Data Bits");

		Menu menu_DB = new Menu(SubmenuPortConfigSubmenuDB);
		SubmenuPortConfigSubmenuDB.setMenu(menu_DB);

		MenuItem menuItemEigth = new MenuItem(menu_DB, SWT.RADIO);
		menuItemEigth.setSelection(true);
		menuItemEigth.setText("8");
		menuItemEigth.addSelectionListener(comPortSelection);

		MenuItem menuItemSeven = new MenuItem(menu_DB, SWT.RADIO);
		menuItemSeven.setText("7");
		menuItemSeven.addSelectionListener(comPortSelection);

	}

	protected void setupConnection() {
		// TODO Auto-generated method stub
		// menu_BR.
	}
	
/*	public static class ComPortRadioMenuItemListener extends SelectionAdapter {
		//SCom

		int x = 0;
		public void widgetSelected(SelectionEvent event) {
			MenuItem item = (MenuItem) event.widget;
			if (item.getSelection()) {
				
				System.out.print(item.getText() + " selected.\n\rThis is the " + x + " instance");
				x++;
			}
		}
	}
	*/
}