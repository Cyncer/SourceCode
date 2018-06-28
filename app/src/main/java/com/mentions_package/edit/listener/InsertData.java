package com.mentions_package.edit.listener;


import com.mentions_package.model.FormatRange;

/**
 * Resume:
 *
 * @author 汪波
 * @version 1.0
 * @since 2017/4/8 汪波 first commit
 */
public interface InsertData {

  CharSequence charSequence();

  FormatRange.FormatData formatData();

  int color();
}
