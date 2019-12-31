package com.aysel.inappbilling;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private BillingClient mBillingClient;

    @BindView(R.id.btn_birHaftalik)
    Button mBirHaftalik;

    @BindView(R.id.btn_haftalikAbonelik)
    Button mBirHaftalikAbonelik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK)
                    buttonIsEnableStatus(true);
                else
                    buttonIsEnableStatus(false);
            }

            @Override
            public void onBillingServiceDisconnected() {
                //TODO Kullanıcıya uyarı ver
                buttonIsEnableStatus(false);
                Toast.makeText(getApplicationContext(), "Ödeme gerçekleştirilemedi, Hata", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buttonIsEnableStatus(boolean isEnabled) {
        mBirHaftalik.setEnabled(isEnabled);
        mBirHaftalikAbonelik.setEnabled(isEnabled);
    }

    @OnClick(R.id.btn_birHaftalik)
    void f_birHaftalik(View view) {
        buyProduct("1_haftalik_yenilemesiz");
    }

    @OnClick(R.id.btn_haftalikAbonelik)
    void f_haftalikAbonelik(View view) {
        buySubscription("1_haftalik_abonelik");
    }

    private void buyProduct(String productID) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(productID)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        mBillingClient.launchBillingFlow(this, flowParams);
    }

    private void buySubscription(String productID) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(productID)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        mBillingClient.launchBillingFlow(this, flowParams);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable final List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (final Purchase purchase : purchases) {
                mBillingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(int responseCode, String purchaseToken) {
                        if (responseCode == BillingClient.BillingResponse.OK) {
                            //satın alma başarılı bir şekilde tamamlandığı durumda yapacağınız işlemler burada yer alacak
                        }
                    }
                });
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            //Kullanıcı iptal ettiği durumlarda yapacağınız işlemler burada yer alacak.
        }
    }
}
