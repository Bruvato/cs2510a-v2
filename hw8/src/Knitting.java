import tester.*;

/*
one to decrbe knit fabric
instructions

knit and purls

knit
front V
back -

purl
front -
back V


knit fabric - row of stitches

right handed kntters
right to left
bottom up
KnittedFabric is flipped every row
 */

import java.util.ArrayList;
import java.util.Iterator;

// Represents a stitch
interface IStitch {

  // Renders this stitch as a String
  String render();

  // Flips this IStitch into its front/back representation
  IStitch flip();
}

// Represents a stitch that looks like "V" on the front and a "-" on the back in our knitted
class Knit implements IStitch {

  // Renders this Knit stitch as a string
  public String render() {
    return "V";
  }

  // Flips this Knit stitch into its front/back representation
  public IStitch flip() {
    return new Purl();
  }
}

// Represents a stitch that looks like "-" on the front and a "V" on the back in our knitted
class Purl implements IStitch {

  // Renders this purl stitch as a string
  public String render() {
    return "-";
  }

  // Flips this Purl stitch into its front/back representation
  public IStitch flip() {
    return new Knit();
  }
}

// Represents a knitted fabric
class KnittedFabric {

  ArrayList<ArrayList<IStitch>> knittedFabric;

  // constructor for knitted fabric
  KnittedFabric(ArrayList<ArrayList<IStitch>> knittedFabric) {
    this.knittedFabric = knittedFabric;
  }

  // convenience constructor to create an empty knitted Fabric
  KnittedFabric() {
    this(new ArrayList<ArrayList<IStitch>>());
  }

  // takes in an iterator of stitches, from left to right on the front side of the fabric,
  // and adds that row of stitches to the current fabric and then returns the current fabric
  KnittedFabric addRow(Iterator<IStitch> row) {
    ArrayList<IStitch> newRow = new ArrayList<>();

    // EFFECT: Adds every element in the given row to a new row of stitches
    while (row.hasNext()) {
      newRow.add(row.next());
    }

    this.knittedFabric.add(newRow);
    return this;
  }

  // draws this fabric as a string of text
  String renderFabric() {
    String result = "";

    // EFFECT: Renders every element in this knitted fabric and adds it to the result string
    for (ArrayList<IStitch> row : this.knittedFabric) {
      // EFFECT: Renders every stitch in the given row and adds it to the result string
      for (IStitch stitch : row) {
        result += stitch.render();
      }
      result += "\n";
    }
    return result;
  }

  // determines if two knitted fabrics look the same
  boolean sameFabric(KnittedFabric other) {
    return this.renderFabric().equals(other.renderFabric())
            || this.flip().renderFabric().equals(other.renderFabric());
  }

  // Flips this KnittedFabric to the other side; The front to the back and vice versa
  KnittedFabric flip() {
    ArrayList<ArrayList<IStitch>> flippedKF = new ArrayList<>();

    // EFFECT: reverses the order of every row in this knittedFabric and flips every stitch
    for (ArrayList<IStitch> row : this.knittedFabric) {
      ArrayList<IStitch> flippedRow = new ArrayList<>();
      // EFFECT: flips every stitch in this row of stitches
      for (IStitch stitch : row) {
        flippedRow.add(0, stitch.flip());
      }
      flippedKF.add(flippedRow);
    }

    return new KnittedFabric(flippedKF);
  }
}

// Represents a single type of instruction to create a part of a KnittedFabric
interface IInstruction{


}

// Represents common functionality for any type of instruction used to
// create a part of a KnittedFabric
abstract class AInstruction implements IInstruction {
  int count;

  // abstracted constructor for all instructions
  AInstruction(int count) {
    this.count = count;
  }
}

// Represents a knit instruction
class KnitInstruction extends AInstruction {

  // constructor
  KnitInstruction(int count) {
    super(count);
  }
}

// Represents a purl instruction
class PurlInstruction extends AInstruction {

  // constructor
  PurlInstruction(int count) {
    super(count);
  }
}

// Represents a repeat instruction
class RepeatInstruction extends AInstruction {
  KnitFabricInstructions instructions;

  // constructor
  RepeatInstruction(int count, KnitFabricInstructions instructions) {
    super(count);
    this.instructions = instructions;
  }

}

// Represents knitting instructions for a full fabric
class KnitFabricInstructions {

  ArrayList<ArrayList<IInstruction>> knitFabricInstructions;

  public KnitFabricInstructions() {
    this.knitFabricInstructions = new ArrayList<ArrayList<IInstruction>>();
  }


  // adds a row of instructions to the growing fabric
  KnitFabricInstructions addRow(Iterator<IInstruction> instructions){
    ArrayList<IInstruction> newRow = new ArrayList<>();

    // EFFECT: Adds every element in the given row of instructions to a new row
    while (instructions.hasNext()) {
      newRow.add(instructions.next());
    }

    this.knitFabricInstructions.add(newRow);

    return this;
  }

  // follows the instructions and generates the knitted fabric result
  KnittedFabric makeFabric() {
    KnittedFabric kf = new KnittedFabric();

    // EFFECT:
    for (ArrayList<IInstruction> row : this.knitFabricInstructions) {

    }
//    kf.addRow();

    // TODO
    //  Design a method KnittedFabric addRow(Iterator<IStitch> rowIter) that takes in an
    // iterator of stitches, from left to right on the front side of the fabric,
    // and adds that row of stitches to the current fabric
    return kf;
  }



}

// TODO
//   You should at minimum have classes that represent the three kinds of instructions,
//   single rows of instructions, and an entire fabric’s worth of instructions


class KnittedFabricExamples {

  KnittedFabric kfGivenTwoRows;
  ArrayList<IStitch> givenRow1;
  ArrayList<IStitch> givenRow2;

  IStitch knit;
  IStitch purl;


  void init() {
    this.kfGivenTwoRows = new KnittedFabric();

    this.knit = new Knit();
    this.purl = new Purl();

    this.givenRow1 = new ArrayList<>();
    this.givenRow1.add(this.knit);
    this.givenRow1.add(this.knit);
    this.givenRow1.add(this.knit);
    this.givenRow1.add(this.purl);
    this.givenRow1.add(this.purl);
    this.givenRow1.add(this.purl);
    this.givenRow1.add(this.purl);
    this.givenRow1.add(this.knit);

    this.givenRow2 = new ArrayList<>();
    this.givenRow2.add(this.purl);
    this.givenRow2.add(this.purl);
    this.givenRow2.add(this.purl);
    this.givenRow2.add(this.knit);
    this.givenRow2.add(this.knit);
    this.givenRow2.add(this.knit);
    this.givenRow2.add(this.knit);
    this.givenRow2.add(this.purl);

    this.kfGivenTwoRows.knittedFabric.add(this.givenRow1);
    this.kfGivenTwoRows.knittedFabric.add(this.givenRow2);
  }

  void testAddRow(Tester t){
    this.init();

    KnittedFabric kf = new KnittedFabric();
    kf.addRow(givenRow1.iterator());
    kf.addRow(givenRow2.iterator());

    t.checkExpect(kf, this.kfGivenTwoRows);
  }

  void testRenderFabric(Tester t) {
    this.init();
    KnittedFabric kf = new KnittedFabric();
    t.checkExpect(kf.renderFabric(), "");

    kf.addRow(givenRow1.iterator());

    t.checkExpect(kf.renderFabric(), "VVV----V\n");

    kf.addRow(givenRow2.iterator());
    t.checkExpect(kf.renderFabric(), "VVV----V\n---VVVV-\n");
  }

  void testFlipStitch(Tester t) {
    this.init();

    t.checkExpect(this.knit.flip(), new Purl());
    t.checkExpect(this.purl.flip(), new Knit());
  }

  void testFlipFabric(Tester t) {
    this.init();

    KnittedFabric flipped = new KnittedFabric();
    KnittedFabric oneRow = new KnittedFabric();
    oneRow.addRow(givenRow1.iterator());
    ArrayList<IStitch> flippedRow1 = new ArrayList<>();
    ArrayList<IStitch> flippedRow2 = new ArrayList<>();

    flippedRow1.add(this.purl);
    flippedRow1.add(this.knit);
    flippedRow1.add(this.knit);
    flippedRow1.add(this.knit);
    flippedRow1.add(this.knit);
    flippedRow1.add(this.purl);
    flippedRow1.add(this.purl);
    flippedRow1.add(this.purl);

    flippedRow2.add(this.knit);
    flippedRow2.add(this.purl);
    flippedRow2.add(this.purl);
    flippedRow2.add(this.purl);
    flippedRow2.add(this.purl);
    flippedRow2.add(this.knit);
    flippedRow2.add(this.knit);
    flippedRow2.add(this.knit);

    t.checkExpect(flipped.flip(), new KnittedFabric());

    flipped.addRow(flippedRow1.iterator());
    t.checkExpect(oneRow.flip(), flipped);

    flipped.addRow(flippedRow2.iterator());
    t.checkExpect(this.kfGivenTwoRows.flip(), flipped);
  }

}