package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

/**
 * Created by Léo on 02-11-15. Grille
 */
public class Grid {
	public final int M_SIZEX = 10; // 50
	public final int M_SIZEY = 10; // 50
	final int M_NBAGENTS = 20; // 20
	final int M_NB_A = 20; // 200
	final int M_NB_B = 20; // 200
	List<Agent> m_lAgents;
	List<ItemA> m_lItemsA;
	List<ItemB> m_lItemsB;
	public Case[][] m_cases = new Case[M_SIZEX][M_SIZEY];
	private static Grid m_instance = null;
	private final Object m_sem = new Object();

	private Grid() {
		m_instance = this;
		initGrid();
	}
        
        int nbCarriedItem(){
            int n=0;
            for(Entity e :m_lItemsA){
                if(e.m_currentCase==null){
                    n++;
                }
            }
            for(Entity e :m_lItemsB){
                if(e.m_currentCase==null){
                    n++;
                }
            }
            return n;
        }
        
        int nbItemsOnGrid(){
            int n=0;
            for (int i = 0; i < M_SIZEX; i++) {
			for (int j = 0; j < M_SIZEY; j++) {
                            
				if(this.getCases()[i][j].getItem()!=null){
                                    n++;
                                } 
			}
		}
            return n;
        }
	/**
	 * singleton
	 * 
	 * @return
	 */
	public static Grid getInstance() {
		if (m_instance == null) {
			m_instance = new Grid();
		}
		return m_instance;
	}
	
	/**
	 * Mise en route de tout les agents
	 */
	public void startAgents() {
		for (Agent a : this.m_lAgents) {
			Thread t = new Thread(a);
			t.start();
		}
	}

	/**
	 * Ajout d'un agent
	 * 
	 * @param s
	 *            lettre de l'agent
	 * @return l'agent
	 * @throws Exception
	 */
	public Case getRandomCase() {
		if (m_lAgents.size() + m_lItemsA.size() + m_lItemsB.size() < M_SIZEX
				* M_SIZEY - 1) {
			Random rnd = new Random();
			int x, y, rx, ry;
			do {
				x = rnd.nextInt(M_SIZEX);
				y = rnd.nextInt(M_SIZEY);
			} while (m_cases[x][y].getAgent() != null);

			return new Case(x, y, this);
		} else {
			return null;
		}
	}

	public Agent addAgent() {
		Agent a = new Agent(getRandomCase());
		m_lAgents.add(a);
		m_cases[a.m_currentCase.m_x][a.m_currentCase.m_y].setAgent(a);
		return a;
	}

	public ItemA addItemA() {
		ItemA a = new ItemA(getRandomCase());
		m_lItemsA.add(a);
		m_cases[a.m_currentCase.m_x][a.m_currentCase.m_y].setItem(a);
		return a;
	}

	public ItemB addItemB() {
		ItemB a = new ItemB(getRandomCase());
		m_lItemsB.add(a);
		m_cases[a.m_currentCase.m_x][a.m_currentCase.m_y].setItem(a);
		return a;
	}

	public Case[][] getCases() {
		return m_cases;
	}

	public void initGrid() {
		m_lAgents = new LinkedList<Agent>();
		m_lItemsA = new LinkedList<ItemA>();
		m_lItemsB = new LinkedList<ItemB>();
		for (int i = 0; i < M_SIZEX; i++) {
			for (int j = 0; j < M_SIZEY; j++) {
				this.getCases()[i][j] = new Case(i, j, this);
			}
		}
		for (int i = 0; i < M_NBAGENTS; i++) {
			addAgent();
		}
		for (int i = 0; i < M_NB_A; i++) {
			addItemA();
		}
		for (int i = 0; i < M_NB_B; i++) {
			addItemB();
		}
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < M_SIZEY; i++) {
			for (int j = 0; j < M_SIZEX; j++) {
				s += "|";
				if (m_cases[j][i] != null
						&& m_cases[j][i].getStringToDraw() != null) {
					s += m_cases[j][i].getStringToDraw();
				} else {
					s += " ";
				}
			}
			s += "|\n";
		}
		return s;
	}

	/**
	 * @return the m_sem
	 */
	public Object getSem() {
		return m_sem;
	}
}