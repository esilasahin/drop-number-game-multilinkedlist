package dataproject1;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

/**
 *
 * @author esila
 */
public class GameFrame extends javax.swing.JFrame {

    private final int ROWS = 7;
    private final int COLS = 5;

    private JLabel[][] cells; //Görsel kutucuklar, bunlarda merge güncellemeleri vs oluyor
    private GameBoard board;//Arkadaki bağlı liste yapım
    private Move[] moves; //Yapılacak hamlelerin listesi

    private int moveIndex; //bilgi panelinde hamle sayısını göstericem
    private boolean waitingMerge; //bir sonraki tıklama işlemini bekle ne olacak diye bak
    private int mergeColumn; //merge yapılan column bilgisii ekranda yazıyorum bunu da

    /**
     * Creates new form GameFrame
     */
    public GameFrame(Move[] moves) {
        initComponents();

        board = new GameBoard(7, 5);
        this.moves = moves; //mainden hamleleri çekip aldık burda

        moveIndex = 0; //hamle yapılmadı daha şu an sıfırr
        waitingMerge = false;//bekleyen işlem yokk
        mergeColumn = -1; //geçerli bir sütun yok

        prepareBoardPanel(); //panel düzeninin ayarı
        createCellLabels(); //35 label oluşturup ekrana diz
        refreshBoard(); //taşları boardımda göstermek için
        refreshInfo(); //ekranda yazan bilgileri güncelleme metodu
    }

    private void prepareBoardPanel() {
        //Oyun panelini row 7 x col 5 kutuluk bir düzen olarak oluşturur
        boardPanel.setLayout(new GridLayout(ROWS, COLS, 6, 6));
        boardPanel.setBackground(new Color(245, 245, 245));
    }

    private void createCellLabels() {
        //Tahtadaki 35 jlabel burada kodla oluşturdum
        //ekranda tek tek label eklemek yerine bu yöntem daha düzenli
        cells = new JLabel[ROWS][COLS]; //kutuları arrayde tutuyoruz
        boardPanel.removeAll(); //daha önceden panelde bir şey varsa temizle

        //satırları tersten ekliyoruz ki row=1 olan kısım ekranda en altta görünsün
        for (int row = ROWS - 1; row >= 0; row--){//6dan başlayıp 0a kadar gittik ki görünüm bize göre olsun
            for (int col = 0; col < COLS; col++) {
                JLabel label = new JLabel("", SwingConstants.CENTER); //sayı kutunun tam ortasında dursun
                label.setOpaque(true); //arka plan rengi gözüksün mor pembe renkleriim
                label.setBackground(new Color(245, 245, 245)); //başta varsayılan renk
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); //kutuların etrafına ince çizgide çerçeve çiziyor
                label.setFont(new Font("Arial", Font.BOLD, 22));

                cells[row][col] = label; // kutuları dizide tutuyoruz ki değişim olunca ulaşabilelimm
                boardPanel.add(label); // kutuyu ekrana ekler
            }
        }
        boardPanel.revalidate(); //bunu java onaylıyor layout düzenini
        boardPanel.repaint(); //yeniden boyama yapıyor ekrana çizim kısmı
    }

    private void refreshBoard() {
        //Linked list içindeki mevcut board durumunu ekrana aktarıyoruz 35 kareyi tek tek gezerek
        for (int row = 1; row <= board.getRows(); row++) {
            for (int col = 1; col <= board.getCols(); col++) {
                JLabel cell = cells[row - 1][col - 1];
                Node node = board.getNode(row, col); //GameBoard verim yani mevcut bağlı listem
                //getNode ile koordinat boş mu dolu mu diye bakıyoruz
                if (node == null) { //null ise boş yap rengini griye çevirr
                    cell.setText("");
                    cell.setBackground(new Color(245, 245, 245));
                } else { //eğer node varsa değeri yazdırıp o değerin rengine boya kutuyuu
                    cell.setText(String.valueOf(node.value));
                    cell.setBackground(getCellColor(node.value));
                }
            }
        }
    }

    private void refreshInfo() {
        //bilgi alanlarını güncelleme
        lblStepCount.setText("Number of moves made: " + moveIndex);

        if (waitingMerge) { //bekleyen merge var mı kontrolü
            lblNextMove.setText("Next action: merge in column " + mergeColumn);
        } else if (moveIndex < moves.length) {//yapılabilecek hamle var mı kontrolü
            lblNextMove.setText("Next action: number " + moves[moveIndex].value
                    + " will drop into column " + moves[moveIndex].column); //hamle listemden sıradaki sayı ve sütun bilgisini çektim
        } else { //hamle kalmadıysa senaryo oyunumuz bittii
            lblNextMove.setText("Scenario completed, game over!");
        }

        if (!waitingMerge && board.isGameOver()) {//merge bekleniyorsa oyun henüz bitmiş sayılmasın
            lblStatus.setText("Status: Game over. A column is full.");
            btnNext.setEnabled(false); //next butonu artık tıklanamaz
        } else if (moveIndex >= moves.length && !waitingMerge) {
            lblStatus.setText("Status: All steps completed.");
            btnNext.setEnabled(false);
        }
    }

    private Color getCellColor(int value) {
        // Sayılara göre kutuların rengi değişsin diye görsel ayar
        return switch (value) { //netbeans switch case imi bu şekile çevirdi
            case 2 -> new Color(255,240,245);
            case 4 -> new Color(255, 192, 203);
            case 8 -> new Color(255, 105, 180);
            case 16 -> new Color(219, 112, 147);
            case 32 -> new Color(221, 160, 221);
            case 64 -> new Color(218, 112, 214);
            case 128 -> new Color(216, 191, 216);
            case 256 -> new Color(230, 230, 250);
            default -> new Color(220, 220, 220);
        };
    }

    private void nextStep() {
        //bekleyen merge varsa önce onu gösteriyoruz
        if (waitingMerge) {
            board.mergeOnce(mergeColumn); //merge işlemi oluyor
            waitingMerge = board.hasMergeInColumn(mergeColumn); //birleşmeden sonra sütuna tekrar bakarız ki yine merge var mı

            if (waitingMerge) { //tekrar merge durumuu
                lblStatus.setText("Status: There is another merge step in column " + mergeColumn + ".");
            } else {
                lblStatus.setText("Status: Merge completed.");
            }
        } else {
            //senaryodaki hamleler bittiyse butonu kapatıyoruz
            if (moveIndex >= moves.length) {
                lblStatus.setText("Status: All steps completed.");
                btnNext.setEnabled(false);
                return;
            }

            //Sıradaki hamleyi mevcut GameBoard üzerine uyguluyoruz
            Move move = moves[moveIndex];
            board.dropNumber(move.value, move.column);
            moveIndex++;

            if (board.hasMergeInColumn(move.column)) {
                waitingMerge = true;
                mergeColumn = move.column;
                lblStatus.setText("Status: Step " + moveIndex + ": number " + move.value
                        + " dropped into column " + move.column
                        + ". There is a merge in this column. It will merge on the next click.");
            } else {
                lblStatus.setText("Status: Step " + moveIndex + ": number " + move.value
                        + " dropped into column " + move.column + ".");
            }
        }

        refreshBoard();//kutuları sayılara göre yeniden boya
        refreshInfo();//bilgi yazılarını güncelle
    }

    private void resetSimulation() {
        //Aynı senaryoyla başa dönüyoruz , sayaçlar sıfırlandı
        board = new GameBoard(7, 5);

        moveIndex = 0;
        waitingMerge = false;
        mergeColumn = -1;

        refreshBoard();
        lblStatus.setText("Status: Game reset.");
        btnNext.setEnabled(true);
        refreshInfo();
    }

    //Bu metod, oyunun penceresini bilgisayarın kendi işletim sistemi temasıyla (Windows/Mac gibi) uyumlu
    //ve güvenli bir şekilde başlattığı için silmedim yoksa benim arayüzümde problemsiz çalışıyor
    public static void open(Move[] moves) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        //arayüz donmadan, güvenli bir şekilde açılsın diye
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameFrame(moves).setVisible(true);
            }
        });
    } 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblStepCount = new javax.swing.JLabel();
        lblNextMove = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        boardPanel = new javax.swing.JPanel();
        btnNext = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblStepCount.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblStepCount.setText("Number of moves made: 0");

        lblNextMove.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblNextMove.setText("Next action: -");

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblStatus.setText("Status: Simulation ready");

        boardPanel.setPreferredSize(new java.awt.Dimension(500, 420));

        javax.swing.GroupLayout boardPanelLayout = new javax.swing.GroupLayout(boardPanel);
        boardPanel.setLayout(boardPanelLayout);
        boardPanelLayout.setHorizontalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
        );
        boardPanelLayout.setVerticalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );

        btnNext.setText("Next Step");
        btnNext.setPreferredSize(new java.awt.Dimension(82, 23));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 3, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 204));
        jLabel1.setText("DROP NUMBER GAME");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(181, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(89, 89, 89))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(242, 242, 242))))
            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(248, 248, 248)
                        .addComponent(jLabel1))
                    .addComponent(lblNextMove, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStepCount, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(lblStepCount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNextMove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatus)
                .addGap(12, 12, 12)
                .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        resetSimulation();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        nextStep();
    }//GEN-LAST:event_btnNextActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameFrame(Main.createScenario()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel boardPanel;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnReset;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblNextMove;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStepCount;
    // End of variables declaration//GEN-END:variables
}
