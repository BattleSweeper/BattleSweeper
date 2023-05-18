import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javafx.scene.layout.GridPane;






public class BattlesweeperApp extends Application {

    private static final int TILE_SIZE = 50;
    private static final int W = 600;
    private static final int H = 800;

    private static final int X_TILES = W / TILE_SIZE;
    private static final int Y_TILES = H / TILE_SIZE-1;

    private Tile[][] grid = new Tile[X_TILES][Y_TILES];
    private Scene scene;
    
    private int bombCount=0;
    private int count=0;
    
    //이미지
    private final String IMAGE_PATH =
            "http://icons.iconarchive.com/icons/iconka/meow-2/64/cat-rascal-icon.png";

    private Parent createContent() {
    	//카운트 초기화
    	bombCount = 0;
    	count=0;
    	
    	
    	

    	GridPane root = new GridPane();
        root.setPrefSize(W, H);
        root.setLayoutY(100);
        
        

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = new Tile(x, y, Math.random() < 0.2);

                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = grid[x][y];

                if (tile.hasBomb) {
                	bombCount++;
                	continue;
                }

                long bombs = getNeighbors(tile).stream().filter(t -> t.hasBomb).count();
             

                if (bombs > 0) {
                    tile.text.setText(String.valueOf(bombs));
                    
                }
            }
        }

        return root;
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        // ttt
        // tXt
        // ttt

        int[] points = new int[] {
              -1, -1,
              -1, 0,
              -1, 1,
              0, -1,
              0, 1,
              1, -1,
              1, 0,
              1, 1
        };

        for (int i = 0; i < points.length; i++) {
            int dx = points[i];
            int dy = points[++i];

            int newX = tile.x + dx;
            int newY = tile.y + dy;

            if (newX >= 0 && newX < X_TILES
                    && newY >= 0 && newY < Y_TILES) {
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }

    private class Tile extends StackPane {
        private int x, y;
        private boolean hasBomb;
        private boolean isOpen = false;

        private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
        private Text text = new Text();

        public Tile(int x, int y, boolean hasBomb) {
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;

            border.setStroke(Color.LIGHTGRAY);

            text.setFont(Font.font(18));
            text.setText(hasBomb ? "X" : "");
            text.setVisible(false);

            getChildren().addAll(border, text);

            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);

            setOnMouseClicked(e -> open());
        }

        public void open() {
            if (isOpen)
                return;

            if (hasBomb) {
               System.out.println("Game Over");
               scene.setRoot(createContent());
               return;
            }

            isOpen = true;
            count++;
            text.setVisible(true);
            border.setFill(null);

            if (text.getText().isEmpty()) {
                getNeighbors(this).forEach(Tile::open);
            }
            
            while (count>=X_TILES*Y_TILES-bombCount) {
            	System.out.println("You Win!");
            	break;
            }
        }
    }
    
    
    public void start(Stage stage) throws Exception {
    	
        scene = new Scene(createContent());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}