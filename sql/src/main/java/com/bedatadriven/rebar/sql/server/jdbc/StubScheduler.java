package com.bedatadriven.rebar.sql.server.jdbc;

import java.util.ArrayDeque;
import java.util.Queue;

import com.google.gwt.core.client.Scheduler;

class StubScheduler extends Scheduler {

	public Queue<ScheduledCommand> queue = new ArrayDeque<ScheduledCommand>();

	public static final StubScheduler INSTANCE = new StubScheduler();
	
	private StubScheduler() {
		
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
  	while(!queue.isEmpty()) {
  		queue.poll().execute();
  	}
  }
}
