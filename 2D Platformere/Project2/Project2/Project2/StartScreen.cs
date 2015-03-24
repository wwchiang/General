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
    public class StartScreen
    {
        private Game1 game;
        private KeyboardState lastState;
        private Texture2D startSelect;
        private Texture2D quitSelect;
        private int selection;
        private SoundEffect selectSound;
        private SoundEffectInstance selectInstance;
        private SoundEffect menuEnter;
        private SoundEffectInstance menuInstance;

        public StartScreen(Game1 game)
        {
            this.game = game;
            lastState = Keyboard.GetState();
            startSelect = game.Content.Load<Texture2D>("startselect");
            quitSelect = game.Content.Load<Texture2D>("quitselect");
            selectSound = game.Content.Load<SoundEffect>("menuselect");
            selectInstance = selectSound.CreateInstance();
            menuEnter = game.Content.Load<SoundEffect>("menuenter");
            menuInstance = menuEnter.CreateInstance();
        }

        public void Update()
        {
            KeyboardState keyboardState = Keyboard.GetState();

            /* Starts the game */
            if (keyboardState.IsKeyDown(Keys.Enter) && lastState.IsKeyUp(Keys.Enter))
            {
                if (selection == 0)
                {
                    menuInstance.Play();
                    game.GetInstructions();
                }
                else if (selection == 1)
                {
                    menuInstance.Play();
                    game.Exit();
                }
            }

            if (keyboardState.IsKeyDown(Keys.Up)) {
                selection = 0;
                selectInstance.Play();
            }

            if (keyboardState.IsKeyDown(Keys.Down))
            {
                selection = 1;
                selectInstance.Play();
            }

            lastState = keyboardState;
        }

        public void Draw(SpriteBatch spriteBatch)
        {

            spriteBatch.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, null, game.shader);
            game.shader.CurrentTechnique.Passes["Pass4"].Apply();

            //Draw select on start
            if (selection == 0) 
            {
                spriteBatch.Draw(startSelect, new Vector2(0, 0), Color.White);
            }

            //Draw select on quit
            else if (selection == 1)
            {
                spriteBatch.Draw(quitSelect, new Vector2(0, 0), Color.White);
            }
                spriteBatch.End();
        }
    }
}
