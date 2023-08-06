package tinycc.implementation.expression;

import tinycc.parser.Token;

public class UnaryExpression extends Expression{
    private  Token operator;
    private boolean postfix;
    private Expression operand;
    public UnaryExpression(Token operator, boolean postfix, Expression operand){
     this.operator =operator;
     this.postfix =postfix;
     this.operand =operand;
     
    }
 @Override
 public String toString() {
    return  "Unary_" + operator.toString() +"[" +operand.toString() + "]";
 }
 }