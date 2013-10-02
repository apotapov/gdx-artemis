package com.badlogic.gdx.artemis.managers;

import com.badlogic.gdx.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


/**
 * Use this class together with PlayerManager.
 * 
 * You may sometimes want to create teams in your game, so that
 * some players are team mates.
 * 
 * A player can only belong to a single team.
 * 
 * @author Arni Arent
 *
 */
public class TeamManager extends Manager {
    protected ObjectMap<String, Array<String>> playersByTeam;
    protected ObjectMap<String, String> teamByPlayer;

    public TeamManager() {
        playersByTeam = new ObjectMap<String, Array<String>>();
        teamByPlayer = new ObjectMap<String, String>();
    }

    public String getTeam(String player) {
        return teamByPlayer.get(player);
    }

    public void setTeam(String player, String team) {
        removeFromTeam(player);

        teamByPlayer.put(player, team);

        Array<String> players = playersByTeam.get(team);
        if(players == null) {
            players = new SafeArray<String>();
            playersByTeam.put(team, players);
        }
        players.add(player);
    }

    public Array<String> getPlayers(String team) {
        return playersByTeam.get(team);
    }

    public void removeFromTeam(String player) {
        String team = teamByPlayer.remove(player);
        if(team != null) {
            Array<String> players = playersByTeam.get(team);
            if(players != null) {
                players.removeValue(player, true);
            }
        }
    }

}
