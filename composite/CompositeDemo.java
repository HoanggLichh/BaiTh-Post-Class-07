import java.util.ArrayList;
import java.util.List;

/** Composite: cây thư mục. File là lá, Folder là composite; client xử lý cả hai qua cùng interface. */
public class CompositeDemo {

    /** Component: giao diện chung cho phần tử đơn lẻ và nhóm phần tử. */
    interface Node {
        long size();
        void print(String indent);
    }

    /** Leaf: không có con, tự làm việc. */
    static class FileNode implements Node {
        private final String name;
        private final long bytes;
        FileNode(String name, long bytes) { this.name = name; this.bytes = bytes; }
        public long size() { return bytes; }
        public void print(String indent) { System.out.println(indent + name + " (" + bytes + " B)"); }
    }

    /** Composite: giữ danh sách con (Node) và uỷ quyền cho từng con rồi gộp kết quả. */
    static class Folder implements Node {
        private final String name;
        private final List<Node> children = new ArrayList<>();
        Folder(String name) { this.name = name; }
        Folder add(Node n) { children.add(n); return this; }
        public long size() {
            long total = 0;
            for (Node n : children) total += n.size();   // đệ quy: con có thể là Folder khác
            return total;
        }
        public void print(String indent) {
            System.out.println(indent + name + "/ (" + size() + " B)");
            for (Node n : children) n.print(indent + "  ");
        }
    }

    public static void main(String[] args) {
        Folder root = new Folder("project")
            .add(new FileNode("README.md", 1200))
            .add(new Folder("src")
                .add(new FileNode("Main.java", 3400))
                .add(new Folder("util").add(new FileNode("Helper.java", 800))))
            .add(new Folder("empty"));
        root.print("");
        // Client dùng cùng một lời gọi cho file đơn và cho cả cây
        System.out.println("File đơn : " + new FileNode("a.txt", 10).size());
        System.out.println("Cả cây   : " + root.size());
    }
}
