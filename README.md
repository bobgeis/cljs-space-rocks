
# cljs-space-rocks

This is a learning project made while learning clojurescript and SVGs.  It's an asteroids clone, because whenever one learns a new language it's fun to make a game in it, and I happen to like asteroids.

[__Play it here__](http://bobgeis.github.io/cljs-space-rocks)

## Controls

Keyboard:
* Arrow keys or WASD to move the player ship.
* Space or Shift to fire the disintegrator.
* Z to operate the Omega-13.  Hold Z to show previous states of the timeline, then release to jump to that moment.  You can go back up to 13 seconds in this way. "Enough time to undo one mistake." ~Commander Taggart
* X to abort a time jump.  That is: if you decide not to jump after all, then press X before releasing Z, and the time jump will not occur.
* P pauses and unpauses the game.
* Enter starts the game or restarts the game.
* Escape, while paused, goes back to the start screen.
* U resets the high score.
* L logs the game state to the js console (for debugging).
* While the game is paused, there will be a button in the lower right that will save the current game to local storage.  Next time the page is loaded, the saved game will be restored.

## Objectives

Oh no! Some hooligans are dumping space rocks into Subspace Locus 1457 again!

Luckily, a dedicated rescue and rock-buster ship is already prepped and on site.  That's you!

* Try to keep Subspace Locus 1457 safe for travelers by busting rocks.
* Bring any escape pods you rescue to the hospital station in the upper right.  You will be rewarded with a temporary enhancement to your rock buster.
* Bring any valuable minerals you happen to collect to refinery base in the lower left.  You will be rewarded with a temporary enhancement to your rock buster.
* If you get into a jam or if you have to abandon ship, use the Omega-13 to try again.
* Good luck!

## Attributions

Background image of Carina Nebula is available [here](https://commons.wikimedia.org/w/index.php?search=carina+nebula&title=Special:Search&go=Go&uselang=en&searchToken=79al97qlirmupg5bpga22jvj2#/media/File:Carina_Nebula.jpg).  Credit to [ESO/T. Preibisch](http://www.eso.org/public/images/eso1208a/) used under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).

## Developers

Download or clone this repo onto your local drive, and cd into the folder containing project.clj.

For a hot reloading dev server do:
```lein figwheel```
or
```lein repl```
and then
```(start)``
and open your browser at: http://localhost:3449/index.html

To make a minified version, do:
```lein do clean, cljsbuild once min```
and verify it functions by opening your browser at: resources/public/index.html

