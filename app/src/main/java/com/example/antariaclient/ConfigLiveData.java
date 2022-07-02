package com.example.antariaclient;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class ConfigLiveData extends ViewModel {

    private MutableLiveData<Config> config;
    public volatile int current = 0;

    public void postConfig(Config config) {
        this.config.postValue(config);
    }

    public void setConfig(Config config){
        this.config.setValue(config);
    }

    public MutableLiveData<Config> getConfig() {
        return config;
    }
    public Config getConfigValue(){
        return config.getValue();
    }

    public ConfigLiveData(){
        config = new MutableLiveData<>();
    }
}
