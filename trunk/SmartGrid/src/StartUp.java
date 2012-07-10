import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

//import ComPort.ComPortShell;


public class StartUp {

	protected Shell shell;
	private static Logger logger = Logger.getRootLogger();
	static Level logLevel = Level.DEBUG;
	ComPort cShell = new ComPort();
	private Text textSend;
	private Text textReceive;

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
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// TODO Auto-generated method stub
				cShell.close();
				cShell.dispose();
			}
		});
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Edit");
		
		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		
		MenuItem mntmPortConfig = new MenuItem(menu_1, SWT.NONE);
		mntmPortConfig.setText("Port Config");
		
		textReceive = new Text(shell, SWT.BORDER);
		textReceive.setBounds(0, 27, 434, 205);
		
		textSend = new Text(shell, SWT.BORDER);
		textSend.setBounds(0, 0, 434, 21);
		textSend.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == 13) {
					logger.debug("confirmed text to send!");

					String x = textReceive.getText();
					textReceive.setText(x  + "\n\r\n\r" + cShell.read());
				}
			}
		});
				
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
