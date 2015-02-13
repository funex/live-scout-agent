package com.funex.scoutapp.model;

public class Team {
	public enum TEAM{
		HOME, AWAY
	}
	private TEAM team;
	private String name;
	private int goals;
	private int yellowCards;
	private int redCards;
	
	public Team() {
		this.goals = 0;
		this.redCards = 0;
		this.yellowCards = 0;
	}
	
	public Team(TEAM team, String name, int goals, int yellowCards, int redCards) {
		this.team = team;
		this.name = name;
		this.goals = goals;
		this.yellowCards = yellowCards;
		this.redCards = redCards;
	}

	public TEAM getTeam() {
		return team;
	}

	public void setTeam(TEAM team) {
		this.team = team;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGoals() {
		return goals;
	}

	public void setGoals(int goals) {
		this.goals = goals;
	}

	public int getYellowCards() {
		return yellowCards;
	}

	public void setYellowCards(int yellowCards) {
		this.yellowCards = yellowCards;
	}

	public int getRedCards() {
		return redCards;
	}

	public void setRedCards(int redCards) {
		this.redCards = redCards;
	}

	
}
