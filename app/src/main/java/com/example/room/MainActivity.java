package com.example.room;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.room.di.ConnectivityManagerModule;
import com.example.room.di.DaggerAppComponent;
import com.example.room.di.RetrofitModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Inject
    RestAPI restAPI;
    @Inject
    ConnectivityManager connectivityManager;
    private List<Model> modelList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private TextView mInfoTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        DaggerAppComponent.builder()
                .retrofitModule(new RetrofitModule())
                .connectivityManagerModule(new ConnectivityManagerModule(getApplication()))
                .build()
                .inject(this);
    }

    private void initView() {
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);
        Button btnLoad = findViewById(R.id.btnLoad);
        Button btnSaveAllSugar = findViewById(R.id.btnSaveAllSugar);
        Button btnSelectAllSugar = findViewById(R.id.btnSelectAllSugar);
        Button btnDeleteAllSugar = findViewById(R.id.btnDeleteAllSugar);
        btnLoad.setOnClickListener(this);
        btnSaveAllSugar.setOnClickListener(this);
        btnSelectAllSugar.setOnClickListener(this);
        btnDeleteAllSugar.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                mInfoTextView.setText("");
                Single<List<Model>> call = restAPI.loadUsers();

                NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

                if (networkinfo != null && networkinfo.isConnected()) {
                    downloadOneUrl(call);
                } else {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnSaveAllSugar:

                disposable.add(((App) getApplicationContext()).getDatabase().userDAO().insert(modelList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe((disposable) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            mInfoTextView.setText("");
                        })
                        .doFinally(() -> progressBar.setVisibility(View.GONE))
                        .doOnError(throwable -> mInfoTextView.setText(R.string.err_db))
                        .subscribe((aLong, throwable) -> mInfoTextView.append(getString(R.string.id_save_data) + aLong))
                );
                break;
            case R.id.btnSelectAllSugar:
                disposable.add(((App) getApplicationContext()).getDatabase().userDAO().getAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe((disposable) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            mInfoTextView.setText("");
                        })
                        .doFinally(() -> progressBar.setVisibility(View.GONE))
                        .doOnError(throwable -> mInfoTextView.setText(R.string.err_db))
                        .subscribe(models -> {
                            if (models != null && models.size() > 0) {
                                mInfoTextView.append("\n Данные из БД Size = " + models.size() +
                                        "\n-----------------");
                                for (int i = 0; i < models.size(); i++) {
                                    mInfoTextView.append(
                                            "\nLogin = " + models.get(i).getLogin() +
                                                    "\nId = " + models.get(i).getId() +
                                                    "\nURI = " + models.get(i).getAvatarUrl() +
                                                    "\n-----------------");
                                }
                            } else {
                                mInfoTextView.setText(R.string.no_data);
                            }
                        }));
                break;
            case R.id.btnDeleteAllSugar:
                disposable.add(((App) getApplicationContext()).getDatabase().userDAO().delete()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe((disposable) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            mInfoTextView.setText("");
                        })
                        .doFinally(() -> progressBar.setVisibility(View.GONE))
                        .doOnError(throwable -> mInfoTextView.setText(R.string.err_db))
                        .subscribe((aLong, throwable) -> mInfoTextView.append(getString(R.string.vol_del_rec) + aLong)));
                break;
        }
    }

    private void downloadOneUrl(Single<List<Model>> call) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    progressBar.setVisibility(View.VISIBLE);
                    this.disposable.add(disposable);
                })
                .doFinally(() -> progressBar.setVisibility(View.GONE))
                .subscribe(new DisposableSingleObserver<List<Model>>() {
                    @Override
                    public void onSuccess(List<Model> models) {
                        if (models != null) {
                            Model curModel;
                            mInfoTextView.append("\n Size = " + models.size() +
                                    "\n-----------------");
                            for (int i = 0; i < models.size(); i++) {
                                curModel = models.get(i);
                                modelList.add(curModel);
                                mInfoTextView.append(
                                        "\nLogin = " + curModel.getLogin() +
                                                "\nId = " + curModel.getId() +
                                                "\nURI = " + curModel.getAvatarUrl() +
                                                "\n-----------------");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mInfoTextView.setText(R.string.err_load);
                    }
                });
    }
}