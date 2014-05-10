package com.revo21.gotguide.utils;
public interface Callback {
	public interface AlertCallback {
		public void onPressButton();
	}
	
	public interface NetworkCallback {
		public void onSuccess(String content);
		public void onFailure();
	}
	
	public interface JsonResponseCallback {
		public void onSuccess(String content);
	}
	
}

