import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

public class SearchSpace {  

	public static String excelPath = "C:\\Users\\Avery Swank\\Documents\\TestSheet.xls";
	
	// Physician data columns
	public static int codeColumn = 1;
	public static int newCodeColumn = 2;
	public static int npiColumn = 5;
	public static int fNameColumn = 6;
	public static int lNameColumn = 8;
	public static int locationColumn = 13;
	
	public static boolean rowBreak = false;
	
	// Orange background attributes
	public static HSSFCellStyle style;

	public static void main(String[] args) throws Exception{
       	
       	// Locate the Excel document that is being processed and identify the sheet
       	POIFSFileSystem fs = new POIFSFileSystem(new File(excelPath));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
           
        // Set the background color or physicians that are matched
        style = wb.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
          
        // Loop through every row in the list, determine physician rows, search the web
        // for where they are currently practicing, and record that within the physican
        int start = 1;
    		
        while(start < sheet.getPhysicalNumberOfRows() || rowBreak == false){
            	
         	try{
           		Physician phy = new Physician(sheet, start, codeColumn, npiColumn, fNameColumn, lNameColumn, locationColumn);
           		start += phy.getPhysicianCount();
           		
           		WebsiteSearch search = new WebsiteSearch(phy);
           		search.SearchWebsites();
           		
          		System.out.println(phy);
                   
          	} catch(Exception e){
           		System.out.println("Search Error: " + e.getMessage());
           		rowBreak = true;
           	}
            	
        }
            
        // Write out the results of the looping to the Excel document
        FileOutputStream outputStream = new FileOutputStream(new File(excelPath));
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
        fs.close();
            
        System.out.println("Done");
    }
}