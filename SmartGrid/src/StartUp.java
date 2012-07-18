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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;

//import ComPort.ComPortShell;


public class StartUp {

	protected Shell shell;
	private static Logger logger = Logger.getRootLogger();
	static Level logLevel = Level.DEBUG;
    // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
	
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


        } catch (Exception ex) {
                logger.debug(ex + "@" + JUtil.getMethodName(1));
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
	Menu menu;
	private void createPopUps() {
		cShell.configure(shell.getLocation());
	}

	/**
	 * Create contents of the window.
	 */
	int foo = 0;
	protected void createContents() {
		cShell.configure("COM5",57600);
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				shell.setVisible(false);
				cShell.close();
				cShell.dispose();
			}
		});
		
		menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Edit");
		
		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		
		MenuItem mntmPortConfig = new MenuItem(menu_1, SWT.NONE);
		mntmPortConfig.setText("Port Config");
		
		textReceive = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		textReceive.setBounds(0, 27, 218, 192);
		textSend = new Text(shell, SWT.BORDER);
		textSend.setBounds(0, 0, 218, 21);
		textSend.setText("310100000001FE7132");
		
		Group grpEncode = new Group(shell, SWT.NONE);
		grpEncode.setText("Encode");
		grpEncode.setBounds(242, 10, 70, 105);
		
		Button btnByte_0 = new Button(grpEncode, SWT.RADIO);
		btnByte_0.setBounds(10, 24, 50, 16);
		btnByte_0.setText("byte");
		btnByte_0.setEnabled(true);
		
		Button btnByte_1 = new Button(grpEncode, SWT.RADIO);
		btnByte_1.setText("String");
		btnByte_1.setBounds(10, 46, 50, 16);
		
		Button btnByte_2 = new Button(grpEncode, SWT.RADIO);
		btnByte_2.setText("ASCII");
		btnByte_2.setBounds(10, 68, 50, 16);

		textSend.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {	
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == 13) {
					logger.debug("confirmed text to send!");

					//String x = textReceive.getText();
					byte[] byteArray = {0x31,0x01,0x00,0x00,0x00,0x01,(byte) 0xFE,0x71,0x32};
					foo++;

					byte[] smartArray = {0x06,0x00,0x02,0x00,0x0D,0x0A};
					cShell.write(byteArray);
					textReceive.setRedraw(false);
					String str = textReceive.getText();
					textReceive.setText((String) cShell.read(ComPort.ReturnType.STRING) + "\n");
					textReceive.append(str);
					textReceive.setRedraw(true);
				}
			}
		});
		
		mntmPortConfig.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				createPopUps();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

	}
}
