package model;

/**
 * Created by Léo on 02-11-15.
 */
public class Case {
	int m_x;
	int m_y;
	protected String m_stringToDraw = " ";
	protected Entity m_agent = null;
	protected Entity m_item = null;
	protected Grid m_grid;

	public Case(int x, int y, Grid grid, Entity entity) {
		m_x = x;
		m_y = y;
		m_grid = grid;
		if (entity != null) {
			m_agent = entity;
			m_stringToDraw = entity.getString();
		}
	}

	public Case(int x, int y, Grid grid) {
		this(x, y, grid, null);
	}

	public Case(Case c, Entity entity) {
		this(c.m_x, c.m_y, c.m_grid, entity);
	}

	public void setEntity(Entity a) {
		if (a.getClass() == Agent.class) {
			setAgent((Agent) a);
		} else {
			setItem(a);
		}

	}

	public void setAgent(Agent a) {
		m_agent = a;
		refreshString();
	}

	public String setItem(Entity e) {
            if(e!=null){
                e.setCase(this);
            }
            else{
                this.m_item.setCase(null);
            }
            m_item = e;
            return refreshString();
	}

	public String refreshString() {
		if (m_agent != null && m_item != null) {
			m_stringToDraw = "Q";
		} else {
			if (m_agent != null) {
				m_stringToDraw = m_agent.getString();
			} else if (m_item != null){
				m_stringToDraw = m_item.getString();
                        }
			else {
                           // System.out.println("tonull");
				m_stringToDraw = " ";
			}
		}
                return m_stringToDraw;
	}

	public Entity getItem() {
		return m_item;
	}

	public Entity getAgent() {
		return m_agent;
	}

	String getStringToDraw() {
		return m_stringToDraw;
	}

}
