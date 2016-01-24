package system.manager.engine;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

public class ForcedListSelectionModel extends DefaultListSelectionModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ForcedListSelectionModel () {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void clearSelection() {
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
    }

}