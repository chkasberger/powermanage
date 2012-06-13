import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;

public class swtFrame
{
	private static Text	text;
	static ComPort		sW	= new ComPort();

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
		text.setEnabled(false);
		text.setTouchEnabled(true);
		text.setBounds(131, 29, 213, 212);
		text.setText("foo bar");

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{

				String[] ports =
				{ "COM5" };
				ComPort.open(ports);
				ComPort.listPorts();

				ComPort.write("uhh yeah\n\r");
				ComPort.write(ComPort.listPorts());

			}
		});
		
		btnNewButton.setBounds(349, 27, 75, 25);
		btnNewButton.setText("New Button");
		
		ComboViewer comPortViewer = new ComboViewer(shell, SWT.NONE);
		Combo combo = comPortViewer.getCombo();
		combo.setBounds(20, 82, 91, 23);

		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}
}
