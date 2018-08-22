
## Considerations

This is a learning project, so not everything here is best practice or well thought out.  The goal was to make something fairly complicated in cljs and also to get experience using SVGs.


## Some Anticipated Questions

* _What is this?_ This is basically a re-make of asteroids in JavaScript [(link)](https://github.com/bobgeis/js-space-rocks) which was a re-make of asteroids in Elm [(link)](https://github.com/bobgeis/LookOutSpaceRocks).  I tried to make them fairly similar to get a feeling for how each language compares.  Also in Elm I used the Collage library, in JavaScript I used canvas, and now in ClojureScript I'm using SVG.

* _Why make a game like asteroids using immutable data structures?_ Cljs data structures are immutable by default, and given how simple a game like asteroids is, the performance hit of using immutable data is probably not the limiting factor.  Also it makes it much simpler to implement a time travel mechanic.


## JS vs CLJS vs Elm

So I've made very similar asteroids games in three different functional languages.  This was intentional, to get a feel for how things worked in each of them.

* Elm - This was the fastest to develop, partly because it was the first and simplest version of the game.  It's performance wasn't that great, and Elm has had multiple breaking releases since then, so I'm not sure how my experience compares with current Elm.  It was nice knowing that if my code compiled, then it "worked" (more or less).

* JavaScript + React + Redux + Immutable - Using JavaScript can go many different directions, but I chose to use a more functional & immutable approach.  Using immutable data structures suggested the idea of a time travel mechanic in the game which was one of the simpler features to implement.  The hard part was dealing with all the small libraries and wiring them up correctly.  It seemed brittle.  Using canvas made the game perform well.

* ClojureScript + Reagent + Re-frame - I think if I were writing a game like this in cljs again, I would probably not use these libraries and SVGs: they are kind of overkill, and canvas would be a worthwhile tradeoff for better performance.  Having said that, I also wanted to get some experience using SVGs, so those were used despite the performance consequence.  Development took about the same amount of time as the JavaScript game, not counting the lead time spent getting a comfortable development workflow in a lisp.


## Architecture Notes

* core.cljs starts the app, calls functions that add listeners, render the application state to the dom, and then calls requestAnimationFrame in a loop.  Everything must be reachable from core.

* view.cljs has the central view code.  It takes the current mode, and calls the render functions that are currently needed.

* input.cljs adds the event listeners and dispatches keyboard input.

* reg.cljs registers a lot of re-frame event, fx, cofx, and sub handlers.

* model.cljs has the game model and manipulations thereof.  This namespace does a lot of work and deserves to be broken up more.

* helper.* are namespaces for helper functions that aren't specific to this implementation of asteroids.

* obj.*.cljs namespaces contain functions and data specific to particular types of game object (eg: the player, rocks, bullets, traveling ships, etc) as well as logic for handling interactions between object types (eg: colliding rocks with bullets).  The logic for creating, updating, and drawing each object is in the same file.  There is probably work that could be done to consolidate some of the logic that is very similar, and also separate the rendering/view code.

* misc.cljs has helper functions that _are_ specific to this implementation of asteroids.

* drand.cljs is a simple deterministi pseudorandom number generation library.  Note that it has side effects!  In practice, each game object that might need to call this has a seed, and before using the library, it would first set the seed.  The reason for using this over the built-in random functions, is to allow reproducibility when the player goes back in time.  That is: if it was nondeterministic, then if a rock spawned that the player didn't like, they could go back in time to before the spawn and see if they could get a better one, but with the deterministic randomness, the same rock should always spawn.

* id.cljs has some simple number wheel functions.  They are used to generate integer 'id's to identify instances of object maps, which are then assoced into a bigger collection map for all objects of that type.  The reason for this is to allow easy identification/update/removal of individual instances of object maps, which is mostly not necessary in this game but would have been if certain features were/are implemented.

* omega.cljs has logic related to time travel!  Basically the app state stores the current game map under the :scene key.  The omega logic periodically stores the current scene in a timeline sequence elsewhere in the app state.  When traveling back in time, it reads back over the timeline sequence and then picks out a particular scene and assocs it onto the app-state's :scene key.  Easy!

* text.cljs has logic for the text windows that appear overlaid on the game area.

* emblem.cljs has some simple logic for drawing svg paths for the symbols that appear on the ships and stations in game.


## Dependencies

* Reagent - a wrapper over React

* Re-frame - a state management library.  It works like an event queue to keep all state changes in one place.

* Specter - this is a library for manipulating large nested data structures in clojure.  I could probably have gotten by without it (my data structures aren't that large) but it was still handy.  It's a little strange to me that it has it's own NONE.

* Transit - used to serialize data for local storage.


## Things Learned

* Getting a dev setup that works with lisp, can be a lot of yak shaving.  If I continue to like clojure after this project, it might be worth investing in learning emacs.

* Parinfer (infer parentheses placement from indentation when writing in a lisp) is actually very handy, at least for newcomers.  It turns a language based on parentheses, into a language based on indentation (like python).  It is much easier to keep track of indentation than parentheses, at least without practice and/or other tools.  I'm not currently using parinfer because it was being buggy, but if it gets updated I intend to look again.  At one point a clojure programmer learned I was using parinfer and responded "Ew" or something to that effect :P

* SVGs look nicer than canvas (in my opinion), and are surprisingly performant (at least in small numbers, we'll see what happens as things get more complicated).

* At least in the naive implementation, large numbers of SVGs perform worse than canvas.

* It isn't necessary to specify a z-index to make html appear on top of the SVG (contrast with canvas, where it sometimes was necessary).

* Because the SVG's coordinate system is independent of the window's, you can specify a different coordinate system, and then us Math.floor to convert floats to ints.  This can make the SVG smaller than it would otherwise be.

* The game runs well on my dev laptop in chrome, but not so well in firefox, and not so well on older/slower machines.  I think this is largely due to my use of SVGs instead of canvas (the javascript version of this game runs much better in those cases).  Because this is a project for learning SVGs as well as cljs, I don't intend to switch it, but I might be able to make more performant SVGs and/or implement a "low graphics" option.

* In an asteroids-like game, thirteen seconds is a LONG time.  Well more than enough for a "single mistake".  So even though it hurts the Galaxy Quest reference, I think I should adjust that.  Perhaps allow time travel to any point within the last 13 seconds, rather than the full 13 seconds?

* I made it so holding the Z key will cause a ghost-like overlay of the past play in reverse from the current moment.  Releasing Z will cause the player to jump to that moment, while pressing X during the playback will abort the jump.  It is no longer required to have 13 seconds, and unused time is kept, eg: if the user has 10 seconds stored, and goes back three seconds, then they still have 7 seconds stored.

* It seems that at least some of the performance issues in firefox/other machines is due to the particle effects.  Before 16 particles were created by every explosion and for asteroids the player is shooting at there are often several explosions in a row. I thought it was very pretty, but removing the particles makes it much more playable in firefox.  I've kept the particles on player death for now.

* I considered using the [use element](https://developer.mozilla.org/en-US/docs/Web/SVG/Element/use) (used in the example [here](https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/transform)) because lots of the svgs are repeated, but it seems like this is not actually a good way to improve performance ([example discussion on SO](https://stackoverflow.com/questions/8604999/does-reusing-symbols-improve-svg-performance)).  In canvas, you can draw a complicated pattern to a secondary canvas and then stamp it onto the visible primary canvas and this can be much faster than redrawing the complicated pattern every time. It is disappointing that there doesn't seem to be an efficient SVG equivalent.  On the other hand react/reagent should be smart about not changing the DOM for parts that don't change, so maybe it isn't that bad.

* SVG path elements are extremely flexible! The MDN documentation is very good. [path elements](https://developer.mozilla.org/en-US/docs/Web/SVG/Element/path) and the [d attribute](https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d).  Positioning text within an SVG is doable, but I found it to be a bit of a hassle.

* Functions involving transients were getting full of nulls for some reason (js nulls that is, not clojure nils).  It appears that getting things out of transients is different than getting things out of persistent maps.  (:z {:x 1 :y}) -> nil for persistent maps, but it was doing something else for transients in cljs.


## To Do

* Load saved game by default

* Low graphics mode to possibly improve performance on slow browsers/machines.

* Code cleaning

* Tests & CI

* See if transients can improve update performance

* Spec

* Sound effects

* Helper boats?

* Hooligan ships?


## Issues

* Shrinking the window can cause label text to overlap.

* Performance slow/janky in Firefox and slower computers.

* Safari: Text background doesn't show.

* IE: IE
