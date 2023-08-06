package tinycc.implementation;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.CallExpression;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.FunctionDefinition;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.statement.BlockStatement;
import tinycc.implementation.statement.DeclarationStatement;
import tinycc.implementation.statement.ExpressionStatement;
import tinycc.implementation.statement.IfStatement;
import tinycc.implementation.statement.ReturnStatement;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.statement.WhileStatement;
import tinycc.implementation.type.BaseType;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.parser.ASTFactory;

import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class AST implements ASTFactory {
   private List<ExternalDeclaration> externalDeclarations = new ArrayList<>();
   private List<FunctionDefinition> functionDefinition = new ArrayList<>();

   @Override
   public Statement createBlockStatement(Locatable loc, List<Statement> statements) {

      return new BlockStatement(loc, statements);
   }

   @Override
   public Statement createDeclarationStatement(Type type, Token name, Expression init) {
      return new DeclarationStatement(type, name, init);
   }

   @Override
   public Statement createExpressionStatement(Locatable loc, Expression expression) {
      return new ExpressionStatement(loc, expression);
   }

   @Override
   public Statement createIfStatement(Locatable loc, Expression condition, Statement consequence,
         Statement alternative) {
      return new IfStatement(loc, condition, consequence, alternative);
   }

   @Override
   public Statement createReturnStatement(Locatable loc, Expression expression) {
      return new ReturnStatement(loc, expression);
   }

   @Override
   public Statement createWhileStatement(Locatable loc, Expression condition, Statement body) {
      return new WhileStatement(loc, condition, body);
   }

   @Override
   public Type createFunctionType(Type returnType, List<Type> parameters) {
      return new FunctionType(returnType, parameters);
   }

   @Override
   public Type createPointerType(Type pointsTo) {
      return new PointerType(pointsTo);
   }

   @Override
   public Type createBaseType(TokenKind kind) {
      return new BaseType(kind);
   }

   @Override
   public Expression createBinaryExpression(Token operator, Expression left, Expression right) {
      return new BinaryExpression(operator, left, right);
   }

   @Override
   public Expression createCallExpression(Token token, Expression callee, List<Expression> arguments) {
      return new CallExpression(token, callee, arguments);
   }

   @Override
   public Expression createConditionalExpression(Token token, Expression condition, Expression consequence,
         Expression alternative) {
      return condition; // return new ConditionalExpression(token,condition,consequence,alternative);
   }

   @Override
   public Expression createUnaryExpression(Token operator, boolean postfix, Expression operand) {
      return new UnaryExpression(operator, postfix, operand);
   }

   @Override
   public Expression createPrimaryExpression(Token token) {
      return new PrimaryExpression(token);
   }

   @Override
   public void createExternalDeclaration(Type type, Token name) {
      ExternalDeclaration declare = new ExternalDeclaration(type, name);
      externalDeclarations.add(declare);
   }

   @Override
   public void createFunctionDefinition(Type type, Token name, List<Token> parameterNames, Statement body) {
      FunctionDefinition function = new FunctionDefinition(type, name, parameterNames, body);

      functionDefinition.add(function);
   }

}
