package ase.database;

public abstract class Database implements IDatabase {
	
	/**
	 * No Argument constructor that invokes the implemented Connect method
	 * @throws DatabaseException
	 */
	public Database() throws DatabaseException {
		this.connect();
	}
}
