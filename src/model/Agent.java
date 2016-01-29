/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import java.util.LinkedList;
import sun.nio.cs.AbstractCharsetProvider;

/**
 *
 * @author Léo
 */
public class Agent extends Entity implements Runnable {
        static boolean m_talkative=true;
	enum MemType {
		O, A, B
	};

	private static final int s_maxMemorySize = 8;
	private LinkedList<MemType> m_memory;
	private int step = 1;
	private Entity carriedItem=null;
	private final double KPLUS = 0.1;
	private final double KMINUS = 0.3;
        Object m_sem;

	public Agent(Case c) {
		super(c);
		this.m_string = "*";
		carriedItem = null;
		m_memory = new LinkedList<MemType>();
                m_sem=this.m_grid.getSem();
	}

	public void addToMem(String memStr) {
		if (memStr == null || memStr.equals("O")) {
			m_memory.add(MemType.O);
		} else if (memStr.equals("A")) {
			m_memory.add(MemType.A);
		} else if (memStr.equals("B")) {
			m_memory.add(MemType.B);
		}
		if (m_memory.size() > s_maxMemorySize) {
			m_memory.removeFirst();
		}
	}

        
        @Override
	public void run() {
		boolean finished = false;
		
		while(!finished) {
			
			if(null != carriedItem) {
                                if(m_talkative)
                                    System.out.println(this.toString() + " Porte un objet");
				boolean dropCase = dropItem();
				if(!dropCase) {
                                       if(m_talkative)
                                            System.out.println(this.toString() + " Se d�place");
					move();
				}
				else {
                                    if(m_talkative)
					System.out.println(this.toString() + " Lache l'objet");
				}
			}
			else {
                                if(m_talkative)
                                    System.out.println(this.toString() + " Ne porte pas d'objet");
				boolean itemGrabbed = grabItem();
				if(!itemGrabbed) {
                                        if(m_talkative)
                                            System.out.println(this.toString() + " Se d�place2");
					move();
				}
				else {
                                    if(m_talkative)
					System.out.println(this.toString() + " Attrape un objet");
				}
			}
                        try{
                            Thread.sleep(100);
                        }
                        catch(Exception e){
                            System.err.println(e);
                        }
                             if (MainWindow.getInstance() != null) {
                    MainWindow.getInstance().drawGrid(m_grid);
                }
		}
		
	}

	private ArrayList<Case> neighbors() {
		ArrayList<Case> neighbors = new ArrayList<Case>();
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(i == 1 && j == 1) {
					continue;
				}
				int x = this.m_currentCase.m_x-1+i;
				int y = this.m_currentCase.m_y-1+j;
				if( x < this.m_grid.M_SIZEX && x >= 0 && y < this.m_grid.M_SIZEY && y >= 0 ) {
					neighbors.add(this.m_currentCase.m_grid.m_cases[x][y]);
				}
			}
		}
		return neighbors;
	}
	
	private ArrayList<Case> freeNeighbors() {
		ArrayList<Case> freeNeighbors = new ArrayList<Case>();
                ArrayList<Case> neighb= neighbors();
		for(Case c:neighb) {
			if( c!=null  &&  c.m_agent==null) {
                             // System.out.println("addfree");
				freeNeighbors.add(c);
			}
		}
		return freeNeighbors;
	}
	
	private void move() {
            synchronized(m_sem){
                if(m_currentCase!=null && m_currentCase.m_item!=null){
                    addToMem(m_currentCase.m_item.getString());
                }
                else 
                    addToMem(null);
                //System.out.println("se déplace");
		ArrayList<Case> freeNeighbors = freeNeighbors();
		if(freeNeighbors.size() > 0) {
                    System.out.println("feengihb>0");
			int rand = (int) Math.floor((Math.random() * freeNeighbors.size()));
			this.setCurrentCase(freeNeighbors.get(rand));
		}
            }
	}

	private boolean grabItem() {
            synchronized(m_sem){
                /*
		ArrayList<Case> listAAround = new ArrayList<Case>();
		ArrayList<Case> listBAround = new ArrayList<Case>();
		ArrayList<Case> l = neighbors();
		l.removeAll(freeNeighbors());
		for(Case c : l) {
                    if(c!=null && c.m_stringToDraw!=null){
			if(c.m_stringToDraw.equals("A")) {
				listAAround.add(c);
			}
			if(c.m_stringToDraw.equals("B")) {
				listBAround.add(c);
			}
                    }
		}
                        */
                if(this.m_currentCase.m_item!=null){
                    double p;
                    if(this.m_currentCase.m_item.getClass()==ItemA.class){
                         p= Math.pow(KPLUS/(KPLUS + countItemsInMemory(m_memory, MemType.A)/s_maxMemorySize), 2);
                    }
                    else{
                        p = Math.pow(KPLUS/(KPLUS + countItemsInMemory(m_memory, MemType.B)/s_maxMemorySize), 2);
                    
                    }
                    double rand = Math.random();
                    if(rand<p){
                        this.carriedItem=m_currentCase.m_item;
                        m_currentCase.setItem(null);
                    }           
                }
                return (carriedItem!=null);		
		
            }
	}
	
	private boolean dropItem() {
            synchronized(m_sem){
		double rand = Math.random();
		String letter = (this.carriedItem.m_string);
		double p;
		ArrayList<Case> freeNeighbors = freeNeighbors();
		if(this.m_currentCase.m_item==null ){
			return false;
		}
		if(letter.equals("A")) {
			p = Math.pow( (countItemsAround(MemType.A)/freeNeighbors.size()) / (KMINUS+(countItemsAround(MemType.A)/freeNeighbors.size()) ), 2);
		}
		else {
			p = Math.pow( (countItemsAround(MemType.B)/freeNeighbors.size()) / (KMINUS+(countItemsAround(MemType.B)/freeNeighbors.size()) ), 2);
		}
		if(rand < p && freeNeighbors.size() > 0) {
			//int rand2 = (int) Math.floor((Math.random() * freeNeighbors.size()));
			//Case dropCase = freeNeighbors.get(rand2);
			this.m_grid.m_cases[m_currentCase.m_x][m_currentCase.m_y].setItem(carriedItem);
			this.carriedItem = null;
			return true;
		}
		return false;
            }
	}
	 
	private int countItemsInMemory(LinkedList<MemType> list, MemType mt) {
		if(null == list) {
			return 0;
		}
		int count = 0;
		for(MemType m : list) {
			if(mt.equals(m)) {
				count++;
			}
		}
		return count;
	}
	
	private int countItemsAround(MemType mt) {
		ArrayList<Case> neighbors = neighbors();
		if(null == neighbors || 0 == neighbors.size()) {
			return 0;
		}
		int count = 0;
		for(Case c : neighbors) {
			if(null != c && c.m_stringToDraw.equals(mt.toString())) {
				count++;
			}
		}
		return count;
	}
        
      public void setCurrentCase(Case c) {
    	//this.m_grid.m_cases[c.m_x][c.m_y] = this.m_grid.m_cases[m_currentCase.m_x][m_currentCase.m_y];
    	//this.m_grid.m_cases[m_currentCase.m_x][m_currentCase.m_y] = null;
    	//System.out.println("currentcasechange");
        this.m_grid.m_cases[c.m_x][c.m_y].setAgent(this);
        this.m_grid.m_cases[m_currentCase.m_x][m_currentCase.m_y].setAgent(null);
        this.m_currentCase = c;
    }
}
