package com.funex.scoutapp.model;

import java.util.Date;
import java.util.List;

public class Game {
	private String id;
	private Team home;
	private Team away;
	private Date scheduledDate;
	private Date startTime;
	private Date endTime;
	private boolean firstHalfFinished = false;
	private boolean secondHalfFinished = false;
	private boolean gameRunning = false;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Team getHome() {
		return home;
	}
	public void setHome(Team home) {
		this.home = home;
	}
	public Team getAway() {
		return away;
	}
	public void setAway(Team away) {
		this.away = away;
	}
	public Date getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(Date date) {
		this.scheduledDate = date;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public boolean isFirstHalfFinished() {
		return firstHalfFinished;
	}
	public void setFirstHalfFinished(boolean firstHalfFinished) {
		this.firstHalfFinished = firstHalfFinished;
	}
	public boolean isSecondHalfFinished() {
		return secondHalfFinished;
	}
	public void setSecondHalfFinished(boolean secondHalfFinished) {
		this.secondHalfFinished = secondHalfFinished;
	}
	public boolean isGameRunning() {
		return gameRunning;
	}
	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}
	
	

}
