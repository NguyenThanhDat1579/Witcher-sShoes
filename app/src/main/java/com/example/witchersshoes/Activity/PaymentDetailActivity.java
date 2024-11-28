package com.example.witchersshoes.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.CartAdapter;
import com.example.witchersshoes.Adapter.PaymentDetailAdapter;
import com.example.witchersshoes.Api.CreateOrder;
import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Date;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentDetailActivity extends AppCompatActivity {

    private RecyclerView paymentDetailView;
    private ImageView backBtn;
    private Button btnOrder;
    private TextView txtName, txtPhone, txtLocation, totalFeeTxt, taxTxt, deliveryTxt, totalTxt;
    private PaymentDetailAdapter paymentDetailAdapter;
    private CartAdapter cartAdapter;
    private List<ProductModel> cartItems = new ArrayList<>();
    private double totalFee = 0;
    private RadioButton radioPayment1, radioZaloPay;
    ProgressDialog progressDialog;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        paymentDetailView = findViewById(R.id.paymentDetailView);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtLocation = findViewById(R.id.txtLocation);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        backBtn = findViewById(R.id.backBtn);
        btnOrder = findViewById(R.id.btnOrder);
        radioPayment1 = findViewById(R.id.radioPayment1);
        radioZaloPay = findViewById(R.id.radioZaloPay);

        paymentDetailView.setLayoutManager(new LinearLayoutManager(this));

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioPayment1.isChecked()){
                    processCheckout();
                }
                else if(radioZaloPay.isChecked()){
                    paymentZaloPay();
                }
            }
        });

        loadCartItems();

    }

// Lấy thông tin nguời dùng

    private void loadDataUser(String khachHangID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("KhachHang")
                .document(khachHangID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Customer customer = new Customer();
                        customer.setId(documentSnapshot.getId());
                        customer.setUsername(documentSnapshot.getString("tenKhachHang"));
                        customer.setPhone(documentSnapshot.getString("soDienThoai"));
                        customer.setAddress(documentSnapshot.getString("diaChi"));

                        // Cập nhật thông tin người dùng lên giao diện
                        txtName.setText(customer.getUsername());
                        txtPhone.setText(customer.getPhone());
                        txtLocation.setText(customer.getAddress());
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

//    Lấy dữ liệu bên giỏ hàng

    private void loadCartItems() {
        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);

        if (khachHangID != null) {
            loadDataUser(khachHangID);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Cart")
                    .addSnapshotListener((value, error) -> { // Sử dụng addSnapshotListener để cập nhật realtime
                        if (error != null) {
                            Toast.makeText(this, "Lỗi khi tải sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            cartItems.clear();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                ProductModel item = new ProductModel();
                                // Lưu ID của document làm ID sản phẩm
                                item.setID(document.getId());
                                item.setTitle(document.getString("tenSanPham"));
                                item.setNumberInCart(document.getLong("soLuong") != null ?
                                        document.getLong("soLuong").intValue() : 1);
                                item.setPrice(document.getDouble("giaTien") != null ?
                                        document.getDouble("giaTien") : 0.0);

                                String imageUrl = document.getString("hinhAnh");
                                item.setPicUrl(new ArrayList<>(Collections.singletonList(
                                        imageUrl != null ? imageUrl : "default_image_url")));

                                cartItems.add(item);
                            }

                            // Cập nhật UI
                            if (paymentDetailAdapter == null) {
                                paymentDetailAdapter = new PaymentDetailAdapter(PaymentDetailActivity.this, cartItems,
                                        PaymentDetailActivity.this::calculateCartTotal);
                                paymentDetailView.setAdapter(paymentDetailAdapter);
                            } else {
                                paymentDetailAdapter.notifyDataSetChanged();
                            }

                            calculateCartTotal();


                        }
                    });
        }

    }

    private void processCheckout() {
        // Lấy thông tin người dùng hiện tại
        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);
        String tenKhachHang = preferences.getString("tenKhachHang",null);
        String diaChi = preferences.getString("diaChi",null);
        String soDienThoai = preferences.getString("soDienThoai",null);

        if (khachHangID == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Tham chiếu đến collections
        CollectionReference orderCollection = db.collection("KhachHang").document(khachHangID).collection("Orders");
        CollectionReference cartCollection = db.collection("KhachHang").document(khachHangID).collection("Cart");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lí đơn hàng của bạn...");
        progressDialog.setCancelable(false); // Ngăn người dùng tắt dialog bằng cách bấm bên ngoài
        progressDialog.show(); // Hiển thị dialog

        // Bắt đầu transaction ( giao dịch )
        db.runTransaction(transaction -> {
            // Tạo document order mới
            Map<String, Object> orderData = new HashMap<>();

            // Tạo danh sách các sản phẩm
            List<Map<String, Object>> products = new ArrayList<>();
            double totalAmount = 0;

            // Thêm từng sản phẩm vào danh sách
            for (ProductModel cartItem : cartItems) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("productID", cartItem.getID());
                productData.put("productName", cartItem.getTitle());
                productData.put("quantity", cartItem.getNumberInCart());
                productData.put("price", cartItem.getPrice());
                productData.put("imageUrl", cartItem.getPicUrl());

                // Tính tổng tiền của đơn hàng
                totalAmount += cartItem.getPrice() * cartItem.getNumberInCart();

                products.add(productData);
            }

            // Thêm thông tin vào đơn hàng
            orderData.put("products", products);  // Danh sách sản phẩm
            orderData.put("totalAmount", totalAmount);  // Tổng tiền
            orderData.put("createdAt", new Timestamp(new Date()));  // Thời gian tạo
            orderData.put("status", "Chờ xác nhận");  // Trạng thái đơn hàng
            orderData.put("customerID", khachHangID);  // ID khách hàng
            orderData.put("customerName",tenKhachHang);
            orderData.put("customerPhone",soDienThoai);
            orderData.put("customerAddress",diaChi);



            // Tạo một document mới trong collection Orders
            orderCollection.add(orderData);

            // Xóa tất cả items trong Cart
            for (ProductModel cartItem : cartItems) {
                cartCollection.document(cartItem.getID()).delete();
            }
//
            return null;
        }).addOnSuccessListener(aVoid -> {
            // Xử lý khi thanh toán thành công
            progressDialog.dismiss();
            cartItems.clear();
            paymentDetailAdapter.notifyDataSetChanged();
            calculateCartTotal();

//          Chuyển đến màn hình đơn hàng
            Intent intent = new Intent(PaymentDetailActivity.this, OrderActivity.class);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            // Xử lý khi có lỗi xảy ra
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi khi đặt hàng: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void calculateCartTotal() {
        totalFee = 0;
        for (ProductModel item : cartItems) {
            totalFee += item.getPrice() * item.getNumberInCart();
        }

            double tax = totalFee * 0.1; // Thuế 10%
            double deliveryFee = 20; // Phí vận chuyển cố định

            totalFeeTxt.setText(totalFee+"00₫");
            taxTxt.setText(tax+"00₫");
            deliveryTxt.setText(deliveryFee+"00₫");
            totalTxt.setText((totalFee + tax + deliveryFee)+"00₫");
        }
    private void paymentZaloPay() {
        CreateOrder orderApi = new CreateOrder();
        try {
            // Lấy tổng tiền từ giao diện
            String totalAmount = String.format("%.0f", totalFee * 1000);
            Log.d("ZaloPay", "Data from createOrder: " + totalAmount);
            // Tạo đơn hàng qua API
            JSONObject data = orderApi.createOrder(totalAmount);
            Log.d("ZaloPay", "Data from createOrder: " + data.toString());
            String code = data.getString("returncode");
            if (code.equals("1")) {
                // Lấy token giao dịch từ API
                String token = data.getString("zptranstoken");
                // Gọi thanh toán qua ZaloPay SDK
                ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                        startActivity(new Intent(PaymentDetailActivity.this, OrderActivity.class));
                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                        runOnUiThread(() -> Toast.makeText(PaymentDetailActivity.this,
                                "Bạn đã hủy thanh toán", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                        runOnUiThread(() -> Toast.makeText(PaymentDetailActivity.this,
                                "Lỗi thanh toán: " + zaloPayError.toString(), Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                // Xử lý nếu không nhận được mã returncode = "1"
                String returnMessage = data.optString("return_message", "Unknown error");
                throw new Exception("ZaloPay error: " + returnMessage);
            }
        } catch (Exception e) {
            Log.e("ZaloPay", "Payment error", e);
            Toast.makeText(this, "Lỗi thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Cần bắt sự kiện onNewIntent vì ZaloPay App sẽ gọi deeplink về app của Merchant
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}