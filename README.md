
# cljs-space-rocks

This is a learning project made while learning clojurescript.  It's an asteroids clone, because whenever one learns a new language it's fun to make a game in it, and I happen to like asteroids.

[__Play it here__](http://bobgeis.github.io/cljs-space-rocks)

## Controls

Keyboard:
* Arrow keys or WASD to move the player ship.
* Space or Shift to fire the disintigrator.
* Z activated the Omega 13.  Once fully charged, the player can go back in time 13 seconds "Enough time to undo a single mistake."  Note that if it's fully charged when the player ship is destroyed, it remains available after game over.
* P pauses and unpauses the game.
* Enter starts the game or restarts the game.
* Escape, while paused, goes back to the start screen.
* U resets the high score.
* L logs the game state to the js console (for debugging).

## Objectives

Oh no! Some hooligans are dumping space rocks into Subspace Locus 1457 again!

Luckily, a dedicated rescue and rock-buster ship is already prepped and on site.  That's you!

* Try to keep Subspace Locus 1457 safe for travelers, by busting rocks.
* Bring any valuable minerals you happen to collect to refinery base in the South West quadrant.
* If you get into a jam or if you have to abandon ship, use the Omega-13 to try again.
* Good luck!

## Attributions

Background image of Carina Nebula is available [here](https://commons.wikimedia.org/w/index.php?search=carina+nebula&title=Special:Search&go=Go&uselang=en&searchToken=79al97qlirmupg5bpga22jvj2#/media/File:Carina_Nebula.jpg).  Credit to [ESO/T. Preibisch](http://www.eso.org/public/images/eso1208a/) used under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).

## Developers

Download or clone this repo onto your local drive, and cd into the folder containing project.clj.

For a hot reloading dev server do:
```lein figwheel```
and open your browser at: http://localhost:3449/index.html

To make a minified version, do:
```lein do clean, cljsbuild once min```
and verify it functions by opening your browser at: resources/public/index.html

