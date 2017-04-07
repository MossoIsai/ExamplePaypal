package com.blueicon.examplepaypal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    private Button btnPaypal;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration payPalConfiguration = new PayPalConfiguration()
            .acceptCreditCards(false).
                    environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).
                    merchantName("establecimiento").
                    clientId("cliente").
                    sandboxUserPin("developermoso@hotmail.com").
                    merchantPrivacyPolicyUri(Uri.parse("https://www.paypal.com/webapps/mpp/ua/privacy-full")).
                    merchantUserAgreementUri(Uri.parse("https://www.paypal.com/webapps/mpp/ua/useragreement-full")).
                    languageOrLocale("es_MX");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPaypal = (Button)findViewById(R.id.button);
        btnPaypal.setOnClickListener(eventPaypal);

    }
    View.OnClickListener eventPaypal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBuyPressed("1.00","MXN");

        }
    };
    public void onBuyPressed(String cantidad, String typeMoney) {
        PayPalPayment payPalPayment = new
                PayPalPayment(new BigDecimal(cantidad),
                typeMoney,
                "Nombre del importe",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent,REQUEST_CODE_PAYMENT);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String estatus = confirm.toJSONObject().getJSONObject("response").getString("state");
                        Log.d("PAYPAL",""+estatus);
                        if(estatus.equals("approved")){
                            System.out.println("Venta realizada con exito");
                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("PAYPAL", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e("PAYPAL",
                        "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }
}
