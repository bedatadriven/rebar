package com.bedatadriven.rebar.persistence.client;

import javax.persistence.EntityManagerFactory;

public interface PersistenceUnit {
	
	 EntityManagerFactory createEntityManagerFactory(ConnectionProvider connectionProvider);
	 
}
