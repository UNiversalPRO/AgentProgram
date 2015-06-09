package unalcol.agents.examples.labyrinth.multeseo.universalEater;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

public abstract class EaterTeseoAgentProgram implements AgentProgram {

	protected SimpleLanguage language;
	protected Vector<String> cmd = new Vector<String>();
	protected int id;//1 - prueba posiciones, 2 - prueba dijkstra, 3 - prueba comida
	public EaterTeseoAgentProgram() {
	}

	public void setLanguage(SimpleLanguage _language) {
		language = _language;
	}

	public void init() {
		cmd.clear();
	}

	public abstract int accion(boolean PF, boolean PD, boolean PA, boolean PI,
			boolean MT, boolean RSRC, boolean COLOR, boolean SHAPE, boolean SIZE, boolean WEIGHT, int TYPE, boolean LEVEL);
	
	public abstract void move();
	
	/**
	 * 
	 * @return true si la comida es buena o no se sabe
	 * 		   false si la comida es mala o el agente esta satisfecho
	 */
	public abstract boolean eat( int energy, int code );
	
	public abstract void clearMemories();
	
	/**
	 * execute
	 *
	 * @param perception
	 *            Perception
	 * @return Action[]
	 */
	public Action compute(Percept p) {
		if (cmd.size() == 0) {
			boolean PF = ((Boolean) p.getAttribute(language.getPercept(0)))
					.booleanValue();
			boolean PD = ((Boolean) p.getAttribute(language.getPercept(1)))
					.booleanValue();
			boolean PA = ((Boolean) p.getAttribute(language.getPercept(2)))
					.booleanValue();
			boolean PI = ((Boolean) p.getAttribute(language.getPercept(3)))
					.booleanValue();
			boolean MT = ((Boolean) p.getAttribute(language.getPercept(4)))
					.booleanValue();
			boolean RSRC = ((Boolean) p.getAttribute(language.getPercept(5)))
					.booleanValue();
			Boolean per = ((Boolean) p.getAttribute(language.getPercept(6)));
			boolean COLOR = false;
			if(per != null){
				COLOR = per.booleanValue();
			}
			per = ((Boolean) p.getAttribute(language.getPercept(7)));
			boolean SHAPE = false;
			if(per != null){
				SHAPE = per.booleanValue();
			}
			per = ((Boolean) p.getAttribute(language.getPercept(8)));
			boolean SIZE = false;
			if(per != null){
				SIZE = per.booleanValue();
			}
			per = ((Boolean) p.getAttribute(language.getPercept(9)));
			boolean WEIGHT = false;
			if(per != null){
				WEIGHT = per.booleanValue();
			}
			int TYPE = ((Integer) p.getAttribute(language.getPercept(10)));
			per = ((Boolean) p.getAttribute(language.getPercept(11)));
			boolean LEVEL = false;
			if(per != null){
				LEVEL = per.booleanValue();
			}
			
			
			int d = accion(PF, PD, PA, PI, MT, RSRC, COLOR, SHAPE, SIZE, WEIGHT, TYPE, LEVEL);
			/**
			 * d = 0 el automata no gira d = 1 el automata gira una vez d = 2 el
			 * automata gira dos veces d = 3 el automata gira tres veces Despues
			 * de terminar los giros el automata se mueve hacia adelante
			 */
			if (0 <= d && d < 4) {
				for (int i = 1; i <= d; i++) {
					cmd.add(language.getAction(3)); // rotate
				}

				cmd.add(language.getAction(2)); // advance
			} else {
				cmd.add(language.getAction(0)); // die
			}
		}
		
		String x = cmd.get(0);
		////////////////////////Imprimir
//		for (int i = 0; i < cmd.size(); i++) {
//			System.out.print(cmd.get(i) + " ");
//		}
//		System.out.println();
//		
//		try {
//			Thread.sleep(3000);
//		} catch (Exception e) {
//		}
		//////////////////////////////////
		if( ( ( Boolean ) p.getAttribute( language.getPercept( 5 ) ) ).booleanValue() ){ //hay comida
			Boolean per = ((Boolean) p.getAttribute(language.getPercept(6)));
			boolean COLOR = false;
			if(per != null){
				COLOR = per.booleanValue();
			}
			per = ((Boolean) p.getAttribute(language.getPercept(7)));
			boolean SHAPE = false;
			if(per != null){
				SHAPE = per.booleanValue();
			}
			per = ((Boolean) p.getAttribute(language.getPercept(8)));
			boolean SIZE = false;
			if(per != null){
				SIZE = per.booleanValue();
			}
			per = ((Boolean) p.getAttribute(language.getPercept(9)));
			boolean WEIGHT = false;
			if(per != null){
				WEIGHT = per.booleanValue();
			}
			int TYPE = ((Integer) p.getAttribute(language.getPercept(10)));
			int code = 0;
			if(COLOR){
				code += 1; 
			}else{
				code += 2;
			}
			if(SHAPE){
				code += 10; 
			}else{
				code += 20;
			}
			if(SIZE){
				code += 100; 
			}else{
				code += 200;
			}
			if(WEIGHT){
				code += 1000; 
			}else{
				code += 2000;
			}
			if( eat( TYPE, code ) ){
				return new Action( language.getAction(4) ); // comer
			}
		}
		cmd.remove(0);
		if( x.equals( language.getAction(2) ) ){ // advance
			if( ( ( Boolean ) p.getAttribute( language.getPercept(0) ) ).booleanValue() ){ //pared al frente
				clearMemories();
				if(id != 0)
					System.err.println("*****************************error***********************************");
				return new Action( language.getAction(0) );
			}
			move();
		}
		try{
			return new Action(x);
		}catch( Exception e ){
			clearMemories();
			return new Action( language.getAction(0) );
		}
	}

	/**
	 * goalAchieved
	 *
	 * @param perception
	 *            Perception
	 * @return boolean
	 */
	public boolean goalAchieved(Percept p) {

		return (((Boolean) p.getAttribute(language.getPercept(4)))
				.booleanValue());
	}
	
}
