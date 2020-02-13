
ChessDrills is a suite of mini chess games designed to improve your chess. It currently features two games. See [the site page]("https://heategn.github.io/heategn-workshop") for short gameplay videos.

### Invisible Pairs

The game is played by first adding a select number of piece randomly to the board. Only two pieces can be captured by one another. The game begins when the player, after memorizing the position, clicks the board to start the game. The pieces then become invisible except for one piece that moves to a location within capturable range of another piece (or visa versa). The player must click that "other" piece. The process repeats until all moves have been made. The game is over if the wrong square is clicked.

### Quick Capture

A number of white pieces are placed randomly on the board within capture distance of at least one other piece -- with the exception of the last piece, which should not be attacked by any other white piece. Then, the dark piece (player piece) is placed on a random square attacking the last piece, and possibly others. The player then tries to find and capture that unprotected piece as quickly as possible, moving to the next round. The goal is to get through all of the rounds as fast as possible.

# Run

1. Download `runtime_linux_x64.zip` for Linux or `runtime_windows_x64.zip` for Windows. 
2. Extract into a directory of your choice.
3. From the command line: cd into the base directory and run `./bin/chessdrills`.
4. From the desktop: open the folder **bin** and execute **chessdrills**

# Build

The project can be build with Apache Ant on Linux.  

* Run `ant build` in the base directory to compile, and then `ant run` to start application from the classfiles.
* Run `ant dist` to build a jar file, type `ant runjar` to start from the jar. 
* Run `ant testbuild` to build the test classfiles, `ant testconsole` to execute the tests. See **Testing** below. 
* Run `ant deploylinux` to build a standalone Linux application. It will deploy to the **runtime_linux_x64** directory. See **Standalone Deployment** below.
* Run `ant deploywindows` to build a standalone Windows application. It will deploy to the **runtime_windows_x64** directory. See **Standalone Deployment** below.

# Testing

JUnit5's console standalone package is used. See [JUnit's homepage](https://www.junit5.org) for details.

Run `testconsole` to execute and report the test results on the console.

The test files are located in the **src/test** directory. Uncomment a **@Test** attribute to run a test, but you'll probably want to heavily modify or write your own tests. The current tests are ad-hoc and certainly miss a few edge-cases.

# Standalone Deployment

Deployment involves building a custom runtime targeted at the specified platform. Linux and Windows platforms supported. Linux is required for the *ant* task due to a shell script used to execute the jlink tool. See deploy.sh to see what is executed and to guide porting to other platforms.

See Oracle's documentation for overview of the [jlink tool]("https://docs.oracle.com/javase/9/tools/jlink.htm#JSWOR-GUID-CECAC52B-CFEE-46CB-8166-F17A8E9280E9").

Run `ant deploylinux` or `ant deploywindows` to build the image. The output will be directed to a new **runtime_[platform]_x64** directory. To run the image, cd into the directory and run `./bin/chessdrills`.

# Known Issues

- Sometimes, on Linux, the chess board does not appear during startup. Resizing the window will get it to appear.
