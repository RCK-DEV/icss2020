package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.*;

import static nl.han.ica.icss.ast.types.ExpressionType.*;

public class Checker {

    private LinkedList<HashMap<String,ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        walkTree(ast.root);
    }

    private void walkTree(ASTNode node) {
        if(node instanceof Stylesheet| node instanceof Stylerule){
            variableTypes.addFirst(new HashMap<>());
        } else if ( node instanceof VariableAssignment) {
            addVariable((VariableAssignment) node);
        }
        else if (node instanceof VariableReference){
            checkVariableReference((VariableReference)node);
        }
        else if(node instanceof Operation){
            checkOperationType((Operation)node);
        }
        else if(node instanceof Declaration){
            checkDeclaration((Declaration)node);
        }
        else if(node instanceof IfClause){
            checkIfClause((IfClause) node);
        }

        node.getChildren().forEach(this::walkTree);

        if(node instanceof Stylesheet| node instanceof Stylerule){
            variableTypes.pop();
        }
    }

    private void addVariable(VariableAssignment node) {
        variableTypes.getFirst().put(node.name.name,getExpressionType(node.expression));
    }

    private ExpressionType getExpressionType(Expression expression) {
        return new ExpressionTypeFactory().getExpressionType(expression, this);
    }

    // CH01
    protected ExpressionType checkVariableReference(VariableReference variableReference) {
        for(HashMap<String,ExpressionType> current:variableTypes){
            if(current.containsKey(variableReference.name)){
                return current.get(variableReference.name);
            }
        }
        variableReference.setError("The variable "+variableReference.name+" hasn't been assigned.");
        return ExpressionType.UNDEFINED;
    }

    // CH02, CH03
    private void checkOperationType(Operation operation) {
        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            ExpressionType lhsExpressionType = getExpressionType(operation.lhs);
            ExpressionType rhsExpressionType = getExpressionType(operation.rhs);

            if (lhsExpressionType != rhsExpressionType) {
                operation.setError("You cannot mix different operand types inside an expression");
            }
        }

        if (operation instanceof MultiplyOperation &&
                getExpressionType(operation.lhs) != SCALAR && getExpressionType(operation.rhs) != SCALAR) {
            operation.setError("A multiplication operation can only consist of scalar operands");
        }

        if (getExpressionType(operation.lhs) == COLOR || getExpressionType(operation.rhs) == COLOR) {
            operation.setError("Color values cannot be used with arithmetic operators");
        }
    }

    // CH04
    private void checkDeclaration(Declaration declaration) {
        if (declaration.expression instanceof Operation) { return; }

        String propertyName = declaration.property.name;
        ExpressionType expressionType = getExpressionType(declaration.expression);

        if (expressionType == PIXEL && (propertyName.equals("width") || propertyName.equals("height"))) return;
        if (expressionType == BOOL && (propertyName.equals("false") || propertyName.equals("true"))) return;
        if (expressionType == COLOR && (propertyName.equals("color") || propertyName.equals("background-color"))) return;

        declaration.setError("Declaration contains invalid value type");
    }

    // CH05
    private void checkIfClause(IfClause node) {
        Expression ifExpression = node.conditionalExpression;
        if (ifExpression instanceof BoolLiteral || ifExpression instanceof VariableReference) {
            if (ifExpression instanceof VariableReference) {
                ExpressionType expressionType = variableTypes.get(1).get(((VariableReference) ifExpression).name);
                if(expressionType != ExpressionType.BOOL) {
                    ifExpression.setError("If-clause does not contain a boolean expression");
                }
            }
        } else {
            ifExpression.setError("If-clause does not contain a boolean expression");
        }
    }
}
