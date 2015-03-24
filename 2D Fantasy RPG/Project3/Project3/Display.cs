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

namespace Project3
{
    public class Display
    {
        Game game;
        Player p;

        public int HP { set; get; }
        public int level { set; get; }
        public int experience { set; get; }
        public int threshold { set; get; }

        //Will probably make a custom font
        public SpriteFont font;

        public bool ShowHUD;
        
        //Background Texture
        public Texture2D HUDBackGround;
        public Texture2D HUDOverlay;
        public Texture2D HUDTeal;
        public Texture2D HUDpurple;
        public List<Item> items;

        public Display(Player player, Game game)
        {
            p = player;
            font = null;
            ShowHUD = true;
            items = new List<Item>();
            HP = 50;
            level = 1;
            experience = 0;
            threshold = 10;
        }

        public void increaseLevel()
        {
            level++;
            p.baseAtk += 3;
            p.basedef += 1;
            HP += 10;
        }

        public void setExperience(int exp)
        {
            experience = exp;
        }

        public void increaseThreshold()
        {
            threshold += 50;
        }

        public void LoadContent(ContentManager content)
        {
            font = content.Load<SpriteFont>("Displayfont");
            HUDBackGround = content.Load<Texture2D>("Overlays/hud_maindisplay_96x96");
            HUDOverlay = content.Load<Texture2D>("Overlays/item_overlay");
            HUDTeal = content.Load<Texture2D>("Overlays/item_teal_overlay");
            HUDpurple = content.Load<Texture2D>("Overlays/item_purple_overlay");
        }

        public void Update(GameTime gametime)
        {
            KeyboardState keyState = Keyboard.GetState();
        }

        public void Draw(SpriteBatch sb)
        {

            if (ShowHUD)
            {
                // Initial draw state 
                // Always use the camera position formula, then offset it to where you want the things to be located 
                // formula (p.world.camera.Position.X/2, p.world.camera.Position.Y/2)
                // example here, offset for x is 0 since it's at the very left
                // offset for y is the screen size height (320) - texture height 
                sb.Draw(HUDBackGround, new Vector2(p.world.camera.Position.X / 2, p.world.camera.Position.Y / 2 + 352 - HUDBackGround.Height), Color.White);

                // for words, you'll have to adjust y coordinates manually 
                sb.DrawString(font, "HP: " + HP, new Vector2(p.world.camera.Position.X / 2 + 9, p.world.camera.Position.Y / 2 + 267), Color.White);
                sb.DrawString(font, "Level: " + level, new Vector2(p.world.camera.Position.X / 2 + 7, p.world.camera.Position.Y / 2 + 287), Color.White);
                sb.DrawString(font, "Money: \n $" + p.playerInventory.money , new Vector2(p.world.camera.Position.X / 2 + 8, p.world.camera.Position.Y / 2 + 308), Color.White);

                int offset = 20;
                for (int i = 0; i < 10; i++)
                {
                    sb.Draw(HUDOverlay, new Vector2(p.world.camera.Position.X / 2 + 75 + offset, p.world.camera.Position.Y / 2 + 322 - HUDOverlay.Height), Color.White);
                    
                    if (i < p.world.player.playerInventory.items.Count)
                    {
                        Item current = p.playerInventory.items[i];
                        if (current == p.weapon || current == p.shield || current == p.consumable)
                        {
                            sb.Draw(HUDpurple, new Vector2(p.world.camera.Position.X / 2 + 75 + offset, p.world.camera.Position.Y / 2 + 322 - HUDTeal.Height), Color.White);
                        }
                        else
                        {
                            sb.Draw(HUDTeal, new Vector2(p.world.camera.Position.X / 2 + 75 + offset, p.world.camera.Position.Y / 2 + 322 - HUDTeal.Height), Color.White);
                        }
                       

                        sb.Draw(p.world.player.playerInventory.items[i].itemTexture, new Vector2(p.world.camera.Position.X / 2 + 75 + offset, p.world.camera.Position.Y / 2 + 322 - p.world.player.playerInventory.items[i].itemTexture.Height), Color.White);

                    }
                    offset += 32;
                }

            }
        }
    }
}
