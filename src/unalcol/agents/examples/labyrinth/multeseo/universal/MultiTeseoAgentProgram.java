package unalcol.agents.examples.labyrinth.multeseo.universal;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

public abstract class MultiTeseoAgentProgram implements AgentProgram {

	protected SimpleLanguage language;
	protected Vector<String> cmd = new Vector<String>();

	public MultiTeseoAgentProgram() {
	}

	public void setLanguage(SimpleLanguage _language) {
		language = _language;
	}

	public void init() {
		cmd.clear();
	}

	public abstract int accion(boolean PF, boolean PD, boolean PA, boolean PI,
			boolean MT, boolean AF, boolean AD, boolean AA, boolean AI);
	
	public abstract void move();
	
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
			boolean AF = ((Boolean) p.getAttribute(language.getPercept(5)))
					.booleanValue();
			boolean AD = ((Boolean) p.getAttribute(language.getPercept(6)))
					.booleanValue();
			boolean AA = ((Boolean) p.getAttribute(language.getPercept(7)))
					.booleanValue();
			boolean AI = ((Boolean) p.getAttribute(language.getPercept(8)))
					.booleanValue();

			int d = accion(PF, PD, PA, PI, MT, AF, AD, AA, AI);
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
		cmd.remove(0);
		
		if( x.equals( language.getAction(2) ) ){ // advance
			if( ( (Boolean) p.getAttribute( language.getPercept(5) ) ).booleanValue() 
					|| ((Boolean) p.getAttribute(language.getPercept(0))).booleanValue() ){ // agente al frente o pared al frente
				System.out.println("***************************EROR**********************************");
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
