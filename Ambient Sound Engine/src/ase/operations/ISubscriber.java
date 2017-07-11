package ase.operations;

public interface ISubscriber<D extends Iterable<I>, I> {
	/**
	 * This method will be called when the data structure changes, but
	 * none of the iterable data elements have.
	 * The exceptions are if the entire data structure has completely
	 * changed, or the subject indicates that an element has been removed,
	 * in which case the previous index of the removed element is passed
	 * @param data
	 * @param index The index of the element removed. If none, -1 
	 */
	public void notifySubscriber(D data, int index);
	
	/**
	 * This method is called when one of the data elements changes.
	 * It is guaranteed that if this method is called, nothing
	 * about the larger data structure has changed except the
	 * referenced element
	 * @param index
	 * @param data
	 * @param subData
	 */
	public void notifySubscriber(int index, D data, I subData);
}
