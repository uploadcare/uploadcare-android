package com.uploadcare.android.widget.fragment;

import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.adapter.FilesAdapter;
import com.uploadcare.android.widget.adapter.FilesGridAdapter;
import com.uploadcare.android.widget.adapter.FilesLinearAdapter;
import com.uploadcare.android.widget.controller.UploadcareWidget;
import com.uploadcare.android.widget.data.Action;
import com.uploadcare.android.widget.data.Chunk;
import com.uploadcare.android.widget.data.ChunkResponse;
import com.uploadcare.android.widget.data.SocialSource;
import com.uploadcare.android.widget.data.Thing;
import com.uploadcare.android.widget.interfaces.ItemTapListener;
import com.uploadcare.android.widget.utils.RecyclerViewOnScrollListener;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UploadcareFilesFragment extends Fragment implements ItemTapListener, SearchView.OnQueryTextListener, View.OnClickListener {

    public interface OnFileActionsListener {

        public void onError(String message);

        public void onFileSelected(String fileUrl);

        public void onAuthorizationNeeded(ChunkResponse chunkResponse);

        public void onChunkSelected(List<Chunk> chunks, String title);

        public int currentRootChunk();
    }

    private SocialSource mSocialSource;

    private List<Chunk> mChunks;

    private CircularProgressBar mCircularProgressBar;

    private RecyclerView mRecyclerView;

    private View mLoadingMoreView;

    private View mEmptyView;

    private SearchView mSearchView;

    private FilesAdapter mFilesAdapter;

    private RecyclerViewOnScrollListener mRecyclerViewOnScrollListener;

    private OnFileActionsListener mOnFileActionsListener;

    private boolean scroll = false;

    private int currentChunk = 0;

    private String title;

    private boolean rootFragment = false;

    private boolean searchFragment = false;

    /**
     * Create a new instance of UploadcareFilesFragment, initialized to
     * show the provided Chunk content.
     */
    public static UploadcareFilesFragment newInstance(SocialSource socialSource,
            List<Chunk> chunkList, String title, boolean root) {
        UploadcareFilesFragment f = new UploadcareFilesFragment();
        Bundle args = new Bundle();
        args.putParcelable("socialsource", socialSource);
        args.putString("title", title);
        args.putBoolean("root", root);
        args.putParcelableArrayList("chunks", (ArrayList<Chunk>) chunkList);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ucw_fragment_files, container, false);

        if (savedInstanceState != null) {
            mSocialSource = savedInstanceState.getParcelable("socialsource");
            mChunks = savedInstanceState.getParcelableArrayList("chunks");
            currentChunk = savedInstanceState.getInt("currentChunk");
            title = savedInstanceState.getString("title");
            rootFragment = savedInstanceState.getBoolean("root");
            searchFragment= savedInstanceState.getBoolean("search");
        } else {
            Bundle arguments = getArguments();
            mSocialSource = arguments.getParcelable("socialsource");
            mChunks = arguments.getParcelableArrayList("chunks");
            title = arguments.getString("title");
            rootFragment = arguments.getBoolean("root");
            searchFragment = arguments.getBoolean("search",false);
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.ucw_recycler_view);
        mLoadingMoreView = rootView.findViewById(R.id.ucw_additional_loading);
        mEmptyView = rootView.findViewById(R.id.ucw_empty_view);
        mSearchView = (SearchView) rootView.findViewById(R.id.ucw_search_view);
        mSearchView.setOnQueryTextListener(this);
        mCircularProgressBar = (CircularProgressBar) rootView.findViewById(R.id.ucw_progress);
        RecyclerView.LayoutManager layoutManager = null;
        if(mSocialSource.name.equalsIgnoreCase(UploadcareWidget.SOCIAL_NETWORK_BOX)||
                mSocialSource.name.equalsIgnoreCase(UploadcareWidget.SOCIAL_NETWORK_DROPBOX)||
                mSocialSource.name.equalsIgnoreCase(UploadcareWidget.SOCIAL_NETWORK_EVERNOTE)||
                mSocialSource.name.equalsIgnoreCase(UploadcareWidget.SOCIAL_NETWORK_SKYDRIVE)||
                mSocialSource.name.equalsIgnoreCase(UploadcareWidget.SOCIAL_NETWORK_GDRIVE)){
            layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
            mRecyclerView.setLayoutManager(layoutManager);
            mFilesAdapter = new FilesLinearAdapter(getActivity(), this, UploadcareWidget.getInstance().getFileType());
            AnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mFilesAdapter);
            alphaAdapter.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
            alphaAdapter.setInterpolator(new DecelerateInterpolator());
            int pad= (int) convertDpToPixel(8, getActivity());
            mRecyclerView.setPadding(0,pad,0,pad);
            mRecyclerView.setAdapter(alphaAdapter);
        }else {
            layoutManager = new GridLayoutManager(getActivity(),
                    getResources().getInteger(R.integer.columns));
            mRecyclerView.setLayoutManager(layoutManager);
            mFilesAdapter = new FilesGridAdapter(getActivity(), this, UploadcareWidget.getInstance().getFileType());
            AnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(
                    new ScaleInAnimationAdapter(mFilesAdapter));
            alphaAdapter.setDuration(
                    getResources().getInteger(android.R.integer.config_shortAnimTime));
            alphaAdapter.setInterpolator(new DecelerateInterpolator());
            int pad= (int) convertDpToPixel(2, getActivity());
            mRecyclerView.setPadding(pad,pad,pad,pad);
            mRecyclerView.setAdapter(alphaAdapter);
        }

        mRecyclerViewOnScrollListener = new RecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                if (scroll) {
                    scroll = false;
                    getChunkData(true,null);
                }
            }
        };


        getChunkData(false,null);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSocialSource != null) {
            outState.putParcelable("socialsource", mSocialSource);
        }
        if (mChunks != null) {
            outState.putParcelableArrayList("chunks", (ArrayList<Chunk>) mChunks);
        }
        outState.putInt("currentChunk", currentChunk);
        outState.putString("title", title);
        outState.putBoolean("root", rootFragment);
        outState.putBoolean("search", searchFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnFileActionsListener = (OnFileActionsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement OnFileActionsListener");
        }
    }

    @Override
    public void itemTap(Thing thing) {
        switch (thing.objectType) {
            case Thing.TYPE_ALBUM:
                if (thing.action.path != null && thing.action.path.chunks != null) {
                    mOnFileActionsListener.onChunkSelected(thing.action.path.chunks, thing.title);
                }
                break;
            case Thing.TYPE_PHOTO:
                if (thing.action.action.equalsIgnoreCase(Action.ACTION_SELECT_FILE)) {
                    mOnFileActionsListener.onFileSelected(thing.action.url);
                }
                break;
            case Thing.TYPE_FOLDER:
                if (thing.action.path != null && thing.action.path.chunks != null) {
                    mOnFileActionsListener.onChunkSelected(thing.action.path.chunks, thing.title);
                }
                break;
            case Thing.TYPE_FILE:
                if (thing.action.action.equalsIgnoreCase(Action.ACTION_SELECT_FILE)) {
                    mOnFileActionsListener.onFileSelected(thing.action.url);
                }
                break;
            case Thing.TYPE_FRIEND:
                if (thing.action.path != null && thing.action.path.chunks != null) {
                    mOnFileActionsListener.onChunkSelected(thing.action.path.chunks, thing.title);
                }
                break;
            default:
                Log.d("UploadcareFilesActivity", "itemTap: Unknown thing type:" + thing.objectType);
                break;
        }
    }

    private void getChunkData(final boolean loadMore, String query) {
        mRecyclerView.clearOnScrollListeners();
        mEmptyView.setVisibility(View.GONE);
        if (!loadMore) {
            mFilesAdapter.clear();
            mCircularProgressBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingMoreView.setVisibility(View.VISIBLE);
        }
        StringBuilder stringBuilder = new StringBuilder();
        if(query!=null){
            stringBuilder.append(mSocialSource.rootChunks
                    .get(mOnFileActionsListener.currentRootChunk()).pathChunk).append("/");
            stringBuilder.append("-").append("/").append(query);
        }else if(searchFragment&&loadMore) {
            stringBuilder.append(mChunks.get(currentChunk).pathChunk);
        }else if (rootFragment) {
            stringBuilder.append(mChunks.get(currentChunk).pathChunk);
        } else {
            stringBuilder.append(mSocialSource.rootChunks
                    .get(mOnFileActionsListener.currentRootChunk()).pathChunk).append("/");
            for (int i = 0; i < mChunks.size(); i++) {
                if (i != mChunks.size() - 1) {
                    stringBuilder.append(mChunks.get(i).pathChunk).append("/");
                } else {
                    stringBuilder.append(mChunks.get(i).pathChunk);
                }
            }
        }

        UploadcareWidget.getInstance().getSocialApi().getSourceChunk(
                mSocialSource.getCookie(getActivity()), mSocialSource.urls.sourceBase,
                stringBuilder.toString(),
                loadMore ? mFilesAdapter.getNext() : "",
                new Callback<ChunkResponse>() {
                    @Override
                    public void success(ChunkResponse chunkResponse, Response response) {
                        Log.d("Files", chunkResponse.toString());
                        mLoadingMoreView.setVisibility(View.GONE);
                        if (chunkResponse.error != null) {
                            mOnFileActionsListener.onAuthorizationNeeded(chunkResponse);
                        } else {
                            if (!loadMore) {
                                mCircularProgressBar.setVisibility(View.GONE);
                                mFilesAdapter.updateImages(chunkResponse.things,
                                        chunkResponse.nextPage);
                            } else {
                                mFilesAdapter.addImages(chunkResponse.things,
                                        chunkResponse.nextPage);
                            }
                            scroll = (chunkResponse.nextPage != null);
                            mRecyclerViewOnScrollListener.clear();
                            if (scroll) {
                                mRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);
                            }
                        }
                        if (chunkResponse.searchPath != null) {
                            mSearchView.setVisibility(View.VISIBLE);
                            mSearchView.requestFocus();
                            searchFragment=true;
                        } else if (mFilesAdapter.isEmpty()) {
                            mSearchView.setVisibility(View.GONE);
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mSearchView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mLoadingMoreView.setVisibility(View.GONE);
                        mCircularProgressBar.setVisibility(View.GONE);
                        mOnFileActionsListener.onError(error.getLocalizedMessage());
                        if (mFilesAdapter.isEmpty()) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void changeChunk(int position) {
        if (position == currentChunk) {
            return;
        }
        searchFragment=false;
        currentChunk = position;
        mSearchView.setVisibility(View.GONE);
        getChunkData(false,null);
    }

    public void refreshChunk() {
        getChunkData(false,null);
    }

    public List<Chunk> getChunks() {
        return mChunks;
    }

    public String getTitle() {
        return title;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getChunkData(false,query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ucw_search_view) {
            mSearchView.requestFocus();
        }
    }
}
