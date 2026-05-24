package dataproject1;

public class GameBoard {
    private final int rows; //tahta satır sayısı
    private final int cols; //tahta sütun sayısı
    private Node head; //tüm node'lara tek head üstünden ulaştığım headimm

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }
    public Node getHead() {
        return head;
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }

    public void dropNumber(int value, int column) {
        if (getColumnSize(column)>=rows) {//seçilen sütunda zaten 7 taş var mı diye kontrol
            throw new IllegalStateException("Column is full: " + column);
        }
        int row = getColumnSize(column) + 1; //yeni taş sütundaki ilk boş satıra düşer
        Node newNode = new Node(value, row, column);
        addToList(newNode); //node u ana zincire ekle
        rebuildLinks();//ek pointer bağlantıları yeniden kurulsun diye
    }

    public boolean mergeOnce(int column) {
        Node base = findMergeBase(column); //sütundaki merge olacak alt node'u bul

        if (base == null || base.up == null) { //merge yoksa çık
            return false; }
        
        Node upper = base.up; //üstteki eş değere sahip node
        base.value *= 2; //alttaki node değeri 2 katına çıksın
        removeFromList(upper); //üstteki node listeden silinsin
        pullDown(column, upper.row);//listede yer değiştirmezler sadece sütundaki üstte kalan node'lar 1 satır aşağı iniyorlar
        rebuildLinks(); //ek pointer bağlantıları tekrar düzenlenir
        return true; //işlemler yapılınca true döndürür
    }

    public boolean hasMergeInColumn(int column) {
        return findMergeBase(column) != null; //bu sütunda merge yapılması gerekiyor mu true false dönüyor, guinin kodlarında kullandım
    }

    public int getColumnSize(int column) { //sütunda kaç taş nodee var bunu sayıyoruz
        int count = 0;
        Node temp = head;

        while (temp != null) { //listedeki düğümler boş olana kadar gez
            if (temp.col == column) { //düğümün sütun numarası bizim sütun numarasına mı eşit
                count++; //o zaman o sütundaki node'ları sayan sayacın değerini arttır
            }
            temp = temp.next; //düğümün sütun numarası uymadı sonraki düğüme bak
        }
        return count;
    }

    public boolean isGameOver() { //c node sayısı eğer satır sayısına eşitse oyunu bitir
        for (int c = 1; c <= cols; c++) { //tahtadaki tüm sütunları tek tek gezen bir döngü
            if (getColumnSize(c) >= rows) { //yukarıdaki getColumnSize metodunu her sütun için çağırıyorum
                return true; //herhangi bir sütun dolduysa oyun bitiyo
            }
        }
        return false;
    }

    public void rebuildLinks() {
        clearAllExtraLinks(); //önce eski ek pointer bağlantıları temizle (up,right,down,leftleri)

        Node temp = head;
        while (temp != null) {
            temp.down = getNode(temp.row - 1, temp.col); //aynı sütunda alt komşuyu down a bağlıyoruz
            temp.up = getNode(temp.row + 1, temp.col); //aynı sütunda üst komşu
            temp.left = findLeftNode(temp.row, temp.col); //aynı satırda soldaki ilk node u bulup bağlıyoruz
            temp.right = findRightNode(temp.row, temp.col); //aynı satırda sağdaki ilk node
            temp = temp.next; //bir sonraki düğüme geçiyoruz bağlantılar için
        }
    }

    public Node getNode(int row, int col) { //bu metodu yazdırırken kullanıyorum mesela
        Node temp = head; //satır ve sütun bilgisine göre geziyoruz head den başlayarak

        while (temp != null) {
            if (temp.row == row && temp.col == col) {
                return temp; //istenen koordinattaki node bulundu
            }
            temp = temp.next;
        }
        return null; //o hücre boş demektir
    }
    
    public void printBoard() {
        System.out.println("-----------------------------");
        for (int r = rows; r >= 1; r--) { //konsol yukarıdan aşağıya yazar o yüzden tersten
            for (int c = 1; c <= cols; c++) { //hangi satırdaysak soldan sağa doğru her sütuna uğruyoruz
                Node node = getNode(r, c); //3. satır 2.sütun dolu mu misall
                if (node == null) {
                    System.out.printf("%4s", "."); //boş hücreyse nokta koyalım
                } else {
                    System.out.printf("%4d", node.value); //dolu hücre ise değeri koyuyorum
                }
            }
            System.out.println();
        }
        System.out.println("-----------------------------");
    }

    private void addToList(Node node) {
        if (head == null) { //eğer hiç düğüm yoksa
            head = node; //ilk node head olur
            return;
        }
        Node temp = head; //düğümleri gezmek için
        while (temp.next != null) { //null olana kadar gez
            temp = temp.next; //ana zincirin sonuna git
        }
        temp.next = node; //yeni node u sona bağla
    }

    private void removeFromList(Node target) {//bir node silinecekse->target diyorum
        if (head == null || target == null) {//liste boşsa veya silinecek taş yoksa
            return;//bir şey yapma
        }

        if (head == target) { //1. eğer hedef node : head ise
            head = head.next; //head i ilerletiyorum ki tüm listeyi kaybetmeyeyim
            clearExtraLinks(target); //silinen node un tüm bağlantılarını tamamen sileyim ki 
            target.next = null; //tüm bağlar koptuu
            return;
        }
        //2. aradan düğüm silinecek ise
        Node prev = head;//temp in önceki düğümünü tutuyoruz aslında (en baştaki düğüm)
        Node temp = head.next;//sonrakini tutuyoruz (ikinci düğüm)

        while (temp != null) {
            if (temp == target) { //silinecek düğüm target=temp silinecek
                prev.next = temp.next; //hedef node'u zincirden çıkar: 1in nextini 2nin nextine '3e bağladım ki' 2 silinsin
                clearExtraLinks(temp); //up,down,right,left bsğlsntılarını temizliyoruz
                temp.next = null;//nextini de null yaptık
                return;
            }
            prev = temp; //prev ve templeri bir adım ileriye taşırız
            temp = temp.next;
        }
    }

    private Node findMergeBase(int column) { //alt alta aynı sayı var mı
        Node temp = findBottomNode(column); //en alttan başlamak için temp

        while (temp != null && temp.up != null) { 
            if (temp.value == temp.up.value) { //node ve up ının değeri aynı mı
                return temp; //merge olacak alt node bulunmuş olur, alttaki 2 katına çıkacak üsttekini sileceğim
            }
            temp = temp.up; //yukarı çıkıp kontrol etmeye devam et
        }
        return null;
    }

    private Node findBottomNode(int column) {
        Node temp = head;
        Node bottom = null; //en alttaki düğüm değişkeni

        while (temp != null) {
            if (temp.col == column) { //olduğumuz sütundaki satır değeri en düşük düğümü arıyoruz
                if (bottom == null || temp.row < bottom.row) {
                    bottom = temp; //ilgili sütundaki en alt node
                }
            }
            temp = temp.next;//satıra ait düğümleri bulana kadar next
        }
        return bottom;
    }

    private void pullDown(int column, int removedRow) {
        Node temp = head; 
        //en baştan tüm listeyi kontrol edelim
        while (temp != null) { 
            //taş işlem yaptığımız sütunda mı + taş silinenin üstünde mi
            if (temp.col == column && temp.row > removedRow) {//mergeOnce metodunda upper.row değeri atıyoruz burdan removedRow a
                temp.row--; //silinen node'un üstündekiler bir satır aşağı iner
            }
            temp = temp.next;
        }
    }

    private Node findLeftNode(int row, int col) {
        for (int c = col - 1; c >= 1; c--) { //bulunduğumuz sütunun solundan(col-1), 1. sütuna kadar gideriz c-- ile
            Node node = getNode(row, c); //node var mı diye bakıyoruz
            if (node != null) { //getNode boş dönmediyse soldakini bulduk demek oluyor
                return node; //soldaki ilk dolu hücre olarak döndürr
            }
        }
        return null;
    }

    private Node findRightNode(int row, int col) { //bu da sağ taraf içinn
        for (int c = col + 1; c <= cols; c++) {
            Node node = getNode(row, c);
            if (node != null) {
                return node; //sağdaki ilk dolu hücre
            }
        }
        return null;
    }

    private void clearAllExtraLinks() { //istisnasız her taşın kollarını temizlemek için
        Node temp = head; //ana zincirden başla gezz

        while (temp != null) { //sona kadar listeyi dolaşıyorum
            clearExtraLinks(temp); //her bir taş için o node un yan bağlantılarını sıfırla
            temp = temp.next; //sonrakine geç
        }
    }

    private void clearExtraLinks(Node node) { //tek bir node un dört yanındaki bağlantıları temizleme
        node.up = null; //next i temizlemedik o ana zincirim, onu null yaparsam kaybolur bunu istemeyizz
        node.down = null; //bu temizliğin amacı taşların tahtadaki komşularıyla olan bağlarını temizlemek
        node.left = null; //merge ve pulldown işlemleri olduğunda bağlantılarda hatalar oluşmasın diye varlar
        node.right = null;
    }
}