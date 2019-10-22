
ChessDrills is a suite of drills designed to improve your chess. It currently features one game, called "Invisible Pairs", which is a clone of an old DOS game called "Dynamic Pairs" (if I remember correctly) that I can't find online anymore. 

Other games I plan to add are inspired by drills found in the ChessMaster series. If you have an idea for a game feel free to add a feature/pull request.

# Invisible Pairs

![invsible-pairs](https://github.com/heategn/ChessDrills/blob/master/cdanimation.gif)

The game is played by first adding a select number of piece randomly to the board. Only two pieces can be captured by one another. The game begins when the player has memorized the position and clicks the board to start the game. The pieces then become invisible, moving one of the two aforementioned pieces to a new location on the board within capturable range of another piece (or visa versa) Then, while the pieces are still hidden, the player must click the square with the "other" piece. The process repeats for a given number of turns or until the player clicks the wrong square.

# Run

Requires Java 8. The jar file can be found at /dist/ChessDrills.jar.

To launch from the command-line:

`java -jar ChessDrills.jar`

Platforms: Tested on Windows 7 and Linux.

# Build

The project was created in NetBeans 8.1. Requires Java 8/OpenJDK 8 and JavaFx/OpenJFX8.

To build from the command-line:
 
`ant -f [PROJECT_ROOT]/ChessDrills -Dnb.internal.action.name=build jar`

# Known Issues

- There is an issue where, upon launch, the board will not always size itself correctly. Requires a restart.
- When the board is resized, sometimes the pieces will not line up correctly with the re-sized squares.



