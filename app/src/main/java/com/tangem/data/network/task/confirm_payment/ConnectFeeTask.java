package com.tangem.data.network.task.confirm_payment;

import android.app.Activity;
import android.view.View;

import com.tangem.data.network.request.FeeRequest;
import com.tangem.data.network.task.FeeTask;
import com.tangem.domain.wallet.SharedData;
import com.tangem.presentation.activity.ConfirmPaymentActivity;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class ConnectFeeTask extends FeeTask {
    private WeakReference<ConfirmPaymentActivity> reference;

    public ConnectFeeTask(ConfirmPaymentActivity context, SharedData sharedData) {
        super(sharedData);
        reference = new WeakReference<>(context);
    }

    @Override
    protected void onPostExecute(List<FeeRequest> requests) {
        super.onPostExecute(requests);
        ConfirmPaymentActivity confirmPaymentActivity = reference.get();

        for (FeeRequest request : requests) {
            if (request.error == null) {
                BigDecimal Fee = BigDecimal.ZERO;
                try {
                    try {
                        String tmpAnswer = request.getAsString();
                        Fee = new BigDecimal(tmpAnswer); // BTC per 1 kb
                    } catch (Exception e) {

                        if (sharedCounter != null) {
                            int errCounter = sharedCounter.errorRequest.incrementAndGet();


                            if (errCounter >= sharedCounter.allRequest) {
                                confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);
                                confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! No connection with blockchain nodes");
                            }
                        } else {
                            confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);
                            confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! No connection with blockchain nodes");
                        }

                        //FinishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! No connection with blockchain nodes");
                        return;
                    }

                    if (Fee.equals(BigDecimal.ZERO)) {
                        confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);
                        confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! Wrong data received from the node");
                        return;
                    }

                    long inputCount = request.txSize;

                    if (inputCount != 0) {
                        Fee = Fee.multiply(new BigDecimal(inputCount)).divide(new BigDecimal(1024)); // per Kb -> per byte
                    } else {
                        confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! Tx length unknown");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (sharedCounter != null) {
                        int errCounter = sharedCounter.errorRequest.incrementAndGet();
                        if (errCounter >= sharedCounter.allRequest) {
                            confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);
                            confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! No connection with blockchain nodes");
                        }
                    } else {
                        confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);
                        confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! No connection with blockchain nodes");
                    }
                    return;
                }



                confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);

                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(7);
                df.setMinimumFractionDigits(3);
                df.setGroupingUsed(false);
                String strFee = df.format(Fee);

                if ((request.getBlockCount() == FeeRequest.MINIMAL) && (confirmPaymentActivity.getMinFee() == null)) {
                    confirmPaymentActivity.setMinFee(strFee);
                    confirmPaymentActivity.setMinFeeInInternalUnits(confirmPaymentActivity.getCard().internalUnitsFromString(strFee));
                } else if ((request.getBlockCount() == FeeRequest.NORMAL) && (confirmPaymentActivity.getNormalFee() == null)) {
                    confirmPaymentActivity.setNormalFee(strFee);
                } else if ((request.getBlockCount() == FeeRequest.PRIORITY) && (confirmPaymentActivity.getMaxFee() == null)) {
                    confirmPaymentActivity.setMaxFee(strFee);
                }

                confirmPaymentActivity.doSetFee(confirmPaymentActivity.getRgFee().getCheckedRadioButtonId());

                confirmPaymentActivity.getEtFee().setError(null);
                confirmPaymentActivity.setFeeRequestSuccess(true);
                if (confirmPaymentActivity.getFeeRequestSuccess() && confirmPaymentActivity.getBalanceRequestSuccess()) {
                    confirmPaymentActivity.getBtnSend().setVisibility(View.VISIBLE);
                }
                confirmPaymentActivity.setDtVerified(new Date());




            } else {

                if (sharedCounter != null) {
                    int errCounter = sharedCounter.errorRequest.incrementAndGet();
                    if (errCounter >= sharedCounter.allRequest) {
                        confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);
                        confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! No connection with blockchain nodes");
                    }
                } else {
                    confirmPaymentActivity.getProgressBar().setVisibility(View.INVISIBLE);
                    confirmPaymentActivity.finishActivityWithError(Activity.RESULT_CANCELED, "Cannot calculate fee! No connection with blockchain nodes");
                }
            }
        }
    }

}