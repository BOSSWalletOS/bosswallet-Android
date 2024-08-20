package com.bosswallet.app.entity;

import android.content.Context;
import android.text.TextUtils;

public class MediaLinks
{
    // Update these media platform links and ids to target your media groups,
    // then update the MEDIA_TARGET_APPLICATION to match your applicationId
    public static final String MEDIA_TARGET_APPLICATION = "io.stormbird.wallet";
    public static final String AWALLET_TELEGRAM_URL = "https://t.me/AlphaWalletSupport";
    public static final String AWALLET_DISCORD_URL = "https://discord.gg/mx23YWRTYf";
    public static final String AWALLET_TWITTER_ID = "twitter://user?user_id=938624096123764736";
    public static final String AWALLET_FACEBOOK_ID = "fb://page/1958651857482632";
    public static final String AWALLET_TWITTER_URL = "https://twitter.com/BossWallet";
    public static final String AWALLET_FACEBOOK_URL = "https://www.facebook.com/BossWallet/";
    public static final String AWALLET_LINKEDIN_URL = null;
    public static final String AWALLET_REDDIT_URL = "https://www.reddit.com/r/BossWallet/";
    public static final String AWALLET_INSTAGRAM_URL = null;
    public static final String AWALLET_BLOG_URL = null;
    public static final String AWALLET_FAQ_URL = "https://bosswallet.com/faq/";
    public static final String AWALLET_GITHUB = "https://github.com/boss-wallet";
    public static final String AWALLET_EMAIL1 = "feedback+android";
    public static final String AWALLET_EMAIL2 = "bosswallet.com";
    public static final String AWALLET_SUBJECT = "BossWallet Android Help";

    public static boolean isMediaTargeted(Context context)
    {
        if (!TextUtils.isEmpty(MEDIA_TARGET_APPLICATION))
        {
            return context.getPackageName().equals(MEDIA_TARGET_APPLICATION);
        }
        else
        {
            return false;
        }
    }
}
