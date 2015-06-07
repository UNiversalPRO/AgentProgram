package unalcol.agents.examples.labyrinth.teseo.universal;


import java.util.TreeMap;

import unalcol.agents.examples.labyrinth.teseo.simple.SimpleTeseoAgentProgram;

//Corregir: Carga Mapa Agente Enloquece

public class UniversalAgent extends SimpleTeseoAgentProgram {
	
	private TreeMap<Integer, TreeMap<Integer, Integer[]>> memory;
	private int posX;
	private int posY;
	
	private int preX;
	private int preY;
	
	private final int N = 0;
	private final int E = 1;
	private final int S = 2;
	private final int O = 3;

	private int compass;
	
	public UniversalAgent(){
		memory = new TreeMap<Integer, TreeMap<Integer,Integer[]>>();
		posX = 0;
		posY = 0;
		preX = 0;
		preY = 0;
		compass = 0;
	}
	
    //0 al frente, 1 a la derecha, 2 atras, 3 a la izquierda, 4 morir X(
	
	@Override
	public int accion( boolean PF, boolean PD, boolean PA, boolean PI, boolean MT ) {
		int ret = 4;
		if( MT ){ return 4; }
		
		if( !memory.containsKey( posX ) ){
			memory.put( posX, new TreeMap<Integer, Integer[]>() );
		}
		if( !memory.get( posX ).containsKey( posY ) ){
			memory.get( posX ).put( posY, new Integer[4] );
		}
		
		if( PF ){
			memory.get( posX ).get( posY )[( N + compass ) % 4] = -1;
		}else{
			if ( memory.get( posX ).get( posY )[( N + compass ) % 4] == null ) {
				memory.get( posX ).get( posY )[( N + compass ) % 4] = 0;
			}
		}
		
		if( PD ){
			memory.get( posX ).get( posY )[( E + compass ) % 4] = -1;
		}else{
			if ( memory.get( posX ).get( posY )[( E + compass ) % 4] == null ) {
				memory.get( posX ).get( posY )[( E + compass ) % 4] = 0;
			}
		}
		
		if( PA ){
			memory.get( posX ).get( posY )[( S + compass ) % 4] = -1;
		}else{
			if ( memory.get( posX ).get( posY )[( S + compass ) % 4] == null ) {
				memory.get( posX ).get( posY )[( S + compass ) % 4] = 0;
			}
		}
		
		if( PI ){
			memory.get( posX ).get( posY )[( O + compass ) % 4] = -1;
		}else{
			if ( memory.get( posX ).get( posY )[( O + compass ) % 4] == null ) {
				memory.get( posX ).get( posY )[( O + compass ) % 4] = 0;
			}
		}
		

		//Imprimir 
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
		
		int count = 0;
		for ( int i = 0; i < 4; i++ ) {
			if( memory.get( posX ).get( posY )[i] >= 0 ){
				count++;
			}
		}
			
		if( count == 1 ){
			for ( int i = compass, c = 0; c < 4; i = ( i + 1 ) % 4, c++ ) {
				if( memory.get( posX ).get( posY )[i] >= 0 ){
					memory.get( posX ).get( posY )[lastMovement( 1 )] = -1;  //pone pared donde sale de la casilla actual
					int tmpFuture[] = newPosition( i );
					if( memory.containsKey( tmpFuture[0] ) ){
						if( memory.get( tmpFuture[0] ).containsKey( tmpFuture[1] ) ){
							memory.get( tmpFuture[0] ).get( tmpFuture[1] )[( i + 2 ) % 4] = -2;
						}else{
							newElement(i, -2);
						}
					}else{
						newElement(i, -2);
					}
					ret = turn( i );
					break;
				}
			}
		}else if( count == 2 ){
			boolean exit = false;
			for ( int j = 0; j < ( 2 << 30 ) - 1; j++ ) {
				for ( int i = compass, c = 0; c < 4; i = ( i + 1 ) % 4, c++ ) {
					if( memory.get( posX ).get( posY )[i] == j ){
						int tmpFuture[] = newPosition( i );
						if( preX != tmpFuture[0] || preY != tmpFuture[1] ){
							ret = turn( i );
							exit = true;
							break;
						}
					}
				}
				if( exit ){
					break;
				}
			}
		}else if( count == 3 || count == 4 ){
			
			if( preX != posX || preY != posY ){
				memory.get( preX ).get( preY )[lastMovement( -1 )]++;  //marca el camino donde sale de la casilla previa
				memory.get( posX ).get( posY )[lastMovement( 1 )]++;   //marca el camino donde entra de la casilla actual
			}

			boolean exit = false;
			boolean priority = false;
			int tmpI = -1;
			for ( int i = compass, c = 0; c < 4; i = ( i + 1 ) % 4, c++ ) {
				if( memory.get( posX ).get( posY )[i] == 0 ){
					int tmpFuture[] = newPosition( i );
					
					if( memory.containsKey( tmpFuture[0] ) ){
						if( !memory.get( tmpFuture[0] ).containsKey( tmpFuture[1] ) ){
							newElement( i, 0 );
							priority = true;
						}
					}else{
						newElement( i, 0 );
						priority = true;
					}
					
					tmpI = i;
					exit = true;
					if( priority ) {
						memory.get( tmpFuture[0] ).get( tmpFuture[1] )[( i + 2 ) % 4]++;
						break;
					}else if( i == 3 ){
						memory.get( tmpFuture[0] ).get( tmpFuture[1] )[( i + 2 ) % 4]++;
					}
				}
			}
			if( tmpI != -1 ){
				memory.get( posX ).get( posY )[tmpI]++;  // marca el camino cuando sale de la casilla actual
				ret = turn( tmpI );
			}
			
			if ( !exit )
			for ( int j = 1; j < ( 2 << 30 ) - 1; j++ ) {
				for ( int i = compass, c = 0; c < 4; i = ( i + 1 ) % 4, c++ ) {
					if( memory.get( posX ).get( posY )[i] == j ){
						memory.get( posX ).get( posY )[i]++;  // marca el camino cuando sale de la casilla actual
						int tmpFuture[] = newPosition( i );
						
						if( memory.containsKey( tmpFuture[0] ) ){
							if( !memory.get( tmpFuture[0] ).containsKey( tmpFuture[1] ) ){
								newElement( i, 0 );
							}
						}else{
							newElement( i, 0 );
						}
						memory.get( tmpFuture[0] ).get( tmpFuture[1] )[( i + 2 ) % 4]++;
						ret = turn( i );
						exit = true;
						break;
					}
				}
				if( exit ){
					break;
				}
			}
		}
		
		for ( int i = 0; i < ret; i++ ) {
			compass = ( compass + 1 ) % 4;
		}
		
		return ret;
	}
	
	private int turn( int orientation ){
		preX = posX;
		preY = posY;
		if( orientation == 0 ){
			posY++;
		}
		if( orientation == 1 ){
			posX++;
		}
		if( orientation == 2 ){
			posY--;
		}
		if( orientation == 3 ){
			posX--;
		}
		System.out.println("orientacion ="+orientation+" compas = "+compass);
		if( orientation - compass < 0){
			return ( 4 + orientation - compass ) % 4;
		}else{
			return ( orientation - compass ) % 4;
		}
	}
	
	private int[] newPosition( int orientation ){
		int x = posX, y = posY;
		if( orientation == 0 ){
			y = posY + 1;
		}
		if( orientation == 1 ){
			x = posX + 1;
		}
		if( orientation == 2 ){
			y = posY - 1;
		}
		if( orientation == 3 ){
			x = posX - 1;
		}
		int []pos = {x, y};
	
		return pos;
	}
	
	/**
	 * 
	 * @param mult Verifica si es pre o post
	 * @return la orientación de donde una entra
	 */
	private int lastMovement( int mult ){
		int x = ( posX - preX ) * mult;
		int y = ( posY - preY ) * mult;
		if( x < 0 ){
			return E;
		}
		if( x > 0 ){
			return O;
		}
		if( y < 0 ){
			return N;
		}
		return S;	
	}
	
	private void newElement( int orientation, int mark ){
		int tmpFuture[] = newPosition( orientation );
		if( !memory.containsKey( tmpFuture[0] ) ){
			memory.put(tmpFuture[0], new TreeMap<Integer, Integer[]>() );
		}
		if( !memory.get( tmpFuture[0] ).containsKey( tmpFuture[1] ) ){
			memory.get( tmpFuture[0] ).put( tmpFuture[1], new Integer[4] );
			
		}
		memory.get( tmpFuture[0] ).get( tmpFuture[1] )[( orientation + 2 ) % 4] = mark;
	}
	
	
}
