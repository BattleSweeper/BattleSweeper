package dev.battlesweeper.widgets;

import dev.battlesweeper.event.Event;
import dev.battlesweeper.event.EventHandler;
import dev.battlesweeper.event.MutableEventHandler;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.utils.FontUtils;
import dev.battlesweeper.utils.ResourceUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

public class GameView extends Pane {

    private final int TILE_SIZE;
    private static final int TILE_MARGIN = 4;
    private final int W;
    private final int H;

    private final Position[] minePositions;

    private static final int X_TILES = 16;
    private static final int Y_TILES = 16;

    private static final String TILE_MINE       = "tile_mine.png";
    private static final String TILE_EMPTY      = "tile_empty.png";
    private static final String TILE_FLAGGED    = "tile_flagged.png";
    private static final String TILE_UNKNOWN    = "tile_unknown.png";
    private static final String TILE_UNREVEALED   = "tile_unrevealed.png";

    private static final int COUNT_BOMB  = -1;
    private static final int COUNT_EMPTY = 0;

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_OPEN    = 1;
    private static final int STATE_FLAGGED = 2;

    private final MutableEventHandler eventHandler = new MutableEventHandler();

    private final Tile[][] grid = new Tile[X_TILES][Y_TILES];
    private final Image[] numberImages = new Image[5];

    private final Font fontRegular;
    private final Font fontBold;

    //승리 조건을 임시로 계산하기 위한 변수
    // 게임중에 변경되면 안됩니다(Constant)
    public int totalBomb      = 0;
    private int flagCount      = 0;
    private int validFlagCount = 0;

    public int explodedBomb = 0;
    private long startTimeMillis;

    public int timerValue;



    Text labelFlagsLeft;

    //이미지 경로
    Image imageFlagged = new Image(getIconPath(TILE_FLAGGED));
    Image imageUnrevealed   = new Image(getIconPath(TILE_UNREVEALED));

    public GameView(int width, int height, Position[] mines) {
        super();
        W = width;
        H = height;
        TILE_SIZE = ((W - 40) / 16) - TILE_MARGIN;

        minePositions = mines;

        fontRegular = FontUtils.loadFontFromResource("NanumSquareNeo-bRg.ttf", 20);
        fontBold    = FontUtils.loadFontFromResource("NanumSquareNeo-cBd.ttf", 20);

        for (var i = 0; i < numberImages.length; ++i) {
            var path = getIconPath((i + 1) + ".png");
            numberImages[i] = new Image(path);
        }

        SubScene mineField = new SubScene(createContent(), W, H);

        SubScene topBar = new SubScene(createTopBar(), W,100);

        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_LEFT);
        root.getChildren().addAll(topBar, mineField);
        this.getChildren().add(root);

        startTimeMillis = System.currentTimeMillis();
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    private Parent createTopBar() {

        Pane root1 = new BorderPane();
        TopBar topBar = new TopBar();
        Text timer = new Text(160,32,"0");
        labelFlagsLeft = new Text(450,32, String.valueOf(totalBomb));

        timer.setFont(fontBold);
        labelFlagsLeft.setFont(fontBold);

        root1.setLayoutX(50);
        root1.setLayoutY(30);
        root1.getChildren().add(topBar.topBarImage);
        root1.getChildren().add(timer);
        root1.getChildren().add(labelFlagsLeft);

        eventHandler.listenFor(TileUpdateEvent.class)
                .subscribe(event -> {
                    labelFlagsLeft.setText(String.valueOf(totalBomb - flagCount - explodedBomb));
                });

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timerValue++;
                    if (timerValue <= 999) {
                        timer.setText(Integer.toString(timerValue));
                    } else {
                        //timeline.stop();
                    }
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE); // 무한 반복 설정
        timeline.play();


        return root1;
    }

    private Parent createContent() {
        //지뢰 카운트 초기화
        GridPane root = new GridPane();

        root.setPrefSize(W, H);
        //타일 시작 위치
        root.setLayoutX(25);

        var mines = minePositions;

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = new Tile(x, y);
                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }

        for (var pos : mines) {
            var tile = grid[pos.x()][pos.y()];
            tile.setBombCount(COUNT_BOMB);
            getNeighbors(tile).forEach(Tile::incBombCount);
        }
        totalBomb = mines.length;

        Arrays.stream(grid).forEach(row -> Arrays.stream(row).forEach(Tile::finalizeBombCount));

        return root;
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        // ttt
        // tXt
        // ttt

        int[][] points = new int[][] {
                { -1, -1 },
                { -1,  0 },
                { -1,  1 },
                {  0, -1 },
                {  0,  1 },
                {  1, -1 },
                {  1,  0 },
                {  1,  1 }
        };

        for (var point : points) {
            int dx = point[0];
            int dy = point[1];

            int newX = tile.x + dx;
            int newY = tile.y + dy;

            if (newX >= 0 && newX < X_TILES
                    && newY >= 0 && newY < Y_TILES) {
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }

    private String getIconPath(String res) {
        return ResourceUtils.getIconPath(res);
    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    private class TopBar extends VBox {
        private final ImageView topBarImage;

        public TopBar(){
            topBarImage = new ImageView(new Image(getIconPath("TopBar.png")));
            topBarImage.setFitWidth(W - 100);
            topBarImage.setPreserveRatio(true);
            topBarImage.setSmooth(true);
            topBarImage.setCache(true);
            topBarImage.setVisible(true);
        }
    }

    private class Tile extends StackPane {
        private final int x, y;
        private int bombCount = 0;
        private int state = STATE_DEFAULT;

        private ImageView baseImage;
        private ImageView numberImage;
        private ImageView overlayImage;

        public void setBombCount(int count) {
            this.bombCount = count;
        }

        public void incBombCount() {
            if (this.bombCount == COUNT_BOMB)
                return;
            this.bombCount++;
        }

        public void finalizeBombCount() {
            var count = this.bombCount;
            switch (count) {
                case COUNT_BOMB -> { // 지뢰 있음
                    baseImage = new ImageView(getIconPath(TILE_MINE));
                }
                case COUNT_EMPTY -> { // 주변에 지뢰 없음
                    baseImage = new ImageView(getIconPath(TILE_EMPTY));
                }
                default -> { // 주변에는 있음
                    baseImage = new ImageView(getIconPath(TILE_EMPTY));
                    numberImage = new ImageView(numberImages[count - 1]);
                }
            }
            overlayImage = new ImageView(getIconPath(TILE_UNREVEALED));

            setImageViewAttrs(baseImage);
            getChildren().add(baseImage);
            if (numberImage != null) {
                setImageViewAttrs(numberImage);
                numberImage.setFitWidth(TILE_SIZE - ((double) W / 47));
                getChildren().add(numberImage);
            }

            setImageViewAttrs(overlayImage);
            getChildren().add(overlayImage);
        }

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;

            var totalTileSize = (TILE_SIZE + TILE_MARGIN);
            setTranslateX(x * totalTileSize);
            setTranslateY(y * totalTileSize);

            //이벤트 처리부분
            setOnMouseClicked(e-> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    open();
                }
                if (e.getButton() == MouseButton.SECONDARY) {
                    flag();
                }
            });
        }

        public void open() {
            if (isOpen() || isFlagged())
                return;

            overlayImage.setVisible(false);
            if (hasBomb()) {
                var event = GameOverEvent.builder()
                        .flagCount(flagCount)
                        .time(getElapsedTime())
                        .build();
                explodedBomb++;
                eventHandler.fireEvent(event);
                System.out.println("Game Over");
                //scene.setRoot(createContent());
                return;
            }
            state = STATE_OPEN;
            notifyUpdate(TileUpdateEvent.ACTION_TILE_OPEN);

            if (this.bombCount == COUNT_EMPTY)
                getNeighbors(this).forEach(Tile::open);
        }

        public void flag() {
            if (isOpen())
                return;

            if (isFlagged()) {
                flagCount--;
                if (hasBomb())
                    validFlagCount--;

                state = STATE_DEFAULT;
                overlayImage.setImage(imageUnrevealed);
                notifyUpdate(TileUpdateEvent.ACTION_FLAG_REMOVE);
                return;
            } else if (flagCount >= totalBomb)
                return;

            flagCount++;
            if (hasBomb()) {
                validFlagCount++;
            }
            state = STATE_FLAGGED;
            overlayImage.setImage(imageFlagged);
            notifyUpdate(TileUpdateEvent.ACTION_FLAG_PLACE);

            if (validFlagCount >= totalBomb) {
                var event = new GameWinEvent(getElapsedTime());
                eventHandler.fireEvent(event);
                System.out.println("You Win!");
            }
        }

        private void notifyUpdate(int action) {
            var event = TileUpdateEvent.builder()
                    .action(action)
                    .position(new Position(x, y))
                    .bombLeft(totalBomb - flagCount)
                    .build();
            eventHandler.fireEvent(event);
        }

        public boolean isOpen() {
            return this.state == STATE_OPEN;
        }

        public boolean isFlagged() {
            return this.state == STATE_FLAGGED;
        }

        public boolean hasBomb() {
            return this.bombCount == COUNT_BOMB;
        }

        private void setImageViewAttrs(ImageView image) {
            image.setFitWidth(TILE_SIZE);
            image.setPreserveRatio(true);
            image.setSmooth(true);
            image.setCache(true);
        }
    }

    /* --- Events --- */

    @Getter @Builder @ToString
    public static class TileUpdateEvent implements Event {

        private Position position;
        private int action;
        private int bombLeft;

        public static final int ACTION_TILE_OPEN   = 0;
        public static final int ACTION_FLAG_PLACE  = 1;
        public static final int ACTION_FLAG_REMOVE = 2;
    }

    @Getter @Builder @ToString
    public static class GameOverEvent implements Event {

        private int flagCount;
        private long time;
    }

    @Getter @AllArgsConstructor @ToString
    public static class GameWinEvent implements Event {

        private long time;
    }


}
