package com.mentions_package.edit.util;

import android.util.Log;

import com.mentions_package.model.FormatRange;
import com.mentions_package.model.Range;
import com.mentions_package.model.Tag;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Resume:
 *
 * @author 汪波
 * @version 1.0
 * @since 2017/4/8 汪波 first commit
 */
public class FormatRangeManager extends RangeManager {

    private static final String TAG = "FormatRangeManager";

    public CharSequence getFormatCharSequence(String text) {
        if (isEmpty()) {
            return text;
        }

        int lastRangeTo = 0;
        ArrayList<? extends Range> ranges = get();
        Collections.sort(ranges);

        StringBuilder builder = new StringBuilder("");
        CharSequence newChar;
        for (Range range : ranges) {
            if (range instanceof FormatRange) {
                FormatRange formatRange = (FormatRange) range;
                FormatRange.FormatData convert = formatRange.getConvert();
                newChar = convert.formatCharSequence();
                builder.append(text.substring(lastRangeTo, range.getFrom()));
                builder.append(newChar);
                lastRangeTo = range.getTo();

//                Log.i(TAG, "getFormatCharSequence: lastRangeTo = " + lastRangeTo);
//                Log.i(TAG, "getFormatCharSequence: lastRangeFrom = " + range.getFrom());
            }
        }

        builder.append(text.substring(lastRangeTo));
        return builder.toString();
    }

    public ArrayList<Tag> getTagSequence(String text) {

        ArrayList<Tag> mTaglist = new ArrayList<>();
        if (isEmpty()) {
            return new ArrayList<>();
        }

        int lastRangeTo = 0;
        ArrayList<? extends Range> ranges = get();
        Collections.sort(ranges);

        StringBuilder builder = new StringBuilder("");
        CharSequence newChar;
        for (Range range : ranges) {
            if (range instanceof FormatRange) {
                FormatRange formatRange = (FormatRange) range;
                FormatRange.FormatData convert = formatRange.getConvert();
                newChar = convert.formatCharSequence();

                String name = text.substring(lastRangeTo, range.getFrom());
                builder.append(name);
                builder.append(newChar);

                String id = range.getId(), nameP = "";
                int startIndex, length;

                startIndex = range.getFrom();
                length = range.getTo() - range.getFrom();
                nameP = text.substring(startIndex, startIndex + length);
                nameP=nameP.substring(1);
                lastRangeTo = range.getTo();

                Tag mTag = new Tag(id, nameP.trim(), startIndex, length);
                mTaglist.add(mTag);
                Log.i(TAG, "getFormatCharSequence: startIndex = " + mTag.startIndex);
                Log.i(TAG, "getFormatCharSequence: length = " + mTag.length);
                Log.i(TAG, "getFormatCharSequence: name = " + mTag.name.trim());
                Log.i(TAG, "getFormatCharSequence: id = " + mTag.id);

            }
        }

        builder.append(text.substring(lastRangeTo));
        return mTaglist;
    }

    public ArrayList<String> getTagSequenceId(String text) {

        ArrayList<String> mTaglist = new ArrayList<>();
        if (isEmpty()) {
            return new ArrayList<>();
        }

        int lastRangeTo = 0;
        ArrayList<? extends Range> ranges = get();
        Collections.sort(ranges);

        StringBuilder builder = new StringBuilder("");
        CharSequence newChar;
        for (Range range : ranges) {
            if (range instanceof FormatRange) {
                try {
                    FormatRange formatRange = (FormatRange) range;
                    FormatRange.FormatData convert = formatRange.getConvert();
                    newChar = convert.formatCharSequence();

                    String name = text.substring(lastRangeTo, range.getFrom());
                    builder.append(name);
                    builder.append(newChar);

                    String id = range.getId(), nameP = "";
                    int startIndex, length;

                    startIndex = range.getFrom();
                    length = range.getTo() - range.getFrom();
                    nameP = text.substring(startIndex, startIndex + length);
                    nameP = nameP.substring(1);
                    lastRangeTo = range.getTo();

                    Tag mTag = new Tag(id, nameP.trim(), startIndex, length);
                    mTaglist.add(id);
                    Log.i(TAG, "getFormatCharSequence: startIndex = " + mTag.startIndex);
                    Log.i(TAG, "getFormatCharSequence: length = " + mTag.length);
                    Log.i(TAG, "getFormatCharSequence: name = " + mTag.name.trim());
                    Log.i(TAG, "getFormatCharSequence: id = " + mTag.id);
                }catch (StringIndexOutOfBoundsException e)
                {

                }

            }
        }

        builder.append(text.substring(lastRangeTo));
        return mTaglist;
    }
}
