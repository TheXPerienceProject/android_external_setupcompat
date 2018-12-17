/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.setupcompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import androidx.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import com.google.android.setupcompat.lifecycle.LifecycleFragment;
import com.google.android.setupcompat.template.ButtonFooterMixin;
import com.google.android.setupcompat.template.StatusBarMixin;
import com.google.android.setupcompat.template.SystemNavBarMixin;
import com.google.android.setupcompat.util.WizardManagerHelper;

/** A templatization layout with consistent style used in Setup Wizard or app itself. */
public class PartnerCustomizationLayout extends TemplateLayout {

  private Activity activity;

  public PartnerCustomizationLayout(Context context) {
    this(context, 0, 0);
  }

  public PartnerCustomizationLayout(Context context, int template) {
    this(context, template, 0);
  }

  public PartnerCustomizationLayout(Context context, int template, int containerId) {
    super(context, template, containerId);
    init(null, R.attr.sucLayoutTheme);
  }

  public PartnerCustomizationLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, R.attr.sucLayoutTheme);
  }

  @TargetApi(VERSION_CODES.HONEYCOMB)
  public PartnerCustomizationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, defStyleAttr);
  }

  private void init(AttributeSet attrs, int defStyleAttr) {
    activity = lookupActivityFromContext(getContext());

    boolean isSetupFlow = WizardManagerHelper.isAnySetupWizard(activity.getIntent());
    registerMixin(
        StatusBarMixin.class,
        new StatusBarMixin(
            this,
            activity.getWindow(),
            attrs,
            defStyleAttr,
            /* applyPartnerResources= */ isSetupFlow));
    registerMixin(
        SystemNavBarMixin.class,
        new SystemNavBarMixin(
            this,
            activity.getWindow(),
            attrs,
            defStyleAttr,
            /* applyPartnerResources= */ isSetupFlow));

    TypedArray a =
        getContext()
            .obtainStyledAttributes(
                attrs, R.styleable.SucPartnerCustomizationLayout, defStyleAttr, 0);

    final int primaryBtn =
        a.getResourceId(R.styleable.SucPartnerCustomizationLayout_sucPrimaryFooterButton, 0);
    final int secondaryBtn =
        a.getResourceId(R.styleable.SucPartnerCustomizationLayout_sucSecondaryFooterButton, 0);

    registerMixin(
        ButtonFooterMixin.class,
        new ButtonFooterMixin(
            this, primaryBtn, secondaryBtn, /* applyPartnerResources= */ isSetupFlow));

    final int footer = a.getResourceId(R.styleable.SucPartnerCustomizationLayout_sucFooter, 0);
    if (footer != 0) {
      inflateFooter(footer);
    }

    boolean layoutFullscreen =
        a.getBoolean(R.styleable.SucPartnerCustomizationLayout_sucLayoutFullscreen, true);
    a.recycle();

    if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && layoutFullscreen) {
      setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    // Override the FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, FLAG_TRANSLUCENT_STATUS,
    // FLAG_TRANSLUCENT_NAVIGATION and SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN attributes of window forces
    // showing status bar and navigation bar.
    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
  }

  @Override
  protected View onInflateTemplate(LayoutInflater inflater, int template) {
    if (template == 0) {
      template = R.layout.partner_customization_layout;
    }
    return inflateTemplate(inflater, 0, template);
  }

  @Override
  protected ViewGroup findContainer(int containerId) {
    if (containerId == 0) {
      containerId = R.id.suc_layout_content;
    }
    return super.findContainer(containerId);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    LifecycleFragment.attachNow(lookupActivityFromContext(getContext()));
  }

  private static Activity lookupActivityFromContext(Context context) {
    if (context instanceof Activity) {
      return (Activity) context;
    } else if (context instanceof ContextWrapper) {
      return lookupActivityFromContext(((ContextWrapper) context).getBaseContext());
    } else {
      throw new IllegalArgumentException("Cannot find instance of Activity in parent tree");
    }
  }

  /**
   * Sets the footer of the layout, which is at the bottom of the content area outside the scrolling
   * container. The footer can only be inflated once per instance of this layout.
   *
   * @param footer The layout to be inflated as footer.
   * @return The root of the inflated footer view.
   */
  public View inflateFooter(@LayoutRes int footer) {
    ViewStub footerStub = findManagedViewById(R.id.suc_layout_footer);
    footerStub.setLayoutResource(footer);
    return footerStub.inflate();
  }
}
