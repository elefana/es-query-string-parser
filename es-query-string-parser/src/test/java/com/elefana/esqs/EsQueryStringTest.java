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

import org.junit.Test;

import junit.framework.Assert;

public class EsQueryStringTest implements EsQueryStringWalker {
	private final StringBuilder queryResult = new StringBuilder();
	
	@Test
	public void testDefaultFieldQuery() {
		EsQueryString queryString = EsQueryString.parse("value");
		queryString.walk(this);
		Assert.assertEquals("null='value'", queryResult.toString());
	}
	
	@Test
	public void testBasicFieldQuery() {
		EsQueryString queryString = EsQueryString.parse("status:active");
		queryString.walk(this);
		Assert.assertEquals("status='active'", queryResult.toString());
	}
	
	@Test
	public void testGroupedFieldQuery() {
		EsQueryString queryString = EsQueryString.parse("status:(active inactive)");
		queryString.walk(this);
		Assert.assertEquals("status='active' DEFAULT status='inactive'", queryResult.toString());
	}
	
	@Test
	public void testGroupedOperatorFieldQuery() {
		EsQueryString queryString = EsQueryString.parse("status:(active OR inactive)");
		queryString.walk(this);
		Assert.assertEquals("status='active' OR status='inactive'", queryResult.toString());
		
		queryString = EsQueryString.parse("status:(active AND inactive)");
		queryString.walk(this);
		Assert.assertEquals("status='active' AND status='inactive'", queryResult.toString());
	}
	
	@Test
	public void testMultiFieldQuery() {
		EsQueryString queryString = EsQueryString.parse("status:active AND deleted:false");
		queryString.walk(this);
		Assert.assertEquals("status='active' AND deleted='false'", queryResult.toString());
	}

	@Override
	public void beginField(EsQueryOperator operator, EsFieldQuery field) {
		
	}

	@Override
	public void append(EsFieldQuery field, EsQueryOperator operator) {
		queryResult.append(" " + operator.name() + " ");
	}

	@Override
	public void append(EsFieldQuery field, boolean phrase, String term) {
		queryResult.append(field.getFieldName());
		queryResult.append("=");
		if(phrase) {
			queryResult.append(term);
		} else {
			queryResult.append("'");
			queryResult.append(term);
			queryResult.append("'");
		}
	}

	@Override
	public void endField(EsFieldQuery field) {
		
	}

	@Override
	public void beginGrouping() {
		queryResult.append('(');
	}

	@Override
	public void endGrouping() {
		queryResult.append(')');
	}

}
