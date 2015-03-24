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
    public class LevelInfo
    {
        public int x;
        public int y;
        public String backgroundTexture;
        public Boolean hasTexture;
        public String texture;
        public String shader1;
        public String shader2; 
    }

    public class MapTileData
    {
        public String tileTexture;
        public Boolean isTrap;
        public Boolean isBouncy;
        public Boolean isBreakable;
        public Boolean isUnstable;
        public Boolean isSaw;
        public Boolean isLock;
        public Boolean isKey;
        public Boolean isCake;
        public Vector2 mapPosition;
    }
}
