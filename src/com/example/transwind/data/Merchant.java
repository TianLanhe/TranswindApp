package com.example.transwind.data;

public class Merchant extends User {

	public String origination;
	
	@Override
	public int getType() {
		return MERCHANT;
	}

}
