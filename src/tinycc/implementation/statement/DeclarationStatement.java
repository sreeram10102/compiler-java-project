package tinycc.implementation.statement;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;

import tinycc.parser.Token;

public class DeclarationStatement extends Statement{
    private Type type;
    private Token name;
    private Expression init;
    public DeclarationStatement(Type type, Token name, Expression init){
     this.type =type;
     this.name =name;
     this.init =init;
    }
 @Override
 public String toString() {
    if(init==null){
        return "Declaration_"+name.toString() +"[" +type.toString() +"]";

    } 
    return "Declaration_"+name.toString() +"[" +type.toString() +"," + init.toString()+"]";
 }
}