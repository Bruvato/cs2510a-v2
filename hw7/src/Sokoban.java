import java.awt.Color;

import javalib.impworld.*;
import javalib.worldimages.*;
import tester.Tester;

import java.util.ArrayList;

// represents universal constants in our code
class Constants {
  static final int SCENE_WIDTH = 600;
  static final int SCENE_HEIGHT = 600;
  static final double SCALE_FACTOR = 0.5;

  static final int CELL_WIDTH = 120;
  static final int CELL_HEIGHT = 120;

  // TODO
  //  ensure file paths are correct
  static WorldImage BLANK_SPRITE = new RectangleImage(CELL_WIDTH, CELL_HEIGHT, OutlineMode.OUTLINE,
          Color.BLACK);
  static WorldImage PLAYER_SPRITE = new FromFileImage("./src/assets/player.png");
  static WorldImage WALL_SPRITE = new FromFileImage("./src/assets/wall.png");
  static WorldImage BOX_SPRITE = new FromFileImage("./src/assets/box.png");
  static WorldImage HOLE_SPRITE = new FromFileImage("./src/assets/hole.png");

  static WorldImage TARGET_RED_SPRITE = new OverlayImage(
          new CircleImage(25, OutlineMode.SOLID, Color.RED),
          new OverlayImage(new CircleImage(45, OutlineMode.SOLID, Color.WHITE),
                  new CircleImage(60, OutlineMode.SOLID, Color.RED)));

  static WorldImage TARGET_BLUE_SPRITE = new OverlayImage(
          new CircleImage(25, OutlineMode.SOLID, Color.BLUE),
          new OverlayImage(new CircleImage(45, OutlineMode.SOLID, Color.WHITE),
                  new CircleImage(60, OutlineMode.SOLID, Color.BLUE)));

  static WorldImage TARGET_GREEN_SPRITE = new OverlayImage(
          new CircleImage(25, OutlineMode.SOLID, Color.GREEN),
          new OverlayImage(new CircleImage(45, OutlineMode.SOLID, Color.WHITE),
                  new CircleImage(60, OutlineMode.SOLID, Color.GREEN)));

  static WorldImage TARGET_YELLOW_SPRITE = new OverlayImage(
          new CircleImage(25, OutlineMode.SOLID, Color.YELLOW),
          new OverlayImage(new CircleImage(45, OutlineMode.SOLID, Color.WHITE),
                  new CircleImage(60, OutlineMode.SOLID, Color.YELLOW)));

}

// convenient computations and methods that don't fit into other classes
class Utils {

  // Converts the given color to an image of a trophy with the corresponding color
  static WorldImage colorToTrophySprite(String color) {
    return new FromFileImage("./src/assets/trophy_" + color + ".png");
  }

  // Converts the given color to an image of a target with the corresponding color
  static WorldImage colorToTargetSprite(String color) {
    switch (color) {
      case "red":
        return Constants.TARGET_RED_SPRITE;
      case "green":
        return Constants.TARGET_GREEN_SPRITE;
      case "blue":
        return Constants.TARGET_BLUE_SPRITE;
      case "yellow":
        return Constants.TARGET_YELLOW_SPRITE;
      default:
        throw new RuntimeException("Target of the given color is not in our library. sorry :(");
    }
  }

  // Converts a given string direction to a degree of rotation.
  // 0 degrees assumes to the right. Rotates clockwise, meaning 90 degrees is down
  static double directionToDegrees(String direction) {
    switch (direction) {
      case "up":
        return 270.0;
      case "right":
        return 0.0;
      case "down":
        return 90.0;
      case "left":
        return 180.0;
      default:
        throw new RuntimeException("Invalid direction given.");
    }
  }

  // Returns the list of lists of cells represented by the given strings of grounds and contents
  static ArrayList<ArrayList<Cell>> makeGrid(String groundStr, String contentStr) {
    ArrayList<ArrayList<Cell>> grid = new ArrayList<ArrayList<Cell>>();

    ArrayList<String> groundRows = split(groundStr, "\n");
    ArrayList<String> contentRows = split(contentStr, "\n");

    // EFFECT: Adds each row represented by both the
    //         given ground and contents string into the grid
    for (int listIdx = 0; listIdx < groundRows.size(); listIdx++) {
      grid.add(makeRow(groundRows.get(listIdx), contentRows.get(listIdx)));
    }

    return grid;
  }

  // Returns the list of cells represented by the given strings of grounds and contents
  static ArrayList<Cell> makeRow(String groundStr, String contentStr) {

    ArrayList<Cell> row = new ArrayList<Cell>();
    int stringLen = groundStr.length();
    // EFFECT: populates the above list with cells made up of a ground and content corresponding to
    //         current same stringIdx
    for (int stringIdx = 0; stringIdx < stringLen; stringIdx++) {
      IGround ground = makeGround(groundStr.substring(stringIdx, stringIdx + 1));
      IContent content = makeContent(contentStr.substring(stringIdx, stringIdx + 1));

      Cell cell = new Cell(ground, content);

      row.add(cell);
    }

    return row;
  }

  // Returns the ground represented by the given string ground
  static IGround makeGround(String groundStr) {
    switch (groundStr) {
      case "Y":
        return new Target("yellow");
      case "G":
        return new Target("green");
      case "B":
        return new Target("blue");
      case "R":
        return new Target("red");
      case "H":
        return new Hole();
      case "_":
        return new Normal();
      default:
        throw new IllegalArgumentException("invalid ground cell description: " + groundStr);
    }
  }

  // Returns the content represented by the given string
  static IContent makeContent(String contentStr) {
    switch (contentStr) {
      case "y":
        return new Trophy("yellow");
      case "g":
        return new Trophy("green");
      case "b":
        return new Trophy("blue");
      case "r":
        return new Trophy("red");
      case "W":
        return new Wall();
      case "B":
        return new Box();
      case ">":
        return new Player("right");
      case "<":
        return new Player("left");
      case "^":
        return new Player("up");
      case "v":
        return new Player("down");
      case "_":
        return new Blank();
      default:
        throw new IllegalArgumentException("invalid content cell description: " + contentStr);
    }
  }

  // locates the player's index given a content string representing the row of a board
  // return -1 if not found
  static int findPlayerIndex(String contentStr) {

    // EFFECT: iterate through each string in the content string
    //         and locate the index of the player string if found
    for (int strIdx = 0; strIdx < contentStr.length(); strIdx++) {
      String s = contentStr.substring(strIdx, strIdx + 1);
      if ("^v<>".contains(s)) {
        return strIdx;
      }
    }

    // player not found in content string
    return -1;
  }

  // locates the player's position given a content string
  static Posn findPlayer(String contentStr) {
    ArrayList<String> rows = split(contentStr, "\n");

    // EFFECT: iterate through each row and find the position of the player if found
    for (int listIdx = 0; listIdx < rows.size(); listIdx++) {

      String row = rows.get(listIdx);
      int playerIndex = findPlayerIndex(row);

      // if player is not found on the current row, skip to the next iteration
      if (playerIndex == -1) {
        continue;
      }

      // player found so we return its position
      return new Posn(playerIndex, listIdx);
    }

    // player not found in the content string
    throw new IllegalArgumentException("No player found in: " + contentStr);
  }

  // splits a given string into an arraylist of strings by the given regular expression
  static ArrayList<String> split(String input, String regex) {
    ArrayList<String> result = new ArrayList<>();

    int start = 0;

    // EFFECT: iterate through the string and find substrings seperated by the regex
    //          and add it to the result
    while (true) {
      int strIdx = input.indexOf(regex, start);

      if (strIdx == -1) {
        result.add(input.substring(start));
        break;
      }

      result.add(input.substring(start, strIdx));
      start = strIdx + regex.length();
    }

    return result;
  }


  /* Parameters
   * destination - Posn
   * level - Level
   * Methods on Parameters
   * level.isWon() - boolean
   * level.updateBoard(String key) - Level
   * level.movePlayer(String direction) - Level
   * level.playerPosition() - Posn
   * level.getPlayerY() - int
   * level.getPlayerX() - int
   * level.height() - int
   * level.width() - int
   * level.destinationHasNoContent(Posn destination) - boolean
   * level.turnToEmptyContentAt(Posn coord) - Level
   * level.turnToPlayerContentAt(Posn coord, String direction) - Level
   * level.emptiedTileAt(Posn coord) - ITile
   * level.playerOnTileAt(Posn coord, String direction) - ITile
   */

  /* Fields
   * Methods
   * this.invalidTrophyColor(String color) - boolean
   * this.colorToTrophy(String color) - WorldImage
   * this.invalidTargetColor(String color) - boolean
   * this.colorToTarget(String color) - WorldImage
   * this.invalidPlayerDirection(String direction) - boolean
   * this.makeBoardList(String tiles, String contents) - IList<IList<ITile>>
   * this.directionToDegrees(String direction) - double
   * this.makeRow(String tiles, String contents) - IList<ITile>
   * this.makeTile(String tile, String content) - ITile
   * this.makeContent(String content) - IContent
   * this.moveCoord(Posn coord, String direction) - Posn
   * this.destinationInBoard(Posn destination, Level level) - boolean
   * this.validMoveKey(String key) - boolean
   * this.keyToDirection(String key) - String
   * Methods on Fields
   */
}


// represents a single cell in the Sokoban board
class Cell {

  IGround ground;
  IContent content;

  // initializes a cell with a given ground object and content object
  Cell(IGround ground, IContent content) {
    this.ground = ground;
    this.content = content;
  }

  // converts this cell to an image
  WorldImage cellToImage() {
    return new OverlayImage(this.content.contentToImage(), this.ground.groundToImage());
  }

  // determines if this cell won
  boolean cellWon() {
    return this.ground.groundWon(this.content);
  }

  boolean cellFall() {
    return this.ground.groundFall(this.content);
  }

  boolean cellLost(){
    return this.content.contentLost();
  }


}

// -------------- GROUND AND CONTENT ------------------------

// a ground object in a cell
interface IGround {

  // Draws this ground as its WorldImage representation
  WorldImage groundToImage();

  // determines if this ground object won given a content object
  boolean groundWon(IContent other);

  // TODO
  boolean groundFall(IContent other);

}

// a ground object
abstract class AGround implements IGround {
  // Draws this ground as its WorldImage representation
  public abstract WorldImage groundToImage();

  // determines if this ground object won given a content object
  public boolean groundWon(IContent other) {
    // vacuously true
    return true;
  }

  // TODO
  @Override
  public boolean groundFall(IContent other) {
    return false;
  }


}

// a content object in a cell
interface IContent {

  // Draws this content as its WorldImage representation
  WorldImage contentToImage();

  // determines if this content object won given a Target object
  boolean contentWon(Target other);

  // determines if this content is vacant
  boolean isVacant();

  // determines if this content is fixed1
  boolean isFixed();

  // TODO
  boolean contentFall(IGround other);

  // TODO
  boolean contentLost();
}

// a content object
abstract class AContent implements IContent {
  // Draws this content as its WorldImage representation
  public abstract WorldImage contentToImage();

  // determines if this content object won given a Target object
  public boolean contentWon(Target other) {
    return false;
  }

  // determines if this content is vacant
  public boolean isVacant() {
    return false;
  }

  // determines if this content is fixed
  public boolean isFixed() {
    return false;
  }

  // TODO
  @Override
  public boolean contentFall(IGround other) {
    return true;
  }

  @Override
  public boolean contentLost() {
    return true;
  }
}

// --------- GROUND OBJECTS ------------------------

// Represents Normal ground in the game Sokoban
class Normal extends AGround {

  // draws this blank object on the ground layer
  public WorldImage groundToImage() {
    return Constants.BLANK_SPRITE;
    // return new RectangleImage(120, 120, OutlineMode.SOLID, Color.WHITE);
  }

}

// Represents a Target as the ground in the game Sokoban
class Target extends AGround {
  String color;

  // Constructor
  Target(String color) {
    this.color = color;
  }

  //Draws this Target as its WorldImage equivalent
  public WorldImage toImage() {
    return new OverlayImage(Utils.colorToTargetSprite(this.color),
            new RectangleImage(50, 50, OutlineMode.SOLID, Color.WHITE));
  }

  // Draws this Target as its WorldImage equivalent
  public WorldImage groundToImage() {
    return Utils.colorToTargetSprite(this.color);
  }

  // determines if this target ground object won given a content object
  public boolean groundWon(IContent other) {
    return other.contentWon(this);
  }
}

class Hole extends AGround {

  // Draws this Hole as its WorldImage equivalent
  public WorldImage groundToImage() {
    return Constants.HOLE_SPRITE;
  }

  // TODO
  @Override
  public boolean groundFall(IContent other) {
    return other.contentFall(this);
  }


}

// --------- CONTENT OBJECTS ------------------------

// Represents an blank content in Sokoban
class Blank extends AContent {
  // draws this blank object on the content layer
  public WorldImage contentToImage() {
    return new EmptyImage();
  }

  @Override
  // determines if this blank object is vacant
  public boolean isVacant() {
    return true;
  }

  // TODO
  @Override
  public boolean contentFall(IGround other) {
    return false;
  }
}

// Represents a wall in Sokoban
class Wall extends AContent {

  // Draws this Wall as its WorldImage equivalent
  public WorldImage contentToImage() {
    return Constants.WALL_SPRITE;
  }

  @Override
  // determines if this wall content is fixed
  public boolean isFixed() {
    return true;
  }

}

// Represents a box in Sokoban
class Box extends AContent {

  // Draws this Box as its WorldImage equivalent
  public WorldImage contentToImage() {
    return Constants.BOX_SPRITE;
  }


}

// Represents a colored trophy in Sokoban
class Trophy extends AContent {
  String color;

  Trophy(String color) {
    this.color = color;
  }

  // Determines if this Trophy has the same trophy color as the given color
  // unused, TODO
  public boolean sameTrophyColorAs(String color) {
    return this.color.equalsIgnoreCase(color);
  }

  // Draws this Trophy as its WorldImage equivalent
  public WorldImage contentToImage() {
    return Utils.colorToTrophySprite(this.color);
  }

  // Determines if this trophy content is won given the Target that it is on
  public boolean contentWon(Target other) {
    return other.color.equalsIgnoreCase(this.color);
  }
}

// Represents the player in Sokoban
class Player extends AContent {
  String direction;

  Player(String direction) {
    this.direction = direction;
  }

  // Converts this Player content into a WorldImage equivalent
  public WorldImage contentToImage() {
    return new RotateImage(Constants.PLAYER_SPRITE, Utils.directionToDegrees(this.direction));
  }

  // TODO
  @Override
  public boolean contentLost() {
    return false;
  }
}

// --------------- LEVEL -----------------------

// Represents a Level in Sokoban
class Level {

  // TODO
  //  explain coordinate system

  // a sokoban board
  ArrayList<ArrayList<Cell>> board;
  Posn playerPos;
  String groundStr;
  String contentStr;

  // configures this level given a board: list of lists of cells
  Level(ArrayList<ArrayList<Cell>> board) {
    this.board = board;
  }

  // configures this level given the ground and content level description strings
  public Level(String groundStr, String contentsStr) {
    if (groundStr.length() != contentsStr.length()) {
      throw new IllegalArgumentException("Invalid Ground/Content descriptions: "
              + "Given ground and content strings cannot be of different sizes");
    }
    this.board = Utils.makeGrid(groundStr, contentsStr);
    this.playerPos = Utils.findPlayer(contentsStr);
    this.groundStr = groundStr;
    this.contentStr = contentsStr;
  }

  // Converts this level's board into a WorldImage to be shown
  WorldImage draw() {
    WorldImage boardImage = new EmptyImage();
    // EFFECT: Draws every row in this board and puts each row below one another
    for (ArrayList<Cell> row : this.board) {
      WorldImage rowImage = new EmptyImage();
      // EFFECT: Draws every cell in the given row and places each drawing beside one another
      for (Cell cell : row) {
        rowImage = new BesideImage(rowImage, cell.cellToImage());
      }
      boardImage = new AboveImage(boardImage, rowImage);
    }
    return boardImage;
  }

  // Determines if this level is won. A level is won when all the Targets have
  // their corresponding trophy on them.
  boolean levelWon() {
    // EFFECT: Determines if every row in the board is won
    for (ArrayList<Cell> row : this.board) {
      // EFFECT: Determines if every cell in the given row is won
      for (Cell cell : row) {
        if (!cell.cellWon()) {
          return false;
        }
      }
    }
    return true;
  }

  // returns the cell at the given position in this sokoban board
  Cell getCell(Posn posn) {
    return this.board.get(posn.y).get(posn.x);
  }

  // Moves the player in the given direction if possible. The player can only move onto a ground
  // that is empty or push one object IFF that object has space to be pushed
  void movePlayer(String direction) {

    Posn moveToPos = this.playerPos.movePosn(direction); // pos of where player is moving to
    Posn pushToPos = moveToPos.movePosn(direction); // pos of where pushable is moving to

    Posn topLeft = new Posn(0, 0);
    Posn bottomRight = new Posn(this.board.get(0).size() - 1, this.board.size() - 1);

    if (!moveToPos.withinBounds(topLeft, bottomRight)) {
      return; // exit this method if move position is out of bounds
    }

    Cell playerCell = this.getCell(this.playerPos);
    Cell moveToCell = this.getCell(moveToPos);

    if (moveToCell.content.isFixed()) {
      return; // exit method if content at move position is fixed
    }

    // check if push to position is within bounds
    if (pushToPos.withinBounds(topLeft, bottomRight)) {
      Cell pushToCell = this.getCell(pushToPos);

      // check if content at push to position is vacant
      if (pushToCell.content.isVacant()) {
        pushToCell.content = moveToCell.content;
        moveToCell.content = new Blank();
      }
    }

    // check if content at move to position is vacant
    if (moveToCell.content.isVacant()) {
      moveToCell.content = playerCell.content;
      playerCell.content = new Blank();

      // update player position
      this.playerPos = moveToPos;
    }

  }



  void resetLevel() {
    this.board = Utils.makeGrid(this.groundStr, this.contentStr);
    this.playerPos = Utils.findPlayer(this.contentStr);
  }

  boolean levelLost() {
    for (ArrayList<Cell> row : this.board) {
      for (Cell cell : row) {
        if (!cell.cellLost()) {
          return false;
        }
      }
    }

    return true;
  }

  // TODO
  // updates
  void updateLevel() {
    for (ArrayList<Cell> row : this.board) {
      for (Cell cell : row) {
        if (cell.cellFall()) {
          cell.ground = new Normal();
          cell.content = new Blank();
        }
      }
    }
  }


}

class Posn {
  int x; // the col index
  int y; // the row index

  public Posn(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // Calculates a new posn based on the given direction
  Posn movePosn(String direction) {
    switch (direction) {
      case "up":
        return new Posn(this.x, this.y - 1);
      case "right":
        return new Posn(this.x + 1, this.y);
      case "down":
        return new Posn(this.x, this.y + 1);
      case "left":
        return new Posn(this.x - 1, this.y);
      default:
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }
  }

  // determines if this position is within the boundary box
  // given by the top left point and the bottom right point
  boolean withinBounds(Posn topLeft, Posn bottomRight) {
    return topLeft.x <= this.x && topLeft.y <= this.y
            && bottomRight.x >= this.x && bottomRight.y >= this.y;
  }
}

// Represents the current level state in Sokoban
class Sokoban extends World {
  Level level;
  boolean gameOver;

  // Constructor
  Sokoban(Level level) {
    this.level = level;
    updateGameState();
  }

  void updateGameState() {
    this.level.updateLevel();
    this.gameOver = this.level.levelLost() || this.level.levelWon();
  }

  // TODO
  // ormap holes


  // creates the scene for this Sokoban world
  public WorldScene makeScene() {
    WorldImage levelImage;

    if (this.gameOver) {
      levelImage = new TextImage("game over (press r to reset)", Color.red);
    }else{
      levelImage = new ScaleImage(this.level.draw(), Constants.SCALE_FACTOR);
    }

    WorldScene levelScene = new WorldScene(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
    levelScene.placeImageXY(levelImage, Constants.SCENE_WIDTH / 2, Constants.SCENE_HEIGHT / 2);
    return levelScene;
  }

  // handles key events
  public void onKeyEvent(String key) {
    if (key.equals("up") || key.equals("down") || key.equals("right") || key.equals("left")) {
      // this.level = this.level.update
      this.level.movePlayer(key);
    }

    if (key.equals("r")) {
      this.level.resetLevel();
    }

    this.updateGameState();
  }
}

// TODO
//  - 2 worlds
//  - return game over world when lose
//  - update level, check win/lose conditions on start/initialization
//  - testing

// Represents the tests for Sokoban
class ExamplesSokoban {

  String exampleLevelGround;
  String exampleLevelContents;
  String exampleLevelContents2;
  String exampleLevelContents2E;
  String exampleLevelContents2R;
  String exampleLevelContents2U;
  String exampleLevelContents3;
  String exampleSmallGround;
  String exampleSmallContents;
  String gameGround;
  String gameContent;

  Level blernerLevel;
  Level blernerLevel2;
  Level blernerLevel2E;
  Level blernerLevel2R;
  Level blernerLevel2U;
  Level blernerLevel3;
  Level small;
  Level gameLevel;

  World game;

  void init() {
    exampleLevelGround =
            "________\n" + "___R____\n" + "________\n" + "_B____Y_\n" + "________\n" + "___G____\n"
                    + "________";
    exampleLevelContents =
            "__WWW___\n" + "__W_WW__\n" + "WWWr_WWW\n" + "W_b>yB_W\n" + "WW_gWWWW\n" + "_WW_W___\n"
                    + "__WWW___";
    exampleLevelContents2 =
            "__WWW___\n" + "__W_WW__\n" + "WWW__WWW\n" + "W__>_B_W\n" + "WW__WWWW\n" + "_WW_W___\n"
                    + "__WWW___";
    exampleLevelContents2E =
            "__WWW___\n" + "__W_WW__\n" + "WWW__WWW\n" + "W____B_W\n" + "WW__WWWW\n" + "_WW_W___\n"
                    + "__WWW___";
    exampleLevelContents2R =
            "__WWW___\n" + "__W_WW__\n" + "WWW__WWW\n" + "W___>B_W\n" + "WW__WWWW\n" + "_WW_W___\n"
                    + "__WWW___";
    exampleLevelContents2U =
            "__WWW___\n" + "__W_WW__\n" + "WWW^_WWW\n" + "W____B_W\n" + "WW__WWWW\n" + "_WW_W___\n"
                    + "__WWW___";
    exampleLevelContents3 =
            "__WWW___\n" + "__W_WW__\n" + "WWW_rWWW\n" + "Wb_>_ByW\n" + "WW__WWWW\n" + "_WWgW___\n"
                    + "__WWW___";
    exampleSmallGround = "__B\n" + "_R_";
    exampleSmallContents = "Wb_\n" + ">Br";

    gameGround = "________\n" + "________\n" + "_B______\n" + "_____G__\n" + "_R______\n"
            + "___HY___\n" + "___B__R_\n" + "____G___\n" + "________\n";
    gameContent = "__WWWWW_\n" + "WWW___W_\n" + "W_>b__W_\n" + "WWW_g_W_\n" + "W_WWy_W_\n"
            + "W_W___WW\n" + "Wr_bgr_W\n" + "W______W\n" + "WWWWWWWW\n";


    blernerLevel = new Level(exampleLevelGround, exampleLevelContents);
    blernerLevel2 = new Level(exampleLevelGround, exampleLevelContents2);
    // blernerLevel2E = new Level(exampleLevelGround, exampleLevelContents2E);
    blernerLevel2R = new Level(exampleLevelGround, exampleLevelContents2R);
    blernerLevel2U = new Level(exampleLevelGround, exampleLevelContents2U);
    blernerLevel3 = new Level(exampleLevelGround, exampleLevelContents3);
    small = new Level(exampleSmallGround, exampleSmallContents);
    gameLevel = new Level(gameGround, gameContent);

    game = new Sokoban(gameLevel);
  }

  void testBigBang(Tester t) {
    this.init();

    game.bigBang(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, 1);
  }

  // test level construction ----------------------------------

  void testMakeGround(Tester t) {
    this.init();
    // blank
    t.checkExpect(Utils.makeGround("_"), new Normal());

    // targets
    t.checkExpect(Utils.makeGround("B"), new Target("blue"));
    t.checkExpect(Utils.makeGround("G"), new Target("green"));
    t.checkExpect(Utils.makeGround("R"), new Target("red"));
    t.checkExpect(Utils.makeGround("Y"), new Target("yellow"));

    // hole
    t.checkExpect(Utils.makeGround("H"), new Hole());
  }

  void testMakeContent(Tester t) {
    this.init();
    // blank
    t.checkExpect(Utils.makeContent("_"), new Blank());

    // trophies
    t.checkExpect(Utils.makeContent("b"), new Trophy("blue"));
    t.checkExpect(Utils.makeContent("g"), new Trophy("green"));
    t.checkExpect(Utils.makeContent("r"), new Trophy("red"));
    t.checkExpect(Utils.makeContent("y"), new Trophy("yellow"));

    // wall
    t.checkExpect(Utils.makeContent("W"), new Wall());

    // box
    t.checkExpect(Utils.makeContent("B"), new Box());

    // Player
    t.checkExpect(Utils.makeContent(">"), new Player("right"));
    t.checkExpect(Utils.makeContent("<"), new Player("left"));
    t.checkExpect(Utils.makeContent("^"), new Player("up"));
    t.checkExpect(Utils.makeContent("v"), new Player("down"));
  }

  void testMakeRow(Tester t) {
    this.init();
    ArrayList<Cell> row = new ArrayList<Cell>();

    row.add(new Cell(new Normal(), new Wall()));
    row.add(new Cell(new Target("blue"), new Blank()));
    row.add(new Cell(new Normal(), new Trophy("blue")));
    row.add(new Cell(new Normal(), new Player("right")));
    row.add(new Cell(new Normal(), new Trophy("yellow")));
    row.add(new Cell(new Normal(), new Box()));
    row.add(new Cell(new Target("yellow"), new Blank()));
    row.add(new Cell(new Normal(), new Wall()));

    t.checkExpect(Utils.makeRow("_B____Y_", "W_b>yB_W"), row);
  }

  void testMakeGrid(Tester t) {
    // TODO
    //  exception test util methods?
    this.init();
    ArrayList<ArrayList<Cell>> grid = new ArrayList<>();

    ArrayList<Cell> row0 = new ArrayList<>();
    row0.add(new Cell(new Normal(), new Wall()));
    row0.add(new Cell(new Target("red"), new Trophy("red")));

    ArrayList<Cell> row1 = new ArrayList<>();
    row1.add(new Cell(new Target("green"), new Player("right")));
    row1.add(new Cell(new Normal(), new Box()));

    grid.add(row0);
    grid.add(row1);

    t.checkExpect(Utils.makeGrid("_R\n" + "G_", "Wr\n" + ">B"), grid);
  }

  void testCellWon(Tester t) {
    this.init();

    t.checkExpect(new Cell(new Normal(), new Blank()).cellWon(), true);
    t.checkExpect(new Cell(new Normal(), new Trophy("red")).cellWon(), true);
    t.checkExpect(new Cell(new Target("red"), new Blank()).cellWon(), false);
    t.checkExpect(new Cell(new Target("red"), new Trophy("red")).cellWon(), true);
    t.checkExpect(new Cell(new Target("red"), new Trophy("green")).cellWon(), false);
  }

  void testLevelWon(Tester t) {
    this.init();

    String groundString = "__RB_Y_G";
    String contentWonString = "_vrb_y_g";
    String contentNotWonString = ">r_y_bg_";

    Level smallWon = new Level(groundString, contentWonString);
    Level smallNotWon = new Level(groundString, contentNotWonString);

    t.checkExpect(smallWon.levelWon(), true);
    t.checkExpect(smallNotWon.levelWon(), false);
  }

  void testPlayerIndex(Tester t) {
    this.init();

    t.checkExpect(Utils.findPlayerIndex("________\n"), -1);
    t.checkExpect(Utils.findPlayerIndex("W_b>yB_W\n"), 3);
    t.checkExpect(Utils.findPlayerIndex(">Br"), 0);
    t.checkExpect(Utils.findPlayerIndex(""), -1);
  }

  void testFindPlayer(Tester t) {
    init();
    t.checkExpect(Utils.findPlayer(exampleLevelContents), new Posn(3, 3));

    t.checkExpect(Utils.findPlayer(exampleSmallContents), new Posn(0, 1));
  }

  void testSplit(Tester t) {
    this.init();
    ArrayList<String> list = new ArrayList<>();
    list.add("a");
    list.add("b");
    list.add("c");

    t.checkExpect(Utils.split("a,b,c", ","), list);

    list.add("");

    t.checkExpect(Utils.split("a,b,c,", ","), list);

    ArrayList<String> row = new ArrayList<>();

    row.add("W_b>yB_W");
    row.add("WW_gWWWW");
    row.add("_WW_W___");

    t.checkExpect(Utils.split("W_b>yB_W\n" + "WW_gWWWW\n" + "_WW_W___", "\n"), row);
  }

  void testMovePosn(Tester t) {
    this.init();

    t.checkExpect(new Posn(0, 0).movePosn("up"), new Posn(0, -1));
    t.checkExpect(new Posn(0, 0).movePosn("down"), new Posn(0, 1));
    t.checkExpect(new Posn(0, 0).movePosn("left"), new Posn(-1, 0));
    t.checkExpect(new Posn(0, 0).movePosn("right"), new Posn(1, 0));
  }

  void testWithinBounds(Tester t) {
    this.init();

    t.checkExpect(new Posn(0, 0)
            .withinBounds(new Posn(0, 0), new Posn(3, 4)), true);
    t.checkExpect(new Posn(2, 5)
            .withinBounds(new Posn(0, 0), new Posn(3, 4)), false);
  }

  void testMovePlayer(Tester t) {
    this.init();

    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2));
    this.gameLevel.movePlayer("up"); // move into wall, no change
    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2));
    this.gameLevel.movePlayer("left"); // move into blank, moved
    t.checkExpect(this.gameLevel.playerPos, new Posn(1, 2));
    this.gameLevel.movePlayer("right");
    this.gameLevel.movePlayer("right"); // move into pushable, move & push
    t.checkExpect(this.gameLevel.playerPos, new Posn(3, 2));
    this.gameLevel.movePlayer("right");
    this.gameLevel.movePlayer("right"); // move into pushable that's moving into wall, no change
    t.checkExpect(this.gameLevel.playerPos, new Posn(4, 2));
  }


}
