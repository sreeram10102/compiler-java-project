package tinycc.implementation.expression;


import java.util.List;

import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class FunctionDefinition {
   private Type type;
   private Token name;
   private List<Token> parameterNames;
   private Statement body ;
   FunctionDefinition function = new FunctionDefinition(type, name, parameterNames, body);

   public FunctionDefinition(tinycc.implementation.type.Type type2, Token name, List<Token> parameterNames, Statement body){
    this.type=type2;
    this.name =name;
    this.parameterNames = parameterNames;
    this.body =body;
   }
}

