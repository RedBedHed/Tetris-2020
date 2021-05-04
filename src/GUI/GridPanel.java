package GUI;

import Engine.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import Engine.Tetromino.Tetromino;
import Engine.Tetromino.Tetromino.TetrominoFactory;
import Engine.Navigation.TetrisLandscape;

// Boilerplate
public final class GridPanel extends JPanel {

    public static final int PANEL_WIDTH;
    public static final int PANEL_HEIGHT;
    public static final Dimension PANEL_DIMENSION;
    public static final Color TEXT_COLOR;
    public static final int INITIAL_SCROLL_UPDATE_LIMIT;
    public static final int INITIAL_LEVEL_SCORE_LIMIT;
    public static final int FIRST_LEVEL_SCORE_LIMIT;
    public static final int TIMER_DELAY;
    public static final GridPanel INSTANCE;

    static {
        PANEL_WIDTH = Utility.GRID_WIDTH;
        PANEL_HEIGHT = Utility.GRID_HEIGHT;
        PANEL_DIMENSION = new Dimension(Utility.GRID_WIDTH, Utility.GRID_HEIGHT);
        TEXT_COLOR = new Color(200,200,200);
        INITIAL_SCROLL_UPDATE_LIMIT = 64;
        INITIAL_LEVEL_SCORE_LIMIT = 2048;
        FIRST_LEVEL_SCORE_LIMIT = 8192;
        TIMER_DELAY = 10;
        INSTANCE = new GridPanel();
    }

    private Tetromino currentTet;
    private Tetromino ghostTet;
    private List<Tetromino> tetLineup;
    private Tetromino tetHold;
    private int scrollUpdateCount;
    private int tetTransitionUpdateCount;
    private int levelTransitionUpdateCount;
    private int levelUpLimit;
    private int scrollUpdateLimit;
    private TetrisLandscape landscape;
    private TransitionStatus impactTransitionStatus;
    private TransitionStatus levelTransitionStatus;
    private GameStatus gameStatus;
    private boolean held;
    private ButtonType buttonType;
    private int level;
    private Palette palette;

    private GridPanel(){
        setLayout(null);
        setSize(PANEL_DIMENSION);
        init();
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e){
                if(gameStatus.isGameOver()){
                    if(buttonType.isNo())
                        System.exit(0);
                    else if(buttonType.isYes()){
                        Game.INSTANCE.reset();
                        gameStatus = GameStatus.RUNNING;
                    }
                } else {
                    gameStatus = gameStatus.pause();
                    updatePanel();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e){
                if(gameStatus.isGameOver()) {
                    buttonType = ButtonType.select(e);
                    updatePanel();
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e){
                final int kc = e.getKeyCode();
                if(kc == KeyEvent.VK_ESCAPE) {
                    INSTANCE.gameStatus = INSTANCE.gameStatus.pause();
                    INSTANCE.updatePanel();
                    return;
                }
                if(INSTANCE.gameStatus.isRunning()) {
                    if(kc == KeyEvent.VK_SPACE) {
                        if (!INSTANCE.ghostTet.isNull()) {
                            INSTANCE.currentTet = INSTANCE.ghostTet.manifest();
                            INSTANCE.ghostTet = TetrominoFactory.NULL_TET;
                            INSTANCE.postImpactUpdate();
                        }
                    } else if (kc == KeyEvent.VK_UP || kc == KeyEvent.VK_W) {
                        final Tetromino temp = INSTANCE.currentTet;
                        if ((INSTANCE.currentTet =
                                INSTANCE.landscape.tryRotation(INSTANCE.currentTet)) != temp)
                            INSTANCE.ghostTet = findGhost(INSTANCE.currentTet);
                    } else if (kc == KeyEvent.VK_LEFT || kc == KeyEvent.VK_A) {
                        final Tetromino temp = INSTANCE.currentTet;
                        if ((INSTANCE.currentTet =
                                INSTANCE.landscape.tryMovingLeft(INSTANCE.currentTet)) != temp)
                            INSTANCE.ghostTet = findGhost(INSTANCE.currentTet);
                    } else if (kc == KeyEvent.VK_RIGHT || kc == KeyEvent.VK_D) {
                        final Tetromino temp = INSTANCE.currentTet;
                        if ((INSTANCE.currentTet =
                                INSTANCE.landscape.tryMovingRight(INSTANCE.currentTet)) != temp)
                            INSTANCE.ghostTet = findGhost(INSTANCE.currentTet);
                    } else if (kc == KeyEvent.VK_DOWN || kc == KeyEvent.VK_S) {
                        final Tetromino temp = INSTANCE.currentTet;
                        if ((INSTANCE.currentTet =
                                INSTANCE.landscape.tryFalling(INSTANCE.currentTet)) != temp)
                            INSTANCE.ghostTet = findGhost(INSTANCE.currentTet);
                    } else if (kc == KeyEvent.VK_C) {
                        if (!INSTANCE.held && INSTANCE.currentTet != TetrominoFactory.NULL_TET) {
                            if (INSTANCE.tetHold == TetrominoFactory.NULL_TET) {
                                INSTANCE.tetHold = INSTANCE.currentTet.respawn();
                                INSTANCE.transition();
                            } else {
                                final Tetromino temp = INSTANCE.currentTet.respawn();
                                INSTANCE.currentTet = INSTANCE.tetHold;
                                INSTANCE.tetHold = temp;
                                INSTANCE.ghostTet = findGhost(INSTANCE.currentTet);
                            }
                            INSTANCE.held = true;
                            Game.INSTANCE.update(
                                    INSTANCE.tetLineup, INSTANCE.tetHold,
                                    INSTANCE.level, INSTANCE.landscape.getScore()
                            );
                            INSTANCE.updatePanel();
                        }
                    }
                }
            }
        });
        new Timer(TIMER_DELAY, new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(gameStatus.isRunning()) performTimerAction();
            }
        }).start();
    }

    private synchronized void init(){
        currentTet = TetrominoFactory.NULL_TET;
        ghostTet = TetrominoFactory.NULL_TET;
        landscape = TetrisLandscape.defaultInstance();
        palette = Palette.OCEAN;
        tetLineup = TetrominoFactory.generateLineup(palette);
        tetHold = TetrominoFactory.NULL_TET;
        impactTransitionStatus = TransitionStatus.ACTIVE;
        levelTransitionStatus = TransitionStatus.INACTIVE;
        buttonType = ButtonType.NONE;
        tetTransitionUpdateCount = 0;
        levelTransitionUpdateCount = 0;
        scrollUpdateCount = 0;
        levelUpLimit = INITIAL_LEVEL_SCORE_LIMIT;
        level = 0;
        scrollUpdateLimit = INITIAL_SCROLL_UPDATE_LIMIT;
        held = false;
        gameStatus = GameStatus.RUNNING;
    }

    public void reset(){
        init();
    }

    public synchronized Tetromino getTetHold(){
        return tetHold;
    }

    protected synchronized List<Tetromino> getTetLineup(){
        return tetLineup;
    }

    public enum GameStatus {
        RUNNING {
            @Override public boolean isRunning() {     return true; }
            @Override public boolean isPaused() {     return false; }
            @Override public boolean isGameOver() {   return false; }
            @Override public GameStatus pause() {    return PAUSED; }
        },
        PAUSED {
            @Override public boolean isRunning() {    return false; }
            @Override public boolean isPaused() {      return true; }
            @Override public boolean isGameOver() {   return false; }
            @Override public GameStatus pause() {   return RUNNING; }
        },
        GAME_OVER {
            @Override public boolean isRunning() {    return false; }
            @Override public boolean isPaused() {     return false; }
            @Override public boolean isGameOver() {    return true; }
            @Override public GameStatus pause() { return GAME_OVER; }
        };
        public abstract boolean isRunning();
        public abstract boolean isPaused();
        public abstract boolean isGameOver();
        public abstract GameStatus pause();
    }

    private synchronized void performTimerAction(){
        if(levelTransitionStatus.isActive()) {
            levelTransitionUpdateCount++;
            if (levelTransitionUpdateCount >= 200) {
                levelTransitionUpdateCount = 0;
                palette = palette.next();
                landscape = landscape.reColor(palette);
                final List<Tetromino> replacementLineup = new ArrayList<>();
                for (Tetromino t : tetLineup) replacementLineup.add(t.reColor(palette));
                tetLineup = Utility.lockedList(replacementLineup);
                currentTet = currentTet.reColor(palette);
                ghostTet = ghostTet.reColor(palette);
                tetHold = tetHold.reColor(palette);
                levelTransitionStatus = TransitionStatus.INACTIVE;
            }
        } else {
            if (landscape.getScore() >= levelUpLimit) {
                if(level == 0) levelUpLimit = FIRST_LEVEL_SCORE_LIMIT;
                else levelUpLimit <<= 1;
                scrollUpdateLimit = INITIAL_SCROLL_UPDATE_LIMIT - (level << 3);
                level++;
                levelTransitionStatus = TransitionStatus.ACTIVE;
                updatePanel();
                Game.INSTANCE.update(tetLineup, tetHold, level, landscape.getScore());
            } else if (impactTransitionStatus.isActive()) {
                tetTransitionUpdateCount++;
                if (tetTransitionUpdateCount >= 35) {
                    tetTransitionUpdateCount = 0;
                    scrollUpdateCount = 0;
                    if (landscape.contains(tetLineup.get(0))) {
                        currentTet = TetrominoFactory.NULL_TET;
                        gameStatus = GameStatus.GAME_OVER;
                        updatePanel();
                        return;
                    } else {
                        transition();
                        Game.INSTANCE.update(tetLineup, tetHold, level, landscape.getScore());
                    }
                    impactTransitionStatus = TransitionStatus.INACTIVE;
                }
            } else {
                scrollUpdateCount++;
                if (scrollUpdateCount >= scrollUpdateLimit) {
                    scrollUpdateCount = 0;
                    if (landscape.isImpactedBy(currentTet)) {
                        postImpactUpdate();
                        return;
                    } else {
                        currentTet = currentTet.fall();
                        ghostTet = findGhost(currentTet);
                    }
                }
            }
            updatePanel();
        }
    }

    private synchronized void postImpactUpdate(){
        landscape = landscape.merge(currentTet, level);
        updatePanel();
        Game.INSTANCE.update(tetLineup, tetHold, level, landscape.getScore());
        impactTransitionStatus = TransitionStatus.ACTIVE;
        currentTet = TetrominoFactory.NULL_TET;
        ghostTet = TetrominoFactory.NULL_TET;
        held = false;
    }

    private synchronized void updatePanel(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                revalidate();
                repaint();
            }
        });
    }

    private enum TransitionStatus {
        ACTIVE {   @Override public boolean isActive() { return true;  }},
        INACTIVE { @Override public boolean isActive() { return false; }};
        public abstract boolean isActive();
    }

    private enum ButtonType {

        YES {
            @Override public boolean isYes(){  return true; }
            @Override public boolean isNo(){  return false; }
        },
        NO {
            @Override public boolean isYes(){ return false; }
            @Override public boolean isNo(){   return true; }
        },
        NONE {
            @Override public boolean isYes(){ return false; }
            @Override public boolean isNo(){  return false; }
        };

        public static ButtonType select(MouseEvent e){
            final int x = e.getX();
            final int y = e.getY();
            final boolean WITHIN_YES_X_INTERVAL =  (x > 75 && x < 168);
            final boolean WITHIN_YES_Y_INTERVAL = (y > 395 && y < 450);
            final boolean WITHIN_NO_X_INTERVAL  = (x > 168 && x < 250);
            final boolean WITHIN_NO_Y_INTERVAL  = (y > 395 && y < 450);
            return (WITHIN_YES_X_INTERVAL && WITHIN_YES_Y_INTERVAL) ? YES:
            (WITHIN_NO_X_INTERVAL && WITHIN_NO_Y_INTERVAL)? NO: NONE;
        }

        public abstract boolean isYes();
        public abstract boolean isNo();

    }

    private static Tetromino findGhost(final Tetromino currentTet){
        if(currentTet.isNull()) return currentTet;
        int x = currentTet.getAxis().x;
        int y = currentTet.getAxis().y;
        Tetromino ghost = currentTet.copyAt(x, y);
        while(!INSTANCE.landscape.isImpactedBy(ghost))
            ghost = ghost.fall();
        return ghost.dematerialize();
    }

    private void transition(){
        currentTet = tetLineup.get(0);
        ghostTet = findGhost(currentTet);
        final List<Tetromino> replacementTetLineup = new ArrayList<>(12);
        for (int i = 1; i < tetLineup.size(); i++) replacementTetLineup.add(tetLineup.get(i));
        if (tetLineup.size() <= 5) replacementTetLineup.addAll(TetrominoFactory.generateLineup(palette));
        tetLineup = Utility.lockedList(replacementTetLineup);
    }

    @Override
    public void paintComponent(Graphics g) {
        palette.paint(g);
        ghostTet.paint(g);
        currentTet.paint(g);
        landscape.paint(g);
        final int TITLE_SIZE = 50;
        if (gameStatus.isPaused()) {
            final float OPACITY = 0.55f;
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.SrcOver.derive(OPACITY));
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(
                    0,0, PANEL_WIDTH + Utility.SQUARE_BUFFER,
                    PANEL_HEIGHT + Utility.SQUARE_BUFFER
            );
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("TimesRoman", Font.BOLD, TITLE_SIZE));
            g2.drawString("PAUSED", 57, 260);
        } else if(gameStatus.isGameOver()) {
            final float OPACITY = 0.55f;
            final int SUB_TEXT_SIZE = 25;
            final int LOG_2_8 = 3;
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.SrcOver.derive(OPACITY));
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(
                    0,0, PANEL_WIDTH + Utility.SQUARE_BUFFER,
                    PANEL_HEIGHT + Utility.SQUARE_BUFFER
            );
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("TimesRoman", Font.BOLD, TITLE_SIZE));
            g2.drawString("GAME OVER", 6, 260);
            g2.setFont(new Font("TimesRoman", Font.PLAIN, SUB_TEXT_SIZE));
            g2.drawString(String.format(
                    "Your score is %d", landscape.getScore()),
                    90 - (Utility.countPositiveDigits(
                            landscape.getScore()) << LOG_2_8
                    ), 290
            );
            g2.drawString("Restart?", 115,400);
            if(buttonType.isYes()) g2.setColor(Color.BLACK);
            g2.drawString("Yes", 110, 430);
            if(buttonType.isNo()) g2.setColor(Color.BLACK);
            else g2.setColor(TEXT_COLOR);
            g2.drawString("No", 180, 430);
        } else if(levelTransitionStatus.isActive()){
            final float OPACITY = 0.55f;
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.SrcOver.derive(OPACITY));
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(
                    0,0, PANEL_WIDTH + Utility.SQUARE_BUFFER,
                    PANEL_HEIGHT + Utility.SQUARE_BUFFER
            );
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("TimesRoman", Font.BOLD, TITLE_SIZE));
            g2.drawString("LEVEL UP", 36, 260);
        }
    }

}
