package cn.edu.thssdb.server;

import cn.edu.thssdb.rpc.thrift.IService;
import cn.edu.thssdb.schema.Manager;
import cn.edu.thssdb.service.IServiceHandler;
import cn.edu.thssdb.utils.Global;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.thssdb.parser.SQLEvaluator;
import cn.edu.thssdb.utils.Global;
import org.apache.thrift.server.TThreadPoolServer;

public class ThssDB {

  private static final Logger logger = LoggerFactory.getLogger(ThssDB.class);

  private static IServiceHandler handler;
  private static IService.Processor processor;
  private static TServerSocket transport;
  private static TServer server;

  private Manager manager;


  public Manager getManager() {
    return manager;
  }

  public void setManager(Manager manager) {
    this.manager = manager;
  }

  public ThssDB() {
    manager = Manager.getInstance();
    manager.init();
  }

  public static ThssDB getInstance() {
    return ThssDBHolder.INSTANCE;
  }

  public static void main(String[] args) {
    ThssDB server = ThssDB.getInstance();
    server.start();
  }

  private void start() {
    handler = new IServiceHandler();
    processor = new IService.Processor(handler);
    Runnable setup = () -> setUp(Global.DEFAULT_SERVER_PORT,processor);
    new Thread(setup).start();

    handler = new IServiceHandler();
    processor = new IService.Processor(handler);
    Runnable setup2 = ()-> setUp2(Global.DEFAULT_SERVERR_PORT2,processor);

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    new Thread(setup2).start();
  }

  private static void setUp(int port,IService.Processor processor) {
    try {
      transport = new TServerSocket(port);
      server = new TSimpleServer(new TServer.Args(transport).processor(processor));
      logger.info("Starting ThssDB ...");
      server.serve();
    } catch (TTransportException e) {
      logger.error(e.getMessage());
    }
  }

  private static void setUp2(int port,IService.Processor processor) {
    try {
      transport = new TServerSocket(port);
      server = new TSimpleServer(new TServer.Args(transport).processor(processor));
      logger.info("Starting ThssDB ...");
      server.serve();
    } catch (TTransportException e) {
      logger.error(e.getMessage());
    }
  }

  private static class ThssDBHolder {
    private static final ThssDB INSTANCE = new ThssDB();
    private ThssDBHolder() {

    }
  }
}
