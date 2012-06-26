import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

import org.apache.log4j.*;

public class mainFrame {

	private static Logger logger = Logger.getRootLogger();
	private Level logLevel = Level.DEBUG;
	
	private JFrame frame;
	ComPort comPort = new ComPort();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainFrame window = new mainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public mainFrame() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		configureLogger();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		createMenu();
		
	}

	private void configureLogger() {
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

	private void createMenu(){
		/*
		 * Create menu bar
		 */
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		/*
		 * Create sub menu "Config" 
		 */
		JMenu mnConfig = new JMenu("Config");
		menuBar.add(mnConfig);
		
		/*
		 * Create ComPort configuration menu
		 */
		JMenu menu = new JMenu("Port Config");
		mnConfig.add(menu);
		
		createMenuPortConfig(menu);
		
	}
	
	private void createMenuPortConfig(JMenu menu) {
		// TODO Auto-generated method stub
		/*
		 * create button groups for port settings
		 */
		final ButtonGroup buttonGroupBaudRate = new ButtonGroup();
		final ButtonGroup buttonGroupParity = new ButtonGroup();
		final ButtonGroup buttonGroupStopBits = new ButtonGroup();
		final ButtonGroup buttonGroupDataBits = new ButtonGroup();
		
		/*
		 * baud rate settings
		 */
		JMenu mnBaudRate = new JMenu("Baud Rate");
		menu.add(mnBaudRate);
		
		JRadioButtonMenuItem menuItemBR19200 = new JRadioButtonMenuItem("19200", true);
		menuItemBR19200.setName("BaudRate");
		menuItemBR19200.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupBaudRate.add(menuItemBR19200);
		mnBaudRate.add(menuItemBR19200);
		
		JRadioButtonMenuItem menuItemBR9600 = new JRadioButtonMenuItem("9600");
		menuItemBR9600.setName("BaudRate");
		menuItemBR9600.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupBaudRate.add(menuItemBR9600);
		mnBaudRate.add(menuItemBR9600);

		/*
		 * Parity settings
		 */
		JMenu mnParity = new JMenu("Parity");
		menu.add(mnParity);

		JRadioButtonMenuItem menuItemPT2 = new JRadioButtonMenuItem("EVEN");
		menuItemPT2.setName("Parity");		
		menuItemPT2.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupParity.add(menuItemPT2);
		mnParity.add(menuItemPT2);
						
		JRadioButtonMenuItem menuItemPT1 = new JRadioButtonMenuItem("ODD");
		menuItemPT1.addItemListener(buttonChangeListenerPortConfig);
		menuItemPT1.setName("Parity");
		buttonGroupParity.add(menuItemPT1);
		mnParity.add(menuItemPT1);

		JRadioButtonMenuItem menuItemPT0 = new JRadioButtonMenuItem("NONE", true);
		menuItemPT0.addItemListener(buttonChangeListenerPortConfig);
		menuItemPT0.setName("Parity");
		buttonGroupParity.add(menuItemPT0);
		mnParity.add(menuItemPT0);

		/*
		 * stop bits settings
		 */
		JMenu mnStopBits = new JMenu("Stop Bits");
		menu.add(mnStopBits);
		
		JRadioButtonMenuItem menuItemST2 = new JRadioButtonMenuItem("2");
		menuItemST2.setName("StopBits");
		menuItemST2.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupStopBits.add(menuItemST2);
		mnStopBits.add(menuItemST2);
		
		//Stop bit value 1.5 not supported by RXTXComm.jar
		/*JRadioButtonMenuItem menuItemST15 = new JRadioButtonMenuItem("1.5");
		menuItemST15.setName("StopBits");
		menuItemST15.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupStopBits.add(menuItemST15);
		mnStopBits.add(menuItemST15);
		*/
		
		JRadioButtonMenuItem menuItemST1 = new JRadioButtonMenuItem("1", true);
		menuItemST2.setName("StopBits");
		menuItemST1.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupStopBits.add(menuItemST1);
		mnStopBits.add(menuItemST1);
		
		JRadioButtonMenuItem menuItemST0 = new JRadioButtonMenuItem("0");
		menuItemST2.setName("StopBits");
		menuItemST0.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupStopBits.add(menuItemST0);
		mnStopBits.add(menuItemST0);
		
		/*
		 * data bits settings 
		 */
		JMenu mnDataBits = new JMenu("Data Bits");
		menu.add(mnDataBits);
		
		JRadioButtonMenuItem menuItemDB8 = new JRadioButtonMenuItem("8", true);
		menuItemDB8.setName("DataBits");
		menuItemDB8.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupDataBits.add(menuItemDB8);
		mnDataBits.add(menuItemDB8);
		
		JRadioButtonMenuItem menuItemDB7 = new JRadioButtonMenuItem("7");
		menuItemDB7.setName("DataBits");
		menuItemDB7.addItemListener(buttonChangeListenerPortConfig);
		buttonGroupDataBits.add(menuItemDB7);
		mnDataBits.add(menuItemDB7);
	}

	
	private ItemListener buttonChangeListenerPortConfig = new ItemListener() {
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Object[] args = new Object[2];
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) arg0.getSource();
			
			if(item.isSelected()){
				args[0]=item.getName();
				args[1] = item.getText();
				
				comPort.open(args);
				
				System.out.print("Set " + item.getName() + " to " + item.getText() + "\r");
				
			}

		}
	};
}
