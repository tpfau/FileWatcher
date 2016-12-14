package org.cytoscape.FileWatcher.internal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;

public class FileWatcherSettingsGUI  extends JPanel{

	JComboBox<String> TableSelection = new JComboBox<>(); 
	JComboBox<String> ColumnSelection = new JComboBox<>();
	HashMap<String,CyTable> tables = new HashMap<>();
	File selectedFile;
	JTextField fileLocation;
	CyTable selectedTable; 
	String selectedColumn;
	FileUtil util;
	public FileWatcherSettingsGUI(CyNetwork network, FileUtil util, CySwingApplication source) {
		// TODO Auto-generated constructor stub
		super();		
		tables.put("Node table", network.getDefaultNodeTable());
		tables.put("Edge table", network.getDefaultEdgeTable());
		tables.put("Network table", network.getDefaultNetworkTable());
		Vector<String> options = new Vector<>();
		options.addAll(tables.keySet());
		TableSelection.setModel(new DefaultComboBoxModel<>(options));		
		ColumnChoiceUpdater ccu = new ColumnChoiceUpdater(tables, ColumnSelection);
		TableSelection.addItemListener(ccu);
		TableSelection.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					selectedTable = tables.get(e.getItem());
				}
			}
		});
		ColumnSelection.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					selectedColumn = e.getItem().toString();
				}
				
			}
		});
		TableSelection.setSelectedIndex(1);
		TableSelection.setSelectedIndex(0);
		fileLocation = new JTextField();
		fileLocation.setEditable(true);				
		JButton but = new JButton("Choose File to Listen To");
		but.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectedFile = util.getFile(source.getJFrame(), "Select file to listen to", FileUtil.LOAD,Collections.singletonList(new FileChooserFilter("DataSet Files",new String[]{""})));					
				fileLocation.setText(selectedFile.getPath());
			}
		});
		fileLocation.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//ignore
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//ignore
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				File f = new File(fileLocation.getText());
				if(f.exists())
				{
					selectedFile = f;
				}				
			}
		});
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(new JLabel("Select a Table to map the data to"), gbc);
		gbc.gridx++;
		this.add(TableSelection, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		this.add(new JLabel("Select Column to match items"), gbc);
		gbc.gridx++;
		this.add(ColumnSelection, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		this.add(but, gbc);
		gbc.gridx++;
		this.add(fileLocation, gbc);
		
	}
	
	
	private class ColumnChoiceUpdater implements ItemListener
	{		
		HashMap<String,CyTable> tables;
		JComboBox<String> colSelection;		
		public ColumnChoiceUpdater(HashMap<String,CyTable> tables, JComboBox<String> ColumnSelection) {
			// TODO Auto-generated constructor stub
			this.tables = tables;
			colSelection = ColumnSelection;
		}
	

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				CyTable ctable = tables.get(e.getItem());
				Vector<String> colNames = new Vector<>();
				for(CyColumn col : ctable.getColumns())
					{
					colNames.add(col.getName());
					};
					colSelection.setModel(new DefaultComboBoxModel<>(colNames));
					colSelection.setSelectedIndex(0);
			}
		}
	}
	
	public FileWatcherSettings getSettings()
	{
		FileWatcherSettings fws = new FileWatcherSettings();
		fws.f = selectedFile;
		fws.ColumnName = selectedColumn;
		fws.targetTable = selectedTable;
		return fws;
		
	}
}
