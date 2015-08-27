// Generated from com\synaptix\taskmanager\antlr\GraphCalc.g4 by ANTLR 4.5.1
package com.synaptix.taskmanager.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GraphCalcParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GraphCalcVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GraphCalcParser#compile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompile(GraphCalcParser.CompileContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphCalcParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(GraphCalcParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphCalcParser#exprOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprOr(GraphCalcParser.ExprOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphCalcParser#exprAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprAnd(GraphCalcParser.ExprAndContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphCalcParser#exprNext}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprNext(GraphCalcParser.ExprNextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code id}
	 * labeled alternative in {@link GraphCalcParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(GraphCalcParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parens}
	 * labeled alternative in {@link GraphCalcParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(GraphCalcParser.ParensContext ctx);
}