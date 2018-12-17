/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.setupcompat.logging;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.setupcompat.logging.internal.DefaultSetupMetricsLogger;
import com.google.android.setupcompat.logging.internal.SetupMetricsLoggingConstants.MetricBundleKeys;
import com.google.android.setupcompat.logging.internal.SetupMetricsLoggingConstants.MetricType;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;

/** SetupMetricsLogger provides an easy way to log custom metrics to SetupWizard. */
public class SetupMetricsLogger {

  /** Logs an instance of {@link CustomEvent} to SetupWizard. */
  public static void logCustomEvent(@NonNull Context context, @NonNull CustomEvent customEvent) {
    Preconditions.checkNotNull(context, "Context cannot be null.");
    Preconditions.checkNotNull(customEvent, "CustomEvent cannot be null.");
    Bundle bundle = new Bundle();
    bundle.putParcelable(MetricBundleKeys.CUSTOM_EVENT, customEvent);
    DefaultSetupMetricsLogger.get(context).logEventSafely(MetricType.CUSTOM_EVENT, bundle);
  }

  /** Increments the counter value with the name {@code counterName} by {@code times}. */
  public static void logCounter(
      @NonNull Context context, @NonNull MetricKey counterName, int times) {
    Preconditions.checkNotNull(context, "Context cannot be null.");
    Preconditions.checkNotNull(counterName, "CounterName cannot be null.");
    Preconditions.checkArgument(times > 0, "Counter cannot be negative.");
    Bundle bundle = new Bundle();
    bundle.putParcelable(MetricBundleKeys.METRIC_KEY, counterName);
    bundle.putInt(MetricBundleKeys.COUNTER_INT, times);
    DefaultSetupMetricsLogger.get(context).logEventSafely(MetricType.COUNTER_EVENT, bundle);
  }

  /**
   * Logs the {@link Timer}'s duration by calling {@link #logDuration(Context, MetricKey, long)}.
   */
  public static void logDuration(@NonNull Context context, @NonNull Timer timer) {
    Preconditions.checkNotNull(context, "Context cannot be null.");
    Preconditions.checkNotNull(timer, "Timer cannot be null.");
    Preconditions.checkArgument(
        timer.isStopped(), "Timer should be stopped before calling logDuration.");
    logDuration(
        context, timer.getMetricKey(), TimeUnit.NANOSECONDS.toMillis(timer.getDurationInNanos()));
  }

  /** Logs a duration event to SetupWizard. */
  public static void logDuration(
      @NonNull Context context, @NonNull MetricKey timerName, long timeInMillis) {
    Preconditions.checkNotNull(context, "Context cannot be null.");
    Preconditions.checkNotNull(timerName, "Timer name cannot be null.");
    Preconditions.checkArgument(timeInMillis >= 0, "Duration cannot be negative.");
    Bundle bundle = new Bundle();
    bundle.putParcelable(MetricBundleKeys.METRIC_KEY, timerName);
    bundle.putLong(MetricBundleKeys.TIME_MILLIS_LONG, timeInMillis);
    DefaultSetupMetricsLogger.get(context).logEventSafely(MetricType.DURATION_EVENT, bundle);
  }
}
