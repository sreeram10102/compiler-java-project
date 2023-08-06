package tinycc.implementation.statement;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;

public class ReturnStatement extends Statement{
    private Locatable loc;
    private  Expression expression;
    public ReturnStatement(Locatable loc, Expression expression){
        this.loc =loc;
        this.expression =expression;
    }
    @Override
    public String toString() {
       if(expression!=null) {
     return   " Return[" +expression.toString() + "]";
       }
       return   " Return[]";
    }
}