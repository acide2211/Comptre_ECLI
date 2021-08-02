package controller;

import javax.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author sd023331 - St&eacute;phane Doseray
 */
@Named
@RequestScoped
// TODO AuthenticationException & Exception management
public class LoginController implements java.io.Serializable {

	// Members
	private static final long serialVersionUID = -570209501414653253L;

	private String username = "";
	private String password = "";

	// Constructor
	public LoginController() {
	}

	
	private boolean isAdminUser() throws Exception {
		be.chba.securities.crypto.CHBACryptographyService cryptoService = be.chba.securities.crypto.CHBACryptographyService.getInstance();
		be.chba.io.CHBAFileReader fileReader = be.chba.io.CHBAFileReader.getInstance();

		File currentDir = new File(".");
		File filePrivateKey = new File(currentDir.getCanonicalPath() + File.separator + "chba-private.key");
		cryptoService.generatePrivateKey(filePrivateKey);

		File a = new File(currentDir.getCanonicalPath() + File.separator + "a");
		fileReader.setURL(a.toURI().toURL().toString());
		byte[] encryptedUserData = (byte[]) fileReader.read(be.chba.io.CHBAFileType.BINARY);
		byte[] decryptedUserData = cryptoService.decrypt(encryptedUserData);

		File b = new File(currentDir.getCanonicalPath() + File.separator + "b");
		fileReader.setURL(b.toURI().toURL().toString());
		byte[] encryptedPasswordData = (byte[]) fileReader.read(be.chba.io.CHBAFileType.BINARY);
		byte[] decryptedPasswordData = cryptoService.decrypt(encryptedPasswordData);

		return (be.chba.util.CHBAStringToolsService.getInstance().compare(this.username, decryptedUserData)
				&& be.chba.util.CHBAStringToolsService.getInstance().compare(this.password, decryptedPasswordData));
	}

	
	public void authenticate(String groupName) {
		
		try {
			be.chba.data.CHBAAgent authenticatedUser = null;
			if (isAdminUser()) {
				System.out.println("Hello Admin");
				authenticatedUser = new be.chba.data.CHBAAgent("Administrator", "", "0", "", -1);

			} else {
				if (this.username.isEmpty() || this.password.isEmpty())
					throw new Exception("Invalid credentials");

				authenticatedUser = be.chba.securities.iam.CHBAIAMService.getInstance().authenticate(this.username,
						this.password);
				System.out.println("The user " + authenticatedUser.getFirstName() + " "
						+ authenticatedUser.getLastName() + " is authenticated");

				if (be.chba.securities.iam.CHBAIAMService.getInstance().isUserMemberOfGroup(this.username,
						"g_" + groupName, be.chba.securities.iam.CHBAIAMContainer.USER_CONTAINER)) {
					System.out.println("The user " + authenticatedUser.getFirstName() + " "
							+ authenticatedUser.getLastName() + " is member of " + groupName + " users");
				} else {
					System.out.println("The user " + authenticatedUser.getFirstName() + " "
							+ authenticatedUser.getLastName() + " is not member of " + groupName + " users");
					throw new Exception("The user " + authenticatedUser.getFirstName() + " "
							+ authenticatedUser.getLastName() + " is not member of " + groupName + " users");
				}
			}
			// Authenticated

			/*
			 * java.util.Iterator<String> itIds =
			 * javax.faces.context.FacesContext.getCurrentInstance().
			 * getClientIdsWithMessages(); while (itIds.hasNext()) {
			 * java.util.List<javax.faces.application.FacesMessage> messageList =
			 * javax.faces.context.FacesContext.getCurrentInstance().getMessageList(itIds.
			 * next()); if (!messageList.isEmpty()) { // if empty, it will be unmodifiable
			 * and throw UnsupportedOperationException... messageList.clear(); }
			 */
			ExternalContext extContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			extContext.redirect(extContext.getRequestContextPath() + "/empty.xhtml");
			extContext.getSessionMap().put("AuthenticatedUser", authenticatedUser);

			// récupération des valeurs du fichier properties et ajout des valeur dans
			// variables de session

//      Properties properties = new Properties();
//		
//      ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//      try {
//    	  properties.load(ec.getResourceAsStream("/WEB-INF/dataGLC.properties"));
//      } catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//      }
//	
//      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+properties.getProperty("MATIN_DEBUT"));
//      
//      extContext.getSessionMap().put("MATIN_DEBUT", properties.getProperty("MATIN_DEBUT"));
//      extContext.getSessionMap().put("MATIN_FIN", properties.getProperty("MATIN_FIN"));
//      extContext.getSessionMap().put("APMIDI_DEBUT", properties.getProperty("APMIDI_DEBUT"));
//      extContext.getSessionMap().put("APMIDI_FIN", properties.getProperty("APMIDI_FIN"));

		} catch (Exception e) {
			System.out.println("Authentication error: " + e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new javax.faces.application.FacesMessage(FacesMessage.SEVERITY_ERROR,
							"L'authentification a échoué.", ""));
			e.printStackTrace();
		}
	}

	// Getters
	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	// Setters
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}