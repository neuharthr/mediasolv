package com.lmm.server.lite;

public class LitePlayer extends LiteBase {

	public LitePlayer( String uid ) {
		super( uid );
	}

	public LitePlayer( String uid, String name ) {
		super( uid );
		setName(name);
	}

}