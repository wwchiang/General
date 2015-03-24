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
    public class EndGameScreen
    {
        private Game1 game;
        private KeyboardState lastState;
        private Texture2D texture; 

        public EndGameScreen(Game1 game)
        {
            this.game = game;
            lastState = Keyboard.GetState();
            texture = game.Content.Load<Texture2D>("endcard");
        }

        public void Update()
        {
            KeyboardState keyboardState = Keyboard.GetState();

            if (keyboardState.IsKeyDown(Keys.Enter) && lastState.IsKeyUp(Keys.Enter))
            {
                game.ReturnToMenu();
            }
            else if (keyboardState.IsKeyDown(Keys.Escape) && lastState.IsKeyUp(Keys.Escape))
            {
                game.Exit();
            }

            lastState = keyboardState;
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Begin();
            if (texture != null)
                spriteBatch.Draw(texture, new Vector2(0f, 0f), Color.White);

             spriteBatch.End();
        }
    }
}
