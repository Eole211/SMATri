/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author Léo
 */
public class TPTri {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	//Grid g = Grid.getInstance();
    	//MainWindow mw = MainWindow.getInstance();
        //mw.drawGrid(g);
    	Grid g = Grid.getInstance();
    	System.out.println(g.toString());
    	g.startAgents();
    	long start = System.currentTimeMillis();
    	int count = 0;
    	while (true) {
    		if ( System.currentTimeMillis() > (start + (500 * count)) ) {
    			System.out.println(g.toString());
    			count++;
    		}
    	}
    }
    
}