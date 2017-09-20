package com.assis.redondo.daniel.appdoikeda.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.assis.redondo.daniel.appdoikeda.R;
import com.assis.redondo.daniel.appdoikeda.data.DataController;
import com.assis.redondo.daniel.appdoikeda.data.GenericViewController;
import com.assis.redondo.daniel.appdoikeda.data.db.DbFrameModel;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.DataEvent;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.Event;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.EventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.MainViewHolder> {

    private static final int VIEW_FRAME_LIST_CONTENT = 0;
    private static final int VIEW_FRAME_LOADING_CELL = 1;

    private final GenericViewController mGenericViewController;
    private DataController mDataController;
    private ArrayList<DbFrameModel> mFramesReference;
    private ArrayList<DbFrameModel> mFrames;
    private int contactsCount;
    private int total;
    private Activity activity;
    private Context context;

    private boolean mSearching = false;
    private boolean noFrames = false;
    private boolean isHidingPrices;

    public FrameAdapter(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.mDataController = DataController.getInstance(activity);
        this.mGenericViewController = GenericViewController.getInstance(context);
        this.mGenericViewController.addListener(DataEvent.SEARCH_FOR_FRAME, searchListener);
        this.mGenericViewController.addListener(DataEvent.REFRESH_LIST, refreshListener);

        this.mFrames = new ArrayList<DbFrameModel>();
        this.mFramesReference = new ArrayList<DbFrameModel>();

        mFramesReference = new ArrayList<>(DataController.getInstance(activity).getFrameModels());
        mFrames = new ArrayList<>(DataController.getInstance(activity).getFrameModels());
        updateAdapterCount();
    }

    private EventListener refreshListener = new EventListener() {

        @Override
        public void onEvent(Event event) {
            mSearching = false;
            mDataController.setSelectedFrameId(null);
            mFramesReference = new ArrayList<>(DataController.getInstance(activity).getFrameModels());
            mFrames = new ArrayList<>(DataController.getInstance(activity).getFrameModels());
            updateAdapterCount();
            notifyDataSetChanged();
        }
    };

    private EventListener searchListener = new EventListener() {

        @Override
        public void onEvent(Event event) {

            String searchReference = (String) ((DataEvent) event).getObject();

            searchReference = Normalizer.normalize(searchReference, Normalizer.Form.NFD);
            searchReference = searchReference.replaceAll("[^\\p{ASCII}]", "");

            if(searchReference.length() > 0) {
                mSearching = true;

                ArrayList<DbFrameModel> searchMatchFrames = new ArrayList<DbFrameModel>();

                if (mFramesReference != null) {
                    for (DbFrameModel vo : mFramesReference) {
                        String refName = vo.getFrameId();
                        refName = Normalizer.normalize(refName, Normalizer.Form.NFD);
                        refName = refName.replaceAll("[^\\p{ASCII}]", "");
                        if (refName.toLowerCase().contains(searchReference.toLowerCase())) searchMatchFrames.add(vo);
                    }
                    mFrames = new ArrayList<>(searchMatchFrames);
                }
                updateAdapterCount();
                notifyDataSetChanged();
            } else {
                mSearching = false;
                mFrames = new ArrayList<>(mFramesReference);
                updateAdapterCount();
                notifyDataSetChanged();
            }

        }
    };

    private void updateAdapterCount() {
        if(mFrames.size() == 0){
            noFrames = true;
            contactsCount = mSearching == true ? 0 : 1 ;
        } else {
            contactsCount = mFrames.size();
        }

        total = contactsCount;
    }

    public boolean isHidingPrices() {
        return isHidingPrices;
    }

    public void setPricesFlag(boolean isHidingPrices) {
        this.isHidingPrices = isHidingPrices;
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder(View itemView) {
            super(itemView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount()){
            return VIEW_FRAME_LOADING_CELL;
        } else {
            return VIEW_FRAME_LIST_CONTENT;
        }
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == VIEW_FRAME_LIST_CONTENT) {
            View FrameHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_frame_type, parent, false);
            return new FrameViewHolder(FrameHolder);
        } else {
            View LoadingHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_recycler_loading, parent, false);
            return new LoadingViewHolder(LoadingHolder);
        }
    }


    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {

        if(holder.getItemViewType() == VIEW_FRAME_LIST_CONTENT) {
            final FrameViewHolder frameHolder = (FrameViewHolder) holder;

            frameHolder.textFrameId.setText(mFrames.get(position).getFrameId());
            if(isHidingPrices){
                frameHolder.textFramePrice.setVisibility(View.GONE);
            } else {
                frameHolder.textFramePrice.setVisibility(View.VISIBLE);
                frameHolder.textFramePrice.setText(mFrames.get(position).getFrameValue());
            }

            if(mDataController.getSelectedFrameId() != null &&
                    mDataController.getSelectedFrameId().contentEquals(mFrames.get(position).getFrameId())){
                frameHolder.imagePlaceOrContact.setVisibility(View.VISIBLE);
            } else {
                frameHolder.imagePlaceOrContact.setVisibility(View.GONE);
            }

            frameHolder.frameCellLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataController.setSelectedFrameId(mFrames.get(position));
                    notifyDataSetChanged();
                    frameHolder.imagePlaceOrContact.setVisibility(View.VISIBLE);
                    activity.finish();
                }
            });

        } else {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;

        }


    }

    @Override
    public int getItemCount() {
        return total; //loading cell layout
    }

    public class LoadingViewHolder extends MainViewHolder {

        private View loadingCellLayout;
        private View loadingLayout;
        private View noFrameLayout;
        private ProgressBar progressBar;
        private TextView textNoSuggestions;
        private View separatorLayout;

        public LoadingViewHolder(View itemView) {
            super(itemView);

            loadingCellLayout = (View) itemView.findViewById(R.id.loadingCellLayout);
            loadingLayout = (View) itemView.findViewById(R.id.noFrameLayout);
            progressBar = (ProgressBar)  itemView.findViewById(R.id.progressBar);
            noFrameLayout = (View) itemView.findViewById(R.id.noFrameLayout);
            textNoSuggestions = (TextView) itemView.findViewById(R.id.textNoSuggestions);
            separatorLayout = (View) itemView.findViewById(R.id.separatorLayout);

            loadingLayout.setVisibility(View.GONE);
            noFrameLayout.setVisibility(View.GONE);

            // Typefaces.setTextFont(context.getAssets(), textPlaceOrContact, Fonts.RB_REGULAR);
            // Typefaces.setTextFont(context.getAssets(), textNoSuggestions, Fonts.RB_REGULAR);

        }
    }

    public class FrameViewHolder extends MainViewHolder {

        private  View frameLayout;
        private  View frameCellLayout;
        private  TextView textFrameId;
        private  TextView textFramePrice;
        private  ImageView imagePlaceOrContact;
        private  View separatorLayout;

        public FrameViewHolder(View itemView) {
            super(itemView);

            frameCellLayout = (View) itemView.findViewById(R.id.frameCellLayout);
            frameLayout = (View) itemView.findViewById(R.id.frameLayout);
            textFrameId = (TextView) itemView.findViewById(R.id.textFrameId);
            textFramePrice = (TextView) itemView.findViewById(R.id.textFramePrice);
            imagePlaceOrContact = (ImageView) itemView.findViewById(R.id.imagePlaceOrContact);
            separatorLayout = (View) itemView.findViewById(R.id.separatorLayout);

            // Typefaces.setTextFont(context.getAssets(), textPlaceOrContact, Fonts.RB_REGULAR);
            // Typefaces.setTextFont(context.getAssets(), textNoSuggestions, Fonts.RB_REGULAR);

        }
    }


}
