package bots;
import java.util.List;
import pirates.game.Direction;
import pirates.game.Location;
import pirates.game.Treasure;
import pirates.game.Pirate;
import pirates.game.PirateBot;
import pirates.game.PirateGame;

public class MyBot implements PirateBot {

    private class BoardStatus {
        public Pirate Pirate;
        public Treasure Treasure;
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
        status.Pirate = game.myPirates().get(0);
        game.debug("pirate: " + status.Pirate.getId());
        status.Treasure = game.treasures().get(0);
        game.debug("treasure: " + status.Treasure.getId());
        return status;
    }
    
    private boolean tryDefend(PirateGame game, Pirate pirate) {
        for (Pirate enemy : game.enemyPirates()) {
            if (game.inRange(pirate,  enemy)) {
                game.defend(pirate);
                return true;
            }
        }
        return false;
    }

    private boolean tryAttack(PirateGame game, Pirate pirate) {
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
        if (!tactics.Pirate.hasTreasure()) {        
            tactics.Moves = game.getActionsPerTurn();
            tactics.FinalDestination = status.Treasure.getLocation();
        }
        else {
            tactics.Moves = 1;
            tactics.FinalDestination = status.Pirate.getInitialLocation();
        }
        List<Location> possibleLocations = game.getSailOptions(tactics.Pirate, tactics.FinalDestination, tactics.Moves);
        tactics.TempDestination = possibleLocations.get(0);
        return tactics;
    }

    
    private void takeAction(PirateGame game, PirateTactics tactics) {
        if (tryDefend(game, tactics.Pirate) == true) {
            return;
        }
        if (tryAttack(game, tactics.Pirate) == true) {
            return;
        }
        game.setSail(tactics.Pirate, tactics.TempDestination);
    }
}

