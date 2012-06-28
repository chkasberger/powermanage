import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.graphics.Point;

//import ComPort.ComPortShell;


public class StartUp {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			StartUp window = new StartUp();
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

	private void createPopUps() {
		// TODO Auto-generated method stub
		/*Display display = Display.getDefault();
		PopUpForm pConf = new PopUpForm(display);
		pConf.open();
		
		DialogWindow myDialog = new DialogWindow(shell, 0);
		myDialog.open();
		myDialog.setText("fooBar");
		*/
		//ComPortShell cPort = new ComPortShell();
		ComPort cPort = new ComPort();
		
		
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		
		
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("New SubMenu");
		
		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		
		MenuItem mntmPortConfig = new MenuItem(menu_1, SWT.NONE);
		mntmPortConfig.setText("Port Config");
		mntmPortConfig.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				createPopUps();
				//cPort.openConfigWindow();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	}
}
