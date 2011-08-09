package com.bedatadriven.rebar.sql.server.jdbc;

import com.google.gwt.core.client.Scheduler;

class SyncScheduler extends Scheduler {

  @Override
  public void scheduleDeferred(ScheduledCommand scheduledCommand) {
    scheduledCommand.execute();
  }

  @Override
  public void scheduleEntry(RepeatingCommand repeatingCommand) {
    while(repeatingCommand.execute()) {}
  }

  @Override
  public void scheduleEntry(ScheduledCommand scheduledCommand) {
    scheduledCommand.execute();
  }

  @Override
  public void scheduleFinally(RepeatingCommand repeatingCommand) {
    while(repeatingCommand.execute()) {}
  }

  @Override
  public void scheduleFinally(ScheduledCommand scheduledCommand) {
    scheduledCommand.execute();
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
}
