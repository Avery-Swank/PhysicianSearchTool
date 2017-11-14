import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class WebsiteSearch {

	private Physician physician;
	
	private String fName;
	private String lName;
	private String cityName;
	
	private final WebClient webClient;
    private HtmlPage page1;
    private HtmlForm form;
	
	public WebsiteSearch(Physician phy){
		physician = phy;
		fName = phy.getFirstName();
		lName = phy.getLastName();
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setJavaScriptEnabled(false);
		
		// Remove unnecessary warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
	}
	
	// Go through popular practicing websites and set a location for a physician's
	// area of practice
	public void SearchWebsites(){
		if(isInactive()){
			physician.SetLocation("D");
		} else if(isHCMC()){
			physician.SetLocation("HCMC");
		} else if(isHealthEast()){
			physician.SetLocation("HEALTHEAST");
		}
		
		physician.RedesignPhysician();
	}
	
	// Search the Minnesota Board of Medical Practice website for active/inactive physicians
	public boolean isInactive(){
		try {
			page1 = webClient.getPage("http://docfinder.docboard.org/mn/df/mndf.htm");
			form = page1.getFormByName("");
			
			HtmlSubmitInput button = form.getInputByName("");
			HtmlTextInput textField1 = form.getInputByName("medlname");
			HtmlTextInput textField2 = form.getInputByName("medfname");
			textField1.setValueAttribute(lName);
			textField2.setValueAttribute(fName);
			
			HtmlPage page2 = button.click();
			
			String searchContent = page2.asText();
			
			return searchContent.contains("Inactive");
			
		} catch (FailingHttpStatusCodeException | IOException | ElementNotFoundException e) {
			System.out.println("Docfinder ERROR: " + e.getMessage());
		}
		
		return false;
	}
	
	// Search the Hennepin County Medical Center website for active/inactive physicians
	public boolean isHCMC(){
		try {
			page1 = webClient.getPage("https://www.hcmc.org/providers/index.htm");
			form = page1.getFormByName("providerSearch");
			
			HtmlImageInput button = (HtmlImageInput) form.getInputByValue("submit");
			HtmlTextInput textField1 = form.getInputByName("p_name");
			textField1.setValueAttribute(lName);
			
			HtmlPage page2 = (HtmlPage) button.click();
			String searchContent = page2.asText();
			searchContent = searchContent.toUpperCase();
			
			return searchContent.contains(lName) && searchContent.contains(fName);
			
		} catch (Exception e) {
			System.out.println("HCMC ERROR: " + e.getMessage());
		}
		
		return false;
	}
	
		// Search the Health East website for active/inactive physicians
		public boolean isHealthEast(){
			try {
				page1 = webClient.getPage("http://www.healtheast.org/find-a-doctor.html");
				List<HtmlForm> forms = page1.getForms();
				form = forms.get(1);

				HtmlInput button = form.getInputByName("submit");
				HtmlTextInput textField1 = form.getInputByName("firstname");
				HtmlTextInput textField2 = form.getInputByName("lastname");
				textField1.setValueAttribute(fName);
				textField2.setValueAttribute(lName);
				
				HtmlPage page2 = (HtmlPage) button.click();
				String searchContent = page2.asText();
				searchContent = searchContent.toUpperCase();
				
				return !searchContent.contains("NO RECORDS WERE FOUND");
				
			} catch (Exception e) {
				System.out.println("HEALTHEAST ERROR: " + e.getMessage());
			}
			
			return false;
		}

}
