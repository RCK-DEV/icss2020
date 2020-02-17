package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;

// GE01, GE02
public class Generator {

	private final String NEWLINE = "\n";
	private final String TAB = "\t";

	public String generate(AST ast) {
		StringBuilder stringBuilder = new StringBuilder();

		for (ASTNode node : ast.root.getChildren()) {
			stringBuilder.append(walkTree(node));
		}

		return stringBuilder.toString();
	}

	private String walkTree(ASTNode node) {
		StringBuilder stringBuilder = new StringBuilder();

		if (node instanceof Stylerule) {
			Stylerule styleruleNode = (Stylerule) node;

			for (Selector selector : styleruleNode.selectors) {
				stringBuilder
						.append(selector.toString())
						.append(" ");
			}

			stringBuilder.append("{" + NEWLINE);

			for (ASTNode declaration : node.getChildren()) {
				if (declaration instanceof Declaration) {
					Declaration declarationNode = (Declaration) declaration;
					stringBuilder
							.append(TAB)
							.append(declarationNode.property.name)
							.append(": ")
							.append(declarationNode.expression.toString());
					stringBuilder
							.append(";")
							.append(NEWLINE);
				}
			}
			stringBuilder.append("}" + NEWLINE);
		}

		return stringBuilder.toString();
	}
}
