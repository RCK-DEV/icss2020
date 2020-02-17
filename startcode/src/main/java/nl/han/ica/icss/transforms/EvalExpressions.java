package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

// TR01
public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() { variableValues = new LinkedList<>(); }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.push(new HashMap<>());
        walkTree(ast.root);
    }

    private void walkTree(ASTNode node) {
        if ( node instanceof Declaration) {
            transformDeclaration((Declaration)node);
        }
        else if ( node instanceof VariableAssignment) {
            transformVariableAssignment((VariableAssignment) node);
        }

        for(ASTNode child: node.getChildren()){
            walkTree(child);
        }
    }

    private void transformVariableAssignment(VariableAssignment variableAssignment) {
        Literal literal = literalOf(variableAssignment.expression);
        variableAssignment.expression = literal;
        variableValues.getFirst().put(variableAssignment.name.name, literal);
    }

    private void transformDeclaration(Declaration declaration) {
        Literal literal = literalOf(declaration.expression);
        declaration.expression = literal;
    }

    private Literal literalOf(Expression expression){
        if(expression instanceof Literal) {
            return (Literal) expression;
        } else if(expression instanceof Operation) {
            return calculateOperation((Operation) expression);
        } else if(expression instanceof VariableReference) {
            return makeVariableLiteral((VariableReference) expression);
        }
        return null;
    }

    private Literal calculateOperation(Operation operation) {
        Literal leftOperand = literalOf(operation.lhs);
        Literal rightOperand = literalOf(operation.rhs);
        return calculator(leftOperand, rightOperand, operation);
    }

    private Literal makeVariableLiteral(VariableReference variableReference) {
        for(HashMap<String,Literal> current : variableValues) {
            if(current.containsKey(variableReference.name))
                return current.get(variableReference.name);
        }
        return null;
    }

    private Literal calculator(Literal left, Literal right, Operation operation) {
        if (operation instanceof AddOperation) {
            return createLiteral(left, getValue(left) + getValue(right));
        } else if (operation instanceof MultiplyOperation) {
            return createLiteral(left, getValue(left) * getValue(right));
        } else if (operation instanceof SubtractOperation){
            return createLiteral(left, getValue(left) - getValue(right));
        }
        else return null;
    }

    private Literal createLiteral(Literal literal, int value) {
        if(literal instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        }  else if(literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        }
        else if(literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        }
        return null;
    }

    private int getValue(Expression expression) {
        if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value;
        } else if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value;
        } else if (expression instanceof ScalarLiteral) {
            return ((ScalarLiteral) expression).value;
        }
        return 0;
    }

}
