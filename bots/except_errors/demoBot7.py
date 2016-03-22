
import traceback, sys

def do_turn(game):
    try:
        inner_do_turn(game)
    except:
        traceback.print_exc(file=sys.stdout)

def inner_do_turn(game):
    pirate, treasure = get_board_status(game)
    locations = assign_targets(game, pirate, treasure)
    take_action(game, pirate, locations)


def get_board_status(game):
    pirate = game.my_pirates()[0]
    game.debug("pirate: " + str(pirate.id))
    treasure = game.treasures()[-1]
    game.debug("treasure: " + str(treasure.id))
    return pirate, treasure


def assign_targets(game, pirate, treasure):
    if not pirate.has_treasure:
        moves = game.get_actions_per_turn()
        locations = game.get_sail_options(pirate, treasure.location, moves)
    else:
        moves = 1
        locations = game.get_sail_options(pirate, pirate.initial_loc, moves)
    return locations


def take_action(game, pirate, locations):
    if try_defend(game, pirate):
        return
    if try_attack(game, pirate):
        return
    game.set_sail(pirate, locations[0])


def try_defend(game, pirate):
    for enemy in game.enemy_pirates():
        if game.in_range(pirate, enemy):
            game.defend(pirate)
            return True
    return False


def try_attack(game, pirate):
    for enemy in game.enemy_pirates():
        if game.in_range(pirate, enemy):
            game.attack(pirate, enemy)
            return True
    return False
