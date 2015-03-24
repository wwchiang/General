using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;

namespace Project3
{
    public class World
    {
        public Game1 game;
        public Camera camera;

        public Song gameMusic;
        public SoundEffect menuSound;
        public SoundEffectInstance menuSoundInstance;
        public SoundEffect talkToNPC;
        public SoundEffectInstance talkToNPCInstance;
        public SoundEffect shopSound;
        public SoundEffectInstance shopSoundInstance;
        public SoundEffect attackSound;
        public SoundEffectInstance attackSoundInstance;
        public SoundEffect hitEnterSound;
        public SoundEffectInstance hitEnterSoundInstance;
        public SoundEffect hitBackSpaceSound;
        public SoundEffectInstance hitBackSpaceSoundInstance;

        public Map currentMap;

        public ObjectLoader loader;

        //Player Textures//
        Texture2D playerup;
        Texture2D playerdown;
        Texture2D playerleft;
        Texture2D playerright;
        public Texture2D initial;

        //Tile Textures
        Texture2D grassText;
        Texture2D redTransition;
        Texture2D blueTransition;

        //// ---- MOVEMENT ---- //
        KeyboardState currentKeyboardState;
        KeyboardState previousKeyboardState;

        int width = 50;
        int height = 40;

        Map map;
        int currMapNum; 

        public Player player;
        Display HUD;
        public BattleSystem battleSystem;
        
        public SpriteFont font;
        public SpriteFont battleFont;
        public SpriteFont shopDialogueFont;

        public World(Game1 game, Camera c)
        {
            this.game = game;
            camera = c;
            map = new Map(game);
        }

        public void LoadContent(ContentManager Content)
        {
            map.LoadContent();
            map.GenerateMap(0);
            currMapNum = 0;
            
            currentMap = map;

            width = map.width;
            height = map.height;

            initial = game.Content.Load<Texture2D>("Player/playerinitial");
            playerleft = game.Content.Load<Texture2D>("Player/player_walk_west");
            playerright = game.Content.Load<Texture2D>("Player/player_walk_east");
            playerup = game.Content.Load<Texture2D>("Player/player_walk_north");
            playerdown = game.Content.Load<Texture2D>("Player/player_walk_south");

            // Load Music and Sound Effects
            //-----------------------------------------------------------------------------
            hitBackSpaceSound = Content.Load<SoundEffect>("return_button");
            hitBackSpaceSoundInstance = hitBackSpaceSound.CreateInstance();
            hitEnterSound = Content.Load<SoundEffect>("enter_sound");
            hitEnterSoundInstance = hitEnterSound.CreateInstance();
            gameMusic = Content.Load<Song>("8bit_game_music");
            menuSound = Content.Load<SoundEffect>("Menu_sound");
            menuSoundInstance = menuSound.CreateInstance();
            talkToNPC = Content.Load<SoundEffect>("talk");
            talkToNPCInstance = talkToNPC.CreateInstance();
            shopSound = Content.Load<SoundEffect>("shop");
            shopSoundInstance = shopSound.CreateInstance();
            attackSound = Content.Load<SoundEffect>("battle_start");
            attackSoundInstance = attackSound.CreateInstance();
            //-----------------------------------------------------------------------------

            player = new Player(playerup, playerdown, playerleft, playerright, new Vector2(width/2 - 4, height/2 - 1), currentMap, this);

            loader = new ObjectLoader(this);
            loader.Initialize();
            loader.SetMerchantsNPCs();
            loader.setEnemies();

            font = game.Content.Load<SpriteFont>("DialogueFont");
            shopDialogueFont = game.Content.Load<SpriteFont>("ShopFont");
            battleFont = game.Content.Load<SpriteFont>("BattleFont");
            camera.setBoundaries(width * 32, height * 32);

            HUD = new Display(player, game); 
            HUD.LoadContent(Content);

            // GAME MUSIC-------------------------------------------------------------------
            MediaPlayer.Play(gameMusic);
            MediaPlayer.Volume = 0.08f;
            MediaPlayer.IsRepeating = true;

            battleSystem = new BattleSystem(player, HUD, this);
            battleSystem.LoadTextures();

        }

        public void TransitionMap(int path)
        {
            /* Clears the current map */
            currentMap = null;

            /* Loads new map */
            map.GenerateMap(path);
            currentMap = map;
            currMapNum = path;

            /* Reloads the NPCs */
            if (currMapNum == 0)
                loader.SetMerchantsNPCs();

            player.ChangeMap(map);

            loader.setEnemies();
        }

        public void Update(GameTime gametime)
        {
            currentKeyboardState = Keyboard.GetState();
        
            /* Temporary for now to make it easier for debugging. 
             Basically exits the game when the [ESCAPE] key is pressed. */
            if (currentKeyboardState.IsKeyDown(Keys.Escape))
                game.Exit();

            player.UpdateInput(gametime, currentKeyboardState);
            player.UpdatePosition(gametime);

            camera.Update(gametime, player);

        }

        public void Draw(SpriteBatch sb)
        {
            sb.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, null, null, camera.GetViewMatrix()); 
            map.Draw(sb);
            
            player.Draw(sb);
            HUD.Draw(sb);

            if (player.isBattling)
            {
                battleSystem.Draw(sb);
            }
            sb.End();
        }
    }
}
