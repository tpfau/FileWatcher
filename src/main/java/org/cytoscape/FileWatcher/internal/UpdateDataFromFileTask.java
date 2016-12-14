package org.cytoscape.FileWatcher.internal;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**

 */
public class UpdateDataFromFileTask extends AbstractTask
{


	FileWatcherSettings settings;
	UpdateDataFromFileTask(FileWatcherSettings settings)
	{			
		this.settings = settings;
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		//Read the file. check whether its a tsv, csv or xls/xlsx
		Workbook wb = null;
		try{
			wb = WorkbookFactory.create(settings.f);			
		}
		catch(Exception e)
		{
			//try as a csv
		}
		if(wb != null)
		{
			readxls(wb);
		}
		else
		{
			readcsv(settings.f);
		}

	}

	private void readcsv(File f) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(f));
		String currentline = br.readLine();
		Vector<String> headers = new Vector<String>();
		int matchcolindex = 0;
		Class matchClass = settings.targetTable.getColumn(settings.ColumnName).getType();
		String[] headerline = currentline.split("\t");
		for(int i = 0; i < headerline.length; i++)
		{
			if(headerline[i].equals(settings.ColumnName))
			{
				matchcolindex = i;
			}
			headers.add(headerline[i]);
		}
		currentline = br.readLine();
		while(currentline != null)
		{
			String[] currentdata = currentline.split("\t");

			Object target = getMatchingObject(currentdata[matchcolindex], matchClass);				
			Collection<CyRow> matchingrows = settings.targetTable.getMatchingRows(settings.ColumnName, target);
			for(int cellindex = 0; cellindex < currentdata.length; cellindex++)
			{
				if(cellindex == matchcolindex || headers.get(cellindex).equals("")){
					continue;
				}									
				if(settings.targetTable.getColumn(headers.get(cellindex)) == null)
				{
					settings.targetTable.createColumn(headers.get(cellindex), getValueClass(currentdata[cellindex]), false);
				}
				CyColumn currentCol = settings.targetTable.getColumn(headers.get(cellindex));
				for(CyRow currentCyRow : matchingrows)
				{
					currentCyRow.set(headers.get(cellindex), getMatchingObject(currentdata[cellindex], currentCol.getType()));
				}
			}

			currentline = br.readLine();
		}

		br.close();
	}

	private void readxls(Workbook wb)
	{
		//Read the header lineO
		Sheet sheet = wb.getSheetAt(0);
		Row HeaderRow = sheet.getRow(0);
		int matchcolindex = 0; 
		Vector<String> headers = new Vector<>();
		for(int i = 0; i < HeaderRow.getLastCellNum(); i++)
		{
			Cell current = HeaderRow.getCell(i, Row.RETURN_BLANK_AS_NULL);
			if(current == null)
			{
				headers.add(null);
			}
			else
			{
				if(current.getCellType() == Cell.CELL_TYPE_STRING)
				{
					headers.add(current.getStringCellValue());
				}
				else if(current.getCellType() == Cell.CELL_TYPE_NUMERIC)
				{
					headers.add(Double.toString(current.getNumericCellValue()));
				}
				if(headers.lastElement().equals(settings.ColumnName))
				{
					matchcolindex = headers.size()-1;
				}
				//ignore anything else
			}
		}
		CyColumn matchcol = settings.targetTable.getColumn(settings.ColumnName);

		Class targetclass = matchcol.getType();			
		for(int i = 1; i <= sheet.getLastRowNum(); i++)
		{
			Row currentRow = sheet.getRow(i);
			Object target = getMatchingObject(currentRow.getCell(matchcolindex), targetclass);

			Collection<CyRow> matchingrows = settings.targetTable.getMatchingRows(settings.ColumnName, target);
			for(int cellindex = 0; cellindex < currentRow.getLastCellNum(); cellindex++)
			{
				if(cellindex == matchcolindex || headers.get(cellindex) == null){
					continue;
				}
				Cell currentCell = currentRow.getCell(cellindex);					
				if(settings.targetTable.getColumn(headers.get(cellindex)) == null)
				{
					settings.targetTable.createColumn(headers.get(cellindex), getCellClass(currentCell), false);
				}
				CyColumn currentCol = settings.targetTable.getColumn(headers.get(cellindex));
				for(CyRow currentCyRow : matchingrows)
				{
					currentCyRow.set(headers.get(cellindex), getMatchingObject(currentCell, currentCol.getType()));
				}
			}
		}

	}


	private Class getCellClass(Cell currentCell)
	{
		if(currentCell.getCellType() == Cell.CELL_TYPE_FORMULA || currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
		{
			return Double.class;
		}
		else if(currentCell.getCellType() == Cell.CELL_TYPE_STRING)
		{
			return String.class;
		}
		else if(currentCell.getCellType() == Cell.CELL_TYPE_BOOLEAN)
		{
			return Boolean.class;
		}
		else
		{
			return null;
		}		
	}

	private boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?([eE]-?\\d+)?");  //match a number with optional '-' and decimal.
	}

	private Class getValueClass(String Value)
	{

		if(isNumeric(Value))
		{
			return Double.class;
		}
		else 
		{
			return String.class;
		}
	}

	private Object getMatchingObject(String currentValue, Class targetclass)
	{
		Object target = null;
		if(targetclass == Double.class || targetclass == Long.class)
		{
			Double value = Double.parseDouble(currentValue);
			target = value;
			if(targetclass == Long.class)
			{
				target = Math.round(value);
			}
		}
		else if(targetclass == Float.class || targetclass == Integer.class)
		{
			Float value = Float.parseFloat(currentValue);
			target = value;
			if(targetclass == Integer.class)
			{
				target = Math.round(value);
			}
		}
		else if(targetclass == Boolean.class )
		{			
			Boolean value = Boolean.parseBoolean(currentValue);
			target = value;
		}
		else if(targetclass == String.class)
		{
			target = currentValue;
		}
		return target;
	}

	private Object getMatchingObject(Cell currentCell, Class targetclass)
	{
		Object target = null;
		if(targetclass == Double.class || targetclass == Long.class)
		{
			Double value = currentCell.getNumericCellValue();
			target = value;
			if(targetclass == Long.class)
			{
				target = Math.round(value);
			}
		}
		else if(targetclass == Float.class || targetclass == Integer.class)
		{
			Float value = (float)currentCell.getNumericCellValue();
			target = value;
			if(targetclass == Integer.class)
			{
				target = Math.round(value);
			}
		}
		else if(targetclass == Boolean.class )
		{			
			Boolean value = currentCell.getBooleanCellValue();
			target = value;
		}
		else if(targetclass == String.class)
		{
			target = currentCell.getStringCellValue();
		}
		return target;
	}
}


