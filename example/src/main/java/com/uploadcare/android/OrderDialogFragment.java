package com.uploadcare.android;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.uploadcare.android.example.R;
import com.uploadcare.android.library.urls.Order;

public class OrderDialogFragment extends DialogFragment {

    public interface OrderSelectedListener {
        void onOrderSelected(Order order);
    }

    private OrderSelectedListener listener;

    public static OrderDialogFragment newInstance() {
        return new OrderDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            if (getParentFragment() != null) {
                listener = (OrderSelectedListener) getParentFragment();
            } else {
                listener = (OrderSelectedListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent must implement ReportReasonListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext()).setTitle(R.string.order_dialog_title)
                .setItems(R.array.orders, ((dialog, which) -> orderSelected(which)))
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void orderSelected(int position) {
        Order order;
        switch (position) {
            case 1:
                order = Order.UPLOAD_TIME_DESC;
                break;
            case 2:
                order = Order.SIZE_ASC;
                break;
            case 3:
                order = Order.SIZE_DESC;
                break;
            case 0:
            default:
                order = Order.UPLOAD_TIME_ASC;
                break;

        }
        listener.onOrderSelected(order);
    }

}
