package com.gajos.ftp;
	
import java.io.File;
import java.io.PrintStream;
import java.util.Optional;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

public class FTPClientGUI extends Application {
	
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
		
		/**
		 * Menu item for closing an active project.
		 */
		@FXML private MenuItem closeProject;
		
		/**
		 * Primary stage.
		 */
		public Stage primaryStage;
		
		/*****************************Update methods****************************/
		private void updateDetailList()
		{
			
		}
		
		private void updateDirectoryTree()
		{
			
		}
		
		private void updateProjectTree()
		{
			
		}
		
		private void updateMenu()
		{
			if(FTPClientGUI.this.activeProject == null)
			{
				closeProject.setDisable(true);
				saveAsProject.setDisable(true);
				saveProject.setDisable(true);
			}
			else
			{
				closeProject.setDisable(false);
				saveAsProject.setDisable(false);
				saveProject.setDisable(false);
			}
			
		}
		
		/*****************************Binded methods****************************/
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
					activeProject.serialize(FTPClientGUI.this.activeProject.getFilePath());
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			if(activeProject.getFilePath() == null)
			{
				this.saveAsProjectHandler();
				return;
			}
			try {
				activeProject.serialize(FTPClientGUI.this.activeProject.getFilePath());
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@FXML protected void saveAsProjectHandler()
		{
			//Using file chooser.
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(lc.getLanguage().saveFileAs());
			File file = fileChooser.showOpenDialog(primaryStage);
			if(file != null)
			{
				try {
					FTPClientGUI.this.activeProject.setFilePath(file.getAbsolutePath());
					FTPClientGUI.this.activeProject.deserialize(FTPClientGUI.this.activeProject.getFilePath());
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Method to close actual active project.
		 */
		@FXML protected void closeProjectHandler()
		{
			FTPClientGUI.this.activeProject = null;
			
			//Update GUI.
			updateDetailList();
			updateDirectoryTree();
			updateProjectTree();
			updateMenu();
		}

		
	}
	
	/**
	 * Active project of FTP servers.
	 */
	private FTPProject activeProject;
	
	//FX controller.
	private FXController controller;
	
	/**
	 * Debug flag.
	 */
	public static Boolean DEBUG = true;	
	
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
			Scene scene = new Scene(this.controller,900,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("FTP Client");
			
			//Init update.
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
		if((args.length > 0) && ("DEBUG".equals(args[0])))
		{
			FTPClientGUI.DEBUG = true;
		}
		launch(args);
	}
}
