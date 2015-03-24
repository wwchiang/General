using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;

namespace Project2
{
    public class World
    {

        public Game1 game;

        RenderTarget2D renderTarget;

        //// ---- MOVEMENT ---- //
        KeyboardState currentKeyboardState;
        KeyboardState previousKeyboardState;
        //GamePadState currentGamePadState;
        // GamePadState previousGamePadState;

        Player player;
        public Camera camera { get; private set; }

        Viewport newView;

        Texture2D playerIdleRight;
        Texture2D playerIdleLeft;
        Texture2D movingRightTexture;
        Texture2D movingLeftTexture;
        Texture2D jumpRight;
        Texture2D jumpLeft;
        Texture2D playerDeath;
        public Texture2D EffectLayer1;

        Texture2D tileTexture;
        public Song gameMusic;
        public SoundEffect jumpSound;
        public SoundEffectInstance jumpSoundInstance;
        public SoundEffect laughSound;
        public SoundEffectInstance laughSoundInstance;
        public SoundEffect deathSound;
        public SoundEffectInstance deathSoundInstance;
        public SoundEffect collapseSound;
        public SoundEffectInstance collapseSoundInstance;
        public SoundEffect bounceSound;
        public SoundEffectInstance bounceSoundInstance;
        public SoundEffect keySound;
        public SoundEffectInstance keySoundInstance;
        public SoundEffect breakSound;
        public SoundEffectInstance breakSoundInstance;

        List<MapTile> mapTiles;
        int level_counter = 0;

        public LevelInfo levelinfo { get; private set; }

        public ParallaxingBackground background;
        String shader1, shader2;

        Boolean isNewLevel;

        public World(Game1 g, Camera c)
        {
            game = g;
            camera = c;
            newView = g.GraphicsDevice.Viewport;
            mapTiles = new List<MapTile>();
            background = new ParallaxingBackground(g);
            isNewLevel = true;
        }

        public void LoadContent(ContentManager Content)
        {
            // Adding textures
            tileTexture = Content.Load<Texture2D>("cube");

            // Textures for player 
            playerIdleRight = Content.Load<Texture2D>("idleright2");
            playerIdleLeft = Content.Load<Texture2D>("idleleft2");
            movingRightTexture = Content.Load<Texture2D>("walkright2");
            movingLeftTexture = Content.Load<Texture2D>("walkleft2");
            playerDeath = Content.Load<Texture2D>("playerdeath");

            // Music and sound effects 
            gameMusic = Content.Load<Song>("Darkness_Pt_1v2");
            jumpSound = Content.Load<SoundEffect>("jump");
            jumpSoundInstance = jumpSound.CreateInstance();
            laughSound = Content.Load<SoundEffect>("Goblin");
            laughSoundInstance = laughSound.CreateInstance();
            deathSound = Content.Load<SoundEffect>("meow");
            deathSoundInstance = deathSound.CreateInstance();
            collapseSound = Content.Load<SoundEffect>("collapsing");
            collapseSoundInstance = collapseSound.CreateInstance();
            bounceSound = Content.Load<SoundEffect>("bouncesound");
            bounceSoundInstance = bounceSound.CreateInstance();
            keySound = Content.Load<SoundEffect>("keyget");
            keySoundInstance = keySound.CreateInstance();
            breakSound = Content.Load<SoundEffect>("wallbreak");
            breakSoundInstance = breakSound.CreateInstance();
            PresentationParameters pp = game.graphics.GraphicsDevice.PresentationParameters;
            //renderTarget = new RenderTarget2D(game.graphics.GraphicsDevice, pp.BackBufferWidth, pp.BackBufferHeight);
            renderTarget = new RenderTarget2D(
                game.graphics.GraphicsDevice,
 //               game.graphics.GraphicsDevice.PresentationParameters.BackBufferWidth,
                              game.graphics.GraphicsDevice.PresentationParameters.BackBufferWidth * 2,
                game.graphics.GraphicsDevice.PresentationParameters.BackBufferHeight,
                false,
                game.graphics.GraphicsDevice.PresentationParameters.BackBufferFormat,
                DepthFormat.Depth24);
            EffectLayer1 = Content.Load<Texture2D>("background1");
            LoadMap(0);
            PlayMusic(gameMusic);
            MediaPlayer.Volume = 0.7f;
        }

        public void LoadMap(int i)
        {
            
            levelinfo = game.Content.Load<LevelInfo>("LevelInfo" + i);
            shader1 = levelinfo.shader1;
            shader2 = levelinfo.shader2;

            if (isNewLevel)
            {
                background.Initialize(game.Content.Load<Texture2D>(levelinfo.backgroundTexture), 1,
                    levelinfo.x, levelinfo.texture, levelinfo.hasTexture);
            }

            MapTileData[] data = game.Content.Load<MapTileData[]>("Level" + i);

            foreach (MapTileData d in data)
            { 
                mapTiles.Add(new MapTile((int)d.mapPosition.X, (int)d.mapPosition.Y,
                    game.Content.Load<Texture2D>(d.tileTexture), game, d.isBouncy, d.isBreakable, d.isTrap, 
                    d.isUnstable, d.isCake, d.isSaw, d.isLock, d.isKey, false));
            }

            /* So the player will begin on top of the blocks*/
            player = new Player(0, newView.Height - 3 * tileTexture.Height, game,
                playerIdleRight, playerIdleLeft, movingRightTexture, movingLeftTexture, null, null, playerDeath, this);

            player.setBoundaries(levelinfo.x, levelinfo.y);
            camera.setBoundaries(levelinfo.x, levelinfo.y); // Passes in Map Boundaries to Camera
            deathSoundInstance.Stop(); //stop death sound
            
            //PlayMusic(gameMusic);
           
        }

        public void changeLevel()
        {
            isNewLevel = true;
            camera.ResetCamera();
            mapTiles.Clear();

            level_counter++;

            /* If the level is not the last level*/
            if (level_counter < 3)
                LoadMap(level_counter);
            else
                game.EndGame();
            
        }

        private void PlayMusic(Song song)
        {
            try
            {
                MediaPlayer.Play(song);


                // Loop the song
                MediaPlayer.IsRepeating = true;
            }
            catch { }
        }


        public void Update(GameTime gametime)
        {
            
            background.Update();
            
            //previousGamePadState = currentGamePadState;
            previousKeyboardState = currentKeyboardState;

            // Read the current state of the keyboard and gamepad and store it
            currentKeyboardState = Keyboard.GetState();
            //currentGamePadState = GamePad.GetState(PlayerIndex.One);

            foreach (MapTile tile in mapTiles)
            {
                tile.Update(gametime);
            }
            player.Update(gametime, currentKeyboardState);
            UpdateCollisions();

            camera.Update(gametime, player);

            // For convenience when testing, press [ESC] key to leave the game 
            if (currentKeyboardState.IsKeyDown(Keys.Escape))
                game.Exit();

            // If player is dead, reset the level. 
            if (player.CheckDeath() || player.hasFallenToDeath) {
                player.isDead = false;
                player.hasFallenToDeath = false;
                LevelReset();
            }

            /* If player touches the cake, transition to new level / end the game */
            if (player.end)
            {
                laughSoundInstance.Play();
                player.end = false;
                
                changeLevel();
            }

        }

        /* Resets the current level */
        public void LevelReset() 
        {
            isNewLevel = false;
            camera.ResetCamera();
            mapTiles.Clear();
            LoadMap(level_counter);
        }

        public void UpdateCollisions()
        {

            Rectangle terrainHitBox;
            Rectangle playerHitBox = player.getHitBox();

            player.isOnPlatform = false;

            foreach (MapTile tile in mapTiles)
            {
                terrainHitBox = new Rectangle((int)(tile.mapPositions.X),
                      (int)tile.mapPositions.Y, tile.Width, tile.Height);

                if (player.hasKey && tile.isLock)
                {
                    //turn block off, maybe play animation for it
                }
                player.CheckCollisionSide(playerHitBox, terrainHitBox, tile);
                playerHitBox = player.getHitBox();

            }
        }


        public void Draw(SpriteBatch sb)
        {
            
                game.graphics.GraphicsDevice.SetRenderTarget(renderTarget);


                Matrix m = Matrix.CreateTranslation(new Vector3(-camera.Position * 0.1f, 0.0f)) * camera.GetViewMatrix();

                sb.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, null, null,m);
                //sb.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, null, null, camera.GetViewMatrix());
                // sb.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, null, null);

                game.shader.CurrentTechnique.Passes[shader1].Apply();

                background.Draw(sb);

                
                 game.graphics.GraphicsDevice.SetRenderTarget(null);
                EffectLayer1 = (Texture2D)renderTarget;
                
               
                 game.shader.CurrentTechnique.Passes[shader2].Apply();
                background.Draw(EffectLayer1, sb); //Custom Draw to renderedTexture for multi-pass shading

                sb.End();
            
                
            
            
            sb.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, null, null, camera.GetViewMatrix());
            foreach (MapTile tile in mapTiles)
            {
                tile.Draw(sb);
            }
            

            player.Draw(sb);
            sb.End();
        }

    }
}