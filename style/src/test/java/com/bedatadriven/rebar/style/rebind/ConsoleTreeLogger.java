package com.bedatadriven.rebar.style.rebind;

import com.google.gwt.core.ext.TreeLogger;

public class ConsoleTreeLogger extends TreeLogger {

	private String indent;
	
	public ConsoleTreeLogger(String indent) {
		this.indent = indent;
	}
	
	public ConsoleTreeLogger() {
		this("");
	}
	
	@Override
	public TreeLogger branch(Type type, String msg, Throwable caught,
			HelpInfo helpInfo) {
		log(type, msg, caught, helpInfo);
		return new ConsoleTreeLogger(indent + "    ");
	}

	@Override
	public boolean isLoggable(Type type) {
		return true;
	}

	@Override
	public void log(Type type, String msg, Throwable caught, HelpInfo helpInfo) {
		System.err.println(indent + "[" + type.name() + "] " + msg);
		if(caught != null) {
			caught.printStackTrace();
		}
	}
}
