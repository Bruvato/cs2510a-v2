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

  // TODO ensure file paths are correct
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
  /* Fields
   * Methods
   * Methods on Fields
   */
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
    return topLeft.col <= this.col && topLeft.row <= this.row
            && bottomRight.col >= this.col && bottomRight.row >= this.row;
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

  // Determines if the player can move to this Cell
  // EFFECT: if the player can move to this cell AFTER pushing, the given board will the content of
  //         the cell at the given Posn mutated
  boolean moveIfPossible(Posn pushToPos, Level level) {
    return this.content.checkMoveOrDoPush(pushToPos, level);
    /* Fields of Parameters
     * pushToPos.col - int
     * pushToPos.row - int
     * level.board - ArrayList<ArrayList<Cell>>
     * level.playerPos - Posn
     * level.groundStr - String
     * level.contentStr - String
     * Methods on Parameters
     * pushToPos.movePosn(String direction) - Posn
     * pushToPos.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     * level.getCell(Posn posn) - Cell
     * level.draw() - WorldImage
     * level.levelWon() - boolean
     * level.levelLost() - boolean
     * level.movePlayer(String direction) - void
     * level.resetLevel() - void
     * level.updateLevel() - void
     */
  }

  /* Fields
   * this.ground - IGround
   * this.content - IContent
   * Methods
   * this.cellToImage() - WorldImage
   * this.cellWon() - boolean
   * this.cellLost() - boolean
   * this.cellFall() - boolean
   * this.moveIfPossible(Posn pushToPos, Level level) - boolean
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
   * this.content.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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
     * other.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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
     * other.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
     * other.contentFall(IGround other) - boolean
     */
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

  // Determines if this content will fall into the given ground
  boolean contentFall(IGround other);

  // Determines if the player can move onto the cell that this content is held in
  // EFFECT: Mutates the Cell at the given posn in the board if this content is pushable
  boolean checkMoveOrDoPush(Posn pushToPos, Level level);

  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * this.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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

  // Determines if the player can move onto the cell that this content is held in
  // EFFECT: Mutates the Cell at the given posn in the board of the level
  //         if this content is pushable
  public boolean checkMoveOrDoPush(Posn pushToPos, Level level) {
    Posn topLeft = new Posn(0, 0);
    Posn bottomRight = new Posn(level.board.get(0).size() - 1, level.board.size() - 1);

    // if the posn we are trying to push to is within bounds AND has nothing in it...
    if (pushToPos.withinBounds(topLeft, bottomRight) && level.getCell(
            pushToPos).content.isVacant()) {
      level.getCell(pushToPos).content = this; // push myself into that cell's content
      return true; // which makes it possible for player to move
    }
    return false;
  }
  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * this.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
   * Methods on Fields
   */
}

// --------- GROUND OBJECTS ------------------------

// Represents Normal ground in the game Sokoban
class Normal extends AGround {

  // draws this blank object on the ground layer
  public WorldImage groundToImage() {
    return Constants.BLANK_SPRITE;
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
     * other.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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
    return new OverlayImage(Constants.HOLE_SPRITE, Constants.BLANK_SPRITE);
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
     * other.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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

  // Determines if the player can move onto the cell that this content is held in
  // EFFECT: Mutates the Cell at the given posn in the board if this content is pushable
  public boolean checkMoveOrDoPush(Posn pushToPos, Level level) {
    return true;
    /* Fields of Parameters
     * pushToPos.col - int
     * pushToPos.row - int
     * level.board - ArrayList<ArrayList<Cell>>
     * level.playerPos - Posn
     * level.groundStr - String
     * level.contentStr - String
     * Methods on Parameters
     * pushToPos.movePosn(String direction) - Posn
     * pushToPos.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     * level.getCell(Posn posn) - Cell
     * level.draw() - WorldImage
     * level.levelWon() - boolean
     * level.levelLost() - boolean
     * level.movePlayer(String direction) - void
     * level.resetLevel() - void
     * level.updateLevel() - void
     */
  }
  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * this.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
   * Methods on Fields
   */
}

// Represents a wall in Sokoban
class Wall extends AContent {

  // Draws this Wall as its WorldImage equivalent
  public WorldImage contentToImage() {
    return Constants.WALL_SPRITE;
  }

  // Determines if the player can move onto the cell that this wall is held in
  // EFFECT: Mutates the Cell at the given posn in the board if this content is pushable
  public boolean checkMoveOrDoPush(Posn pushToPos, Level level) {
    return false;
    /* Fields of Parameters
     * pushToPos.col - int
     * pushToPos.row - int
     * level.board - ArrayList<ArrayList<Cell>>
     * level.playerPos - Posn
     * level.groundStr - String
     * level.contentStr - String
     * Methods on Parameters
     * pushToPos.movePosn(String direction) - Posn
     * pushToPos.withinBounds(Posn topLeft, Posn bottomRight) - boolean
     * level.getCell(Posn posn) - Cell
     * level.draw() - WorldImage
     * level.levelWon() - boolean
     * level.levelLost() - boolean
     * level.movePlayer(String direction) - void
     * level.resetLevel() - void
     * level.updateLevel() - void
     */
  }
  /* Fields
   * Methods
   * this.contentToImage() - WorldImage
   * this.contentWon(IContent other) - boolean
   * this.contentLost(IContent other) - boolean
   * this.isVacant() - boolean
   * this.contentFall() - boolean
   * this.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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
   * this.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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
   * this.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
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
   * this.checkMoveOrDoPush(Posn pushToPos, Level level) - boolean
   * Methods on Fields
   */
}

// --------------- LEVEL -----------------------

// Represents a Level in Sokoban
class Level {
  // the board is represented with (0,0) being the top left of the game.
  // (size of board, size of an array in board) is the bottom right cell in the game
  // as well as the last item in the last array of the arrayList.

  // a sokoban board
  ArrayList<ArrayList<Cell>> board;
  Posn playerPos;
  String groundStr;
  String contentStr;

  // configures this level given a board: list of lists of cells
  Level(ArrayList<ArrayList<Cell>> board) {
    this.board = board;
    this.playerPos = null;
    this.groundStr = null;
    this.contentStr = null;
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

  // returns the cell at the given position in this sokoban board
  Cell getCell(Posn posn) {
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

  // Moves the player in the given direction if possible. The player can only move onto a ground
  // that is empty or push one object IFF that object has space to be pushed
  void movePlayer(String direction) {
    Posn moveToPos = this.playerPos.movePosn(direction); // pos of where player is moving to
    Posn pushToPos = moveToPos.movePosn(direction); // pos of where pushable is moving to
    Posn topLeft = new Posn(0, 0);
    Posn bottomRight = new Posn(this.board.get(0).size() - 1, this.board.size() - 1);

    Cell playerCell = this.getCell(this.playerPos);

    if (moveToPos.withinBounds(topLeft, bottomRight) && this.getCell(moveToPos)
            .moveIfPossible(pushToPos, this)) {
      this.getCell(moveToPos).content = playerCell.content;
      playerCell.content = new Blank();
      this.playerPos = moveToPos;
    }
  }

  // resets this level to its initial state
  void resetLevel() {
    this.board = Utils.makeGrid(this.groundStr, this.contentStr);
    this.playerPos = Utils.findPlayer(this.contentStr);
  }

  // updates this level and the cells within it
  void updateLevel() {
    // EFFECT: Changes all cells in the board to account for any object on a hole
    for (ArrayList<Cell> row : this.board) {
      // EFFECT: Changes all cells in a row in the board to account for any object on a hole
      for (Cell cell : row) {
        if (cell.cellFall()) {
          cell.ground = new Normal();
          cell.content = new Blank();
        }
      }
    }
  }

  /* Fields
   * this.board - ArrayList<ArrayList<Cell>>
   * this.playerPos - Posn
   * this.groundStr - String
   * this.contentStr - String
   * Methods
   * this.getCell(Posn posn) - Cell
   * this.draw() - WorldImage
   * this.levelWon() - boolean
   * this.levelLost() - boolean
   * this.movePlayer(String direction) - void
   * this.resetLevel() - void
   * this.updateLevel() - void
   * Methods on Fields
   * this.playerPos.movePosn(String direction) - Posn
   * this.playerPos.withinBounds(Posn topLeft, Posn bottomRight) - boolean
   */
}

// Represents the current level state in Sokoban
class Sokoban extends World {
  Level level;
  boolean gameOver;

  // Constructor
  Sokoban(Level level) {
    this.level = level;
    this.updateGameState();
  }

  // creates the scene for this Sokoban world
  public WorldScene makeScene() {
    WorldImage levelImage;

    if (this.gameOver) {
      levelImage = new TextImage("Game over (press r to reset)", Color.red);
    } else {
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

  // Updates the state of the game and this gameOver boolean to represent if the game is over
  void updateGameState() {
    this.level.updateLevel();
    this.gameOver = this.level.levelLost() || this.level.levelWon();
  }
  /* Fields
   * this.level - Level
   * this.gameOver - boolean
   * Methods
   * this.makeScene() - WorldScene
   * this.onKeyEvent(String key) - void
   * this.updateGameState() - void
   * Methods on Fields
   * level.getCell(Posn posn) - Cell
   * level.draw() - WorldImage
   * level.levelWon() - boolean
   * level.levelLost() - boolean
   * level.movePlayer(String direction) - void
   * level.resetLevel() - void
   * level.updateLevel() - void
   */
}

// TODO
//  - 2 worlds
//  - return game over world when lose
//  - update level, check win/lose conditions on start/initialization

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

    gameGround =
            "________\n" + "________\n" + "_B______\n" + "_____G__\n" + "_R______\n" + "___HY___\n"
                    + "___B__R_\n" + "____G___\n" + "________\n";
    gameContent =
            "__WWWWW_\n" + "WWW___W_\n" + "W_>b__W_\n" + "WWW_g_W_\n" + "W_WWy_W_\n" + "W_W___WW\n"
                    + "Wr_bgr_W\n" + "W______W\n" + "WWWWWWWW\n";

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

  // Utils test
  void testColorToTrophy(Tester t) {
    this.init();
    Utils utils = new Utils(); // have to create to exception test

    t.checkExpect(Utils.colorToTrophySprite("blue"), new FromFileImage("./src/assets/trophy_blue.png"));
    t.checkExpect(Utils.colorToTrophySprite("green"), new FromFileImage("./src/assets/trophy_green.png"));
    t.checkExpect(Utils.colorToTrophySprite("red"), new FromFileImage("./src/assets/trophy_red.png"));
    t.checkExpect(Utils.colorToTrophySprite("yellow"), new FromFileImage("./src/assets/trophy_yellow.png"));
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

    Level testMakeGridLevel = new Level(grid);
    testMakeGridLevel.playerPos = new Posn(0, 1);
    testMakeGridLevel.groundStr = "_R\n" + "G_";
    testMakeGridLevel.contentStr = "Wr\n" + ">B";

    t.checkExpect(Utils.makeGrid("_R\n" + "G_", "Wr\n" + ">B"), grid);
    t.checkExpect(testMakeGridLevel, new Level("_R\n" + "G_", "Wr\n" + ">B"));
    t.checkConstructorExceptionType(IllegalArgumentException.class, "Level",
            "_R\nG__", "Wr\n" + ">B");
    t.checkConstructorExceptionType(IllegalArgumentException.class, "Level",
            exampleLevelGround, exampleLevelContents2E);
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
    t.checkExpect(new Cell(new Hole(), new Player("^")).cellLost(), false);
    t.checkExpect(new Cell(new Normal(), new Player("^")).cellLost(), false);
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

    t.checkExpect(new Posn(0, 0).withinBounds(new Posn(0, 0),
            new Posn(3, 4)), true);
    t.checkExpect(new Posn(2, 5).withinBounds(new Posn(0, 5),
            new Posn(1, 8)), false);
  }
// test Level methods -------------------

  void testGetCell(Tester t) {
    this.init();

    t.checkExpect(gameLevel.getCell(new Posn(0, 0)), new Cell(new Normal(), new Blank()));
    t.checkExpect(blernerLevel3.getCell(new Posn(4, 2)), new Cell(new Normal(), new Trophy("red")));
    t.checkExceptionType(RuntimeException.class, gameLevel, "getCell", new Posn(100, 100));
  }

  void testDrawLevel(Tester t) {
    this.init();

    WorldImage NormalPlayer = new Cell(new Normal(), new Player(">")).cellToImage();
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

    t.checkExpect(small.draw(), new AboveImage(new AboveImage(new EmptyImage(), new BesideImage(
            new BesideImage(new BesideImage(new EmptyImage(), NormalWall), NormalBlue), BlueBlank)),
            rowTwoImage));
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

  void testResetLevel(Tester t) {
    this.init();

    t.checkExpect(gameLevel.getCell(new Posn(2, 2)),
            new Cell(new Normal(), new Player("right")));

    gameLevel.movePlayer("right");
    gameLevel.movePlayer("right");
    gameLevel.movePlayer("right");

    t.checkFail(gameLevel.getCell(new Posn(2, 2)),
            new Cell(new Normal(), new Player("right")));

    gameLevel.resetLevel();
    t.checkExpect(gameLevel.getCell(new Posn(2, 2)),
            new Cell(new Normal(), new Player("right")));
  }

  void testUpdateLevel(Tester t) {
    this.init();

    String testUpdateGround = "H_HR\n" + "___H";
    String testUpdateContent = "_rb_\n" + "_vB_";

    Level updateTest = new Level(testUpdateGround, testUpdateContent);
    t.checkExpect(updateTest.getCell(new Posn(2, 0)), new Cell(new Hole(), new Trophy("blue")));
    updateTest.updateLevel();
    t.checkExpect(updateTest.getCell(new Posn(2, 0)), new Cell(new Normal(), new Blank()));
    // hole on the bottom right
    t.checkExpect(updateTest.getCell(new Posn(3, 1)), new Cell(new Hole(), new Blank()));
    // push box into the hole at bottom right
    updateTest.movePlayer("right");
    updateTest.updateLevel();
    t.checkExpect(updateTest.getCell(new Posn(3, 1)), new Cell(new Normal(), new Blank()));
    updateTest.movePlayer("up");
    updateTest.movePlayer("left");
    updateTest.movePlayer("left");
    updateTest.updateLevel();
    t.checkExpect(updateTest.getCell(new Posn(0, 0)), new Cell(new Normal(), new Blank()));
  }

  // untested Cell tests

  void testCellToImage(Tester t) {
    WorldImage NormalWall = new Cell(new Normal(), new Wall()).cellToImage();
    WorldImage NormalBlue = new Cell(new Normal(), new Trophy("blue")).cellToImage();
    WorldImage BlueBlank = new Cell(new Target("blue"), new Blank()).cellToImage();

    t.checkExpect(NormalWall, new OverlayImage(new FromFileImage("./src/assets/wall.png"),
            Constants.BLANK_SPRITE));
    t.checkExpect(NormalBlue, new OverlayImage(Utils.colorToTrophySprite("blue"),
            Constants.BLANK_SPRITE));
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

//  void testMoveIfPossible(Tester t) {
//    // TODO
//  }

  // Ground methods

  void testGroundFall(Tester t) {
    this.init();

    t.checkExpect(new Hole().groundFall(new Box()), true);
    t.checkExpect(new Hole().groundFall(new Trophy("red")), true);
    t.checkExpect(new Hole().groundFall(new Player("down")), true);
    t.checkExpect(new Hole().groundFall(new Blank()), false);
    t.checkExpect(new Normal().groundFall(new Box()), false);
  }
}
