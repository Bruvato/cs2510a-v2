import java.awt.Color;
import javalib.funworld.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;
import tester.Tester;
import java.util.function.*;

// a cell
class Cell {
    ICellObject ground;
    ICellObject content;

    /*
     * TEMPLATE: Fields: ... this.ground ... -- ICellObject ... this.content ... --
     * ICellObject
     *
     * Methods: ... this.stackCell(Cell) ... -- Cell ... this.cellToImage() ... --
     * WorldImage
     *
     * Methods for Fields: ... this.ground.stackWith(ICellObject) ... -- ICellObject
     * ... this.content.stackWith(ICellObject) ... -- ICellObject ...
     * this.ground.cellObjToImage() ... -- WorldImage ...
     * this.content.cellObjToImage() ... -- WorldImage
     */

    // initializes a cell with a given ground object and content object
    public Cell(ICellObject ground, ICellObject content) {
        this.ground = ground;
        this.content = content;
    }

    // initializes a cell with a given ground object
    public Cell(AGround ground) {
        this.ground = ground;
        this.content = new Blank();
    }

    // initializes a cell with a given content object
    public Cell(AContent content) {
        this.ground = new Blank();
        this.content = content;
    }

    // initializes a empty cell
    public Cell() {
        this.ground = new Blank();
        this.content = new Blank();
    }

    // stacks this cell with another given cell
    Cell stackCell(Cell cell) {
        return new Cell(this.ground.stackWith(cell.ground), this.content.stackWith(cell.content));
    }

    // converts this cell to an image
    WorldImage cellToImage() {
        return new OverlayImage(this.content.cellObjToImage(), this.ground.cellObjToImage());
    }

    // determines if this cell won
    boolean cellWon() {
        return this.ground.sameColor(this.content);
    }
}

interface ICellObject {
    /*
     * TEMPLATE: Methods: ... this.stackWith(ICellObject) ... -- ICellObject ...
     * this.stackWithGround(AGround) ... -- ICellObject ...
     * this.stackWithContent(AContent) ... -- ICellObject ... this.cellObjToImage()
     * ... -- WorldImage
     */

    // stacks this cell object with another given
    ICellObject stackWith(ICellObject other);

    // stacks this cell object with another given ground object
    ICellObject stackWithGround(AGround other);

    // stacks this cell object with another given content object
    ICellObject stackWithContent(AContent other);

    // converts this cell object to an image
    WorldImage cellObjToImage();

    // determines if this cell object is the same color as another given
    boolean sameColor(ICellObject other);

    // determines if this cell object is the same color as a given trophy
    boolean sameColorTrophy(Trophy other);

    // determines if this cell object is the same color as a given target
    boolean sameColorTarget(Target other);

}

// a ground object in a cell
abstract class AGround implements ICellObject {

    /*
     * TEMPLATE: Methods: ... this.cellObjToImage() ... -- WorldImage
     */

    // stacks this ground object with a given cell object
    public ICellObject stackWith(ICellObject other) {
        return other.stackWithGround(this);
    }

    // stacks this ground object with a given ground object
    public ICellObject stackWithGround(AGround other) {
        throw new IllegalArgumentException(
                "invalid stack: cannot stack this gound object with an existing ground object");
    }

    // stacks this ground object with a given content object
    public ICellObject stackWithContent(AContent other) {
        throw new IllegalArgumentException(
                "invalid stack: cannot stack this gound object with an existing content object");
    }

    // converts this ground cell object to an image
    public abstract WorldImage cellObjToImage();

    // determines if this ground cell object is the same color as another given
    public boolean sameColor(ICellObject other) {
        return true;
    }

    // determines if this ground cell object is the same color as a given trophy
    public boolean sameColorTrophy(Trophy other) {
        return false;
    }

    // determines if this ground cell object is the same color as a given target
    public boolean sameColorTarget(Target other) {
        return false;
    }

}

// a content object in a cell
abstract class AContent implements ICellObject {

    /*
     * TEMPLATE: Methods: ... this.cellObjToImage() ... -- WorldImage
     */

    // stacks this content object with a given cell object
    public ICellObject stackWith(ICellObject other) {
        return other.stackWithContent(this);
    }

    // stacks this content object with a given ground object
    public ICellObject stackWithGround(AGround other) {
        throw new IllegalArgumentException(
                "invalid stack: cannot stack this content object with an existing ground object");
    }

    // stacks this content object with a given content object
    public ICellObject stackWithContent(AContent other) {
        throw new IllegalArgumentException(
                "invalid stack: cannot stack this content object with an existing content object");
    }

    // converts this content cell object to an image
    public abstract WorldImage cellObjToImage();

    // determines if this content cell object is the same color as another given
    public boolean sameColor(ICellObject other) {
        return true;
    }

    // determines if this content cell object is the same color as a given trophy
    public boolean sameColorTrophy(Trophy other) {
        return false;
    }

    // determines if this content cell object is the same color as a given target
    public boolean sameColorTarget(Target other) {
        return false;
    }

}

// a blank cell object
class Blank implements ICellObject {

    /*
     * TEMPLATE: Methods: ... this.cellObjToImage() ... -- WorldImage
     */

    // stacks this blank object with a given cell object
    public ICellObject stackWith(ICellObject other) {
        return other;
    }

    // stacks this blank object with a given ground object
    public ICellObject stackWithGround(AGround other) {
        return other;
    }

    // stacks this blank object with a given content object
    public ICellObject stackWithContent(AContent other) {
        return other;
    }

    // converts this blank cell object to an image
    public WorldImage cellObjToImage() {
        return new RectangleImage(120, 120, OutlineMode.OUTLINE, Color.black);
    }

    // determines if this blank cell object is the same color as another given
    public boolean sameColor(ICellObject other) {
        return true;
    }

    // determines if this blank cell object is the same color as a given trophy
    public boolean sameColorTrophy(Trophy other) {
        return false;
    }

    // determines if this blank cell object is the same color as a given target
    public boolean sameColorTarget(Target other) {
        return false;
    }
}

// a cell that contains a player
class Player extends AContent {
    String facingDirection;

    /*
     * TEMPLATE: Fields: ... this.facingDirection ... -- String
     *
     * Methods: ... this.cellObjToImage() ... -- WorldImage
     *
     * Methods for Fields: ... this.facingDirection.equals(String) ... -- boolean
     */

    public Player(String facingDirection) {
        super();
        this.facingDirection = facingDirection;
    }

    // converts this player cell object to an image
    public WorldImage cellObjToImage() {
        return new FromFileImage("./src/assets/player.png");
    }

}

// a cell that contains a trophy
class Trophy extends AContent {
    String color;

    /*
     * TEMPLATE: Fields: ... this.color ... -- String
     *
     * Methods: ... this.cellObjToImage() ... -- WorldImage
     *
     * Methods for Fields: ... this.color.equals(String) ... -- boolean
     */

    public Trophy(String color) {
        super();
        this.color = color;
    }

    // converts this trophy cell object to an image
    public WorldImage cellObjToImage() {
        return new FromFileImage("./src/assets/trophy_" + this.color + ".png");
    }

    // determines if this trophy cell object is the same color as another given
    public boolean sameColor(ICellObject other) {
        return other.sameColorTrophy(this);
    }

    // determines if this trophy cell object is the same color as another given
    // target
    public boolean sameColorTarget(Target other) {
        return this.color.equals(other.color);
    }

}

// a cell that contains a wall
class Wall extends AContent {

    /*
     * TEMPLATE: Methods: ... this.cellObjToImage() ... -- WorldImage
     */

    public Wall() {
        super();
    }

    // converts this wall cell object to an image
    public WorldImage cellObjToImage() {
        return new FromFileImage("./src/assets/wall.png");
    }

}

// a cell that contains a box
class Box extends AContent {

    /*
     * TEMPLATE: Methods: ... this.cellObjToImage() ... -- WorldImage
     */

    public Box() {
        super();
    }

    // converts this box cell object to an image
    public WorldImage cellObjToImage() {
        return new FromFileImage("./src/assets/box.png");
    }

}

//a cell that contains a target on the ground
class Target extends AGround {
    String color;

    /*
     * TEMPLATE: Fields: ... this.color ... -- String
     *
     * Methods: ... this.cellObjToImage() ... -- WorldImage
     *
     * Methods for Fields: ... this.color.equals(String) ... -- boolean
     */

    public Target(String color) {
        super();
        this.color = color;
    }

    // converts this target cell object to an image
    public WorldImage cellObjToImage() {
        switch (this.color) {
            case "blue":
                return new CircleImage(60, OutlineMode.OUTLINE, Color.BLUE);
            case "green":
                return new CircleImage(60, OutlineMode.OUTLINE, Color.GREEN);
            case "red":
                return new CircleImage(60, OutlineMode.OUTLINE, Color.RED);
            case "yellow":
                return new CircleImage(60, OutlineMode.OUTLINE, Color.YELLOW);
            default:
                return new CircleImage(60, OutlineMode.OUTLINE, Color.BLACK);
        }
    }

    // determines if this target cell object is the same color as another given
    public boolean sameColor(ICellObject other) {
        return other.sameColorTarget(this);
    }

    // determines if this target cell object is the same color as another given
    // trophy
    public boolean sameColorTrophy(Trophy other) {
        return this.color.equals(other.color);
    }

}

// a sokoban level
class Level {
    IList<IList<Cell>> grid;

    /*
     * TEMPLATE: Fields: ... this.grid ... -- IList<IList<Cell>>
     *
     * Methods: ... this.getPlayerLocation() ... -- Position ...
     * this.isCellEmpty(Position) ... -- boolean ... this.isDirectionMovable(String)
     * ... -- boolean ... this.switchTwo(Position, Position) ... -- Level ...
     * this.changeFacing(String) ... -- void ... this.move(String) ... -- Level ...
     * this.draw() ... -- WorldImage
     *
     * Methods for Fields: ... this.grid.map(Function) ... -- IList<IList<Cell>> ...
     * this.grid.foldr(BiFunction, U) ... -- U ... this.grid.parallel(BiFunction,
     * IList<IList<Cell>>) ... -- IList<IList<Cell>> ... this.grid.getIndex(int) ...
     * -- IList<Cell> ... this.grid.indexOf(Function<T, Boolean>) ... -- int ...
     * this.grid.change(IList<Cell>, int) ... -- IList<IList<Cell>>
     */

    public Level(IList<IList<Cell>> grid) {
        this.grid = grid;
    }

    // configures this level given the ground and content level description strings
    public Level(String groundDescription, String contentsDescription) {
        IList<IList<Cell>> groundLevel = new GroundLevelDescription()
                .gridDescriptionToGrid(groundDescription);
        IList<IList<Cell>> contentsLevel = new ContentLevelDescription()
                .gridDescriptionToGrid(contentsDescription);

        this.grid = groundLevel.parallel(new StackCells(), contentsLevel);
    }

    // get the location of the player in the level
    Position getPlayerLocation() {
        IList<Integer> helper = this.grid.map(new ApplyIndexOf());
        int row = helper.indexOf(new NotNegOne(), 1);
        int col = helper.getIndex(row);
        return new Position(row, col);
    }

    // check whether the content of that cell is blank or not
    boolean isCellEmpty(Position pos) {
        return grid.getIndex(pos.row).getIndex(pos.col).content instanceof Blank;
    }

    // check whether the player can move along the direction
    boolean isDirectionMovable(String dir) {
        switch (dir) {
            case "up":
                return this.isCellEmpty(
                        new Position(this.getPlayerLocation().row - 1, this.getPlayerLocation().col));
            case "down":
                return this.isCellEmpty(
                        new Position(this.getPlayerLocation().row + 1, this.getPlayerLocation().col));
            case "left":
                return this.isCellEmpty(
                        new Position(this.getPlayerLocation().row, this.getPlayerLocation().col - 1));
            case "right":
                return this.isCellEmpty(
                        new Position(this.getPlayerLocation().row, this.getPlayerLocation().col + 1));
            default:
                throw new IllegalArgumentException("cannot move in that way");
        }
    }

    // switch the contents of the two positions
    Level switchTwo(Position p1, Position p2) {
        IList<IList<Cell>> newGrid = this.grid;
        Cell cell1 = new Cell(grid.getIndex(p1.row).getIndex(p1.col).ground,
                grid.getIndex(p2.row).getIndex(p2.col).content);
        Cell cell2 = new Cell(grid.getIndex(p2.row).getIndex(p2.col).ground,
                grid.getIndex(p1.row).getIndex(p1.col).content);
        newGrid = newGrid.change(newGrid.getIndex(p1.row).change(cell1, p1.col), p1.row);
        newGrid = newGrid.change(newGrid.getIndex(p2.row).change(cell2, p2.col), p2.row);
        return new Level(newGrid);
    }

    // change the facing of the player
    void changeFacing(String direction) {
        if (direction.equals("up") || direction.equals("down") || direction.equals("left")
                || direction.equals("right")) {
            this.grid.change(grid.getIndex(this.getPlayerLocation().row).change(
                    new Cell(new Player("up")), this.getPlayerLocation().col), this.getPlayerLocation().row);
        }
    }

    // make the player move along the direction if possible
    Level move(String direction) {
        this.changeFacing(direction);
        if (this.isDirectionMovable(direction)) {
            switch (direction) {
                case "up":
                    return this.switchTwo(this.getPlayerLocation(),
                            new Position(this.getPlayerLocation().row - 1, this.getPlayerLocation().col));
                case "down":
                    return this.switchTwo(this.getPlayerLocation(),
                            new Position(this.getPlayerLocation().row + 1, this.getPlayerLocation().col));
                case "left":
                    return this.switchTwo(this.getPlayerLocation(),
                            new Position(this.getPlayerLocation().row, this.getPlayerLocation().col - 1));
                case "right":
                    return this.switchTwo(this.getPlayerLocation(),
                            new Position(this.getPlayerLocation().row, this.getPlayerLocation().col + 1));
                default:
                    return this;
            }
        }
        return this;
    }

    // draws this level
    WorldImage draw() {
        return this.grid.map(new CellsToImages()).map(new ImagesToImage()).foldr(new AboveImages(),
                new EmptyImage());
    }

    // determines if the player has won the level
    boolean levelWon() {
        return this.grid.andMap(new CellsWon());
    }

}

// a level description
interface ILevelDescription {

    /*
     * TEMPLATE: Methods: ... this.cellDescriptionToCell(String) ... -- Cell ...
     * this.rowDescriptionToRow(String) ... -- IList<Cell> ...
     * this.gridDescriptionToGrid(String) ... -- IList<IList<Cell>>
     */

    // converts a given cell description to a cell
    Cell cellDescriptionToCell(String s);

    // converts a row description to a row of cells
    IList<Cell> rowDescriptionToRow(String row);

    // converts a given grid description to a grid of cells
    IList<IList<Cell>> gridDescriptionToGrid(String grid);

}

// a level description
abstract class ALevelDescription implements ILevelDescription {

    /*
     * TEMPLATE: Methods: ... this.rowDescriptionToRow(String) ... -- IList<Cell>
     * ... this.gridDescriptionToGrid(String) ... -- IList<IList<Cell>>
     */

    // converts a given row description to a row of cells
    public IList<Cell> rowDescriptionToRow(String row) {
        String firstRow = row.substring(0, 1);
        String restRow = row.substring(1);
        Cell cell = this.cellDescriptionToCell(firstRow);

        if (restRow.equals("") || restRow.equals("\n")) {
            return new ConsList<Cell>(cell, new MtList<Cell>());
        }

        return new ConsList<Cell>(cell, this.rowDescriptionToRow(restRow));
    }

    // converts a given grid description to a grid of cells
    public IList<IList<Cell>> gridDescriptionToGrid(String grid) {
        int i = grid.indexOf("\n");
        if (i < 0) {
            return new ConsList<IList<Cell>>(this.rowDescriptionToRow(grid), new MtList<IList<Cell>>());
        }

        String firstGrid = grid.substring(0, i + 1);
        String restRow = grid.substring(i + 1);

        return new ConsList<IList<Cell>>(this.rowDescriptionToRow(firstGrid),
                this.gridDescriptionToGrid(restRow));
    }
}

// a ground level description
class GroundLevelDescription extends ALevelDescription {

    /*
     * TEMPLATE: Methods: ... this.cellDescriptionToCell(String) ... -- Cell
     */

    // converts a given ground cell description to a cell
    public Cell cellDescriptionToCell(String s) {
        switch (s) {
            case "Y":
                return new Cell(new Target("yellow"));
            case "G":
                return new Cell(new Target("green"));
            case "B":
                return new Cell(new Target("blue"));
            case "R":
                return new Cell(new Target("red"));
            case "_":
                return new Cell();
            default:
                throw new IllegalArgumentException("invalid ground cell description");
        }
    }

}

// a content level description
class ContentLevelDescription extends ALevelDescription {

    /*
     * TEMPLATE: Methods: ... this.cellDescriptionToCell(String) ... -- Cell
     */

    // converts a given content cell description to a cell
    public Cell cellDescriptionToCell(String s) {
        switch (s) {
            case "y":
                return new Cell(new Trophy("yellow"));
            case "g":
                return new Cell(new Trophy("green"));
            case "b":
                return new Cell(new Trophy("blue"));
            case "r":
                return new Cell(new Trophy("red"));
            case "W":
                return new Cell(new Wall());
            case "B":
                return new Cell(new Box());
            case ">":
                return new Cell(new Player("right"));
            case "<":
                return new Cell(new Player("left"));
            case "^":
                return new Cell(new Player("up"));
            case "v":
                return new Cell(new Player("down"));
            case "_":
                return new Cell();
            default:
                throw new IllegalArgumentException("invalid content cell description");

        }
    }

}

// ---------------FUNCTION OBJECTS-------------------------

// a function object that stacks two given cells
class StackCell implements BiFunction<Cell, Cell, Cell> {

    /*
     * TEMPLATE: Methods: ... this.apply(Cell, Cell) ... -- Cell
     */
    // stacks two given cells
    public Cell apply(Cell ground, Cell content) {
        return ground.stackCell(content);
    }
}

// a function object that stacks two given list of cells
class StackCells implements BiFunction<IList<Cell>, IList<Cell>, IList<Cell>> {

    /*
     * TEMPLATE: Methods: ... this.apply(IList<Cell>, IList<Cell>) ... --
     * IList<Cell>
     */
    // stacks two given list of cells
    public IList<Cell> apply(IList<Cell> ground, IList<Cell> content) {
        return ground.parallel(new StackCell(), content);
    }
}

// a function object that converts a cell to a image
class CellToImage implements Function<Cell, WorldImage> {
    /*
     * TEMPLATE: Methods: ... this.apply(Cell) ... -- WorldImage
     */
    // converts a cell to a image
    public WorldImage apply(Cell cell) {
        return cell.cellToImage();
    }
}

//a function object that converts a list of cells to a list of images
class CellsToImages implements Function<IList<Cell>, IList<WorldImage>> {
    /*
     * TEMPLATE: Methods: ... this.apply(IList<Cell>) ... -- IList<WorldImage>
     */
    // converts a list of cells to a list of images
    public IList<WorldImage> apply(IList<Cell> cells) {
        return cells.map(new CellToImage());
    }
}

// a function object that converts a list of images to an image via beside images
class ImagesToImage implements Function<IList<WorldImage>, WorldImage> {
    /*
     * TEMPLATE: Methods: ... this.apply(IList<WorldImage>) ... -- WorldImage
     */
    // converts a list of images to an image via beside images
    public WorldImage apply(IList<WorldImage> images) {
        return images.foldr(new BesideImages(), new EmptyImage());
    }
}

// a function object that aligns a list of images horizontally
class BesideImages implements BiFunction<WorldImage, WorldImage, WorldImage> {
    /*
     * TEMPLATE: Methods: ... this.apply(WorldImage, WorldImage) ... -- WorldImage
     */
    // aligns a list of images horizontally
    public WorldImage apply(WorldImage current, WorldImage acc) {
        return new BesideImage(current, acc);
    }
}

//a function object that aligns a list of images vertically
class AboveImages implements BiFunction<WorldImage, WorldImage, WorldImage> {
    /*
     * TEMPLATE: Methods: ... this.apply(WorldImage, WorldImage) ... -- WorldImage
     */
    // aligns a list of images vertically
    public WorldImage apply(WorldImage current, WorldImage acc) {
        return new AboveImage(current, acc);
    }
}

// a predicate object that determines if this cell won
class CellWon implements Predicate<Cell> {
    /*
     * TEMPLATE: Methods: ... this.apply(Cell) ... -- boolean
     */
    // determines if this cell won
    public boolean test(Cell cell) {
        return cell.cellWon();
    }
}

//a predicate object that determines if this list of cells won
class CellsWon implements Predicate<IList<Cell>> {
    /*
     * TEMPLATE: Methods: ... this.apply(Cell) ... -- boolean
     */
    // determines if this list of cells won
    public boolean test(IList<Cell> cells) {
        return cells.andMap(new CellWon());
    }
}

//determine whether player is in the cell
class IsPlayer implements Function<Cell, Boolean> {
    /*
     * TEMPLATE: Methods: ... this.apply(Cell) ... -- Boolean
     */
    public Boolean apply(Cell target) {
        return target.content instanceof Player;
    }
}

//determine whether a number is negative one
class NotNegOne implements Function<Integer, Boolean> {
    /*
     * TEMPLATE: Methods: ... this.apply(Integer) ... -- Boolean
     */
    public Boolean apply(Integer target) {
        return target != -1;
    }
}

//apply indexOf method to a list of cell
class ApplyIndexOf implements Function<IList<Cell>, Integer> {

    /*
     * TEMPLATE: Methods: ... this.apply(IList<Cell>) ... -- Integer
     */
    public Integer apply(IList<Cell> target) {
        return target.indexOf(new IsPlayer(), 1);
    }
}

// ------------- HELPERS / UTLITY--------------

class Position {
    int row;
    int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /*
     * TEMPLATE: Fields: ... this.row ... -- int ... this.col ... -- int
     *
     * Methods for Fields: ... this.row == int ... -- boolean ... this.col == int
     * ... -- boolean
     */
}

//a list of type T
interface IList<T> {

    // maps through this list of T to produce a list of U
    <U> IList<U> map(Function<T, U> f);

    // applies foldr through this list of T to produce a U
    <U> U foldr(BiFunction<T, U, U> f, U base);

    // produces a new list of T by applying a binary function to corresponding
    // elements of this list of T and another list of T in parallel
    IList<T> parallel(BiFunction<T, T, T> f, IList<T> list);

    // produces a new list of T by applying a binary function to corresponding
    // elements of this list of T and another empty list of T in parallel
    IList<T> parallel(BiFunction<T, T, T> f, MtList<T> list);

    // produces a new list of T by applying a binary function to corresponding
    // elements of this list of T and another non empty list of T in parallel
    IList<T> parallel(BiFunction<T, T, T> f, ConsList<T> list);

    // determines if every element of this list of T satisfies a predicate of type T
    boolean andMap(Predicate<T> p);

    // locate the target pass the given test and return the index of that target. If
    // no target found, return -1
    int indexOf(Function<T, Boolean> f, int i);

    // get the item at the given index
    T getIndex(int i);

    // change the nth element to the given element
    IList<T> change(T newElement, int n);
}

//an empty list of type T
class MtList<T> implements IList<T> {

    /*
     * TEMPLATE: Methods: ... this.map(Function<T, U>) ... -- IList<U> ...
     * this.foldr(BiFunction<T, U, U>, U) ... -- U ... this.parallel(BiFunction<T,
     * T, T>, IList<T>) ... -- IList<T> ... this.indexOf(Function<T, Boolean>, int)
     * ... -- int ... this.getIndex(int) ... -- T ... this.change(T, int) ... --
     * IList<T>
     */

    // maps through this empty list of T to produce a list of U
    public <U> IList<U> map(Function<T, U> f) {
        return new MtList<U>();
    }

    // applies foldr through this empty list of T to produce a U
    public <U> U foldr(BiFunction<T, U, U> f, U base) {
        return base;
    }

    // produces a new list of T by applying a binary function to corresponding
    // elements of this empty list of T and another list of T in parallel
    public IList<T> parallel(BiFunction<T, T, T> f, IList<T> list) {
        return list.parallel(f, this);
    }

    // produces a new list of T by applying a binary function to corresponding
    // elements of this empty list of T and another empty list of T in parallel
    public IList<T> parallel(BiFunction<T, T, T> f, MtList<T> list) {
        return this;
    }

    // produces a new list of T by applying a binary function to corresponding
    // elements of this empty list of T and another non empty list of T in parallel
    public IList<T> parallel(BiFunction<T, T, T> f, ConsList<T> list) {
        throw new IllegalArgumentException("invalid lists: lists cannot be of different size");
    }

    // determines if every element of this empty list of T satisfies a predicate of
    // type T
    public boolean andMap(Predicate<T> p) {
        return true;
    }

    // locate the target and return the index of that target. If no target found,
    // return -1
    public int indexOf(Function<T, Boolean> f, int i) {
        return -1;
    }

    // get the item at the given index
    public T getIndex(int i) {
        throw new IllegalArgumentException("the list is not long enough to get the index");
    }

    // change the element to the given element
    public IList<T> change(T newElement, int n) {
        throw new IllegalArgumentException("cannot change the empty list");
    }
}

//a non empty list of T
class ConsList<T> implements IList<T> {
    T first;
    IList<T> rest;

    /*
     * TEMPLATE: Fields: ... this.first ... -- T ... this.rest ... -- IList<T>
     *
     * Methods: ... this.map(Function<T, U>) ... -- IList<U> ...
     * this.foldr(BiFunction<T, U, U>, U) ... -- U ... this.parallel(BiFunction<T,
     * T, T>, IList<T>) ... -- IList<T> ... this.indexOf(Function<T, Boolean>, int)
     * ... -- int ... this.getIndex(int) ... -- T ... this.change(T, int) ... --
     * IList<T>
     *
     * Methods for Fields: ... this.rest.map(Function<T, U>) ... -- IList<U> ...
     * this.rest.foldr(BiFunction<T, U, U>, U) ... -- U ...
     * this.rest.parallel(BiFunction<T, T, T>, IList<T>) ... -- IList<T> ...
     * this.rest.indexOf(Function<T, Boolean>, int) ... -- int ...
     * this.rest.getIndex(int) ... -- T ... this.rest.change(T, int) ... -- IList<T>
     */

    public ConsList(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    // maps through this non empty list of T to produce a list of U
    public <U> IList<U> map(Function<T, U> f) {
        return new ConsList<U>(f.apply(this.first), this.rest.map(f));
    }

    // applies foldr through this non empty list of T to produce a U
    public <U> U foldr(BiFunction<T, U, U> f, U base) {
        return f.apply(this.first, this.rest.foldr(f, base));
    }

    // produces a new list of T by applying a binary function to corresponding
    // elements of this non empty list of T and another list of T in parallel
    public IList<T> parallel(BiFunction<T, T, T> f, IList<T> list) {
        return list.parallel(f, this);
    }

    // produces a new list of T by applying a binary function to corresponding
    // elements of this non empty list of T and another empty list of T in parallel
    public IList<T> parallel(BiFunction<T, T, T> f, MtList<T> list) {
        throw new IllegalArgumentException("invalid lists: lists cannot be of different size");
    }

    // produces a new list of T by applying a binary function to corresponding
    // elements of this non empty list of T and another non empty list of T in
    // parallel
    public IList<T> parallel(BiFunction<T, T, T> f, ConsList<T> list) {
        return new ConsList<T>(f.apply(this.first, list.first), this.rest.parallel(f, list.rest));
    }

    // determines if every element of this non empty list of T satisfies a predicate
    // of type T
    public boolean andMap(Predicate<T> p) {
        return p.test(this.first) && this.rest.andMap(p);
    }

    // locate the target and return the index of that target. If no target found,
    // return -1
    public int indexOf(Function<T, Boolean> f, int i) {
        if (f.apply(this.first)) {
            return i;
        }
        else {
            return this.rest.indexOf(f, i + 1);
        }
    }

    // get the item at the given index
    public T getIndex(int i) {
        if (i == 1) {
            return this.first;
        }
        else {
            return this.rest.getIndex(i - 1);
        }
    }

    // change the element to the given element
    public IList<T> change(T newElement, int n) {
        if (n == 1) {
            return new ConsList<T>(newElement, this.rest);
        }
        else {
            return new ConsList<T>(this.first, this.rest.change(newElement, n - 1));
        }
    }
}

// represent a sokoban game
class Sokoban extends World {
    static final int WIDTH = 1000;
    static final int HEIGHT = 1000;

    Level level;

    /*
     * TEMPLATE: Fields: ... this.level ... -- Level
     *
     * Methods: ... this.makeScene() ... -- WorldScene ... this.onKeyEvent(String)
     * ... -- World
     *
     * Methods for Fields: ... this.level.move(String) ... -- Level ...
     * this.level.getPlayerLocation() ... -- Position ...
     * this.level.isCellEmpty(Position) ... -- boolean ...
     * this.level.isDirectionMovable(String) ... -- boolean ...
     * this.level.switchTwo(Position, Position) ... -- Level ...
     * this.level.changeFacing(String) ... -- void ... this.level.draw() ... --
     * WorldImage
     */
    public Sokoban(Level level) {
        this.level = level;
    }

    // draw the game
    public WorldScene makeScene() {
        WorldScene s = new WorldScene(WIDTH, HEIGHT);
        return s.placeImageXY(level.draw(), WIDTH / 2, HEIGHT / 2);
    }

    // the player move base on the key pressed
    public World onKeyEvent(String key) {
        if (key.equals("up") || key.equals("down") || key.equals("right") || key.equals("left")) {
            return new Sokoban(level.move(key));
        }
        else {
            return this;
        }
    }
}

// ------------------------------------------

class SokobanExamples {
    static final int WIDTH = 1000;
    static final int HEIGHT = 1000;

    WorldCanvas c = new WorldCanvas(WIDTH, HEIGHT);
    WorldScene s = new WorldScene(WIDTH, HEIGHT);
    String exampleLevelGround = "________\n" + "___R____\n" + "________\n" + "_B____Y_\n"
            + "________\n" + "___G____\n" + "________";
    String exampleLevelContents = "__WWW___\n" + "__W_WW__\n" + "WWWrgWWW\n" + "W_b>yB_W\n"
            + "WW__WWWW\n" + "_WW_W___\n" + "__WWW___";

    Level level = new Level(exampleLevelGround, exampleLevelContents);
    WorldImage levelImg = level.draw();

    World game = new Sokoban(level);

    // ---------------- test level descriptions -----------------------------------
    GroundLevelDescription groundLevelDescription = new GroundLevelDescription();
    ContentLevelDescription contentLevelDescription = new ContentLevelDescription();

    boolean testBigBang(Tester t) {
        return game.bigBang(WIDTH, HEIGHT, 1);
    }

    void testCellDescriptionToCell(Tester t) {
        // ground
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("Y"),
                new Cell(new Target("yellow")));
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("_"), new Cell());
        // content
        t.checkExpect(contentLevelDescription.cellDescriptionToCell("g"),
                new Cell(new Trophy("green")));
        t.checkExpect(contentLevelDescription.cellDescriptionToCell("W"), new Cell(new Wall()));
        t.checkExpect(contentLevelDescription.cellDescriptionToCell("B"), new Cell(new Box()));
        t.checkExpect(contentLevelDescription.cellDescriptionToCell(">"),
                new Cell(new Player("right")));
    }

    void testRowDescriptionToRow(Tester t) {
        // ground
        t.checkExpect(groundLevelDescription.rowDescriptionToRow("_B____Y_\n"),
                new ConsList<Cell>(new Cell(),
                        new ConsList<Cell>(new Cell(new Target("blue")),
                                new ConsList<Cell>(new Cell(),
                                        new ConsList<Cell>(new Cell(),
                                                new ConsList<Cell>(new Cell(),
                                                        new ConsList<Cell>(new Cell(),
                                                                new ConsList<Cell>(new Cell(new Target("yellow")),
                                                                        new ConsList<Cell>(new Cell(), new MtList<Cell>())))))))));
        // content
        t.checkExpect(contentLevelDescription.rowDescriptionToRow("W_b>yB_W\n"),
                new ConsList<Cell>(new Cell(new Wall()),
                        new ConsList<Cell>(new Cell(), new ConsList<Cell>(new Cell(new Trophy("blue")),
                                new ConsList<Cell>(new Cell(new Player("right")),
                                        new ConsList<Cell>(new Cell(new Trophy("yellow")),
                                                new ConsList<Cell>(new Cell(new Box()), new ConsList<Cell>(new Cell(),
                                                        new ConsList<Cell>(new Cell(new Wall()), new MtList<Cell>())))))))));
    }

    void testGridDescriptionToGrid(Tester t) {
        // ground
        t.checkExpect(groundLevelDescription.gridDescriptionToGrid("_R\n" + "G_"),
                new ConsList<>(
                        new ConsList<>(new Cell(), new ConsList<>(new Cell(new Target("red")), new MtList<>())),
                        new ConsList<>(new ConsList<>(new Cell(new Target("green")),
                                new ConsList<>(new Cell(), new MtList<>())), new MtList<>())));

        // content
        t.checkExpect(
                contentLevelDescription.gridDescriptionToGrid("Wr_\n"
                        + ">yB"),
                new ConsList<>(new ConsList<>(new Cell(new Wall()),
                        new ConsList<>(new Cell(new Trophy("red")),
                                new ConsList<>(new Cell(), new MtList<>()))),
                        new ConsList<>(new ConsList<>(new Cell(new Player("right")),
                                new ConsList<>(new Cell(new Trophy("yellow")),
                                        new ConsList<>(new Cell(new Box()), new MtList<>()))),
                                new MtList<>())));
    }

    // --------------- test stack cell ----------------------------------
    void testStackCell(Tester t) {
        ICellObject blank = new Blank();
        ICellObject target = new Target("blue");
        ICellObject trophy = new Trophy("blue");
        // blank + blank
        t.checkExpect(new Cell(blank, blank).stackCell(new Cell(blank, blank)), new Cell(blank, blank));
        // ground + blank
        t.checkExpect(new Cell(target, blank).stackCell(new Cell(blank, blank)),
                new Cell(target, blank));
        // content + blank
        t.checkExpect(new Cell(blank, trophy).stackCell(new Cell(blank, blank)),
                new Cell(blank, trophy));
        // content + ground
        t.checkExpect(new Cell(target, trophy).stackCell(new Cell(blank, blank)),
                new Cell(target, trophy));
        t.checkExpect(new Cell(target, blank).stackCell(new Cell(blank, trophy)),
                new Cell(target, trophy));
        // invalid
        t.checkException(
                new IllegalArgumentException(
                        "invalid stack: cannot stack this gound object with an existing ground object"),
                new Cell(target, blank), "stackCell", new Cell(target, trophy));

        // stack rows
        t.checkExpect(
                groundLevelDescription.rowDescriptionToRow("B_").parallel(new StackCell(),
                        contentLevelDescription.rowDescriptionToRow("bW")),
                new ConsList<Cell>(new Cell(new Target("blue"), new Trophy("blue")),
                        new ConsList<Cell>(new Cell(new Wall()), new MtList<Cell>())));
        // stack grid
        t.checkExpect(
                groundLevelDescription.gridDescriptionToGrid("B_\n" + "_R").parallel(new StackCells(),
                        contentLevelDescription.gridDescriptionToGrid("bW\n" + ">B")),
                new ConsList<>(
                        new ConsList<>(new Cell(new Target("blue"), new Trophy("blue")),
                                new ConsList<>(new Cell(new Blank(), new Wall()), new MtList<>())),
                        new ConsList<>(
                                new ConsList<>(new Cell(new Blank(), new Player("right")),
                                        new ConsList<>(new Cell(new Target("red"), new Box()), new MtList<>())),
                                new MtList<>())));
    }

    // ------------- test level won ------------------------
    void testLevelWon(Tester t) {
        // cell won
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("R")
                .stackCell(contentLevelDescription.cellDescriptionToCell("r")).cellWon(), true);
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("R")
                .stackCell(contentLevelDescription.cellDescriptionToCell("g")).cellWon(), false);
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("R")
                .stackCell(contentLevelDescription.cellDescriptionToCell("_")).cellWon(), false);
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("R")
                .stackCell(contentLevelDescription.cellDescriptionToCell(">")).cellWon(), false);
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("_")
                .stackCell(contentLevelDescription.cellDescriptionToCell(">")).cellWon(), true);
        t.checkExpect(groundLevelDescription.cellDescriptionToCell("_")
                .stackCell(contentLevelDescription.cellDescriptionToCell("_")).cellWon(), true);

        // cells won
        t.checkExpect(new Level("_R\n__", "_r\n__").levelWon(), true);
        t.checkExpect(new Level("_R\n__", "r_\n__").levelWon(), false);
        t.checkExpect(new Level("__\n__", "__\n__").levelWon(), true);
        t.checkExpect(new Level("RG\n__", "r_\ng_").levelWon(), false);

    }

    // --------------- test parallel

    // a function object that computes the sum of two integers
    class Addition implements BiFunction<Integer, Integer, Integer> {
        // computes the sum of two given integers
        public Integer apply(Integer t, Integer u) {
            return t + u;
        }
    }

    void testParallel(Tester t) {
        IList<Integer> mt = new MtList<Integer>();
        IList<Integer> list0 = new ConsList<Integer>(1,
                new ConsList<Integer>(2, new ConsList<Integer>(3, mt)));
        IList<Integer> list1 = new ConsList<Integer>(4,
                new ConsList<Integer>(5, new ConsList<Integer>(6, mt)));
        IList<Integer> list2 = new ConsList<Integer>(4, new ConsList<Integer>(5, mt));
        Addition add = new Addition();

        // empty + empty
        t.checkExpect(mt.parallel(add, mt), mt);
        // empty + non empty
        t.checkException(
                new IllegalArgumentException("invalid lists: lists cannot be of different size"), mt,
                "parallel", add, list0);
        // same size
        t.checkExpect(list0.parallel(add, list1),
                new ConsList<Integer>(5, new ConsList<Integer>(7, new ConsList<Integer>(9, mt))));
        // different size
        t.checkException(
                new IllegalArgumentException("invalid lists: lists cannot be of different size"), list1,
                "parallel", add, list2);

    }
}