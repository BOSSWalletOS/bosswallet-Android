package com.bosswallet.app.service;

import com.bosswallet.app.entity.QueryResponse;

import java.io.IOException;

/**
 * Created by JB on 4/11/2022.
 */
public interface IPFSServiceType
{
    String getContent(String url);
    QueryResponse performIO(String url, String[] headers) throws IOException;
}
