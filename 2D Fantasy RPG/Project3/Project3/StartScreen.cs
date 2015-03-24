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

namespace Project3
{
    public class StartScreen
    {
        private Game1 game;
        private KeyboardState lastState;
        private String startSelect;
        private String quitSelect;
        private int selection;
        private SpriteFont font;

        public StartScreen(Game1 game)
        {
            this.game = game;
            lastState = Keyboard.GetState();
            startSelect = "Start Game";
            quitSelect = "Quit Game";
            font = game.Content.Load<SpriteFont>("MenuFont");
        }

        public void Update()
        {
            KeyboardState keyboardState = Keyboard.GetState();

            /* Starts the game */
            if (keyboardState.IsKeyDown(Keys.Enter) && lastState.IsKeyUp(Keys.Enter))
            {
                if (selection == 0)
                {
                    game.StartGame();
                }

                else if (selection == 1)
                {
                    game.Exit();
                }
            }

            if (keyboardState.IsKeyDown(Keys.W))
            {
                game.menuSoundInstance.Volume = 0.4f;
                game.menuSoundInstance.Pan = 0.5f;
                game.menuSoundInstance.Play();
                selection = 0;
            }

            if (keyboardState.IsKeyDown(Keys.S))
            {
                game.menuSoundInstance.Volume = 0.4f;
                game.menuSoundInstance.Pan = 0.5f;
                game.menuSoundInstance.Play();
                selection = 1;
            }

            lastState = keyboardState;
        }

        public void Draw(SpriteBatch spriteBatch)
        {

            spriteBatch.Begin();


            //Draw select on start
            if (selection == 0)
            {
                spriteBatch.DrawString(font, startSelect, new Vector2(game.GraphicsDevice.Viewport.Width/2 - font.MeasureString(startSelect).X/2, game.GraphicsDevice.Viewport.Height/2 - font.MeasureString(startSelect).Y), Color.Yellow);
                spriteBatch.DrawString(font, quitSelect, new Vector2(game.GraphicsDevice.Viewport.Width / 2 - font.MeasureString(quitSelect).X/2, game.GraphicsDevice.Viewport.Height / 2), Color.White);
            }

            //Draw select on quit
            else if (selection == 1)
            {
                spriteBatch.DrawString(font, startSelect, new Vector2(game.GraphicsDevice.Viewport.Width / 2 - font.MeasureString(startSelect).X / 2, game.GraphicsDevice.Viewport.Height / 2 - font.MeasureString(startSelect).Y), Color.White);
                spriteBatch.DrawString(font, quitSelect, new Vector2(game.GraphicsDevice.Viewport.Width / 2 - font.MeasureString(quitSelect).X / 2, game.GraphicsDevice.Viewport.Height / 2), Color.Yellow);

            }


            spriteBatch.End();
        }
    }
}
