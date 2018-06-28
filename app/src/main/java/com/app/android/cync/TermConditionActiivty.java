package com.app.android.cync;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.joanzapata.pdfview.listener.OnPageChangeListener;

/**
 * Created by ketul.patel on 18/1/16.
 */
public class TermConditionActiivty extends Activity {
    com.joanzapata.pdfview.PDFView pdfViewPager;
    private ImageView ivBack;
    public static final String SAMPLE_FILE = "cynctc.pdf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_termscondition);
        initView();
    }

    private void initView() {
        pdfViewPager = (com.joanzapata.pdfview.PDFView) findViewById(R.id.pdfViewPagerZoom);
        pdfViewPager.fromAsset(SAMPLE_FILE)
                .defaultPage(1)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {

                    }
                })
                .load();
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
    }
}