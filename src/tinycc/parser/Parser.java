package tinycc.parser;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.diagnostic.Location;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;

/**
 * The TinyC parser.
 */
public class Parser {
	private final Diagnostic diagnostic;
	private final Lexer lexer;
	private final ASTFactory factory;
	private Token token;
	private Token lookAhead;

	public static final Location errorLocation = new Location("<error>", 0, 0);

	public class ParserError extends Exception {};

	/**
	 * Initializes a new parser.
	 *
	 * @param diagnostic The diagnostic module to use
	 * @param lexer      The lexer to use.
	 * @param factory    The ASTFactory to emit the AST nodes.
	 * @see Diagnostic
	 * @see Lexer
	 * @see ASTFactory
	 */
	public Parser(final Diagnostic diagnostic, final Lexer lexer, final ASTFactory factory) {
		if (diagnostic == null || lexer == null || factory == null)
			throw new IllegalArgumentException();
		this.diagnostic = diagnostic;
		this.lexer = lexer;
		this.factory = factory;
		/* Initialize current token and look ahead. */
		nextToken();
		nextToken();
	}

	/**
	 * Parses this translation unit. Each AST-node is generated by a call to the
	 * corresponding method of the ASTFactory.
	 */
	public void parseTranslationUnit() {
		while (!peek(TokenKind.EOF)) {
			try {
				parseExternalDeclaration();
			} catch (ParserError e) {
				return;
			}
		}
	}

	// -------------- Internal methods of the parser --------------

	private void nextToken() {
		token = lookAhead;
		lookAhead = lexer.next();
	}

	private boolean peek(final TokenKind t) {
		return token.getKind() == t;
	}

	private boolean accept(final TokenKind t) {
		if (peek(t)) {
			nextToken();
			return true;
		} else {
			return false;
		}
	}

	private void expect(final TokenKind t) throws ParserError {
		if (!accept(t)) {
			diagnostic.printError(token, "expected '%s', but got '%s'", t, token);
			throw new ParserError();
		}
	}

	private Statement parseBlock() throws ParserError {
		final Locatable loc = token;
		expect(TokenKind.LBRACE);
		final List<Statement> stmts = new ArrayList<Statement>();
		for (;;) {
			switch (token.getKind()) {
			case AND:
			case ASTERISK:
			case BANG:
			case BREAK:
			case CHAR:
			case CHARACTER:
			case CONTINUE:
			case IDENTIFIER:
			case IF:
			case INT:
			case LBRACE:
			case LPAREN:
			case MINUS:
			case MINUS_MINUS:
			case NUMBER:
			case PLUS:
			case PLUS_PLUS:
			case RETURN:
			case SEMICOLON:
			case SIZEOF:
			case STRING:
			case TILDE:
			case VOID:
			case WHILE:
			case _ASSERT:
			case _ASSUME:
				stmts.add(parseStatement(false));
				continue;

			case RBRACE:
				expect(TokenKind.RBRACE);
				break;

			case EOF:
				diagnostic.printError(loc, "reached end of file while parsing block");
				break;

			default:
				diagnostic.printError(token, "expected statement or '}' while parsing block, but got '%s'", token);
				break;
			}
			break;
		}
		return factory.createBlockStatement(loc, stmts);
	}

	private Statement parseBreak() throws ParserError {
		final Locatable loc = token;
		expect(TokenKind.BREAK);
		expect(TokenKind.SEMICOLON);
		return factory.createBreakStatement(loc);
	}

	private Statement parseContinue() throws ParserError {
		final Locatable loc = token;
		expect(TokenKind.CONTINUE);
		expect(TokenKind.SEMICOLON);
		return factory.createContinueStatement(loc);
	}

	private Token parseIdentifier() {
		final Token t = token;
		if (peek(TokenKind.IDENTIFIER)) {
			nextToken();
			return t;
		} else {
			diagnostic.printError(t, "expected identifier, but got '%s'", t);
			return new Token(errorLocation, TokenKind.IDENTIFIER, "<missing>");
		}
	}

	private Statement parseDeclaration() throws ParserError {
		final Type type = parseType();
		final Token name = parseIdentifier();
		final Expression init = accept(TokenKind.EQUAL) ? parseExpression() : null;
		expect(TokenKind.SEMICOLON);
		return factory.createDeclarationStatement(type, name, init);
	}

	private static boolean isType(final TokenKind t) {
		switch (t) {
		case CHAR:
		case INT:
		case VOID:
			return true;

		default:
			return false;
		}
	}

	private Expression parseOperand() throws ParserError {
		final Token t = token;
		switch (t.getKind()) {
		case AND:
		case ASTERISK:
		case BANG:
		case MINUS:
		case MINUS_MINUS:
		case PLUS:
		case PLUS_PLUS:
		case SIZEOF:
		case TILDE: {
			nextToken();
			final Expression operand = parseExpression(Precedence.UNARY);
			return factory.createUnaryExpression(t, false, operand);
		}

		case LPAREN:
			expect(TokenKind.LPAREN);
			if (isType(token.getKind())) {
				@SuppressWarnings("unused")
				final Type type = parseType();
				expect(TokenKind.RPAREN);
				final Expression operand = parseExpression(Precedence.CAST);
				// return factory.createCastExpression(t, type, operand);
				diagnostic.printError(t, "cast not supported");
				return operand;
			} else {
				final Expression expr = parseExpression();
				expect(TokenKind.RPAREN);
				return expr;
			}

		case IDENTIFIER:
		case NUMBER:
		case CHARACTER:
		case STRING:
			nextToken();
			return factory.createPrimaryExpression(t);

		default:
			diagnostic.printError(token, "expected expression, but got '%s'", token);
			throw new ParserError();
		}
	}

	private Expression parseExpression(final Precedence precedence) throws ParserError {
		Expression expr = parseOperand();
		for (;;) {
			final Token t = token;
			final TokenKind kind = t.getKind();
			if (kind.getLPrec().less(precedence))
				return expr;
			nextToken();
			switch (kind) {
			case LBRACKET:
				expr = parseArrayAccess(expr, t);
				break;
			case LPAREN:
				expr = parseFunctionCall(expr, t);
				break;
			case QUESTION_MARK:
				expr = parseConditionalExpression(expr, t);
				break;

			default: {
				final Precedence rPrec = kind.getRPrec();
				if (rPrec == Precedence.NONE) {
					expr = factory.createUnaryExpression(t, true, expr);
				} else {
					final Expression right = parseExpression(rPrec);
					expr = factory.createBinaryExpression(t, expr, right);
				}
				break;
			}
			}
		}
	}

	private Expression parseArrayAccess(final Expression expr, final Token t) throws ParserError  {
		final Expression index = parseExpression();
		expect(TokenKind.RBRACKET);
		return factory.createBinaryExpression(t, expr, index);
	}

	private Expression parseConditionalExpression(final Expression expr, final Token t) throws ParserError  {
		final Expression consequence = parseExpression();
		expect(TokenKind.COLON);
		final Expression alternative = parseExpression(Precedence.CONDITIONAL);
		return factory.createConditionalExpression(t, expr, consequence, alternative);
	}

	private Expression parseFunctionCall(final Expression expr, final Token t) throws ParserError  {
		final List<Expression> args = new ArrayList<Expression>();
		if (!peek(TokenKind.RPAREN)) {
			do {
				args.add(parseExpression(Precedence.ASSIGNMENT));
			} while (accept(TokenKind.COMMA));
		}
		expect(TokenKind.RPAREN);
		return factory.createCallExpression(t, expr, args);
	}

	public Expression parseExpression() throws ParserError  {
		return parseExpression(Precedence.EXPRESSION);
	}

	private Statement parseIf() throws ParserError  {
		final Locatable loc = token;
		expect(TokenKind.IF);
		expect(TokenKind.LPAREN);
		final Expression cond = parseExpression();
		expect(TokenKind.RPAREN);
		final Statement consequence = parseInnerStatement();
		final Statement alternative = accept(TokenKind.ELSE) ? parseInnerStatement() : null;
		return factory.createIfStatement(loc, cond, consequence, alternative);
	}

	private Statement parseReturn() throws ParserError  {
		final Locatable loc = token;
		expect(TokenKind.RETURN);
		final Expression expr = peek(TokenKind.SEMICOLON) ? null : parseExpression();
		expect(TokenKind.SEMICOLON);
		return factory.createReturnStatement(loc, expr);
	}

	private Expression acceptAnnotatedExpressionStmt(TokenKind kind) throws ParserError  {
		if (accept(kind)) {
			expect(TokenKind.LPAREN);
			Expression res = parseExpression();
			expect(TokenKind.RPAREN);
			expect(TokenKind.SEMICOLON);
			return res;
		}
		else
			return null;
	}

	private Statement parseWhile() throws ParserError  {
		final Locatable loc = token;
		expect(TokenKind.WHILE);
		expect(TokenKind.LPAREN);
		final Expression cond = parseExpression();
		expect(TokenKind.RPAREN);
			
		Expression inv  = null;
		Expression term = null;
		Token bound = null;

		// parse loop invariant if there's one
		if (accept(TokenKind._INVARIANT)) {
			expect(TokenKind.LPAREN);
			inv = parseExpression();
			expect(TokenKind.RPAREN);

			// parse a ranking function if there's one
			if (accept(TokenKind._TERM)) {
				expect(TokenKind.LPAREN);
				term = parseExpression();
				if (accept(TokenKind.SEMICOLON)) {
					bound = parseIdentifier();
				}
				expect(TokenKind.RPAREN);
			}
		} else {
			final Statement body = parseInnerStatement();
			return factory.createWhileStatement(loc, cond, body);
		}

		final Statement body = parseInnerStatement();
		return factory.createAnnotatedWhileStatement(loc, cond, body, inv, term, bound);
	}

	private Statement parseExpressionStatement() throws ParserError  {
		final Locatable loc = token;
		final Expression expr = parseExpression();
		expect(TokenKind.SEMICOLON);
		return factory.createExpressionStatement(loc, expr);
	}

	private Statement parseInnerStatement() throws ParserError  {
		return parseStatement(true);
	}

	public Statement parseStatement(final boolean inner) throws ParserError  {
		switch (token.getKind()) {
		case BREAK:
			return parseBreak();
		case CONTINUE:
			return parseContinue();
		case IF:
			return parseIf();
		case LBRACE:
			return parseBlock();
		case RETURN:
			return parseReturn();
		case WHILE:
			return parseWhile();

		case CHAR:
		case INT:
		case VOID:
			if (inner)
				diagnostic.printError(token, "declaration cannot be an inner statement, use {}");
			return parseDeclaration();

		case AND:
		case ASTERISK:
		case BANG:
		case CHARACTER:
		case IDENTIFIER:
		case LPAREN:
		case MINUS:
		case MINUS_MINUS:
		case NUMBER:
		case PLUS:
		case PLUS_PLUS:
		case SIZEOF:
		case STRING:
		case TILDE:
			return parseExpressionStatement();

		case _ASSUME:
			return factory.createAssumeStatement(token, acceptAnnotatedExpressionStmt(TokenKind._ASSUME));
		case _ASSERT:
			return factory.createAssertStatement(token, acceptAnnotatedExpressionStmt(TokenKind._ASSERT));

		default:
			diagnostic.printError(token, "expected statement, but got '%s'", token);
			throw new ParserError();
		}
	}

	public Type parseType() throws ParserError {
		final TokenKind kind;
		switch (token.getKind()) {
		case CHAR:
		case INT:
		case VOID:
			kind = token.getKind();
			nextToken();
			break;

		default:
			diagnostic.printError(token, "expected type, but got '%s'", token);
			throw new ParserError();
		}

		Type type = factory.createBaseType(kind);
		while (accept(TokenKind.ASTERISK)) {
			type = factory.createPointerType(type);
		}

		return type;
	}

	private void parseExternalDeclaration() throws ParserError {
		Type type = parseType();
		final Token name = parseIdentifier();
		switch (token.getKind()) {
		case LPAREN:
			expect(TokenKind.LPAREN);
			final List<Type> parameterTypes = new ArrayList<Type>();
			final List<Token> parameterNames = new ArrayList<Token>();
			if (peek(TokenKind.VOID) && lookAhead.getKind() == TokenKind.RPAREN) {
				/* No parameters. */
				expect(TokenKind.VOID);
			} else if (!peek(TokenKind.RPAREN)) {
				do {
					final Type pType = parseType();
					final Token pName = peek(TokenKind.IDENTIFIER) ? parseIdentifier() : null;
					parameterTypes.add(pType);
					parameterNames.add(pName);
				} while (accept(TokenKind.COMMA));
			}
			expect(TokenKind.RPAREN);
			type = factory.createFunctionType(type, parameterTypes);
			switch (token.getKind()) {
			case LBRACE: {
				final Statement body = parseBlock();
				factory.createFunctionDefinition(type, name, parameterNames, body);
				return;
			}

			case SEMICOLON:
				expect(TokenKind.SEMICOLON);
				break;

			default:
				diagnostic.printError(token, "expected '{' or ';' while parsing function, but got '%s'", token);
				break;
			}
			break;

		case SEMICOLON:
			expect(TokenKind.SEMICOLON);
			break;

		default:
			diagnostic.printError(token, "expected '(' or ';' while parsing external declaration, but got '%s'", token);
			break;
		}
		factory.createExternalDeclaration(type, name);
	}
}
