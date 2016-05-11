/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.java.decompiler.main.extern;

import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IFernflowerPreferences
{

	public static final String REMOVE_BRIDGE = "rbr";
	public static final String REMOVE_SYNTHETIC = "rsy";
	public static final String DECOMPILE_INNER = "din";
	public static final String DECOMPILE_CLASS_1_4 = "dc4";
	public static final String DECOMPILE_ASSERTIONS = "das";
	public static final String HIDE_EMPTY_SUPER = "hes";
	public static final String HIDE_DEFAULT_CONSTRUCTOR = "hdc";
	public static final String DECOMPILE_GENERIC_SIGNATURES = "dgs";
	public static final String NO_EXCEPTIONS_RETURN = "ner";
	public static final String DECOMPILE_ENUM = "den";
	public static final String REMOVE_GET_CLASS_NEW = "rgn";
	public static final String LITERALS_AS_IS = "lit";
	public static final String BOOLEAN_TRUE_ONE = "bto";
	public static final String ASCII_STRING_CHARACTERS = "asc";
	public static final String SYNTHETIC_NOT_SET = "nns";
	public static final String UNDEFINED_PARAM_TYPE_OBJECT = "uto";
	public static final String USE_DEBUG_VAR_NAMES = "udv";
	public static final String REMOVE_EMPTY_RANGES = "rer";
	public static final String FINALLY_DEINLINE = "fdi";
	public static final String IDEA_NOT_NULL_ANNOTATION = "inn";
	public static final String LAMBDA_TO_ANONYMOUS_CLASS = "lac";
	public static final String BYTECODE_SOURCE_MAPPING = "bsm";

	public static final String LOG_LEVEL = "log";
	public static final String MAX_PROCESSING_METHOD = "mpm";
	public static final String RENAME_ENTITIES = "ren";
	public static final String USER_RENAMER_CLASS = "urc";
	public static final String NEW_LINE_SEPARATOR = "nls";
	public static final String INDENT_STRING = "ind";
	public static final String BANNER = "ban";

	public static final String DUMP_ORIGINAL_LINES = "__dump_original_lines__";
	public static final String UNIT_TEST_MODE = "__unit_test_mode__";

	public static final String LINE_SEPARATOR_WIN = "\r\n";
	public static final String LINE_SEPARATOR_UNX = "\n";

	public static final Map<String, Object> DEFAULTS = IFernflowerPreferences.getDefaults( );

	public static Map<String, Object> getDefaults( )
	{
		HashMap<String, Object> defaults = new HashMap<String, Object>( );

		defaults.put( REMOVE_BRIDGE, "1" );
		defaults.put( REMOVE_SYNTHETIC, "0" );
		defaults.put( DECOMPILE_INNER, "1" );
		defaults.put( DECOMPILE_CLASS_1_4, "1" );
		defaults.put( DECOMPILE_ASSERTIONS, "1" );
		defaults.put( HIDE_EMPTY_SUPER, "1" );
		defaults.put( HIDE_DEFAULT_CONSTRUCTOR, "1" );
		defaults.put( DECOMPILE_GENERIC_SIGNATURES, "0" );
		defaults.put( NO_EXCEPTIONS_RETURN, "1" );
		defaults.put( DECOMPILE_ENUM, "1" );
		defaults.put( REMOVE_GET_CLASS_NEW, "1" );
		defaults.put( LITERALS_AS_IS, "0" );
		defaults.put( BOOLEAN_TRUE_ONE, "1" );
		defaults.put( ASCII_STRING_CHARACTERS, "0" );
		defaults.put( SYNTHETIC_NOT_SET, "1" );
		defaults.put( UNDEFINED_PARAM_TYPE_OBJECT, "1" );
		defaults.put( USE_DEBUG_VAR_NAMES, "1" );
		defaults.put( REMOVE_EMPTY_RANGES, "1" );
		defaults.put( FINALLY_DEINLINE, "1" );
		defaults.put( IDEA_NOT_NULL_ANNOTATION, "1" );
		defaults.put( LAMBDA_TO_ANONYMOUS_CLASS, "0" );
		defaults.put( BYTECODE_SOURCE_MAPPING, "0" );

		defaults.put( LOG_LEVEL, IFernflowerLogger.Severity.INFO.name( ) );
		defaults.put( MAX_PROCESSING_METHOD, "0" );
		defaults.put( RENAME_ENTITIES, "0" );
		defaults.put( NEW_LINE_SEPARATOR, ( InterpreterUtil.IS_WINDOWS ? "0" : "1" ) );
		defaults.put( INDENT_STRING, "   " );
		defaults.put( BANNER, "" );
		defaults.put( UNIT_TEST_MODE, "0" );
		defaults.put( DUMP_ORIGINAL_LINES, "0" );

		return Collections.unmodifiableMap( defaults );
	}
}