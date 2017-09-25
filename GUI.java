import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * GUI for Module 3 DirectoryLister assignment
 * 
 * Simple interface that allows user to select a directory using a file chooser
 * dialog, and display all the files and folders in the selected directory
 */
public class GUI extends JFrame
{
	
	// -----------------------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------------------

	/** Event handler for button click events. */
	private ButtonHandler buttonHandler;
	
	/** Container for displaying Browse button and address of selected directory */
	private JPanel addressPanel;
	
	/** Text container for displaying address of selected directory */
	private JLabel addressLabel;
	
	/** Button for bringing up file chooser dialog */
	private JButton browseButton;
	
	/** Underlying model for the JTable that displays contents of selected directory */ 
	private DefaultTableModel tableModel;
	
	/** Table that displays contents of selected directory */
	private JTable directoryContentsTable;
	
	/** Allows filesTable to be scrollable */
	private JScrollPane tablePane;
	
	/** Object containing non-GUI logic for program */
	private DirectoryLister model;

	
	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------
	
	/**
	 *	Create a new GUI.
	 */
	public GUI()
	{
		// use small default size for low-res screens
		setSize(800, 600);
		
		// set value for title bar of window
		setTitle("Directory Lister");
		
		// allows the program to exit when the window is closed
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
		// create window components
		initGUI();

		// make sure everything is visible 
		validate();
		repaint();
		setVisible(true);
	}
	
	
	// -----------------------------------------------------------------------
	// Methods
	// -----------------------------------------------------------------------
	
	
	/**
	 *	Create all the components to be displayed in the main window.
	 */
	private void initGUI()
	{
		// use standard BorderLayout for the window itself
		setLayout(new BorderLayout());
		
		// event handler for button clicks
		buttonHandler = new ButtonHandler();
		
		// create text label for displaying selected directory
		addressLabel = new JLabel();
		
		// create Browse button
		browseButton = new JButton("Browse...");
		browseButton.addActionListener(buttonHandler);

		// create panel for showing Browse button and value for selected directory
		addressPanel = new JPanel();
		
		// ensure components are laid out from left to right
		addressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// add components to addressPanel
		addressPanel.add(browseButton);
		addressPanel.add(addressLabel);
		
		// create the table for displaying the directory contents
		createDirectoryContentsTable();
		
		// make sure table is scrollable
		tablePane = new JScrollPane(directoryContentsTable);
		
		// add components to main window
		add(addressPanel, BorderLayout.NORTH);
		add(tablePane, BorderLayout.CENTER);
	}
	
	
	/**
	 *	Create the table for displaying the contents of the selected directory.
	 */
	private void createDirectoryContentsTable()
	{
		// create underlying model for table that displays contents of selected directory
		tableModel = new DefaultTableModel();
		
		// table model has 4 columns, to display: file/folder name, size (files only), type (file or folder), and date last modified 
		tableModel.addColumn("Name");
		tableModel.addColumn("Size");
		tableModel.addColumn("Type");
		tableModel.addColumn("Date Modified");
		
		// create GUI table component
		directoryContentsTable = new JTable(tableModel);

		// disallow reordering of table columns
		directoryContentsTable.getTableHeader().setReorderingAllowed(false);
		
		// create a TableCellRenderer for displaying left justified text
		DefaultTableCellRenderer leftJustifiedRenderer = new DefaultTableCellRenderer();
		leftJustifiedRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		
		// create a TableCellRenderer for displaying right justified text
		DefaultTableCellRenderer rightJustifiedRenderer = new DefaultTableCellRenderer();
		rightJustifiedRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		
		// set cell renderers for data cells
		directoryContentsTable.getColumn("Name").setCellRenderer(leftJustifiedRenderer);
		directoryContentsTable.getColumn("Size").setCellRenderer(rightJustifiedRenderer);
		directoryContentsTable.getColumn("Type").setCellRenderer(leftJustifiedRenderer);
		directoryContentsTable.getColumn("Date Modified").setCellRenderer(leftJustifiedRenderer);
		
		// create and format headers for column that displays file/folder names
		JLabel nameLabel = new JLabel(" Name", SwingConstants.LEFT);
		nameLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		
		directoryContentsTable.getColumn("Name").setHeaderRenderer(new CustomTableCellRenderer());
		directoryContentsTable.getColumn("Name").setHeaderValue(nameLabel);
		
		// create and format header for column that displays file/folder sizes
		JLabel sizeLabel = new JLabel("Size ", SwingConstants.RIGHT);
		sizeLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		
		directoryContentsTable.getColumn("Size").setHeaderRenderer(new CustomTableCellRenderer());
		directoryContentsTable.getColumn("Size").setHeaderValue(sizeLabel);
		
		// create and format header for column that displays file/folder types
		JLabel typeLabel = new JLabel(" Type", SwingConstants.LEFT);
		typeLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		
		directoryContentsTable.getColumn("Type").setHeaderRenderer(new CustomTableCellRenderer());
		directoryContentsTable.getColumn("Type").setHeaderValue(typeLabel);
		
		// create and format header for column that displays dates last modified for files/folders
		JLabel dateModifiedLabel = new JLabel(" Date Modified", SwingConstants.LEFT);
		dateModifiedLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		
		directoryContentsTable.getColumn("Date Modified").setHeaderRenderer(new CustomTableCellRenderer());
		directoryContentsTable.getColumn("Date Modified").setHeaderValue(dateModifiedLabel);
	}
	
	
	/**
	 *	Register the specified model with this GUI.
	 */
	public void registerModel(DirectoryLister model)
	{
		this.model = model;
	}
	
	
	/**
	 *	Return the absolute path of a directory selected by the user via a JFileChooser.
	 */
	public String getAbsoluteDirectoryPath()
	{
		// display file chooser dialog
		JFileChooser jfc = new JFileChooser();
		
		// only display directories (folders)
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		// show the dialog
		int result = jfc.showOpenDialog(this);
		
		// return the selected directory, if one is chosen; otherwise, return null
		if (jfc.getSelectedFile() != null && result != 1)
		{
			return jfc.getSelectedFile().getAbsolutePath();
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 *	Set the text of the address label.
	 */
	public void setAddressLabelText(String text)
	{
		addressLabel.setText(text);
	}
	
	
	/**
	 *	Update the table with the specified file/folder information.
	 *	The information for each file/folder occupies a single row in the table.
	 *
	 *	Information is displayed in the following order:
	 *
	 *		Column 1: absolute path of file or folder
	 *		Column 2: size of file in kilobytes, or "" for folders
	 *		Column 3: type - "File" or "Folder"
	 *		Column 4: date last modified in format: month/day/year hour:minute:second AM/PM
	 */
	public void updateListing(String absolutePath, String size, String type, String dateLastModified)
	{
		// add information in new row in table
		tableModel.addRow(new String[] {" " + absolutePath,
										size + " ",
										" " + type,
										" " + dateLastModified});
	}
	
	
	/**
	 *	Clear the contents of the previous directory traversal, and clear the address showing the current directory.
	 */
	public void resetGUI()
	{
		// clear address
		addressLabel.setText("");
		
		// remove all rows from table
		while (tableModel.getRowCount() > 0)
		{
			tableModel.removeRow(0);
		}
	}
	
	
	// -----------------------------------------------------------------------
	// Inner Classes
	// -----------------------------------------------------------------------
	
	/**
	 *	Inner class for allowing customization of the appearance of a table cell.
	 *	This class is used in this class to allow left or right justification of text in a
	 *	table header cell, without losing the borders of those cells.
	 */
	class CustomTableCellRenderer implements TableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
													   boolean hasFocus, int row, int column)
		{
			return (JComponent)value;
		}
	}
	
	
	/**
	 *	Inner class to handle button events.
	 */
	class ButtonHandler implements ActionListener
	{
		
		// -----------------------------------------------------------------------
		// Methods
		// -----------------------------------------------------------------------
		
		/**
		 *	Respond to a button click event. 
		 */
		public void actionPerformed(ActionEvent e)
		{
			JButton b = (JButton)e.getSource();
			
			if (b.getText().equals("Browse..."))
			{
				// prompt user to select directory
				model.selectDirectory();
			}
		}
	}

}