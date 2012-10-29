package com.bedatadriven.rebar.sql.server.jdbc;

import java.util.ArrayDeque;
import java.util.Queue;

import com.google.gwt.core.client.Scheduler;

/**
 * 
 * Server-side scheduler that simulates the asyc-style event loop.
 * 
 * @author alex
 *
 */
public class JdbcScheduler extends Scheduler {

	
	private static ThreadLocal<JdbcScheduler> THREAD_LOCAL = new ThreadLocal<JdbcScheduler>();
	
	private Queue<ScheduledCommand> queue = new ArrayDeque<ScheduledCommand>();
	private boolean running = false;
	private boolean allowNestedProcessing = false;
	
	public JdbcScheduler() {
		
	}
	
	
	public void allowNestedProcessing() {
		this.allowNestedProcessing = true;
	}
	
	public static JdbcScheduler get() {
		JdbcScheduler instance = THREAD_LOCAL.get();
		if(instance == null) {
			instance = new JdbcScheduler();
			THREAD_LOCAL.set(instance);
		}
		return instance;
	}
	
  @Override
  public void scheduleDeferred(ScheduledCommand scheduledCommand) {
  	queue.add(scheduledCommand);
  }

  @Override
  public void scheduleEntry(RepeatingCommand repeatingCommand) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void scheduleEntry(ScheduledCommand scheduledCommand) {
  	queue.add(scheduledCommand);
  }

  @Override
  public void scheduleFinally(RepeatingCommand repeatingCommand) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void scheduleFinally(ScheduledCommand scheduledCommand) {
  	queue.add(scheduledCommand);
  }

  @Override
  public void scheduleFixedDelay(RepeatingCommand repeatingCommand, int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void scheduleFixedPeriod(RepeatingCommand repeatingCommand, int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void scheduleIncremental(RepeatingCommand repeatingCommand) {
    throw new UnsupportedOperationException();
  }
  
  public void process() {
  	if(!running || allowNestedProcessing) {
  		running = true;
	  	
	  	while(!queue.isEmpty()) {
	  		ScheduledCommand command = null;
	  		try{
	  			command = queue.poll();
				command.execute();
	  		} catch (Exception ex) {
	  			throw new RuntimeException("Unhandled exception while executing queue (item = " + 
	  					(command == null ? "null" : command.getClass()), ex);
	  		}
	  		
	  	}
	  	
	  	running = false;
  	}
  }
  
  /**
   * Removes all pending tasks from the task queue.
   * Should only be called from tests to assure clean isolation from other tests
   */
  public void forceCleanup() {
  	queue.clear();
  	running = false;
  	
  }

public void assertAllFinished() {
	if (!queue.isEmpty()) {
		throw new AssertionError("Expected empty queue");
	}
}
}
