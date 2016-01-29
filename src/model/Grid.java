package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

/**
 * Created by Léo on 02-11-15. Grille
 */
public class Grid {
	public final int M_SIZEX = 10; //50
	public final int M_SIZEY = 10; //50
	final int M_NBAGENTS = 8; //20
	final int M_NB_A = 10; //200
	final int M_NB_B = 10; //20
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

	/**
	 * singleton
	 * 
	 * @return
	 */
	public static Grid getInstance() {
		if (m_instance == null) {
			m_instance = new Grid();
			// System.out.println("newgrid !");
		}
		return m_instance;
	}

	/**
	 * bouge l'agent situé à la première case vers la deuxième case
	 * 
	 * @param c1
	 *            1ère case
	 * @param c2
	 *            2ème case
	 */
	/*
	 * public void moveAgent(Case c1, Case c2){ synchronized(m_sem){
	 * if(c1.getAgent()!=null && c2.getAgent()==null){
	 * //if(c1.getAgent()!=null){ c1.getAgent().setCurrentCase(c2);
	 * m_cases[c2.m_x][c2.m_y].setAgent(c1.getAgent());
	 * m_cases[c1.m_x][c1.m_y].setAgent(null); } //} }
	 * 
	 * 
	 * }
	 */
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
			} while (m_cases[x][y].getAgent()!= null);

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

	/**
	 * Retourne les cases voisines occupées ou les cases voisines libres autour
	 * de la case
	 * 
	 * @param x
	 *            position sur l'axe x de la case
	 * @param y
	 *            position sur l'axe y de la case
	 * @param free
	 *            true : cases occupées, false cases libres
	 * @return
	 */
        /*
	public List<Case> getFreeNeighbors(int x, int y, boolean free) {
		Case[][] cases = m_cases;
		List<Case> emptyNeighb = new LinkedList<>();
		if (x > 0) {
                    System.out.println("x :"+x);
                        Entity ent=cases[x - 1][y].getEntity();
			if ((ent == null || ent.getClass()!=Agent.class) == free) {
				emptyNeighb.add(cases[x - 1][y]);
			}
		}
		if (x < this.M_SIZEX - 1) {
                     Entity ent=cases[x + 1][y].getEntity();
			if ((ent == null || ent.getClass()!=Agent.class) == free) {
				emptyNeighb.add(cases[x + 1][y]);
			}
		}
		if (y > 0) {
                     Entity ent=cases[x][y - 1].getEntity();
			if ((ent == null || ent.getClass()!=Agent.class) == free) {
				emptyNeighb.add(cases[x][y - 1]);
			}
		}
		if (y < this.M_SIZEY - 1) {
                     Entity ent=cases[x][y + 1].getEntity();
			if ((ent == null || ent.getClass()!=Agent.class) == free) {
				emptyNeighb.add(cases[x][y + 1]);
			}
		}
		return emptyNeighb;
	}
*/
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
				if (m_cases[j][i] != null && m_cases[j][i].getStringToDraw()!=null) {
					s += m_cases[j][i].getStringToDraw();
				} else {
					s += " ";
				}
			}
			s += "|\n";
		}
		s += m_lAgents.size() + "\n";
		s += m_lItemsA.size() + "\n";
		s += m_lItemsB.size() + "\n";

		return s;
	}

    /**
     * @return the m_sem
     */
    public Object getSem() {
        return m_sem;
    }
}