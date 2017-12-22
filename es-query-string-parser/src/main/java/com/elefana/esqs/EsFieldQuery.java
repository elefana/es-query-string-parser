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

public class EsFieldQuery {
	private final EsQueryOperator operator;
	
	private String fieldName;
	private EsQueryExpression queryExpression;
	
	private EsFieldQuery nextQuery;

	public EsFieldQuery() {
		this(EsQueryOperator.DEFAULT);
	}

	private EsFieldQuery(EsQueryOperator operator) {
		super();
		this.operator = operator;
	}
	
	public void walk(EsQueryStringWalker walker) {
		walker.beginField(operator, this);
		if(queryExpression != null) {
			queryExpression.walk(this, walker);
		}
		walker.endField(this);
		
		if(nextQuery == null) {
			return;
		}
		nextQuery.walk(walker);
	}
	
	public boolean isDefaultField() {
		return fieldName == null;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public EsQueryOperator getOperator() {
		return operator;
	}

	public EsQueryExpression getQueryExpression() {
		return queryExpression;
	}

	public void setQueryExpression(EsQueryExpression queryExpression) {
		this.queryExpression = queryExpression;
	}

	/**
	 * The next query in the chain
	 * @return Null if there is no query
	 */
	public EsFieldQuery getNextQuery() {
		return nextQuery;
	}

	public EsFieldQuery appendQuery(EsQueryOperator operator) {
		if(nextQuery != null) {
			return nextQuery.appendQuery(operator);
		} else {
			nextQuery = new EsFieldQuery(operator);
			return nextQuery;
		}
	}
}
