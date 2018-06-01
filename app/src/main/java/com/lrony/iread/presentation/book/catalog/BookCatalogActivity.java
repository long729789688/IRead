package com.lrony.iread.presentation.book.catalog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.classic.common.MultipleStatusView;
import com.lrony.iread.AppManager;
import com.lrony.iread.R;
import com.lrony.iread.model.bean.BookChapterBean;
import com.lrony.iread.model.bean.CollBookBean;
import com.lrony.iread.model.bean.packages.BookChapterPackage;
import com.lrony.iread.model.db.DBManger;
import com.lrony.iread.mvp.MvpActivity;
import com.lrony.iread.presentation.read.ReadActivity;
import com.lrony.iread.ui.help.RecyclerViewItemDecoration;
import com.lrony.iread.util.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuxiaobin on 18-6-1.
 */

public class BookCatalogActivity extends MvpActivity<BookCatalogContract.Presenter> implements BookCatalogContract.View, SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "BookCatalogActivity";
    private static final String K_EXTRA_BOOK = "book";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout mRefreshView;
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mStatusView;

    private String mBookId;
    private LinearLayoutManager mLinearLayoutManager;

    private BookCatalogAdapter mAdapter;

    private ArrayList<BookChapterBean> mBookBean = new ArrayList<>();
    private CollBookBean mCollBookBean;

    private boolean mIsReverserder = false;

    public static Intent newIntent(Context context, String bookid) {
        Intent intent = new Intent(context, BookCatalogActivity.class);
        intent.putExtra(K_EXTRA_BOOK, bookid);
        return intent;
    }


    @Override
    public void loading() {
        super.loading();
        mStatusView.showLoading();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_catalog);
        ButterKnife.bind(this);

        mBookId = getIntent().getStringExtra(K_EXTRA_BOOK);
        KLog.d(TAG, "mBookId: " + mBookId);

        getPresenter().start();
        initView();
        initListener();
        getPresenter().loadBookInfo(true, mBookId);
    }

    private void initListener() {
        KLog.d(TAG, "initListener");

        mRefreshView.setOnRefreshListener(this);

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            showToast(mBookBean.get(position).getTitle());
            ReadActivity.startActivity(this, mCollBookBean, true, position+1);
        }));

        mStatusView.setOnRetryClickListener((v ->
                getPresenter().loadBookInfo(true, mBookId)
        ));

    }

    private void initView() {
        KLog.d(TAG, "initView");
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(
                new RecyclerViewItemDecoration.Builder(this)
                        .color(ContextCompat.getColor(this, R.color.colorDivider))
                        .thickness(1)
                        .create());

        mRefreshView.setColorSchemeResources(R.color.colorAccent);

        mAdapter = new BookCatalogAdapter(mBookBean);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setRecyclerReverserder(boolean arg) {
        KLog.d(TAG, "setRecyclerReverserder");
        if (mLinearLayoutManager != null) {
            mLinearLayoutManager.setStackFromEnd(arg);
            mLinearLayoutManager.setReverseLayout(arg);
        } else {
            KLog.d(TAG, "setRecyclerReverserder mLinearLayoutManager is null");
        }

    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        getPresenter().loadBookInfo(false, mBookId);
    }

    @Override
    public void finshLoadBookInfo(List<BookChapterBean> bean) {
        KLog.d(TAG, "finshLoadBookInfo");
        if (null == bean) {
            AppManager.getInstance().finishActivity(this);
        }
        mCollBookBean = DBManger.getInstance().loadBookTbById(mBookId);
        mBookBean.clear();
        mBookBean.addAll(bean);
        mAdapter.notifyDataSetChanged();

    }

    @NonNull
    @Override
    public BookCatalogContract.Presenter createPresenter() {
        return new BookCatalogPresenter();
    }

    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_book_catalog_swich:
                if (mIsReverserder) {
                    setRecyclerReverserder(false);
                    mIsReverserder = false;
                } else {
                    setRecyclerReverserder(true);
                    mIsReverserder = true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void error() {
        super.error();
        mStatusView.showError();
    }

    @Override
    public void empty() {
        super.empty();
        mStatusView.showEmpty();
    }

    @Override
    public void complete() {
        super.complete();
        mStatusView.showContent();
        mRefreshView.setRefreshing(false);

        if (mBookBean.size() <= 0) mStatusView.showEmpty();
    }

}
