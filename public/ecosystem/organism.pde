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

	color c1;
	color c2;

	Organism(float[] orgStats, PVector startLoc){
		this.foodRank = orgStats[0];
		this.r = orgStats[1]/2;
		this.visionR = orgStats[2]/2;
		this.maxVel = orgStats[3];
		this.startDiet = orgStats[4];
		this.calories = orgStats[4];
		this.burnRatePerUpdate = orgStats[5]*(1.0/fPS);
		this.hunting = false;
		this.nearest = 1000;
		this.nearestPV = new PVector(0,0);
		colorize(int(foodRank));

		loc = new PVector(random(r, width - r), random(r, height - r));

		vel = new PVector(random(-maxVel, maxVel),random(-maxVel, maxVel));
		
		acc = new PVector(0,0);
		//render();
	}

	void colorize(int foodRank){
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

	void run(ArrayList organisms){
		if (foodRank != 0){
			interact(organisms);
			if (calories >= 2*startDiet) reproduce(organisms);
			burnCal();
		}

		//plant reproduction
		if ((foodRank == 0) && (organisms.size() < 100)){
			if (get(int(loc.x),int(loc.y)) == color(52,52,52)){
				float f = random(0,1);
				if (f < 1.0/120.0) reproduce(organisms);
			}
		}
		update();
		render();
	}

	void interact(ArrayList organisms){
		if (foodRank < 2) findPred(organisms);
		if (!hunted) findPrey(organisms);
	}

	void findPred(ArrayList organisms){
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

	void findPrey(ArrayList organisms){
		hunting = false;
		nearest = 1000;
		for (int i = 0; i < organisms.size(); i++){
			Organism other = (Organism) organisms.get(i);
			if ((this.foodRank == other.foodRank + 1) && (loc.dist(other.loc) < visionR)){
				if(loc.dist(other.loc) <= r){
					ecosystem.killOrganism(this.foodRank - 1, i, other.calories*.25, other.loc);
					calories += other.calories*.75;
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

	PVector steer(PVector target){
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

	void reproduce(ArrayList organisms){
	
		ecosystem.spawn(ecoStats[int(foodRank)], loc);
		calories -= this.startDiet - 1;

	}

	void burnCal(){
		this.calories -= this.burnRatePerUpdate;
	}

	void update() {
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

	void checkWallCollision(){
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

	void render(){
		// Draw
		fill(c2);
		ellipse(loc.x, loc.y, visionR*2, visionR*2);
		fill(c1);
		ellipse(loc.x, loc.y, r*2, r*2);

	}

	void move(PVector newLoc){
		loc.set(newLoc);
	}
}
