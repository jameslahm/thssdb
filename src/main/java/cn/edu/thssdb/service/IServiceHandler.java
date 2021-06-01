package cn.edu.thssdb.service;

import cn.edu.thssdb.rpc.thrift.ConnectReq;
import cn.edu.thssdb.rpc.thrift.ConnectResp;
import cn.edu.thssdb.rpc.thrift.DisconnectReq;
import cn.edu.thssdb.rpc.thrift.DisconnectResp;
import cn.edu.thssdb.rpc.thrift.ExecuteStatementReq;
import cn.edu.thssdb.rpc.thrift.ExecuteStatementResp;
import cn.edu.thssdb.rpc.thrift.GetTimeReq;
import cn.edu.thssdb.rpc.thrift.GetTimeResp;
import cn.edu.thssdb.rpc.thrift.IService;
import cn.edu.thssdb.rpc.thrift.Status;
import cn.edu.thssdb.schema.Manager;
import cn.edu.thssdb.schema.Session;
import cn.edu.thssdb.schema.SessionManager;
import cn.edu.thssdb.statement.BaseStatement;
import cn.edu.thssdb.utils.Global;
import org.apache.thrift.TException;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.SQLEvaluator;
import cn.edu.thssdb.server.ThssDB;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IServiceHandler implements IService.Iface {

  @Override
  public GetTimeResp getTime(GetTimeReq req) throws TException {
    GetTimeResp resp = new GetTimeResp();
    resp.setTime(new Date().toString());
    resp.setStatus(new Status(Global.SUCCESS_CODE));
    return resp;
  }

  @Override
  public ConnectResp connect(ConnectReq req) throws TException {
    // TODO
    ConnectResp resp = new ConnectResp();
    resp.setSessionId(Global.SESSION_ID);
    SessionManager.getInstance().addSession(Global.SESSION_ID,"test");
    resp.setStatus(new Status(Global.SUCCESS_CODE));
    Global.SESSION_ID ++;
    return resp;
  }

  @Override
  public DisconnectResp disconnect(DisconnectReq req) throws TException {
    // TODO
    DisconnectResp resp = new DisconnectResp();
    resp.setStatus(new Status(Global.SUCCESS_CODE));

    // Persist
    ThssDB.getInstance().getManager().persist();

    return resp;
  }

  @Override
  public ExecuteStatementResp executeStatement(ExecuteStatementReq req) throws TException {
    // TODO
    ExecuteStatementResp resp = new ExecuteStatementResp();
    Session session = SessionManager.getInstance().getSessionById(req.sessionId);
    SQLEvaluator evaluator = new SQLEvaluator(ThssDB.getInstance().getManager(),session);
    ArrayList<BaseStatement> stats = evaluator.evaluate(req.statement);
    List<SQLEvalResult> results = stats.stream().map((stat)-> {
        stat.setSession(session);
        return stat.exec();
    }
    ).collect(Collectors.toList());
    SQLEvalResult result = results.get(0);
    if (result.onError()) {
        resp.setHasResult(false);
        resp.setIsAbort(true);
        Status status = new Status();
        status.setCode(-1);
        status.setMsg(result.error.getMessage());
        resp.setStatus(status);
    } else {
        resp.setHasResult(true);
        resp.setIsAbort(false);
        Status status = new Status();
        status.setCode(0);
        status.setMsg(result.message);
        resp.setStatus(status);
        if (result.queryResult != null) {
            resp.setColumnsList(result.queryResult.columnsToString());
            resp.setRowList(result.queryResult.rowsToString());
        }
    }
    return resp;
  }
}
