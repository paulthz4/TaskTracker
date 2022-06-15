package application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.scene.control.Button;

public class Task {
	private boolean active;
	private LocalDateTime myDateObj = LocalDateTime.now();
	private LocalDate date = LocalDate.now();
	private long startTime = 0;
	private long stopTime;
	private long totalTime;
	private int stoppages = 0;
	private List<String> stoppageTimes = new ArrayList<>();
	private String title;
	private Button start = new Button("Start");
	private Button stop = new Button("Stop");
	private Button refresh = new Button("Refresh");
	private Button clearTask = new Button("RemoveTask");
	private Button deleteTask = new Button("Delete Task");
	private Stack<Long> timeList = new Stack<>();

	public Task() {
		active = false;
		title = "Task " + this.getClass();
	}

	public Task(String title) {
		active = false;
		this.title = title;
	}

	// return time in hh:mm format
	private String convertTime(long time) {
		if (time == 0)
			return time + "00:00:00";
		// returns hours and minutes
		if (time >= 3.6e6)
			return (time / 1000) / 60 / 60 + ":" + ((time / 1000) / 60) % 60 + ":" + time/100 % 60;
		// returns minutes and seconds
		else if ((time / 1000) >= 60)
			return "00:" + (time / 1000) / 60 + ":" + time / 1000 % 60 ;
		// returns seconds
		else
			return "00:00:" + (time / 1000);
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean a) {
		active = a;
	}

	public String getDateAndTimeCreated() {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
		String date = myDateObj.format(myFormatObj);
		return date;
	}

	public String getTimeCreated() {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
		String time = myDateObj.format(myFormatObj);
		return time;
	}

	public String getDateCreated() {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy MM dd E MMM");
		String formattedDate = myDateObj.format(myFormatObj);
		return formattedDate;
	}

	public String getTotalTime() {
		if (totalTime == 0)
			return totalTime + "";
		// returns hours and minutes
		if (totalTime >= 3.6e6)
			return (totalTime / 1000) / 60 / 60 + " hours " + ((totalTime / 1000) / 60) % 60 + " minutes";
		// returns minutes and seconds
		else if ((totalTime / 1000) >= 60)
			return (totalTime / 1000) / 60 + " minutes " + totalTime / 1000 % 60 + " seconds";
		// returns seconds
		else
			return (totalTime / 1000) + " seconds";
	}

	public Long getTotalTimeOnly() {
		return totalTime;
	}

	public String getTimeWorked() {
		long time = 0;
		if (active) {
			// the start buttons has been pressed but the stop btn hasn't been pressed
			time = System.currentTimeMillis() - startTime;

			if (time == 0)
				return time + "";
			if (time >= 3.6e6)
				return (time / 1000) / 60 / 60 % 24 + " hours " + ((time / 1000) / 60) % 60 + " minutes"; // returns
																											// hours and
																											// minutes
			else if ((time / 1000) >= 60)
				return (time / 1000) / 60 + " minutes " + time / 1000 % 60 + " seconds"; // returns minutes and seconds
			else
				return (time / 1000) + " seconds"; // returns seconds

		}
		// if the Task is inactive (free)
		else {
			if (time == 0)
				return time + "";
			if (time >= 3.6e6)
				return (time / 1000) / 60 / 60 % 24 + " hours " + ((time / 1000) / 60) % 60 + " minutes"; // returns
																											// hours and
																											// minutes
			else if ((time / 1000) >= 60)
				return (time / 1000) / 60 + " minutes " + time / 1000 % 60 + " seconds"; // returns minutes and seconds
			else
				return (time / 1000) + " seconds"; // returns seconds
		}
	}

	public void setStopTime() {
		stopTime = System.currentTimeMillis();
		timeList.push(stopTime - startTime);
		stoppageTimes.add(convertTime(stopTime - startTime));
		totalTime += timeList.pop();
		stopTime = 0;
		stoppages++;
	}

	public void setStartTime() {
		startTime = System.currentTimeMillis();
	}
	
	public int getStoppages() {
		return stoppages;
	}
	
	public List<String> getStoppageTimesList(){
		return this.stoppageTimes;
	}

	public String getTitle() {
		return title;
	}

	public Button getStartBtn() {
//		start.setStyle("-fx-text-fill: #22C628");
		return start;
	}

	public Button getStopBtn() {
		return stop;
	}

	public Button getRefreshBtn() {
		return refresh;
	}

	public Button getClearTaskBtn() {
		clearTask.setStyle("-fx-text-fill: #F43838");
		return clearTask;
	}

	public Button getDeleteTaskBtn() {
		deleteTask.setStyle("-fx-color:  #F43838");
		return deleteTask;
	}

	@Override
	public String toString() {
		String str = "Task name: " + this.getTitle() + "\n Date created: " + this.getDateCreated() + " "
				+ this.getTimeCreated() + "\n Time worked: " + this.getTimeWorked() + "\n Total Time: "
				+ this.getTotalTime() + "\n";
		return str;
	}
}
