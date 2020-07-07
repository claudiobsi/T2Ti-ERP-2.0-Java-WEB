package com.t2tierp.controller.lojavirtual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
 
@ManagedBean
@ApplicationScoped
public class ChatUsers implements Serializable {
     
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> users;
     
    @PostConstruct
    public void init() {
        users = new ArrayList<String>();
    }
 
    public List<String> getUsers() {
        return users;
    }
     
    public void remove(String user) {
        this.users.remove(user);
    }
     
    public void add(String user) {
        this.users.add(user);
    }
         
    public boolean contains(String user) {
        return this.users.contains(user);
    }
}