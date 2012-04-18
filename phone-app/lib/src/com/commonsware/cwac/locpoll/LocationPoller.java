/***
  Copyright (c) 2010 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.commonsware.cwac.locpoll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BroadcastReceiver to be launched by AlarmManager. Simply
 * passes the work over to LocationPollerService, who arranges
 * to make sure the WakeLock stuff is done properly.
 */
public class LocationPoller extends BroadcastReceiver {
  public static final String EXTRA_ERROR="com.commonsware.cwac.locpoll.EXTRA_ERROR";
  public static final String EXTRA_INTENT="com.commonsware.cwac.locpoll.EXTRA_INTENT";
  public static final String EXTRA_LOCATION="com.commonsware.cwac.locpoll.EXTRA_LOCATION";
  public static final String EXTRA_PROVIDER="com.commonsware.cwac.locpoll.EXTRA_PROVIDER";
  public static final String EXTRA_LASTKNOWN="com.commonsware.cwac.locpoll.EXTRA_LASTKNOWN";

  /**
    * Standard entry point for a BroadcastReceiver. Delegates
    * the event to LocationPollerService for processing.
    */
  @Override
  public void onReceive(Context context, Intent intent) {
    LocationPollerService.requestLocation(context, intent);
  }
}
