package tinycc.implementation.expression;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

import tinycc.parser.Token;

public class CallExpression extends Expression{
    private Token token;
    private Expression callee;
    private List<Expression> arguments ;
    public CallExpression(Token token, Expression callee, List<Expression> arguments){
     this.token = token;
     this.callee = callee;
     this.arguments = arguments;
    }
 @Override
 public String toString() {
    StringBuilder build = new StringBuilder();
    build.append("Call_");
    build.append ("[");
    build.append(token.toString());
    build.append(",");
    build.append(callee.toString());
    build.append(",");
     int i =0;
     Expression[] size = new Expression[arguments.size()];
     Iterator<Expression> iterator = arguments.iterator();
     while(iterator.hasNext()){
        size[i]=iterator.next();
        build.append(size[i])
;        if(!iterator.hasNext()){
            break;
        }
        build.append(",");
        i++;
     }
 
     
   return  build.toString();
 }
    
 }