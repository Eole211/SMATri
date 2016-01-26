/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Léo
 */
public class Agent extends Entity implements Runnable {
	enum MemType {
		O, A, B
	};

	private static final int s_maxMemorySize = 8;
	private LinkedList<MemType> m_memory;
	private int step = 1;
	private Entity carriedItem;
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

	public void run() {
		boolean finished = false;
		
		while(!finished) {
			
			if(null != carriedItem) {
				System.out.println(this.toString() + " Porte un objet");
				Case dropCase = dropItem();
				if(null == dropCase) {
					System.out.println(this.toString() + " Se d�place");
					move();
				}
				else {
					System.out.println(this.toString() + " Lache l'objet");
				}
			}
			else {
				System.out.println(this.toString() + " Ne porte pas d'objet");
				boolean itemGrabbed = grabItem();
				if(!itemGrabbed) {
					System.out.println(this.toString() + " Se d�place2");
					move();
				}
				else {
					System.out.println(this.toString() + " Attrape un objet");
				}
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
		for(Case c : neighbors()) {
			if(null != c && null == c.m_entity) {
				freeNeighbors.add(c);
			}
		}
		return freeNeighbors;
	}
	
	private void move() {
            synchronized(m_sem){
		ArrayList<Case> freeNeighbors = freeNeighbors();
		if(freeNeighbors.size() > 0) {
			int rand = (int) Math.floor((Math.random() * freeNeighbors.size()));
			this.setCurrentCase(freeNeighbors.get(rand));
		}
            }
	}

	private boolean grabItem() {
            synchronized(m_sem){
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
		double rand = Math.random();
		double pA = Math.pow(KPLUS/(KPLUS + countItemsInMemory(m_memory, MemType.A)/s_maxMemorySize), 2);
		double pB = Math.pow(KPLUS/(KPLUS + countItemsInMemory(m_memory, MemType.B)/s_maxMemorySize), 2);
		Case selectedItem = null;
		if(!listAAround.isEmpty() && rand < pA) {
			selectedItem = listAAround.get( ((int)Math.floor((Math.random() * listAAround.size()))) );
		}
		else if(!listBAround.isEmpty() && rand < pB + pA) {
			selectedItem = listBAround.get( ((int)Math.floor((Math.random() * listBAround.size()))) );
		}
		
		if(null != selectedItem) {
			System.out.println("Selected item :" + selectedItem.m_stringToDraw);
			this.carriedItem = selectedItem.m_entity;
			selectedItem.m_grid.m_cases[selectedItem.m_x][selectedItem.m_y] = null;
			return true;
		}
		return false;
            }
	}
	
	private Case dropItem() {
            synchronized(m_sem){
		double rand = Math.random();
		String letter = (this.carriedItem.m_string);
		double p;
		ArrayList<Case> freeNeighbors = freeNeighbors();
		if(0 == freeNeighbors.size()) {
			return null;
		}
		if(letter.equals("A")) {
			p = Math.pow( (countItemsAround(MemType.A)/freeNeighbors.size()) / (KMINUS+(countItemsAround(MemType.A)/freeNeighbors.size()) ), 2);
		}
		else {
			p = Math.pow( (countItemsAround(MemType.B)/freeNeighbors.size()) / (KMINUS+(countItemsAround(MemType.B)/freeNeighbors.size()) ), 2);
		}
		if(rand < p && freeNeighbors.size() > 0) {
			int rand2 = (int) Math.floor((Math.random() * freeNeighbors.size()));
			Case dropCase = freeNeighbors.get(rand2);
			this.m_grid.m_cases[dropCase.m_x][dropCase.m_y].m_entity = this.carriedItem;
			this.carriedItem = null;
			return dropCase;
		}
		return null;
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
}
