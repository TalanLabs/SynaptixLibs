package com.synaptix.taskmanager.antlr;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.taskmanager.antlr.GraphCalcParser.CompileContext;
import com.synaptix.taskmanager.antlr.GraphCalcParser.ExprAndContext;
import com.synaptix.taskmanager.antlr.GraphCalcParser.ExprContext;
import com.synaptix.taskmanager.antlr.GraphCalcParser.ExprNextContext;
import com.synaptix.taskmanager.antlr.GraphCalcParser.ExprOrContext;
import com.synaptix.taskmanager.antlr.GraphCalcParser.IdContext;
import com.synaptix.taskmanager.antlr.GraphCalcParser.ParensContext;

public class EvalGraphCalcVisitor extends GraphCalcBaseVisitor<AbstractGraphNode> {

	@Override
	public AbstractGraphNode visitCompile(CompileContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public AbstractGraphNode visitExpr(ExprContext ctx) {
		return visit(ctx.exprAnd());
	}

	@Override
	public AbstractGraphNode visitExprOr(ExprOrContext ctx) {
		if (ctx.exprAnd().size() > 1) {
			List<AbstractGraphNode> nodes = new ArrayList<AbstractGraphNode>();
			for (ExprAndContext term : ctx.exprAnd()) {
				nodes.add(visit(term));
			}
			return new OrGraphNode(nodes);
		}
		return visit(ctx.exprAnd(0));
	}

	@Override
	public AbstractGraphNode visitExprAnd(ExprAndContext ctx) {
		if (ctx.exprNext().size() > 1) {
			List<AbstractGraphNode> nodes = new ArrayList<AbstractGraphNode>();
			for (ExprNextContext term : ctx.exprNext()) {
				nodes.add(visit(term));
			}
			return new ParallelGraphNode(nodes);
		}
		return visit(ctx.exprNext(0));
	}

	@Override
	public AbstractGraphNode visitExprNext(ExprNextContext ctx) {
		AbstractGraphNode firstNode = visit(ctx.first);
		if (ctx.factor().size() > 1) {
			for (int i = 1; i < ctx.factor().size(); i++) {
				AbstractGraphNode nextNode = visit(ctx.factor(i));
				firstNode = new NextGraphNode(firstNode, nextNode);
			}
		}
		return firstNode;
	}

	@Override
	public AbstractGraphNode visitParens(ParensContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public AbstractGraphNode visitId(IdContext ctx) {
		return new IdGraphNode(ctx.getText());
	}
}