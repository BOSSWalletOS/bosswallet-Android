package com.bosswallet.token.entity;

public class BadContract extends Exception
{
    public BadContract()
    {

    }

    public BadContract(String message)
    {
        super(message);
    }
}
