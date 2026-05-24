package dataproject1;

public class Game {
    //Oyunun linked list tabanlı tahtası gameboard nesnesi tutuluyo
    private final GameBoard board;

    public Game(int rows, int cols) {
        board = new GameBoard(rows, cols);
    }

    public void play(Move[] moves) {
        int step = 1;

        for (Move move : moves) {
            System.out.println("Step " + step + ": " + move.value + " -> column " + move.column);

            board.printBoard(); //Önce mevcut durum yazdırılıyor hamle yapılmadan önceki halinii
            board.dropNumber(move.value, move.column); //sayı sütuna bırakılıyor
            board.printBoard();  //Taş düştükten sonraki durum yazdırılıyor

            //Eğer merge olduktan sonra tekrar başka bir merge durumu oluşuyorsa aynı sütunda merge işlemleri devam ediyor
            while (board.mergeOnce(move.column)) {
                board.printBoard();
            }

            //Herhangi bir sütun dolduysa oyun bitiyor
            if (board.isGameOver()) {
                System.out.println("Game Over");
                return;
            }
            step++;
        }
       //System.out.println("Scenario finished.");
    }

    //public GameBoard getBoard() {
      //  return board;
    //}
}
