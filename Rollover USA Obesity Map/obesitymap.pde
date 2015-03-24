PImage mapImage;
PFont font;
PFont source;
Table locationTable;
Table nameTable;
int rowCount;
Table dataTable;
float dataMin = 2;
float dataMax = 11;

int offset = 80;
boolean toggle;
Integrator[] column1;
Integrator[] column2;
Integrator[] current;

String textA = "Number of Fast Food Restaurants(per 100,000)";
String textB = "Obesity Percentage";
void setup( ) {
 toggle = false;
 size(800, 560);
 mapImage = loadImage("mapdark.png");
 locationTable = new Table("locations.tsv");
 nameTable = new Table("names.tsv");
 rowCount = locationTable.getRowCount( );
 dataTable = new Table("random.tsv");
 // Setup: load initial values into the Integrator.
 column1 = new Integrator[rowCount];
 column2 = new Integrator[rowCount]; 
 current = new Integrator[rowCount];
 for (int row = 0; row < rowCount; row++) {
   float colA = dataTable.getFloat(row, 1);
   float colB = dataTable.getFloat(row, 2);

   column1[row] = new Integrator(colA);
   column2[row] = new Integrator(colB);
   current[row] = new Integrator(colA);
 }
 
 font = loadFont("rollover.vlw");
 source = loadFont("source.vlw");
 textFont(font);
 smooth( );
 noStroke( );
 frameRate(30);
 updateTable();
}
float closestDist;
String closestText;
float closestTextX;
float closestTextY;

void draw( ) {
 background(0);
 image(mapImage, offset, offset);
 fill(255);
 textFont(font);
 textAlign(CENTER);
 if (toggle) {
   text(textB, width/2, height-48);
 }
 else {
   text(textA, width/2, height-48);
 }
 textFont(source);
 textAlign(LEFT);
 text("Data: U.S. Department of Agriculture", 10, height-25);
 text("http://www.ers.usda.gov/data-products/food-access-research-atlas/documentation.aspx",
   10, height-10);
 // Draw: update the Integrator with the current values,
 // which are either those from the setup( ) function
 // or those loaded by the target( ) function issued in
 // updateTable( ).
 for (int row = 0; row < rowCount; row++) {
   current[row].update( );
 }
 closestDist = MAX_FLOAT;
 for (int row = 0; row < rowCount; row++) {
   String abbrev = dataTable.getRowName(row);
   float x = locationTable.getFloat(abbrev, 1) + offset;
   float y = locationTable.getFloat(abbrev, 2) + offset;
   drawData(x, y, abbrev);
 }

 if (closestDist != MAX_FLOAT) {
   fill(255);
   textFont(font);
   textAlign(CENTER);
   stroke(0);
   text(closestText, closestTextX, closestTextY);
 }
}

void drawData(float x, float y, String abbrev) {
 // Figure out what row this is.
 int row = dataTable.getRowIndex(abbrev);
 // Get the current value.
 float value = current[row].value;
 //System.out.println(value);
 float radius = 0;
 float percent = norm(value, dataMin, dataMax);
 if (toggle) {
    radius = map(value, dataMin, dataMax, 8, 8.3);
 }
 else {
    radius = map(value, dataMin, dataMax, 5, 20);
 }
 color between;
 if (toggle) {
    between = lerpColor(#ffffff, #db0024, percent);
 }
 else {
    between = lerpColor(#ffffff, #00c8c1, percent);
 }
 fill(between); // red

 ellipseMode(RADIUS);
 if (toggle) {
   stroke(#8f0018);
 } else {
   stroke(#007e7a);
 }
 smooth();
 ellipse(x, y, radius, radius);
 float d = dist(x, y, mouseX, mouseY);
 if ((d < radius + 2) && (d < closestDist)) {
   closestDist = d;
   String name = nameTable.getString(abbrev, 1);
   // Use target (not current) value for showing the data point.
   String val;
   if (toggle) {
     float format = current[row].target * 100;
     val = nf(format, 0, 1) + "%";
   }
   else {
     val = nf(current[row].target, 0, 1);
   }
   closestText = name + " " + val;
   closestTextX = x;
   closestTextY = y-radius-4;
 }
}

void keyPressed( ) {
 if (key == ' ') {
   updateTable( );
 }
}
void updateTable( ) {
  if (toggle) {
    //Set min & max to 0 and 11
    dataMin = 0;
    dataMax = 12.9;
    for (int row = 0; row < rowCount; row++) {
      float newFloat = (column1[row].value) + 0.0;
      current[row].target(newFloat);
      //System.out.println(newFloat);
    }
    toggle = false;
  }
  else {
    dataMin = 0;
    dataMax = 0.30;
    for (int row = 0; row < rowCount; row++) {
      float newFloat = (column2[row].value) + 0.0;
      current[row].target(newFloat);
      //System.out.println(newFloat);
    }
    toggle = true;
    //Set min and max to 0 and 0.5
  }
// for (int row = 0; row < rowCount; row++) {
//   float newValue = random(0, 10);
//   interpolators[row].target(newValue);
// }
}
