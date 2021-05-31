package cn.edu.thssdb.parser;

import cn.edu.thssdb.parser.items.Comparer;
import cn.edu.thssdb.parser.items.Condition;
import cn.edu.thssdb.parser.items.TableQuery;
import cn.edu.thssdb.parser.items.ValueEntry;
import javafx.scene.control.Tab;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import java.util.ArrayList;
import java.util.List;
import cn.edu.thssdb.statement.*;
import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.type.ColumnType;
import cn.edu.thssdb.utils.Pair;
import sun.swing.MenuItemLayoutHelper;

public class SQLCustomVisitor extends SQLBaseVisitor {
    @Override
    public Object visitParse(SQLParser.ParseContext ctx){
        return visit(ctx.sql_stmt_list());
    }
    @Override
    public Object visitSql_stmt_list(SQLParser.Sql_stmt_listContext ctx){
        ArrayList<BaseStatement> results = new ArrayList<>();
        List<SQLParser.Sql_stmtContext> sql_stmts = ctx.sql_stmt();
        for(SQLParser.Sql_stmtContext stmt:sql_stmts){
            results.add((BaseStatement) visit(stmt));
        }
        return results;
    }
    @Override
    public Object visitSql_stmt(SQLParser.Sql_stmtContext ctx){
        return visit(ctx.getChild(0));
    }

    @Override
    public Object visitCreate_db_stmt(SQLParser.Create_db_stmtContext ctx){
        String db_name = (String) visit(ctx.getChild(2));
        return new CreateDatabaseStatement(db_name);
    }

    @Override
    public Object visitDrop_db_stmt(SQLParser.Drop_db_stmtContext ctx){
        String db_name;
        if (ctx.getChildCount() == 3){
            db_name = (String) visit(ctx.getChild(2));
        }
        else{
            db_name = (String) visit(ctx.getChild(4));
        }
        return new DropDatabaseStatement(db_name);
    }

    @Override
    public Object visitCreate_user_stmt(SQLParser.Create_user_stmtContext ctx){
        String username = (String) visit(ctx.getChild(2));
        String password = (String) visit(ctx.getChild(5));
        password = password.substring(1,password.length()-1);
        return new CreateUserStatement(username,password);
    }

    @Override
    public Object visitDrop_user_stmt(SQLParser.Drop_user_stmtContext ctx){
        String username;
        if (ctx.getChildCount()==3){
            username = (String) visit(ctx.getChild(2));
        }
        else{
            username = (String) visit(ctx.getChild(4));
        }
        return new DropUserStatement(username);
    }

    @Override
    public Object visitCreate_table_stmt(SQLParser.Create_table_stmtContext ctx){
        String table_name = (String) visit(ctx.getChild(2));
        List<SQLParser.Column_defContext> column_defs = ctx.column_def();
        ArrayList<String> primary_keys = new ArrayList<>();
        if(ctx.table_constraint()!= null){
            primary_keys.addAll((ArrayList<String>) visit(ctx.table_constraint()));
        }
        ArrayList<Column> temp_columns = new ArrayList<>();
        for(int i = 0;i<column_defs.size();i++){
            temp_columns.add((Column)visit(column_defs.get(i)));
        }
        Column[] columns = new Column[temp_columns.size()];
        for (Column column:temp_columns){
            for (String primary_key:primary_keys){
                if(column.getName().equalsIgnoreCase(primary_key)){
                    column.setPrimary(true);
                }
            }
        }
        for (int i = 0;i<column_defs.size();i++){
            columns[i] = temp_columns.get(i);
        }
        return new CreateTableStatement(table_name,columns);
    }
    public Object visitShow_meta_stmt(SQLParser.Show_meta_stmtContext ctx){
        String table_name = (String) visit(ctx.getChild(2));
        return new ShowTableStatement(table_name);
    }

    // TODO
    // grant,revoke

    @Override
    public Object visitUse_db_stmt(SQLParser.Use_db_stmtContext ctx){
        String db_name = (String) visit(ctx.getChild(1));
        return new UseDatabaseStatement(db_name);
    }

    @Override
    public Object visitDelete_stmt(SQLParser.Delete_stmtContext ctx){
        String table_name = (String) visit(ctx.getChild(2));
        if(ctx.getChildCount() == 3){
            Condition cond = (Condition) visit(ctx.getChild(2));
            return new DeleteStatement(table_name,cond);
        }
        else{
            return new DeleteStatement(table_name);
        }
    }

    @Override
    public Object visitDrop_table_stmt(SQLParser.Drop_table_stmtContext ctx){
        String table_name;
        if (ctx.getChildCount() ==3){
            table_name = (String) visit(ctx.getChild(2));
        }
        else{
            table_name = (String) visit(ctx.getChild(4));
        }
        return new DropTableStatement(table_name);
    }

    @Override
    public Object visitShow_db_stmt(SQLParser.Show_db_stmtContext ctx){
        return new ShowDatabaseStatement();
    }

    @Override
    public Object visitQuit_stmt(SQLParser.Quit_stmtContext ctx){
        return new QuitStatement();
    }

    @Override
    public Object visitShow_table_stmt(SQLParser.Show_table_stmtContext ctx){
        return new ShowTablesStatement();
    }

    @Override
    public Object visitInsert_stmt(SQLParser.Insert_stmtContext ctx){
        String table_name = (String) visit(ctx.getChild(2));
        List<SQLParser.Value_entryContext> value_ctxs = ctx.value_entry();
        ArrayList<ValueEntry> values = new ArrayList<>();
        for (SQLParser.Value_entryContext v_ctx:value_ctxs){
            values.add((ValueEntry) visit(v_ctx));
        }
        List<SQLParser.Column_nameContext> column_ctxs = ctx.column_name();
        if (column_ctxs != null){
            ArrayList<String> column_names = new ArrayList<>();
            for (SQLParser.Column_nameContext c_ctx: column_ctxs){
                column_names.add((String) visit(c_ctx));
            }
            return new InsertStatement(table_name,column_names,values);
        }
        else{
            return new InsertStatement(table_name,values);
        }
    }

    @Override
    public Object visitValue_entry(SQLParser.Value_entryContext ctx){
        List<SQLParser.Literal_valueContext> literal_ctxs = ctx.literal_value();
        ArrayList<String> values = new ArrayList<>();
        for (SQLParser.Literal_valueContext l_ctx:literal_ctxs){
            values.add(((Pair<String,Boolean>)visit(l_ctx)).left);
        }
        return new ValueEntry(values);
    }

    @Override
    public Object visitSelect_stmt(SQLParser.Select_stmtContext ctx) {
        List<SQLParser.Result_columnContext> result_ctxs = ctx.result_column();
        List<SQLParser.Table_queryContext> query_ctxs = ctx.table_query();
        ArrayList<String> result_columns = new ArrayList<>();
        for (SQLParser.Result_columnContext r_ctx : result_ctxs) {
            result_columns.add((String) visit(r_ctx));
        }
        ArrayList<TableQuery> table_queries = new ArrayList<>();
        for (SQLParser.Table_queryContext q_ctx : query_ctxs) {
            table_queries.add((TableQuery) visit(q_ctx));
        }
        SQLParser.Multiple_conditionContext cond_ctx = ctx.multiple_condition();
        if (cond_ctx != null) {
            Condition condition = (Condition) visit(cond_ctx);
            return new SelectStatement(result_columns, table_queries, condition);
        } else {
            return new SelectStatement(result_columns, table_queries);
        }
    }

    //TODO
    //create_view,drop_view

    @Override
    public Object visitUpdate_stmt(SQLParser.Update_stmtContext ctx){
        String table_name = (String) visit(ctx.table_name());
        String column_name = (String) visit(ctx.column_name());
        Comparer value = (Comparer) visit(ctx.expression());
        Condition condition = (Condition) visit(ctx.multiple_condition());

        return new UpdateStatement(table_name,column_name,value,condition);
    }

    @Override
    public Object visitColumn_def(SQLParser.Column_defContext ctx){
        String name = (String) visit(ctx.column_name());

        Pair<ColumnType,Integer> p = (Pair<ColumnType, Integer>) visit(ctx.type_name());
        List<SQLParser.Column_constraintContext> constraint_ctxs = ctx.column_constraint();
        boolean primary = false;
        boolean not_null = false;
        for(SQLParser.Column_constraintContext c_ctx:constraint_ctxs){
            String constraint = (String) visit(c_ctx);
            if (constraint.equalsIgnoreCase("NOT NULL")){
                not_null = true;
            }
            else if (constraint.equalsIgnoreCase("PRIMARY KEY")){
                primary = true;
            }
        }

        return new Column(name,p.left,primary,not_null,p.right);
    }
    @Override
    public Object visitType_name(SQLParser.Type_nameContext ctx){
        ColumnType type = ColumnType.fromString(ctx.getChild(0).getText());
        Integer max_len = 0;
        if(ctx.getChildCount() >1){
            max_len = Integer.parseInt(ctx.getChild(2).getText());
        }
        return new Pair<ColumnType,Integer>(type,max_len);
    }

    @Override
    public Object visitColumn_constraint(SQLParser.Column_constraintContext ctx){
        return ctx.getText();
    }

    @Override
    public Object visitMultiple_condition(SQLParser.Multiple_conditionContext ctx){
        if (ctx.getChildCount() == 1){
            return visit(ctx.getChild(0));
        }
        else{
            Condition left_condition = (Condition) visit(ctx.getChild(0));
            Condition right_condition = (Condition) visit(ctx.getChild(2));
            String logic_op = (String) ctx.getChild(1).getText();
            return new Condition(left_condition,right_condition,logic_op,null,null,null);
        }
    }

    @Override
    public Object visitCondition(SQLParser.ConditionContext ctx){
        Comparer left_comparer = (Comparer) visit(ctx.getChild(0));
        Comparer right_comparer = (Comparer) visit(ctx.getChild(2));
        String comparator = (String) visit(ctx.getChild(1));
        return new Condition(null,null,null,left_comparer,right_comparer,comparator);
    }

    @Override
    public Object visitComparer(SQLParser.ComparerContext ctx){
        SQLParser.Column_full_nameContext column_full_name = ctx.column_full_name();
        if(column_full_name != null){
            return new Comparer((String) visit(ctx.getChild(0)),false,false);
        }
        else{
            Pair<String,Boolean> pair = (Pair<String, Boolean>) visit(ctx.getChild(0));
            return new Comparer(pair.left,true,pair.right);
        }
    }

    @Override
    public Object visitComparator(SQLParser.ComparatorContext ctx){
        return ctx.getText();
    }

    @Override
    public Object visitExpression(SQLParser.ExpressionContext ctx){
        if (ctx.getChildCount() == 1){
            return visit(ctx.getChild(0));
        }
        else{
            //TODO
            //support expression,not only comparer
            return null;
        }
    }

    @Override
    public Object visitResult_column(SQLParser.Result_columnContext ctx){
        return ctx.getText();
    }

    @Override
    public Object visitTable_query(SQLParser.Table_queryContext ctx){
        if (ctx.getChildCount() == 1){
            String table_name = (String) visit(ctx.getChild(0));
            return new TableQuery(table_name);
        }
        else {
            TableQuery left = (TableQuery) visit(ctx.table_query());
            String right_name = (String) visit(ctx.table_name());
            TableQuery right = new TableQuery(right_name);
            String join_type;
            if (ctx.K_NATURAL() != null){
                join_type = "natural";
                return new TableQuery(left,right,join_type,null);
            }
            else if (ctx.K_LEFT()!= null){
                join_type = "left outer";
            }
            else if (ctx.K_RIGHT()!=null){
                join_type = "right outer";
            }
            else if (ctx.K_FULL()!=null){
                join_type = "full outer";
            }
            else {
                join_type = "inner";
            }
            Condition condition = (Condition) visit(ctx.multiple_condition());
            return new TableQuery(left,right,join_type,condition);
        }
    }
    // TODO
    // auth_level
    @Override
    public Object visitLiteral_value(SQLParser.Literal_valueContext ctx){
        String value = ctx.getChild(0).getText();
        if (value.startsWith("\'")){
            return new Pair<String,Boolean>(value.substring(1,value.length()-1),false);
        }
        else{
            if (value.equalsIgnoreCase("null")){
                return new Pair<String,Boolean>(value,true);
            }
            return new Pair<String,Boolean>(value,false);
        }
    }

    @Override
    public Object visitColumn_full_name(SQLParser.Column_full_nameContext ctx){
        return ctx.getChild(0).getText();
    }

    @Override
    public Object visitDatabase_name(SQLParser.Database_nameContext ctx){
        return ctx.getChild(0).getText();
    }

    @Override
    public Object visitTable_name(SQLParser.Table_nameContext ctx){
        return ctx.getChild(0).getText();
    }

    @Override
    public Object visitUser_name(SQLParser.User_nameContext ctx){
        return ctx.getChild(0).getText();
    }

    @Override
    public Object visitColumn_name(SQLParser.Column_nameContext ctx){
        return ctx.getChild(0).getText();
    }

    @Override
    public Object visitView_name(SQLParser.View_nameContext ctx){
        return ctx.getChild(0).getText();
    }

    @Override
    public Object visitPassword(SQLParser.PasswordContext ctx){
        return ctx.getChild(0).getText();
    }
}
