package com.dxys.demo.bingo.TabPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dxys.demo.bingo.R;
import com.dxys.demo.bingo.altaotu.ImageGroup;
import com.dxys.demo.bingo.altaotu.ImageGroupMannager;
import com.dxys.demo.bingo.altaotu.MyOkHttp;
import com.dxys.demo.bingo.altaotu.TabIfoManager;
import com.dxys.demo.bingo.net.NetUtils;
import com.dxys.demo.bingo.view.LoadMoreView;

import java.util.ArrayList;

import static com.dxys.demo.bingo.Utlis.log;


public class ImageGroupFg extends Fragment {


    private MyHandler myHandler;
    private int tabIndex;
    private int firstLoad = 0;
    private ImageGroupMannager imageGroupMannager;
    private ArrayList<ImageGroup> imageGroups;
    private SwipeRefreshLayout swipeRefreshlayout;
    private MyRecyclerAdapter myRecyclerAdapter;
    private LoadMoreView loadMoreView;
    private int hestorySize = 0;
    private boolean hasCreat = false;
    private static Context context;

    public ImageGroupFg(){
    }

    public static ImageGroupFg newInstance(Context icontext,int index)
    {
        context = icontext;
        ImageGroupFg imageGroupFg = new ImageGroupFg();
        Bundle bundle = new Bundle();
        bundle.putInt("index",index);
        imageGroupFg.setArguments(bundle);
        imageGroupFg.onCreate(null);
        return imageGroupFg;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && !hasCreat)
        {
            tabIndex = getArguments().getInt("index");
            myHandler = new MyHandler(context.getMainLooper());
            imageGroupMannager = new ImageGroupMannager(TabIfoManager.getTabIfo(tabIndex).tabUrl);
            imageGroups = imageGroupMannager.getImageGroups();
            imageGroupMannager.setOnImageGroupSizeChangeListener(new ImageGroupMannager.OnImageGroupSizeChangeListener() {
                @Override
                public void onChanged(int page) {
                    Message message = new Message();
                    message.what = 100;
                    message.obj = page;
                    myHandler.sendMessage(message);
                }

                @Override
                public void onEnd() {
                    Message message = new Message();
                    message.what = 101;
                    myHandler.sendMessage(message);
                }

                @Override
                public void onLoadMore() {
                    Message message = new Message();
                    message.what = 102;
                    myHandler.sendMessage(message);
                }

                @Override
                public void onRefreshed() {
                    Message message = new Message();
                    message.what = 103;
                    myHandler.sendMessage(message);
                }
            });
            hasCreat = true;
        }
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100: {
                    if (imageGroups.size()-hestorySize > 3)
                    {
                        swipeRefreshlayout.setRefreshing(false);
                        loadMoreView.stop();
                    }
                    imageGroups = imageGroupMannager.getImageGroups();
                    myRecyclerAdapter.updata();
                    break;
                }
                case 101: {
                    loadMoreView.stop();
                    Toast.makeText(context, "没有更多了！", Toast.LENGTH_SHORT).show();
                    swipeRefreshlayout.setRefreshing(false);
                    break;
                }
                case 102:{
                    loadMoreView.show();
                    hestorySize = imageGroups.size();
                    break;
                }
                case 103:{
                    loadMoreView.stop();
                    swipeRefreshlayout.setRefreshing(false);
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        log("fg: onCreatView");
        View viewRoot = inflater.inflate(R.layout.fragment_mzitu_fg, container, false);
        initview(viewRoot);
        return viewRoot;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        log("set usr visible " + isVisibleToUser + "  pageIndex:" + tabIndex + "  firstload:" + firstLoad);

        if (isVisibleToUser && firstLoad == 0) {
            if (NetUtils.isNetworkAvailable(context))
            {
                imageGroupMannager.refreshAll();

                firstLoad = 1;
                if (swipeRefreshlayout != null)
                    swipeRefreshlayout.setRefreshing(true);
                else
                    firstLoad = 2;
            }else
            {
                Toast.makeText(context, "无网络连接！", Toast.LENGTH_SHORT).show();
            }
        }
        if (!isVisibleToUser) {
            if (swipeRefreshlayout != null)
                swipeRefreshlayout.setRefreshing(false);
            MyOkHttp myOkHttp = MyOkHttp.getInstence();
            myOkHttp.cancleAllRequest(TabIfoManager.getTabIfo(tabIndex).tabUrl);
            firstLoad = 0;
            if (imageGroupMannager != null)
            imageGroupMannager.setlocked(true);
        }
    }

    private void initview(View rootView) {
        swipeRefreshlayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_efresh_layout);
        final RecyclerView recyclerview = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerview.setItemAnimator(new NoAlphaItemAnimator());
        myRecyclerAdapter = new MyRecyclerAdapter(context, imageGroups);
        recyclerview.setAdapter(myRecyclerAdapter);
        swipeRefreshlayout.setOnRefreshListener(new MyOnRefreshListener());
        if (firstLoad == 2) {
            swipeRefreshlayout.setRefreshing(true);
            firstLoad = 3;
        }
        loadMoreView = (LoadMoreView)rootView.findViewById(R.id.load_more_view);
        final int[] colors = {getResources().getColor(R.color.colorArray1),getResources().getColor(R.color.colorArray2),getResources().getColor(R.color.colorArray3)};
        loadMoreView.setColors(colors);
        swipeRefreshlayout.setColorSchemeColors(colors);
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) recyclerview.getLayoutManager();
                int totalItemCount = recyclerview.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPositions(null)[0];
                int visibleItemCount = recyclerview.getChildCount();
                if (totalItemCount > 4 && lastVisibleItemPosition > totalItemCount - 4)
                {
                    if (NetUtils.isNetworkAvailable(context))
                        imageGroupMannager.addMore();
                    else
                        Toast.makeText(context, "无网络连接!无法加载更多！", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            if (NetUtils.isNetworkAvailable(context))
            imageGroupMannager.refreshAll();
            else
            {
                Toast.makeText(context, "无网络连接!", Toast.LENGTH_SHORT).show();
                swipeRefreshlayout.setRefreshing(false);
            }
        }
    }


}
