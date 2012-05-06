
int oSize = 25;
float maxspeed = 5;
int width = 800;
int height = 600;
int plantPop = 40;
int preyPop = 10;
int predPop = 2;
float vision = 10*oSize;
float calPerSec = .5;
PVector origin = new PVector(0,0);
int fPS = 30;


// Rank in the ecosystem, diameter, vision radius, maxspeed, starting diet
float[] plantStats = {0,3,3,maxspeed*.1,1,calPerSec};
float[] preyStats = {1,oSize/2,vision*.6,maxspeed*.6,5,calPerSec};
float[] predStats = {2,oSize,vision,maxspeed,25, calPerSec};
float[][] ecoStats = {plantStats, preyStats, predStats};

Ecosystem ecosystem;


void setup(){
	
	size(800, 600);
	ecosystem = new Ecosystem();
	for (int i = 0; i < plantPop; i++){
		ecosystem.addOrganism(new Organism(ecoStats[0], origin));
	}
	for (int i = plantPop; i < preyPop + plantPop; i++){
		ecosystem.addOrganism(new Organism(ecoStats[1], origin));
	}
	for (int i = plantPop + preyPop; i < predPop + plantPop + preyPop; i++){
		ecosystem.addOrganism(new Organism(ecoStats[2], origin));
	}
	noStroke();
	smooth();
	frameRate(fPS);
}

void draw(){
	background(52);
	ecosystem.run();
}

void mousePressed(){
	ecosystem.spawn(ecoStats[1], new PVector(mouseX,mouseY));
}
