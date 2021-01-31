package net.lintford.library.core.helpers;

import java.io.Serializable;

public class Pair<S, T> implements Serializable {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 4699186013704689310L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private S mComponentA;
	private T mComponentB;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public S a() {
		return mComponentA;

	}

	public void a(S pComponentA) {
		mComponentA = pComponentA;

	}

	public T b() {
		return mComponentB;

	}

	public void b(T pComponentB) {
		mComponentB = pComponentB;

	}

	@Override
	public String toString() {
		return "(" + mComponentA.toString() + ", " + mComponentB.toString() + ")";
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Pair)) {
			return false;
		}

		return ((Pair<?, ?>) o).a().equals(this.mComponentA) && ((Pair<?, ?>) o).b().equals(this.mComponentB);
	}

}
