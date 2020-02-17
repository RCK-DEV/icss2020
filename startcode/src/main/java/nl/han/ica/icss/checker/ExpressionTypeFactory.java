package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import static nl.han.ica.icss.ast.types.ExpressionType.SCALAR;

public class ExpressionTypeFactory {
    public ExpressionType getExpressionType(Expression expression, Checker checker) {
        if(expression instanceof ColorLiteral){
            return ExpressionType.COLOR;
        } else if (expression instanceof PercentageLiteral){
            return ExpressionType.PERCENTAGE;
        }else if (expression instanceof PixelLiteral){
            return ExpressionType.PIXEL;
        }else if (expression instanceof ScalarLiteral){
            return SCALAR;
        } else if (expression instanceof BoolLiteral){
            return ExpressionType.BOOL;
        }else if(expression instanceof VariableReference){
            return checker.checkVariableReference((VariableReference) expression);
        }
        return ExpressionType.UNDEFINED;
    }


}
