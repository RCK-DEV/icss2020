package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TR02
public class RemoveIf implements Transform {

    private HashMap<String, Expression> variableReferences;

    @Override
    public void apply(AST ast) {
        variableReferences = new HashMap<>();
        findAllVariableReferences(ast.root);

        walkTree(ast.root, ast.root.getChildren());
    }

    private void walkTree(ASTNode parent, List<ASTNode> children) {
        for(ASTNode child : children) {
            if(child instanceof IfClause) { evaluateIfClause((IfClause) child, parent); }
            walkTree(child, child.getChildren());
        }
    }

    private void findAllVariableReferences(ASTNode node) {

        if(node instanceof VariableAssignment) {
            String name = ((VariableAssignment) node).name.name;
            Expression expression = ((VariableAssignment) node).expression;
            variableReferences.put(name, expression);
        }

        node.getChildren().forEach(this::findAllVariableReferences);
    }

    private List<ASTNode> evaluateIfClause(IfClause ifClause, ASTNode parent) {
        List<ASTNode> values = new ArrayList<>();

        if(ifClause.conditionalExpression instanceof BoolLiteral ||
                ifClause.conditionalExpression instanceof VariableReference) {

            if (booleanValueOf(ifClause.conditionalExpression)) {

                for (ASTNode node : ifClause.body) {
                    if (node instanceof IfClause) {
                        values.addAll(evaluateIfClause((IfClause) node, ifClause));
                    } else {
                        values.add(node);
                    }
                }

            }
        }

        removeIfClause(ifClause, parent);

        if(!(parent instanceof IfClause)) {
            for(ASTNode node : values) {
                parent.addChild(node);
            }
        }

        return values;
    }

    private boolean booleanValueOf(ASTNode node) {
        if(node instanceof BoolLiteral) { return ((BoolLiteral) node).value; }
        if(node instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) node;
            if(variableReferences.containsKey(variableReference.name)) {
                return ((BoolLiteral) variableReferences.get(variableReference.name)).value;
            }
        }

        return false;
    }

    private void removeIfClause(IfClause ifClause, ASTNode parent) {
        if(parent instanceof Stylerule) {
            Stylerule stylerule = (Stylerule) parent;
            stylerule.body.remove(ifClause);
        } else {
            parent.removeChild(ifClause);
        }
    }
}
