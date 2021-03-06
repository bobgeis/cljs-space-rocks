
This is a learning project, so take the "versions" with a grain of salt.

## [0.2.1] 2018-08-22

* Added powerups triggered by delivering loot to a base.  The refinery gives a rapid shot and the hospital gives a spread shot.
* Some update code was modified to use transients.
* Saved games are now loaded by default.


## [0.2.0] 2018-08-16

* Added the ability to save and load games using local storage.  To save, pause a running game with P or Enter.  To load a game, reload or return the splash screen.  In both cases, there should be buttons in the lower right section of the play area that say "Save Current Game", "Load Saved Game" and "Clear Saved Game".


## [0.1.1] 2018-08-13

* The game SVG scales to the size of the browser window.
* The SVG coordinate system has been changed to allow rounding of decimals values to integers.  This should make the total SVG smaller.
* Time travel can now be done in increments of less than 13 seconds.  Hold Z to play the timeline in reverse and release Z to jump to that moment.  Press X during playback to abort the jump.
* Some game objects near the edge of the play area now wrap (appear on both sides).
* Added traveling ships to protect.
* Traveler ships can drop escape pods that, like the gems dropped by rocks, the player can catch.
* Added a hospital station that the escape pods can be delivered to.
* Rocks will spawn more quickly as more ships transit the play area successfully.
* Ships will spawn more quickly as the player delivers more gems or pods to their respective stations.
* Ships now have svg emblems
* Updated thoughts markdown files.


## [0.1.0] 2018-07-23

Minimally playable.

Current features:
* Player ship moves and shoots.
* Rocks spawn and break if shot.
* Time travel works.
* Rocks can drop gems.
* Player can pick up gems and drop them off at the base.

