import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class terrarium extends PApplet {


int oSize = 25;
float maxspeed = 5;
int width = 800;
int height = 600;
int plantPop = 40;
int preyPop = 10;
int predPop = 2;
float vision = 10*oSize;
float calPerSec = .5f;
PVector origin = new PVector(0,0);
int fPS = 30;


// Rank in the ecosystem, diameter, vision radius, maxspeed, starting diet
float[] plantStats = {0,3,3,maxspeed*.1f,1,calPerSec};
float[] preyStats = {1,oSize/2,vision*.6f,maxspeed*.6f,5,calPerSec};
float[] predStats = {2,oSize,vision,maxspeed,25, calPerSec};
float[][] ecoStats = {plantStats, preyStats, predStats};

Ecosystem ecosystem;


public void setup(){
	
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

public void draw(){
	background(52);
	ecosystem.run();
}

public void mousePressed(){
	ecosystem.spawn(ecoStats[1], new PVector(mouseX,mouseY));
}
class Ecosystem{
	ArrayList organisms;

	Ecosystem(){
		organisms = new ArrayList();
	}

	public void run() {
		for (int i = 0; i < organisms.size(); i++){
			Organism o = (Organism) organisms.get(i);
			o.run(organisms);
			if (o.calories < o.startDiet/2) this.killOrganism(o.foodRank, i, o.calories, o.loc);

		}
	}

	public void addOrganism(Organism o){
		organisms.add(o);
	}

	public void spawn(float[] oS, PVector tempL){	
		PVector l = tempL;
		Organism o;
		o = new Organism(oS, l);
		l.add(new PVector(random(-3,3),random(-3,3)));
		o.move(l);
		ecosystem.addOrganism(o);	
	}

	public void decompose(PVector l, int pop){

		for (int i = 0; i < pop; i++){
			Organism o;
	
			l.add(new PVector(random(-3,3),random(-3,3)));
			o = new Organism(ecoStats[0], l);
			addOrganism(o);	
		}
	}

	public void killOrganism(float fr, int i, float cal, PVector l){
		organisms.remove(i);
		if (fr > 0){
			decompose(l, PApplet.parseInt(cal));
		}
	}
}
class Organism {
	PVector loc;
	PVector vel;
	PVector acc;
	float r;
	float visionR;
	float maxVel;
	float foodRank;
	float startDiet;
	float calories;
	boolean hunting;
	boolean hunted;
	float burnRatePerUpdate;
	int ecoI;
	float nearest;
	PVector nearestPV;

	int c1;
	int c2;

	Organism(float[] orgStats, PVector startLoc){
		this.foodRank = orgStats[0];
		this.r = orgStats[1]/2;
		this.visionR = orgStats[2]/2;
		this.maxVel = orgStats[3];
		this.startDiet = orgStats[4];
		this.calories = orgStats[4];
		this.burnRatePerUpdate = orgStats[5]*(1.0f/fPS);
		this.hunting = false;
		this.nearest = 1000;
		this.nearestPV = new PVector(0,0);
		colorize(PApplet.parseInt(foodRank));

		loc = new PVector(random(r, width - r), random(r, height - r));

		vel = new PVector(random(-maxVel, maxVel),random(-maxVel, maxVel));
		
		acc = new PVector(0,0);
		//render();
	}

	public void colorize(int foodRank){
		switch(foodRank){
			case 0:
				c1 = color(random(0,50),random(150,255),random(0,50));
				break;
			case 1:
				c1 = color(random(0,50),random(0,50),random(150,255));
				break;
			case 2:
				c1 = color(random(150,255),random(0,50),random(0,50));
				break;
		}
		c2 = color(c1, 10);
	}

	public void run(ArrayList organisms){
		if (foodRank != 0){
			interact(organisms);
			if (calories >= 2*startDiet) reproduce(organisms);
			burnCal();
		}

		//plant reproduction
		if ((foodRank == 0) && (organisms.size() < 100)){
			if (get(PApplet.parseInt(loc.x),PApplet.parseInt(loc.y)) == color(52,52,52)){
				float f = random(0,1);
				if (f < 1.0f/120.0f) reproduce(organisms);
			}
		}
		update();
		render();
	}

	public void interact(ArrayList organisms){
		if (foodRank < 2) findPred(organisms);
		if (!hunted) findPrey(organisms);
	}

	public void findPred(ArrayList organisms){
		hunted = false;
		for (int i = 0; i < organisms.size(); i++){
			Organism other = (Organism) organisms.get(i);
			if ((this.foodRank == other.foodRank - 1) && (loc.dist(other.loc) < visionR)){
				acc.add(steer(other.loc));
				acc.mult(-1);
				hunted = true;
				break;
			}
		}

	}

	public void findPrey(ArrayList organisms){
		hunting = false;
		nearest = 1000;
		for (int i = 0; i < organisms.size(); i++){
			Organism other = (Organism) organisms.get(i);
			if ((this.foodRank == other.foodRank + 1) && (loc.dist(other.loc) < visionR)){
				if(loc.dist(other.loc) <= r){
					ecosystem.killOrganism(this.foodRank - 1, i, other.calories*.25f, other.loc);
					calories += other.calories*.75f;
					hunting = false;
					nearest = 1000;
					break;
				} else {

					float tempDist = loc.dist(other.loc);
					if (tempDist < nearest){
						nearest = tempDist;
						nearestPV = other.loc;
					}
					hunting=true;
	
					acc.set(steer(nearestPV));
				}
			}
		}		
	}

	public PVector steer(PVector target){
		PVector steer;  // The steering vector
	    PVector desired = target.sub(target,loc);  // A vector pointing from the location to the target
	    float d = desired.mag(); // Distance from the target is the magnitude of the vector
	    // If the distance is greater than 0, calc steering (otherwise return zero vector)
	    if (d > 0) {
			// Normalize desired
			desired.normalize();
			desired.mult(maxVel);
			// Steering = Desired minus Velocity
			steer = target.sub(desired,vel);
			steer.limit(maxVel);  // Limit to maximum steering force
	    } else {
	    	steer = new PVector(0,0);
	    }
	    return steer;
	}

	public void reproduce(ArrayList organisms){
	
		ecosystem.spawn(ecoStats[PApplet.parseInt(foodRank)], loc);
		calories -= this.startDiet - 1;

	}

	public void burnCal(){
		this.calories -= this.burnRatePerUpdate;
	}

	public void update() {
		if ((hunting == false) && (hunted == false)){

			// Update velocity
			vel.add(new PVector(random(-1,1),random(-1,1)));

		} else{
			vel.add(acc);
		}

		vel.limit(maxVel);
		checkWallCollision();

		// Update org position
		loc.add(vel);

		acc.mult(0);

	}

	public void checkWallCollision(){
		if (loc.x < r ) {
			vel.x = 1;
		}
		if (loc.x > width - r) {
			vel.x = -1;
		}
		if (loc.y < r) {
			vel.y = 1;
		}
		if (loc.y > height - r) {
			vel.y = -1;
		}
	}

	public void render(){
		// Draw
		fill(c2);
		ellipse(loc.x, loc.y, visionR*2, visionR*2);
		fill(c1);
		ellipse(loc.x, loc.y, r*2, r*2);

	}

	public void move(PVector newLoc){
		loc.set(newLoc);
	}
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "terrarium" });
  }
}
