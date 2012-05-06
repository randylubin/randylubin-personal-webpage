class Ecosystem{
	ArrayList organisms;

	Ecosystem(){
		organisms = new ArrayList();
	}

	void run() {
		for (int i = 0; i < organisms.size(); i++){
			Organism o = (Organism) organisms.get(i);
			o.run(organisms);
			if (o.calories < o.startDiet/2) this.killOrganism(o.foodRank, i, o.calories, o.loc);

		}
	}

	void addOrganism(Organism o){
		organisms.add(o);
	}

	void spawn(float[] oS, PVector tempL){	
		PVector l = tempL;
		Organism o;
		o = new Organism(oS, l);
		l.add(new PVector(random(-3,3),random(-3,3)));
		o.move(l);
		ecosystem.addOrganism(o);	
	}

	void decompose(PVector l, int pop){

		for (int i = 0; i < pop; i++){
			Organism o;
	
			l.add(new PVector(random(-3,3),random(-3,3)));
			o = new Organism(ecoStats[0], l);
			addOrganism(o);	
		}
	}

	void killOrganism(float fr, int i, float cal, PVector l){
		organisms.remove(i);
		if (fr > 0){
			decompose(l, int(cal));
		}
	}
}
