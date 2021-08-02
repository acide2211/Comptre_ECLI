package controller;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * TODO...
 */
@Named
@ViewScoped
public class TimeOutController implements java.io.Serializable {

	// Constant
	private static final long serialVersionUID = 598498900883935815L;
	
	public void onIdle() throws IOException {
		 FacesContext context = FacesContext.getCurrentInstance();
		    context.getExternalContext().redirect("login.xhtml");
    }
}