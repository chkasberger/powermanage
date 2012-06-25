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
		 * 
		 */
		JMenu mnBaudRate = new JMenu("Baud Rate");
		menu.add(mnBaudRate);
		
		JRadioButtonMenuItem menuItemBR19200 = new JRadioButtonMenuItem("19200", true);
		menuItemBR19200.addItemListener(buttonChangeListener);
		buttonGroupBaudRate.add(menuItemBR19200);
		//radioButtonMenuItem.setSelected(true);
		mnBaudRate.add(menuItemBR19200);
		
		JRadioButtonMenuItem menuItemBR9600 = new JRadioButtonMenuItem("9600");
		menuItemBR9600.addItemListener(buttonChangeListener);
		buttonGroupBaudRate.add(menuItemBR9600);
		mnBaudRate.add(menuItemBR9600);
		
		/*
		 * 
		 */
		JMenu mnParity = new JMenu("Parity");
		menu.add(mnParity);

		JRadioButtonMenuItem menuItemPT2 = new JRadioButtonMenuItem("EVEN");
		menuItemPT2.addItemListener(buttonChangeListener);
		buttonGroupParity.add(menuItemPT2);
		mnParity.add(menuItemPT2);
						
		JRadioButtonMenuItem menuItemPT1 = new JRadioButtonMenuItem("ODD");
		menuItemPT1.addItemListener(buttonChangeListener);
		buttonGroupParity.add(menuItemPT1);
		mnParity.add(menuItemPT1);

		JRadioButtonMenuItem menuItemPT0 = new JRadioButtonMenuItem("NONE", true);
		menuItemPT0.addItemListener(buttonChangeListener);
		buttonGroupParity.add(menuItemPT0);
		mnParity.add(menuItemPT0);

		/*
		 * 
		 */
		JMenu mnStopBits = new JMenu("Stop Bits");
		menu.add(mnStopBits);
		
		JRadioButtonMenuItem menuItemST2 = new JRadioButtonMenuItem("2");
		menuItemST2.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST2);
		mnStopBits.add(menuItemST2);
		
		JRadioButtonMenuItem menuItemST15 = new JRadioButtonMenuItem("1.5");
		menuItemST15.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST15);
		//radioButtonMenuItem.setSelected(true);
		mnStopBits.add(menuItemST15);
		
		JRadioButtonMenuItem menuItemST1 = new JRadioButtonMenuItem("1", true);
		menuItemST1.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST1);
		mnStopBits.add(menuItemST1);
		
		JRadioButtonMenuItem menuItemST0 = new JRadioButtonMenuItem("0");
		menuItemST0.addItemListener(buttonChangeListener);
		buttonGroupStopBits.add(menuItemST0);
		//radioButtonMenuItem.setSelected(true);
		mnStopBits.add(menuItemST0);
		/*
		 * 
		 */
		JMenu mnDataBits = new JMenu("Data Bits");
		menu.add(mnDataBits);
		
		JRadioButtonMenuItem menuItemDB8 = new JRadioButtonMenuItem("8", true);
		menuItemDB8.addItemListener(buttonChangeListener);
		buttonGroupDataBits.add(menuItemDB8);
		//radioButtonMenuItem.setSelected(true);
		mnDataBits.add(menuItemDB8);
		
		JRadioButtonMenuItem menuItemDB7 = new JRadioButtonMenuItem("7");
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
				
				System.out.print(item.getText() + "\n\r");
				
				System.out.print(buttonGroupBaudRate.getElements() + "\r" +
				buttonGroupParity.getElements() + "\r" +
				buttonGroupStopBits.getElements() + "\r" +
				buttonGroupDataBits.getElements() 
				);
				
				
			}

		}
	};
}
