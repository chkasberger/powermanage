import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.xml.bind.Marshaller.Listener;

//import org.eclipse.swt.widgets.MenuItem;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;


public class mainFrame {

	private JFrame frame;

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
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		createMenu();

	}
	
	final ButtonGroup buttonGroupBaudRate = new ButtonGroup();
	final ButtonGroup buttonGroupParity = new ButtonGroup();
	final ButtonGroup buttonGroupStopBits = new ButtonGroup();
	final ButtonGroup buttonGroupDataBits = new ButtonGroup();

	private void createMenu(){
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnConfig = new JMenu("Config");
		menuBar.add(mnConfig);
		
		JMenu menu = new JMenu("Port Config");
		mnConfig.add(menu);
		
		/*
		 * baud rate settings
		 */
		JMenu mnBaudRate = new JMenu("Baud Rate");
		menu.add(mnBaudRate);
		
		JRadioButtonMenuItem menuItemBR19200 = new JRadioButtonMenuItem("19200", true);
		menuItemBR19200.setName("BaudRate");
		menuItemBR19200.addItemListener(buttonChangeListener);
		buttonGroupBaudRate.add(menuItemBR19200);
		mnBaudRate.add(menuItemBR19200);
		
		JRadioButtonMenuItem menuItemBR9600 = new JRadioButtonMenuItem("9600");
		menuItemBR9600.setName("BaudRate");
		menuItemBR9600.addItemListener(buttonChangeListener);
		buttonGroupBaudRate.add(menuItemBR9600);
		mnBaudRate.add(menuItemBR9600);

		/*
		 * Parity settings
		 */
		JMenu mnParity = new JMenu("Parity");
		menu.add(mnParity);

		JRadioButtonMenuItem menuItemPT2 = new JRadioButtonMenuItem("EVEN");
		menuItemPT2.setName("Parity");		
		menuItemPT2.addItemListener(buttonChangeListener);
		buttonGroupParity.add(menuItemPT2);
		mnParity.add(menuItemPT2);
						
		JRadioButtonMenuItem menuItemPT1 = new JRadioButtonMenuItem("ODD");
		menuItemPT1.addItemListener(buttonChangeListener);
		menuItemPT1.setName("Parity");
		buttonGroupParity.add(menuItemPT1);
		mnParity.add(menuItemPT1);

		JRadioButtonMenuItem menuItemPT0 = new JRadioButtonMenuItem("NONE", true);
		menuItemPT0.addItemListener(buttonChangeListener);
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
		menuItemST2.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST2);
		mnStopBits.add(menuItemST2);
		
		JRadioButtonMenuItem menuItemST15 = new JRadioButtonMenuItem("1.5");
		menuItemST15.setName("StopBits");
		menuItemST15.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST15);
		mnStopBits.add(menuItemST15);
		
		JRadioButtonMenuItem menuItemST1 = new JRadioButtonMenuItem("1", true);
		menuItemST2.setName("StopBits");
		menuItemST1.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST1);
		mnStopBits.add(menuItemST1);
		
		JRadioButtonMenuItem menuItemST0 = new JRadioButtonMenuItem("0");
		menuItemST2.setName("StopBits");
		menuItemST0.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST0);
		mnStopBits.add(menuItemST0);
		
		/*
		 * data bits settings 
		 */
		JMenu mnDataBits = new JMenu("Data Bits");
		menu.add(mnDataBits);
		
		JRadioButtonMenuItem menuItemDB8 = new JRadioButtonMenuItem("8", true);
		menuItemDB8.setName("DataBits");
		menuItemDB8.addItemListener(buttonChangeListener);
		buttonGroupDataBits.add(menuItemDB8);
		mnDataBits.add(menuItemDB8);
		
		JRadioButtonMenuItem menuItemDB7 = new JRadioButtonMenuItem("7");
		menuItemDB7.setName("DataBits");
		menuItemDB7.addItemListener(buttonChangeListener);
		buttonGroupDataBits.add(menuItemDB7);
		mnDataBits.add(menuItemDB7);
	}
	
	ItemListener buttonChangeListener = new ItemListener() {
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			// TODO Auto-generated method stub
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) arg0.getSource();//
			if(item.isSelected()){
				
				System.out.print(item.getName() + "\n\r");
				System.out.print(item.getText() + "\n\r");
				
			}

		}
	};
}
