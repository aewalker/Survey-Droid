/*---------------------------------------------------------------------------*
 * ExtrasDBHandler.java                                                      *
 *                                                                           *
 * Extension of the SurveyDroidDBHandler for extra data items like photos.   *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
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
package org.surveydroid.android.database;

import java.io.ByteArrayOutputStream;

import org.surveydroid.android.Base64Coder;
import org.surveydroid.android.Util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

public class ExtrasDBHandler extends SurveyDroidDBHandler
{
	//for logging
	private static final String TAG = "ExtrasDBHandler";
	
	/**
	 * Create a new ExtrasDBHandler object.
	 * 
	 * @param context
	 */
	public ExtrasDBHandler(Context context)
	{
		super(context);
	}
	
	/**
	 * Write a photo to the database.
	 * 
	 * @param pic - the photo (as a {@link Bitmap})
	 * @param created - timestamp of when the photo was taken
	 * @param hires - if true, be careful about memory usage
	 * 
	 * @return true on success
	 */
	public boolean writePhoto(Bitmap pic, long created, boolean hires)
	{
		Util.d(null, TAG, "writing photo to database");
		
		//first, do go to base 64
		String photo64;
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int qual = 100;
			if (hires) qual = 0;
			pic.compress(Bitmap.CompressFormat.JPEG, qual, baos);
			//memory conservation wizardry
			pic.recycle();
			pic = null;
			byte[] picBytes = baos.toByteArray();
			baos = null;
			char[] photo64chars = Base64Coder.encode(picBytes);
			picBytes = null;
			photo64 = new String(photo64chars);
			photo64chars = null;
		}
		catch (Exception e)
		{
			Util.e(contx, TAG, Util.fmt(e));
			return false;
		}
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(SurveyDroidDB.ExtrasTable.CREATED, created);
		values.put(SurveyDroidDB.ExtrasTable.TYPE,
				SurveyDroidDB.ExtrasTable.PHOTO_TYPE);
		values.put(SurveyDroidDB.ExtrasTable.DATA, photo64);
		photo64 = null;
		//run it
		if (db.insert(SurveyDroidDB.EXTRAS_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
}
