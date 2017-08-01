package ase.operations;

public interface ISubscriber<D> {
	public void notifySubscriber(D data);
}
