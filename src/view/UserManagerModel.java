package view;

import javax.swing.table.AbstractTableModel;

import controller.UserManager;

/**
 * Model for the UserManagerGUI table
 * @author bhobbs
 *
 */
public class UserManagerModel extends AbstractTableModel {

	private String[] columnNames = { "ID", "Name", "Table", "Chat" };
	
	//all the data to be displayed in the table
	private Object[][] data = UserManager.instance().getUserManagerData();

	/**
	 * Gets the number of columns
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Get the number of rows
	 */
	@Override
	public int getRowCount() {
		return data.length;
	}

	/**
	 * Get the column name
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Get the column class
	 */
	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/**
	 * Get the value of a cell
	 */
	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/**
	 * Return if the cell is editable
	 */
    public boolean isCellEditable(int row, int col) {
    	//only the chat checkboxes are editable
        if (col < 3) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Set the value of a cell
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
        if(col == 3)
        	UserManager.instance().changePrivateChatScope(Integer.parseInt(getValueAt(row, 0).toString()), Boolean.parseBoolean(value.toString()));
    }
    
    /**
     * Update the data of the table
     */
	public void updateData() {
		data = UserManager.instance().getUserManagerData();
		this.fireTableDataChanged();
	}

}
