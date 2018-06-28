package com.mentions_package.model;

/**
 * Project ID：400YF17051
 * Resume:
 *
 * @author 汪波
 * @version 1.0
 * @since 2017/4/8 汪波 first commit
 */
public class FormatRange extends Range {

  private FormatData convert;
  private String id;

  public FormatRange(int from, int to) {
    super(from, to);
  }

 public FormatRange(int from, int to,String id) {
    super(from, to,id);
  }

  public FormatData getConvert() {
    return convert;
  }

  public interface FormatData {

    CharSequence formatCharSequence();
  }

  private CharSequence rangeCharSequence;

  public void setConvert(FormatData convert) {
    this.convert = convert;
  }

  public CharSequence getRangeCharSequence() {
    return rangeCharSequence;
  }

  public void setRangeCharSequence(CharSequence rangeCharSequence) {
    this.rangeCharSequence = rangeCharSequence;
  }
}
