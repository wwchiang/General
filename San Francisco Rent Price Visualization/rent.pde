//Integrator[] rentData;

float dataMin = 0;
float dataMax = 5000;
Table dataTable;
int rowCount;
int radius = 6;
PFont font;
PFont source;

int numPartitions = 8;
void setup() {
  size(950,520);
  
  dataTable = new Table("rent.txt");
  rowCount = dataTable.getRowCount();
  //rentData = new Integrator[rowCount];
  //for (int row = 0
  
  font = loadFont("rollover.vlw");
  textFont(font);
  smooth();
  noStroke();
  
}
float closestDist;
String closestText;
float closestTextX;
float closestTextY;

int xRangeMin = 125;
int xRangeMax = 900;
int yRangeMin = 50;
int yRangeMax = 400;
color oneBrColor = #0326d7;
color twoBrColor = #d71203;
void draw() {
  background(240);
  textAlign(CENTER);
  fill(0);
  text("Average Rent in San Francisco (July 2009 - December 2014)", xRangeMax/2, yRangeMin);
  textAlign(LEFT);
  text("Source: RentJungle.com, http://www.rentjungle.com/average-rent-in-san-francisco-rent-trends", 10, height-30);
  text("Source 2: SF Rent Board, http://www.sfrb.org/index.aspx?page=47", 10, height-12);
  //Draw underlying bars
  int labelX = xRangeMin + 250;
  int labelY1 = yRangeMax + 20;
  int labelY2 = yRangeMax + 45;
  textAlign(LEFT);
  fill(oneBrColor);
  rect(labelX, labelY1, 20, 20);
  fill(twoBrColor);
  rect(labelX, labelY2, 20, 20);

  fill(0);
  text("1 Bedroom Apartment", labelX + 25, labelY1 + 15);
  text("2 Bedroom Apartment", labelX + 25, labelY2 + 15);
  int yRange = yRangeMax - yRangeMin;
  int yRangeStep = yRange/numPartitions;
  for (int i = 0; i < numPartitions; i++) {
    int x1 = xRangeMin - 30;
    int x2 = xRangeMax;
    int y = yRangeMax - i*yRangeStep;
    strokeWeight(1);
    if (i == 0) stroke(70);
    else stroke(150);
    line(x1, y, x2, y);
    
    textAlign(RIGHT);
    float yDataStep = (dataMax - dataMin) / numPartitions;
    text("$" + nf(yDataStep*i, 0, 2), x1, y);
  }
  int xStep = (xRangeMax - xRangeMin)/rowCount;
  smooth();
  //formula for x - if 10 values, and range is 100, then do range/numValues
  closestDist = MAX_FLOAT;
  noStroke();
  for (int row = 0; row < rowCount; row++) {
    //System.out.println("SWAGGER");
    String date = dataTable.getString(row, 0);
    int oneBedroom = dataTable.getInt(row, 1);
    int twoBedroom = dataTable.getInt(row, 2);
    
    int xVal = xRangeMin + (xStep*row);
    int dateY = yRangeMin;
    float oneBedY = map(oneBedroom, dataMin, dataMax, yRangeMax, yRangeMin);
    float twoBedY = map(twoBedroom, dataMin, dataMax, yRangeMax, yRangeMin);
    //System.out.println(xVal);
    //Functionality -- click one point, save value. Click another point, save that, 
    //and compute it as the difference. 
    fill(oneBrColor);
    ellipse(xVal, oneBedY, radius, radius);
    checkDistance(xVal, oneBedY, date, oneBedroom);
    
    fill(twoBrColor);
    ellipse(xVal, twoBedY, radius, radius);
    checkDistance(xVal, twoBedY, date, twoBedroom);
    
    if(row != rowCount - 1) {
      int oneBedNext = dataTable.getInt(row + 1, 1);
      int twoBedNext = dataTable.getInt(row + 1, 2);
      float oneNextY = map(oneBedNext, dataMin, dataMax, yRangeMax, yRangeMin);
      float twoNextY = map(twoBedNext, dataMin, dataMax, yRangeMax, yRangeMin);
      int xNext = xRangeMin + (xStep*(row+1));
      strokeWeight(2);
      stroke(oneBrColor);
      line(xVal, oneBedY, xNext, oneNextY);
      stroke(twoBrColor);
      line(xVal, twoBedY, xNext, twoNextY);
      stroke(0);
      strokeWeight(0);
    }
    
  }
  
  if (closestDist != MAX_FLOAT) {
   fill(210);
   rect(closestTextX-50,closestTextY-15, 100, 20);
   fill(0);
   textFont(font);
   textAlign(CENTER);
   //stroke(0);
   text(closestText, closestTextX, closestTextY);
  }
  
    
  //System.out.println("---------------");
}

void checkDistance(float x, float y, String date, int price) {

  float d = dist(x, y, mouseX, mouseY);
  if ((d < radius + 2) && (d < closestDist)) {
    
    //rect(x, y-25, 100, 20);
    closestDist = d;
    closestText = date + " : $" + price;
    closestTextX = x;
    closestTextY = y - radius - 4;
  }
}
