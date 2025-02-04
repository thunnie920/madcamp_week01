// Generated by view binder compiler. Do not edit!
package com.example.week01_project2.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.week01_project2.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CameraBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final FrameLayout bottomSheet;

  @NonNull
  public final Button detailsButton;

  @NonNull
  public final ImageView imageView;

  private CameraBinding(@NonNull FrameLayout rootView, @NonNull FrameLayout bottomSheet,
      @NonNull Button detailsButton, @NonNull ImageView imageView) {
    this.rootView = rootView;
    this.bottomSheet = bottomSheet;
    this.detailsButton = detailsButton;
    this.imageView = imageView;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CameraBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CameraBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.camera, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CameraBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      FrameLayout bottomSheet = (FrameLayout) rootView;

      id = R.id.detailsButton;
      Button detailsButton = ViewBindings.findChildViewById(rootView, id);
      if (detailsButton == null) {
        break missingId;
      }

      id = R.id.imageView;
      ImageView imageView = ViewBindings.findChildViewById(rootView, id);
      if (imageView == null) {
        break missingId;
      }

      return new CameraBinding((FrameLayout) rootView, bottomSheet, detailsButton, imageView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
