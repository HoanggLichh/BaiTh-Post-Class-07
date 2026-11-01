/** Decorator: thêm mã hoá và nén cho nguồn dữ liệu bằng cách bọc lồng nhau, không dùng kế thừa. */
public class DecoratorDemo {

    /** Component: giao diện chung. */
    interface DataSource {
        void write(String data);
        String read();
    }

    /** Concrete component: đối tượng gốc, chỉ lưu dữ liệu thô (giả lập file bằng biến). */
    static class FileDataSource implements DataSource {
        private String stored = "";
        public void write(String data) { stored = data; }
        public String read() { return stored; }
    }

    /** Base decorator: giữ tham chiếu tới DataSource được bọc và chuyển tiếp mọi lời gọi. */
    static abstract class DataSourceDecorator implements DataSource {
        protected final DataSource wrappee;
        DataSourceDecorator(DataSource wrappee) { this.wrappee = wrappee; }
        public void write(String data) { wrappee.write(data); }
        public String read() { return wrappee.read(); }
    }

    /** Concrete decorator 1: mã hoá (Caesar +1, chỉ để minh hoạ). */
    static class EncryptionDecorator extends DataSourceDecorator {
        EncryptionDecorator(DataSource s) { super(s); }
        public void write(String data) { super.write(shift(data, 1)); }
        public String read() { return shift(super.read(), -1); }
        private static String shift(String s, int k) {
            StringBuilder sb = new StringBuilder();
            for (char c : s.toCharArray()) sb.append((char) (c + k));
            return sb.toString();
        }
    }

    /** Concrete decorator 2: "nén" bằng run-length đơn giản (aaa -> 3a). Dữ liệu demo không chứa chữ số. */
    static class CompressionDecorator extends DataSourceDecorator {
        CompressionDecorator(DataSource s) { super(s); }
        public void write(String data) { super.write(compress(data)); }
        public String read() { return decompress(super.read()); }
        private static String compress(String s) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); ) {
                int j = i;
                while (j < s.length() && s.charAt(j) == s.charAt(i)) j++;
                sb.append(j - i).append(s.charAt(i));
                i = j;
            }
            return sb.toString();
        }
        private static String decompress(String s) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i += 2) sb.append(String.valueOf(s.charAt(i + 1)).repeat(s.charAt(i) - '0'));
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        String text = "aaabbbbcc";

        FileDataSource raw = new FileDataSource();
        raw.write(text);
        System.out.println("Thô              : " + raw.read());

        // Bọc nhiều lớp: lớp ngoài cùng xử lý trước khi ghi (nén trước, rồi mới mã hoá), và ngược lại khi đọc
        FileDataSource file = new FileDataSource();
        DataSource stacked = new CompressionDecorator(new EncryptionDecorator(file));
        stacked.write(text);
        System.out.println("Lưu trong file   : " + file.read());
        System.out.println("Đọc lại qua lớp  : " + stacked.read());
        System.out.println("Khớp dữ liệu gốc : " + stacked.read().equals(text));

        // Cùng client, cấu hình khác lúc chạy: chỉ mã hoá
        FileDataSource file2 = new FileDataSource();
        DataSource onlyEnc = new EncryptionDecorator(file2);
        onlyEnc.write("abc");
        System.out.println("Chỉ mã hoá       : " + file2.read() + " -> " + onlyEnc.read());
    }
}
