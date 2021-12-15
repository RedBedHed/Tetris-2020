package GUI;

import Engine.Utility;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public enum Palette implements Paintable {

    OCEAN(List.of(
            new Color(10, 180, 200),
            new Color(50, 100, 200),
            new Color(50, 115, 100),
            new Color(10, 50, 150),
            new Color(5, 175, 140),
            new Color(100, 200, 250),
            new Color(20, 20, 100)
    )){
        @Override
        public Palette next() {
            return TROPICAL;
        }

        @Override
        public void paint(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLACK);
            fillBackground(g2);
            g2.drawImage(Image.SEA_FLOOR, 0, 0, 400, 660, null);
            drawLines(g2);
        }
    },
    TROPICAL(List.of(
            new Color( 250, 100, 250),
            new Color(200, 75, 20),
            new Color(200, 100, 10),
            new Color(99, 33, 175),
            new Color(60, 0, 30),
            new Color(200, 33, 75),
            new Color(100, 5, 100)
    )){
        @Override
        public Palette next() {
            return GOLD;
        }

        @Override
        public void paint(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLACK);
            fillBackground(g2);
            g2.drawImage(Image.SUNSET, 0, 0, 850, 660, null);
            drawLines(g2);
        }
    },
    GOLD(List.of(
            new Color(144, 145, 145),
            new Color(99, 34, 19),
            new Color(212, 175, 55),
            new Color(153, 121, 38),
            new Color(144, 145, 145),
            new Color(99, 34, 19),
            new Color(153, 121, 38)
    )){
        @Override
        public Palette next() {
            return HILL;
        }

        @Override
        public void paint(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLACK);
            fillBackground(g2);
            g2.drawImage(Image.OBSIDIAN, 0, 0, 400, 660, null);
            drawLines(g2);
        }
    },
    HILL(List.of(
            new Color(13, 145, 49),
            new Color(28, 94, 57),
            new Color(17, 123, 80),
            new Color(9, 131, 101),
            new Color(1, 68, 19),
            new Color(40, 188, 122),
            new Color(91, 198, 65)
    )){
        @Override
        public Palette next() {
            return STARLIGHT;
        }

        @Override
        public void paint(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLACK);
            fillBackground(g2);
            g2.drawImage(Image.GREEN, 0, 0, 420, 650, null);
            drawLines(g2);
        }
    },
    STARLIGHT(List.of(
            new Color(200, 200, 200),
            new Color(200, 200, 200),
            new Color(200, 200, 200),
            new Color(200, 200, 200),
            new Color(200, 200, 200),
            new Color(200, 200, 200),
            new Color(200, 200, 200)
    )){
        @Override
        public Palette next() {
            return CRAYON;
        }

        @Override
        public void paint(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLACK);
            fillBackground(g2);
            g2.drawImage(Image.NIGHT_SKY, 0, 0, 400, 660, null);
            drawLines(g2);
        }
    },
    CRAYON(List.of(
            new Color(150, 200, 50),
            new Color(200, 30, 30),
            new Color(50, 150, 200),
            new Color(80, 50, 200),
            new Color(225, 150, 50),
            new Color(200, 20, 150),
            new Color(30, 200, 60)
    )){
        @Override
        public Palette next() {
            return FUSE;
        }

        @Override
        public void paint(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(100, 100, 100));
            fillBackground(g2);
            g2.setColor(Color.GRAY);
            drawLines(g2);
        }
    },
    FUSE(List.of(
            new Color(93, 87, 200),
            new Color(200, 42, 183),
            new Color(68, 147, 155),
            new Color(13, 13, 14),
            new Color(233, 68, 132),
            new Color(72, 119, 200),
            new Color(142, 81, 200)
    )){
        @Override
        public Palette next() {
            return OCEAN;
        }

        @Override
        public void paint(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(149, 147, 151));
            fillBackground(g2);
            g2.setColor(Color.DARK_GRAY);
            drawLines(g2);
        }
    };

    public static final int PALETTE_SIZE = 7;

    private final List<Color> colors;

    Palette(final List<Color> colors) {
        this.colors = colors;
    }

    public Color getColor(final int index){
        return colors.get(index);
    }

    private static void fillBackground(final Graphics2D g){
        g.clearRect(
                0, 0, Utility.GRID_WIDTH,
                Utility.GRID_HEIGHT + Utility.SQUARE_BUFFER
        );
        g.fillRect(
                0, 0, Utility.GRID_WIDTH,
                Utility.GRID_HEIGHT + Utility.SQUARE_BUFFER
        );
    }

    private static void drawLines(final Graphics2D g){
        for (int i = 0; i <= Utility.GRID_WIDTH; i += Utility.SQUARE_LENGTH)
            g.drawLine(i, 0, i, Utility.GRID_HEIGHT);
        for (int i = 0; i < Utility.GRID_HEIGHT; i += Utility.SQUARE_LENGTH)
            g.drawLine(0, i, Utility.GRID_WIDTH, i);
    }

    private static final class Image {

        private Image() { }

        private static final BufferedImage OBSIDIAN = load("obs.png");
        private static final BufferedImage SEA_FLOOR = load("floor.png");
        private static final BufferedImage NIGHT_SKY = load("sky.png");
        private static final BufferedImage SUNSET = load("sunset.png");
        private static final BufferedImage GREEN = load("green.png");

        private static BufferedImage load(final String fileName){
            BufferedImage image;
            try {
                image = ImageIO.read(new File(
                    Utility.IMAGES_PATH + fileName
                ));
            } catch(IOException e) {
                image = null; e.printStackTrace();
            }
            return image;
        }

    }

    public abstract Palette next();

}
