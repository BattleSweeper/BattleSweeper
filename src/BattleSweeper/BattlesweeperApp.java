package BattleSweeper;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class BattlesweeperApp extends Application {

    private static final int TILE_SIZE = 75;
    private static final int W = 800;
    private static final int H = 800;

    private static final int X_TILES = 5;
    private static final int Y_TILES = 5;

    private Tile[][] grid = new Tile[X_TILES][Y_TILES];
    private Scene scene;
    
    //승리 조건을 임시로 계산하기 위한 변수
    private int bombCount=0;
    private int count=0;
    
    //이미지 경로
    Image TopBarImage_path = new Image("\\BattleSweeper\\icon\\TopBar.png");
    Image RevealedImage_path = new Image("\\BattleSweeper\\icon\\Revealed.png");
    Image TileImage_path = new Image("\\BattleSweeper\\icon\\tile.png");
    Image FlaggedImage_path = new Image("\\BattleSweeper\\icon\\Flagged.png");
    
    Image NumberImage_path1 = new Image("\\BattleSweeper\\icon\\one.png");
    Image NumberImage_path2 = new Image("\\BattleSweeper\\icon\\Two.png");
    Image NumberImage_path3 = new Image("\\BattleSweeper\\icon\\Three.png");
    Image NumberImage_path4 = new Image("\\BattleSweeper\\icon\\Four.png");
    Image NumberImage_path5 = new Image("\\BattleSweeper\\icon\\Five.png");
    
    
   
    
    
    private Parent createTopBar() {
    	

    	Pane root1 = new BorderPane();
    	TopBar topBar = new TopBar();
    	Text timer = new Text(210,40,"0");
    	Text bombCount_text = new Text(500,40,""+bombCount);
    	
        
    	root1.setLayoutX(50);
    	root1.setLayoutY(30);
    	root1.getChildren().add(topBar.TopBarImage);
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
 
        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
            	
            	double randomNumber = Math.random();
                Tile tile = new Tile(x, y, randomNumber < 0.2);
                

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
             

                if (bombs == 1) {
                    tile.text.setText(String.valueOf(bombs));
                    tile.getChildren().add(tile.NumberImage1); 
                }else if(bombs == 2) {
                	tile.text.setText(String.valueOf(bombs));
                	tile.getChildren().add(tile.NumberImage2);  
                }else if(bombs == 3) {
                	tile.text.setText(String.valueOf(bombs));
                	tile.getChildren().add(tile.NumberImage3); 
                }else if(bombs == 4) {
                	tile.text.setText(String.valueOf(bombs));
                	tile.getChildren().add(tile.NumberImage4); 
                }else if(bombs == 5) {
                	tile.text.setText(String.valueOf(bombs));
                	tile.getChildren().add(tile.NumberImage5); 
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
    private class TopBar extends VBox{
    	private ImageView TopBarImage = new ImageView(TopBarImage_path);
    	
        
        public TopBar(){
        	TopBarImage.setImage(TopBarImage_path);
        	TopBarImage.setFitWidth(700);
        	TopBarImage.setPreserveRatio(true);
        	TopBarImage.setSmooth(true);
        	TopBarImage.setCache(true);
        	TopBarImage.setVisible(true);
        	
        }
    	
    }

    private class Tile extends StackPane {
        private int x, y;
        private boolean hasBomb;
        private boolean isOpen = false;
        
        
        //검정 타일
        private Rectangle border = new Rectangle();
        private Text text = new Text();
        
        private ImageView RevealedImage = new ImageView();
        private ImageView TileImage = new ImageView();
        
        private ImageView NumberImage1 = new ImageView();
        private ImageView NumberImage2 = new ImageView();
        private ImageView NumberImage3 = new ImageView();
        private ImageView NumberImage4 = new ImageView();
        private ImageView NumberImage5 = new ImageView();
        
        private ImageView FlaggedImage = new ImageView();

        public Tile(int x, int y, boolean hasBomb) {

            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;
            
             //이미지 로드 부분
        	RevealedImage.setImage(RevealedImage_path);
            RevealedImage.setFitWidth(72);
            RevealedImage.setPreserveRatio(true);
            RevealedImage.setSmooth(true);
            RevealedImage.setCache(true);
            
            TileImage.setImage(TileImage_path);
            TileImage.setFitWidth(72);
            TileImage.setPreserveRatio(true);
            TileImage.setSmooth(true);
            TileImage.setCache(true);
            
            
            NumberImage1.setImage(NumberImage_path1);
            NumberImage1.setVisible(false);
            NumberImage2.setImage(NumberImage_path2);
            NumberImage2.setVisible(false);
            NumberImage3.setImage(NumberImage_path3);
            NumberImage3.setVisible(false);
            NumberImage4.setImage(NumberImage_path4);
            NumberImage4.setVisible(false);
            NumberImage5.setImage(NumberImage_path5);
            NumberImage5.setVisible(false);
            
            
            FlaggedImage.setImage(FlaggedImage_path);
            FlaggedImage.setFitWidth(72);
            FlaggedImage.setPreserveRatio(true);
            FlaggedImage.setSmooth(true);
            FlaggedImage.setCache(true);
            FlaggedImage.setVisible(false);
            
            border.setStroke(Color.LIGHTGRAY);
            getChildren().add(RevealedImage);  
            getChildren().add(TileImage); 
            getChildren().add(FlaggedImage);
            

            text.setFont(Font.font(18));
            text.setText(hasBomb ? "X" : "");
            text.setVisible(false);
            
            

            getChildren().addAll(border, text);
            

            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);
            
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
            if (isOpen)
                return;

            if (hasBomb) {
               System.out.println("Game Over");
               //scene.setRoot(createContent());
               return;
            }

            isOpen = true;
            
            TileImage.setVisible(false);
            NumberImage1.setVisible(true);
            NumberImage2.setVisible(true);
            NumberImage3.setVisible(true);
            NumberImage4.setVisible(true);
            NumberImage5.setVisible(true);
            
            border.setFill(null);
            

            if (text.getText().isEmpty()) {
                getNeighbors(this).forEach(Tile::open);
            }
            
        }
        
        public void flag() {
            if (isOpen)
                return;
            
            if (hasBomb) {
                count++;
             }

            FlaggedImage.setVisible(true);

            
            while (count>=bombCount) { 
            	System.out.println("You Win!");
            	break;
            }
        }
        
        
        
        
        //FXML 파일을 로드하는 코드? 검토가 필요함
        
        
    }
    

    
    // FXML 및 게임 로드 부분
    // stackpane을 사용했지만 둘 다 불러와지지 않고 게임이 덮어씌워지는 문제가 있음.
    public void start(Stage stage) throws Exception {
    
        SubScene subSceneTwo = new SubScene(createContent(),W,H);
        
        SubScene subSceneOne = new SubScene(createTopBar(),W,100);

        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_LEFT);
        root.getChildren().addAll(subSceneOne,subSceneTwo);
        Scene mainScene = new Scene(root,W,H+100);
        stage.setScene(mainScene);
        stage.show();
        
    }

	public static void main(String[] args) {
        launch(args);
    }
}