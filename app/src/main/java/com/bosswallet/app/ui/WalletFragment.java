package com.bosswallet.app.ui;

import static android.app.Activity.RESULT_OK;
import static com.bosswallet.app.C.ADDED_TOKEN;
import static com.bosswallet.app.C.ErrorCode.EMPTY_COLLECTION;
import static com.bosswallet.app.C.Key.WALLET;
import static com.bosswallet.app.ui.HomeActivity.RESET_TOKEN_SERVICE;
import static com.bosswallet.app.ui.MyAddressActivity.KEY_ADDRESS;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyProperties;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bosswallet.app.R;
import com.bosswallet.app.C;
import com.bosswallet.app.analytics.Analytics;
import com.bosswallet.app.entity.BackupOperationType;
import com.bosswallet.app.entity.ContractLocator;
import com.bosswallet.app.entity.CustomViewSettings;
import com.bosswallet.app.entity.ErrorEnvelope;
import com.bosswallet.app.entity.ServiceSyncCallback;
import com.bosswallet.app.entity.TokenFilter;
import com.bosswallet.app.entity.Wallet;
import com.bosswallet.app.entity.WalletType;
import com.bosswallet.app.entity.tokens.Token;
import com.bosswallet.app.entity.tokens.TokenCardMeta;
import com.bosswallet.app.interact.GenericWalletInteract;
import com.bosswallet.app.service.KeyService;
import com.bosswallet.app.service.TickerService;
import com.bosswallet.app.service.TokensService;
import com.bosswallet.app.ui.widget.TokensAdapterCallback;
import com.bosswallet.app.ui.widget.adapter.TokensAdapter;
import com.bosswallet.app.ui.widget.entity.AvatarWriteCallback;
import com.bosswallet.app.ui.widget.entity.WarningData;
import com.bosswallet.app.ui.widget.holder.TokenGridHolder;
import com.bosswallet.app.ui.widget.holder.TokenHolder;
import com.bosswallet.app.ui.widget.holder.WarningHolder;
import com.bosswallet.app.util.LocaleUtils;
import com.bosswallet.app.viewmodel.WalletViewModel;
import com.bosswallet.app.walletconnect.AWWalletConnectClient;
import com.bosswallet.app.widget.BuyEthOptionsView;
import com.bosswallet.app.widget.LargeTitleView;
import com.bosswallet.app.widget.NotificationView;
import com.bosswallet.app.widget.ProgressView;
import com.bosswallet.app.widget.SystemView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import org.web3j.crypto.Keys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import wallet.core.jni.HDWallet;

/**
 * Created by justindeguzman on 2/28/18.
 */
@AndroidEntryPoint
public class WalletFragment extends BaseFragment implements
        TokensAdapterCallback,
        View.OnClickListener,
        Runnable,
        AvatarWriteCallback,
        ServiceSyncCallback
{
    public static final String SEARCH_FRAGMENT = "w_search";
    private static final String TAG = "WFRAG";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private WalletViewModel viewModel;
    private SystemView systemView;
    private TokensAdapter adapter;
    private View selectedToken;
    private String importFileName;
    private RecyclerView recyclerView;
    private boolean isVisible;
    private TokenFilter currentTabPos = TokenFilter.ALL;
    private LargeTitleView largeTitleView;
    private ActivityResultLauncher<Intent> handleBackupClick;
    private ActivityResultLauncher<Intent> tokenManagementLauncher;
    private boolean completed = false;
    private boolean hasWCSession = false;


    //New Add
    private Wallet currentWallet;
    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String LEGACY_CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    public static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";

    private static final int AUTHENTICATION_DURATION_SECONDS = 30;

    private static final String PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;


    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM;

    private KeyService.AuthenticationLevel authLevel;


    @Inject
    AWWalletConnectClient awWalletConnectClient;

    private final ActivityResultLauncher<Intent> networkSettingsHandler = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                //send instruction to restart tokenService
                getParentFragmentManager().setFragmentResult(RESET_TOKEN_SERVICE, new Bundle());
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        LocaleUtils.setActiveLocale(getContext()); // Can't be placed before above line

        if (CustomViewSettings.canAddTokens())
        {
            toolbar(view, R.menu.menu_wallet, this::onMenuItemClick);
        }
        else
        {
            toolbar(view);
        }


        String seedPhrase = "betray law harsh guitar disorder grain scrub capital waste escape uphold shadow";

        HDWallet newWallet = new HDWallet(seedPhrase, "");
//        storeHDKey(newWallet, false); //store encrypted bytes in case of re-entry
//        checkAuthentication(IMPORT_HD_KEY);


        initResultLaunchers();

        initViews(view);

        initViewModel();

        initList();

//        initTabLayout(view);

        initNotificationView(view);

        setImportToken();

        viewModel.prepare();


        getChildFragmentManager()
                .setFragmentResultListener(SEARCH_FRAGMENT, this, (requestKey, bundle) ->
                {
                    Fragment fragment = getChildFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);
                    if (fragment != null && fragment.isVisible() && !fragment.isDetached())
                    {
                        fragment.onDetach();
                        getChildFragmentManager().beginTransaction()
                                .remove(fragment)
                                .commitAllowingStateLoss();
                    }
                });

        return view;
    }



    public synchronized static String getFilePath(Context context, String fileName)
    {
        //check for matching file
        File check = new File(context.getFilesDir(), fileName);
        if (check.exists())
        {
            return check.getAbsolutePath(); //quick return
        }
        else
        {
            //find matching file, ignoring case
            File[] files = context.getFilesDir().listFiles();
            for (File checkFile : files)
            {
                if (checkFile.getName().equalsIgnoreCase(fileName))
                {
                    return checkFile.getAbsolutePath();
                }
            }
        }

        return check.getAbsolutePath(); //Should never get here
    }

    /***
    private boolean tryInitStrongBoxKey(KeyGenerator keyGenerator, String keyAddress, boolean useAuthentication) throws InvalidAlgorithmParameterException
    {
        try
        {
            keyGenerator.init(new KeyGenParameterSpec.Builder(
                    keyAddress,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setKeySize(256)
                    .setUserAuthenticationRequired(useAuthentication)
                    .setIsStrongBoxBacked(true)
                    .setInvalidatedByBiometricEnrollment(false)
                    .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_DURATION_SECONDS)
                    .setRandomizedEncryptionRequired(true)
                    .setEncryptionPaddings(PADDING)
                    .build());

            keyGenerator.generateKey();
        }
        catch (StrongBoxUnavailableException e)
        {
            return false;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            return false;
        }

        return true;
    }

    private KeyGenerator getMaxSecurityKeyGenerator(String keyAddress, boolean useAuthentication)
{
    KeyGenerator keyGenerator = null;

    try
    {
        keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEY_STORE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && tryInitStrongBoxKey(keyGenerator, keyAddress, useAuthentication))
        {
            if (useAuthentication) authLevel = KeyService.AuthenticationLevel.STRONGBOX_AUTHENTICATION;
            else authLevel = STRONGBOX_NO_AUTHENTICATION;
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && tryInitStrongBoxKey(keyGenerator, keyAddress, false))
        {
            authLevel = STRONGBOX_NO_AUTHENTICATION;
        }
        else if (tryInitTEEKey(keyGenerator, keyAddress, useAuthentication))
        {
            //fallback to non Strongbox
            if (useAuthentication) authLevel = KeyService.AuthenticationLevel.TEE_AUTHENTICATION;
            else authLevel = TEE_NO_AUTHENTICATION;
        }
        else if (tryInitTEEKey(keyGenerator, keyAddress, false))
        {
            authLevel = TEE_NO_AUTHENTICATION;
        }
    }
    catch (NoSuchAlgorithmException | NoSuchProviderException ex)
    {
        ex.printStackTrace();
        return null;
    }
    catch (Exception e)
    {
        e.printStackTrace();
        authLevel = KeyService.AuthenticationLevel.NOT_SET;
    }

    return keyGenerator;
}
     */
    private boolean writeBytesToFile(String path, byte[] data)
    {
        File file = new File(path);
        try (FileOutputStream fos = new FileOutputStream(file))
        {
            fos.write(data);
        }
        catch (IOException e)
        {
            Timber.d(e, "Exception while writing file ");
            return false;
        }

        return true;
    }


    /**
    private synchronized boolean storeEncryptedBytes(byte[] data, boolean createAuthLocked, String fileName)
    {
        KeyStore keyStore = null;
        try
        {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);

            String encryptedHDKeyPath = getFilePath(context, fileName);
            KeyGenerator keyGenerator = getMaxSecurityKeyGenerator(fileName, createAuthLocked);
            final SecretKey secretKey = keyGenerator.generateKey();
            final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            String ivPath = getFilePath(context, fileName + "iv");
            boolean success = writeBytesToFile(ivPath, iv);
            if (!success)
            {
                //deleteKey(fileName);
                throw new ServiceErrorException(
                        ServiceErrorException.ServiceErrorCode.FAIL_TO_SAVE_IV_FILE,
                        "Failed to create the iv file for: " + fileName + "iv");
            }

            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    new FileOutputStream(encryptedHDKeyPath),
                    cipher))
            {
                cipherOutputStream.write(data);
            }
            catch (Exception ex)
            {
                //deleteKey(fileName);
                throw new ServiceErrorException(
                        ServiceErrorException.ServiceErrorCode.KEY_STORE_ERROR,
                        "Failed to create the file for: " + fileName);
            }

            return true;
        }
        catch (Exception ex)
        {
//            deleteKey(fileName);
            Timber.tag(TAG).d(ex, "Key store error");
        }

        return false;
    }

    private synchronized boolean storeHDKey(HDWallet newWallet, boolean keyRequiresAuthentication)
    {
        PrivateKey pk = newWallet.getKeyForCoin(CoinType.ETHEREUM);
        currentWallet = new Wallet(CoinType.ETHEREUM.deriveAddress(pk));

        return storeEncryptedBytes(newWallet.mnemonic().getBytes(), keyRequiresAuthentication, currentWallet.address);
    }
     */
    private void initResultLaunchers()
    {
        tokenManagementLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    if (result.getData() == null) return;
                    ArrayList<ContractLocator> tokenData = result.getData().getParcelableArrayListExtra(ADDED_TOKEN);
                    Bundle b = new Bundle();
                    b.putParcelableArrayList(C.ADDED_TOKEN, tokenData);
                    getParentFragmentManager().setFragmentResult(C.ADDED_TOKEN, b);
                });

        handleBackupClick = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    String keyBackup = null;
                    boolean noLockScreen = false;
                    Intent data = result.getData();
                    if (data != null)
                    {
                        keyBackup = data.getStringExtra("Key");
                        data.getBooleanExtra("nolock", false);
                    }
                    Bundle backup = new Bundle();
                    backup.putBoolean(C.HANDLE_BACKUP, result.getResultCode() == RESULT_OK);
                    backup.putString("Key", keyBackup);
                    backup.putBoolean("nolock", noLockScreen);
                    getParentFragmentManager().setFragmentResult(C.HANDLE_BACKUP, backup);
                });
    }

    class CompletionLayoutListener extends LinearLayoutManager
    {
        public CompletionLayoutListener(Context context)
        {
            super(context);
        }

        public CompletionLayoutListener(FragmentActivity activity, int orientation, boolean reverseLayout)
        {
            super(activity, orientation, reverseLayout);
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state)
        {
            super.onLayoutCompleted(state);
            final int firstVisibleItemPosition = findFirstVisibleItemPosition();
            final int lastVisibleItemPosition = findLastVisibleItemPosition();
            int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
            if (!completed && itemsShown > 1)
            {
                completed = true;
                viewModel.startUpdateListener();
                viewModel.getTokensService().startUpdateCycleIfRequired();
            }
        }
    }

    private void initList()
    {
        adapter = new TokensAdapter(this, viewModel.getAssetDefinitionService(), viewModel.getTokensService(),
                tokenManagementLauncher);
        adapter.setHasStableIds(true);
        setLinearLayoutManager(TokenFilter.ALL.ordinal());
        recyclerView.setAdapter(adapter);
        if (recyclerView.getItemAnimator() != null)
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addRecyclerListener(holder -> adapter.onRViewRecycled(holder));
        recyclerView.setLayoutManager(new CompletionLayoutListener(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void initViewModel()
    {
        viewModel = new ViewModelProvider(this)
                .get(WalletViewModel.class);
        viewModel.progress().observe(getViewLifecycleOwner(), systemView::showProgress);
        viewModel.tokens().observe(getViewLifecycleOwner(), this::onTokens);
        viewModel.backupEvent().observe(getViewLifecycleOwner(), this::backupEvent);
        viewModel.defaultWallet().observe(getViewLifecycleOwner(), this::onDefaultWallet);
        viewModel.onFiatValues().observe(getViewLifecycleOwner(), this::updateValue);
        viewModel.onUpdatedTokens().observe(getViewLifecycleOwner(), this::updateMetas);
        viewModel.removeDisplayTokens().observe(getViewLifecycleOwner(), this::removeTokens);
        viewModel.getTokensService().startWalletSync(this);
        viewModel.activeWalletConnectSessions().observe(getViewLifecycleOwner(), walletConnectSessionItems -> {
            hasWCSession = !walletConnectSessionItems.isEmpty();
            adapter.showActiveWalletConnectSessions(walletConnectSessionItems);
        });
    }

    private void initViews(@NonNull View view)
    {
        systemView = view.findViewById(R.id.system_view);
        recyclerView = view.findViewById(R.id.list);

        systemView.showProgress(true);

        systemView.attachRecyclerView(recyclerView);

        largeTitleView = view.findViewById(R.id.large_title_view);

        ((ProgressView) view.findViewById(R.id.progress_view)).hide();
    }

    private void onDefaultWallet(Wallet wallet)
    {
        if (CustomViewSettings.showManageTokens())
        {
            adapter.setWalletAddress(wallet.address);
        }



        //Do we display new user backup popup?
        Bundle result = new Bundle();
        result.putBoolean(C.SHOW_BACKUP, wallet.lastBackupTime > 0);
        getParentFragmentManager().setFragmentResult(C.SHOW_BACKUP, result); //reset tokens service and wallet page with updated filters

    }

    private void updateMetas(TokenCardMeta[] metas)
    {
        if (metas.length > 0)
        {
            adapter.updateTokenMetas(metas);
            systemView.hide();
            viewModel.checkDeleteMetas(metas);
            viewModel.calculateFiatValues();
        }
    }

    public void updateAttestationMeta(TokenCardMeta tcm)
    {
        updateMetas(new TokenCardMeta[]{tcm});
        viewModel.checkRemovedMetas();
    }

    //Refresh value of wallet once sync is complete
    @Override
    public void syncComplete(TokensService svs, int syncCount)
    {
        svs.getFiatValuePair()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateValue)
                .isDisposed();

        if (syncCount > 0)
        {
            //now refresh the tokens to pick up any new ticker updates
            viewModel.getTokensService().getTickerUpdateList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(adapter::notifyTickerUpdate)
                    .isDisposed();
        }
    }

    //Could the view have been destroyed?
    private void updateValue(Pair<Double, Double> fiatValues)
    {
        try
        {
            // to avoid NaN
            double changePercent = fiatValues.first != 0 ? ((fiatValues.first - fiatValues.second) / fiatValues.second) * 100.0 : 0.0;
            largeTitleView.subtitle.setText(getString(R.string.wallet_total_change, TickerService.getCurrencyString(fiatValues.first - fiatValues.second),
                    TickerService.getPercentageConversion(changePercent)));
            largeTitleView.title.setText(TickerService.getCurrencyString(fiatValues.first));
            int color = ContextCompat.getColor(requireContext(), changePercent < 0 ? R.color.negative : R.color.positive);
            largeTitleView.subtitle.setTextColor(color);

            if (viewModel.getWallet() != null && viewModel.getWallet().type != WalletType.WATCH && isVisible)
            {
                viewModel.checkBackup(fiatValues.first);
            }
        }
        catch (Exception e)
        {
            // empty: expected if view has terminated before we can shut down the service return
        }
    }

    private void refreshList()
    {
        handler.post(() ->
        {
            adapter.clear();
            viewModel.prepare();
            viewModel.notifyRefresh();
        });
    }

    private void removeTokens(Token[] tokensToRemove)
    {
        for (Token remove : tokensToRemove)
        {
            adapter.removeToken(remove.getDatabaseKey());
        }
    }

    @Override
    public void comeIntoFocus()
    {
        isVisible = true;
        if (completed)
        {
            viewModel.startUpdateListener();
            viewModel.getTokensService().startUpdateCycleIfRequired();
        }
        checkWalletConnect();
    }

    @Override
    public void leaveFocus()
    {
        isVisible = false;
        viewModel.stopUpdateListener();
        softKeyboardGone();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (isVisible)
        {
            viewModel.stopUpdateListener();
        }
    }

    private void initTabLayout(View view)
    {
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        if (CustomViewSettings.hideTabBar())
        {
            tabLayout.setVisibility(View.GONE);
            return;
        }
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.assets));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.collectibles));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.defi_header));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.governance_header));
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.attestations));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                TokenFilter newFilter = setLinearLayoutManager(tab.getPosition());
                adapter.setFilterType(newFilter);
                switch (newFilter)
                {
                    case ALL:
                    case ASSETS:
                    case DEFI:
                    case GOVERNANCE:
                        recyclerView.setLayoutManager(new CompletionLayoutListener(getActivity()));
                        viewModel.prepare();
                        break;
                    case COLLECTIBLES:
                        setGridLayoutManager(TokenFilter.COLLECTIBLES);
                        viewModel.prepare();
                        break;
                    case ATTESTATIONS: // TODO: Filter Attestations
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });
    }

    private void setGridLayoutManager(TokenFilter tab)
    {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                if (adapter.getItemViewType(position) == TokenGridHolder.VIEW_TYPE)
                {
                    return 1;
                }
                return 2;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        currentTabPos = tab;
    }

    private TokenFilter setLinearLayoutManager(int selectedTab)
    {
        currentTabPos = TokenFilter.values()[selectedTab];
        return currentTabPos;
    }

    @Override
    public void onTokenClick(View view, Token token, List<BigInteger> ids, boolean selected)
    {
        if (selectedToken == null)
        {
            getParentFragmentManager().setFragmentResult(C.TOKEN_CLICK, new Bundle());
            selectedToken = view;
            /*Token clickOrigin = viewModel.getTokenFromService(token);
            if (clickOrigin == null || token.getInterfaceSpec() == ContractType.ATTESTATION)
            {
                clickOrigin = token;
            }*/
            viewModel.showTokenDetail(getActivity(), token);
            handler.postDelayed(this, 700);
        }
    }

    @Override
    public void onLongTokenClick(View view, Token token, List<BigInteger> tokenId)
    {

    }

    @Override
    public void reloadTokens()
    {
        viewModel.reloadTokens();
    }

    @Override
    public void onBuyToken()
    {
        final BottomSheetDialog buyEthDialog = new BottomSheetDialog(getActivity());
        BuyEthOptionsView buyEthOptionsView = new BuyEthOptionsView(getActivity());
        buyEthOptionsView.setOnBuyWithRampListener(v -> {
            Intent intent = viewModel.getBuyIntent(getCurrentWallet().address);
            ((HomeActivity) getActivity()).onActivityResult(C.TOKEN_SEND_ACTIVITY, RESULT_OK, intent);
            viewModel.track(Analytics.Action.BUY_WITH_RAMP);
            buyEthDialog.dismiss();
        });
        buyEthOptionsView.setOnBuyWithCoinbasePayListener(v -> {
            viewModel.showBuyEthOptions(getActivity());
        });
        buyEthOptionsView.setDismissInterface(() -> {
            if (buyEthDialog != null && buyEthDialog.isShowing())
            {
                buyEthDialog.dismiss();
            }
        });
        buyEthDialog.setContentView(buyEthOptionsView);
        buyEthDialog.show();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        currentTabPos = TokenFilter.ALL;
        selectedToken = null;
        if (viewModel == null)
        {
            requireActivity().recreate();
            return;
        }
        else
        {
            viewModel.track(Analytics.Navigation.WALLET);
            if (largeTitleView != null)
            {
                largeTitleView.setVisibility(View.VISIBLE); //show or hide Fiat summary
            }
        }

        if (isVisible)
        {
            viewModel.startUpdateListener();
            viewModel.getTokensService().startUpdateCycleIfRequired();
        }

        checkWalletConnect();
    }

    private void checkWalletConnect()
    {
        if (adapter != null)
        {
            adapter.checkWalletConnect();
        }
    }

    private void onTokens(TokenCardMeta[] tokens)
    {
        if (tokens != null)
        {
            adapter.setTokens(tokens);
            checkScrollPosition();
            viewModel.calculateFiatValues();
        }
        systemView.showProgress(false);

        if (currentTabPos.equals(TokenFilter.ALL))
        {
            checkWalletConnect();
        }
        else
        {
            adapter.showActiveWalletConnectSessions(Collections.emptyList());
        }
    }

    /**
     * Checks to see if the current session was started from clicking on a TokenScript notification
     * If it was, identify the contract and pass information to adapter which will identify the corresponding contract token card
     */
    private void setImportToken()
    {
        if (importFileName != null)
        {
            ContractLocator importToken = viewModel.getAssetDefinitionService().getHoldingContract(importFileName);
            if (importToken != null)
                Toast.makeText(getContext(), importToken.address, Toast.LENGTH_LONG).show();
            if (importToken != null && adapter != null) adapter.setScrollToken(importToken);
            importFileName = null;
        }
    }

    /**
     * If the adapter has identified the clicked-on script update from the above call and that card is present, scroll to the card.
     */
    private void checkScrollPosition()
    {
        int scrollPos = adapter.getScrollPosition();
        if (scrollPos > 0 && recyclerView != null)
        {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(scrollPos, 0);
        }
    }

    private void backupEvent(GenericWalletInteract.BackupLevel backupLevel)
    {
        if (adapter.hasBackupWarning()) return;

        WarningData wData;
        switch (backupLevel)
        {
            case BACKUP_NOT_REQUIRED:
                break;
            case WALLET_HAS_LOW_VALUE:
                wData = new WarningData(this);
                wData.title = getString(R.string.time_to_backup_wallet);
                wData.detail = getString(R.string.recommend_monthly_backup);
                wData.buttonText = getString(R.string.back_up_now);
                wData.colour = R.color.text_secondary;
                wData.wallet = viewModel.getWallet();
                adapter.addWarning(wData);
                break;
            case WALLET_HAS_HIGH_VALUE:
                wData = new WarningData(this);
                wData.title = getString(R.string.wallet_not_backed_up);
                wData.detail = getString(R.string.not_backed_up_detail);
                wData.buttonText = getString(R.string.back_up_now);
                wData.colour = R.color.error;
                wData.wallet = viewModel.getWallet();
                adapter.addWarning(wData);
                break;
        }
    }

    private void onError(ErrorEnvelope errorEnvelope)
    {
        if (errorEnvelope.code == EMPTY_COLLECTION)
        {
            systemView.showEmpty(getString(R.string.no_tokens));
        }
        else
        {
            systemView.showError(getString(R.string.error_fail_load_tokens), this);
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.try_again)
        {
            viewModel.prepare();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        viewModel.stopUpdateListener();
        if (adapter != null && recyclerView != null) adapter.onDestroy(recyclerView);
    }

    @Override
    public void resetTokens()
    {
        if (viewModel != null && adapter != null)
        {
            //reload tokens
            refreshList();

            handler.post(() ->
            {
                //first abort the current operation
                adapter.clear();
                //show syncing
            });
        }
    }

    @Override
    public void run()
    {
//        if (selectedToken != null && selectedToken.findViewById(R.id.token_layout) != null)
//        {
//            selectedToken.findViewById(R.id.token_layout).setBackgroundResource(R.drawable.background_marketplace_event);
//        }
        selectedToken = null;
    }

    @Override
    public void backUpClick(Wallet wallet)
    {
        Intent intent = new Intent(getContext(), BackupKeyActivity.class);
        intent.putExtra(WALLET, wallet);

        switch (viewModel.getWalletType())
        {
            case HDKEY:
                intent.putExtra("TYPE", BackupOperationType.BACKUP_HD_KEY);
                break;
            case KEYSTORE:
                intent.putExtra("TYPE", BackupOperationType.BACKUP_KEYSTORE_KEY);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        handleBackupClick.launch(intent);
    }

    @Override
    public void remindMeLater(Wallet wallet)
    {
        handler.post(() ->
        {
            if (viewModel != null) viewModel.setKeyWarningDismissTime(wallet.address);
            if (adapter != null) adapter.removeItem(WarningHolder.VIEW_TYPE);
        });
    }

    @Override
    public void storeWalletBackupTime(String backedUpKey)
    {
        handler.post(() ->
        {
            if (viewModel != null) viewModel.setKeyBackupTime(backedUpKey);
            if (adapter != null) adapter.removeItem(WarningHolder.VIEW_TYPE);
        });
    }

    @Override
    public void setImportFilename(String fName)
    {
        importFileName = fName;
    }

    @Override
    public void avatarFound(Wallet wallet)
    {
        //write to database
        viewModel.saveAvatar(wallet);
    }

    public Wallet getCurrentWallet()
    {
        return viewModel.getWallet();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem)
    {
        if (menuItem.getItemId() == R.id.action_my_wallet)
        {
            viewModel.showMyAddress(requireActivity());
        }
        if (menuItem.getItemId() == R.id.action_scan)
        {
            Bundle b = new Bundle();
            b.putParcelableArrayList(C.QRCODE_SCAN, null);
            getParentFragmentManager().setFragmentResult(C.QRCODE_SCAN, b);
        }
        return super.onMenuItemClick(menuItem);
    }

    private void initNotificationView(View view)
    {
        NotificationView notificationView = view.findViewById(R.id.notification);
        notificationView.setVisibility(View.GONE);
    }

    @Override
    public void onSearchClicked()
    {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        networkSettingsHandler.launch(intent);
        //startActivity(intent);
    }

    @Override
    public void onWCClicked()
    {
        Intent intent = awWalletConnectClient.getSessionIntent(getContext());
        startActivity(intent);
    }

    @Override
    public boolean hasWCSession()
    {
        return hasWCSession || (awWalletConnectClient != null && awWalletConnectClient.hasWalletConnectSessions());
    }

    @Override
    public void onSwitchClicked()
    {
        Intent intent = new Intent(getActivity(), NetworkToggleActivity.class);
        intent.putExtra(C.EXTRA_SINGLE_ITEM, false);
        networkSettingsHandler.launch(intent);
    }

    public class SwipeCallback extends ItemTouchHelper.SimpleCallback
    {
        private final TokensAdapter mAdapter;
        private Drawable icon;
        private ColorDrawable background;

        SwipeCallback(TokensAdapter adapter)
        {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
            if (getActivity() != null)
            {
                icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_hide_token);
                if (icon != null)
                {
                    icon.setTint(ContextCompat.getColor(getActivity(), R.color.error_inverse));
                }
                background = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.error));
            }
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1)
        {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int position)
        {

        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
        {
            if (viewHolder.getItemViewType() == TokenHolder.VIEW_TYPE)
            {
                Token t = ((TokenHolder) viewHolder).token;
                if (t != null && t.isEthereum()) return 0;
            }
            else
            {
                return 0;
            }

            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
        {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int offset = 20;
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0)
            {
                int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin;
                icon.setBounds(iconRight, iconTop, iconLeft, iconBottom);
                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + offset,
                        itemView.getBottom());
            }
            else if (dX < 0)
            {
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getRight() + ((int) dX) - offset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            }
            else
            {
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);
        }
    }

    @Override
    public void onToolbarClicked(View view)
    {
        //can we do it this way?
        //copy address
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(KEY_ADDRESS, Keys.toChecksumAddress(viewModel.getWalletAddr()));
        if (clipboard != null)
        {
            clipboard.setPrimaryClip(clip);
        }
    }
}
