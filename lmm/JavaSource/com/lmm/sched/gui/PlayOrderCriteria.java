package com.lmm.sched.gui;

import java.util.HashSet;
import java.util.Set;

public class PlayOrderCriteria {
	
	private int minOrder = 1;
	private int maxOrder = Integer.MAX_VALUE;
	private Set<Integer> invalidOrders = new HashSet<Integer>(16);
	
	
	public PlayOrderCriteria() {
		super();
	}

	public PlayOrderCriteria(int maxOrder, Set<Integer> invalidOrders) {
		super();
		this.maxOrder = maxOrder;
		this.invalidOrders = invalidOrders;
	}

	public Set<Integer> getInvalidOrders() {
		return invalidOrders;
	}

	public int getMaxOrder() {
		return maxOrder;
	}

	public int getMinOrder() {
		return minOrder;
	}


}
