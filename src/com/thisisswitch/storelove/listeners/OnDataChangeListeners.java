package com.thisisswitch.storelove.listeners;

public interface OnDataChangeListeners {

	public void onLoadingStarted(String type);
	public void onLoadingFinished(String response,boolean isDone);
	
}
