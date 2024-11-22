package com.teratail.q_v15xaffnxcg4pt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.*;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.*;
import androidx.core.content.res.ResourcesCompat;

import java.io.Serializable;
import java.util.Arrays;

public class TimetableView extends View {
  public TimetableView(Context context) {
    this(context, null);
  }

  public TimetableView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TimetableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public TimetableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimetableView, defStyleAttr, defStyleRes);
    setFontFamily(ta.getResourceId(R.styleable.TimetableView_android_fontFamily, 0));
    setTextSizePx(ta.getDimensionPixelSize(R.styleable.TimetableView_android_textSize, 20));
    setLength(ta.getInt(R.styleable.TimetableView_length, 24));
    setReservedColor(ta.getColor(R.styleable.TimetableView_reservedColor, Color.RED));
    setVacantColor(ta.getColor(R.styleable.TimetableView_vacantColor, Color.WHITE));
    ta.recycle();
  }

  private boolean[] reserveStates = new boolean[10];
  private @FontRes int fontFamily;
  private Typeface typeface;
  private int textSizePx;
  private @ColorInt int reservedColor, vacantColor;
  private LabelSupplier labelSupplier;

  void setFontFamily(@FontRes int fontFamily) {
    this.fontFamily = fontFamily;
    typeface = fontFamily == 0 ? null : ResourcesCompat.getFont(getContext(), fontFamily);
    invalidate();
  }
  @FontRes int getFontFamily() {
    return fontFamily;
  }

  void setTextSizePx(int textSizePx) {
    this.textSizePx = textSizePx;
    invalidate();
  }
  int getTextSizePx() {
    return textSizePx;
  }

  void setLabelSupplier(LabelSupplier labelSupplier) {
    this.labelSupplier = labelSupplier;
    invalidate();
  }
  LabelSupplier getLabelSupplier() {
    return labelSupplier;
  }

  void setLength(int length) {
    reserveStates = Arrays.copyOf(reserveStates, length);
    invalidate();
  }
  int getLength() {
    return reserveStates.length;
  }

  void setReservedColor(@ColorInt int color) {
    reservedColor = color;
    invalidate();
  }
  @ColorInt int getReservedColor() {
    return reservedColor;
  }

  void setVacantColor(@ColorInt int color) {
    vacantColor = color;
    invalidate();
  }
  @ColorInt int getVacantColor() {
    return vacantColor;
  }

  void setReserveState(int no, boolean state) {
    reserveStates[no] = state;
    invalidate();
  }
  boolean getReserveState(int no) {
    return reserveStates[no];
  }
  void clear() {
    Arrays.fill(reserveStates, false);
    invalidate();
  }

  private static final PathEffect dots = new DashPathEffect(new float[]{ 10.0f, 10.0f }, 0);

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if(canvas == null || canvas.getWidth() <= 0 || canvas.getHeight() <= 0) return;

    Paint paint = new Paint();
    paint.setTypeface(typeface);
    paint.setTextSize(textSizePx);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(2);

    float textHeight = paint.descent() - paint.ascent();
    float textBaseline = getPaddingTop() - paint.ascent();
    float rectTop = getPaddingTop() + textHeight;
    float rectBottom = canvas.getHeight() - (getPaddingTop() + getPaddingBottom());

    float right = 0;
    for(int i=0; i<reserveStates.length; i++) {
      float left = i==0 ? getX(canvas,i) : right;
      right = getX(canvas, i+1);
      paint.setColor(reserveStates[i] ? reservedColor : vacantColor);
      canvas.drawRect(left, rectTop, right, rectBottom, paint);

      paint.setColor(Color.BLACK);
      String label = labelSupplier == null ? null : labelSupplier.getLabel(i);
      boolean isLabeling = false;
      if(label != null && !label.isEmpty()) {
        canvas.drawText(label, left, textBaseline, paint);
        isLabeling = true;
      }

      paint.setColor(Color.BLACK);
      paint.setPathEffect(i%2==0 ? null : dots);
      canvas.drawLine(left, isLabeling?getPaddingTop():rectTop, left ,rectBottom, paint);
    }
  }

  private float getX(Canvas canvas, int i) {
    return getPaddingLeft() + (float)((canvas.getWidth()-(getPaddingLeft()+getPaddingRight())) * i) / reserveStates.length;
  }

  public interface LabelSupplier extends Serializable {
    String getLabel(int no);
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Parcelable parent = super.onSaveInstanceState();
    SavedState saved = new SavedState(parent);
    saved.reserveStates = reserveStates;
    saved.fontFamily = fontFamily;
    saved.textSizePx = textSizePx;
    saved.reservedColor = reservedColor;
    saved.vacantColor = vacantColor;
    return saved;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    SavedState saved = (SavedState) state;
    super.onRestoreInstanceState(saved.getSuperState());
    reserveStates = saved.reserveStates;
    setFontFamily(saved.fontFamily);
    setTextSizePx(saved.textSizePx);
    setReservedColor(saved.reservedColor);
    setVacantColor(saved.vacantColor);
  }

  private static class SavedState extends BaseSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };

    public boolean[] reserveStates;
    public @FontRes int fontFamily;
    public int textSizePx;
    public @ColorInt int reservedColor, vacantColor;

    public SavedState(Parcel in) {
      super(in);
      int reserveCount = in.readInt();
      reserveStates = new boolean[reserveCount];
      in.readBooleanArray(reserveStates);
      fontFamily = in.readInt();
      textSizePx = in.readInt();
      reservedColor = in.readInt();
      vacantColor = in.readInt();
    }

    public SavedState(Parcelable superState) {
      super(superState);
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(reserveStates.length);
      out.writeBooleanArray(reserveStates);
      out.writeInt(fontFamily);
      out.writeInt(textSizePx);
      out.writeInt(reservedColor);
      out.writeInt(vacantColor);
    }
  }
}
