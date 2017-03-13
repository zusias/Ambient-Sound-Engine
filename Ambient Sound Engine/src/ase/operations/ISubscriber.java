package ase.operations;

public interface ISubscriber<D> {
	public void notify(D data);
}
