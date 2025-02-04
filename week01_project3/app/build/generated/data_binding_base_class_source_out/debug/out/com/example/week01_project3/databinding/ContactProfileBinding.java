// Generated by view binder compiler. Do not edit!
package com.example.week01_project3.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.week01_project3.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ContactProfileBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout bboxInfo;

  @NonNull
  public final ImageView bgImage;

  @NonNull
  public final ImageButton buttonCall;

  @NonNull
  public final ImageButton buttonText;

  @NonNull
  public final ImageButton buttonVideo;

  @NonNull
  public final CardView cardView;

  @NonNull
  public final ConstraintLayout constraintLayout;

  @NonNull
  public final ImageView contactImage;

  @NonNull
  public final TextView contactInfo;

  @NonNull
  public final ImageView hotNupjuk;

  @NonNull
  public final TextView infoBday;

  @NonNull
  public final TextView infoEmail;

  @NonNull
  public final TextView infoName;

  @NonNull
  public final TextView infoTelNum;

  @NonNull
  public final TextView titleBday;

  @NonNull
  public final TextView titleEmail;

  @NonNull
  public final TextView titleName;

  @NonNull
  public final TextView titleTelNum;

  private ContactProfileBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout bboxInfo, @NonNull ImageView bgImage,
      @NonNull ImageButton buttonCall, @NonNull ImageButton buttonText,
      @NonNull ImageButton buttonVideo, @NonNull CardView cardView,
      @NonNull ConstraintLayout constraintLayout, @NonNull ImageView contactImage,
      @NonNull TextView contactInfo, @NonNull ImageView hotNupjuk, @NonNull TextView infoBday,
      @NonNull TextView infoEmail, @NonNull TextView infoName, @NonNull TextView infoTelNum,
      @NonNull TextView titleBday, @NonNull TextView titleEmail, @NonNull TextView titleName,
      @NonNull TextView titleTelNum) {
    this.rootView = rootView;
    this.bboxInfo = bboxInfo;
    this.bgImage = bgImage;
    this.buttonCall = buttonCall;
    this.buttonText = buttonText;
    this.buttonVideo = buttonVideo;
    this.cardView = cardView;
    this.constraintLayout = constraintLayout;
    this.contactImage = contactImage;
    this.contactInfo = contactInfo;
    this.hotNupjuk = hotNupjuk;
    this.infoBday = infoBday;
    this.infoEmail = infoEmail;
    this.infoName = infoName;
    this.infoTelNum = infoTelNum;
    this.titleBday = titleBday;
    this.titleEmail = titleEmail;
    this.titleName = titleName;
    this.titleTelNum = titleTelNum;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ContactProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ContactProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.contact_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ContactProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bbox_info;
      ConstraintLayout bboxInfo = ViewBindings.findChildViewById(rootView, id);
      if (bboxInfo == null) {
        break missingId;
      }

      id = R.id.bg_image;
      ImageView bgImage = ViewBindings.findChildViewById(rootView, id);
      if (bgImage == null) {
        break missingId;
      }

      id = R.id.button_call;
      ImageButton buttonCall = ViewBindings.findChildViewById(rootView, id);
      if (buttonCall == null) {
        break missingId;
      }

      id = R.id.button_text;
      ImageButton buttonText = ViewBindings.findChildViewById(rootView, id);
      if (buttonText == null) {
        break missingId;
      }

      id = R.id.button_video;
      ImageButton buttonVideo = ViewBindings.findChildViewById(rootView, id);
      if (buttonVideo == null) {
        break missingId;
      }

      id = R.id.cardView;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      id = R.id.constraintLayout;
      ConstraintLayout constraintLayout = ViewBindings.findChildViewById(rootView, id);
      if (constraintLayout == null) {
        break missingId;
      }

      id = R.id.contact_image;
      ImageView contactImage = ViewBindings.findChildViewById(rootView, id);
      if (contactImage == null) {
        break missingId;
      }

      id = R.id.contact_info;
      TextView contactInfo = ViewBindings.findChildViewById(rootView, id);
      if (contactInfo == null) {
        break missingId;
      }

      id = R.id.hot_nupjuk;
      ImageView hotNupjuk = ViewBindings.findChildViewById(rootView, id);
      if (hotNupjuk == null) {
        break missingId;
      }

      id = R.id.info_bday;
      TextView infoBday = ViewBindings.findChildViewById(rootView, id);
      if (infoBday == null) {
        break missingId;
      }

      id = R.id.info_email;
      TextView infoEmail = ViewBindings.findChildViewById(rootView, id);
      if (infoEmail == null) {
        break missingId;
      }

      id = R.id.info_name;
      TextView infoName = ViewBindings.findChildViewById(rootView, id);
      if (infoName == null) {
        break missingId;
      }

      id = R.id.info_telNum;
      TextView infoTelNum = ViewBindings.findChildViewById(rootView, id);
      if (infoTelNum == null) {
        break missingId;
      }

      id = R.id.title_bday;
      TextView titleBday = ViewBindings.findChildViewById(rootView, id);
      if (titleBday == null) {
        break missingId;
      }

      id = R.id.title_email;
      TextView titleEmail = ViewBindings.findChildViewById(rootView, id);
      if (titleEmail == null) {
        break missingId;
      }

      id = R.id.title_name;
      TextView titleName = ViewBindings.findChildViewById(rootView, id);
      if (titleName == null) {
        break missingId;
      }

      id = R.id.title_telNum;
      TextView titleTelNum = ViewBindings.findChildViewById(rootView, id);
      if (titleTelNum == null) {
        break missingId;
      }

      return new ContactProfileBinding((ConstraintLayout) rootView, bboxInfo, bgImage, buttonCall,
          buttonText, buttonVideo, cardView, constraintLayout, contactImage, contactInfo, hotNupjuk,
          infoBday, infoEmail, infoName, infoTelNum, titleBday, titleEmail, titleName, titleTelNum);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
