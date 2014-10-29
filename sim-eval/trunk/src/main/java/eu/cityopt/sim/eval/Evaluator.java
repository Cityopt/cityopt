package eu.cityopt.sim.eval;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Evaluation engine of the expression language.
 *
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class Evaluator {
	private ScriptEngine engine;

	private static final String ENGINE_NAME = "python";

	public Evaluator() throws EvaluationException {
		ScriptEngineManager manager = new ScriptEngineManager();
		this.engine = manager.getEngineByName(ENGINE_NAME);
		if (engine == null) {
			throw new EvaluationException(
					"Cannot find scripting engine \"" + ENGINE_NAME + "\"");
		}
		if (!(engine instanceof Compilable)) {
			throw new EvaluationException(
					"Scripting engine \"" + ENGINE_NAME + "\" has no compiler");
		}
		String threading = (String) engine.getFactory().getParameter("THREADING");
		if (threading == null) {
			throw new EvaluationException(
					"Scripting engine \"" + ENGINE_NAME + "\" is not multi-threaded");
		}
		//TODO: engine.setContext
	}

	Compilable getCompiler() {
		return (Compilable) engine;
	}
}
