package tinycc.implementation.type;

import tinycc.parser.TokenKind;

public class BaseType extends Type{
    private TokenKind kind;
    public BaseType(TokenKind kind){
       this.kind =kind;
    }
    @Override
    public String toString() {
       return "Type_"+kind.toString(); 
    }
}