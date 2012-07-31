/*---------------------------------------------------------------------------*
 * Options.java                                                              *
 *                                                                           *
 * Annotation for providing information about database fields.               *
 *---------------------------------------------------------------------------*
 * Copyright (C) 2011-2012 Survey Droid Contributors                         *
 *                                                                           *
 * This file is part of Survey Droid.                                        *
 *                                                                           *
 * Survey Droid is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU General Public License as published by      *
 * the Free Software Foundation, either version 3 of the License, or         *
 * (at your option) any later version.                                       *
 *                                                                           *
 * Survey Droid is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 * GNU General Public License for more details.                              *
 *                                                                           *
 * You should have received a copy of the GNU General Public License         *
 * along with Survey Droid.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package org.survey_droid.survey_droid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for providing information about database fields.
 * 
 * @author Austin Walker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Options
{	
	/** shortcut for "INTEGER UNSIGNED NOT NULL" */
	static final String ID = "INTEGER UNSIGNED NOT NULL";
	
	/** shortcut for "INTEGER UNSIGNED NOT NULL" */
	static final String ENUM = "INTEGER UNSIGNED NOT NULL";
	
	/** shortcut for "INTEGER UNSIGNED NOT NULL DEFAULT 0" */
	static final String BOOL = "INTEGER UNSIGNED NOT NULL DEFAULT 0";
	
	String value();
}
