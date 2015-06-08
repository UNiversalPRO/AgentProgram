package unalcol.agents.examples.labyrinth.multeseo.universalEater;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;


//Corregir: Carga Mapa Agente Enloquece
//			Mirar como evitar que se mueva donde acaba de llegar un agente

public class EaterUniversalAgent extends EaterTeseoAgentProgram {
	
	private TreeMap<Integer, TreeMap<Integer, Integer[]>> memory;
	private TreeMap<Integer,Double> food;//X, Y //1: true 2: false /Probabilidad 
	private TreeMap<Node, TreeMap<Node, Integer[]>> G; //[peso, salida]
	private TreeSet<Node> completeNodes;
	private LinkedList<Integer> ways;
	
	private Node ultimeNode;
	private int ultDirNode;
	
	private int distance = 0; //cuenta la distancia de un nodo a otro
	private int waiting = 0; //variable que sirve para esperar si el otro agente se va a mover
	
	private int posX;
	private int posY;
	private int preX;
	private int preY;

	private final int N = 0;
	private final int E = 1;
	private final int S = 2;
	private final int O = 3;

	private int compass;
	private int lastEnergy = 20;
	
	private int id = 0;
	private boolean eatYet = false;
	
	
	public EaterUniversalAgent() {
		memory = new TreeMap<Integer, TreeMap<Integer, Integer[]>>(); // pos X, pos Y [PF, PD, PA, PI]
		G = new TreeMap<Node, TreeMap<Node,Integer[]>>(); // para dijkstra
		food = new TreeMap<Integer, Double>();
		ways = new LinkedList<Integer>();
		completeNodes = new TreeSet<Node>();
		posX = 0;
		posY = 0;
		preX = 0;
		preY = 0;
		compass = 0;
		
	}

	public EaterUniversalAgent(int id) { // AGENTE DE PRUEBAS
		this();
		this.id = id;
	}

	// 0 al frente, 1 a la derecha, 2 atras, 3 a la izquierda, 4 quieto :|
	@Override
	public int accion( boolean PF, boolean PD, boolean PA, boolean PI,
			boolean MT, boolean RSRC, boolean COLOR, boolean SHAPE, boolean SIZE, 
			boolean WEIGHT, int TYPE, boolean LEVEL ) {
		
		if (MT) {
			return 4;
		}

		boolean agent[] = new boolean[4];

		if (!memory.containsKey(posX)) {
			memory.put(posX, new TreeMap<Integer, Integer[]>());
		}
		if (!memory.get(posX).containsKey(posY)) {
			memory.get(posX).put(posY, new Integer[4]);
		}

		if (PF) {
			memory.get(posX).get(posY)[(N + compass) % 4] = -1;
		} else {
			if (memory.get(posX).get(posY)[(N + compass) % 4] == null) {
				memory.get(posX).get(posY)[(N + compass) % 4] = 0;
			}
		}

		if (PD) {
			memory.get(posX).get(posY)[(E + compass) % 4] = -1;
		} else {
			if (memory.get(posX).get(posY)[(E + compass) % 4] == null) {
				memory.get(posX).get(posY)[(E + compass) % 4] = 0;
			}
		}

		if (PA) {
			memory.get(posX).get(posY)[(S + compass) % 4] = -1;
		} else {
			if (memory.get(posX).get(posY)[(S + compass) % 4] == null) {
				memory.get(posX).get(posY)[(S + compass) % 4] = 0;
			}
		}

		if (PI) {
			memory.get(posX).get(posY)[(O + compass) % 4] = -1;
		} else {
			if (memory.get(posX).get(posY)[(O + compass) % 4] == null) {
				memory.get(posX).get(posY)[(O + compass) % 4] = 0;
			}
		}

		// ////////////////////Imprimir si es agente de pruebas
		 if(id == 1){
			 System.out.println( "anterior = "+preX +", "+ preY );
			 for ( int i = 0; i < 4; i++ ) {
			 System.out.print( memory.get( preX ).get( preY )[i] + " " );
			 }
			 System.out.println();
			 System.out.println( "actual = "+posX +", "+ posY );
			 for ( int i = 0; i < 4; i++ ) {
			 System.out.print( memory.get( posX ).get( posY )[i] + " " );
			 }
			 System.out.println();
		 System.out.println("Se movio");
		 }
		// ////////////////////

		int numberOfWays = 0;
		for (int i = 0; i < 4; i++) {
			if (memory.get(posX).get(posY)[i] >= 0) {
				numberOfWays++;
			}
		}
		
		if ( numberOfWays == 0 ){
			if( ultimeNode != null ){
				int tmp = 0;
				for (int i = 0; i < 4; i++) {
					if( memory.get( ultimeNode.x ).get( ultimeNode.y )[i] != -1 ){
						tmp++;
					}
				}
				if( tmp == 3 ){
					completeNodes.add( new Node( posX, posY ) );
				}
			}
			distance = 0;
			return 0;
		}
		
		if ( numberOfWays == 1 ) {
			
			distance = 0;
			for (int i = compass, c = 0; c < 4; i = (i + 1) % 4, c++) {
				if (memory.get(posX).get(posY)[i] >= 0) {
					return turn(i);
				}
			}
		} else if ( numberOfWays == 2 ) {
			distance++;
			for (int j = 0; j < (2 << 30) - 1; j++) { // es para irse primero por el de menor marca
				for ( int i = 0; i < 4; i++ ) {
					int tmpFut[] = newPosition( i );
					if( !memory.containsKey(tmpFut[0]) || !memory.get(tmpFut[0]).containsKey( tmpFut[1] ) ){
						if ( memory.get(posX).get(posY)[i] == j ) {
							int tmpFuture[] = newPosition(i);
							if (preX != tmpFuture[0] || preY != tmpFuture[1]) { // No devolverse en el camino que iba
								if ( agent[i] && waiting < 3 ) { // espera si el agente esta por el camino al que iba
									waiting++;
									return 4;
								}
								waiting = 0;
								if (agent[i]) { // se devuelve por el camino en el que estaba
									for (int l = ( compass + 1 ) % 4, k = 0; k < 4; l = (l + 1) % 4, k++) {
										if ( memory.get(posX).get(posY)[l] >= 0 ) {
											return turn(l);										
										}
									}
								} else { // continua por el camino al que iba

									return turn(i);
								}
								break;
							}
						}
					}
				}
				
				for (int i = compass, c = 0; c < 4; i = (i + 1) % 4, c++) { // recorre los 4 lados, menos rotaciones
					if (memory.get(posX).get(posY)[i] == j) {
						int tmpFuture[] = newPosition(i);
						if (preX != tmpFuture[0] || preY != tmpFuture[1]) { // No devolverse en el camino que iba
							if ( agent[i] && waiting < 3 ) { // espera si el agente esta por el camino al que iba
								waiting++;
								return 4;
							}
							waiting = 0;
							if (agent[i]) { // se devuelve por el camino en el que estaba
								for (int l = ( compass + 1 ) % 4, k = 0; k < 4; l = (l + 1) % 4, k++) {
									if ( memory.get(posX).get(posY)[l] >= 0 ) {
										return turn(l);
									}
								}
							} else { // continua por el camino al que iba

								return turn(i);							
							}
							break;
						}
					}
				}
			}
		} else if ( numberOfWays == 3 || numberOfWays == 4 ) {
			
			if ( preX != posX || preY != posY ) {
				memory.get(preX).get(preY)[lastMovement(-1)]++; // marca el camino donde sale de la casilla previa
				memory.get(posX).get(posY)[lastMovement(1)]++; // marca el camino donde entra de l casilla actual
			}
			
			Node thisNode = new Node( posX, posY );
			int numCero = 0;
			for ( int i = 0; i < 4; i++ ) {
				if(memory.get(posX).get(posY)[i] == 0){
					numCero++;
				}
			}
			if( numCero == 0 ) {
				completeNodes.add( thisNode );
			}
			if( ultimeNode != null ){	
				if( G.containsKey( thisNode ) && G.get( thisNode ).containsKey( ultimeNode ) ){
					if( distance < G.get( thisNode ).get( ultimeNode )[0] ){
						makeLink( thisNode, ultimeNode, distance,  ultDirNode, (compass + 2) % 4 );
					}
				}else{
					makeLink( thisNode, ultimeNode, distance, ultDirNode, (compass + 2) % 4 );
				}
			}
			
			ultimeNode = thisNode;
			distance = 0;
			
			if( !ways.isEmpty() ){
				return turn( ways.removeFirst() );
			}
			
			if( completeNodes.contains( thisNode ) ){
				
				if(id == 2){
					System.out.print("{");
					for( Node a : G.keySet() ){
						System.out.print(a+"={");
						for(Node b : G.get( a ).keySet()){
							System.out.print(b+"={");
							for ( int c: G.get(a).get(b) ) {
								System.out.print(c + ",");
							}
							System.out.print("},");
						}
						System.out.print("}||");
					}
					System.out.println("}");
					System.out.println( completeNodes );
				}

				int ret = dijkstra( thisNode );
				if( ret != 4 ) return turn( dijkstra( thisNode ) );
			}
			
			if( numCero == 1 ) {
				completeNodes.add( thisNode );
			}

			boolean priority = false;
			int tmpI = -1;
			Random rnd = new Random();
			for (int i = rnd.nextInt( 4 ), c = 0; c < 4; i = (i + 1) % 4, c++) {
				if (memory.get(posX).get(posY)[i] == 0) {
					if ( agent[i] && waiting < 3 ) { // espera si el agente esta por el camino al que iba
						waiting++;
						return 4;
					}
					waiting = 0;
					if( agent[i] ){
						continue;
					}
					
					int tmpFuture[] = newPosition(i);

					if (memory.containsKey(tmpFuture[0])) {
						if (!memory.get(tmpFuture[0]).containsKey(tmpFuture[1])) {
							newElement(i, 0);
							priority = true;
						}
					} else {
						newElement(i, 0);
						priority = true;
					}

					tmpI = i;
					if (priority) {
						memory.get(tmpFuture[0]).get(tmpFuture[1])[(i + 2) % 4]++;
						break;
					} else if (i == 3) {
						memory.get(tmpFuture[0]).get(tmpFuture[1])[(i + 2) % 4]++;
					}
				}
			}
			if (tmpI != -1) {
				memory.get(posX).get(posY)[tmpI]++; // marca el camino cuando sale de la casilla actual
				int tmp =  turn(tmpI);
				ultDirNode = compass;
				return tmp;
			}

			for (int j = 1; j < (2 << 30) - 1; j++) {
				for (int i = compass, c = 0; c < 4; i = (i + 1) % 4, c++) {
					if (memory.get(posX).get(posY)[i] == j) {
						if ( agent[i] && waiting < 3 ) { // espera si el agente esta por el camino al que iba
							waiting++;
							return 4;
						}
						waiting = 0;
						if( agent[i] ){
							continue;
						}
						
						memory.get(posX).get(posY)[i]++; // marca el camino cuando sale de la casilla actual
						int tmpFuture[] = newPosition(i);

						if (memory.containsKey(tmpFuture[0])) {
							if (!memory.get(tmpFuture[0]).containsKey(
									tmpFuture[1])) {
								newElement(i, 0);
							}
						} else {
							newElement(i, 0);
						}
						memory.get(tmpFuture[0]).get(tmpFuture[1])[(i + 2) % 4]++;
						int tmp =  turn(i);
						ultDirNode = compass;
						return tmp;
					}
				}
			}
		}
		return 4;
	}

	/**
	 * 
	 * @param orientation a donde quiere ir
	 * @return el numero de giros necesarios para ir a la posicion que desea
	 */
	private int turn(int orientation) { 
//		System.out.println("orientacion ="+orientation+" compas = "+compass);
		int turn;
		if (orientation - compass < 0) {
			turn = (4 + orientation - compass) % 4;
		} else {
			turn = (orientation - compass) % 4;
		}
		for (int i = 0; i < turn; i++) {
			compass = (compass + 1) % 4;
		}
		return turn;
	}

	private int[] newPosition(int orientation) {
		int x = posX, y = posY;
		if (orientation == 0) {
			y = posY + 1;
		}
		if (orientation == 1) {
			x = posX + 1;
		}
		if (orientation == 2) {
			y = posY - 1;
		}
		if (orientation == 3) {
			x = posX - 1;
		}
		int[] pos = { x, y };

		return pos;
	}

	/**
	 * 
	 * @param mult Verifica si es pre o post
	 * @return la orientación de donde una entra
	 */
	private int lastMovement(int mult) {
		int x = (posX - preX) * mult;
		int y = (posY - preY) * mult;
		if (x < 0) {
			return E;
		}
		if (x > 0) {
			return O;
		}
		if (y < 0) {
			return N;
		}
		return S;
	}

	private void newElement(int orientation, int mark) {
		int tmpFuture[] = newPosition(orientation);
		if (!memory.containsKey(tmpFuture[0])) {
			memory.put(tmpFuture[0], new TreeMap<Integer, Integer[]>());
		}
		if (!memory.get(tmpFuture[0]).containsKey(tmpFuture[1])) {
			memory.get(tmpFuture[0]).put(tmpFuture[1], new Integer[4]);

		}
		memory.get(tmpFuture[0]).get(tmpFuture[1])[(orientation + 2) % 4] = mark;

	}

	
	/**
	 * Actualiza el x, el y 
	 */
	@Override
	public void move() {
		eatYet = false;
		preX = posX;
		preY = posY;
		
		if (compass == 0) {
			posY++;
		}
		if (compass == 1) {
			posX++;
		}
		if (compass == 2) {
			posY--;
		}
		if (compass == 3) {
			posX--;
		}
	}
	
	@Override
	public boolean eat( int energy, int code ){
		
		int tmpLast = lastEnergy;
		lastEnergy = energy;
		
		if( energy == tmpLast){ //se canso de comer		
			return false;
		}
		if( food.containsKey(code) && food.get(code) == 0.0 ){ //sabe que la comida es mala
			return false;
		}
		if( tmpLast > energy && eatYet ){ //probo una comida mala
			food.put(code, 0.0);
			return false;
		}
		if( tmpLast <= energy && eatYet ){ //probo una comida buena
			food.put(code, 1.0);
		}
		
		if( /* !food.isEmpty() && */ ( !food.containsKey(code) || (food.get(code) != 1.0 && food.get(code) != 0.0) ) ){ //no sabe si la comida es buena o mala
			
			if( energy <= 4 ){ // si ya esta apunto de morirse coma
				eatYet = true;
				return true;
			}
			boolean eat = entropyOrder(energy, code);
			if(!eat){
				food.put( code, probability( code ) );
				return false;
			}
		}
		eatYet = true;
		return true;
	}
	
	private double probability( int code ){
		double percent = 0.0;

		for (int i = 0; i < 4; i++) {
			percent += eatFeature( i, code );
		}
		percent /= 4.0;
		
		if(id == 3){
			System.out.println("Probality of food: " + percent);
		}
		return percent;
	}
	
	
	/**
	 * es ordenar cada caracteristica dependiendo de su entropia
	 * @return si come o no come
	 */
	private boolean entropyOrder( int energy, int code ){
		
		TreeMap<Double, ArrayList<Integer>> entropies = new TreeMap<Double, ArrayList<Integer>>();
		ArrayList<Double> enti = new ArrayList<Double>();
	
		enti.add(entropyFeature(0)); //color
		enti.add(entropyFeature(1)); //figura
		enti.add(entropyFeature(2)); //tamaño
		enti.add(entropyFeature(3)); //peso
		enti.add( normalEntropy(energy) ); //Normal
		
		for (int i = 0; i < enti.size(); i++) {
			if( !entropies.containsKey( enti.get(i) ) ){
				entropies.put( enti.get(i), new ArrayList<Integer>() );
			}
			entropies.get(enti.get(i)).add(i);
		}
		
		Random r = new Random();
		for ( Entry<Double, ArrayList<Integer>> ent : entropies.entrySet() ) {
			double percent = 0.0;
			while( !ent.getValue().isEmpty() ){
				switch ( ent.getValue().remove( ent.getValue().size() - 1 ) ) {
				case 0:
					percent = eatFeature(0, code);
					if( r.nextDouble() < percent ){
						return true;
					}
					break;
				case 1:
					percent = eatFeature(1, code);
					if( r.nextDouble() < percent ){
						return true;
					}
					break;
				case 2:
					percent = eatFeature(2, code);
					if( r.nextDouble() < percent ){
						return true;
					}
					break;
				case 3:
					percent = eatFeature(3, code);
					if( r.nextDouble() < percent ){
						return true;
					}
					break;
				default:
					if( eatNormal( energy, ent.getKey() ) ){
						return true;
					}
					break;
				}
			}
		}
		
		return false;
	}
	
	private boolean eatNormal( int energy, double percent ){
		
		Random r = new Random();
		
		if( id == 3 ){
			System.out.println("Normal: " + ( 1 - percent ) );
		}
		
		if( r.nextDouble() < percent ){
			return false;
		}
		else{
			return true;
		}
		
	}
	
	private double eatFeature( int id, int code ){
		
		double percent = 0.0;
		double total0 = 0.0;
		double good0 = 0.0;
		double total1 = 0.0;
		double good1 = 0.0;
		
		for ( Entry<Integer, Double> food1: food.entrySet() ) {
			char[] cod = (food1.getKey()+"").toCharArray();
			double pr = food1.getValue();
			if(cod[id] == '1'){
				total1++;
				good1 += pr;
			}
			else{
				total0++;
				good0 += pr;
			}			
		}
		
		char[] foodCode = (code+"").toCharArray();
		
		char value = foodCode[id];
		
		if(value == '1'){
			if(total1 == 0){
				percent = 0.2;
			}
			else{
				percent = good1 / total1;
			}
		}
		else{
			if(total0 == 0){
				percent = 0.2;
			}
			else{
				percent = good0 / total0;
			}
		}
		
		if(this.id == 3){
			if( id == 0 )
				System.out.println("Color: "+ percent);
			if( id == 1 )
				System.out.println("Figura: "+ percent);
			if( id == 2 )
				System.out.println("Tamaño: "+ percent);
			if( id == 3 )
				System.out.println("Peso: "+ percent);
		}
		
		return percent;
	
	}
	
	/**
	 * saca entropia de la distribucion normal
	 * @param energy energia del agente
	 * @return entropia de la normal
	 */
	private double normalEntropy(int energy){
		double entropy = 0.0;
		
		if(energy > 20){
			energy -= (energy - 20);
		}
		
		double distr = normal(20, 10);
		if( distr > 20 ){
			distr -= (distr - 20);
		}
		if( distr <= energy ){
			entropy = 1.0;
		}
		else{
			if(distr == 0.0){
				entropy = 1.0;
			}else{
				entropy = (double)(energy) / distr;
			}
		}
		
		return entropy;
	}
	
	/**
	 * retorna la entropia de una caracteristica
	 * @param id  id de cada caracteristica de la comida
	 * @return entropia de cada caracteristica
	 */
	private double entropyFeature( int id ){ //0 forma, 1 color....
		double entropy = 0.0;
		double total0 = 0.0;
		double good0 = 0.0;
		double total1 = 0.0;
		double good1 = 0.0;
		
		for ( Entry<Integer, Double> food1: food.entrySet() ) {
			char[] code = (food1.getKey()+"").toCharArray();
			double percent = food1.getValue();
			if(code[id] == '1'){
				total1++;
				good1 += percent;
			}
			else{
				total0++;
				good0 += percent;
			}			
		}
		double p1 = 0.0;
		double p2 = 0.0;
		if(total0 == 0){
			p1 = 1;
		}else{
			p1 =  ( 1 - Math.abs( 0.5 - ( good0 / total0 ) ) );
		}
		if(total1 == 0){
			p2 = 1;
		}else{
			p2 = ( 1 - Math.abs( 0.5 - ( good1 / total1 ) ) );
		}
		entropy = ( p1 + p2 ) / 2;
		
		return entropy;
	}
	
	public void makeLink( Node node1, Node node2, int weight, int s1, int s2 ){
		Integer tmp1[] = {weight, s2};
		Integer tmp2[] = {weight, s1};
		if( !G.containsKey(node1) )
			G.put(node1, new TreeMap<Node,Integer[]>());
		
		G.get(node1).put( node2, tmp1 );
		if(!G.containsKey(node2))
			G.put(node2, new TreeMap<Node,Integer[]>());
		
		G.get(node2).put(node1, tmp2);
	}
	
	private static class Node implements Comparable<Node>{
		private int x;
		private int y;
		
		private Node( int x, int y ) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo( Node o ) {
			if( x != o.x )
				return x - o.x;
			return y - o.y;

		} 
		
		@Override
		public String toString() {
			return "["+x+","+y+"]";
		}
	}

	
	static class Couple implements Comparable<Couple>{
		protected Node key;
		protected Couple parent;
		protected int weight;
		
		public Couple( Node key, int weight, Couple parent ){
			this.key = key;
			this.weight = weight;
			this.parent = parent;
		}

		@Override
		public int compareTo( Couple c ) {
			return this.weight - c.weight;
		}
		
		@Override
		public String toString(){
			return "[Key=" + key + ", Value=" + weight + "]";
		}
	}
	
	
	private int dijkstra( Node source ){
		
		TreeMap<Node, Integer> dist = new TreeMap<>();
		
		PriorityQueue<Couple> pq = new PriorityQueue<>();
		
		pq.add( new Couple( source, 0, null ) );
		while( !pq.isEmpty() ){
			Couple node = pq.remove();
			if(!dist.containsKey( node.key )){
				dist.put( node.key, node.weight );
				if(G.containsKey(node.key)){
					for( Entry<Node, Integer[]> neighbor : G.get(node.key).entrySet()){
						if( !dist.containsKey(neighbor.getKey())){
							Couple neigh = new Couple( neighbor.getKey(), neighbor.getValue()[0] + node.weight, node );
							pq.add( neigh );
							
							if( !completeNodes.contains( neighbor.getKey() ) ){
//								System.out.println( "To node: X:" + neighbor.getKey().x +" Y:" + neighbor.getKey().y );
								fillWay( neigh );
								return ways.removeFirst();
							}
								
						}
					}
				}
			}
		}
//		System.out.println( "There aren't a possible node to go" );
		return 4;
	} 
	
	private void fillWay( Couple ultime ){
		Couple curr = ultime;
		while( curr.parent != null ){
			ways.add( 0, G.get( curr.parent.key ).get( curr.key )[1] );
			curr = curr.parent;
		}
	}

	
    /**
     * Retorna un numero real con una distribucion normal.
     */
    public static double normal(double media, double desviacionEstandar) {
        // se utiliza la forma polar del metodo de Box-Muller
    	// http://es.wikipedia.org/wiki/Metodo_de_Box-Muller
        double r, x, y;
        do {
        	x = Math.random() * 2.0 - 1.0; // [-1,1]
        	y = Math.random() * 2.0 - 1.0; // [-1,1]
            r = x*x + y*y;
        } while (r >= 1 || r == 0);

        double boxMuller = x * Math.sqrt(-2 * Math.log(r) / r);
        // Observacion:  x * Math.sqrt(-2 * Math.log(r) / r) is un independiente valor normal
        
        return media + desviacionEstandar * boxMuller; 
    }	
	@Override
	public void clearMemories(){
		memory = new TreeMap<Integer, TreeMap<Integer, Integer[]>>(); // [PF, PD, PA, PI]
		G = new TreeMap<Node, TreeMap<Node,Integer[]>>();
		ways = new LinkedList<Integer>();
		completeNodes = new TreeSet<Node>();
		posX = 0;
		posY = 0;
		preX = 0;
		preY = 0;
		compass = 0;
		distance = 0;
		waiting = 0;
		ultDirNode = 0;
		ultimeNode = null;
	}
}