package DataCollector;

import javax.swing.JTextArea;

public class Display implements Runnable{

	private JTextArea textArea;

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public Display()
	{

	}

	public void DisplayOutgoing(String msg)
	{
		/*
        _displayWindow.Invoke(new EventHandler(delegate
        {

            _displayWindow.SelectionFont = new Font(_displayWindow.SelectionFont, FontStyle.Bold);
            _displayWindow.SelectionColor = Color.Black;
            _displayWindow.AppendText("\n" + msg);
            _displayWindow.ScrollToCaret();
        }
             ));
		 */
	}

	public void DisplayIncoming(String msg)
	{

		/*
        _displayWindow.Invoke(new EventHandler(delegate
        {

            _displayWindow.SelectionFont = new Font(_displayWindow.SelectionFont, FontStyle.Bold);
            _displayWindow.SelectionColor = Color.Green;
            _displayWindow.AppendText("\n" + msg);
            _displayWindow.ScrollToCaret();
        }
             ));
		 */
	}

	public void DisplayERRORMSG(String msg)
	{

		/*
        _displayWindow.Invoke(new EventHandler(delegate
        {

            _displayWindow.SelectionFont = new Font(_displayWindow.SelectionFont, FontStyle.Bold);
            _displayWindow.SelectionColor = Color.Red;
            _displayWindow.AppendText("\n" + msg);
            _displayWindow.ScrollToCaret();
        }
             ));
		 */
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}


}
