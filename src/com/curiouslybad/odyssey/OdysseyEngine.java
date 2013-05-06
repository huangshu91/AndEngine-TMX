package com.curiouslybad.odyssey;

import java.io.IOException;

import org.andengine.engine.camera.ZoomCamera;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
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
    
    Sprite sPlayer = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2,mPlayerTextureRegion, this.mEngine.getVertexBufferObjectManager());
    scene.attachChild(sPlayer);
    
    
    return scene;
  }
  
  

  @Override
  public void onCreateResources() throws IOException {
    this.mPlayerTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/player.png", TextureOptions.DEFAULT);
    this.mPlayerTextureRegion = TextureRegionFactory.extractTiledFromTexture(this.mPlayerTexture, 3, 4);
    this.mPlayerTexture.load();
  }

}