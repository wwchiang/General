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
    public class ParallaxingBackground
    {
        //Parallaxing Background Image
        Boolean hasEffect { get; set; }
        Texture2D texture;

        /* For fixed backgrounds */
        Game game;
        Texture2D background;
        List <Vector2> positions;

        // Positions of the background
        Vector2[] background_Pos;

        // Background movement speed
        public int background_speed { set; get; }

        public ParallaxingBackground(Game game)
        {
            positions = new List<Vector2>();
            positions.Add(new Vector2(0, 0));
            this.game = game;
        }

        public void Initialize(Texture2D texture, int speed, int screenWidth, String effect, Boolean b)
        {

            /* Code below is for the fixed background aspect that repeats */
            background = texture;

            if (background.Width < screenWidth)
            {
                int remainder = screenWidth % background.Width;
                for (int i = 1; i < remainder; i++)
                {
                    Vector2 pos = new Vector2(background.Width * i, 0);
                    positions.Add(pos);
                }
            }

            hasEffect = b;

            if (hasEffect)
            {
                //this.texture = texture;
                this.texture = game.Content.Load<Texture2D>(effect);

                this.background_speed = speed;

                // Divide the screen width by the texture width to determine the number of tiles necessary.
                // Add 1 so there won't be a gap in the tiling
                background_Pos = new Vector2[screenWidth / texture.Width + 1];

                // Sets the first positions of the parallaxing background
                for (int i = 0; i < background_Pos.Length; i++)
                {
                    //  Tiles need to be side by side in order to create a tiling effect
                    background_Pos[i] = new Vector2(i * texture.Width, 0);
                }
            }
        }

        public void Update()
        {
            if (hasEffect)
            {
                // Updates the positions of the background
                for (int i = 0; i < background_Pos.Length; i++)
                {
                    // Updates the position of the screen by adding the speed
                    background_Pos[i].X += background_speed;
                    // If the speed is negative and has the background moving left
                    if (background_speed <= 0)
                    {
                        // See if the texture is out of view and put that texture at the end of the screen
                        if (background_Pos[i].X <= -texture.Width)
                        {
                            background_Pos[i].X = texture.Width * (background_Pos.Length - 1);
                        }
                    }
                    // If the speed is positive and the background moves right
                    else
                    {
                        // See if the texture is out of view and then position it to the beginning of the viewable screen
                        if (background_Pos[i].X >= texture.Width * (background_Pos.Length - 1))
                        {
                            background_Pos[i].X = -texture.Width;
                        }
                    }
                }
            }
        }

        public void Draw(SpriteBatch spriteBatch)
        {

            /* Repeats the fixed background */
            foreach (Vector2 pos in positions)
            {
                spriteBatch.Draw(background, pos, Color.White);
            }

            /* Parallaxing moving effect for the second layer of the background 
             if applicable */
            if (hasEffect)
            {
                for (int i = 0; i < background_Pos.Length; i++)
                {
                    spriteBatch.Draw(texture, background_Pos[i], Color.White);
                }
            }
        }
        public void Draw(Texture2D renderTexture, SpriteBatch sb)
        {
            /* Repeats the fixed background */
            foreach (Vector2 pos in positions)
            {
                sb.Draw(renderTexture, pos, Color.White);
            }

          
            

        }
    }
}
