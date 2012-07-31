/*---------------------------------------------------------------------------*
 * SurveyDroidDB.java                                                        *
 *                                                                           *
 * Sets up the main database using information from the contract.            *
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
package org.survey_droid.survey_droid.content;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.Options;

/**
 * Sets up the main database using information from the contract.
 *
 * @author Diego Vargas
 * @author Vladimir Costescu
 * @author Tony Xiao
 */
public class SurveyDroidDB extends SQLiteOpenHelper
{
    private static final String TAG = "SurveyDroidDB";

    //Change the version number here to force the database to
    //update itself.  This throws out all data.
    private static final String DATABASE_NAME = "sd.db";
    private static final int DATABASE_VERSION = 11;
    
    /** Holds the names of all database tables */
    public static final String[] tables;
    /** Holds the create syntax of all database fields by table (not including _ID) */
    private static final String[][] fields;

    //TODO holy crap error handling...
    static
    {
    	//collect database table info
    	Class<?>[] tableClasses = ProviderContract.class.getDeclaredClasses();
    	List<String> tableNames = new LinkedList<String>();
    	List<List<String>> tableFields = new LinkedList<List<String>>();
    	for (Class<?> table : tableClasses)
    	{
    		for (Class<?> iface : table.getInterfaces())
			{
				if (iface.equals(ProviderContract.DataTableContract.class))
				{
					String name;
					Class<?> fieldsClass = null;
					try
					{
						name = (String) table.getField("NAME").get(null);
					}
					catch (ClassCastException e)
					{
						String msg = "Unable to get name of data table for class " + table.getName() + ": NAME field is not a String";
						Util.e(null, TAG, msg);
						throw new RuntimeException(msg, e);
					}
					catch (NoSuchFieldException e)
					{
						String msg = "Unable to get name of data table for class " + table.getName() + ": NAME field does not exist";
						Util.e(null, TAG, msg);
						throw new RuntimeException(msg, e);
					}
					catch (NullPointerException e)
					{
						String msg = "Unable to get name of data table for class " + table.getName() + ": NAME field is not static";
						Util.e(null, TAG, msg);
						throw new RuntimeException(msg, e);
					}
					catch (IllegalAccessException e)
					{
						String msg = "Unable to get name of data table for class " + table.getName() + ": NAME field is not public";
						Util.e(null, TAG, msg);
						throw new RuntimeException(msg, e);
					}
					
					Class<?>[] subs = table.getDeclaredClasses();
					for (Class<?> sub : subs)
					{
						//if (!sub.getName().equals("Fields")) continue;
						for (Class<?> iface2 : sub.getInterfaces())
						{
							if (iface2.equals(BaseColumns.class))
							{
								fieldsClass = sub;
								break;
							}
						}
						if (fieldsClass != null) break;
					}
					if (fieldsClass == null)
					{
						String msg = "Unable to get fields of data table for class " + table.getName() + ": Fields class does not exist or does not implement BaseColumns";
						Util.e(null, TAG, msg);
						throw new RuntimeException(msg);
					}
					Field[] dbFields = fieldsClass.getDeclaredFields();
					List<String> fieldDeclarations = new LinkedList<String>();
					for (Field dbField : dbFields)
					{
						String fieldDeclaration;
						try
						{
							fieldDeclaration = (String) dbField.get(null);
						}
						catch (ClassCastException e)
						{
							String msg = "Unable to get name of data table field " + dbField.getName() + " from class " + table.getName() + ": field is not a String";
							Util.e(null, TAG, msg);
							throw new RuntimeException(msg, e);
						}
						catch (NullPointerException e)
						{
							String msg = "Unable to get name of data table field " + dbField.getName() + " from class " + table.getName() + ": field is not static";
							Util.e(null, TAG, msg);
							throw new RuntimeException(msg, e);
						}
						catch (IllegalAccessException e)
						{
							String msg = "Unable to get name of data table field " + dbField.getName() + " from class " + table.getName() + ": field is not public";
							Util.e(null, TAG, msg);
							throw new RuntimeException(msg, e);
						}
						Options opts = dbField.getAnnotation(Options.class);
						if (opts == null)
						{
							String msg = "Unable to get options for data table field " + dbField.getName() + " from class " + table.getName() + ": field does not have Options annotation";
							Util.e(null, TAG, msg);
							throw new RuntimeException(msg);
						}
						fieldDeclaration = fieldDeclaration + " " + opts.value();
						fieldDeclarations.add(fieldDeclaration);
					}
					tableNames.add(name);
					tableFields.add(fieldDeclarations);
				}
			}
    	}
    	tables = tableNames.toArray(new String[0]);
    	fields = new String[tableFields.size()][];
    	for (int i = 0; i < tableFields.size(); i++)
    	{
    		fields[i] = tableFields.get(i).toArray(new String[0]);
    	}
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	Util.d(null, TAG, "onCreate");
    	//create the database
        db.beginTransaction();

        try
        {
        	for (int i = 0; i < tables.length; i++)
        	{
        		db.execSQL("DROP TABLE IF EXISTS " + tables[i]);
        	}
        	
        	for (int i = 0; i < tables.length; i++)
        	{
        		StringBuilder sql = new StringBuilder("CREATE TABLE ");
        		sql.append(tables[i]);
        		sql.append(" (");
        		sql.append(BaseColumns._ID);
        		sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        		for (int j = 0; j < fields[i].length; j++)
        		{
        			sql.append(fields[i][j]);
        			if (j == fields[i].length - 1) sql.append(")");
        			else sql.append(", ");
        		}
        		Util.v(null, TAG, "SQL: \"" + sql.toString() + "\"");
        		db.execSQL(sql.toString());
        	}
        	
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
        	String msg = "Failed to create database: invalid SQL";
        	Util.e(null, TAG, msg);
        	throw new RuntimeException(msg, e);
        }
        finally
        {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Util.i(null, TAG, "Upgrading database from version " + oldVersion +
        		" to " + newVersion + ", which will destroy all old data");
        onCreate(db);
    }

    /**
     * Create the database object.
     *
     * @param context - {@link Context} needed for super
     */
    public SurveyDroidDB(Context context)
    {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Util.d(null, TAG, "in constructor");
    }
}
