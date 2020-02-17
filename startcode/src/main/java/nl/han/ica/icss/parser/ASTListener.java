package nl.han.ica.icss.parser;

import java.util.Stack;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private Stack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new Stack<>();
	}

	public AST getAST() {
		return ast;
	}

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet styleSheet = new Stylesheet();
		ast.root = styleSheet;
		currentContainer.push(styleSheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.peek().addChild(stylerule);
		currentContainer.push(stylerule);

	}

	@Override
	public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.peek().addChild(new IdSelector(ctx.ID_IDENT().toString()));
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.peek().addChild(new ClassSelector(ctx.CLASS_IDENT().toString()));
	}

	@Override
	public void enterElementSelector(ICSSParser.ElementSelectorContext ctx) {
		currentContainer.peek().addChild(new TagSelector(ctx.LOWER_IDENT().toString()));
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.peek().addChild(declaration);
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		currentContainer.peek().addChild(new PropertyName(ctx.LOWER_IDENT().toString()));
	}

	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		currentContainer.peek().addChild(new ColorLiteral(ctx.COLOR().toString()));
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		currentContainer.peek().addChild(new PercentageLiteral(ctx.PERCENTAGE().toString()));
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		currentContainer.peek().addChild(new PixelLiteral(ctx.PIXELSIZE().toString()));
	}

	@Override
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		currentContainer.peek().addChild(new BoolLiteral(ctx.getText()));
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		currentContainer.peek().addChild(new ScalarLiteral(ctx.SCALAR().toString()));
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment variableAssignment = new VariableAssignment();
		currentContainer.peek().addChild(variableAssignment);
		currentContainer.push(variableAssignment);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		currentContainer.peek().addChild(new VariableReference(ctx.CAPITAL_IDENT().toString()));
	}
}