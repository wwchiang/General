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

namespace Project2DataFiles
{
    public class MapTileData
    {
        public Texture2D tileTexture;
        public Boolean isSpecial;
        public Vector2 mapPosition;

    }

    public class Data
    {
        public Texture2D tileTexture;
        public Boolean isSpecial;
        public Vector2 mapPosition;
    }
}
