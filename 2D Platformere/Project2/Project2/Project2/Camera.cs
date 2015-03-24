using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Project2
{
    public class Camera
    {
        public Matrix transform { get; private set; }
        public Viewport view { get; set; }
        public Vector2 center { get; set; }
        public Vector2 Position;
        Vector2 playerPositionInWorldSpace;
        Vector2 boundaries;

        public Camera(Viewport newView)
        {
            view = newView;
            Origin = new Vector2(newView.X / 2, newView.Y / 2);
            Zoom = 1.0f;
        }

        public Vector2 Origin { get; set; }
        public float Zoom { get; set; }
        public float Rotation { get; set; }

        public void setBoundaries(int x, int y)
        {
            boundaries = new Vector2(x, y);

        }

        public Matrix GetViewMatrix() //was Vector2 parallax
        {            
            return Matrix.CreateTranslation(new Vector3(-Position * 0.5f, 0.0f)) *
                Matrix.CreateTranslation(new Vector3(-Origin, 0.0f)) *
                //Matrix.CreateRotationZ(Rotation) *   //Rotation don't think this is necessary
                Matrix.CreateScale(Zoom, Zoom, 1) *
                Matrix.CreateTranslation(new Vector3(Origin, 0.0f));
        }

        public Vector2 position
        {
            get { return Position; }
            set
            {
                Position = value;
            }
        }

        public void ResetCamera()
        {
            Position = Origin;
        }

        public void Update(GameTime gameTime, Player player)
        {
            playerPositionInWorldSpace = player.position;

            center = new Vector2(playerPositionInWorldSpace.X - view.Width / 2, playerPositionInWorldSpace.Y - view.Height / 2);  

            if (view.Width < boundaries.X)
            {
                if (playerPositionInWorldSpace.X >= view.Width / 2)
                {
                    if (center.X < boundaries.X - view.Width)
                    {
                        Position = center * new Vector2(2, 0);
                    }
                }
                else if (playerPositionInWorldSpace.Y <= boundaries.Y)
                {
                    ResetCamera();

                }
            }
        }
    }
}
    

