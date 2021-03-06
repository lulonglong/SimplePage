package com.haosimple.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.haosimple.action.MoveUrlOperateAction;
import com.haosimple.common.entity.vo.CommonResultVO;
import com.haosimple.common.servlet.BaseServlet;
import com.haosimple.common.util.Configuration;
import com.haosimple.common.util.StringUtil;

/**
 * Servlet implementation class AddUrlServlet
 */

public class MoveUrlOperateServlet extends BaseServlet {

	/** */
	private static final long serialVersionUID = 1515204880936343477L;
	private MoveUrlOperateAction moveUrlOperateAction = new MoveUrlOperateAction();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		super.doPost(req, res);
	}

	@Override
	public String execute(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		CommonResultVO vo = new CommonResultVO();
		Timestamp timestamp = new Timestamp(new Date().getTime());

		try {
			moveUrlOperateAction.move(timestamp, Integer.valueOf(Configuration
					.getValueByKey("moveSingleMaxCount")));
		} catch (SQLException e) {
			logger.error(StringUtil.getExceptionStack(e));
			vo.setErrorCode("030001");
		} catch (Exception e) {
			logger.error(StringUtil.getExceptionStack(e));
			vo.setErrorCode("030002");
		}

		return vo.toJsonString();
	}

}
