package com.gajos.ftp;
	
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.fxml.FXML;

public class FTPClientGUI extends Application {
	
    private final ImageView rootIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/ftp-server.png"))
        );
    
    private final ImageView directoryIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/directory.png"))
        );
    
    private final ImageView fileIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/file.png"))
        );
	
	/**
	 * Custom controller for FXML.
	 * @author P.Gajewski
	 *
	 */
	private class FXController extends VBox
	{
		/*****************************Private variables************************/
		/**
		 * Language controller.
		 */
		private LanguageController lc = LanguageController.getInstance();
		
		/**
		 * TextArea for Logs
		 */
		@FXML private TextArea logsTextArea;
		
		/**
		 * ListView of endpoints in active project
		 */
		@FXML private ListView<String> endpointsList;
		
		/**
		 * ListView of details info about field.
		 */
		@FXML private ListView<String> detailsList;
		
		/**
		 * TreeView of directory content.
		 */
		@FXML private TreeView<String> directoryTree;
		
		/**
		 * Menu item for opening existing project.
		 */
		@FXML private MenuItem openProject;
		
		/**
		 * Menu item for creating a new project.
		 */
		@FXML private MenuItem newProject;
		
		/**
		 * Menu item for saving an active project in old file path.
		 */
		@FXML private MenuItem saveProject;
		
		/**
		 * Menu item for saving an active project with new file path.
		 */
		@FXML private MenuItem saveAsProject;
		
		@FXML private MenuItem revertProject;
		
		/**
		 * Menu item for closing an active project.
		 */
		@FXML private MenuItem closeProject;
		
		@FXML private MenuItem serverMenu;
		
		@FXML private MenuItem addServer;
		
		@FXML private MenuItem removeServer;
		
		@FXML private MenuItem connectServer;
		
        @FXML private MenuItem disconnectServer;
        
        @FXML private MenuItem changeProperties;
        
        @FXML private MenuItem changeServerUser;
        	        
        
        final private TreeItem<String> chooseServerItem =  new TreeItem<String> (lc.getLanguage()
        		.chooseServerFirst());
        
        final private TreeItem<String> connectItem =  new TreeItem<String> (lc.getLanguage()
        		.connect());
        
		/**
		 * Primary stage.
		 */
		public Stage primaryStage;
		
		/*****************************Update methods****************************/
		private void updateDetailList()
		{
			List<String> details = new ArrayList<>();
			if(activeProject == null)
			{
				detailsList.setItems(null);
				return;
			} else {
				details.add(lc.getLanguage().projectPath() + activeProject.getFilePath());
			}
			
			if(activeClient == null)
			{
				ObservableList<String> items =FXCollections.observableArrayList (
						details.stream().toArray(String[]::new));
				detailsList.setItems(items);
				
				return;
			} else {
				details.add((lc.getLanguage().hostname() + ": " + activeClient.getHostname()));
				details.add((lc.getLanguage().serverAddress() + ": " + activeClient.getIpAddress().getHostAddress()));
				details.add((lc.getLanguage().serverPort() + ": " + Integer.toString(activeClient.getPort())));
			}
			
			if(activeFile != null && activeClient.isConnected())
			{
				details.add(lc.getLanguage().filePath() + activeFile.getServerPath());
				details.add(lc.getLanguage().size() + Long.toString((activeFile.getSize())));
			}
			
			//Public all.
			ObservableList<String> items =FXCollections.observableArrayList (
					details.stream().toArray(String[]::new));
			detailsList.setItems(items);
		}
		
		private void updateDirectoryTree()
		{
			TreeItem<String> rootItem;
			if(activeProject == null)
			{
				rootItem = chooseServerItem;
		        directoryTree.setRoot(rootItem);
				return;
			}
			
			if(activeClient == null)
			{
				rootItem = chooseServerItem;
		        directoryTree.setRoot(rootItem);
				return;
			}
			
			//Print root directory for actual client. 		
	        rootItem = new TreeItem<String> (activeClient
	        		.getHostname(), rootIcon);
	        rootItem.setExpanded(true);
	      
	        
	        if(activeClient.isConnected())
	        {
	        	TreeItem<String> item = new TreeItem<String>("/", directoryIcon);
	        	
	        	item.setExpanded(true);
	        	
	        	rootItem.getChildren().add(item);
	        } else {
	        	rootItem.getChildren().add(connectItem);
	        }
	        
	        directoryTree.setRoot(rootItem);
		}
		
		private void updateProjectTree()
		{
			if(activeProject == null)
			{
				endpointsList.setItems(null);
				return;
			}
			
			//Print all clients.
			String[] nameArray = activeProject.getClients().stream()
				.map(FTPClient::getHostname)
				.sorted()
				.toArray(String[] ::new);
			
			ObservableList<String> items =FXCollections.observableArrayList (
					nameArray);
			endpointsList.setItems(items);
		}
		
		private void updateMenu()
		{
			if(FTPClientGUI.this.activeProject == null)
			{
				closeProject.setDisable(true);
				saveAsProject.setDisable(true);
				saveProject.setDisable(true);
				serverMenu.setDisable(true);
				revertProject.setDisable(true);
			} else {
				closeProject.setDisable(false);
				saveAsProject.setDisable(false);
				saveProject.setDisable(false);
				serverMenu.setDisable(false);
				if("".equals(FTPClientGUI.this.activeProject.getFilePath()))
				{
					revertProject.setDisable(true);
				} else {
					revertProject.setDisable(false);
				}
			}
			
			if(FTPClientGUI.this.activeClient == null)
			{
				removeServer.setDisable(true);
				connectServer.setDisable(true);
				disconnectServer.setDisable(true);
				changeProperties.setDisable(true);
				changeServerUser.setDisable(true);
			} else {
				removeServer.setDisable(false);
				changeProperties.setDisable(false);
				changeServerUser.setDisable(false);
				
				if(FTPClientGUI.this.activeClient.isConnected())
				{
					connectServer.setDisable(true);
					disconnectServer.setDisable(false);
				} else {
					connectServer.setDisable(false);
					disconnectServer.setDisable(true);
				}
			}
		}
		
		private void printException(FTPException e)
		{
			String prefix = null;
			switch(e.getStatus())
			{
				case OK: prefix = lc.getLanguage().ok(); break;
				case ERROR: prefix = lc.getLanguage().error(); break;
				case WARNING: prefix = lc.getLanguage().warning(); break;
			}
			
			System.out.println(prefix + ' ' + e.getMessage());
		}		
		
		private void printLog(String log)
		{
			System.out.println(log);
		}
		
		private void handleMouseClicked(MouseEvent event) {
		    Node node = event.getPickResult().getIntersectedNode();
		    // Accept clicks only on node cells, and not on empty spaces of the TreeView
		    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell<String>) node).getText() != null)) {
		        TreeItem<String> item = directoryTree.getSelectionModel().getSelectedItem();
		        if (item == connectItem)
					connectServerHandler();
		        if(item == chooseServerItem)
		        	return;
		        if(item == directoryTree.getRoot())
		        	return;
		        
		        //Create new actual file.
		        String itemName = item.getValue();
		        activeFile = new FTPFile(activeClient,itemName,getTreeItemPath(item));
		        
		        if(isFileADirectory(itemName))
		        {
		        	addNextLevel(item);
		        } else {
		        	
		        }
		        
		    }
		}
		
		private boolean isFileADirectory(String path)
		{
			return path.lastIndexOf('/') == path.length() -1;
		}
		
		private String getTreeItemPath(TreeItem<String> item)
		{			
			StringBuilder path = new StringBuilder();
			path.append('/');
			
			//Collect full path to directory.
			TreeItem<String> parent = item.getParent();

			while(parent != directoryTree.getRoot())
			{
				if(parent == null)
					return null;
				String value = parent.getValue();
				if(value == null)
					return null;
				path.insert(0, value);
				path.insert(0, '/');
				parent = parent.getParent();
			}
			return path.toString();
		}
		
		private void addNextLevel(TreeItem<String> item)
		{
			String path = getTreeItemPath(item);
			item.setExpanded(true);
			//Get directory content.
			activeClient.<String>operiationWithTimeout(p->{activeClient.changeDirectory(p);}, path.toString(), connectionTimeout);
			List<String> content = activeClient.<List<String>>operiationWithTimeout(()->{return activeClient.listDirectoryContent();}, connectionTimeout);
			//List<String> content = Arrays.asList("folder1/", "folder2/", "plik1", "plik2");
			item.getChildren().clear();
			content.stream()
					.forEach(s -> {
						if(FXController.this.isFileADirectory(s)) {
							item.getChildren().add(new TreeItem<String>(s, new ImageView(directoryIcon.getImage())));
						} else {
							item.getChildren().add(new TreeItem<String>(s, new ImageView(fileIcon.getImage())));							
						}
					});
		}
		
		/*****************************Binded methods****************************/
		
		@FXML protected void preferencesHandler()
		{
        	final Stage dialog = new Stage();
			dialog.initStyle(StageStyle.UTILITY);
			
			//Creating a GridPane container
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setVgap(5);
			grid.setHgap(5);
			grid.setPrefWidth(100);
			
			//Defining the Name text field
			final TextField hostnameField = new TextField();
			hostnameField.setPromptText(lc.getLanguage().username());
			hostnameField.setText(activeClient.getLogin());
			hostnameField.setPrefColumnCount(10);
			GridPane.setConstraints(hostnameField, 0, 0);
			grid.getChildren().add(hostnameField);
			
			//Defining the Last Name text field
			final TextField passwordField = new TextField();
			passwordField.setPromptText(lc.getLanguage().password());
			passwordField.setText(activeClient.getPassword());
			GridPane.setConstraints(passwordField, 0, 1);
			grid.getChildren().add(passwordField);

			//Defining the Submit button
			Button submit = new Button(lc.getLanguage().change());
			submit.setMinWidth(30);
			submit.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					try {
						activeClient.setLogin(hostnameField.getText());
						activeClient.setPassword(passwordField.getText());
						
						//Close dialog.
						dialog.close();
						updateProjectTree();
						updateDirectoryTree();
						updateDetailList();
					} catch(FTPException e) {
						if(FTPClientGUI.DEBUG)
						{
							e.printStackTrace();

						} else {
							printException(e);
						}					}
				}
			});
			GridPane.setConstraints(submit, 1, 0);
			grid.getChildren().add(submit);
			
			//Defining the Clear button
			Button clear = new Button(lc.getLanguage().clear());
			clear.setMinWidth(30);
			clear.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					hostnameField.clear();
					passwordField.clear();
				}
			});
			GridPane.setConstraints(clear, 1, 1);
			grid.getChildren().add(clear);
			
			Scene scene = new Scene(grid,280, 120);
			dialog.setScene(scene);
			dialog.setTitle(lc.getLanguage().addServer());
			dialog.show();			
		}
		
		/**
		 * Method to create new client project.
		 */
		@FXML protected void newProjectHandler()
		{
			if(FTPClientGUI.this.activeProject != null)
			{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(lc.getLanguage().savingConfirm());
				alert.setHeaderText(lc.getLanguage().savingAsk());
				alert.setContentText(lc.getLanguage().savingAlert());

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
				    if(activeProject.getFilePath() == null)
				    {
				    	saveAsProjectHandler();
				    }
				    else
				    {
				    	saveProjectHandler();
				    }
				}
			}
			FTPClientGUI.this.activeProject = new FTPProject();
			
			//Update GUI.
			updateDetailList();
			updateDirectoryTree();
			updateProjectTree();
			updateMenu();
		}
		FXController(){}
		
		public void initialize()
		{
			//Init endpoint list handler.
			endpointsList.getSelectionModel().selectedItemProperty().addListener(
	                new ChangeListener<String>() {
	                    public void changed(ObservableValue<? extends String> ov, 
	                        String old_val, String new_val) {
	                    	if(new_val == null)
	                    		return;

	                    	FTPClientGUI.this.activeClient = FTPClientGUI.this.activeProject
	                    			.				getClientByHostname(new_val);
	                    	FXController.this.updateDirectoryTree();
	                    	FXController.this.updateDetailList();
	                    	FXController.this.updateMenu();
	                }
	            });
			
			//Set first item.
			 directoryTree.setRoot(chooseServerItem);
			 directoryTree.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
				    handleMouseClicked(event);});
			 
			 //Create log windows.
	        ControlOutputStream console = new ControlOutputStream(logsTextArea);
	        PrintStream ps = new PrintStream(console, true);
	        System.setOut(ps);
	        System.setErr(ps);
		}
		
		/**
		 * Method to open previously saved project.
		 */
		@FXML protected void openProjectHandler()
		{
			//Using file chooser.
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(lc.getLanguage().openFile());
			File file = fileChooser.showOpenDialog(primaryStage);
			if(file != null)
			{
				try {
					activeProject = new FTPProject();
					activeProject.deserialize(file.getAbsolutePath());
				} catch (FTPException e) {
					if(FTPClientGUI.DEBUG)
					{
						e.printStackTrace();

					} else {
						printException(e);
					}
					activeProject = null;
				}
			}
			
			//Update GUI.
			updateDetailList();
			updateDirectoryTree();
			updateProjectTree();
			updateMenu();
		}
		
		/**
		 * Method to save actual project.
		 */
		@FXML protected void saveProjectHandler()
		{
			if(activeProject.getFilePath() == "")
			{
				this.saveAsProjectHandler();
				return;
			}
			try {
				activeProject.serialize(FTPClientGUI.this.activeProject.getFilePath());
			} catch (FTPException e) {
				if(FTPClientGUI.DEBUG)
				{
					e.printStackTrace();

				} else {
					printException(e);
				}
			}
		}
		
		
		@FXML protected void saveAsProjectHandler()
		{
			//Using file chooser.
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(lc.getLanguage().saveFileAs());
			File file = fileChooser.showSaveDialog(primaryStage);
			if(file != null)
			{
				try {
					FTPClientGUI.this.activeProject.setFilePath(file.getAbsolutePath());
					FTPClientGUI.this.activeProject.serialize(FTPClientGUI.this.activeProject.getFilePath());
				} catch (FTPException e) {
					if(FTPClientGUI.DEBUG)
					{
						e.printStackTrace();

					} else {
						printException(e);
					}
				}
			}
		}
		
		/**
		 * Method to close actual active project.
		 */
		@FXML protected void closeProjectHandler()
		{
			activeProject = null;
			activeClient = null;
			activeFile = null;

			//Update GUI.
			updateDetailList();
			updateDirectoryTree();
			updateProjectTree();
			updateMenu();
		}

		@FXML protected void addServerHandler()
		{
			final Stage dialog = new Stage();
			dialog.initStyle(StageStyle.UTILITY);
			
			//Creating a GridPane container
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setVgap(5);
			grid.setHgap(5);
			grid.setPrefWidth(100);
			
			//Defining the Name text field
			final TextField hostnameField = new TextField();
			hostnameField.setPromptText(lc.getLanguage().hostname());
			hostnameField.setPrefColumnCount(10);
			GridPane.setConstraints(hostnameField, 0, 0);
			grid.getChildren().add(hostnameField);
			
			//Defining the Last Name text field
			final TextField serverAddressField = new TextField();
			serverAddressField.setPromptText(lc.getLanguage().serverAddress());
			GridPane.setConstraints(serverAddressField, 0, 1);
			grid.getChildren().add(serverAddressField);
			
			//Defining the Comment text field
			final TextField portField = new TextField();
			portField.setPrefColumnCount(15);
			portField.setPromptText(lc.getLanguage().serverPort());
			GridPane.setConstraints(portField, 0, 2);
			grid.getChildren().add(portField);
			
			//Defining the Submit button
			Button submit = new Button(lc.getLanguage().add());
			submit.setMinWidth(30);
			submit.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					try {
						FTPClient client = new FTPClient();
						client.setHostname(hostnameField.getText());
						client.setIpAddress(serverAddressField.getText());
						client.setPort(Integer.parseInt(portField.getText()));
						FTPClientGUI.this.activeProject.addServerEndpoint(client);
						
						//Close dialog.
						dialog.close();
						updateProjectTree();
						updateDirectoryTree();
						updateDetailList();
					} catch(FTPException e) {
						if(FTPClientGUI.DEBUG)
						{
							e.printStackTrace();

						} else {
							printException(e);
						}					}
				}
			});
			GridPane.setConstraints(submit, 1, 1);
			grid.getChildren().add(submit);
			
			//Defining the Clear button
			Button clear = new Button(lc.getLanguage().clear());
			clear.setMinWidth(30);
			clear.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					hostnameField.clear();
					serverAddressField.clear();
					portField.clear();
				}
			});
			GridPane.setConstraints(clear, 1, 2);
			grid.getChildren().add(clear);
			
			Scene scene = new Scene(grid,280, 120);
			dialog.setScene(scene);
			dialog.setTitle(lc.getLanguage().addServer());
			dialog.show();
        }
		
		@FXML protected void removeServerHandler()
		{
			if(activeClient.isConnected())
				disconnectServerHandler();
			activeProject.removeServerEndpoint(activeClient);
			activeClient = null;
			activeFile = null;
			updateProjectTree();
			updateDirectoryTree();
			updateDetailList();
			updateMenu();
		}
		
		@FXML protected void connectServerHandler()
		{
			try {
				activeClient.<Void>operiationWithTimeout(()->{activeClient.connect();  return null;}, connectionTimeout);
			} catch(FTPException e) {
				if(FTPClientGUI.DEBUG)
				{
					e.printStackTrace();
				} else {
					printException(e);
				}
			}
			finally {
				updateProjectTree();
				updateDirectoryTree();
				updateDetailList();
				updateMenu();
			}
		}
		
        @FXML protected void disconnectServerHandler()
        {
        	try {
	        	activeClient.disconnect();
			} catch (FTPException e) {
				if(FTPClientGUI.DEBUG)
				{
					e.printStackTrace();
				} else {
					printException(e);
				}
			} finally {
				updateProjectTree();
				updateDirectoryTree();
				updateDetailList();
				updateMenu();
			}
        }
        
        @FXML protected void changePropertiesHandler()
        {
			final Stage dialog = new Stage();
			dialog.initStyle(StageStyle.UTILITY);
			
			//Creating a GridPane container
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setVgap(5);
			grid.setHgap(5);
			grid.setPrefWidth(100);
			
			//Defining the Name text field
			final TextField hostnameField = new TextField();
			hostnameField.setPromptText(lc.getLanguage().hostname());
			hostnameField.setText(activeClient.getHostname());
			hostnameField.setPrefColumnCount(10);
			GridPane.setConstraints(hostnameField, 0, 0);
			grid.getChildren().add(hostnameField);
			
			//Defining the Last Name text field
			final TextField serverAddressField = new TextField();
			serverAddressField.setPromptText(lc.getLanguage().serverAddress());
			serverAddressField.setText(activeClient.getIpAddress().getHostAddress());
			GridPane.setConstraints(serverAddressField, 0, 1);
			grid.getChildren().add(serverAddressField);
			
			//Defining the Comment text field
			final TextField portField = new TextField();
			portField.setPrefColumnCount(15);
			portField.setPromptText(lc.getLanguage().serverPort());
			portField.setText(Integer.toString(activeClient.getPort()));
			GridPane.setConstraints(portField, 0, 2);
			grid.getChildren().add(portField);
			
			//Defining the Submit button
			Button submit = new Button(lc.getLanguage().change());
			submit.setMinWidth(30);
			submit.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					try {
						activeClient.setHostname(hostnameField.getText());
						activeClient.setIpAddress(serverAddressField.getText());
						activeClient.setPort(Integer.parseInt(portField.getText()));
						
						//Close dialog.
						dialog.close();
						updateProjectTree();
						updateDirectoryTree();
						updateDetailList();
					} catch(FTPException e) {
						if(FTPClientGUI.DEBUG)
						{
							e.printStackTrace();

						} else {
							printException(e);
						}					}
				}
			});
			GridPane.setConstraints(submit, 1, 1);
			grid.getChildren().add(submit);
			
			//Defining the Clear button
			Button clear = new Button(lc.getLanguage().clear());
			clear.setMinWidth(30);
			clear.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					hostnameField.clear();
					serverAddressField.clear();
					portField.clear();
				}
			});
			GridPane.setConstraints(clear, 1, 2);
			grid.getChildren().add(clear);
			
			Scene scene = new Scene(grid,280, 120);
			dialog.setScene(scene);
			dialog.setTitle(lc.getLanguage().addServer());
			dialog.show();        	
        }
        
        @FXML protected void changeServerUserHandler()
        {
        	final Stage dialog = new Stage();
			dialog.initStyle(StageStyle.UTILITY);
			
			//Creating a GridPane container
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setVgap(5);
			grid.setHgap(5);
			grid.setPrefWidth(100);
			
			//Defining the Name text field
			final TextField hostnameField = new TextField();
			hostnameField.setPromptText(lc.getLanguage().username());
			hostnameField.setText(activeClient.getLogin());
			hostnameField.setPrefColumnCount(10);
			GridPane.setConstraints(hostnameField, 0, 0);
			grid.getChildren().add(hostnameField);
			
			//Defining the Last Name text field
			final TextField passwordField = new TextField();
			passwordField.setPromptText(lc.getLanguage().password());
			passwordField.setText(activeClient.getPassword());
			GridPane.setConstraints(passwordField, 0, 1);
			grid.getChildren().add(passwordField);

			//Defining the Submit button
			Button submit = new Button(lc.getLanguage().change());
			submit.setMinWidth(30);
			submit.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					try {
						activeClient.setLogin(hostnameField.getText());
						activeClient.setPassword(passwordField.getText());
						
						//Close dialog.
						dialog.close();
						updateProjectTree();
						updateDirectoryTree();
						updateDetailList();
					} catch(FTPException e) {
						if(FTPClientGUI.DEBUG)
						{
							e.printStackTrace();

						} else {
							printException(e);
						}					}
				}
			});
			GridPane.setConstraints(submit, 1, 0);
			grid.getChildren().add(submit);
			
			//Defining the Clear button
			Button clear = new Button(lc.getLanguage().clear());
			clear.setMinWidth(30);
			clear.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					hostnameField.clear();
					passwordField.clear();
				}
			});
			GridPane.setConstraints(clear, 1, 1);
			grid.getChildren().add(clear);
			
			Scene scene = new Scene(grid,280, 120);
			dialog.setScene(scene);
			dialog.setTitle(lc.getLanguage().addServer());
			dialog.show();        	
        }
		
        @FXML protected void revertProjectHandler()
        {
        	activeClient = null;
        	activeFile = null;
        	activeProject.deserialize(activeProject.getFilePath());
        	updateMenu();
        	updateDirectoryTree();
        	updateDetailList();
        }
        
        @FXML protected void closeApp()
        {
        	primaryStage.close();
        }
    
        private class ActionProgress implements Runnable{

			private long destinationSize;
			
			private File destinactionFile;
        	
			private boolean running;
			
			@Override
			public void run() {
				//Init dialog.
	    		final Stage dialog = new Stage();
	    		final ProgressBar pb = new ProgressBar(0.0);
	    		final File destFile = this.destinactionFile;
	    		final Long destSize = this.destinationSize;
	    		
	    		Button downloadButton = new Button(lc.getLanguage().cancel());
	    		downloadButton.setMinWidth(30);
	    		downloadButton.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	    			@Override
	    			public void handle(MouseEvent arg0) {
	    				try {
	    					ActionProgress.this.stop();
	    					activeClient.stopDataTransfer();
	    				} catch(FTPException e) {
	    					if(FTPClientGUI.DEBUG)
	    					{
	    						e.printStackTrace();

	    					} else {
	    						printException(e);
	    					}
	    				}
	    			}
	    		});
	    		
	            final VBox vb = new VBox();
	            vb.setSpacing(5);
	            vb.getChildren().add(pb);
				Scene scene = new Scene(vb, 280, 120);
				dialog.setScene(scene);
				dialog.show();
	    		
	    		running = true;
	    		while(running && pb.getProgress() != 1.0)
	    		{
	    			pb.setProgress(destFile.getTotalSpace()/destSize);
	    			try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    		
				
			}
			
        	public void setTask(File destinationFile, Long destinationSize)
        	{
        		this.destinactionFile = destinationFile;
        		this.destinationSize = destinationSize;
        	}
        	
        	synchronized public void stop()
        	{
        		running = false;
        	}
        	
        	synchronized public void resume()
        	{
        		running = true;
        	}
        };
        
        private Runnable showSendingProgress = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
        	
        };
        
        private void showFileManagerDialog(TreeItem<String> item)
    	{
    		final Stage dialog = new Stage();
    		dialog.initStyle(StageStyle.UTILITY);
    		
    		//Creating a GridPane container
    		GridPane grid = new GridPane();
    		grid.setPadding(new Insets(10, 10, 10, 10));
    		grid.setVgap(5);
    		grid.setHgap(5);
    		grid.setPrefWidth(100);
    		
    		//Defining the Submit button
    		Button downloadButton = new Button(this.lc.getLanguage().add());
    		downloadButton.setMinWidth(30);
    		downloadButton.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent arg0) {
    				try {
    					String path = getTreeItemPath(item);
    					//Open dialog.
    					FileChooser fileChooser = new FileChooser();
    					fileChooser.setTitle(lc.getLanguage().downloadFile());
    					File file = fileChooser.showSaveDialog(dialog);
    					if(file != null)
    						activeClient.receiveFile(path, file);

    					
    				} catch(FTPException e) {
    					if(FTPClientGUI.DEBUG)
    					{
    						e.printStackTrace();

    					} else {
    						printException(e);
    					}					}
    			}
    		});
    		GridPane.setConstraints(downloadButton, 0, 0);
    		grid.getChildren().add(downloadButton);
    		
    		//Define upload button
    		Button uploadButton = new Button(this.lc.getLanguage().add());
    		downloadButton.setMinWidth(30);
    		downloadButton.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent arg0) {
    				try {
    					String path = getTreeItemPath(item);
    					//Open dialog.
    					FileChooser fileChooser = new FileChooser();
    					fileChooser.setTitle(lc.getLanguage().uploadFile());
    					File file = fileChooser.showOpenDialog(dialog);
    					if(file != null)
    					{
    						activeClient.sendFile(file, path);
    					}

    					
    				} catch(FTPException e) {
    					if(FTPClientGUI.DEBUG)
    					{
    						e.printStackTrace();

    					} else {
    						printException(e);
    					}					}
    			}
    		});
    		GridPane.setConstraints(downloadButton, 0, 0);
    		grid.getChildren().add(downloadButton);
    	}
        
	}
	
	
	/**
	 * Active project of FTP servers.
	 */
	private FTPProject activeProject;
	
	/**
	 * Actual chosen FTP Client.
	 */
	private FTPClient activeClient;
	
	/**
	 * Actual active file on server.
	 */
	private FTPFile activeFile;
	
	//FX controller.
	private FXController controller;
	
	private long connectionTimeout = FTPClient.DEFAULT_TIMEOUT;
	
	/**
	 * Debug flag.
	 */
	public static Boolean DEBUG = false;	
	
	/**
	 * Start function.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
			this.controller = new FXController();
			this.controller.primaryStage = primaryStage;
			loader.setController(this.controller);
			loader.setRoot(this.controller);
			loader.load();
			Scene scene = new Scene(this.controller,1200,620);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMinHeight(660);
			primaryStage.setMinWidth(1200);
			primaryStage.setTitle("FTP Client");
			
			//Init update.
			this.controller.initialize();
			controller.updateMenu();
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Entry static main.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
