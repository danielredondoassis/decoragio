package com.assis.redondo.daniel.appdoikeda.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.assis.redondo.daniel.appdoikeda.R;
import com.assis.redondo.daniel.appdoikeda.data.DataController;
import com.assis.redondo.daniel.appdoikeda.data.GenericViewController;

import androidx.recyclerview.widget.RecyclerView;


public class SearchFrameAdapter extends RecyclerView.Adapter<SearchFrameAdapter.MainViewHolder> {

    private DataController mDataController;
    private InputMethodManager mInputMethodManager;
    private GenericViewController mGenericViewController;
    private Activity activity;
    private Context context;
    private static final int VIEW_SEARCH_FIELD = 0;
    private String mSearchReference;
    private int total;
    private TextView textContactReference;


    public SearchFrameAdapter(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.mDataController = DataController.getInstance(activity);
        this.mGenericViewController = GenericViewController.getInstance(context);

        mInputMethodManager = (InputMethodManager) context.getSystemService(
                context.INPUT_METHOD_SERVICE);

        this.total = 1;
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder(View itemView) {
            super(itemView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_SEARCH_FIELD;
    }


    public class SearchFieldViewHolder extends MainViewHolder {

        private final EditText editTextSearch;
        private final ImageButton btnCancel;
        private final View searchDividerLayout;

        public SearchFieldViewHolder(View itemView) {
            super(itemView);

            editTextSearch = (EditText) itemView.findViewById(R.id.editTextSearch);
            btnCancel = (ImageButton) itemView.findViewById(R.id.btnCancel);
            searchDividerLayout = (View) itemView.findViewById(R.id.searchDividerLayout);
            //Typefaces.setTextFont(context.getAssets(), editTextSearch, Fonts.RB_REGULAR);


        }
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View SearchContent = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_frame_search, parent, false);
        return new SearchFieldViewHolder(SearchContent);
    }


    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {

        final SearchFieldViewHolder searchHolder = (SearchFieldViewHolder) holder;

        searchHolder.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length() > 0){
                    mSearchReference = editable.toString();

                    if(textContactReference != null){
                        textContactReference.setText(editable.toString());
                    }
                    if(total != 2) {
                        mGenericViewController.adjustRecyclerViewSize(100);
                        total = 2;
                        notifyItemRangeChanged(1, 1);
                        notifyItemInserted(1);
                    }
                    mGenericViewController.setContactOrPlaceReference(mSearchReference);

                } else if (editable.toString().length() == 0){
                    mSearchReference = "";
                    total = 1;
                    notifyItemRangeChanged(1, 1);
                    notifyItemRemoved(1);
                    mGenericViewController.adjustRecyclerViewSize(50);
                    mGenericViewController.setContactOrPlaceReference(mSearchReference);
                }
            }
        });

        searchHolder.editTextSearch.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                mSearchReference = searchHolder.editTextSearch.getText().toString();


                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_S || keyCode == KeyEvent.KEYCODE_SEARCH) {

                    mGenericViewController.setContactOrPlaceReference(mSearchReference);
                    mInputMethodManager.hideSoftInputFromWindow(searchHolder.editTextSearch.getWindowToken(), 0);

                }
                return false;
            }
        });

        searchHolder.editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mInputMethodManager.hideSoftInputFromWindow(searchHolder.editTextSearch.getWindowToken(), 0);
                }
            }
        });


        searchHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchHolder.editTextSearch.setText("");
                mSearchReference = "";
                mInputMethodManager.hideSoftInputFromWindow(searchHolder.editTextSearch.getWindowToken(), 0);
                total = 1;
                notifyItemRangeChanged(1,1);
                notifyItemRemoved(1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return total;
    }

}
