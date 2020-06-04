package tiled;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import base.BaseActor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to load the map, its objects and draw it on screen
 * @author libgdx
 */
public class TilemapActor extends Actor
{
	
    public static int windowWidth  = 720;
    public static int windowHeight = 480;
   
    private TiledMap tiledMap;
    private OrthographicCamera tiledCamera;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;

   /**
    * Constructor
    * @param filename map name
    * @param theStage stage where to paint
    */
    public TilemapActor(String filename, Stage theStage){
    
        tiledMap = new TmxMapLoader().load(filename);

        int tileWidth = (int)tiledMap.getProperties().get("tilewidth");
        int tileHeight = (int)tiledMap.getProperties().get("tileheight");
        int numTilesHorizontal = (int)tiledMap.getProperties().get("width");
        int numTilesVertical = (int)tiledMap.getProperties().get("height");
        int mapWidth = tileWidth  * numTilesHorizontal;
        int mapHeight = tileHeight * numTilesVertical;

        tiledMapRenderer = new OrthoCachedTiledMapRenderer(tiledMap);
        tiledMapRenderer.setBlending(true);
        tiledCamera = new OrthographicCamera();
        tiledCamera.setToOrtho(false, windowWidth, windowHeight);
        tiledCamera.update();

        theStage.addActor(this);

        BaseActor.setWorldBounds(mapWidth, mapHeight);
    }
    
    /**
     * Class to load rectangle objects
     * @param propertyName object name
     * @return objects list
     */
    public ArrayList<MapObject> getRectangleList(String propertyName){
        ArrayList<MapObject> list = new ArrayList<MapObject>();

        for (MapLayer layer : tiledMap.getLayers()){
            for (MapObject obj : layer.getObjects()){
                if (!(obj instanceof RectangleMapObject)) {
                	 continue;
                }
                MapProperties props = obj.getProperties();

                if (props.containsKey("name") && props.get("name").equals(propertyName) )
                    list.add(obj);
            }
        }
        return list;
    }
    
    /**
     * Class to load list objects
     * @param propertyName object name
     * @return objects list
     */
    public ArrayList<MapObject> getTileList(String propertyName){
        ArrayList<MapObject> list = new ArrayList<MapObject>();

        for (MapLayer layer : tiledMap.getLayers() ){
            for (MapObject obj : layer.getObjects() ){
                if (!(obj instanceof TiledMapTileMapObject) )
                    continue;

                MapProperties props = obj.getProperties();

            

                TiledMapTileMapObject tmtmo = (TiledMapTileMapObject)obj;
                TiledMapTile t = tmtmo.getTile();
                MapProperties defaultProps = t.getProperties();

                if (defaultProps.containsKey("name") && defaultProps.get("name").equals(propertyName) )
                    list.add(obj);

                Iterator<String> propertyKeys = defaultProps.getKeys();

                while (propertyKeys.hasNext() ){
                    String key = propertyKeys.next();

                    if (props.containsKey(key)){
                    	 continue;
                    }
                    else{
                        Object value = defaultProps.get(key);
                        props.put( key, value );
                    }
                }
            }
        }
        return list;
    }
    
    public void act(float dt){
        super.act( dt );
    }
    
    /**
     * Draw the map on screen
     */
    public void draw(Batch batch, float parentAlpha) {
        Camera mainCamera = getStage().getCamera();
        tiledCamera.position.x = mainCamera.position.x;
        tiledCamera.position.y = mainCamera.position.y;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);

        batch.end();
        tiledMapRenderer.render();        
        batch.begin();
    }
}
