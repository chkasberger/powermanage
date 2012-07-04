import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;

//import ComPort.ComPortShell;


public class StartUp {

	protected Shell shell;
	private static Logger logger = Logger.getRootLogger();
	static Level logLevel = Level.DEBUG;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			configureLogger();
			StartUp window = new StartUp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void configureLogger() {
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
                logger.debug(ex);
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
	ComPort cShell = new ComPort();
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
		cShell.close();		
	}
	
	private void createPopUps() {
		//ComPort cShell = new ComPort();
		cShell.configure();
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
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setBounds(200, 85, 75, 25);
		btnNewButton.setText("New Button");
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
