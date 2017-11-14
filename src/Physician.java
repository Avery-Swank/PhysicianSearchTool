import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class Physician {
	
	private HSSFSheet sheet;
	private HSSFRow[] rows;
	private HSSFRow row;
	private HSSFCell cell;

	private String newCode;
	private int npiNumber;
	private String firstName;
	private String lastName;
	private int addressColumn;
	private String newLocation;
	
	// Attributes: .xls sheet, row #, first name column #, last name column #, address column #
	public Physician(HSSFSheet st, int r, int code, int npi, int fNameC, int lNameC, int addressC) {
		sheet = st;
		row = sheet.getRow(r);
		
		// Extract physican code
		cell = row.getCell(code - 1);
		try{
			newCode = cell.getStringCellValue();
		} catch(Exception e){
			newCode = Integer.toString((int) cell.getNumericCellValue());
		}
		
		// Extract npi number
		cell = row.getCell(npi - 1);
		npiNumber =  (int)cell.getNumericCellValue();
		
		// Extract first name string
		cell = row.getCell(fNameC - 1);
		firstName = cell.getStringCellValue().toUpperCase();
		
		// Extract last name string
		cell = row.getCell(lNameC - 1);
		lastName = cell.getStringCellValue().toUpperCase();
		
		// Extract address location
		addressColumn = addressC;
		
		// Determine if and how many more duplicate rows are for the same physican
		int i = r + 1;
		try{
			row = sheet.getRow(i);
			cell = row.getCell(npi - 1);
			int newNPI = (int)cell.getNumericCellValue();
			
			while(npiNumber == newNPI){
				i++;
				row = sheet.getRow(i);
				cell = row.getCell(npi - 1);
				newNPI = (int)cell.getNumericCellValue();
			}
		} catch(Exception e){
			
		}
		
		rows = new HSSFRow[i - r];
		
		for(int j = 0; j < rows.length; j++){
			rows[j] = sheet.getRow(r + j);
		}
	}
	
	// Add physicians new location and new code to the excel corresponding rows
	public void RedesignPhysician(){
		if(newLocation != null){
			cell = rows[0].createCell(addressColumn - 1);
			cell.setCellValue(newLocation);
			
			if(getPhysicianCount() > 1){
				for(int i = 1; i < getPhysicianCount(); i++){
					cell = rows[i].createCell(2 - 1);
					cell.setCellValue(newCode);
					
					cell = rows[i].createCell(addressColumn - 1);
					cell.setCellValue("D");
				}
			}
			
			HighlightPhysician();
		}
	}
	
	public void HighlightPhysician(){
		for(int i = 0; i < getPhysicianCount(); i++){
			cell = rows[i].getCell(0);
			cell.setCellStyle(SearchSpace.style);
		}
	}
	
	public void SetLocation(String s){
		newLocation = s;
	}
	
	public String getCode(){
		return newCode;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public int getPhysicianCount(){
		return rows.length;
	}
	
	public String toString(){
		return firstName + ' ' + lastName + " practices at " + newLocation;
	}

}
