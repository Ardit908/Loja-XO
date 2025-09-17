import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TicTacToe extends JFrame implements ActionListener {
    private JButton[] buttons = new JButton[9];
    private char current = 'X';
    private JLabel status;
    private boolean vsComputer = false;
    private String difficulty = "Easy"; 
    private Random rand = new Random();

    // ====== PIKËT ======
    private static int scoreX = 0;
    private static int scoreO = 0;
    private JLabel scoreLabel;

    public TicTacToe(boolean vsComputer, String difficulty) {
        this.vsComputer = vsComputer;
        this.difficulty = difficulty;

        setTitle("X O - TicTacToe");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel board = new JPanel();
        board.setLayout(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 60);

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(font);
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(this);
            board.add(buttons[i]);
        }

        status = new JLabel("Lojtari X nis lojën", SwingConstants.CENTER);
        status.setFont(new Font("Arial", Font.PLAIN, 18));

        scoreLabel = new JLabel(getScoreText(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton resetBtn = new JButton("Rifillo Raundin");
        resetBtn.addActionListener(e -> resetBoard());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(resetBtn, BorderLayout.EAST);

        add(board, BorderLayout.CENTER);
        add(scoreLabel, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        if (!btn.getText().equals("")) return;

        btn.setText(String.valueOf(current));

        if (hasWinner()) {
            handleWin(current);
        } else if (isDraw()) {
            status.setText("Barazim!");
        } else {
            current = (current == 'X') ? 'O' : 'X';
            status.setText("Lëvizja e lojtarit " + current);

            if (vsComputer && current == 'O') {
                computerMove();
            }
        }
    }

    private void computerMove() {
        int move = -1;
        switch (difficulty) {
            case "Easy": move = randomMove(); break;
            case "Medium": move = mediumMove(); break;
            case "Hard": move = bestMove(); break;
        }

        buttons[move].setText("O");

        if (hasWinner()) {
            handleWin('O');
        } else if (isDraw()) {
            status.setText("Barazim!");
        } else {
            current = 'X';
            status.setText("Lëvizja e lojtarit X");
        }
    }

    // ====== LOGJIKA PIKËVE ======
    private void handleWin(char winner) {
        if (winner == 'X') scoreX++;
        else scoreO++;

        status.setText("Fitues: " + winner);
        scoreLabel.setText(getScoreText());
        disableButtons();

        if (scoreX == 10 || scoreO == 10) {
            String msg;
            if (vsComputer) {
                msg = (scoreX == 10) ? "Loja mbaroi!\nFituesi ishte Lojtari!" :
                        "Loja mbaroi!\nFituesi ishte Kompjuteri!";
            } else {
                msg = (scoreX == 10) ? "Loja mbaroi!\nFituesi ishte Lojtari X!" :
                        "Loja mbaroi!\nFituesi ishte Lojtari O!";
            }

            int option = JOptionPane.showOptionDialog(this, msg,
                    "Loja Mbaroi",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, new String[]{"Rifillo nga fillimi"}, "Rifillo nga fillimi");

            if (option == 0) {
                // rifillon loja me pikë nga 0
                scoreX = 0;
                scoreO = 0;
                resetBoard();
                scoreLabel.setText(getScoreText());
            }
        }
    }

    private String getScoreText() {
        if (vsComputer) {
            return "Pikët → Lojtari: " + scoreX + " | Kompjuteri: " + scoreO;
        } else {
            return "Pikët → Lojtari X: " + scoreX + " | Lojtari O: " + scoreO;
        }
    }

    // ====== FUNKSIONE NDËRHËMËS ======
    private int randomMove() {
        int move;
        do {
            move = rand.nextInt(9);
        } while (!buttons[move].getText().equals(""));
        return move;
    }

    private boolean wouldWin(char player, int index) {
        buttons[index].setText(String.valueOf(player));
        boolean win = hasWinner() && checkWinner().equals(String.valueOf(player));
        buttons[index].setText("");
        return win;
    }

    private int mediumMove() {
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("") && wouldWin('O', i)) return i;
        }
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("") && wouldWin('X', i)) return i;
        }
        return randomMove();
    }

    private int bestMove() {
        int bestScore = Integer.MIN_VALUE;
        int move = -1;
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O");
                int score = minimax(0, false);
                buttons[i].setText("");
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        return move;
    }

    private int minimax(int depth, boolean isMaximizing) {
        String winner = checkWinner();
        if (winner != null) {
            if (winner.equals("O")) return 10 - depth;
            else if (winner.equals("X")) return depth - 10;
            else return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("O");
                    int score = minimax(depth + 1, false);
                    buttons[i].setText("");
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("X");
                    int score = minimax(depth + 1, true);
                    buttons[i].setText("");
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private String checkWinner() {
        int[][] lines = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for (int[] L : lines) {
            String a = buttons[L[0]].getText();
            String b = buttons[L[1]].getText();
            String c = buttons[L[2]].getText();
            if (!a.equals("") && a.equals(b) && b.equals(c)) return a;
        }
        boolean draw = true;
        for (JButton b : buttons) if (b.getText().equals("")) draw = false;
        if (draw) return "Draw";
        return null;
    }

    private boolean hasWinner() {
        return checkWinner() != null && !checkWinner().equals("Draw");
    }

    private boolean isDraw() {
        return checkWinner() != null && checkWinner().equals("Draw");
    }

    private void disableButtons() {
        for (JButton b : buttons) b.setEnabled(false);
    }

    private void resetBoard() {
        for (JButton b : buttons) {
            b.setText("");
            b.setEnabled(true);
        }
        current = 'X';
        status.setText("Lojtari X nis lojën");
    }

    // ================== MENUTË ==================
    private static void showWelcomeScreen() {
        JFrame welcome = new JFrame("Mire se erdhe");
        welcome.setSize(400, 200);
        welcome.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcome.setLocationRelativeTo(null);

        JLabel title = new JLabel("Mirë se erdhe në lojën TicTacToe!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton startBtn = new JButton("Fillo lojën");
        JButton aboutBtn = new JButton("Rreth krijuesit");

        startBtn.addActionListener(e -> {
            welcome.dispose();
            showModeSelection();
        });

        aboutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                "Unë jam Ardit Bardhi, 19 vjeç.\n" +
                "Kam mbaruar gjimnazin dhe vazhdoj studimet e larta.\n" +
                "Këtë lojë e kam krijuar vet si projekt personal\n" +
                "për të zhvilluar aftësitë e mia në programim.",
                "Rreth krijuesit", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel panel = new JPanel();
        panel.add(startBtn);
        panel.add(aboutBtn);

        welcome.setLayout(new BorderLayout());
        welcome.add(title, BorderLayout.NORTH);
        welcome.add(panel, BorderLayout.CENTER);

        welcome.setVisible(true);
    }

    private static void showModeSelection() {
        JFrame menu = new JFrame("Zgjidh mënyrën e lojës");
        menu.setSize(400, 200);
        menu.setLocationRelativeTo(null);

        JButton twoPlayers = new JButton("Dy lojtarë");
        JButton vsComputer = new JButton("Luaj me kompjuterin");

        twoPlayers.addActionListener(e -> {
            menu.dispose();
            scoreX = 0; scoreO = 0;
            SwingUtilities.invokeLater(() -> new TicTacToe(false, "Easy").setVisible(true));
        });

        vsComputer.addActionListener(e -> {
            String[] levels = {"Easy", "Medium", "Hard"};
            String diff = (String) JOptionPane.showInputDialog(null,
                    "Zgjidh nivelin e vështirësisë:", "Niveli i lojës",
                    JOptionPane.QUESTION_MESSAGE, null, levels, levels[0]);
            menu.dispose();
            scoreX = 0; scoreO = 0;
            SwingUtilities.invokeLater(() -> new TicTacToe(true, diff).setVisible(true));
        });

        JPanel panel = new JPanel();
        panel.add(twoPlayers);
        panel.add(vsComputer);

        menu.add(panel);
        menu.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showWelcomeScreen());
    }
}
