// Generated by view binder compiler. Do not edit!
package com.example.week01_project3.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.week01_project3.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FanBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout fanLayout;

  @NonNull
  public final ImageView fanfan;

  @NonNull
  public final ImageView hotNupjuk;

  @NonNull
  public final ImageButton speedUpButton;

  @NonNull
  public final ImageButton talkButton;

  private FanBinding(@NonNull ConstraintLayout rootView, @NonNull ConstraintLayout fanLayout,
      @NonNull ImageView fanfan, @NonNull ImageView hotNupjuk, @NonNull ImageButton speedUpButton,
      @NonNull ImageButton talkButton) {
    this.rootView = rootView;
    this.fanLayout = fanLayout;
    this.fanfan = fanfan;
    this.hotNupjuk = hotNupjuk;
    this.speedUpButton = speedUpButton;
    this.talkButton = talkButton;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FanBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FanBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.fan, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FanBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout fanLayout = (ConstraintLayout) rootView;

      id = R.id.fanfan;
      ImageView fanfan = ViewBindings.findChildViewById(rootView, id);
      if (fanfan == null) {
        break missingId;
      }

      id = R.id.hot_nupjuk;
      ImageView hotNupjuk = ViewBindings.findChildViewById(rootView, id);
      if (hotNupjuk == null) {
        break missingId;
      }

      id = R.id.speedUpButton;
      ImageButton speedUpButton = ViewBindings.findChildViewById(rootView, id);
      if (speedUpButton == null) {
        break missingId;
      }

      id = R.id.talkButton;
      ImageButton talkButton = ViewBindings.findChildViewById(rootView, id);
      if (talkButton == null) {
        break missingId;
      }

      return new FanBinding((ConstraintLayout) rootView, fanLayout, fanfan, hotNupjuk,
          speedUpButton, talkButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
