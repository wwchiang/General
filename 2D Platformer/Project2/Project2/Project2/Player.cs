using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Project2
{

    public class Player
    {
        public Game game;
        World world;

        //Gameplay Mechanics//

        int mapWidth;
        int mapHeight;

        /* Player states and attributes */
        /* Use animation instead of texture*/
        Animation idleRight;
        Animation idleLeft;
        Animation moveRight;
        Animation moveLeft;
        Animation jumpRight;
        Animation jumpLeft;
        Animation deathAnimation;
        public Boolean isFacingRight;

        public Boolean isFalling;
        public Boolean isOnPlatform;
        public Boolean isDead;
        public Boolean hasFallenToDeath;

        //For key block
        public Boolean hasKey;

        Boolean isMoving;
        Boolean isIdle;
        Boolean isJumping;

        /* Player lives. Can be moved to HUD class if we create one. */
        public int lives;

        /* Player positions */
        public Vector2 spawnPosition;  //changed to public..might change back
        public Vector2 position;

        float max_x_velocity = 325;
        float max_y_velocity = 1200;

        public Vector2 velocity;
        Vector2 slowdown = new Vector2(15, 0);
        Vector2 gravity = new Vector2(0, 25);

        public int Width
        {
            get { return idleRight.FrameWidth; }
        }

        public int Height
        {
            get { return idleRight.FrameHeight; }
        }

        public Boolean end = false;


        public void setBoundaries(int mapWidth, int mapHeight)
        {
            this.mapHeight = mapHeight;
            this.mapWidth = mapWidth;
        }

        public Vector2 getBoundaries()
        {
            return new Vector2(mapWidth, mapHeight);
        }

        public Player(int X, int Y, Game1 g, Texture2D idleR, Texture2D idleL, Texture2D moveR, Texture2D moveL,
            Texture2D jumpR, Texture2D jumpL, Texture2D death, World w)
        {
            lives = 3;
            isDead = false;
            game = g;
            world = w;
            spawnPosition = new Vector2(X, Y);
            position = new Vector2(X, Y);
            isFalling = true;
            isOnPlatform = false;
            end = false;
            hasKey = false;
            hasFallenToDeath = false;

            isFacingRight = true;

            idleRight = new Animation();
            idleLeft = new Animation();
            moveRight = new Animation();
            moveLeft = new Animation();
            jumpRight = new Animation();
            jumpLeft = new Animation();
            deathAnimation = new Animation();

            // Initialize Animations here
            // Animation arguments -- Texture2D texture, Vector2 position, int frameWidth, int frameHeight, int frameCount,
            //int frametime, Color color, float scale, bool looping)
            idleRight.Initialize(idleR, Vector2.Zero, 64, 64, 2, 300, Color.White, 1, true, false);
            idleLeft.Initialize(idleL, Vector2.Zero, 64, 64, 2, 300, Color.White, 1, true, false);
            moveRight.Initialize(moveR, Vector2.Zero, 64, 64, 4, 100, Color.White, 1, true, false);
            moveLeft.Initialize(moveL, Vector2.Zero, 64, 64, 4, 100, Color.White, 1, true, false);
            deathAnimation.Initialize(death, Vector2.Zero, 64, 64, 12, 50, Color.White, 1, false, false);
            //playerAnimation.Initialize(playerTexture, Vector2.Zero, 32, 32, 
        }

        public void setXVelocity(float velocity)
        {

        }
        public void setYVelocity(float velocity)
        {
            this.velocity.Y = velocity;
        }

        private void UpdateAnimations(GameTime gameTime)
        {
            idleLeft.Update(position + new Vector2(Width / 2, Height / 2), gameTime);
            idleRight.Update(position + new Vector2(Width / 2, Height / 2), gameTime);
            moveRight.Update(position + new Vector2(Width / 2, Height / 2), gameTime);
            moveLeft.Update(position + new Vector2(Width / 2, Height / 2), gameTime);
            deathAnimation.Update(position + new Vector2(Width / 2, Height / 2), gameTime);

        }
        public void Update(GameTime gameTime, KeyboardState keyboard)
        {
            // Update:

            /*update anim positions */
            UpdateAnimations(gameTime);

            float time = (float)gameTime.ElapsedGameTime.TotalSeconds;

            if (!isOnPlatform)
            {
                if (velocity.Y < max_y_velocity)
                {
                    velocity.Y += gravity.Y;
                }
                if (velocity.Y > max_y_velocity)
                {
                    velocity.Y = max_y_velocity;
                }
            }


            //if velocity < 0, add to velocity until it reaches 0
            // if velocity > 0, subtract until it reaches 0
            if (velocity.X < 25 && velocity.X >= 0)
            {
                velocity.X = 0;
                isIdle = true;
                isMoving = false;
            }

            if (velocity.X > 0)
            {

                velocity.X -= slowdown.X;
            }
            else
            {
                velocity.X += slowdown.X;
            }

            if (velocity.X < 25 && velocity.X >= 0)
            {
                velocity.X = 0;
                isIdle = true;
                isMoving = false;
            }

            if (!isDead)
            {
                if (keyboard.IsKeyDown(Keys.Right))
                {
                    if ((Math.Abs(velocity.X) < max_x_velocity))
                    {
                        isMoving = true;
                        isIdle = false;
                        isFacingRight = true;
                        velocity.X += 50;
                    }
                }
                if (keyboard.IsKeyDown(Keys.Left))
                {

                    if ((Math.Abs(velocity.X) < max_x_velocity))
                    {
                        isMoving = true;
                        isIdle = false;
                        isFacingRight = false;
                        velocity.X -= 50;
                    }
                }
                if (keyboard.IsKeyDown(Keys.Down))
                {
                }

                if (keyboard.IsKeyDown(Keys.Up))
                {
                    if (isOnPlatform)
                    {

                        velocity.Y += -700;
                        isOnPlatform = false;
                        world.jumpSoundInstance.Play();
                        world.jumpSoundInstance.Volume = 1.0f;
                    }
                }
            }
            UpdatePosition(time);
            StayWithinBounds();


        }

        /* Player stays within the bounds of the game screen */
        public void StayWithinBounds()
        {
            if (position.X <= 0)
                position.X = 0;

            if (position.X >= mapWidth - idleRight.FrameWidth)
                position.X = mapWidth - idleRight.FrameWidth;

            //if (position.Y <= 0)
                //position.Y = 0;

            if (position.Y >= mapHeight - idleRight.FrameHeight)
            {
                hasFallenToDeath = true;
            }
        }

        public void UpdatePosition(float time)
        {

            position.X += (int)(velocity.X * time);
            position.Y += (int)(velocity.Y * time);
        }

        public Rectangle getHitBox()
        {
           return new Rectangle((int)position.X, (int)position.Y, Width, Height);
        }

        //Check bug when player jumps first, then moves right/left. 
        public void CheckCollisionSide(Rectangle player, Rectangle tile, MapTile mapTile)
        {
            if (mapTile.isUnstable && mapTile.HasPlayedAnimation())
            {
                if (mapTile.GetAnimationFrame() == 24)
                {
                    mapTile.isActive = false;
                }
            }
            if (hasKey && mapTile.isLock && mapTile.isActive)
            {
                mapTile.isActive = false;
                mapTile.PlayAnimationOnce();
            }
            if (player.Intersects(tile) && mapTile.isActive)
            {

                checkTile(mapTile);

                int ydiff = (int)(tile.Y - position.Y);
                int xdiff = (int)(tile.X - position.X);
                int min_translation;

                //if -x, +y - player is topRight
                //if -x, -y - player is bottomright
                //if +x, +y - player is topLeft
                //if +x, -y - player is bottomleft
                if (mapTile.isUnstable)
                {
                    //Play 
                    mapTile.PlayAnimationOnce();
                    world.collapseSoundInstance.Play();  //collapsing sound
                }
                if (mapTile.isTrap || mapTile.isSaw)
                {
                    killPlayer();
                }

                /* If player is colliding with the top left corner of tile*/
                if (xdiff >= 0 && ydiff >= 0)
                {
                    // Kills player, plays animation, if player's death animation is no longer active 
                    /* If player's difference from left of tile is greater than difference from top of tile,
                     * shift to the left*/
                    if (Math.Abs(player.Left - tile.Left) > Math.Abs(player.Top - tile.Top))
                    {
                            min_translation = player.Right - tile.Left;
                            position.X -= min_translation;
                            velocity.X = 0;

                    }
                    /* If player's difference from top of tile is greater than difference from left of tile,
                     * shift upwards */
                    else
                    {

                        /* Implement this where the player hits the tile */
                        if (mapTile.isBouncy)
                        {
                            world.bounceSoundInstance.Play();
                            velocity.Y = 0;
                            velocity.Y += -1000;
                            
                        }

                        else
                        {
                            world.bounceSoundInstance.Stop();
                            min_translation = player.Bottom - tile.Top;
                            position.Y -= min_translation;
                            isOnPlatform = true;
                            if (velocity.Y > 0)
                            {
                                velocity.Y = 0;
                            }

                        }

                    }
                }
                /* If player is colliding with top right corner of tile*/
                else if (xdiff <= 0 && ydiff >= 0)
                {

                    /* If player's difference from right of tile is smaller than difference from top of tile,
                     * shift upwards*/
                    if (Math.Abs(player.Right - tile.Right) < Math.Abs(player.Top - tile.Top))
                    {
                        if (mapTile.isBouncy)
                        {
                            world.bounceSoundInstance.Play();
                            velocity.Y = 0;
                            velocity.Y += -1000;
                        }
                        else
                        {
                            min_translation = player.Bottom - tile.Top;
                            position.Y -= min_translation;
                            isOnPlatform = true;
                            if (velocity.Y > 0)
                            {
                                velocity.Y = 0;
                            }
                        }

                    }
                    /* If player's difference from right of tile is greater than difference from top of tile,
                     * shift to the right*/
                    else
                    {
                            min_translation = player.Left - tile.Right;
                            position.X -= min_translation;
                            velocity.X = 0;

                    }
                }
                /* If player is colliding with bottom-left corner of tile*/
                else if (xdiff >= 0 && ydiff <= 0)
                {
                        /* If player's difference from left of tile is greater than difference from bottom of tile,
                         * shift to the left*/
                        if (Math.Abs(player.Left - tile.Left) > Math.Abs(player.Bottom - tile.Bottom))
                        {
                            min_translation = player.Right - tile.Left;
                            position.X -= min_translation;
                            velocity.X = 0;

                        }
                        /* If player's difference from left of tile is less than difference from bottom of tile,
                         * shift downwards*/
                        else
                        {
                            /* Implement this where the player hits the tile */
                            if (mapTile.isBreakable)
                            {
                                world.breakSound.Play();
                                mapTile.isActive = false;
                                mapTile.PlayAnimationOnce();
                            }
                            min_translation = player.Top - tile.Bottom;
                            position.Y -= min_translation;
                            //velocity.Y = 0;
                            if (velocity.Y < 0)
                            {
                                velocity.Y = 0;
                            }
                            // TODO: Have the player phase through the tile when colliding from the bottom

                        }
                    
                }
                /* If player is colliding with bottom-right corner of tile*/
                else if (xdiff <= 0 && ydiff <= 0)
                {
                    /* Implement this where the player hits the tile */
                        /* If player's difference from right of tile is greater than difference from bottom of tile,
                        * shift to the right*/
                        if (Math.Abs(player.Right - tile.Right) > Math.Abs(player.Bottom - tile.Bottom))
                        {

                            min_translation = player.Left - tile.Right;
                            position.X -= min_translation;
                            velocity.X = 0;
                        }
                        /* If player's difference from right of tile is smaller than difference from bottom of tile,
                         * shift downwards*/
                        else
                        {
                            if (mapTile.isBreakable)
                            {
                                world.breakSound.Play();
                                mapTile.isActive = false;
                                mapTile.PlayAnimationOnce();
                            }

                            min_translation = player.Top - tile.Bottom;
                            position.Y -= min_translation;
                            velocity.Y = 0;

                        }
                        // TODO: Have the player phase through the tile when colliding from the bottom

                }
            }
        }

        private void checkTile(MapTile mapTile)
        {
            // TODO: Check transparency by checking alpha values.
            if (mapTile.isKey)
            {
                world.keySoundInstance.Play();
                hasKey = true;
                mapTile.isActive = false;
                mapTile.PlayAnimationOnce();
                //turn on isKey
            }
            if (mapTile.isCake)
            {
                end = true;
                return;
            }
        }

        private void killPlayer()
        {
            if (!isDead)
            {
                world.deathSoundInstance.Play(); //Death Sound
                deathAnimation.Active = true;
                isDead = true;
            }
        }

        public Boolean CheckDeath()
        {
            if (deathAnimation.Active == false && isDead) {
                return true;
            }
            return false;
        }

        public Vector2 GetPosition()
        {
            return position;
        }

        public void Draw(SpriteBatch spriteBatch)
        {

            if (!isDead)
            {
                if (isFacingRight)
                {
                    if (isIdle)
                    {
                        idleRight.Draw(spriteBatch);
                    }
                    if (isMoving)
                    {
                        moveRight.Draw(spriteBatch);
                    }
                    //Draw animations right
                }
                else
                {
                    if (isIdle)
                    {
                        idleLeft.Draw(spriteBatch);
                    }
                    if (isMoving)
                    {
                        moveLeft.Draw(spriteBatch);
                    }
                }
            }
            else
            {
                if (deathAnimation.Active)
                {
                    deathAnimation.Draw(spriteBatch);
                    
                   
                    //world.LevelReset();
                }
            }
            //spriteBatch.Draw(playerTexture, new Rectangle((int)position.X,
            //    (int)position.Y,
            //    playerTexture.Width, playerTexture.Height), Color.White);
        }

    }
}