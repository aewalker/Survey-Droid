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

import android.os.HandlerThread;
import android.os.PowerManager;
import android.util.Log;

/**
 * HandlerThread that unlocks itself when its work is
 * complete. Used in conjunction with a WakeLock to
 * accomplish this end.
 */
public class WakefulThread extends HandlerThread {
  private PowerManager.WakeLock lock=null;

  /**
   * Constructor
   * 
   * @param lock
   *          Already-acquired WakeLock to be released when
   *          work done
   * @param name
   *          Name to supply to HandlerThread
   */
  WakefulThread(PowerManager.WakeLock lock, String name) {
    super(name);

    this.lock=lock;
  }

  /**
   * Override this method if you want to do something before
   * looping begins
   */
  protected void onPreExecute() {
    // no-op by default
  }

  /**
   * Override this method if you want to do something before
   * the WakeLock is released when the thread's work is done
   * or if an unhandled exception is raised while the thread
   * runs
   */
  protected void onPostExecute() {
    if (lock.isHeld()) {
      lock.release();
    }

    if (!lock.isHeld()) {
      onUnlocked();
    }
  }

  /**
   * Override this method if you want to do something when
   * the WakeLock is fully unlocked (e.g., shut down a
   * service)
   */
  protected void onUnlocked() {
    // no-op by default
  }

  @Override
  protected void onLooperPrepared() {
    try {
      onPreExecute();
    }
    catch (RuntimeException e) {
      Log.e("WakefulThread", "Exception onLooperPrepared()", e);
      onPostExecute();
      throw(e);
    }
  }

  @Override
  public void run() {
    try {
      super.run();
    }
    finally {
      onPostExecute();
    }
  }
}
