package controller;

import javax.inject.Named;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

@Named
@ViewScoped
public class MenuController implements java.io.Serializable {

	// Members
	private static final long serialVersionUID = -570209501414653254L;

	private String welcomeMessage = "";

	public MenuController() {
	}

	// Methods
	@PostConstruct
	public void init() {
		ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
		be.chba.data.CHBAAgent authenticatedUser = (be.chba.data.CHBAAgent) extContext.getSessionMap()
				.get("AuthenticatedUser");
		if (authenticatedUser != null) {
			this.welcomeMessage = authenticatedUser.getFullName();
		}else {
			this.welcomeMessage = "Utilisateur";
		}
	}

	// GETTER-SETTER
	public String getWelcomeMessage() {
		return this.welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}
}