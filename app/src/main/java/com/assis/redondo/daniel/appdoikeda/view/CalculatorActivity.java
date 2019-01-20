package com.assis.redondo.daniel.appdoikeda.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assis.redondo.daniel.appdoikeda.R;
import com.assis.redondo.daniel.appdoikeda.data.DataController;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CalculatorActivity extends AppCompatActivity {


    @BindView(R.id.editHeight)
    EditText editHeight;
    @BindView(R.id.editHeightInput)
    TextInputLayout editHeightInput;
    @BindView(R.id.editWidth)
    EditText editWidth;
    @BindView(R.id.editWidthInput)
    TextInputLayout editWidthInput;
    @BindView(R.id.btnFrame)
    Button btnFrame;
    @BindView(R.id.editPassepartout)
    EditText editPassepartout;
    @BindView(R.id.editPassepartoutInput)
    TextInputLayout editPassepartoutInput;
    @BindView(R.id.checkGlass)
    CheckBox checkGlass;
    @BindView(R.id.checkBackground)
    CheckBox checkBackground;
    @BindView(R.id.checkCollage)
    CheckBox checkCollage;
    @BindView(R.id.frameOptionsLayout)
    LinearLayout frameOptionsLayout;
    @BindView(R.id.btnCalcProject)
    Button btnCalcProject;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.projectTotalValue)
    TextView projectTotalValue;
    private DataController mDataController;
    private AlertDialog.Builder mFillFormDialog;
    private String currentFrameId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }

        mDataController = DataController.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnCalcProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editHeight.getText().toString() != null && !editHeight.getText().toString().contentEquals("") &&
                        editWidth.getText() != null && !editWidth.getText().toString().contentEquals("")) {
                    setModelData();
                    calculateProject();
                } else {
                    checkFieldsAndWarnUser();
                }
            }
        });

        checkGlass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDataController.getProjectModel().setGlassOption(true);
                } else {
                    mDataController.getProjectModel().setGlassOption(false);
                }
            }
        });

        checkBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDataController.getProjectModel().setBackgroundOption(true);
                } else {
                    mDataController.getProjectModel().setBackgroundOption(false);
                }
            }
        });

        checkCollage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDataController.getProjectModel().setCollageOption(true);
                } else {
                    mDataController.getProjectModel().setCollageOption(false);
                }
            }
        });

        btnFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDataController.getProjectModel().getHeight() != null) {
                    mDataController.setHeight(mDataController.getProjectModel().getHeight());
                }

                if (mDataController.getProjectModel().getWidth() != null) {
                    mDataController.setWidth(mDataController.getProjectModel().getWidth());
                }

                Intent newIntent = new Intent(CalculatorActivity.this, FramesActivity.class);
                CalculatorActivity.this.startActivity(newIntent);
            }
        });

        editHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    mDataController.getProjectModel().setHeight(editHeight.getText().toString());
                } else {
                    mDataController.getProjectModel().setHeight("0");
                }
            }
        });

        editWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    mDataController.getProjectModel().setWidth(editWidth.getText().toString());
                } else {
                    mDataController.getProjectModel().setWidth("0");
                }
            }
        });
    }

    private void calculateProject() {
        projectTotalValue.setText("R$ " + mDataController.calculateProject().toString());
    }

    private void setModelData() {
        mDataController.getProjectModel().setCollageOption(checkCollage.isChecked());
        mDataController.getProjectModel().setBackgroundOption(checkBackground.isChecked());
        mDataController.getProjectModel().setGlassOption(checkGlass.isChecked());
        mDataController.getProjectModel().setPassePartout(editPassepartout.getText().toString());
        mDataController.getProjectModel().setHeight(editHeight.getText().toString());
        mDataController.getProjectModel().setWidth(editWidth.getText().toString());
    }

    private void checkFieldsAndWarnUser() {
        if (editHeight.getText().toString() == null || editHeight.getText().toString().contentEquals("")) {
            showInputInfoDialog(CalculatorActivity.this.getResources().getString(R.string.fill_height_field));
        }
        if (editWidth.getText() == null || editWidth.getText().toString().contentEquals("")) {
            showInputInfoDialog(CalculatorActivity.this.getResources().getString(R.string.fill_width_field));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        currentFrameId = mDataController.getSelectedFrameId();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mDataController.getSelectedFrameId() != null){
            btnFrame.setText(mDataController.getSelectedFrameId());
            if(currentFrameId != null && !currentFrameId.contentEquals(mDataController.getSelectedFrameId())){
                projectTotalValue.setText("");
            }
        } else {
            btnFrame.setText("Moldura");
            projectTotalValue.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void showInputInfoDialog(String message) {
        final Context ctx = this;
        if (ctx != null) {
            if (mFillFormDialog == null) {
                mFillFormDialog = new AlertDialog.Builder(ctx);
                mFillFormDialog.setTitle("Atenção");
                mFillFormDialog.setMessage(message);

                mFillFormDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mFillFormDialog = null;
                    }
                });

                mFillFormDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mFillFormDialog = null;
                    }
                });

                mFillFormDialog.show();
            }
        }
    }
}
