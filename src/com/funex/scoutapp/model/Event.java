package com.funex.scoutapp.model;

import com.funex.scoutapp.model.Team.TEAM;

public class Event {
	private String key;
	private TEAM team;
	private ACTION action;
	
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

	public TEAM getTeam() {
		return team;
	}

	public void setTeam(TEAM team) {
		this.team = team;
	}

	public ACTION getAction() {
		return action;
	}

	public void setAction(ACTION action) {
		this.action = action;
	}
    
	public enum ACTION {
		SAFE,
		ATTACK,
		CORNER,
		FREEKICK,
		YELLOWCARD,
		REDCARD,
		OFFSIDE,
		GOAL,
		PENALTY,
		SHOTONGOAL,
		STARTFIRSTHALF,
		ENDFIRSTHALF,
		STARTSECONDHALF,
		ENDSECONDHALF
	}
}
