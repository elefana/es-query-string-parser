//
// Copyright 2017 Viridian Software Limited
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
grammar EsQueryString;

query
    : queryExpression EOF
    ;

queryExpression
    : fieldQuery ((WHITESPACE queryOperator)? WHITESPACE queryExpression)?
    ;

fieldQuery
    : FieldNameLiteral queryTermExpression
    | queryTermExpression
    ;

FieldNameLiteral
	: FieldNameChar+ COLON
	;

fragment
FieldNameChar
	: LETTERORDIGIT
    | UNDERSCORE
	| BACKSLASH
    | FULLSTOP
    | ASTERISK
    | MINUS
    | PLUS
	;

queryTermExpression
    : GROUPBEGIN phraseExpression (WHITESPACE queryOperator WHITESPACE queryTermExpression)? GROUPEND
    | GROUPBEGIN termExpression (WHITESPACE queryOperator WHITESPACE queryTermExpression)? GROUPEND
    | GROUPBEGIN phraseExpression (WHITESPACE queryTermExpression)? GROUPEND
    | GROUPBEGIN termExpression (WHITESPACE queryTermExpression)? GROUPEND
    | phraseExpression
    | termExpression
    ;

termExpression
    : termChar+
    ;

termChar
	: ~WHITESPACE
    ;

phraseExpression
    : DOUBLEQUOTE phraseChar+ DOUBLEQUOTE
    ;

phraseChar
    : ~DOUBLEQUOTE
    ;

queryOperator
    :   AND
    |   OR
    ;

GROUPBEGIN : '(';
GROUPEND : ')';

AND
	: 'AND'
	| 'and'
	;
OR
	: 'OR'
	| 'or'
	;

LETTERORDIGIT: [a-zA-Z0-9];
BACKSLASH: '\\';
UNDERSCORE: '_';
MINUS: '-';
PLUS: '+';
DOUBLEQUOTE: '"';
FULLSTOP: '.';
ASTERISK: '*';
COLON: ':';
QUESTIONMARK: '?';
TILDA: '~';
WHITESPACE: [ \t];
NEWLINE : ( '\r'? '\n' | '\r' );