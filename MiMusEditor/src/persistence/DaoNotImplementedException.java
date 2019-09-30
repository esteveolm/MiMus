package persistence;

/**
 * Exception thrown when attempting to perform a SQL statement
 * that is not implemented in the DAO.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class DaoNotImplementedException extends Exception {

	private static final long serialVersionUID = 5041595206530703978L;

	public DaoNotImplementedException() {}

}
