package com.haosimple.action;

import java.sql.SQLException;
import com.haosimple.common.action.BaseAction;
import com.haosimple.dao.UrlOperateDaoImpl;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class InsertUrlAction extends BaseAction {

	/**
	 * @param urlString
	 * @throws SQLException 
	 */
	public void insertUrl( String urlString ) throws SQLException{
		try {
			new UrlOperateDaoImpl().insertUrl( urlString );
		}
		catch ( MySQLIntegrityConstraintViolationException e ) {
			logger.debug( "url has existed" );
		}
		
	}
	
}
