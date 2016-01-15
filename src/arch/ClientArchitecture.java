package arch;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ks.client.controllers.ClientControllerChain;
import ks.client.interfaces.ILobbyInitialize;
import ks.client.lobby.ConnectFrame;
import ks.client.processor.ClientProcessor;
import ks.framework.common.Configure;
import view.TabbedLayoutGUI;
import view.TableManagerGUI;
import view.UserManagerGUI;

/**
 * This is a sample class that shows how to configure the client to function.
 * Copy (and rename) this into your appropriate location and then you can 
 * begin the process of building up your own server implementation.
 * <p>
 * Note that you will have to provide your response to requests to close the 
 * window. Here it just exits but that may not be what you want.
 * 
 * @author George Heineman
 */
public class ClientArchitecture {
	
	/** Entity created for client. */
	static ConnectFrame cf;
	
	/**
	 * Launch client app.
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// Determine the XML schema we are going to use
		if (!Configure.configure()) {
			System.err.println("Unable to configure Message XML");
			return;
		}
		
		// initialization callback. Client-side groups can pass in 
		// an object that provides this interface into the ConnectFrame
		// constructor and it will be called at the proper time.
		ILobbyInitialize init = clientLobbySetup(); 
		
		// all action on this (default) local host. Should there be a need
		// to connect to a different host computer, then you would need 
		// to pass in command-line argument values from 'args' into the 
		// constructor of UserContext so the client knows to which server
		// to connect.
		cf = new ConnectFrame(init);
		cf.addWindowListener(new WindowAdapter() {

			// Override the closing method to exit from the VM.
			// we need no check because this is before user 
			// has actually connected.
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);			
			}

		});
		
		// here is where you can augment the chain of client-side controllers
		controllerChainSetup();
		
		cf.setVisible(true);
		
		// running...
	}

	/**
	 * Initializes and sets lobby GUI elements
	 * @return the instantiated ILobbyInitialize
	 */
	static ILobbyInitialize clientLobbySetup() {
		// Set UserManager, TableManager and GameManager GUI panels
		UserManagerGUI userManagerGUI = new UserManagerGUI();
		TableManagerGUI tableManagerGUI = new TableManagerGUI();
		//GameManagerGUI gameManagerGUI = new GameManagerGUI();
		
		// Set TabbedLayout gui panel and add table and game manager GUIs to it
		TabbedLayoutGUI tabbedLayoutGUI = new TabbedLayoutGUI();
		tabbedLayoutGUI.setTablesPanel(tableManagerGUI);
		//tabbedLayoutGUI.setGamePanel(gameManagerGUI);
		ILobbyInitialize init = new MyLobbyInitialization(tabbedLayoutGUI, userManagerGUI);
		return init;
	}

	/**
	 * Creates and populates the client processor chain
	 */
	static void controllerChainSetup() {
		ClientControllerChain head = ClientProcessor.head();
		head.append(new ClientExtension());
		head.append(new PlayerResponseExtension());
		head.append(new TableResponseExtension());
		head.append(new ConfirmExtension());
		head.append(new TableEmptyExtension());
		head.append(new LogoutExtension());
		head.append(new InviteExtension());
		head.append(new UpdateResponseExtension());
	}
}
