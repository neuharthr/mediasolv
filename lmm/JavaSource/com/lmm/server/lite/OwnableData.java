package com.lmm.server.lite;

/**
 * Instances of this class give ownership to pieces of data in the system. Each piece of data may be composed
 * of a single instance of this class.
 * 
 */
public class OwnableData {

	private String ownerCode = "LMM";	//primary key
	private String owner = "Last Mile";


	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
}