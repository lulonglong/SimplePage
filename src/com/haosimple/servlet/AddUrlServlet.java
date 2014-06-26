package com.haosimple.servlet;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.haosimple.action.AddUrlAction;
import com.haosimple.action.InsertUrlAction;
import com.haosimple.common.entity.vo.CommonResultVO;
import com.haosimple.common.servlet.BaseServlet;
import com.haosimple.common.util.StringUtil;

/**
 * Servlet implementation class AddUrlServlet
 */

public class AddUrlServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	public String execute( HttpServletRequest req, HttpServletResponse res ) throws IOException {
		CommonResultVO vo=new CommonResultVO();
		String urlString=req.getParameter( "url" );
		
		if(StringUtil.isNullOrWhiteSpace( urlString )){
			vo.setErrorCode( "010001" );
			return vo.toJsonString();
		}
		
		try {
			new InsertUrlAction().insertUrl(urlString);
			new AddUrlAction().addUrl(urlString,req.getHeader( "user-agent" ),req.getRemoteAddr());
		}
		catch ( SQLException e ) {
			logger.error( StringUtil.getExceptionStack( e ) );
		}
		catch (Exception e) {
			logger.error( StringUtil.getExceptionStack( e ) );
		}
		
		logger.info( vo.toJsonString() );
		return vo.toJsonString();
	}

}
