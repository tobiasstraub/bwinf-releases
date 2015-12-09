package org.bytesector.bwinf.a1v2;

import java.util.List;

public class Step {
	
	// Eigenschaften Quellgefäß (=src) und Zielgefäß (=dest), sowie eine Sammlung an Gefäßen mit zur Zeit nach dem Umfüllprozess akutellen Füllständen (=after)
	private Container src, dest;
	private List<Container> after;
	
	public Step(Container src, Container dest, List<Container> after){
		// Konstruktorparameter auf Eigenschaten übertragen
		this.src = src;
		this.dest = dest;
		this.after = after;
	}

	public Container getSrc() {
		return src;
	}

	public Container getDest() {
		return dest;
	}
	
	public List<Container> getAfter() {
		return after;
	}
}
