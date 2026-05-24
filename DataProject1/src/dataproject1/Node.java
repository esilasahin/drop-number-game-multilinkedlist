package dataproject1;

public class Node {
    //taşın değeri ve tahtadaki konumu
    int value;
    int row;
    int col;

    //next ana linked list zinciri için kullanılıyor.
    //diğer pointer'lar komşuluk ilişkilerini göstermek için ek olarak var
    Node next;   // ana zincir
    Node up;     // ek pointer
    Node down;   // ek pointer
    Node left;   // ek pointer
    Node right;  // ek pointer

    public Node(int value, int row, int col) {
        this.value = value;
        this.row = row;
        this.col = col;
    }
}
