package com.artemis.managers;

import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;


/**
 * Use this class together with PlayerManager.
 * 
 * You may sometimes want to create teams in your game, so that
 * some players are teammates.
 * 
 * A player can only belong to a single team.
 * 
 * @author Arni Arent
 *
 */
public class TeamManager extends Manager {

    private final Array<String> EMPTY_PLAYERS_ARRAY = new Array<String>();

    protected ObjectMap<String, Array<String>> playersByTeam;
    protected ObjectMap<String, String> teamByPlayer;

    protected Pool<Array<String>> stringArrayPool;

    public TeamManager() {
        playersByTeam = new ObjectMap<String, Array<String>>();
        teamByPlayer = new ObjectMap<String, String>();

        stringArrayPool = new Pool<Array<String>>() {
            @Override
            protected Array<String> newObject() {
                return new SafeArray<String>();
            }
        };
    }

    /**
     * Returns a team for the specified player.
     * 
     * @param player Player to get the team for.
     * @return Player's team, null if none.
     */
    public String getTeam(String player) {
        return teamByPlayer.get(player);
    }

    /**
     * Assigns a player to the specified team. Removes
     * the player from any other team.
     * 
     * @param player The player to assign to the team.
     * @param team Player's team.
     */
    public void setTeam(String player, String team) {
        removeFromTeam(player);

        teamByPlayer.put(player, team);

        Array<String> players = playersByTeam.get(team);
        if(players == null) {
            players = stringArrayPool.obtain();
            playersByTeam.put(team, players);
        }
        players.add(player);
    }

    /**
     * Get all the players belonging to the specified team.
     * 
     * WARNING: the array should not be modified.
     * 
     * @param team Team that the players belong to.
     * @return A list of players on the team, empty array if none.
     */
    public Array<String> getPlayers(String team) {
        Array<String> players = playersByTeam.get(team);
        if (players == null) {
            return EMPTY_PLAYERS_ARRAY;
        }
        return players;
    }

    /**
     * Remove specified player from the team that they are on.
     * 
     * @param player Player to remove from the team.
     */
    public void removeFromTeam(String player) {
        String team = teamByPlayer.remove(player);
        if(team != null) {
            Array<String> players = playersByTeam.get(team);
            if(players != null) {
                players.removeValue(player, true);
                if (players.size == 0) {
                    stringArrayPool.free(playersByTeam.remove(team));
                }
            }
        }
    }

    @Override
    public void dispose() {
        playersByTeam.clear();
        teamByPlayer.clear();
    }
}
