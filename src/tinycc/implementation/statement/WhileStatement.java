package tinycc.implementation.statement;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;

public class WhileStatement extends Statement{
    private Locatable loc;
    private  Expression condition;
    private Statement body;
    public WhileStatement(Locatable loc, Expression condition, Statement body){
        this.loc=loc;
        this.condition =condition;
        this.body = body;
    }
    @Override
    public String toString() {
       return "While[" + condition.toString() + "," +body.toString() + "]";
    }
}
