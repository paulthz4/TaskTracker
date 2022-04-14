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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import application.App;

public class Main extends Application {
	private ArrayList<Case> list = new ArrayList<>();
	private boolean free = true;
	private String summaryStr = "";
	private ObservableList<String> items = FXCollections.observableArrayList();

	@Override
	public void start(Stage primaryStage) {
		try {
			// pane for creating the cases
			VBox vbox = new VBox(15);
			vbox.setPadding(new Insets(15, 15, 15, 15));

			TextField field = new TextField();
			Button newcasebtn = new Button("New Case");
			Button summary = new Button("Summary");
			Label activeCaseLabel = new Label("Active case: ");
			Label activeCase = new Label("");

			vbox.getChildren().addAll(new Label("New Case:"), field, newcasebtn, summary, activeCaseLabel, activeCase);

			// create ListView
			FilteredList<String> data = new FilteredList<>(items, s -> true);
			TextField searchBar = new TextField();
			searchBar.textProperty().addListener(obs -> {
				String filter = searchBar.getText();
				if (filter == null || filter.length() == 0) {
					data.setPredicate(s -> true);
				} else
					data.setPredicate(s -> s.contains(filter));
			});
			ListView<String> lview = new ListView<>(data);
			lview.setPrefSize(20, 110);

			VBox paneforListView = new VBox(10);
			paneforListView.setPadding(new Insets(1, 10, 10, 10));
			paneforListView.getChildren().addAll(searchBar,  lview);

			// register and handle 'new case' button
			newcasebtn.setOnAction(e -> {
				// make sure not to make duplicates
				if (!items.contains(field.getText()) && field.getText() != "") {
					list.add(new Case(field.getText()));
					items.add(field.getText());
					App.insertTask(list.get(list.size()-1));
				}
				System.out.println(list.toString());
				field.setText("");
			});

			field.setOnKeyReleased(e->{
				if(e.getCode() == KeyCode.ENTER) {
					System.out.println(e.getText());
					if(!items.contains(field.getText()) && field.getText() != "") {
						list.add(new Case(field.getText()));
						items.add(field.getText());
					}
					field.setText("");
					System.out.println(list.toString());
				}
			});
			// displays the case times
			TextArea tarea = new TextArea();
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
				for (Case i : list) {
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

			// pane for case button "start", "stop", and 'clear case' btns
			HBox casebtns = new HBox(15);

			// add listener to ListView
			lview.getSelectionModel().selectedItemProperty().addListener(ov -> {
				int i = 0;
				for (String s : items) {
					if (s == lview.getSelectionModel().getSelectedItem())
						i = items.indexOf(s);
				}
				// creates temp case
				Case temp = list.get(i);
				// gets the description about the case
				if (list.size() > 0) {

					tarea.setText(temp.toString());

					// shows the 'start' and 'stop' buttons
					casebtns.getChildren().clear();
					casebtns.getChildren().addAll(temp.getStartBtn(), temp.getStopBtn(),
							temp.getRefreshBtn(), temp.getClearCaseBtn());

					temp.getStartBtn().setOnAction(e -> {
						if (free) {
							temp.setActive(true);
							free = false;
							temp.setStartTime();
							activeCase.setText(temp.getTitle());
						}
					});

					temp.getStopBtn().setOnAction(e -> {
						if (!free && temp.getTitle() == activeCase.getText()) {
							temp.setActive(false);
							free = true;
							temp.setStopTime();
							activeCase.setText("");
							tarea.setText(temp.toString());
						}

					});

					temp.getRefreshBtn().setOnAction(e -> {
							tarea.setText(temp.toString());
					});

					temp.getClearCaseBtn().setOnAction(e -> {
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
				}

			});

			BorderPane root = new BorderPane();
			// colours #15344f and #007ce7, #eff2f8, #bccace, #1ac876 -> for buttons
//			root.setStyle("-fx-background-color: #007ce7");
			root.setPadding(new Insets(15, 15, 15, 10));
			root.setRight(vbox);
			root.setCenter(tarea);
			root.setTop(paneforListView);
			root.setBottom(casebtns);

			Scene scene = new Scene(root, 450, 450);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Vention Case Tracker");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
