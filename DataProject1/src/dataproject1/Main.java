package dataproject1;

public class Main {
    public static Move[] createScenario() {
            return new Move[]{
            new Move(2, 1),new Move(2, 4),new Move(4, 2),new Move(2, 3),new Move(4, 5),
            new Move(2, 2),new Move(4, 5),new Move(8, 1),new Move(8, 1),new Move(32, 2),
            new Move(2, 3),new Move(64, 3),new Move(16, 4),new Move(64, 2),new Move(32, 3),
            new Move(16, 1),new Move(16, 5),new Move(32, 3),new Move(64, 2),new Move(8, 4),
            new Move(4, 4),new Move(2, 4),new Move(2, 4),new Move(2, 2),new Move(64, 3),
            new Move(32, 3),new Move(16, 3),new Move(8, 3),new Move(8, 3),new Move(4, 2),
            new Move(8, 2)
        }; 
    }
    
    public static void main(String[] args) {
        Move[] moves = createScenario();
        
        Game game = new Game(7, 5); //7x5 tahtada oyun oynanacak gameBoard nesnemi tutuyorum
        game.play(moves); //senaryo sırayla oynanırr
    }
}