import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class mainFrame {

	protected Shell shell;
	private Text text;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			mainFrame window = new mainFrame();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	}
	
	/**
	 * Create contents of the window.
	 */	
	protected void createContents() {
		shell = new Shell();
		shell.setTouchEnabled(true);
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		Combo comboPortList = new Combo(shell, SWT.NONE);
		comboPortList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setupConnection();
			}
		});
		comboPortList.setBounds(10, 10, 91, 23);

		ArrayList<String> lst = new ArrayList<String>(ComPort.listPorts());

		text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		text.setBounds(107, 12, 236, 218);
		for (String s : lst) {
			text.append(s + "\n\r");
			comboPortList.add(s);
		}

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

		Menu menu_2 = new Menu(SubmenuPortConfigSubmenuBR);
		SubmenuPortConfigSubmenuBR.setMenu(menu_2);

		MenuItem menuItem19200 = new MenuItem(menu_2, SWT.RADIO);
		menuItem19200.setSelection(true);
		menuItem19200.setText("19200");

		MenuItem menuItem9600 = new MenuItem(menu_2, SWT.RADIO);
		menuItem9600.setText("9600");

		MenuItem SubmenuPortConfigSubmenuPR = new MenuItem(menu, SWT.CASCADE);
		SubmenuPortConfigSubmenuPR.setText("Parity");

		Menu menu_3 = new Menu(SubmenuPortConfigSubmenuPR);
		SubmenuPortConfigSubmenuPR.setMenu(menu_3);

		MenuItem menuItemNone = new MenuItem(menu_3, SWT.RADIO);
		menuItemNone.setSelection(true);
		menuItemNone.setText("NONE");

		MenuItem menuItemOdd = new MenuItem(menu_3, SWT.RADIO);
		menuItemOdd.setText("ODD");

		MenuItem menuItemEven = new MenuItem(menu_3, SWT.RADIO);
		menuItemEven.setText("EVEN");

		MenuItem SubmenuPortConfigSubmenuSB = new MenuItem(menu, SWT.CASCADE);
		SubmenuPortConfigSubmenuSB.setText("Stop Bits");

		Menu menu_4 = new Menu(SubmenuPortConfigSubmenuSB);
		SubmenuPortConfigSubmenuSB.setMenu(menu_4);

		MenuItem menuItemZero = new MenuItem(menu_4, SWT.RADIO);
		menuItemZero.setText("0");

		MenuItem menuItemOne = new MenuItem(menu_4, SWT.RADIO);
		menuItemOne.setSelection(true);
		menuItemOne.setText("1");

		MenuItem menuItemOnePointFive = new MenuItem(menu_4, SWT.RADIO);
		menuItemOnePointFive.setText("1,5");

		MenuItem menuItemTwo = new MenuItem(menu_4, SWT.RADIO);
		menuItemTwo.setText("2");

		MenuItem SubmenuPortConfigSubmenuDB = new MenuItem(menu, SWT.CASCADE);
		SubmenuPortConfigSubmenuDB.setText("Data Bits");

		Menu menu_1 = new Menu(SubmenuPortConfigSubmenuDB);
		SubmenuPortConfigSubmenuDB.setMenu(menu_1);

		MenuItem menuItemEigth = new MenuItem(menu_1, SWT.RADIO);
		menuItemEigth.setSelection(true);
		menuItemEigth.setText("8");

		MenuItem menuItemSeven = new MenuItem(menu_1, SWT.RADIO);
		menuItemSeven.setText("7");

	}

	protected void setupConnection() {
		// TODO Auto-generated method stub
		
	}


}
