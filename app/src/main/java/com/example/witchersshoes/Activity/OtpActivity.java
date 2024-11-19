package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.R;
import com.example.witchersshoes.SendMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {
    TextView txtEmail, txtReOtp;
    Button btn_verify;
    FirebaseAuth auth;
    EditText otp1, otp2, otp3, otp4;
    SendMail sendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtEmail = findViewById(R.id.txtEmail);
        txtReOtp = findViewById(R.id.txtReOtp);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btn_verify = findViewById(R.id.btn_verify);
        auth = FirebaseAuth.getInstance();
        sendMail = new SendMail();
        // Tự động chuyển focus giữa các ô OTP
        setOtpFocus(otp1, otp2, null); // Ô đầu tiên, không có previous
        setOtpFocus(otp2, otp3, otp1); // Ô thứ hai, previous là otp1
        setOtpFocus(otp3, otp4, otp2); // Ô thứ ba, previous là otp2
        setOtpFocus(otp4, null, otp3); // Ô cuối cùng, không có next

        Intent intent = getIntent();

        String otpFogotPass = intent.getStringExtra("otpPass");
        String emailFogot = intent.getStringExtra("emailFogot");

        txtEmail.setText(emailFogot);


        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otp1.getText().toString().trim()
                        + otp2.getText().toString().trim()
                        + otp3.getText().toString().trim()
                        + otp4.getText().toString().trim();

                 if(otp.equals(otpFogotPass)){
                    Intent intent = new Intent(OtpActivity.this, ChangePassActivity.class);
                    intent.putExtra("email", emailFogot);
                    startActivity(intent);
                } else{
                    Toast.makeText(OtpActivity.this, "Sai mã OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtReOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMail.Send(OtpActivity.this, emailFogot, "Xác thực", "Mã OTP là " + otpFogotPass);
                    startCountdown();
                } catch (Exception e) {
                    Toast.makeText(OtpActivity.this, "Gửi email thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm hỗ trợ xử lý focus
    private void setOtpFocus(final EditText current, final EditText next, final EditText previous) {
        // Tự động chuyển focus khi nhập xong
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Xử lý khi nhấn phím xóa (Backspace)
        current.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // Kiểm tra nếu ô hiện tại đã rỗng
                    if (current.getText().toString().isEmpty() && previous != null) {
                        previous.requestFocus(); // Chuyển focus về ô trước
                        previous.getText().clear(); // Xóa ký tự trong ô trước
                        return true; // Chặn sự kiện tiếp tục xử lý
                    }
                }
                return false; // Không chặn các sự kiện khác
            }
        });
    }

    // Hàm đếm ngược 30 giây
    private void startCountdown() {
        // Disable the button to prevent clicking again
        txtReOtp.setEnabled(false);

        // Tạo đối tượng CountDownTimer
        new CountDownTimer(30000, 1000) {  // 30 giây, cập nhật mỗi 1 giây
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật UI, ví dụ: hiển thị thời gian còn lại
                txtReOtp.setText("Gửi lại sau: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                // Khi đếm ngược kết thúc, cho phép nhấn lại
                txtReOtp.setEnabled(true);
                txtReOtp.setText("Gửi lại OTP");
            }
        }.start();
    }


}