package com.lmm.server.lite;

public class LiteBase {

	private String uuid = null;
	private String name = null;
	private OwnableData owner = new OwnableData();

	public LiteBase( String uid ) {
		super();
		uuid = uid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getUuid() {
		return uuid;
	}


	public OwnableData getOwner() {
		return owner;
	}


	public void setOwner(OwnableData owner) {
		this.owner = owner;
	}

}