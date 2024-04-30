package com.nustfruta.CartAndCheckout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nustfruta.R;
import com.nustfruta.models.Product;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    int[] productImages = {}; //TODO: INITIALIZE WITH R.drawable.

    public static ArrayList<Product> productArrayList;
    public static CartRecyclerViewAdapter cartRecyclerViewAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        // Recyclerview initialization
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productArrayList = new ArrayList<>();

        initProductArrayList();

        cartRecyclerViewAdapter = new CartRecyclerViewAdapter(CartActivity.this, productArrayList);
        recyclerView.setAdapter(cartRecyclerViewAdapter);


    }

    // to avoid memory leak
    public void onDestroy() {
        super.onDestroy();
        cartRecyclerViewAdapter = null;
    }

    private void initProductArrayList() {
        //TODO: fetch product array.
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, price, quantity, subtotal, subtotalPrice, delivery,
                deliveryPrice, dashedLine, total, totalPrice;
        public ImageView productIcon;
        public FloatingActionButton plusButton, minusButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            quantity = itemView.findViewById(R.id.quantity);
            productIcon = itemView.findViewById(R.id.productIcon);
            plusButton = itemView.findViewById(R.id.plusButton);
            minusButton = itemView.findViewById(R.id.minusButton);

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    minusButton.setImageResource(R.drawable.minus_icon);
                    CartActivity.productArrayList.get(getAdapterPosition()).incrementQuantity();
                    CartActivity.cartRecyclerViewAdapter.notifyItemChanged(getAdapterPosition());
                }
            });

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartActivity.productArrayList.get(getAdapterPosition()).decrementQuantity();

                    if (CartActivity.productArrayList.get(getAdapterPosition()).getQuantity() == 0) {
                        CartActivity.productArrayList.remove(getAdapterPosition());
                        CartActivity.cartRecyclerViewAdapter.notifyItemRemoved(getAdapterPosition());
                    } else
                        CartActivity.cartRecyclerViewAdapter.notifyItemChanged(getAdapterPosition());
                }
            });
        }


        public ViewHolder(@NonNull View itemView, int itemViewType) {
            super(itemView);
            subtotal = itemView.findViewById(R.id.subtotal);
            subtotalPrice = itemView.findViewById(R.id.subtotalPrice);
            delivery = itemView.findViewById(R.id.delivery);
            deliveryPrice = itemView.findViewById(R.id.deliveryPrice);
            dashedLine = itemView.findViewById(R.id.lineDivider);
            total = itemView.findViewById(R.id.total);
            totalPrice = itemView.findViewById(R.id.totalPrice);
        }

    }




}

