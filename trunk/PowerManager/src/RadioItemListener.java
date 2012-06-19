import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

public class RadioItemListener extends SelectionAdapter {
	public void widgetSelected(SelectionEvent event) {
		MenuItem item = (MenuItem) event.widget;
		if (item.getSelection()) {
			System.out.print(item.getText() + " is on.");
		}
	}
}