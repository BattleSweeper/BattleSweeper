package dev.battlesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dev.battlesweeper.utils.FontUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BattlesweeperApp extends Application {

    private int TILE_SIZE;
    private static final int TILE_MARGIN = 4;
    private static final int W = 800;
    private static final int H = 800;

    private static final int X_TILES = 16;
    private static final int Y_TILES = 16;

    private static final String RES_PATH = "icon/";

    private static final String TILE_MINE       = "tile_mine.png";
    private static final String TILE_EMPTY      = "tile_empty.png";
    private static final String TILE_FLAGGED    = "tile_flagged.png";
    private static final String TILE_UNKNOWN    = "tile_unknown.png";
    private static final String TILE_REVEALED   = "tile_revealed.png";

    private static final int COUNT_BOMB  = -1;
    private static final int COUNT_EMPTY = 0;

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_OPEN    = 1;
    private static final int STATE_FLAGGED = 2;

    private final Tile[][] grid = new Tile[X_TILES][Y_TILES];
    private final Image[] numberImages = new Image[5];
    private Scene scene;

    private Font fontRegular;
    private Font fontBold;

    //승리 조건을 임시로 계산하기 위한 변수
    private int totalBomb = 0;
    private int flagCount = 0;
    
    //이미지 경로
    Image FlaggedImage_path = new Image(getIconPath(TILE_FLAGGED));

    // FXML 및 게임 로드 부분
    // stackpane을 사용했지만 둘 다 불러와지지 않고 게임이 덮어씌워지는 문제가 있음.
    public void start(Stage stage) {

        try {
            TILE_SIZE = ((W - 40) / 16) - TILE_MARGIN;

            fontRegular = FontUtils.loadFontFromResource("NanumSquareNeo-bRg.ttf", 20);
            fontBold    = FontUtils.loadFontFromResource("NanumSquareNeo-cBd.ttf", 20);

            for (var i = 0; i < numberImages.length; ++i) {
                var path = getIconPath((i + 1) + ".png");
                numberImages[i] = new Image(path);
            }

            SubScene subSceneTwo = new SubScene(createContent(), W, H);

            SubScene subSceneOne = new SubScene(createTopBar(), W,100);

            VBox root = new VBox(10);
            root.setAlignment(Pos.TOP_LEFT);
            root.getChildren().addAll(subSceneOne, subSceneTwo);
            Scene mainScene = new Scene(root, W,H + 100);
            stage.setScene(mainScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private Parent createTopBar() {

    	Pane root1 = new BorderPane();
    	TopBar topBar = new TopBar();
    	Text timer = new Text(210,40,"0");
    	Text bombCount_text = new Text(500,40,String.valueOf(totalBomb));

        timer.setFont(fontBold);
        bombCount_text.setFont(fontBold);

    	root1.setLayoutX(50);
    	root1.setLayoutY(30);
    	root1.getChildren().add(topBar.topBarImage);
    	root1.getChildren().add(timer);
    	root1.getChildren().add(bombCount_text);

    	return root1;
    }

    private Parent createContent() {
    	//지뢰 카운트 초기화
    	GridPane root = new GridPane();
    	
        root.setPrefSize(W, H);
        //타일 시작 위치
        root.setLayoutX(25);

        var mines = BoardGenerator.generateMines(new Position(16, 16), 40);

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
    private class TopBar extends VBox {
    	private final ImageView topBarImage;
        
        public TopBar(){
            topBarImage = new ImageView(new Image(getIconPath("TopBar.png")));
        	topBarImage.setFitWidth(700);
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
                    baseImage = new ImageView(getIconPath(TILE_REVEALED));
                }
                default -> { // 주변에는 있음
                    baseImage = new ImageView(getIconPath(TILE_EMPTY));
                    numberImage = new ImageView(numberImages[count - 1]);
                }
            }
            overlayImage = new ImageView(getIconPath(TILE_EMPTY));

            setImageViewAttrs(baseImage);
            getChildren().add(baseImage);
            if (numberImage != null) {
                setImageViewAttrs(numberImage);
                numberImage.setFitWidth(TILE_SIZE - 15);
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
            if (isOpen())
                return;

            overlayImage.setVisible(false);
            if (hasBomb()) {
               System.out.println("Game Over");
               //scene.setRoot(createContent());
               return;
            }
            state = STATE_OPEN;

            if (this.bombCount == COUNT_EMPTY)
                getNeighbors(this).forEach(Tile::open);
        }
        
        public void flag() {
            if (isOpen())
                return;

            if (hasBomb()) {
                flagCount++;
            }

            overlayImage.setImage(FlaggedImage_path);

            if (flagCount >= totalBomb) {
                System.out.println("You Win!");
            }
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
        //FXML 파일을 로드하는 코드? 검토가 필요함
    }

    private String getIconPath(String res) {
        return Objects.requireNonNull(getClass().getResource(RES_PATH + res)).toExternalForm();
    }
}