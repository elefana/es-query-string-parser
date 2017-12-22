/*******************************************************************************
 * Copyright 2017 Viridian Software Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.elefana.esqs;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.elefana.esqs.antlr.EsQueryStringBaseListener;
import com.elefana.esqs.antlr.EsQueryStringLexer;
import com.elefana.esqs.antlr.EsQueryStringParser;
import com.elefana.esqs.antlr.EsQueryStringParser.FieldQueryContext;
import com.elefana.esqs.antlr.EsQueryStringParser.QueryContext;
import com.elefana.esqs.antlr.EsQueryStringParser.QueryExpressionContext;
import com.elefana.esqs.antlr.EsQueryStringParser.QueryTermExpressionContext;

public class EsQueryString extends EsQueryStringBaseListener {
	private final String originalString;
	private final EsFieldQuery queryChain = new EsFieldQuery();

	public EsQueryString(String originalString) {
		super();
		this.originalString = originalString;
	}

	public void walk(EsQueryStringWalker walker) {
		queryChain.walk(walker);
	}

	@Override
	public void exitQuery(QueryContext ctx) {
		QueryExpressionContext queryExpressionContext = ctx.queryExpression();

		EsFieldQuery mostRecentQuery = queryChain;

		while (queryExpressionContext != null) {
			FieldQueryContext fieldQueryContext = queryExpressionContext.fieldQuery();
			parseFieldQuery(mostRecentQuery, fieldQueryContext);

			if (queryExpressionContext.queryExpression() == null) {
				queryExpressionContext = null;
				break;
			}

			mostRecentQuery = mostRecentQuery.appendQuery(
					queryExpressionContext.queryOperator().OR() != null ? EsQueryOperator.OR : EsQueryOperator.AND);
			queryExpressionContext = queryExpressionContext.queryExpression();
		}
	}

	private void parseFieldQuery(EsFieldQuery query, FieldQueryContext fieldQueryContext) {
		if (fieldQueryContext.FieldNameLiteral() != null) {
			query.setFieldName(fieldQueryContext.FieldNameLiteral().getText());
		}
		query.setQueryExpression(parseQueryExpression(fieldQueryContext.queryTermExpression()));
	}

	private EsQueryExpression parseQueryExpression(QueryTermExpressionContext queryTermExpressionContext) {
		if (queryTermExpressionContext.queryTermExpression() == null
				|| queryTermExpressionContext.queryTermExpression().isEmpty()) {
			if (queryTermExpressionContext.termExpression() != null) {
				return new EsQueryTerm(false, queryTermExpressionContext.termExpression().getText());
			} else {
				return new EsQueryTerm(true, queryTermExpressionContext.phraseExpression().getText());
			}
		}

		EsQueryTerm queryTerm = null;
		EsQueryOperator queryOperator = EsQueryOperator.DEFAULT;

		if (queryTermExpressionContext.termExpression() != null) {
			queryTerm = new EsQueryTerm(false, queryTermExpressionContext.termExpression().getText());
		} else {
			queryTerm = new EsQueryTerm(true, queryTermExpressionContext.phraseExpression().getText());
		}

		if (queryTermExpressionContext.queryOperator() == null) {
			return new EsQueryTermGroup(queryTerm, queryOperator,
					parseQueryExpression(queryTermExpressionContext.queryTermExpression()));
		}
		return new EsQueryTermGroup(queryTerm,
				queryTermExpressionContext.queryOperator().AND() != null ? EsQueryOperator.AND : EsQueryOperator.OR,
				parseQueryExpression(queryTermExpressionContext.queryTermExpression()));
	}

	/**
	 * Parses a {@link String} into a {@link EsQueryString}
	 * 
	 * @param queryString
	 *            The string to parse
	 * @return Null if the string was null or empty
	 */
	public static EsQueryString parse(String queryString) {
		if (queryString == null || queryString.isEmpty()) {
			return null;
		}

		EsQueryStringLexer lexer = new EsQueryStringLexer(new ANTLRInputStream(queryString));
		EsQueryStringParser parser = new EsQueryStringParser(new BufferedTokenStream(lexer));

		QueryContext queryContext = parser.query();
		ParseTreeWalker parseTreeWalker = new ParseTreeWalker();

		EsQueryString result = new EsQueryString(queryString);
		parseTreeWalker.walk(result, queryContext);
		return result;
	}

	/**
	 * Returns the original {@link String} that was parsed
	 * 
	 * @return
	 */
	public String getOriginalString() {
		return originalString;
	}

	/**
	 * Returns the first {@link EsFieldQuery} in the query chain. Subsequent
	 * queries can be retrieved recursively through
	 * {@link EsFieldQuery#getNextQuery()}
	 * 
	 * @return Returns the first {@link EsFieldQuery} in the chain
	 */
	public EsFieldQuery getQueryChain() {
		return queryChain;
	}
}
