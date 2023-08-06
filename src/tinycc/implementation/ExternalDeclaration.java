package tinycc.implementation;


import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class ExternalDeclaration {
   private Type type; 
   private Token name;

   public ExternalDeclaration(Type type2, Token name) {
   this.type =type2;
   this.name= name; 
}
}