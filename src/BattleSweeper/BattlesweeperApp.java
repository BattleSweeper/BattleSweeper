package BattleSweeper;
//게임부분
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;


public class BattlesweeperApp extends Application {
	

    private static final int TILE_SIZE = 75;
    private static final int W = 800;
    private static final int H = 800;

    private static final int X_TILES = 10;
    private static final int Y_TILES = 10;

    private Tile[][] grid = new Tile[X_TILES][Y_TILES];
    private Scene scene;
    
    private int bombCount=0;
    private int count=0;
    
 
    private Parent createContent() {
    	//지뢰 카운트 초기화
    	bombCount = 0;
    	count=0;
    	
    		

    	GridPane root = new GridPane();
        root.setPrefSize(W, H);
        //타일 시작 위치
        root.setLayoutX(50);
        root.setLayoutY(120);
        
        

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
        
        //검정 타일
        private Rectangle border = new Rectangle(TILE_SIZE - 5, TILE_SIZE - 5);
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
            
            //이벤트 처리부분

            setOnMouseClicked(e -> open());
        }

        public void open() {
            if (isOpen)
                return;

            if (hasBomb) {
               System.out.println("Game Over");
               //scene.setRoot(createContent()); 게임 재시작, 오류 발생하는 부분
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
    
    
    // FXML 및 게임 로드 부분
    // stackpane을 사용했지만 둘 다 불러와지지 않고 게임이 덮어씌워지는 문제가 있음.
    public void start(Stage stage) throws Exception {
        try {
        	StackPane Background = FXMLLoader.load(getClass().getResource("GameImage.fxml"));
        	Label labelOne = new Label("One");
        	Background.getChildren().add(labelOne);
        	SubScene subSceneOne = new SubScene(Background,300,100);
            
        	StackPane layoutTwo = new StackPane(createContent());
            Label labelTwo = new Label("Two");
            layoutTwo.getChildren().add(labelTwo);
            SubScene subSceneTwo = new SubScene(layoutTwo,800,800);
           
            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(subSceneOne,subSceneTwo);
            Scene mainScene = new Scene(root,1000,1000);
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}