package application;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import application.App;

public class Main extends Application {
	private ArrayList<Task> list = new ArrayList<>();
	private boolean free = true;
	private String summaryStr = "";
	private ObservableList<String> items = FXCollections.observableArrayList();

	@Override
	public void start(Stage primaryStage) {
		try {
			// pane for creating the Tasks
			VBox vbox = new VBox(15);
			vbox.setPadding(new Insets(15, 15, 15, 15));

			TextField field = new TextField();
			Button newTaskbtn = new Button("New Task");
			Button summary = new Button("Summary");
			Label activeTaskLabel = new Label("Active Task: ");
			Label activeTask = new Label("");
			TextArea tarea = new TextArea();
			
			vbox.getChildren().addAll(new Label("New Task:"), field, newTaskbtn, summary, activeTaskLabel, activeTask);

			HBox searchContainer = new HBox(5);
			
			FilteredList<String> data = new FilteredList<>(items, s -> true);
			TextField searchBar = new TextField();
			searchBar.setMinWidth(345);
			
			// searches the Filtered list with the text in the text field, shows the resulting match 
			searchBar.textProperty().addListener(obs -> {
				String filter = searchBar.getText();
				if (filter == null || filter.length() == 0) {
					data.setPredicate(s -> true);
				} else
					data.setPredicate(s -> s.contains(filter));
			});
			// holds all the tasks
			ListView<String> lview = new ListView<>(data);
			lview.setPrefSize(20, 110);
			
			Button searchBtn = new Button("Search");
			
			searchContainer.getChildren().addAll(searchBar, searchBtn);
			
			VBox paneforListView = new VBox(10);
			paneforListView.setPadding(new Insets(1, 10, 10, 10));
			paneforListView.getChildren().addAll(searchContainer,  lview);

			
			searchBtn.setOnAction(e->{
				if(searchBar.getText() != null || !searchBar.getText().isEmpty()) {
					String task= searchBar.getText();
					String result = App.findByName(task);
					tarea.setText(result);
				}
			});
			
			// register and handle 'new Task' button
			newTaskbtn.setOnAction(e -> {
				// make sure not to make duplicates
				if (!items.contains(field.getText()) && field.getText() != "") {
					list.add(new Task(field.getText()));
					items.add(field.getText());
					System.out.println("successful insert: "+App.insertTask(list.get(list.size()-1)));
				}
//				System.out.println(list.toString());
				field.setText("");
			});

			field.setOnKeyReleased(e->{
				if(e.getCode() == KeyCode.ENTER) {
					System.out.println(e.getText());
					if(!items.contains(field.getText()) && field.getText() != "") {
						list.add(new Task(field.getText()));
						items.add(field.getText());
						System.out.println("successful insert: "+App.insertTask(list.get(list.size()-1)));
					}
					field.setText("");
//					System.out.println(list.toString());
				}
			});
			// displays the Task times
			
			tarea.setEditable(true);
			tarea.setPrefColumnCount(5);
			tarea.setPrefRowCount(6);
			tarea.setWrapText(true);
			tarea.setStyle("-fx-padding: 5px; -fx-border-insets: 5px;-fx-background-insets: 5px;");

			// set action on summary button
			summary.setOnAction(e -> {
				summaryStr = "";
				long totalTime = 0;
				String s = "";
				for (Task i : list) {
					summaryStr += i.toString() + "\n";
					totalTime += i.getTotalTimeOnly();
				}
//				System.out.println(map.toString());
				if (totalTime == 0)
					s += totalTime + "";
				if (totalTime >= 3.6e6)
					s += (totalTime / 1000) / 60 / 60 % 24 + " hours " + ((totalTime / 1000) / 60) % 60 + " minutes"; // returns
																														// hours
																														// and
																														// minutes
				else if ((totalTime / 1000) >= 60)
					s += (totalTime / 1000) / 60 + " minutes " + totalTime / 1000 % 60 + " seconds"; // returns minutes
																										// and seconds
				else
					s += (totalTime / 1000) + " seconds"; // returns seconds
				tarea.setText(summaryStr + "Total Time for all is " + s);
				
			});

			// pane for Task button "start", "stop", and 'clear Task' btns
			HBox Taskbtns = new HBox(15);

			// add listener to ListView
			lview.getSelectionModel().selectedItemProperty().addListener(ov -> {
				int i = 0;
				for (String s : items) {
					if (s == lview.getSelectionModel().getSelectedItem())
						i = items.indexOf(s);
				}
				// creates temp Task
				Task temp = list.get(i);
				// gets the description about the Task
				if (list.size() > 0) {

					tarea.setText(temp.toString());

					// shows the 'start' and 'stop' buttons
					Taskbtns.getChildren().clear();
					Taskbtns.getChildren().addAll(temp.getStartBtn(), temp.getStopBtn(),
							temp.getRefreshBtn(), temp.getClearTaskBtn(), temp.getDeleteTaskBtn());

					temp.getStartBtn().setOnAction(e -> {
						if (free) {
							temp.setActive(true);
							free = false;
							temp.setStartTime();
							
							activeTask.setText(temp.getTitle());
						}
					});

					temp.getStopBtn().setOnAction(e -> {
						if (!free && temp.getTitle() == activeTask.getText()) {
							temp.setActive(false);
							free = true;
							temp.setStopTime();
							activeTaskLabel.setText("Active Task: ");
							activeTask.setText("");
							tarea.setText(temp.toString());
							System.out.println("successful update: "+App.update(temp));
						}

					});

					temp.getRefreshBtn().setOnAction(e -> {
							tarea.setText(temp.toString());
					});

					temp.getClearTaskBtn().setOnAction(e -> {
						free = true;
						int index = items.indexOf(lview.getSelectionModel().getSelectedItem());
						if (items.size() > 1) {
							items.remove(index);
							list.remove(index);
						} else if (items.size() == 1) {
							items.clear();
							list.clear();
							lview.getSelectionModel().clearSelection();
							lview.setItems(items);
						}
						tarea.setText(" ");

					});
					
					temp.getDeleteTaskBtn().setOnAction(e->{
						free = true;
						int index = items.indexOf(lview.getSelectionModel().getSelectedItem());
						if(items.size() != 0)
							System.out.println("successful delete: "+App.deleteTask(list.get(index)));
						if(items.size()>1) {
							items.remove(index);
							list.remove(index);
						} else if(items.size() == 1) {
							items.clear();
							list.clear();
							lview.getSelectionModel().clearSelection();
							lview.setItems(items);
						}
						tarea.setText("");
					});
					
				}

			});

			BorderPane root = new BorderPane();
			// colours #15344f and #007ce7, #eff2f8, #bccace, #1ac876 -> for buttons
//			root.setStyle("-fx-background-color: #007ce7");
			root.setPadding(new Insets(15, 15, 15, 10));
			root.setRight(vbox);
			root.setCenter(tarea);
			root.setTop(paneforListView);
			root.setBottom(Taskbtns);
//			root.set(Color.RED);

			Scene scene = new Scene(root, 450, 450);
			scene.setFill(Color.RED);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Task Tracker");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
