/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.LinkedList;

/**
 *
 * @author Léo
 */
public abstract class Entity {
     protected Case m_currentCase;
     protected Grid m_grid;
     protected String m_string;
     

    public Entity(Case c){
        this.m_currentCase = c;
        this.m_grid = this.m_currentCase.m_grid;
    }
    
    public String getString() {
        return m_string;
    } 
    
  
}
