package DataCollector;

import gnu.io.PortInUseException;

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

import DataCollector.IO.ComPort;
import DataCollector.IO.ComPortEvent;
import DataCollector.IO.ComPortEventListener;

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
	 * @param string
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

		final JComboBox<Integer> comboBoxBaud = new JComboBox<Integer>();
		comboBoxBaud.setBounds(10, 42, 108, 20);
		for (Integer i : ComPort.getBaudRates()) {
			comboBoxBaud.addItem(i);

		}
		comboBoxBaud.setSelectedItem(300);
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

		final JComboBox<String> comboBoxPort = new JComboBox<String>();
		comboBoxPort.setBounds(10, 11, 108, 20);
		frame.getContentPane().add(comboBoxPort);
		comboBoxPort.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1)
				{
					try {
						Port.connect(comboBoxPort.getSelectedItem(),
								comboBoxBaud.getSelectedItem(),
								comboBoxParity.getSelectedItem(),
								comboBoxData.getSelectedItem(),
								comboBoxStop.getSelectedItem());
					} catch (PortInUseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});

		comboBoxPort.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				ArrayList<String> portList = new ArrayList<String>();
				portList = ComPort.portList();

				if(comboBoxPort.getItemCount()-1 != portList.size()){
					comboBoxPort.removeAllItems();
					comboBoxPort.addItem(null);
					for (String s : portList) {
						comboBoxPort.addItem(s);
					}
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

		ItemListener itemListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ie) {
				if(ie.getStateChange() == 1)
				{
					Port.portSettings(comboBoxPort.getSelectedItem(),
							comboBoxBaud.getSelectedItem(),
							comboBoxParity.getSelectedItem(),
							comboBoxData.getSelectedItem(),
							comboBoxStop.getSelectedItem());
				}

			}

		};

		comboBoxBaud.addItemListener(itemListener);
		comboBoxParity.addItemListener(itemListener);
		comboBoxData.addItemListener(itemListener);
		comboBoxStop.addItemListener(itemListener);

		final JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setRows(10);
		textArea.setBounds(145, 68, 502, 211);

		frame.getContentPane().add(textArea);

		JButton btnRequest = new JButton("request");
		btnRequest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					comboBoxBaud.setSelectedIndex(0);

					Port.out.write(new byte[] {0x2f, 0x3f, 0x21, 0x0d, 0x0a});
					Port.out.flush();
					//Port.serialWriter.out.write(new byte[] {0x2f, 0x3f, 0x21, 0x0d, 0x0a});

					//Port.out.write(new Message().ACKNOWLEDGE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// = "fooBar";
			}
		});
		btnRequest.setBounds(128, 41, 89, 23);
		frame.getContentPane().add(btnRequest);

		JButton btnSwitch = new JButton("switch");
		btnSwitch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Port.out.write(new byte[] {0x06, 0x30, 0x33, 0x30, 0x0d, 0x0a});
					//Port.out.write(new Message().ACKNOWLEDGE(null));
					Port.out.flush();
					//Port.serialWriter.out.write(new byte[] {0x06, 0x30, 0x33, 0x30, 0x0d, 0x0a});

					comboBoxBaud.setSelectedIndex(1);


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		btnSwitch.setBounds(227, 41, 89, 23);
		frame.getContentPane().add(btnSwitch);

		Port.addComPortEventListener(new ComPortEventListener() {

			@Override
			public void comPortEventFired(ComPortEvent evt) {
				// TODO Auto-generated method stub
				String str = evt.getSource().toString();
				textArea.append(str + "\r");


				//Port.serialWriter.out.write(new byte[] {0x06, 0x30, 0x33, 0x30, 0x0d, 0x0a});

				//comboBoxBaud.setSelectedIndex(1);

				/*try {
					//Thread.currentThread();
					Thread.sleep(500);
					Port.out.write(new byte[] {0x06, 0x30, 0x33, 0x30, 0x0d, 0x0a});

					comboBoxBaud.setSelectedIndex(1);
					Port.portSettings(comboBoxPort.getSelectedItem(),
							comboBoxBaud.getSelectedItem(),
							comboBoxParity.getSelectedItem(),
							comboBoxData.getSelectedItem(),
							comboBoxStop.getSelectedItem());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 */
			}
		});

		comboBoxParity.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getItem().equals(ComPort.Parity.ODD))
					textArea.setText(ComPort.Parity.ODD.toString());
			}
		});
		comboBoxParity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

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
