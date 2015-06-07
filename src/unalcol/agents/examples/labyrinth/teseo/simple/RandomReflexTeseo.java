/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.labyrinth.teseo.simple;

/**
 *
 * @author Jonatan
 */
public class RandomReflexTeseo  extends SimpleTeseoAgentProgram {

    public RandomReflexTeseo() {}

    /**
     *
     * @param PF Pared al frente
     * @param PD Pared derecha
     * @param PA Pared atras
     * @param PI Pared a la izquierda
     * @param MT Meta
     * @return
     */
    @Override    
    public int accion(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT) {
        if (MT) return -1;
        boolean flag = true;
        int k=0;
        while( flag ){
            k = (int)(Math.random()*4);
            /**
             * El automata solo se movera cuando el numero de giros k
             * lo deje mirando hacia un lado sin pared.
             * PF = 0
             * PD = 1
             * PA = 2
             * PI = 3
             */
            switch(k){
                case 0:
                    flag = PF;
                    break;
                case 1:
                    flag = PD;
                    break;
                case 2:
                    flag = PA;
                    break;
                default:
                    flag = PI;
                    break;                    
            }
        }
        return k;
    }    
}
