import java.awt.Color;

import javalib.impworld.*;
import javalib.worldimages.*;
import jdk.jshell.execution.Util;
import tester.Tester;

import java.util.ArrayList;

// represents universal constants in our code
class Constants {
  static final int SCENE_WIDTH = 800;
  static final int SCENE_HEIGHT = 800;
  static final double SCALE_FACTOR = 0.5;
  static final int CELL_WIDTH = 120;
  static final int CELL_HEIGHT = 120;

  static WorldImage NORMAL_SPRITE = new RectangleImage(CELL_WIDTH, CELL_HEIGHT, OutlineMode.OUTLINE,
          Color.BLACK);
  static WorldImage PLAYER_SPRITE = new FromFileImage("./src/assets/player.png");
  static WorldImage WALL_SPRITE = new FromFileImage("./src/assets/wall.png");
  static WorldImage BOX_SPRITE = new FromFileImage("./src/assets/box.png");
  static WorldImage HOLE_SPRITE = new FromFileImage("./src/assets/hole.png");
  static WorldImage ICE_SPRITE = new FromFileImage("./src/assets/ice.png");

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
    if ("red, green, blue, yellow".contains(color)) {
      return new FromFileImage("./src/assets/trophy_" + color + ".png");
    }
    throw new RuntimeException("Trophy of the given color is not in our library. sorry :(");
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
      case "I":
        return new Ice();
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

  // locates the player's index in the given a content string representing the row of a board
  // return -1 if not found
  static int findPlayerIdxInRow(String contentStr) {
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
      int playerIndex = findPlayerIdxInRow(row);
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

  /* Fields
   * Methods
   * this.colorToTrophySprite(String color) - WorldImage
   * this.colorToTargetSprite(String color) - WorldImage
   * this.makeGrid(String groundStr, String contentStr) - ArrayList<ArrayList<Cell>>
   * this.makeRow(String groundStr, String contentStr) - ArrayList<Cell>
   * this.makeGround(String groundStr) - IGround
   * this.makeContent(String contentStr) - IContent
   * this.findPlayerIndex(String contentStr) - int
   * this.findPlayer(String contentStr) - Posn
   * this.split(String input, String regex) - ArrayList<String>
   * this.keyToDirection(String key) - String
   * Methods on Fields
   */
}

// Represents a coordinate in our level board system. The top left position is at 0, 0 and the
// bottom right is size(), size(). In other words, rows increase as we go down and cols increase
// as we go to the right
class Posn {
  int col; // the col index
  int row; // the row index

  public Posn(int col, int row) {
    this.col = col;
    this.row = row;
  }

  // Calculates a new posn based on the given direction
  Posn movePosn(String direction) {
    switch (direction) {
      case "up":
        return new Posn(this.col, this.row - 1);
      case "right":
        return new Posn(this.col + 1, this.row);
      case "down":
        return new Posn(this.col, this.row + 1);
      case "left":
        return new Posn(this.col - 1, this.row);
      default:
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }
  }

  // determines if this position is within the boundary box
  // given by the top left point and the bottom right point
  boolean withinBounds(Posn topLeft, Posn bottomRight) {
    return topLeft.col <= this.col && topLeft.row <= this.row && bottomRight.col >= this.col
            && bottomRight.row >= this.row;
    /* Fields of Parameters
     * topLeft.col - int
     * topLeft.row - int
     * bottomRight.col - int
     * bottomRight.row - int
     * Methods on Parameters
     * topLeft.movePosn(String direction) - Posn
     * topLeft.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     * bottomRight.movePosn(String direction) - Posn
     * bottomRight.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     */
  }

  // determines if this posn and a given posn are the same (their rows and cols fields are equal)
  boolean samePosn(Posn pos) {
    return pos.col == this.col && pos.row == this.row;
  }

  // TODO remove this later or test if kept
  // produces a string representation of this position
  public String toString() {
    return "(" + this.col + ", " + this.row + ")";
  }

  /* Fields
   * this.col - int
   * this.row - int
   * Methods
   * this.movePosn(String direction) - Posn
   * this.withinBounds(Posn topLeft, Posn bottomRight) - boolean
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

  // todo test/temp
  // determines if the content in this cell can move to the given pos in the given level
  boolean canMoveTo(Posn posn, Level level) {
    return this.content.canMoveTo(posn, level);
  }

  // determines if this cell won
  boolean cellWon() {
    return this.ground.groundWon(this.content);
  }

  // Determines if this cell is lost counted as a loss
  // a cell is lost if the player is not its content
  boolean cellLost() {
    return this.content.contentLost();
  }

  // Determines if the content this cell will fall into this ground
  boolean cellFall() {
    return this.ground.groundFall(this.content);
  }

  // todo test/temp
  // determines if the ground in this cell allows its content to slide
  boolean cellSlide(){
    return this.ground.groundSlide(this.content);
  }

  /* Fields
   * this.ground - IGround
   * this.content - IContent
   * Methods
   * this.cellToImage() - WorldImage
   * this.cellWon() - boolean
   * this.cellLost() - boolean
   * this.cellFall() - boolean
   * this.withinBounds(Posn topLeft, Posn bottomRight) - boolean
   * Methods on Fields
   * this.ground.groundToImage() - WorldImage
   * this.ground.groundWon(IContent other) - boolean
   * this.ground.groundFall(IContent other) - boolean
   * this.content.contentToImage() - WorldImage
   * this.content.contentWon(IContent other) - boolean
   * this.content.contentLost(IContent other) - boolean
   * this.content.isVacant() - boolean
   * this.content.contentFall() - boolean
   */
}

// -------------- GROUND AND CONTENT ------------------------

// a ground object in a cell
interface IGround {
  // Draws this ground as its WorldImage representation
  WorldImage groundToImage();

  // determines if this ground object won given a content object
  boolean groundWon(IContent other);

  // determines if the given IContent will fall into this ground
  boolean groundFall(IContent other);

  /* Fields
   * Methods
   * this.groundToImage() - WorldImage
   * this.groundWon(IContent other) - boolean
   * this.groundFall(IContent other) - boolean
   * Methods on Fields
   */

  // todo tests, templates
  // determines if this ground allows a given content to slide on it
  boolean groundSlide(IContent other);
}

// a ground object
abstract class AGround implements IGround {
  // Draws this ground as its WorldImage representation
  public abstract WorldImage groundToImage();

  // determines if this ground object won given a content object
  public boolean groundWon(IContent other) {
    // vacuously true
    return true;
    /* Fields of Parameters
     * Methods on Parameters
     * other.contentToImage() - WorldImage
     * other.contentWon(Target other) - boolean
     * other.contentLost() - boolean
     * other.isVacant() - boolean
     * other.contentFall(IGround other) - boolean
     */
  }

  // determines if the given IContent will fall into this ground
  public boolean groundFall(IContent other) {
    return false;
    /* Fields of Parameters
     * Methods on Parameters
     * other.contentToImage() - WorldImage
     * other.contentWon(Target other) - boolean
     * other.contentLost() - boolean
     * other.isVacant() - boolean
     * other.contentFall(IGround other) - boolean
     */
  }

  // determines if this ground allows a given content to slide on it
  public boolean groundSlide(IContent other) {
    // todo temp
    return false;
  }

  /* Fields
   * Methods
   * this.groundToImage() - WorldImage
   * this.groundWon(IContent other) - boolean
   * this.groundFall(IContent other) - boolean
   * Methods on Fields
   */

}

// a content object in a cell
interface IContent {

  // Draws this content as its WorldImage representation
  WorldImage contentToImage();

  // determines if this content won given a Target object
  boolean contentWon(Target other);

  // Determines if the cell that this content is in is counted as a loss;
  // cell is lost if the player is not its content
  boolean contentLost();

  // determines if this content is vacant
  boolean isVacant();

  // determines if this content is fixed
  boolean isFixed();

  // Determines if this content will fall into the given ground
  boolean contentFall(IGround other);

  // determine if this content can move to the given position
  boolean canMoveTo(Posn moveToPos, Level level);

  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.isFixed() - boolean
   * this.contentFall() - boolean
   * this.canMoveTo(Posn moveToPos, Level level) - boolean
   * Methods on Fields
   */
}

// a content object
abstract class AContent implements IContent {
  // Draws this content as its WorldImage representation
  public abstract WorldImage contentToImage();

  // determines if this content object won given a Target object
  public boolean contentWon(Target other) {
    return false;
    /* Fields of Parameters
     * other.color - String
     * Methods on Parameters
     * other.groundToImage() - WorldImage
     * other.groundWon(IContent other) - boolean
     * other.groundFall(IContent other) - boolean
     */
  }

  // Determines if the cell that this content is in is counted as a loss;
  // cell is lost if the player is not its content
  public boolean contentLost() {
    return true;
  }

  // determines if this content is vacant
  public boolean isVacant() {
    return false;
  }

  // determines if this content is fixed
  public boolean isFixed() {
    return false;
  }

  // Determines if this content will fall into the given Ground
  public boolean contentFall(IGround other) {
    return true;
    /* Fields of Parameters
     * Methods on Parameters
     * this.groundToImage() - WorldImage
     * this.groundWon(IContent other) - boolean
     * this.groundFall(IContent other) - boolean
     */
  }

  // determine if this content can move to the given position
  public boolean canMoveTo(Posn moveToPos, Level level) {
    Posn topLeft = new Posn(0, 0);
    Posn bottomRight = new Posn(level.board.get(0).size() - 1, level.board.size() - 1);

    if (!moveToPos.withinBounds(topLeft, bottomRight)) {
      return false; // return false if move position is out of bounds
    }
    if (this.isFixed()) {
      return false; // return false if this content is fixed
    }

    IContent moveToContent = level.findCell(moveToPos).content;

    return moveToContent.isVacant();

    /*
     * moveToPos.movePosn(String dir) - Posn
     * moveToPos.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     * moveToPos.samePosn(Posn pos) - boolean
     * level.board
     * level.getCell(Posn posn) - Cell
     * level.draw() - WorldImage
     * level.levelWon() - boolean
     * level.levelLost() - boolean
     * level.movePlayer(String direction) - Level
     * level.switchContents(Posn pos1, Posn pos2, Posn playerPos) - Level
     * level.updateLevel() - Level
     * level.undoMove() - Level
     * level.updateScore() - Level
     */
  }

  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.isFixed() - boolean
   * this.contentFall() - boolean
   * this.canMoveTo(Posn moveToPos, Level level) - boolean
   * Methods on Fields
   */
}

// --------- GROUND OBJECTS ------------------------

// Represents Normal ground in the game Sokoban
class Normal extends AGround {

  // draws this blank object on the ground layer
  public WorldImage groundToImage() {
    return Constants.NORMAL_SPRITE;
  }
  /* Fields
   * Methods
   * this.groundToImage() - WorldImage
   * this.groundWon(IContent other) - boolean
   * this.groundFall(IContent other) - boolean
   * Methods on Fields
   */
}

// Represents a Target as the ground in the game Sokoban
class Target extends AGround {
  String color;

  // Constructor
  Target(String color) {
    this.color = color;
  }

  // Draws this Target as its WorldImage equivalent
  public WorldImage groundToImage() {
    return Utils.colorToTargetSprite(this.color);
  }

  // determines if this target ground object won given a content object
  public boolean groundWon(IContent other) {
    return other.contentWon(this);
    /* Fields of Parameters
     * Methods on Parameters
     * other.contentToImage() - WorldImage
     * other.contentWon(Target other) - boolean
     * other.contentLost() - boolean
     * other.isVacant() - boolean
     * other.contentFall(IGround other) - boolean
     */
  }
  /* Fields
   * this.color - String
   * Methods
   * this.groundToImage() - WorldImage
   * this.groundWon(IContent other) - boolean
   * this.groundFall(IContent other) - boolean
   * Methods on Fields
   */
}

// Represents a Hole as the ground in the game Sokoban. Objects pushed into the hole disappear
// with the hole
class Hole extends AGround {

  // Draws this Hole as its WorldImage equivalent
  public WorldImage groundToImage() {
    return Constants.HOLE_SPRITE;
  }

  // Determines if the given IContent will fall into this hole
  public boolean groundFall(IContent other) {
    return other.contentFall(this);
    /* Fields of Parameters
     * Methods on Parameters
     * other.contentToImage() - WorldImage
     * other.contentWon(Target other) - boolean
     * other.contentLost() - boolean
     * other.isVacant() - boolean
     * other.isFixed() - boolean
     * other.contentFall(IGround other) - boolean
     * other.canMoveTo(Posn moveToPos, Level level) - boolean
     */
  }
  /* Fields
   * this.color - String
   * Methods
   * this.groundToImage() - WorldImage
   * this.groundWon(IContent other) - boolean
   * this.groundFall(IContent other) - boolean
   * Methods on Fields
   */
}

// Represents Ice as the ground in the game Sokoban
class Ice extends AGround {

  // Draws this Ice as its WorldImage equivalent
  public WorldImage groundToImage() {
    return Constants.ICE_SPRITE;
  }

  // determines if this ice allows a given content to slide on it
  public boolean groundSlide(IContent other) {
    return !other.isFixed();
  }
}

// --------- CONTENT OBJECTS ------------------------

// Represents an blank content in Sokoban
class Blank extends AContent {
  // draws this blank object on the content layer
  public WorldImage contentToImage() {
    return new EmptyImage();
  }

  // determines if this blank object is vacant
  public boolean isVacant() {
    return true;
  }

  // Determines if this content will fall into the given ground
  public boolean contentFall(IGround other) {
    return false;
    /* Fields of Parameters
     * Methods on Parameters
     * other.groundToImage() - WorldImage
     * other.groundWon(IContent other) - boolean
     * other.groundFall(IContent other) - boolean
     */
  }

  // determines if this blank is fixed
  public boolean isFixed() {
    return true;
  }

  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * Methods on Fields
   */
}

// Represents a wall in Sokoban
class Wall extends AContent {

  // Draws this Wall as its WorldImage equivalent
  public WorldImage contentToImage() {
    return Constants.WALL_SPRITE;
  }

  // determines if this wall is fixed
  public boolean isFixed() {
    return true;
  }

  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * Methods on Fields
   */
}

// Represents a box in Sokoban
class Box extends AContent {

  // Draws this Box as its WorldImage equivalent
  public WorldImage contentToImage() {
    return Constants.BOX_SPRITE;
  }
  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * Methods on Fields
   */
}

// Represents a colored trophy in Sokoban
class Trophy extends AContent {
  String color;

  Trophy(String color) {
    this.color = color;
  }

  // Draws this Trophy as its WorldImage equivalent
  public WorldImage contentToImage() {
    return Utils.colorToTrophySprite(this.color);
  }

  // Determines if this trophy content is won given the Target that it is on
  public boolean contentWon(Target other) {
    return other.color.equalsIgnoreCase(this.color);
    /* Fields of Parameters
     * other.color - String
     * Methods on Parameters
     * other.groundToImage() - WorldImage
     * other.groundWon(IContent other) - boolean
     * other.groundFall(IContent other) - boolean
     */
  }
  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * Methods on Fields
   */
}

// Represents the player in Sokoban
class Player extends AContent {
  String direction;

  Player(String direction) {
    this.direction = direction;
  }

  // Converts this Player content into a WorldImage equivalent
  public WorldImage contentToImage() {
    return Constants.PLAYER_SPRITE;
  }

  // Determines if the cell that this player is in is counted as a loss;
  // cell is lost if the player is not its content
  public boolean contentLost() {
    return false;
  }


  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * Methods on Fields
   */
}

// --------------- LEVEL -----------------------

// Represents a Level in Sokoban
class Level {
  // the board is represented with (0,0) being the top left of the game.
  // (size of board, size of an array in board) is the bottom right cell in the game
  // as well as the last item in the last array of the arrayList.

  ArrayList<ArrayList<Cell>> board; // a sokoban board
  Posn playerPos; // the player position
  Level prevLevel; // the previous sokoban board
  int steps; // number of steps a player has made


  // configures this level given a board: list of lists of cells, and the player position
  Level(ArrayList<ArrayList<Cell>> board, Posn playerPos, Level prev_level, int steps) {
    this.board = board;
    this.playerPos = playerPos;
    this.prevLevel = prev_level;
    this.steps = steps;
  }

  // configures this level given the ground and content level description strings
  public Level(String groundStr, String contentsStr) {
    if (groundStr.length() != contentsStr.length()) {
      throw new IllegalArgumentException("Invalid Ground/Content descriptions: "
              + "Given ground and content strings cannot be of different sizes");
    }
    this.board = Utils.makeGrid(groundStr, contentsStr);
    this.playerPos = Utils.findPlayer(contentsStr);
    this.prevLevel = this;
    this.steps = 0;
  }

  // returns the cell at the given position in this sokoban board
  Cell findCell(Posn posn) {
    Posn topLeft = new Posn(0, 0);
    Posn bottomRight = new Posn(this.board.get(0).size() - 1, this.board.size() - 1);
    if (!posn.withinBounds(topLeft, bottomRight)) {
      throw new RuntimeException("Tried to access a cell that is out of bounds");
    }
    return this.board.get(posn.row).get(posn.col);
    /* Fields of Parameters
     * posn.col - int
     * posn.row - int
     * Methods on Parameter
     * posn.movePosn(String direction) - Posn
     * posn.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     */
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

  // draws this level's stats
  WorldImage drawStats() {

    WorldImage scoreImage = new TextImage("Steps: " + this.steps, Color.BLACK);
    WorldImage playerPosImage = new TextImage("Player Position: " + this.playerPos.toString(),
            Color.BLACK);
    IContent player = this.findCell(this.playerPos).content;

    // TODO (for debugging) delete this later or test if kept
    Posn moveUp = this.playerPos.movePosn("up");
    Posn pushUp = moveUp.movePosn("up");
    Posn moveDown = this.playerPos.movePosn("down");
    Posn pushDown = moveDown.movePosn("down");
    Posn moveLeft = this.playerPos.movePosn("left");
    Posn pushLeft = moveLeft.movePosn("left");
    Posn moveRight = this.playerPos.movePosn("right");
    Posn pushRight = moveRight.movePosn("right");

    IContent up = this.findCell(moveUp).content;
    IContent down = this.findCell(moveDown).content;
    IContent left = this.findCell(moveLeft).content;
    IContent right = this.findCell(moveRight).content;

    WorldImage moveTo = new TextImage(
            "move up? " + player.canMoveTo(moveUp, this)
                    + "\n move down? " + player.canMoveTo(moveDown, this)
                    + "\n move left? " + player.canMoveTo(moveLeft, this)
                    + "\n move right? " + player.canMoveTo(moveRight, this), Color.red);
    WorldImage pushTo = new TextImage(
            "push up? " + up.canMoveTo(pushUp, this)
                    + "\n push down? " + down.canMoveTo(pushDown, this)
                    + "\n push left? " + left.canMoveTo(pushLeft, this)
                    + "\n push right? " + right.canMoveTo(pushRight, this), Color.red);
    WorldImage prev = new TextImage("previous position: " + prevLevel.playerPos.toString(), Color.BLUE);

    WorldImage above = new AboveAlignImage(
            AlignModeX.LEFT,
            scoreImage,
            playerPosImage,
            moveTo,
            pushTo,
            prev
    );
    return above.movePinhole(-above.getWidth() / 2, -above.getHeight() / 2);
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

  // Determines if this level has been lost
  boolean levelLost() {
    // EFFECT: Determines if any cell in the board is lost
    for (ArrayList<Cell> row : this.board) {
      // EFFECT: Determines if any cell in the given row is lost
      for (Cell cell : row) {
        if (!cell.cellLost()) {
          return false;
        }
      }
    }
    return true;
  }

  // creates a new level with the player and pushable moved in a given direction if possible
  Level movePlayer(String direction) {
    Posn moveToPos = this.playerPos.movePosn(direction); // pos of where player is moving to
    Posn pushToPos = moveToPos.movePosn(direction); // pos of where pushable is moving to

    Cell playerCell = this.findCell(this.playerPos);
    Cell pushableCell = this.findCell(moveToPos);

    if (pushableCell.canMoveTo(pushToPos, this)){ // can push implies can move
      return this.switchContents(pushToPos, moveToPos, this.playerPos, this, this.steps)
              .switchContents(moveToPos, this.playerPos, moveToPos, this, this.steps + 1)
              .slideContent(pushToPos, direction)
              .slidePlayer(moveToPos, direction);
    }

    if (playerCell.canMoveTo(moveToPos, this)) { // cannot push but can move
      return this.switchContents(moveToPos, this.playerPos, moveToPos, this, this.steps + 1)
              .slideContent(pushToPos, direction)
              .slidePlayer(moveToPos, direction);
    }

    return this; // cannot push, cannot move
  }


  // creates a new Level with the contents at the two given positions switched
  Level switchContents(Posn pos1, Posn pos2, Posn playerPos, Level prevLevel, int steps) {
    ArrayList<ArrayList<Cell>> newBoard = new ArrayList<>();
    Cell cell1 = findCell(pos1);
    Cell cell2 = findCell(pos2);

    // EFFECT: populates a new board with the contents at the given positions switched
    for (int row = 0; row < this.board.size(); row++) {
      ArrayList<Cell> newRow = new ArrayList<>();

      // EFFECT: populates a new row with the contents at the given positions switched
      for (int col = 0; col < this.board.get(0).size(); col++) {
        Posn pos = new Posn(col, row); // the current position
        Cell oldCell = this.board.get(row).get(col); // the cell at the current position

        if (pos.samePosn(pos1)) {
          newRow.add(new Cell(oldCell.ground, cell2.content)); // add cell2 content at cell1 pos
        } else if (pos.samePosn(pos2)) {
          newRow.add(new Cell(oldCell.ground, cell1.content)); // add cell1 content at cell2 pos
        } else {
          newRow.add(oldCell);
        }
      }
      newBoard.add(newRow);
    }

    return new Level(newBoard, playerPos, prevLevel, steps);

    /*
     * pos1.movePosn(String direction) - Posn
     * pos1.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     * pos1.samePosn(Posn pos) - boolean
     * pos2.movePosn(String direction) - Posn
     * pos2.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     * pos2.samePosn(Posn pos) - boolean
     */
  }

  // creates a new level with all of its cells updated
  Level updateLevel() {
    ArrayList<ArrayList<Cell>> newBoard = new ArrayList<>();

    // EFFECT: populates a new board to account for any object on a hole
    for (ArrayList<Cell> row : this.board) {
      ArrayList<Cell> newRow = new ArrayList<>();
      // EFFECT: populates a new row in the board to account for any object on a hole
      for (Cell cell : row) {
        if (cell.cellFall()) {
          newRow.add(new Cell(new Normal(), new Blank()));
        } else {
          newRow.add(cell);
        }
      }
      newBoard.add(newRow);
    }

    return new Level(newBoard, this.playerPos, this.prevLevel, this.steps);
  }

  // rewinds the game by one step (increases the player's score)
  Level undoMove() {
    return this.prevLevel.updateScore(this.steps + 1);
  }

  // updates this level's score to the given score
  Level updateScore(int score) {
    return new Level(this.board, this.playerPos, this.prevLevel, score);
  }

  // TODO test, templates,
  // slides a moveable content at the given pos in the given direction if possible
  Level slideContent(Posn pos, String direction) {
    Posn slideToPos = pos.movePosn(direction);
    Cell slideCell = this.findCell(pos);

    if (slideCell.canMoveTo(slideToPos, this) && slideCell.cellSlide()) {
      return this.switchContents(pos, slideToPos, this.playerPos, this.prevLevel, this.steps)
              .slideContent(slideToPos, direction);
    }

    return this;
  }

  // slides a player content at the given pos in the given direction if possible
  Level slidePlayer(Posn playerPos, String direction) {
    Posn slideToPos = playerPos.movePosn(direction);
    Posn pushToPos = slideToPos.movePosn(direction);

    Cell playerCell = this.findCell(playerPos);
    Cell pushableCell = this.findCell(slideToPos);

    if (pushableCell.canMoveTo(pushToPos, this)  && playerCell.cellSlide()) {
      return this.switchContents(pushToPos, slideToPos, playerPos, this.prevLevel, this.steps)
              .switchContents(playerPos, slideToPos, slideToPos, this.prevLevel, this.steps)
              .slidePlayer(slideToPos, direction);
    }

    if (playerCell.canMoveTo(slideToPos, this) && playerCell.cellSlide()) {
      return this.switchContents(playerPos, slideToPos, slideToPos, this.prevLevel, this.steps)
              .slidePlayer(slideToPos, direction);
    }

    return this;
  }
  // todo fix out of bounds?



  /* Fields
   * this.board - ArrayList<ArrayList<Cell>>
   * this.playerPos - Posn
   * this.groundStr - String
   * this.contentStr - String
   *
   * Methods
   * this.findCell(Posn posn) - Cell
   * this.draw() - WorldImage
   * this.levelWon() - boolean
   * this.levelLost() - boolean
   * this.movePlayer(String direction) - Level
   * this.switchContents(Posn pos1, Posn pos2, Posn playerPos) - Level
   * this.updateLevel() - Level
   * this.undoMove() - Level
   * this.updateScore() - Level
   *
   * Methods on Fields
   * this.playerPos.movePosn(String direction) - Posn
   * this.playerPos.withinBounds(Posn topLeft, Posn bottomRight) - boolean
   * this.playerPos.samePosn(Posn pos) - boolean
   *
   */
}

// Represents the current level state in Sokoban
class Sokoban extends World {
  Level level;

  // Constructor
  Sokoban(Level level) {
    this.level = level;
  }

  // creates the scene for this Sokoban world
  public WorldScene makeScene() {
    WorldImage levelImage = new ScaleImage(this.level.draw(), Constants.SCALE_FACTOR);
    WorldImage statsImage = this.level.drawStats();

    WorldScene levelScene = new WorldScene(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

    levelScene.placeImageXY(levelImage, Constants.SCENE_WIDTH / 2, Constants.SCENE_HEIGHT / 2);
    levelScene.placeImageXY(statsImage, 0, 0);

    return levelScene;
  }

  // creates the last scene for this Sokoban world
  public WorldScene lastScene(String s) {
    WorldImage levelImage = new TextImage("Game over", Color.red);
    WorldScene levelScene = new WorldScene(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
    levelScene.placeImageXY(levelImage, Constants.SCENE_WIDTH / 2, Constants.SCENE_HEIGHT / 2);
    return levelScene;
  }

  // handles key events
  public void onKeyEvent(String key) {
    if (key.equals("up") || key.equals("down") || key.equals("right") || key.equals("left")) {
      this.level = this.level.movePlayer(key).updateLevel();
    }
    if (key.equals("u")) {
      this.level = this.level.undoMove();
    }
    if (this.level.levelWon() || this.level.levelLost()) {
      endOfWorld("game over");
    }
  }

  /* Fields
   * this.level - Level
   * this.gameOver - boolean
   * Methods
   * this.makeScene() - WorldScene
   * this.onKeyEvent(String key) - void
   * Methods on Fields
   * level.getCell(Posn posn) - Cell
   * level.draw() - WorldImage
   * level.levelWon() - boolean
   * level.levelLost() - boolean
   * level.movePlayer(String direction, Level level) - Level
   * level.updateLevel() - Level
   * level.undoMove() - Level
   * level.updateScore() - Level
   */
}


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
  String exampleIceContent1;
  String exampleIceContent2;
  String exampleIceGround1;
  String exampleIceGround2;
  String exampleIceTestGround;
  String exampleIceTestContent;

  String gameGround;
  String gameContent;

  Level blernerLevel;
  Level blernerLevel2;
  Level blernerLevel2E;
  Level blernerLevel2R;
  Level blernerLevel2U;
  Level blernerLevel3;
  Level small;
  Level ice1;
  Level ice2;
  Level iceTest;


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

    exampleIceGround1 = "_________\n___II__B_\n_________";
    exampleIceContent1 = "WWWWWWWWW\nW>b_____W\nWWWWWWWWW";

    exampleIceTestGround =
            "_________\n_________\n___II____\n___II__B_\n___II__Y_\n___II__G_\n___II__R_" +
                    "\n___III___\n_________\n_________";
    exampleIceTestContent =
            "_________\n_________\n_________\n_>b______\n___y_____\n____g____\n_____r___" +
                    "\n__B______\n_________\n_________";


    exampleIceGround2 = "________\n__YI____\n__II____\n________\n________";
    exampleIceContent2 = "_WWWWWWW\nWW_____W\nW>y____W\nWW___WWW\n_WWWWW__";

    gameGround =
            "________\n" + "________\n" + "_B______\n" + "_____G__\n" + "_R______\n" + "___HY___\n"
                    + "___B__R_\n" + "____G___\n" + "________";
    gameContent =
            "__WWWWW_\n" + "WWW___W_\n" + "W_>b__W_\n" + "WWW_g_W_\n" + "W_WWy_W_\n" + "W_W___WW\n"
                    + "Wr_bgr_W\n" + "W______W\n" + "WWWWWWWW";

    blernerLevel = new Level(exampleLevelGround, exampleLevelContents);
    blernerLevel2 = new Level(exampleLevelGround, exampleLevelContents2);
    // blernerLevel2E = new Level(exampleLevelGround, exampleLevelContents2E);
    blernerLevel2R = new Level(exampleLevelGround, exampleLevelContents2R);
    blernerLevel2U = new Level(exampleLevelGround, exampleLevelContents2U);
    blernerLevel3 = new Level(exampleLevelGround, exampleLevelContents3);
    small = new Level(exampleSmallGround, exampleSmallContents);
    ice1 = new Level(exampleIceGround1, exampleIceContent1);
    ice2 = new Level(exampleIceGround2, exampleIceContent2);
    iceTest = new Level(exampleIceTestGround, exampleIceTestContent);

    gameLevel = new Level(gameGround, gameContent);

    game = new Sokoban(iceTest);
  }

  void testBigBang(Tester t) {
    this.init();

    game.bigBang(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, 1);
  }

  // Utils test
  void testColorToTrophy(Tester t) {
    this.init();
    Utils utils = new Utils(); // have to create to exception test

    t.checkExpect(Utils.colorToTrophySprite("blue"),
            new FromFileImage("./src/assets/trophy_blue.png"));
    t.checkExpect(Utils.colorToTrophySprite("green"),
            new FromFileImage("./src/assets/trophy_green.png"));
    t.checkExpect(Utils.colorToTrophySprite("red"),
            new FromFileImage("./src/assets/trophy_red.png"));
    t.checkExpect(Utils.colorToTrophySprite("yellow"),
            new FromFileImage("./src/assets/trophy_yellow.png"));
    t.checkExceptionType(RuntimeException.class, utils, "colorToTrophySprite", "black");
  }

  void testColorToTarget(Tester t) {
    this.init();
    Utils utils = new Utils();

    t.checkExpect(Utils.colorToTargetSprite("blue"), Constants.TARGET_BLUE_SPRITE);
    t.checkExpect(Utils.colorToTargetSprite("green"), Constants.TARGET_GREEN_SPRITE);
    t.checkExpect(Utils.colorToTargetSprite("red"), Constants.TARGET_RED_SPRITE);
    t.checkExpect(Utils.colorToTargetSprite("yellow"), Constants.TARGET_YELLOW_SPRITE);
    t.checkExceptionType(RuntimeException.class, utils, "colorToTargetSprite", "black");
  }

  // test level construction (also in Utils) ----------------------------------

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
    //ice
    t.checkExpect(Utils.makeGround("I"), new Ice());
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

    Level testMakeGridLevel = new Level(grid, new Posn(0, 1), null, 0);
    testMakeGridLevel.prevLevel = testMakeGridLevel;
    testMakeGridLevel.playerPos = new Posn(0, 1);

    t.checkExpect(Utils.makeGrid("_R\n" + "G_", "Wr\n" + ">B"), grid);
    t.checkExpect(testMakeGridLevel, new Level("_R\n" + "G_", "Wr\n" + ">B"));
    t.checkConstructorExceptionType(IllegalArgumentException.class, "Level", "_R\nG__",
            "Wr\n" + ">B");
    t.checkConstructorExceptionType(IllegalArgumentException.class, "Level", exampleLevelGround,
            exampleLevelContents2E);

    this.init();

    t.checkExpect(this.gameLevel.board.size(), 9);
    t.checkExpect(this.gameLevel.board.get(0).size(), 8);

  }

  void testCellWon(Tester t) {
    this.init();

    t.checkExpect(new Cell(new Normal(), new Blank()).cellWon(), true);
    t.checkExpect(new Cell(new Normal(), new Trophy("red")).cellWon(), true);
    t.checkExpect(new Cell(new Target("red"), new Blank()).cellWon(), false);
    t.checkExpect(new Cell(new Target("red"), new Trophy("red")).cellWon(), true);
    t.checkExpect(new Cell(new Target("red"), new Trophy("green")).cellWon(), false);
  }

  void testCellLost(Tester t) {
    this.init();

    t.checkExpect(new Cell(new Normal(), new Blank()).cellLost(), true);
    t.checkExpect(new Cell(new Normal(), new Trophy("blue")).cellLost(), true);
    t.checkExpect(new Cell(new Target("red"), new Box()).cellLost(), true);
    t.checkExpect(new Cell(new Hole(), new Blank()).cellLost(), true);
    t.checkExpect(new Cell(new Hole(), new Player("up")).cellLost(), false);
    t.checkExpect(new Cell(new Normal(), new Player("up")).cellLost(), false);
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

  void testLevelLost(Tester t) {
    this.init();

    String levelLostGround = "_HRB_Y_G";
    String levelNotLostContent = "v_rg_y_b";

    Level smallSoonLost = new Level(levelLostGround, levelNotLostContent);
    Sokoban world = new Sokoban(smallSoonLost);

    t.checkExpect(smallSoonLost.levelLost(), false);
    world.onKeyEvent("right");
    t.checkExpect(world.level.levelLost(), true);
  }

  void testPlayerIndex(Tester t) {
    this.init();

    t.checkExpect(Utils.findPlayerIdxInRow("________\n"), -1);
    t.checkExpect(Utils.findPlayerIdxInRow("W_b>yB_W\n"), 3);
    t.checkExpect(Utils.findPlayerIdxInRow(">Br"), 0);
    t.checkExpect(Utils.findPlayerIdxInRow(""), -1);
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
  // Posn tests -----------------------------

  void testMovePosn(Tester t) {
    this.init();

    t.checkExpect(new Posn(0, 0).movePosn("up"), new Posn(0, -1));
    t.checkExpect(new Posn(0, 0).movePosn("down"), new Posn(0, 1));
    t.checkExpect(new Posn(0, 0).movePosn("left"), new Posn(-1, 0));
    t.checkExpect(new Posn(0, 0).movePosn("right"), new Posn(1, 0));
  }

  void testWithinBounds(Tester t) {
    this.init();

    t.checkExpect(new Posn(0, 0).withinBounds(new Posn(0, 0), new Posn(3, 4)), true);
    t.checkExpect(new Posn(2, 5).withinBounds(new Posn(0, 5), new Posn(1, 8)), false);
  }

  void testSamePosn(Tester t) {
    t.checkExpect(new Posn(0, 0).samePosn(new Posn(0, 0)), true);
    t.checkExpect(new Posn(1, 0).samePosn(new Posn(0, 0)), false);
  }

  // test Level methods -------------------

  void testFindCcell(Tester t) {
    this.init();

    t.checkExpect(gameLevel.findCell(new Posn(0, 0)), new Cell(new Normal(), new Blank()));
    t.checkExpect(blernerLevel3.findCell(new Posn(4, 2)),
            new Cell(new Normal(), new Trophy("red")));
    t.checkExceptionType(RuntimeException.class, gameLevel, "findCell", new Posn(100, 100));
  }

  void testDrawLevel(Tester t) {
    this.init();

    WorldImage NormalPlayer = new Cell(new Normal(), new Player("right")).cellToImage();
    WorldImage RedBox = new Cell(new Target("red"), new Box()).cellToImage();
    WorldImage NormalRed = new Cell(new Normal(), new Trophy("red")).cellToImage();

    WorldImage rowTwoImage = new BesideImage(
            new BesideImage(new BesideImage(new EmptyImage(), NormalPlayer), RedBox), NormalRed);

    String smallRowTwoGround = "_R_";
    String smallRowTwoContent = ">Br";

    Level rowTwo = new Level(smallRowTwoGround, smallRowTwoContent);
    t.checkExpect(rowTwo.draw(), new AboveImage(new EmptyImage(), rowTwoImage));

    WorldImage NormalWall = new Cell(new Normal(), new Wall()).cellToImage();
    WorldImage NormalBlue = new Cell(new Normal(), new Trophy("blue")).cellToImage();
    WorldImage BlueBlank = new Cell(new Target("blue"), new Blank()).cellToImage();

    t.checkExpect(small.draw(), new AboveImage(new AboveImage(new EmptyImage(),
            new BesideImage(new BesideImage(new BesideImage(new EmptyImage(), NormalWall),
                    NormalBlue), BlueBlank)), rowTwoImage));
  }

  void testDrawStats(Tester t) {
    this.init();


  }

  void testMovePlayer(Tester t) {
    this.init();

    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2));
    this.gameLevel = this.gameLevel.movePlayer("up");
    // push wall - no change, move into wall - no change
    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2));
    this.gameLevel = this.gameLevel.movePlayer("left");
    // push blank into wall - no change, move into blank - moved
    t.checkExpect(this.gameLevel.playerPos, new Posn(1, 2));
    this.gameLevel = this.gameLevel.movePlayer("right");
    // push blank into trophy - no change, move into blank - moved
    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2));
    this.gameLevel = this.gameLevel.movePlayer("right");
    // push trophy into blank - pushed, move into blank - moved
    t.checkExpect(this.gameLevel.playerPos, new Posn(3, 2));
    this.gameLevel = this.gameLevel.movePlayer("right");
    t.checkExpect(this.gameLevel.playerPos, new Posn(4, 2));
    this.gameLevel = this.gameLevel.movePlayer("right");
    // push trophy into wall - no change, move into trophy - no change
    t.checkExpect(this.gameLevel.playerPos, new Posn(4, 2));
  }

  void testswitchContents(Tester t) {
    this.init();

    t.checkExpect(this.gameLevel.switchContents(new Posn(0, 0), new Posn(1, 0),
            this.gameLevel.playerPos, this.gameLevel, 0), this.gameLevel);

    init();

    this.gameLevel = this.gameLevel.switchContents(new Posn(2, 2), new Posn(3, 2),
            this.gameLevel.playerPos, this.gameLevel, 0);

    t.checkExpect(this.gameLevel.findCell(new Posn(3, 2)),
            new Cell(new Normal(), new Player("right")));

    this.init();

    this.gameLevel = this.gameLevel.switchContents(new Posn(2, 2), new Posn(1, 2),
            this.gameLevel.playerPos, this.gameLevel, 0);

    t.checkExpect(this.gameLevel.findCell(new Posn(1, 2)),
            new Cell(new Target("blue"), new Player("right")));

  }

  // untested Cell tests

  void testCellToImage(Tester t) {
    WorldImage NormalWall = new Cell(new Normal(), new Wall()).cellToImage();
    WorldImage NormalBlue = new Cell(new Normal(), new Trophy("blue")).cellToImage();
    WorldImage BlueBlank = new Cell(new Target("blue"), new Blank()).cellToImage();

    t.checkExpect(NormalWall,
            new OverlayImage(new FromFileImage("./src/assets/wall.png"), Constants.NORMAL_SPRITE));
    t.checkExpect(NormalBlue,
            new OverlayImage(Utils.colorToTrophySprite("blue"), Constants.NORMAL_SPRITE));
    t.checkExpect(BlueBlank, new OverlayImage(new EmptyImage(), Constants.TARGET_BLUE_SPRITE));
  }

  void testCellFall(Tester t) {
    // since cell.cellFall() simply returns cell.content.groundFall(cell.content), there tests will
    // have the same functionality and result
    this.init();

    Cell toFallCell = new Cell(new Hole(), new Box());
    Cell noFallCell = new Cell(new Hole(), new Blank());
    Cell nowhereToFallCell = new Cell(new Normal(), new Trophy("blue"));

    t.checkExpect(toFallCell.cellFall(), true);
    t.checkExpect(noFallCell.cellFall(), false);
    t.checkExpect(nowhereToFallCell.cellFall(), false);
  }

  void testCanMoveTo(Tester t) {
    this.init();

    t.checkExpect(gameLevel.findCell(new Posn(2, 2)).content.canMoveTo(new Posn(1, 2), gameLevel),
            true); // move player to blank - true
    t.checkExpect(gameLevel.findCell(new Posn(2, 2)).content.canMoveTo(new Posn(2, 1), gameLevel),
            false); // move player to wall - false
    t.checkExpect(gameLevel.findCell(new Posn(2, 2)).content.canMoveTo(new Posn(3, 2), gameLevel),
            false); // move player to trophy - false
    t.checkExpect(gameLevel.findCell(new Posn(3, 2)).content.canMoveTo(new Posn(4, 2), gameLevel),
            true); // move trophy to blank - true
    t.checkExpect(gameLevel.findCell(new Posn(4, 4)).content.canMoveTo(new Posn(3, 4), gameLevel),
            false); // move trophy to wall - false
    t.checkExpect(gameLevel.findCell(new Posn(1, 2)).content.canMoveTo(new Posn(1, 1), gameLevel),
            false); // move blank to wall - false
    t.checkExpect(gameLevel.findCell(new Posn(0, 0)).content.canMoveTo(new Posn(1, 0), gameLevel),
            false); // move blank to blank - true
  }

  // Ground methods

  void testGroundToImage(Tester t) {
    this.init();

    t.checkExpect(new Normal().groundToImage(), Constants.NORMAL_SPRITE);
    t.checkExpect(new Target("red").groundToImage(), Constants.TARGET_RED_SPRITE);
    t.checkExpect(new Target("blue").groundToImage(), Constants.TARGET_BLUE_SPRITE);
    t.checkExpect(new Target("yellow").groundToImage(), Constants.TARGET_YELLOW_SPRITE);
    t.checkExpect(new Target("green").groundToImage(), Constants.TARGET_GREEN_SPRITE);
    t.checkExpect(new Hole().groundToImage(), Constants.HOLE_SPRITE);
  }

  void testGroundWon(Tester t) {
    this.init();

    t.checkExpect(new Normal().groundWon(new Trophy("blue")), true);
    t.checkExpect(new Hole().groundWon(new Trophy("blue")), true);
    t.checkExpect(new Target("blue").groundWon(new Trophy("red")), false);
    t.checkExpect(new Target("blue").groundWon(new Trophy("blue")), true);
    t.checkExpect(new Target("yellow").groundWon(new Trophy("yellow")), true);
    t.checkExpect(new Target("green").groundWon(new Trophy("green")), true);
    t.checkExpect(new Target("red").groundWon(new Trophy("red")), true);
  }

  void testGroundFall(Tester t) {
    this.init();

    t.checkExpect(new Hole().groundFall(new Box()), true);
    t.checkExpect(new Hole().groundFall(new Trophy("red")), true);
    t.checkExpect(new Hole().groundFall(new Player("down")), true);
    t.checkExpect(new Hole().groundFall(new Blank()), false);
    t.checkExpect(new Normal().groundFall(new Box()), false);
  }

  // Content Methods
  void testContentToImage(Tester t) {
    this.init();

    t.checkExpect(new Blank().contentToImage(), new EmptyImage());
    t.checkExpect(new Wall().contentToImage(), Constants.WALL_SPRITE);
    t.checkExpect(new Box().contentToImage(), Constants.BOX_SPRITE);

    t.checkExpect(new Trophy("red").contentToImage(), Utils.colorToTrophySprite("red"));
    t.checkExpect(new Trophy("blue").contentToImage(), Utils.colorToTrophySprite("blue"));
    t.checkExpect(new Trophy("yellow").contentToImage(), Utils.colorToTrophySprite("yellow"));
    t.checkExpect(new Trophy("green").contentToImage(), Utils.colorToTrophySprite("green"));

    t.checkExpect(new Player("down").contentToImage(), Constants.PLAYER_SPRITE);
    t.checkExpect(new Player("right").contentToImage(), Constants.PLAYER_SPRITE);
    t.checkExpect(new Player("left").contentToImage(), Constants.PLAYER_SPRITE);
    t.checkExpect(new Player("up").contentToImage(), Constants.PLAYER_SPRITE);
  }

  void testContentWon(Tester t) {
    this.init();

    t.checkExpect(new Blank().contentWon(new Target("red")), false);
    t.checkExpect(new Wall().contentWon(new Target("red")), false);
    t.checkExpect(new Box().contentWon(new Target("red")), false);

    t.checkExpect(new Trophy("green").contentWon(new Target("red")), false);
    t.checkExpect(new Trophy("red").contentWon(new Target("red")), true);
    t.checkExpect(new Trophy("green").contentWon(new Target("green")), true);
    t.checkExpect(new Trophy("blue").contentWon(new Target("blue")), true);
    t.checkExpect(new Trophy("yellow").contentWon(new Target("yellow")), true);

    t.checkExpect(new Player("right").contentWon(new Target("red")), false);
  }

  void testContentLost(Tester t) {
    this.init();

    t.checkExpect(new Blank().contentLost(), true);
    t.checkExpect(new Wall().contentLost(), true);
    t.checkExpect(new Box().contentLost(), true);
    t.checkExpect(new Trophy("green").contentLost(), true);
    t.checkExpect(new Player("down").contentLost(), false);
  }

  void testIsVacant(Tester t) {
    this.init();

    t.checkExpect(new Blank().isVacant(), true);
    t.checkExpect(new Wall().isVacant(), false);
    t.checkExpect(new Box().isVacant(), false);
    t.checkExpect(new Trophy("red").isVacant(), false);
    t.checkExpect(new Player("right").isVacant(), false);
  }

  void testIsFixed(Tester t) {
    this.init();

    t.checkExpect(new Blank().isFixed(), true);
    t.checkExpect(new Wall().isFixed(), true);
    t.checkExpect(new Box().isFixed(), false);
    t.checkExpect(new Trophy("red").isFixed(), false);
    t.checkExpect(new Player("right").isFixed(), false);
  }

  void testContentFall(Tester t) {
    this.init();

    t.checkExpect(new Blank().contentFall(new Normal()), false);
    t.checkExpect(new Wall().contentFall(new Hole()), true);
    t.checkExpect(new Trophy("red").contentFall(new Hole()), true);
    t.checkExpect(new Player("left").contentFall(new Hole()), true);
    t.checkExpect(new Box().contentFall(new Hole()), true);
  }

  void testUpdateLevel(Tester t) {
    this.init();

    String testUpdateGround = "H_HR\n" + "___H";
    String testUpdateContent = "_rb_\n" + "_vB_";

    Level updateTest = new Level(testUpdateGround, testUpdateContent);
    t.checkExpect(updateTest.findCell(new Posn(2, 0)), new Cell(new Hole(), new Trophy("blue")));
    updateTest = updateTest.updateLevel();
    t.checkExpect(updateTest.findCell(new Posn(2, 0)), new Cell(new Normal(), new Blank()));
    // hole on the bottom right
    t.checkExpect(updateTest.findCell(new Posn(3, 1)), new Cell(new Hole(), new Blank()));
    // push box into the hole at bottom right
    updateTest = updateTest.movePlayer("right");
    updateTest = updateTest.updateLevel();
    t.checkExpect(updateTest.findCell(new Posn(3, 1)), new Cell(new Normal(), new Blank()));
    updateTest = updateTest.movePlayer("up");
    updateTest = updateTest.movePlayer("left");
    updateTest = updateTest.movePlayer("left");
    updateTest = updateTest.updateLevel();
    t.checkExpect(updateTest.findCell(new Posn(0, 0)), new Cell(new Normal(), new Blank()));
  }

  void testUndoMove(Tester t) {
    this.init();

    // FIXME gamelevel.playerpos field of field in tests allowed? (also in testMovePlayer)
    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2)); // init player pos
    this.gameLevel = this.gameLevel.movePlayer("left");
    t.checkExpect(this.gameLevel.playerPos, new Posn(1, 2)); // check player moved left
    this.gameLevel = this.gameLevel.undoMove();
    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2)); // player back at init pos
    this.gameLevel = this.gameLevel.movePlayer("right");
    t.checkExpect(this.gameLevel.playerPos, new Posn(3, 2)); // check player pushed and moved right
    this.gameLevel = this.gameLevel.movePlayer("right");
    t.checkExpect(this.gameLevel.playerPos, new Posn(4, 2)); // check player pushed and moved right
    this.gameLevel = this.gameLevel.undoMove();
    t.checkExpect(this.gameLevel.playerPos, new Posn(3, 2)); // check player pos undo
    t.checkExpect(this.gameLevel.findCell(new Posn(4, 2)).content,
            new Trophy("blue")); // check trophy undo
    this.gameLevel = this.gameLevel.undoMove();
    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2)); // check player pos undo
    t.checkExpect(this.gameLevel.findCell(new Posn(3, 2)).content,
            new Trophy("blue")); // check trophy undo
    this.gameLevel = this.gameLevel.undoMove();
    t.checkExpect(this.gameLevel.playerPos, new Posn(2, 2)); // no change, no more prev level
  }

  void testUpdateScore(Tester t) {
    this.init();

    // FIXME fof
    t.checkExpect(this.gameLevel.steps, 0); // init score
    this.gameLevel = this.gameLevel.movePlayer("left");
    t.checkExpect(this.gameLevel.steps, 1); // score inc on move
    this.gameLevel = this.gameLevel.movePlayer("up");
    t.checkExpect(this.gameLevel.steps, 1); // no move, no change
    this.gameLevel = this.gameLevel.undoMove();
    t.checkExpect(this.gameLevel.steps, 2); // score inc on undo
  }


}
