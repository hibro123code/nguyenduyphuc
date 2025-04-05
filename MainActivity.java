package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    private String currentNumber = ""; // Số đang được nhập
    private String leftOperand = "";   // Toán hạng bên trái
    private String pendingOperation = ""; // Phép toán đang chờ
    private boolean isEnteringNumber = true; // Đánh dấu đang nhập số mới hay không

    // Định dạng số để hiển thị gọn gàng (loại bỏ .0 không cần thiết)
    private DecimalFormat decimalFormat = new DecimalFormat("#.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setText("0"); // Khởi tạo hiển thị là 0
    }

    // Xử lý khi nhấn nút số (0-9)
    public void onDigitClick(View view) {
        Button button = (Button) view;
        String digit = button.getText().toString();

        if (!isEnteringNumber) {
            currentNumber = digit;
            isEnteringNumber = true;
        } else {
            // Nếu đang là "0" thì thay bằng số mới, ngược lại thì nối vào
            if (currentNumber.equals("0")) {
                currentNumber = digit;
            } else {
                currentNumber += digit;
            }
        }
        updateDisplay(currentNumber);
    }

    // Xử lý khi nhấn nút dấu thập phân (.)
    public void onDecimalClick(View view) {
        if (!isEnteringNumber) { // Bắt đầu số mới với "0."
            currentNumber = "0.";
            isEnteringNumber = true;
        } else {
            // Chỉ thêm dấu chấm nếu chưa có
            if (!currentNumber.contains(".")) {
                currentNumber += ".";
            }
        }
        updateDisplay(currentNumber);
    }

    // Xử lý khi nhấn nút phép toán (+, -, X, /)
    public void onOperatorClick(View view) {
        Button button = (Button) view;
        String operator = button.getText().toString();

        // Nếu đã có phép toán đang chờ và đang nhập số mới -> tính kết quả trước
        if (!leftOperand.isEmpty() && !pendingOperation.isEmpty() && isEnteringNumber) {
            calculateResult();
        }

        // Lưu toán hạng bên trái và phép toán
        leftOperand = currentNumber;
        pendingOperation = operator;
        isEnteringNumber = false; // Chuẩn bị nhập số tiếp theo
    }

    // Xử lý khi nhấn nút =
    public void onEqualsClick(View view) {
        if (!leftOperand.isEmpty() && !pendingOperation.isEmpty()) {
            calculateResult();
            pendingOperation = ""; // Reset phép toán chờ sau khi nhấn =
            // isEnteringNumber nên là false để phép toán tiếp theo có thể bắt đầu
            isEnteringNumber = false;
            // Để leftOperand giữ kết quả cho phép tính liên tục (ví dụ: 2+3=5, rồi nhấn +4=9)
            leftOperand = currentNumber;
        }
    }

    // Xử lý khi nhấn nút %
    public void onPercentClick(View view) {
        if (!currentNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(currentNumber);
                value /= 100.0;
                currentNumber = decimalFormat.format(value);
                updateDisplay(currentNumber);
                // Sau khi tính %, coi như đã nhập xong số này
                isEnteringNumber = false;
                // Cập nhật leftOperand nếu cần tính tiếp
                leftOperand = currentNumber;

            } catch (NumberFormatException e) {
                showError("Lỗi định dạng số");
            }
        }
    }


    // Xử lý khi nhấn nút DEL (Xóa ký tự cuối)
    public void onDelClick(View view) {
        if (isEnteringNumber && !currentNumber.isEmpty()) {
            currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
            if (currentNumber.isEmpty()) {
                currentNumber = "0"; // Nếu xóa hết thì về 0
            }
            updateDisplay(currentNumber);
        } else if (!isEnteringNumber) {
            // Nếu vừa thực hiện phép toán hoặc nhấn =, DEL sẽ xóa toàn bộ kết quả và bắt đầu lại
            resetCalculator();
        }
        // Nếu đang hiển thị kết quả (isEnteringNumber = false) và nhấn DEL -> Reset?
        // Hành vi này có thể tùy chỉnh. Hiện tại chỉ xóa khi đang nhập số.
        // Nếu muốn xóa cả kết quả thì cần thêm logic ở đây.
    }


    // Hàm thực hiện tính toán
    private void calculateResult() {
        if (leftOperand.isEmpty() || pendingOperation.isEmpty() || currentNumber.isEmpty()) {
            return; // Không đủ thông tin để tính
        }

        try {
            double left = Double.parseDouble(leftOperand);
            double right = Double.parseDouble(currentNumber);
            double result = 0;

            switch (pendingOperation) {
                case "+":
                    result = left + right;
                    break;
                case "-":
                    result = left - right;
                    break;
                case "X":
                    result = left * right;
                    break;
                case "/":
                    if (right == 0) {
                        showError("Lỗi chia cho 0");
                        resetCalculator(); // Reset sau lỗi
                        return;
                    }
                    result = left / right;
                    break;
            }

            currentNumber = decimalFormat.format(result);
            updateDisplay(currentNumber);
            leftOperand = ""; // Xóa toán hạng trái sau khi tính xong (hoặc có thể giữ lại nếu muốn tính liên tục)


        } catch (NumberFormatException e) {
            showError("Lỗi định dạng số");
            resetCalculator();
        }
    }

    // Cập nhật hiển thị trên TextView
    private void updateDisplay(String text) {
        textViewResult.setText(text);
    }

    // Hiển thị thông báo lỗi ngắn
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Reset trạng thái máy tính
    private void resetCalculator() {
        currentNumber = "0";
        leftOperand = "";
        pendingOperation = "";
        isEnteringNumber = true;
        updateDisplay(currentNumber);
    }
}