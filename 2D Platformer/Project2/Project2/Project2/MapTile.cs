using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Project2
{

    public class MapTile
    {
        Game game;
        
        Animation tileAnimation;
        public Vector2 mapPositions;
        
        /* The different tile attributes */
        public Boolean isTrap;
        public Boolean isBouncy;
        public Boolean isBreakable;
        public Boolean isUnstable;
        public Boolean isCake;
        public Boolean isKey;
        public Boolean isLock;
        public Boolean isSaw;
        public Boolean isActive;

        public Vector2 endPosition;
        public Vector2 moveDirection;
        public Vector2 originPosition;
        public Vector2 shift;
        //Current default: saw blocks move left to right by 4 blocks

        public int Width
        {
            get { return tileAnimation.FrameWidth; }
        }

        public int Height
        {
            get { return tileAnimation.FrameHeight; }
        }

        public MapTile(int X, int Y, Texture2D tileTexture, Game game, Boolean bounce, 
            Boolean breakable, Boolean trap, Boolean unstable, Boolean cake, Boolean saw, 
            Boolean l, Boolean key, Boolean active)
        {
            this.game = game;
            int dimension = tileTexture.Height;
            mapPositions = new Vector2((X * dimension), (game.GraphicsDevice.Viewport.Height 
                - (Y * dimension) - dimension));
            tileAnimation = new Animation();
            isBouncy = bounce;
            isBreakable = breakable;
            isTrap = trap;
            isUnstable = unstable;
            isCake = cake;
            isSaw = saw;
            isLock = l;
            isKey = key;
            isActive = true;
            moveDirection = new Vector2(0, 0);
            
            //Console.Write("\nMap Position: " + mapPositions);

            //Unstable tiles
            if (unstable)
            {
                //Console.Write("Unstable");
                tileAnimation.Initialize(tileTexture, mapPositions, 64, 64, 32, 70, Color.White, 1, false, true);
            }

            //Breakable tiles
            else if (breakable)
            {
                //Console.Write("Break");
                tileAnimation.Initialize(tileTexture, mapPositions, 64, 64, 8, 70, Color.White, 1, false, true);

            }
            //Bouncing tiles
            else if (isBouncy)
            {
                //Console.Write("Bounce");
                tileAnimation.Initialize(tileTexture, mapPositions, 64, 64, 24, 50, Color.White, 1, true, false);

            }
            else if (isSaw)
            {
                tileAnimation.Initialize(tileTexture, mapPositions, 64, 64, 8, 20, Color.White, 1, true, false);
                moveDirection = new Vector2(1, 0);
                originPosition = new Vector2(mapPositions.X, mapPositions.Y);
                endPosition = new Vector2(mapPositions.X + tileTexture.Height * 4, mapPositions.Y);
                shift = new Vector2(0, 0);
            }
            else if (isLock || isKey)
            {
                tileAnimation.Initialize(tileTexture, mapPositions, 64, 64, 1, 70, Color.White, 1, false, true);
            }
            //Normal tiles, trap tiles, and cake tiles only have one frame
            else
            {
                //Console.Write("NORM");
                tileAnimation.Initialize(tileTexture, mapPositions, 64, 64, 1, 100, Color.White, 1, true, false);

            }
            
        }

        public int GetAnimationFrame()
        {
            return tileAnimation.GetCurrentFrame();
        }
        public void Update(GameTime gametime)
        {
            float animationFrameComp = Height / 2;
            Vector2 scaledComp = new Vector2(animationFrameComp, animationFrameComp);
            if (isSaw)
            {
                if (mapPositions.X <= endPosition.X && mapPositions.X >= originPosition.X)
                {
                    mapPositions += moveDirection;
                }
                else
                {
                    moveDirection = moveDirection * -1;
                    mapPositions += moveDirection;
                }
            }
                tileAnimation.Update(mapPositions + scaledComp, gametime);
        }
        public void PlayAnimationOnce()
        {
            tileAnimation.PlayFirstFrame = false;
        }

        public Boolean HasPlayedAnimation()
        {
            if (tileAnimation.PlayFirstFrame == false)
            {
                return true;
            }
            return false;
        }
        public void ResetAnimation()
        {
            tileAnimation.PlayFirstFrame = true;
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            //Console.Write("draw");
            //Console.Write("\n CurrentX: " + position.X + " X coordinate:" + position.X * Width);
           
            //spriteBatch.Draw(tileTexture, new Rectangle((int)mapPositions.X, (int)mapPositions.Y,
            //    tileTexture.Width, tileTexture.Height), Color.White);
            
            tileAnimation.Draw(spriteBatch);
        }
    }
}
