package DatacCollector;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class startup {

	private JFrame frame;
	private final Logger logger = Logger.getRootLogger();
	static Level logLevel = Level.DEBUG;
	// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
	private final ComPort Port = new ComPort();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
		setDefaults();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("rawtypes")
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 694, 342);
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
		comboBoxBaud.setBounds(10, 42, 108, 20);
		for (Integer i : ComPort.getBaudRateArray()) {
			comboBoxBaud.addItem(i);

		}
		comboBoxBaud.setSelectedItem(9600);
		frame.getContentPane().add(comboBoxBaud);

		final JComboBox<Enum> comboBoxParity = new JComboBox<Enum>();
		for (Enum e : ComPort.Parity.values()) {
			comboBoxParity.addItem(e);
		}
		comboBoxParity.setBounds(10, 73, 108, 20);
		comboBoxParity.setSelectedItem(ComPort.Parity.EVEN);
		frame.getContentPane().add(comboBoxParity);

		final JComboBox<Enum> comboBoxData = new JComboBox<Enum>();
		for (Enum e : ComPort.DataBits.values()) {
			comboBoxData.addItem(e);
		}
		comboBoxData.setSelectedItem(ComPort.DataBits.SEVEN);
		comboBoxData.setBounds(10, 104, 108, 20);
		frame.getContentPane().add(comboBoxData);

		final JComboBox<Enum> comboBoxStop = new JComboBox<Enum>();
		for (Enum e : ComPort.StopBits.values()) {
			comboBoxStop.addItem(e);
		}
		comboBoxStop.setSelectedItem(ComPort.StopBits.ONE);
		comboBoxStop.setBounds(10, 135, 108, 20);
		frame.getContentPane().add(comboBoxStop);

		JButton btnConnect = new JButton("connect");
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Port.connect(comboBoxPort.getSelectedItem(),
						comboBoxBaud.getSelectedItem(),
						(ComPort.Parity)comboBoxParity.getSelectedItem(),
						(ComPort.DataBits)comboBoxData.getSelectedItem(),
						(ComPort.StopBits)comboBoxStop.getSelectedItem());


			}
		});

		btnConnect.setBounds(128, 10, 89, 23);
		frame.getContentPane().add(btnConnect);

		final JTextArea txtrFoo = new JTextArea();
		txtrFoo.setWrapStyleWord(true);
		txtrFoo.setText("foo");
		txtrFoo.setRows(10);
		txtrFoo.setBounds(145, 68, 502, 211);
		frame.getContentPane().add(txtrFoo);

		JButton btnRequest = new JButton("request");
		btnRequest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					Port.out.write(new byte[] {(byte)0x2f, (byte)0x3f, (byte)0x21, (byte)0x0d, (byte)0x0a});


					/*
	Port.out.write(new byte[] {(byte)0x06, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x0d, (byte)0x0a});

	ACK	06
	PSz	00
	BrI	03
	MSz	00
	cr	0d
	lf	0a
					 */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// = "fooBar";
			}
		});
		btnRequest.setBounds(128, 41, 89, 23);
		frame.getContentPane().add(btnRequest);



		comboBoxParity.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getItem().equals(ComPort.Parity.ODD))
					txtrFoo.setText(ComPort.Parity.ODD.toString());
			}
		});
		comboBoxParity.addActionListener(new ActionListener() {

			int x = 0;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				x++;
				//txtrFoo.setText(Integer.toString(x));
			}
		});
	}

	/**
	 * Set default values for components.
	 */
	private void setDefaults() {

		try
		{
			//(new TwoWaySerialComm()).connect("COM2");
		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
