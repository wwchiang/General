using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Graphics;

namespace Project2
{
    class Animation
    {
        // The image representing the collection of images used for animation
        Texture2D spriteStrip;

        // The scale used to display the sprite strip
        float scale;

        // The time since we last updated the frame
        int elapsedTime;

        // The time we display a frame until the next one
        int frameTime;

        // The number of frames that the animation contains
        int frameCount;

        // The index of the current frame we are displaying
        int currentFrame;

        // The color of the frame we will be displaying
        Color color;

        // The area of the image strip we want to display
        public Rectangle sourceRect = new Rectangle();

        // The area where we want to display the image strip in the game
        public Rectangle destinationRect = new Rectangle();

        // Width of a given frame
        public int FrameWidth;

        // Height of a given frame
        public int FrameHeight;

        // The state of the Animation
        public bool Active;

        // Determines if the animation will keep playing or deactivate after one run
        public bool Looping;

        public bool PlayFirstFrame;

        // Width of a given frame
        public Vector2 Position;

        public void Initialize(Texture2D texture, Vector2 position, int frameWidth, int frameHeight, int frameCount,
            int frametime, Color color, float scale, bool looping, bool PlayFirstFrame )
        {
            this.color = color;
            this.FrameWidth = frameWidth;
            this.FrameHeight = frameHeight;
            this.frameCount = frameCount;
            this.frameTime = frametime;
            this.scale = scale;
            this.PlayFirstFrame = PlayFirstFrame;

            Looping = looping;
            Position = position;
            spriteStrip = texture;

            // Set the time to zero
            elapsedTime = 0;
            currentFrame = 0;

            // Set the Animation to active by default
            Active = true;
        }

        //Can be used to manually set where an animation stops
        public int GetCurrentFrame()
        {
            return currentFrame;
        }
        public void Update(Vector2 position, GameTime gameTime)
        {

            this.Position = position;

            if (Active == false)
                return;

            // If block is not interactable, then it will always play the same frame
            // if block IS interactable, play the first frame until it is touched, turn it off
            // Update the elapsed time
            if (PlayFirstFrame)
            {
                currentFrame = 0;
            }

            else
            {
                elapsedTime += (int)gameTime.ElapsedGameTime.TotalMilliseconds;

                // If the elapsed time is larger than the frame time
                // we need to switch frames
                if (elapsedTime > frameTime)
                {
                    // Move to the next frame
                    currentFrame++;

                    // If the currentFrame is equal to frameCount reset currentFrame to zero
                    if (currentFrame == frameCount)
                    {
                        currentFrame = 0;
                        // If we are not looping deactivate the animation
                        if (Looping == false)
                            Active = false;
                    }

                    // Reset the elapsed time to zero
                    elapsedTime = 0;
                }
            }

            // Grab the correct frame in the image strip by multiplying the currentFrame index by the frame width
            sourceRect = new Rectangle(currentFrame * FrameWidth, 0, FrameWidth, FrameHeight);

            // Grab the correct frame in the image strip by multiplying the currentFrame index by the frame width
            destinationRect = new Rectangle((int)Position.X - (int)(FrameWidth * scale) / 2,
            (int)Position.Y - (int)(FrameHeight * scale) / 2,
            (int)(FrameWidth * scale),
            (int)(FrameHeight * scale));
        }


        public void Draw(SpriteBatch spriteBatch)
        {
            if (Active)
            {
                
                spriteBatch.Draw(spriteStrip, destinationRect, sourceRect, color);
            }
        }


    }
}