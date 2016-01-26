package model;

/**
 * Created by Léo on 02-11-15.
 */
public class Case {
	int m_x;
	int m_y;
	protected String m_stringToDraw = " ";
	protected Entity m_entity = null;
	protected Grid m_grid;
	
	public Case(int x, int y, Grid grid, Entity entity) {
		m_x = x;
		m_y = y;
		m_grid = grid;
		if (entity != null) {
			m_entity = entity;
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
		if (a != null)
			m_stringToDraw = a.getString();
		else
			m_stringToDraw = "";
		m_entity = a;
	}

	public Entity getEntity() {
		return m_entity;
	}

	String getStringToDraw() {
		return m_stringToDraw;
	}

}
