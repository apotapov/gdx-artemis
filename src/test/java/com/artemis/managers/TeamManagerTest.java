package com.artemis.managers;

import com.artemis.World;
import junit.framework.Assert;
import org.junit.Test;

public class TeamManagerTest {

    private static final String PLAYER_1 = "p1";
    private static final String PLAYER_2 = "p2";
    private static final String PLAYER_3 = "p3";

    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";

    @Test
    public void testSimple() {
        World world = new World();
        world.setManager(new TeamManager());
        world.initialize();

        TeamManager manager = world.getManager(TeamManager.class);

        Assert.assertNull(manager.getTeam(PLAYER_2));
        Assert.assertEquals(0, manager.getPlayers(PLAYER_1).size);

        manager.setTeam(PLAYER_1, TEAM_1);

        Assert.assertEquals(TEAM_1, manager.getTeam(PLAYER_1));
        Assert.assertEquals(1, manager.getPlayers(TEAM_1).size);
        Assert.assertEquals(0, manager.getPlayers(TEAM_2).size);

        manager.setTeam(PLAYER_1, TEAM_2);

        Assert.assertEquals(TEAM_2, manager.getTeam(PLAYER_1));
        Assert.assertEquals(1, manager.getPlayers(TEAM_2).size);
        Assert.assertEquals(0, manager.getPlayers(TEAM_1).size);

        manager.removeFromTeam(PLAYER_1);
        manager.removeFromTeam(PLAYER_2);

        Assert.assertNull(manager.getTeam(PLAYER_1));
        Assert.assertEquals(0, manager.getPlayers(TEAM_1).size);
        Assert.assertEquals(0, manager.getPlayers(TEAM_2).size);

        manager.setTeam(PLAYER_1, TEAM_2);
        manager.setTeam(PLAYER_1, TEAM_2);
        Assert.assertEquals(TEAM_2, manager.getTeam(PLAYER_1));
        Assert.assertEquals(1, manager.getPlayers(TEAM_2).size);
        Assert.assertEquals(0, manager.getPlayers(TEAM_1).size);


        manager.setTeam(PLAYER_1, TEAM_1);
        manager.setTeam(PLAYER_2, TEAM_2);
        manager.setTeam(PLAYER_3, TEAM_2);

        Assert.assertEquals(TEAM_1, manager.getTeam(PLAYER_1));
        Assert.assertEquals(TEAM_2, manager.getTeam(PLAYER_2));
        Assert.assertEquals(TEAM_2, manager.getTeam(PLAYER_3));
        Assert.assertEquals(1, manager.getPlayers(TEAM_1).size);
        Assert.assertEquals(2, manager.getPlayers(TEAM_2).size);
    }

}
