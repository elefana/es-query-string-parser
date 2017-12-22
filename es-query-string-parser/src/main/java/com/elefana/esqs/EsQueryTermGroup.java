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

public class EsQueryTermGroup implements EsQueryExpression {
	private final EsQueryTerm term;
	private final EsQueryOperator operator;
	private final EsQueryExpression expression;
	
	public EsQueryTermGroup(EsQueryTerm term, EsQueryOperator operator, EsQueryExpression expression) {
		super();
		this.operator = operator;
		this.term = term;
		this.expression = expression;
	}
	
	@Override
	public void walk(EsFieldQuery field, EsQueryStringWalker walker) {
		walker.beginGrouping();
		term.walk(field, walker);
		walker.append(field, operator);
		expression.walk(field, walker);
		walker.endGrouping();
	}

	public EsQueryOperator getOperator() {
		return operator;
	}

	public EsQueryTerm getTerm() {
		return term;
	}

	public EsQueryExpression getExpression() {
		return expression;
	}
}
