package com.tangem.wallet;

import android.net.Uri;

import com.google.common.base.Strings;
import com.tangem.cardReader.CardProtocol;
import com.tangem.cardReader.Util;

import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dvol on 06.08.2017.
 */
public enum Blockchain {
    Unknown("", "", 1.0, R.drawable.ic_logo_small, ""),
    Bitcoin("BTC", "BTC", 100000000.0, R.drawable.bitcoins, "Bitcoin"),
    BitcoinTestNet("BTC/test", "BTC", 100000000.0, R.drawable.bitcoins_testnet, "Bitcoin Testnet"),
    Ethereum("ETH", "ETH", 1.0, R.drawable.ethereum, "Ethereum"),
    EthereumTestNet("ETH/test", "ETH", 1.0, R.drawable.ethereum_testnet, "Ethereum Testnet"),
    Token("ETH\\XTZ", "BAT", 1.0, R.drawable.bat_token, "Ethereum"),
    BitcoinCash("BCH", "BCH", 100000000.0, R.drawable.bitcoin_cash, "Bitcoin Cash"),
    BitcoinCashTestNet("BCH/test", "BTC", 100000000.0, R.drawable.bitcoin_cash, "Bitcoin Cash Testnet");


    Blockchain(String ID, String Currency, double Multiplier, int ImageResource, String officialName) {
        mID = ID;
        mCurrency = Currency;
        mMultiplier = Multiplier;
        mImageResource = ImageResource;
        mOfficialName = officialName;
    }

    private String mID, mOfficialName;
    private double mMultiplier;
    private String mCurrency;
    private int mImageResource;

    public String getID() {
        return mID;
    }

    public String getOfficialName() {
        return mOfficialName;
    }

    public double getMultiplier() {
        return mMultiplier;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public static Blockchain fromId(String id) {
        for (Blockchain blockchain : values()) {
            if (blockchain.getID().equals(id)) return blockchain;
        }
        return null;
    }

    public static Blockchain fromCurrency(String currency) {
        for (Blockchain blockchain : values()) {
            if (blockchain.getCurrency() == currency) return blockchain;
        }
        return null;
    }

    public static String[] getCurrencies() {
        String[] result = new String[values().length - 1];
        for (int i = 0; i < result.length - 1; i++) {
            result[i] = values()[i + 1].getCurrency();
        }
        return result;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public int getImageResource(android.content.Context context, String name) {
        if(Strings.isNullOrEmpty(name))
            return getImageResource();

        name = name.toLowerCase();

        int resourceId = context.getResources().getIdentifier(name+"_token", "drawable", context.getPackageName());

        if(resourceId <= 0)
            return R.drawable.ethereum;
        return resourceId;
    }
}
