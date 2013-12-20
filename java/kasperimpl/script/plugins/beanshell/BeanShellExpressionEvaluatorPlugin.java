package kasperimpl.script.plugins.beanshell;

import java.util.List;

import kasper.kernel.exception.KRuntimeException;
import kasper.script.ExpressionEvaluatorPlugin;
import kasper.script.expression.ExpressionParameter;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * Evaluation d'une expression en se basant sur BeanShell.
 *
 * @author  pchretien
 * @version $Id: BeanShellExpressionEvaluatorPlugin.java,v 1.1 2012/10/08 16:23:52 pchretien Exp $
 */
public final class BeanShellExpressionEvaluatorPlugin implements ExpressionEvaluatorPlugin {
	/** {@inheritDoc} */
	public <J> J evaluate(final String expression, final List<ExpressionParameter> parameters, final Class<J> type) {
		try {
			return type.cast(doEvaluate(expression, parameters));
		} catch (final EvalError e) {
			throw new KRuntimeException("Erreur durant l'évaluation de l'expression {0}", e, expression);
		}
	}

	private Object doEvaluate(final String expression, final List<ExpressionParameter> parameters) throws EvalError {
		final Interpreter interpreter = new Interpreter();
		interpreter.eval("setAccessibility(true)"); // turn off access restrictions
		for (final ExpressionParameter parameter : parameters) {
			interpreter.set(parameter.getName(), parameter.getValue());
		}
		return interpreter.eval(expression);
	}
}
