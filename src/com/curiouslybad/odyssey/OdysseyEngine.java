package com.curiouslybad.odyssey;

import java.io.IOException;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;

import android.widget.Toast;

public class OdysseyEngine extends SimpleBaseGameActivity {

  static final int           CAMERA_WIDTH  = 1280/2;
  static final int           CAMERA_HEIGHT = 768/2;
  private ZoomCamera         mCamera;

  private ITexture           mPlayerTexture;
  private TiledTextureRegion mPlayerTextureRegion;

  private TMXTiledMap        mTMXTiledMap;
  protected int mCactusCount;

  @Override
  public EngineOptions onCreateEngineOptions() {
    Toast.makeText(this,
        "The tile the player is walking on will be highlighted.",
        Toast.LENGTH_LONG).show();

    this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
        new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
  }

  @Override
  public Scene onCreateScene() {
    this.mEngine.registerUpdateHandler(new FPSLogger());
    final Scene scene = new Scene();

    try {
      final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
        @Override
        public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) 
        {
          
        }
      });
      this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");
    } catch (final TMXLoadException e) {
        Debug.e(e);
    }
    
    scene.attachChild(mTMXTiledMap);
    
    this.mCamera.setBoundsEnabled(false);
    this.mCamera.setBounds(0, 0, this.mTMXTiledMap.getWidth(), this.mTMXTiledMap.getHeight());
    this.mCamera.setBoundsEnabled(true);
    final float centerX = CAMERA_WIDTH / 2;
    final float centerY = CAMERA_HEIGHT / 2;
    
    final AnimatedSprite player = new AnimatedSprite(centerX, centerY, this.mPlayerTextureRegion, this.getVertexBufferObjectManager());
    player.setOffsetCenterY(0);
    player.setPosition(100, 100);
    this.mCamera.setChaseEntity(player);
    
    
    //final Path path = new Path(5).to(50, 740).to(50, 1000).to(820, 1000).to(820, 740).to(0);
    final Path path = new Path(5).to(0, 0).to(0, 300).to(200, 300).to(200, 0).to(0);

    player.registerEntityModifier(new LoopEntityModifier(new PathModifier(30, path, null, new IPathModifierListener() {
      @Override
      public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

      }

      @Override
      public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
        switch(pWaypointIndex) {
          case 0:
            player.animate(new long[] { 200, 200, 200 }, 0, 2, true);
            break;
          case 1:
            player.animate(new long[] { 200, 200, 200 }, 3, 5, true);
            break;
          case 2:
            player.animate(new long[] { 200, 200, 200 }, 6, 8, true);
            break;
          case 3:
            player.animate(new long[] { 200, 200, 200 }, 9, 11, true);
            break;
        }
      }

      @Override
      public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

      }

      @Override
      public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

      }
    })));
    /*
    final Rectangle currentTileRectangle = new Rectangle(0, 0, this.mTMXTiledMap.getTileWidth(), this.mTMXTiledMap.getTileHeight(), this.getVertexBufferObjectManager());
    currentTileRectangle.setOffsetCenter(0, 0);
    currentTileRectangle.setColor(1, 0, 0, 0.25f);
    scene.attachChild(currentTileRectangle);

    final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);

    scene.registerUpdateHandler(new IUpdateHandler() {
      @Override
      public void reset() { }

      @Override
      public void onUpdate(final float pSecondsElapsed) {
        final float[] playerFootCordinates = player.convertLocalCoordinatesToSceneCoordinates(16, 1);

        final TMXTile tmxTile = tmxLayer.getTMXTileAt(playerFootCordinates[Constants.VERTEX_INDEX_X], playerFootCordinates[Constants.VERTEX_INDEX_Y]);
        if(tmxTile != null) {
          currentTileRectangle.setPosition(tmxLayer.getTileX(tmxTile.getTileColumn()), tmxLayer.getTileY(tmxTile.getTileRow()));
        }
      }
    }); */

    scene.attachChild(player);
    
    
    return scene;
  }
  
  

  @Override
  public void onCreateResources() throws IOException {
    this.mPlayerTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/player.png", TextureOptions.DEFAULT);
    this.mPlayerTextureRegion = TextureRegionFactory.extractTiledFromTexture(this.mPlayerTexture, 3, 4);
    this.mPlayerTexture.load();
  }

}