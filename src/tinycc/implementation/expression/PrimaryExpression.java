package tinycc.implementation.expression;

import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class PrimaryExpression extends Expression{
    Token token;
    public PrimaryExpression(Token token){
        this.token=token;
        
    }
    @Override
    public String toString() {
       if(token.getKind()==TokenKind.IDENTIFIER) {
        return "Var_"+ token.toString(token);

       }
       if(token.getKind()==TokenKind.NUMBER){
        return "Const_"+token.toString();
       }
       if(token.getKind()==TokenKind.STRING){
        return "Const_"+token.toString();
       }
       if(token.getKind()==TokenKind.CHARACTER){
        return "Const_"+token.toString();
       }
       return "Const_" +token.toString();
       

    }
}
