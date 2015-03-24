using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;

namespace Project2
{

    public enum Screen
    {
        StartScreen,
        World, /* This will be the GamePlayScreen */
        InstructionScreen,
        EndGameScreen
    }
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class Game1 : Microsoft.Xna.Framework.Game
    {
        public GraphicsDeviceManager graphics;
        public SpriteBatch spriteBatch;

        Screen currentScreen;
        StartScreen startScreen;
        EndGameScreen endScreen;
        InstructionScreen instructionScreen;
        public Camera camera;
        public Effect shader;

        World gameWorld;

        private const int width = 960;
        private const int height = 640;

        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
            graphics.PreferredBackBufferHeight = height;
            graphics.PreferredBackBufferWidth = width;
            
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            // TODO: Add your initialization logic here
            
            camera = new Camera(this.graphics.GraphicsDevice.Viewport);
            
            base.Initialize();

            startScreen = new StartScreen(this);
            currentScreen = Screen.StartScreen;
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);
            shader = Content.Load<Effect>("shader");
            
            // TODO: use this.Content to load your game content here
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            // Allows the game to exit
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed)
                this.Exit();
            
            // TODO: Add your update logic here
            
            switch (currentScreen)
            {
                case Screen.StartScreen:
                    if (startScreen != null)
                        startScreen.Update();
                    break;
                case Screen.InstructionScreen:
                    if (instructionScreen != null)
                        instructionScreen.Update();
                    break;
                case Screen.World:
                    if (gameWorld != null)
                        gameWorld.Update(gameTime);
                    break;
                case Screen.EndGameScreen:
                    if (endScreen != null)
                        endScreen.Update();
                    break;
            }

            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.Black);

            // TODO: Add your drawing code here
            
            switch (currentScreen)
            {
                case Screen.StartScreen:
                    if (startScreen != null)
                        startScreen.Draw(spriteBatch);
                    break;
                case Screen.InstructionScreen:
                    if (instructionScreen != null)
                        instructionScreen.Draw(spriteBatch);
                    break;
                case Screen.World:
                    if (gameWorld != null)
                    {
                        
                        gameWorld.Draw(spriteBatch);
                       
                    }
                    break;
                case Screen.EndGameScreen:
                    endScreen.Draw(spriteBatch);
                    break;
            }
           
            base.Draw(gameTime);
        }

        public void StartGame()
        {
            startScreen = null;
            endScreen = null;
            instructionScreen = null; 

            gameWorld = new World(this, camera);
            
            gameWorld.LoadContent(this.Content);

            currentScreen = Screen.World;
        }

        public void GetInstructions()
        {
            instructionScreen = new InstructionScreen(this);

            currentScreen = Screen.InstructionScreen;

            startScreen = null;
            endScreen = null;
            gameWorld = null;
        }

        public void ReturnToMenu()
        {
            startScreen = new StartScreen(this);

            currentScreen = Screen.StartScreen;

            endScreen = null;
            gameWorld = null;
            instructionScreen = null;
        }

        public void EndGame()
        {
            MediaPlayer.Stop(); //Stops gameMusic from playing

            endScreen = new EndGameScreen(this);
            currentScreen = Screen.EndGameScreen;

            gameWorld = null;
            startScreen = null;
            instructionScreen = null;
        }
    }
}
