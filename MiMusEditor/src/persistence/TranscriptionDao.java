package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Transcription;

public class TranscriptionDao extends UnitDao<Transcription> {

	public TranscriptionDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Transcription unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(Transcription unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub

	}

	@Override
	protected Transcription make(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTable() {
		return "Transcription";
	}

}
