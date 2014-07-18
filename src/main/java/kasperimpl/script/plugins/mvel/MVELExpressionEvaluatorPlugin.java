package kasperimpl.script.plugins.mvel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kasper.script.ExpressionEvaluatorPlugin;
import kasper.script.expression.ExpressionParameter;

/**
 * Evaluation d'une expression en se basant sur MVEL.
 *
 * @author  pchretien
 * @version $Id: MVELExpressionEvaluatorPlugin.java,v 1.1 2012/10/08 16:23:52 pchretien Exp $
 */
public final class MVELExpressionEvaluatorPlugin implements ExpressionEvaluatorPlugin {
	/** {@inheritDoc} */
	public <J> J evaluate(final String expression, final List<ExpressionParameter> parameters, final Class<J> type) {
		final Map<String, Object> context = new HashMap<String, Object>();
		for (final ExpressionParameter parameter : parameters) {
			context.put(parameter.getName(), parameter.getValue());
		}
		return type.cast(doEvaluate(expression, context));
	}

	private Object doEvaluate(final String expression, final Map<String, ?> context) {
		return org.mvel2.MVEL.eval(expression, context);
	}
}
