package com.lrony.iread.model.remote;

import com.lrony.iread.model.bean.BookDetailBean;
import com.lrony.iread.model.bean.BookDetailRecommendBookBean;
import com.lrony.iread.model.bean.SortBookBean;
import com.lrony.iread.model.bean.packages.RecommendBookPackage;
import com.lrony.iread.model.bean.packages.SearchBookPackage;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;

/**
 * Created by Lrony on 18-5-22.
 */
public class RemoteRepository {

    private static final String TAG = "RemoteRepository";

    private static RemoteRepository sInstance;
    private Retrofit mRetrofit;
    private BookApi mBookApi;

    private RemoteRepository() {
        mRetrofit = RemoteHelper.getInstance()
                .getRetrofit();

        mBookApi = mRetrofit.create(BookApi.class);
    }

    public static RemoteRepository getInstance() {
        if (sInstance == null) {
            synchronized (RemoteHelper.class) {
                if (sInstance == null) {
                    sInstance = new RemoteRepository();
                }
            }
        }
        return sInstance;
    }

    /**
     * 搜索关键字
     *
     * @param query
     * @return
     */
    public Single<List<String>> getKeyWords(String query) {
        return mBookApi.getKeyWordPacakge(query)
                .map(bean -> bean.getKeywords());

    }

    /**
     * 查询书籍
     *
     * @param query:书名|作者名
     * @return
     */
    public Single<List<SearchBookPackage.BooksBean>> getSearchBooks(String query) {
        return mBookApi.getSearchBookPackage(query)
                .map(bean -> bean.getBooks());
    }

    /**
     * 查询书籍详情
     *
     * @param bookId:书籍ID
     * @return
     */
    public Single<BookDetailBean> getBookDetail(String bookId) {
        return mBookApi.getBookDetail(bookId);
    }

    /**
     * 获取书籍推荐书籍
     *
     * @param bookId
     * @return
     */
    public Single<List<BookDetailRecommendBookBean>> getDetailRecommendBookPackage(String bookId) {
        return mBookApi.getDetailRecommendBookPackage(bookId)
                .map(bean -> bean.getBooks());
    }

    /**
     * 按分类获取书籍列表
     *
     * @param gender
     * @param type
     * @param major
     * @param minor
     * @param start
     * @param limit
     * @return
     */
    public Single<List<SortBookBean>> getSortBookPackage(String gender, String type, String major, String minor, int start, int limit) {
        return mBookApi.getSortBookPackage(gender, type, major, minor, start, limit)
                .map(bean -> bean.getBooks());
    }

}
