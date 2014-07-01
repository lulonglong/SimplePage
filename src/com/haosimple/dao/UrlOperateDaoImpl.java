package com.haosimple.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.haosimple.common.dao.BaseDao;
import com.haosimple.common.util.DBUtil;
import com.haosimple.common.util.Define;
import com.haosimple.common.util.StringUtil;

public class UrlOperateDaoImpl extends BaseDao {

	private final static String INSERT_URL_SQL = "insert tb_url(url) values(?)";
	private final static String OPERATE_URL_SQL = "insert tb_url_operate(url,type,useragent,userip) values(?,?,?,?)";
	private final static String INSERT_OPERATE_URL_HISTORY_SQL = "insert tb_url_operate_history(url,type,useragent,userip,createTime)  select url,type,useragent,userip,createTime  from tb_url_operate  where createTime<=? order by createTime limit ?;";
	private final static String DELETE_OPERATE_URL_SQL = "delete from tb_url_operate where createTime<=? order by createTime LIMIT ?;";

	// 新增url
	public void insertUrl( String url ) throws SQLException {
		Connection connection = DBUtil.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement( INSERT_URL_SQL );
		preparedStatement.setString( 1, url );
		preparedStatement.execute();
		closeConnection( connection );
	}

	// 添加增加url的记录
	public void addUrlOperate( String url, String userAgent, String userIp ) throws SQLException {
		urlOperate( url, userAgent, userIp, Define.OPERATE_ADD_TYPE );
	}

	// 添加删除url的记录
	public void deleteUrlOperate( String url, String userAgent, String userIp ) throws SQLException {
		urlOperate( url, userAgent, userIp, Define.OPERATE_DELETE_TYPE );
	}

	// 转移数据
	public int moveData( Timestamp date, int singleMaxCount ) throws SQLException {
		Connection conn = DBUtil.getConnection();
		conn.setAutoCommit( false );
		int movedCount = 0;
		try {
			while ( true ) {
				// insert data
				int insertCount = insertData( conn, date, singleMaxCount );
				logger.info( "insert count:" + insertCount );
				if ( insertCount <= 0 )
					break;

				// delete data
				int deleteCount = deleteData( conn, date, singleMaxCount );
				logger.info( "delete count:" + deleteCount );

				if ( insertCount == deleteCount ) {
					conn.commit();
					movedCount += insertCount;
				}
				else {
					conn.rollback();
					logger.error( "delete count is not equal insert count" );
					break;
				}
			}
		}
		catch ( SQLException e ) {
			logger.error( "moveData failed--" + StringUtil.getExceptionStack( e ) );
			conn.rollback();
		}
		finally {
			closeConnection( conn );
		}
		logger.info( "moved count " + movedCount );
		return movedCount;
	}

	// 将数据插入备份表
	private int insertData( Connection conn, Timestamp date, int maxCount ) throws SQLException {
		PreparedStatement statement = conn.prepareStatement( INSERT_OPERATE_URL_HISTORY_SQL );
		statement.setTimestamp( 1, date );
		statement.setInt( 2, maxCount );

		return statement.executeUpdate();
	}

	// 删除已备份数据
	private int deleteData( Connection conn, Timestamp date, int maxCount ) throws SQLException {
		PreparedStatement statement = conn.prepareStatement( DELETE_OPERATE_URL_SQL );
		statement.setTimestamp( 1, date );
		statement.setInt( 2, maxCount );
		return statement.executeUpdate();
	}

	private void urlOperate( String url, String userAgent, String userIp, int type ) throws SQLException {
		Connection connection = DBUtil.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement( OPERATE_URL_SQL );
		preparedStatement.setString( 1, url );
		preparedStatement.setInt( 2, type );
		preparedStatement.setString( 3, userAgent );
		preparedStatement.setString( 4, userIp );
		preparedStatement.execute();
		closeConnection( connection );
	}

//	private Connection getConnection() throws SQLException {
//		String url = Configuration.getValueByKey( "dbconnection" );// "jdbc:mysql://w.rdc.sae.sina.com.cn:3307/app_haosimple";
//		String username = Configuration.getValueByKey( "dbusername" ) == null ? SaeUserInfo.getAccessKey()
//				: Configuration.getValueByKey( "dbusername" );// SaeUserInfo.getAccessKey();
//		String password = Configuration.getValueByKey( "dbpassword" ) == null ? SaeUserInfo.getSecretKey()
//				: Configuration.getValueByKey( "dbpassword" );// SaeUserInfo.getSecretKey();
//		logger.info( "url: "+url+" username: "+username+" pass "+password );
//		Connection conn = DriverManager.getConnection( url, username, password );
//		return conn;
//	}

	private void closeConnection( Connection conn ) {
		try {
			conn.close();
		}
		catch ( SQLException e ) {
			logger.error( "con close failed" );
		}
	}
}
