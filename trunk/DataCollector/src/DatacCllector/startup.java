package DatacCllector;

import gnu.io.CommPortIdentifier;
import gnu.io.RXTXPort;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import org.apache.log4j.*;

public class startup {

	private JFrame frame;
	private Logger logger = Logger.getRootLogger();
	static Level logLevel = Level.DEBUG;
    // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					startup window = new startup();
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
	public startup() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("rawtypes")
	private void initialize() {

	        
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
        try {
            SimpleLayout layout = new SimpleLayout();
            ConsoleAppender consoleAppender = new ConsoleAppender(layout);
            logger.setLevel(logLevel);
            logger.addAppender(consoleAppender);
	    } catch (Exception ex) {
	            logger.debug(ex + "@" + JUtil.getMethodName(1));
	    }
        
		
		final JComboBox<String> comboBoxPort = new JComboBox<String>();
		comboBoxPort.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
		
			}
		});	
		comboBoxPort.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub
				comboBoxPort.removeAllItems();
				ArrayList<String> portList = new ArrayList<String>();
				portList = ComPort.portList();
				for (String s : portList) {
					comboBoxPort.addItem(s);	
				}
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}

		});
		comboBoxPort.setBounds(10, 11, 108, 20);
		frame.getContentPane().add(comboBoxPort);
		
		
		
		final JComboBox<Integer> comboBoxBaud = new JComboBox<Integer>();
		for (Integer i : ComPort.getBaudRateArray()) {
			comboBoxBaud.addItem(i);	
			
		}
		comboBoxBaud.setBounds(10, 42, 108, 20);
		frame.getContentPane().add(comboBoxBaud);
		
		
		JComboBox<Enum> comboBoxParity = new JComboBox<Enum>();	
		comboBoxParity.addItem(ComPort.Parity.NONE);
		comboBoxParity.addItem(ComPort.Parity.ODD);
		comboBoxParity.addItem(ComPort.Parity.EVEN);
		comboBoxParity.addItem(ComPort.Parity.MARK);
		comboBoxParity.addItem(ComPort.Parity.SPACE);
		comboBoxParity.setBounds(10, 73, 108, 20);
		frame.getContentPane().add(comboBoxParity);
		
		JComboBox<Enum> comboBoxData = new JComboBox<Enum>();
		comboBoxData.addItem(ComPort.DataBits.FIVE);
		comboBoxData.addItem(ComPort.DataBits.SIX);
		comboBoxData.addItem(ComPort.DataBits.SEVEN);
		comboBoxData.addItem(ComPort.DataBits.EIGHT);
		comboBoxData.setBounds(10, 104, 108, 20);
		frame.getContentPane().add(comboBoxData);
		
		JComboBox<Enum> comboBoxStop = new JComboBox<Enum>();
		comboBoxStop.addItem(ComPort.StopBits.ONE);
		comboBoxStop.addItem(ComPort.StopBits.ONEPOINTFIVE);
		comboBoxStop.addItem(ComPort.StopBits.TWO);	
		comboBoxStop.setBounds(10, 135, 108, 20);
		frame.getContentPane().add(comboBoxStop);
		
		JButton btnConnect = new JButton("connect");
		btnConnect.setBounds(128, 10, 89, 23);
		frame.getContentPane().add(btnConnect);
	}
}
