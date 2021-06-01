package cn.edu.thssdb.parser;

import cn.edu.thssdb.schema.Manager;
import cn.edu.thssdb.schema.Session;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import cn.edu.thssdb.statement.BaseStatement;

public class SQLEvaluator {
    private Manager manager;
    private Session session;

    public SQLEvaluator(Manager manager, Session session) {
        this.manager = manager;this.session = session;
    }

    public ArrayList<BaseStatement> evaluate(String stmt){
        SQLLexer lexer = new SQLLexer(CharStreams.fromString(stmt));
        SQLParser parser = new SQLParser(new CommonTokenStream(lexer));
        //
        Manager.getMutex().writeLock().lock();
        try {
            SQLCustomVisitor visitor = new SQLCustomVisitor();
            ArrayList<BaseStatement> results = (ArrayList<BaseStatement>) visitor.visitParse(parser.parse());
//            if (Manager.getMutex().isWriteLocked()) {
//                // write log
//                File logFile = new File("./" + this.session.getCurrentDatabaseName() + ".log");
//                if (!logFile.exists()) {
//                    logFile.createNewFile();
//                }
//                FileWriter writer = new FileWriter(logFile);
//                writer.append(stmt).append('\n');
//                writer.close();
//            }
            return results;
        } catch (Exception e) {
        }
        finally {
            Manager.getMutex().writeLock().unlock();
        }
        return null;
    }
}
