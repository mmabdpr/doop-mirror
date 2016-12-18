package org.clyze.deepdoop.datalog.clause;

import java.util.ArrayList;
import java.util.List;
import org.clyze.deepdoop.actions.*;
import org.clyze.deepdoop.datalog.element.*;
import org.clyze.deepdoop.datalog.element.atom.*;
import org.clyze.deepdoop.datalog.expr.*;
import org.clyze.deepdoop.system.*;

public class Rule implements IVisitable, ISourceItem {

	public final LogicalElement head;
	public final IElement       body;
	public final boolean        isDirective;
	SourceLocation              _loc;

	public Rule(LogicalElement head, IElement body) {
		this(head, body, null);
	}
	public Rule(LogicalElement head, IElement body, SourceLocation loc) {
		this.head = head;
		this.body = body;

		this.isDirective = (
				body == null &&
				head.elements.size() == 1 &&
				head.elements.iterator().next() instanceof Directive);
		this._loc = loc;

		List<VariableExpr> varsInHead = head.getVars();
		if (body != null) {
			List<VariableExpr> varsInBody = body.getVars();
			for (VariableExpr v : varsInHead) {
				if (!varsInBody.contains(v))
					ErrorManager.error(location(), ErrorId.UNKNOWN_VAR, v.name);
			}
		}
	}

	public Directive getDirective() {
		return (isDirective ? (Directive) head.elements.iterator().next() : null);
	}


	@Override
	public <T> T accept(IVisitor<T> v) {
		return v.visit(this);
	}
	@Override
	public SourceLocation location() {
		return _loc;
	}
}
