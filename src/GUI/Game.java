package GUI;

import Engine.Tetromino.Tetromino.TetrominoFactory;
import Engine.Tetromino.Tetromino;
import Engine.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Game
 */
public final class Game {

    /**
     * A singleton instance of {@code Game}.
     */
    public static final Game INSTANCE;

    /**
     * The color of this {@code Game}'s {@code ScorePanel} component.
     */
    public static final Color SCORE_PANEL_COLOR = new Color(10, 10, 10);

    /**
     * The color of this {@code Game}'s {@code LineupPanel} component.
     */
    public static final Color LINEUP_PANEL_COLOR = new Color(30, 30, 30);

    /**
     * The color of this {@code Game}'s {@code HoldPanel} component.
     */
    public static final Color HOLD_PANEL_COLOR = new Color(30, 30, 30);

    /**
     * Dimensions.
     */
    public static final int LENGTH = 706;
    public static final int WIDTH = 617;
    public static final Dimension GAME_FRAME_SIZE;
    public static final Dimension SIDE_PANEL_DIMENSION;

    /* init */
    static {
        GAME_FRAME_SIZE = new Dimension(WIDTH, LENGTH);
        SIDE_PANEL_DIMENSION = new Dimension(140, 706);
        INSTANCE = new Game();
    }

    /**
     * A Frame to hold the GUI.
     */
    private final JFrame gameFrame;

    /**
     * A Panel on which the game will be painted.
     */
    private final GridPanel gamePanel;

    /**
     * A panel on which the current lineup of {@code Tetromino}s will be painted.
     */
    private final LineupPanel lineupPanel;

    /**
     * A panel on which the contents of the {@code Tetromino} hold will be painted.
     */
    private final HoldPanel holdPanel;

    /**
     * A panel on which the current score and level will be painted.
     */
    private final ScorePanel scorePanel;

    /**
     * A private constructor for {@code Game}.
     */
    private Game(){
        gameFrame = new JFrame("J-Tet");
        try { gameFrame.setIconImage(ImageIO.read(
                new File(Utility.IMAGES_PATH + "tetris.png")
        )); } catch(IOException e){ e.printStackTrace(); }
        gameFrame.setSize(GAME_FRAME_SIZE);
        gameFrame.setResizable(false);
        gameFrame.setLayout(new BorderLayout());
        gamePanel = new GridPanel();
        lineupPanel = new LineupPanel(gamePanel.getTetLineup());
        holdPanel = new HoldPanel(gamePanel.getTetHold());
        scorePanel = new ScorePanel();
        gameFrame.add(scorePanel, BorderLayout.NORTH);
        gameFrame.add(gamePanel, BorderLayout.CENTER);
        gameFrame.add(lineupPanel, BorderLayout.EAST);
        gameFrame.add(holdPanel, BorderLayout.WEST);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //Show.
    public static void main(String[] args){
        Game.INSTANCE.gameFrame.setVisible(true);
    }

    /**
     * A method to reset the {@code Game}'s components.
     */
    public final synchronized void reset(){
        gamePanel.reset();
        lineupPanel.update(gamePanel.getTetLineup());
        holdPanel.update(gamePanel.getTetHold());
        scorePanel.reset();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameFrame.getContentPane().revalidate();
                gameFrame.getContentPane().repaint();
            }
        });
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (final Thread t : threads) {
            String name = t.getName();
            Thread.State state = t.getState();
            int priority = t.getPriority();
            String type = t.isDaemon() ? "Daemon" : "Normal";
            System.out.printf("%-20s \t %s \t %d \t %s\n", name, state, priority, type);
        }
        System.out.println("\n\n\n\n");
    }

    /**
     * A method to update the {@code Game}'s components from the {@code GridPanel}
     * class.
     *
     * @param lineup the current lineup of {@code Tetromino}s
     * @param hold the current hold {@code Tetromino}
     * @param level the current level
     * @param score the current score
     */
    public final void update(final List<Tetromino> lineup, final Tetromino hold,
                                          final int level, final int score){
        lineupPanel.update(lineup);
        holdPanel.update(hold);
        scorePanel.update(level, score);
    }

    private static final class LineupPanel extends JPanel {

        private static final int DISPLAY_X_COORDINATE = 72;

        private List<Tetromino> tetLineup;

        private LineupPanel(final List<Tetromino> lineup){
            super();
            setPreferredSize(SIDE_PANEL_DIMENSION);
            updateLineup(lineup);
        }

        private void updateLineup(final List<Tetromino> lineup){
            final List<Tetromino> l = new ArrayList<>();
            int i = -48;
            for(final Tetromino t: lineup) l.add(TetrominoFactory.copyAt(DISPLAY_X_COORDINATE, i += 128, t));
            tetLineup = Utility.lockedList(l);
        }

        private synchronized void update(final List<Tetromino> lineup){
            updateLineup(lineup);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    revalidate();
                    repaint();
                }
            });
        }

        @Override
        public void paintComponent(final Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(LINEUP_PANEL_COLOR);
            g2.fillRect(0,0, 144, 650);
            for(Tetromino t: tetLineup) t.paint(g);
        }

    }

    private static final class HoldPanel extends JPanel {

        private static final int DISPLAY_X_COORDINATE = 72;

        private Tetromino hold;

        private HoldPanel(final Tetromino hold){
            super();
            setPreferredSize(SIDE_PANEL_DIMENSION);
            updateHold(hold);
        }

        private void updateHold(final Tetromino hold){
            this.hold = TetrominoFactory.copyAt(DISPLAY_X_COORDINATE, 80, hold);
        }

        private synchronized void update(final Tetromino hold){
            updateHold(hold);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    revalidate();
                    repaint();
                }
            });
        }

        @Override
        public void paintComponent(final Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(HOLD_PANEL_COLOR);
            g2.fillRect(0,0, 144, 650);
            hold.paint(g);
        }

    }

    private static final class ScorePanel extends JPanel {

        private ScorePanel() {
            super();
            setBackground(SCORE_PANEL_COLOR);
            add(updateLabel(0, 0));
            setVisible(true);
        }

        private synchronized void update(final int level, final int score){
            removeAll();
            add(updateLabel(level, score));
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    revalidate();
                    repaint();
                }
            });
        }

        public void reset(){
            update(0, 0);
        }

        private static JLabel updateLabel(final int level, final int score){
            final String hold = "Hold";
            final String next = "Next";
            final JLabel label = new JLabel();
            label.setText(String.format("%-42sLevel: %-30sScore: %-42s%s", hold, level, score, next));
            label.setForeground(Color.WHITE);
            return label;
        }

    }

}
