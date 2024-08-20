package com.bosswallet.app.viewmodel;

import com.bosswallet.app.service.AnalyticsServiceType;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BrowserHistoryViewModel extends BaseViewModel
{
    @Inject
    BrowserHistoryViewModel(AnalyticsServiceType analyticsService)
    {
        setAnalyticsService(analyticsService);
    }
}
