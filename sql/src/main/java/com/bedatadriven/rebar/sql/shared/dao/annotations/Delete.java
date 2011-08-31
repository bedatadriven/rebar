package com.bedatadriven.rebar.sql.shared.dao.annotations;

public @interface Delete {
	Class from();
	String where();
}
