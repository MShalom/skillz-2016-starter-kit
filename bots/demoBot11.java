package bots;
import java.util.List;
import java.util.ArrayList;
import pirates.game.*;


public class MyBot implements PirateBot {

    private class BoardStatus {
        public Pirate Pirate;
        public Treasure Treasure;
        public Script Script;
    }
    
    private class PirateTactics {
        public Pirate Pirate;
        public Location FinalDestination;
        public Location TempDestination;
        public int Moves;
    }
    
    @Override
    public void doTurn(PirateGame game) {
        BoardStatus status = getBoardStatus(game);
        PirateTactics tactics = assignTargets(game, status);
        takeAction(game, tactics);
    }
    
    private BoardStatus getBoardStatus(PirateGame game) {
        BoardStatus status = new BoardStatus();
        status.Pirate = game.myPirates().get(game.myPirates().size()-1);
        game.debug("pirate: " + status.Pirate.getId());
        status.Treasure = game.treasures().get(game.treasures().size()-1);
        game.debug("treasure: " + status.Treasure.getId());
        status.Script = GetAvailableScript(game);
        return status;
    }
    
    private boolean tryDefend(PirateGame game, Pirate pirate) {
	if (game.getDefenseReloadTurns() > 0) return false;
        for (Pirate enemy : game.enemyPirates()) {
            if (game.inRange(pirate,  enemy)) {
                game.defend(pirate);
                return true;
            }
        }
        return false;
    }

    private boolean tryAttack(PirateGame game, Pirate pirate) {
	if (game.getReloadTurns() > 0) return false;
        for (Pirate enemy : game.enemyPirates()) {
            if (game.inRange(pirate,  enemy)) {
                game.attack(pirate, enemy);
                return true;
            }
        }
        return false;
    }

    
    private PirateTactics assignTargets(PirateGame game, BoardStatus status) {
        PirateTactics tactics = new PirateTactics();
        tactics.Pirate = status.Pirate;
        tactics.Moves = game.getActionsPerTurn();
		if (status.Script != null)
		{
			tactics.FinalDestination = status.Script.getLocation();
		}
        else if (!tactics.Pirate.hasTreasure()) {        
            tactics.FinalDestination = status.Treasure.getLocation();
        }
        else {
            tactics.Moves = 1;
            tactics.FinalDestination = status.Pirate.getInitialLocation();
        }
        List<Location> possibleLocations = game.getSailOptions(tactics.Pirate, tactics.FinalDestination, tactics.Moves);
        List<Location> safeLocations = GetSafeLocations(game, possibleLocations);
        tactics.TempDestination = possibleLocations.get(0);
        return tactics;
    }

    
    private void takeAction(PirateGame game, PirateTactics tactics) {
		if (TryBermuda(game, tactics.Pirate) == true) {
			return;
		}

        if (tryDefend(game, tactics.Pirate) == true) {
            return;
        }
        if (tryAttack(game, tactics.Pirate) == true) {
            return;
        }

		for (Script anti_script : game.x()) {
		    if (anti_script.getLocation().row == tactics.TempDestination.row && anti_script.getLocation().col == tactics.TempDestination.col) {
		        return;
		    }
			game.debug("row:%d, col:%d, size:%d", game.x().get(0).getLocation().row, game.x().get(0).getLocation().col, game.x().size());
		}

        game.setSail(tactics.Pirate, tactics.TempDestination);
    }
	
	
	private boolean TryBermuda(PirateGame game, Pirate pirate)
	{
		if (game.getMyBermudaZone() == null && game.getMyScriptsNum() >= game.getRequiredScriptsNum())
		{
			game.summonBermudaZone(pirate);
			return true;
		}
		return false;
	}

	private List<Location> GetSafeLocations(PirateGame game, List<Location> locations)
	{
		List<Location> safeLocations = new ArrayList<Location>();
		for (Location loc : locations)
		{
			if (!game.inEnemyBermudaZone(loc))
			{
				safeLocations.add(loc);
			}
		}
		return safeLocations;
	}

	private Script GetAvailableScript(PirateGame game)
	{
		if (game.scripts().size() > 0)
		{
			return game.scripts().get(0);
		}
		return null;
	}
}

