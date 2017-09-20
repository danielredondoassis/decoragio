package com.assis.redondo.daniel.appdoikeda.utils.dispatcher;

public interface Event {
	
	public String getType();
	public Object getSource();
	public void setSource(Object source);
}
